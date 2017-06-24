package Agents;

import GameLogic.Position;

/**
 * Hawks are aggressive agents that always attack
 */
public class HawkAgent extends Agent {

    public HawkAgent(Position spawnLocation, String name) {
        super(spawnLocation, name);
    }

    @Override
    public Agent spawnChild(Position spawnPos) {
        return new HawkAgent(spawnPos, this.name + "_x");
    }
}
