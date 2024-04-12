package moirai.service

import moirai.composition.*

class RuntimeExecutionCache: ExecutionCache {
    // TODO: Implement LRU or some other caching scheme that makes sense for the domain
    private val cache: MutableMap<List<String>, ExecutionArtifacts> = mutableMapOf()

    override fun fetchExecutionArtifacts(namespace: List<String>): ExecutionCacheRequestResult {
        return if (cache.containsKey(namespace)) {
            InCache(cache[namespace]!!)
        } else {
            NotInCache
        }
    }

    override fun storeExecutionArtifacts(namespace: List<String>, executionArtifacts: ExecutionArtifacts) {
        cache[namespace] = executionArtifacts
    }

    override fun invalidateCache(namespace: List<String>) {
        // TODO: Generally this will lead to an expensive operation, so repeated requests should be throttled
        if (cache.containsKey(namespace)) {
            cache.remove(namespace)
        }
    }
}