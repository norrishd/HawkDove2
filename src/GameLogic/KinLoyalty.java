package GameLogic;

/**
 * Options for how loyal kin will be to each other.
 * None: parent and child will immediately treat each other as strangers
 * Cool-off: parent and child will only play Dove toward each other for 10 moves
 * Eternal: parent and child will only ever play Dove toward each other
 */
public enum KinLoyalty {
    NONE, COOL_OFF, ETERNAL
}



