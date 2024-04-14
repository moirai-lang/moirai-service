package moirai.service

import moirai.eval.UserPlugin
import moirai.eval.Value

data class RuntimeUserPlugin(override val key: String, private val eval: (List<Value>) -> Value): UserPlugin {
    override fun evaluate(args: List<Value>): Value = eval(args)
}