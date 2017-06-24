package GameLogic;

import Agents.Agent;
import Tiles.FloorTile;
import Tiles.Tile;
import Tiles.TilePattern;
import Tiles.WallTile;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Random;

/**
 * A grid world, containing all tiles, agents and game logic
 */
public class GridWorld extends WorldSettings {

    public Tile[][] tiles;             // store all board pieces
    public ArrayList<Agent> agents;    // store all agents in play
    private Random r = new Random();

    public GridWorld() {
        super();
        this.tiles = new Tile[WORLD_X_TILES][WORLD_Y_TILES];     // allocate memory for all needed tiles
        this.agents = new ArrayList<>();
    }

    /**
     * Populate the matrix of Tiles for this GridWOrld
     * @param pattern a TilePattern enum, indicating what the world will look like
     */
    public void generateWorld(TilePattern pattern) {

        for (int y = 0; y < WORLD_Y_TILES; y++) {
            for (int x = 0; x < WORLD_X_TILES; x++) {
                // Make all perimeter Tiles into Walls
                if (x == 0 || x == WORLD_Y_TILES - 1 || y == 0 || y == WORLD_X_TILES - 1) {
                    // TODO add tile sprites and randomise different varieties
                    tiles[x][y] = new WallTile(new Position(x, y), "Wal11.png");
                } else {
                    switch (pattern) {
                        case OPEN_FIELD:
                            tiles[x][y] = new FloorTile(new Position(x, y), "Floor1.png");
                            break;

                        case RANDOM_SPARSE:
                            tiles[x][y] = r.nextDouble() > 0.9 ? new WallTile(new Position(x, y), "Wal11.png") :
                                    new FloorTile(new Position(x, y), "Floor1.png");
                            break;

                        case RANDOM_DENSE:
                            tiles[x][y] = r.nextDouble() > 0.75 ? new WallTile(new Position(x, y), "Wal11.png") :
                                    new FloorTile(new Position(x, y), "Floor1.png");
                            break;

                        case CORRIDORS:
                            boolean gapIncluded = false;
                            if (x % 2 == 1)     // every odd row should be clear
                                tiles[x][y] = new FloorTile(new Position(x, y), "Floor1.png");
                            else {
                                // If at last position where there could be a gap, make sure there is at least one
                                if (y == WORLD_Y_TILES - 2) {
                                    for (int i = 0; i < y; i++)
                                        if (tiles[x][i] instanceof FloorTile)
                                            gapIncluded = true;
                                    if (!gapIncluded)
                                        tiles[x][y] = new FloorTile(new Position(x, y), "Floor1.png");
                                    else {
                                        tiles[x][y] = r.nextDouble() < (1.0 / (WORLD_Y_TILES - 2)) ?
                                                new FloorTile(new Position(x, y), "Floor1.png") :
                                                new WallTile(new Position(x, y), "Wal11.png");
                                    }
                                } else {        // randomly assign a gap with E(# gaps) = 1 per row
                                    tiles[x][y] = r.nextDouble() < (1.0 / (WORLD_Y_TILES - 2)) ?
                                            new FloorTile(new Position(x, y), "Floor1.png") :
                                            new WallTile(new Position(x, y), "Wal11.png");
                                }
                            }
                            break;

                        default:
                            tiles[x][y] = new FloorTile(new Position(x, y), "Floor1.png");
                            break;
                    }
                }
            }
        }
    }

    // Add an agent to a specific tile on the map
    public void addAgent(Agent newVisitor) {
        Position spawnLocation = newVisitor.position;
        if (spawnLocation.x < 0 || spawnLocation.x > WORLD_Y_TILES - 1 ||
                spawnLocation.y < 0 || spawnLocation.y > WORLD_X_TILES - 1)
            throw new IndexOutOfBoundsException("That's outside the world!");
        else if (tiles[spawnLocation.x][spawnLocation.y] == null)
            throw new NoSuchElementException("World tiles haven't been initiated yet");
        else if (!tiles[spawnLocation.x][spawnLocation.y].walkable())
            throw new IllegalArgumentException("Cannot spawn an Agent on this kind of tile");
        tiles[spawnLocation.x][spawnLocation.y].addAgent(newVisitor);
        agents.add(newVisitor);
    }

