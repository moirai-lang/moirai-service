package moirai.service

import moirai.composition.ExecutionArtifacts
import moirai.composition.ExecutionCache
import moirai.composition.ExecutionCacheRequestResult

class RuntimeExecutionCache: ExecutionCache {
    override fun fetchExecutionArtifacts(namespace: List<String>): ExecutionCacheRequestResult {
        TODO("Not yet implemented")
    }

    override fun invalidateCache(namespace: List<String>) {
        TODO("Not yet implemented")
    }

    override fun storeExecutionArtifacts(namespace: List<String>, executionArtifacts: ExecutionArtifacts) {
        TODO("Not yet implemented")
    }
}