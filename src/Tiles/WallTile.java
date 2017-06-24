package Tiles;

import GameLogic.Position;

/**
 * A wall tile. Cannot be passed through and never contains food
 */
public class WallTile extends Tile {

    public WallTile(Position position, String image) {
        super(position, image);
    }

    @Override
    public boolean walkable() {
        return false;
    }
}
