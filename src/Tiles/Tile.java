package Tiles;

import Agents.Agent;
import GameLogic.Position;

import java.util.ArrayList;

/**
 * Class to represent board tiles.
 * Some will be walls, and some can be traversed by agents and may contain food
 */

public abstract class Tile {

    String image;
    public Position position;
    public ArrayList<Agent> agents;        // agents present at this Tile
    private boolean hasFood;

    public Tile(Position position, String image) {
        this.position = position;
        this.image = image;
        this.agents = new ArrayList<>();
        this.hasFood = false;
    }

    public abstract boolean walkable();

    // Add an agent to this tile
    public void addAgent(Agent agent) {
        this.agents.add(agent);
    }

    public boolean hasFood() {
        return hasFood;
    }

    public void growFood() {
        this.hasFood = true;
    }

    public void loseFood() {
        this.hasFood = false;
    }

}
