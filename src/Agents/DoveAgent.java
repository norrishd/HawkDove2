package Agents;

import GameLogic.Position;

/**
 * Doves are peaceful agents that never attack and use a pure Dove strategy
 */
public class DoveAgent extends Agent {

    public DoveAgent(Position spawnLocation, String name) {

        super(spawnLocation, name);
    }

    @Override
    public Agent spawnChild(Position spawnPos) {
        return new DoveAgent(spawnPos, this.name + "_x");
    }
}
