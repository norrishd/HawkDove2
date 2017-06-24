package Tiles;

/**
 * Enum class to store pre-configured patterns of tiles in the world:
 *
 * Open field: all Tiles are walkable except the perimeter
 * Random sparse: a sparse scattering of random placed barriers in the field
 * Random dense: a denser scattering of randomly placed barriers in the field
 * Corridors: long parallel pathways
 */
public enum TilePattern {
    OPEN_FIELD, RANDOM_SPARSE, RANDOM_DENSE, CORRIDORS

}