    // Get random walkable tile
    public Position getWalkableTile() {
        if (tiles.length == 0)
            throw new NoSuchElementException("World tiles haven't been initiated yet.");

        int x = r.nextInt(WORLD_Y_TILES);
        int y = r.nextInt(WORLD_X_TILES);

        // DANGER! Infinite loop!
        while (!tiles[x][y].walkable()) {
            x = r.nextInt(WORLD_Y_TILES);
            y = r.nextInt(WORLD_X_TILES);
        }
        return new Position(x, y);
    }

    // User pressed button for next turn
    void moveAgents() {
        for (Agent agent : agents) {
            Position oldPos = agent.position;
            agent.searchForFood(tiles, DFSlimit);
            agent.move();
            Position newPos = agent.position;

            // if agent moved, update tile it is associated with & harvest food
            if (!oldPos.equals(newPos)) {
                tiles[oldPos.x][oldPos.y].agents.remove(agent);
                tiles[newPos.x][newPos.y].agents.add(agent);
                if (tiles[newPos.x][newPos.y].hasFood()) {
                    tiles[newPos.x][newPos.y].loseFood();
                    agent.gain_food(1);
                }
            }
        }
    }

    // if any agent's food stores drop below 0, it dies
    void killDepletedAgents() {

        ArrayList<Agent> agentsToDelete = new ArrayList<>();

        if (agents.size() > 0) {
            for (Agent agent : agents) {
                if (agent.food <= 0) {
                    Position deathPlace = agent.position;
                    agentsToDelete.add(agent);
                    tiles[deathPlace.x][deathPlace.y].agents.remove(agent);
                }
            }
        }
        if (agentsToDelete.size() > 0)
            for (Agent walkingDeadAgent : agentsToDelete)
                agents.remove(walkingDeadAgent);
    }

    // Find all agents with enough food to spawn a new agent
    void spawnAgents() {
        // Need to add new agents to a separate list and join after iterating through array, or get
        // concurrent modification error
        ArrayList<Agent> newAgents = new ArrayList<>();

        if (agents.size() > 0) {
            for (Agent agent : agents) {
                if (agent.food >= SPAWN_THRESHOLD) {
                    Position[] adjacents = agent.position.getAdjacentPositions();
                    ArrayList<Position> possibleSpawnLocs = new ArrayList<>();
                    // find potential adjacent spawn locations
                    for (Position pos : adjacents) {
                        // ignore squares outside potential range
                        if (pos.x < 1 || pos.x > tiles.length - 2 || pos.y < 1 || pos.y > tiles.length - 2)
                            continue;
                        // find all adjacent squares that are walkable and don't have an agent there
                        else if (tiles[pos.x][pos.y].walkable() && tiles[pos.x][pos.y].agents.size() == 0) {
                            possibleSpawnLocs.add(pos);
                        }
                    }
                    if (possibleSpawnLocs.size() > 0) {
                        agent.lose_food(STARTING_FOOD + SPAWN_COST);
                        newAgents.add(agent.spawnChild(possibleSpawnLocs.get(r.nextInt(possibleSpawnLocs.size()))));
                    }
                }
            }
        }
        if (newAgents.size() > 0) {
            agents.addAll(newAgents);
        }
    }

    // randomly add food to a tile
    public void growFood() {
        // possibly grow food
        if (r.nextDouble() < FOOD_GROWTH_RATE) {
            int x = r.nextInt(WORLD_Y_TILES - 3) + 1;
            int y = r.nextInt(WORLD_X_TILES - 3) + 1;

            if (tiles[x][y].walkable())
                tiles[x][y].growFood();
        }
    }
}
