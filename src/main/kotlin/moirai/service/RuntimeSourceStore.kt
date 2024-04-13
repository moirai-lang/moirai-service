package moirai.service

import moirai.composition.SourceStore
import moirai.semantics.core.LanguageError
import moirai.semantics.core.LanguageException
import moirai.semantics.core.NoSuchFile
import moirai.semantics.core.NotInSource

class RuntimeSourceStore : SourceStore {
    // TODO: Store named scripts in a blob storage database
    private val fetchDict: MutableMap<List<String>, String> = mutableMapOf()

    override fun fetchSourceText(namespace: List<String>): String {
        if (fetchDict.containsKey(namespace)) {
            // TODO: Access control for specific users attempting to fetch scripts
            return fetchDict[namespace]!!
        }
        throw LanguageException(setOf(LanguageError(NotInSource, NoSuchFile(namespace))))
    }

    fun addArtifacts(namespace: List<String>, sourceText: String) {
        fetchDict[namespace] = sourceText
    }
}