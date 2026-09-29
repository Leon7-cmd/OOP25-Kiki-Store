package it.unibo.kikistore.view.utility;

/**
 * Utility class providing operations on grids.
 */
public final class GridUtils {

    private GridUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Performs copy of a matrix.
     *
     * @param source the source matrix to duplicate
     * @return a cloned copy of the matrix
     */
    public static int[][] deepCopyGrid(final int[][] source) {
        if (source == null) {
            return new int[0][0];
        }
        final int[][] copy = new int[source.length][];
        for (int i = 0; i < source.length; i++) {
            copy[i] = source[i] != null ? source[i].clone() : null;
        }
        return copy;
    }
}
