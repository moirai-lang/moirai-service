package moirai.service

import moirai.semantics.core.*

// TODO: Make architectures that are configurable depending on the hardware
object RuntimeArchitecture: Architecture {
    // TODO: Use load testing to determine the exact values
    override val costUpperLimit: Long = 10000
    override val defaultNodeCost: Long = 1
    override fun getNamedCost(name: String): Long {
        TODO("Not yet implemented")
    }

    // TODO: Define overlay costs for common node types
    override fun getNodeCostOverlay(nodeKind: AstNodeKind): NodeCostOverlay {
        return when (nodeKind) {
            // ForEach nodes should be more expensive so that the cost explodes faster
            AstNodeKind.ForEachAst -> DefinedOverlay(25)
            else -> UndefinedOverlay
        }
    }
}