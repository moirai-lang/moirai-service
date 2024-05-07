package moirai.service

import com.fasterxml.jackson.databind.ObjectMapper
import moirai.composition.*
import moirai.eval.evalWithCost
import moirai.semantics.core.ExpectedNamedScript
import moirai.semantics.core.ExpectedTransientScript
import moirai.semantics.core.LanguageException
import moirai.transport.*
import org.springframework.web.bind.annotation.*


@RestController
class RuntimeController {
    private val sourceStore = RuntimeSourceStore()
    private val executionCache = RuntimeExecutionCache()
    private val frontend = CompilerFrontend(
        RuntimeArchitecture,
        sourceStore,
        UserPluginSource(RuntimePlugins.pluginSource)
    )

    @PostMapping("/execute")
    @CrossOrigin
    fun execute(@RequestBody source: String): String {
        try {
            val executionArtifacts = frontend.compileUsingCache(source, executionCache)

            when (val scriptType = executionArtifacts.importScan.scriptType) {
                is PureTransient,
                is TransientScript -> {
                    val res = evalWithCost(RuntimeArchitecture, executionArtifacts, RuntimePlugins.userPlugins)
                    return printConstruct(res.value)
                }

                is NamedScript -> {
                    throw MoiraiServiceException(localize(ExpectedTransientScript(scriptType.nameParts.joinToString("."))))
                }
            }
        } catch (ex: LanguageException) {
            throw MoiraiServiceException(localize(ex.errors.toList()))
        }
    }

    @PostMapping("/store")
    @CrossOrigin
    fun store(@RequestBody source: String): String {
        try {
            // TODO: Full compile with topological sort is a very expensive operation, be sure to throttle users
            val executionArtifacts = frontend.fullCompileWithTopologicalSort(source)

            when (val scriptType = executionArtifacts.importScan.scriptType) {
                is NamedScript -> {
                    executionCache.storeExecutionArtifacts(scriptType.nameParts, executionArtifacts)
                    sourceStore.addArtifacts(scriptType.nameParts, source)
                    return scriptType.nameParts.joinToString(".")
                }

                else -> {
                    throw MoiraiServiceException(localize(ExpectedNamedScript))
                }
            }
        } catch (ex: LanguageException) {
            throw MoiraiServiceException(localize(ex.errors.toList()))
        }
    }

    @PostMapping("/jsonExecuteSingle")
    @CrossOrigin
    fun executeJSON(
        @RequestParam(
            value = "scriptName"
        ) scriptName: String,
        @RequestParam(
            value = "functionName"
        ) functionName: String,
        @RequestBody body: String
    ): String {
        val scriptNameParts = scriptName.split(".")
        when (val fetchArtifactsResult = executionCache.fetchExecutionArtifacts(scriptNameParts)) {
            is InCache -> {
                when (val fetchFunctionResult =
                    fetchTransportFunction(fetchArtifactsResult.executionArtifacts, functionName)) {
                    is TransportFunction -> {
                        if (fetchFunctionResult.formalParams.size != 1) {
                            throw MoiraiServiceException("Function $functionName must have exactly one formal parameter")
                        }

                        val argumentType = fetchFunctionResult.formalParams.first().type
                        if (argumentType !is TransportGroundRecordType) {
                            throw MoiraiServiceException("The single argument to $functionName must be a ground record type")
                        }

                        val mapper = ObjectMapper()
                        val tree = mapper.readTree(body)

                        if (tree.isObject || tree.isPojo) {
                            val recordAst = jsonToMoirai(tree, argumentType)
                            val invokeAst = ApplyTransportAst(fetchFunctionResult.name, listOf(recordAst))
                            val res = evalWithCost(
                                scriptName,
                                invokeAst,
                                frontend,
                                executionCache,
                                RuntimePlugins.userPlugins
                            )
                            return printConstruct(res.value)
                        } else {
                            throw MoiraiServiceException("JSON body must be a JSON object")
                        }
                    }

                    TransportFunctionNotFound -> throw MoiraiServiceException("Identifier $functionName not found")
                }
            }

            NotInCache -> throw MoiraiServiceException("Script named $scriptName as not found in the execution cache")
        }
    }
}