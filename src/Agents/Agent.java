package Agents;

import GameLogic.GridWorld;
import GameLogic.Position;
import GameLogic.SearchNode;
import Tiles.Tile;
import javafx.geometry.Pos;

import java.util.*;

/**
 * Abstract class for agents which navigate navigate the environment
 */
public abstract class Agent {

    // Strategy, starting position etc
    String name;
    public Position position;
    private Position last_pos = null;   // previous position agent was at. Prefers spawning here, and not re-visiting
    private Position next_pos = null;
    public int food;
    private int steps_taken;            // way to keep track how long an agent has survived
    private int children_spawned;

    public SearchNode goal;              // identified food, currently aiming to collect
    private HashMap<Agent, ArrayList<Boolean>> pastEncounters;      // remember outcomes of past encounters with agents
    private Random r = new Random();

    Agent(Position spawnLocation, String name) {
        this.position = spawnLocation;
        this.food = GridWorld.STARTING_FOOD;
        this.steps_taken = 0;
        this.children_spawned = 0;
        this.name = name;
        this.pastEncounters = new HashMap<>();
    }

    /**
     * A modified Depth-limited breadth-first search to find nearby food resources
     *
     * First checks whether any adjacent tiles have food, and if so randomly picks one of those to go to
     *
     * If not, and if food was previously found in a search, checks if that food is still there, and if so progresses
     * on towards it.
     * If no previous goal or food has disappeared, uses BFS to look ahead as far as the limit (current default 5)
     * and sets a new goal if finds food
     * If no food found in the search, randomly picks a tile to move to, or stays put, with a bias against returning
     * to whatever tile it was previously at
     *
     * @param tiles an (M*N) array of Tile objects, one of which will contain the current agent
     * @param max_depth the max search depth to continue BFS to
     */
    public void searchForFood(Tile[][] tiles, int max_depth) {
        Position[] adjacentPositions = getAdjacentPositions(this.position);
        ArrayList<Position> adjacentFood = new ArrayList<>();

        for (Position pos : adjacentPositions) {
            if (tiles[pos.x][pos.y].walkable() && tiles[pos.x][pos.y].hasFood())
                adjacentFood.add(pos);
        }

        // If any adjacent square has food, move to one randomly
        if (adjacentFood.size() > 0) {
            int choice = r.nextInt(adjacentFood.size());
            next_pos = adjacentFood.get(choice);
            // if no adjacent food, check if there's still food at the goal and if so move towards it
        } else if (goal != null) {
            if (goal.tile.hasFood()) {
                next_pos = findNextTilePos(goal, tiles, max_depth);
                // If not, DL-BFS to try to find food
            } else {
                goal = null;
                DL_BFS(tiles, max_depth);
            }
            // No adjacent food and no goal - DL-BFS to try to find
        } else
            DL_BFS(tiles, max_depth);
    }

    // Depth-limited BFS to try to find a nearby food
    private void DL_BFS(Tile[][] tiles, int max_depth) {
        Position[] adjacentPositions;
        HashMap<Position, SearchNode> discovered = new HashMap<>();             // remember visited tiles
        LinkedList<SearchNode> queue = new LinkedList<>();                      // FIFO queue
        queue.add(new SearchNode(tiles[position.x][position.y], null, 0));

        boolean goalFound = false;
        while (!(queue.isEmpty() || goalFound)) {
            SearchNode node = queue.poll();
            if (node.tile.hasFood()) {
                goal = node;
                next_pos = findNextTilePos(goal, tiles, max_depth);
                goalFound = true;
            } else {
                adjacentPositions = getAdjacentPositions(node.tile.position);
                for (Position pos : adjacentPositions) {
                    // ignore edge board tiles, which will be walls
                    if (pos.x < 1 || pos.x > tiles.length - 2 || pos.y < 1 || pos.y > tiles.length - 2)
                        continue;
                    // successors must be previously undiscovered, walkable, and within the max search depth
                    if (!discovered.containsKey(tiles[pos.x][pos.y].position) && tiles[pos.x][pos.y].walkable()
                            && node.depth < max_depth) {
                        SearchNode newNode = new SearchNode(tiles[pos.x][pos.y], node, node.depth + 1);
                        discovered.put(tiles[pos.x][pos.y].position, newNode);
                        queue.add(newNode);
                    }
                }
            }
        }

        // No food found nearby; choose a random move
        if (!goalFound) {
            adjacentPositions = getAdjacentPositions(this.position);
            ArrayList<Position> adjacentWalkables = new ArrayList<>();
            for (Position pos : adjacentPositions) {
                if (tiles[pos.x][pos.y].walkable())
                    adjacentWalkables.add(pos);
            }

            int choice;
            if (adjacentWalkables.size() > 0) {         // Possible there are no valid moves from current position
                choice = r.nextInt(adjacentWalkables.size());
                // re-roll once if select previous tile. Puts bias against going backwards though still allows
                if (adjacentWalkables.get(choice).equals(last_pos))
                    choice = r.nextInt(adjacentWalkables.size());
                next_pos = adjacentWalkables.get(choice);
            } else {                                    // no adjacent walkable tiles from current position
                next_pos = position;
            }


        }
    }

