package moirai.service

import moirai.semantics.core.Architecture

object RuntimeArchitecture: Architecture {
    override val costUpperLimit: Long = 10000
    override val defaultNodeCost: Long = 1
}