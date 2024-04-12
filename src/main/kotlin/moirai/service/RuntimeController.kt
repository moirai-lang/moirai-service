package moirai.service

import moirai.composition.CompilerFrontend
import moirai.composition.NamedScript
import moirai.composition.PureTransient
import moirai.composition.TransientScript
import moirai.eval.eval
import moirai.semantics.core.ExpectedNamedScript
import moirai.semantics.core.ExpectedTransientScript
import moirai.semantics.core.LanguageException
import org.springframework.web.bind.annotation.*

@RestController
class RuntimeController {
    val executionCache = RuntimeExecutionCache()
    val sourceStore = RuntimeSourceStore()

    @PostMapping("/execute")
    @CrossOrigin
    fun execute(@RequestBody source: String): String {
        try {
            val frontend = CompilerFrontend(RuntimeArchitecture, sourceStore)
            val executionArtifacts = frontend.compileUsingCache(source, executionCache)

            when (val scriptType = executionArtifacts.importScan.scriptType) {
                is PureTransient,
                is TransientScript -> {
                    return printConstruct(eval(RuntimeArchitecture, executionArtifacts))
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
            val frontend = CompilerFrontend(RuntimeArchitecture, sourceStore)

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
}