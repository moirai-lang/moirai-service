package moirai.service

import moirai.semantics.core.Architecture

class RuntimeArchitecture: Architecture {
    override val costUpperLimit: Long = 10000
    override val defaultNodeCost: Long = 1
}