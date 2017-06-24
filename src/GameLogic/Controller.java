package GameLogic;

import Agents.DoveAgent;
import Agents.HawkAgent;
import Tiles.Tile;
import Tiles.TilePattern;
import java.util.Random;

/**
 * Controller (listener) class to receive user input and update model
 */

public class Controller {

    GridWorld gridWorld;
    View view;
    Random r = new Random();

    /**
     * Start a new game/simulation run. Regenerate the world tiles, clear agents and repopulate
     */
    public void newGame() {
        gridWorld.generateWorld(TilePattern.RANDOM_SPARSE);
        gridWorld.agents.clear();
        gridWorld.addAgent(new DoveAgent(gridWorld.getWalkableTile(), "Adam"));
        gridWorld.addAgent(new HawkAgent(gridWorld.getWalkableTile(), "Eve"));

        // Generate food on E(x) = 5% of walkable tiles
        for (Tile[] y_tile : gridWorld.tiles)
            for (Tile x_tile : y_tile)
                if (x_tile.walkable() && r.nextDouble() > 0.95)
                    x_tile.growFood();

        view.drawWorldTiles(gridWorld.tiles, gridWorld.TILE_SIZE, gridWorld.OFFSET);
        view.drawFood(gridWorld.tiles, gridWorld.TILE_SIZE, gridWorld.OFFSET);
        view.highlightGoalFood(gridWorld.agents, gridWorld.TILE_SIZE, gridWorld.OFFSET);
        view.drawAgents(gridWorld.agents, gridWorld.TILE_SIZE, gridWorld.OFFSET);
    }

    public void addModel(GridWorld gridWorld) {
        this.gridWorld = gridWorld;
    }
    public void addView(View view) {
        this.view = view;
    }

    public void nextTurn() {

        gridWorld.moveAgents();
        gridWorld.killDepletedAgents();
        gridWorld.spawnAgents();
        gridWorld.growFood();
        view.drawAgents(gridWorld.agents, gridWorld.TILE_SIZE, gridWorld.OFFSET);
        view.drawFood(gridWorld.tiles, gridWorld.TILE_SIZE, gridWorld.OFFSET);
        view.highlightGoalFood(gridWorld.agents, gridWorld.TILE_SIZE, gridWorld.OFFSET);
    }
}
