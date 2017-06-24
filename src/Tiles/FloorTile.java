package Tiles;

import Agents.Agent;
import GameLogic.Position;

import java.util.NoSuchElementException;
import java.util.Random;

/**
 * A walkable Tiles. Agents can traverse, encounter other agents and possibly find food
 */
public class FloorTile extends Tile {

    double fertility;           // [0-1], indicates likelihood of food spawning here if not picking tiles randomly

    // Default constructor if no value is provided for the fertility - will be generated uniform randomly
    public FloorTile(Position position, String image) {
        super(position, image);
        Random r = new Random();        // Generate a fertility value using uniform random distribution
        fertility = r.nextDouble();
    }

    // Constructor if a fertility value is provided
    public FloorTile(Position position, String image, double fertility) {
        super(position, image);
        this.fertility = fertility;
    }

    @Override
    public boolean walkable() {
        return true;
    }

    // Remove an agent from the tile (if they move away or die)
    public void removeAgent(Agent agent) throws Exception {
        if (this.agents.contains(agent))
            this.agents.remove(agent);
        else
            throw new NoSuchElementException("Tried to remove an agent from a tile they weren't on");
    }
}
