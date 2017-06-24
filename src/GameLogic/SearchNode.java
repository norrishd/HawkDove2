package GameLogic;

import Tiles.Tile;

/**
 * Node class to run Agent BFS
 */
public class SearchNode {

    public SearchNode parent;
    public int depth;
    public Tile tile;

    public SearchNode(Tile tile, SearchNode parent, int depth) {
        this.tile = tile;
        this.parent = parent;
        this.depth = depth;
    }
}
