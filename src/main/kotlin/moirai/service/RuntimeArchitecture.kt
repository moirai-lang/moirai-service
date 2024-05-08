package moirai.service

import moirai.semantics.core.*

// TODO: Make architectures that are configurable depending on the hardware
object RuntimeArchitecture: Architecture {
    // TODO: Use load testing to determine the exact values
    override val costUpperLimit: Long = 10000
    override val defaultNodeCost: Long = 1

    // TODO: Define overlay costs for common node types
    override fun getNodeCostOverlay(key: String): NodeCostOverlay {
        return when (key) {
            // ForEach nodes should be more expensive so that the cost explodes faster
            AstNodeNames.ForEachAst.key -> DefinedOverlay(25)
            else -> UndefinedOverlay
        }
    }
}