    //  Helper function to get adjacent positions to a given position
    private Position[] getAdjacentPositions(Position pos) {

        return new Position[] {new Position(pos.x + 1, pos.y), new Position(pos.x - 1, pos.y),
                new Position(pos.x, pos.y - 1), new Position(pos.x, pos.y + 1)};
    }

    // Helper function to take a SearchNode and finds the next Tile in the path
    private Position findNextTilePos(SearchNode node, Tile[][] tiles, int max_depth) {
        while (Math.abs(position.x - node.tile.position.x) + Math.abs(position.y - node.tile.position.y) != 1) {
            if (node.parent != null)
                node = node.parent;
            else {
                // Have strayed off path
                goal = null;
                // Get puzzled for a turn if food gets stolen by another agent
                return this.position;
            }
        }
        return node.tile.position;
    }

    // Move to next square
    public void move() {
        if (!next_pos.equals(position)) {
            last_pos = position;
            position = next_pos;
            next_pos = null;
            steps_taken += 1;
            if (steps_taken % GridWorld.STEPS_TO_LOSE_FOOD == 0) {
                this.lose_food(1);
            }
        }
        // if goal's food is gone (whether or not this agent took it), can forget goal
        if (goal != null && !goal.tile.hasFood())
            goal = null;
    }

    // If finding food uncontested or gaining some from a game
    public void gain_food(int v) {
        this.food += v;
    }

    // Lose food either from walking, spawning or losing a game when playing Hawk
    public void lose_food(int v) {
        this.food -= v;
    }

    /**
     * Abstract method that must be implemented to play a game. Agent received information about the opposing agent
     * @param opposingAgent Another agent trying to claim the food on the same turn
     * @return A Strategy move played by the Agent in this game
     */
    public Strategy playGame(Agent opposingAgent) {
        return null;
    }

    // Receive location to spawn a child if there is a free adjacent tile
    Agent getChildSpawnLocation(Tile[][] tiles) {

        Position spawnPos = null;

        // if previous position is vacant, choose that
        if (last_pos != null && tiles[last_pos.x][last_pos.y].agents.size() == 0)
            spawnPos = last_pos;
        else {
            // else, any adjacent vacant tile
            Position[] adjacents = getAdjacentPositions(position);
            for (Position pos : adjacents) {
                if (tiles[last_pos.x][last_pos.y].agents.size() == 0)
                    spawnPos = pos;
            }
        }

        // all surrounding tiles are walls or occupied; don't spawn yet
        if (spawnPos == null)
            return null;
        else
            return spawnChild(spawnPos);
    }

    // Spawn a child of same type as parent
    public abstract Agent spawnChild(Position spawnPos);

    @Override
    public String toString() {
        String returnString = "Name: " + name + "\nAgent type: " + this.getClass() +
                "\nPosition: " + position.getCoords() + "\nLast pos: " ;
        returnString = last_pos != null ? returnString + last_pos.getCoords() : returnString + "none";
        returnString = goal != null ? returnString + "\nGoal: " + goal.tile.position.getCoords() : returnString + "\nGoal: none";
        returnString += "\nFood: " + food + "\nSteps: " + steps_taken + "\nChildren spawned: " + children_spawned + "\n";

        return returnString;
    }
}
