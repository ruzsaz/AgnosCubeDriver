package hu.agnos.cube.driver;

import hu.agnos.cube.Cube;
import hu.agnos.cube.dimension.Dimension;
import hu.agnos.cube.dimension.Node;
import hu.agnos.cube.measure.Cells;
import lombok.Getter;

/**
 * @author parisek
 */
@Getter
public class CubeHandler {

    private final String[] dimensionHeader; // Array of the dimension names in the handled cube
    private final String[] measureHeader;   // Array of the measure names (including calculated) in the cube

    public CubeHandler(String[] dimensionHeader, String[] measureHeader) {
        this.dimensionHeader = dimensionHeader;
        this.measureHeader = measureHeader;
    }

    public void printDims(Cube cube) {
        for (Dimension dim : cube.getDimensions()) {
            System.out.println("DimName: " + dim.getName());
            Node[][] nodes = dim.getNodes();
            for (int i = 0; i < nodes.length; i++) {
                for (int j = 0; j < nodes[i].length; j++) {
                    System.out.println(i + "," + j + ": " + nodes[i][j]);
                }
            }
        }
    }

    public void printCells(Cube cube) {
        Cells c = cube.getCells();
        float[][] cArray = c.getCells();
        for (int j = 0; j < cArray[0].length; j++) {
            for (float[] doubles : cArray) {
                System.out.print("\t" + doubles[j]);
            }
            System.out.println();
        }
    }

    public int getDimensionIndexByName(String name) {
        for (int i = 0; i < dimensionHeader.length; i++) {
            if (dimensionHeader[i].equals(name)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Egy measure (akár kalkulált is) Measures-béli indexét adja meg
     *
     * @param measureName a keresett measure neve
     * @return a measure Measures-béli indexe, vagy ha az nem található meg,
     * akkor -1
     */
    public int getMeasureIndexByName(String measureName) {
        for (int i = 0; i < this.measureHeader.length; i++) {
            if (this.measureHeader[i].equalsIgnoreCase(measureName)) {
                return i;
            }
        }
        return -1;
    }

}
