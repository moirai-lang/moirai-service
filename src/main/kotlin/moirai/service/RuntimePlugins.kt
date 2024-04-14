package moirai.service

import moirai.eval.IntValue
import moirai.eval.ListValue
import moirai.eval.UserPlugin

object RuntimePlugins {
    // TODO: Define your own plugins
    val pluginSource = """
        plugin def simplePlugin {
            signature (Int, Int) -> Int
            cost Sum(5, 5)
        }
        
        plugin def paramPlugin<T, K: Fin> {
            signature List<T, K> -> T
            cost Mul(5, K)
        }
    """.trimIndent()

    // TODO: Implement your plugins
    val userPlugins: MutableList<UserPlugin> = mutableListOf(
        RuntimeUserPlugin("simplePlugin") {
            val first = it.first() as IntValue
            val last = it.last() as IntValue
            IntValue(first.canonicalForm + last.canonicalForm)
        },
        RuntimeUserPlugin("paramPlugin") {
            val l = it.first() as ListValue
            l.elements.last()
        }
    )
}