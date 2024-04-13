package moirai.service

import moirai.semantics.core.Architecture

// TODO: Make architectures that are configurable depending on the hardware
object RuntimeArchitecture: Architecture {
    // TODO: Use load testing to determine the exact values
    override val costUpperLimit: Long = 10000
    override val defaultNodeCost: Long = 1
}