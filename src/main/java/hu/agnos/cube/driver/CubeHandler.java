package hu.agnos.cube.driver;

import hu.agnos.molap.Cube;
import hu.agnos.molap.dimension.Node;
import hu.agnos.molap.dimension.Dimension;
import hu.agnos.molap.dimension.Hierarchy;
import hu.agnos.molap.measure.Cells;

/**
 *
 * @author parisek
 */
public class CubeHandler {


    /**
     * A cube-on hierarchiák egyedi nevét tartalmazó tömb, a sorrend kötött.
     */
    private final String[] hierarchyHeader;

    /**
     * A cube-ban szereplő measure-ök (a kalkuláltaké is) neveit tartalmazó tömb
     */
    private final String[] measureHeader;

    private final int hierarchySize;


    public CubeHandler(String[] hierarchyHeader, String[] measureHeader) {
        this.hierarchyHeader = hierarchyHeader;
        this.measureHeader = measureHeader;
        this.hierarchySize = this.hierarchyHeader.length;
    }


    public void printDims(Cube cube) {
        for (Dimension dim : cube.getDimensions()) {
            for (Hierarchy hier : dim.getHierarchies()) {
                System.out.println("Hier name : " + hier.getHierarchyUniqueName());

                Node[][] nodes = hier.getNodes();

                for (int i = 0; i < nodes.length; i++) {
                    for (int j = 0; j < nodes[i].length; j++) {
                        System.out.println(i + "," + j + ": " + nodes[i][j]);
                    }
                }
            }
        }
    }
//

    public String[] getHierarchyHeader() {
        return hierarchyHeader;
    }

    public void printCells(Cube cube) {
        Cells c = cube.getCells();

        double[][] cArray = c.getCells();
        for (int j = 0; j < cArray[0].length; j++) {
            for (double[] doubles : cArray) {

                System.out.print("\t" + doubles[j]);
            }
            System.out.println();
        }
    }

    public int getHierarchySize() {
        return hierarchySize;
    }


    public int getHierarchyIndexByUniqueName(String uniqueName) {
        for (int i = 0; i < hierarchyHeader.length; i++) {
            if (hierarchyHeader[i].equals(uniqueName)) {
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
    public int getMeasureIndexByUniqueName(String measureName) {
        for (int i = 0; i < this.measureHeader.length; i++) {
            if (this.measureHeader[i].equalsIgnoreCase(measureName)) {
                return i;
            }
        }
        return -1;
    }

}
