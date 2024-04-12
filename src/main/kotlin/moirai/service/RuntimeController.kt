package moirai.service

import moirai.composition.CompilerFrontend
import moirai.composition.NamedScript
import moirai.composition.PureTransient
import moirai.composition.TransientScript
import moirai.eval.eval
import org.springframework.web.bind.annotation.*

@RestController
class RuntimeController {
    val executionCache = RuntimeExecutionCache()
    val sourceStore = RuntimeSourceStore()

    @PostMapping("/execute")
    @CrossOrigin
    fun execute(@RequestBody source: String): String {
        val frontend = CompilerFrontend(RuntimeArchitecture, sourceStore)
        val executionArtifacts = frontend.compileUsingCache(source, executionCache)

        when (executionArtifacts.importScan.scriptType) {
            is PureTransient,
            is TransientScript -> {
                return printConstruct(eval(RuntimeArchitecture, executionArtifacts))
            }

            else -> {
                // TODO: Localize this
                throw Exception("Expected transient script")
            }
        }
    }

    @PostMapping("/store")
    @CrossOrigin
    fun store(@RequestBody source: String): String {
        val frontend = CompilerFrontend(RuntimeArchitecture, sourceStore)

        // TODO: Full compile with topological sort is a very expensive operation, be sure to throttle users
        val executionArtifacts = frontend.fullCompileWithTopologicalSort(source)

        when (val scriptType = executionArtifacts.importScan.scriptType) {
            is NamedScript -> {
                executionCache.storeExecutionArtifacts(scriptType.nameParts, executionArtifacts)
                return scriptType.nameParts.joinToString { "." }
            }

            else -> {
                // TODO: Localize this
                throw Exception("Expected named script")
            }
        }
    }
}