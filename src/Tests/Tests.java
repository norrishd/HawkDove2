package Tests;

import Agents.DoveAgent;
import GameLogic.GridWorld;
import GameLogic.Position;
import Tiles.TilePattern;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


/**
 * Tests to assist in development and debugging of the package
 */

public class Tests {

    @Test
    public void makeGridWorld() {
        GridWorld gridWorld = new GridWorld();
        assertFalse("Actually, tile size = " + gridWorld.tiles.length, gridWorld.tiles.length == 0);

        for (TilePattern pattern : TilePattern.values()) {
            gridWorld.generateWorld(pattern);

            System.out.println(pattern);
            drawWorld(gridWorld);
        }
    }

    // Helper function to draw an ASCII picture of the world
    public void drawWorld(GridWorld gridWorld) {
        for (int i = 0; i < gridWorld.tiles.length; i++) {
            for (int j = 0; j < gridWorld.tiles[0].length; j++) {
                if (!gridWorld.tiles[i][j].walkable())
                    System.out.print("X ");
                else {
                    if (gridWorld.tiles[i][j].agents.size() > 0)
                        System.out.print("o ");
                    else if (gridWorld.tiles[i][j].hasFood())
                        System.out.print("* ");
                    else
                        System.out.print("_ ");
                }
            }
            System.out.println("");
        }
        System.out.println("");
    }

    @Test
    public void spawnAgent() {
        GridWorld gridWorld = new GridWorld();
        gridWorld.generateWorld(TilePattern.OPEN_FIELD);
        Position spawnLocation = gridWorld.getWalkableTile();
        System.out.println("Agent to spawn at " + spawnLocation.getCoords());
        gridWorld.addAgent(new DoveAgent(spawnLocation, "Adam"));
        drawWorld(gridWorld);
        assertTrue(gridWorld.agents.size() == 1);

        spawnLocation = gridWorld.getWalkableTile();
        System.out.println("Agent to spawn at " + spawnLocation.getCoords());
        gridWorld.addAgent(new DoveAgent(spawnLocation, "Adam"));
        drawWorld(gridWorld);
        assertTrue(gridWorld.agents.size() == 2);

        spawnLocation = gridWorld.getWalkableTile();
        System.out.println("Agent to spawn at " + spawnLocation.getCoords());
        gridWorld.addAgent(new DoveAgent(spawnLocation, "Adam"));
        drawWorld(gridWorld);
        assertTrue(gridWorld.agents.size() == 3);
    }

    @Test
    public void growFood() {
        GridWorld gridWorld = new GridWorld();
        gridWorld.generateWorld(TilePattern.OPEN_FIELD);
        drawWorld(gridWorld);
        gridWorld.growFood();
        drawWorld(gridWorld);
        gridWorld.growFood();
        drawWorld(gridWorld);
        gridWorld.growFood();
        drawWorld(gridWorld);
    }

}
