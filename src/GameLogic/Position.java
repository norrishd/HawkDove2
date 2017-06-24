package GameLogic;

/**
 * Store the (x, y) coordinates for a tile or agent
 */
public class Position {

    public int x;
    public int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Get array of Positions adjacent to this position
    Position[] getAdjacentPositions() {
        return new Position[] {new Position(this.x + 1, this.y), new Position(this.x - 1, this.y),
                new Position(this.x, this.y - 1), new Position(this.x, this.y + 1)};
    }

    public String getCoords(){
        return "(" + x + "," + y + ")";
    }
}
