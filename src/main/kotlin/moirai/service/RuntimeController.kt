package moirai.service

import com.fasterxml.jackson.databind.ObjectMapper
import moirai.composition.*
import moirai.eval.eval
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
                    return printConstruct(eval(RuntimeArchitecture, executionArtifacts, RuntimePlugins.userPlugins))
                }

                is NamedScript -> {
                    throw Exception(localize(ExpectedTransientScript(scriptType.nameParts.joinToString { "." })))
                }
            }
        } catch (ex: LanguageException) {
            throw Exception(localize(ex.errors.toList()))
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
                    return scriptType.nameParts.joinToString { "." }
                }

                else -> {
                    throw Exception(localize(ExpectedNamedScript))
                }
            }
        } catch (ex: LanguageException) {
            throw Exception(localize(ex.errors.toList()))
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
        if(body.contains("$")) {
            throw Exception("String interpolation escape characters are not allowed in the body")
        }

        val scriptNameParts = scriptName.split(".")
        when (val fetchArtifactsResult = executionCache.fetchExecutionArtifacts(scriptNameParts)) {
            is InCache -> {
                when (val fetchFunctionResult =
                    fetchTransportFunction(fetchArtifactsResult.executionArtifacts, functionName)) {
                    is TransportFunction -> {
                        if (fetchFunctionResult.formalParams.size != 1) {
                            throw Exception("Function $functionName must have exactly one formal parameter")
                        }

                        val argumentType = fetchFunctionResult.formalParams.first().type
                        if (argumentType !is TransportGroundRecordType) {
                            throw Exception("The single argument to $functionName must be a ground record type")
                        }

                        val mapper = ObjectMapper()
                        val tree = mapper.readTree(body)

                        if (tree.isObject || tree.isPojo) {
                            val moiraiSource = "$functionName(${jsonToMoirai(tree, argumentType)})"
                            return execute(moiraiSource)
                        } else {
                            throw Exception("JSON body must be a JSON object")
                        }
                    }

                    TransportFunctionNotFound -> throw Exception("Identifier $functionName not found")
                }
            }

            NotInCache -> throw Exception("Script named $scriptName as not found in the execution cache")
        }
    }
}