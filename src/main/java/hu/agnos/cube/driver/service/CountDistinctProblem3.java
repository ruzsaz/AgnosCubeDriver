package hu.agnos.cube.driver.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import gnu.trove.list.array.TIntArrayList;
import hu.agnos.cube.CountDistinctCube;

import hu.agnos.cube.Cube;
import hu.agnos.cube.dimension.Dimension;
import hu.agnos.cube.dimension.Node;
import hu.agnos.cube.meta.resultDto.ResultElement;

/**
 * @author ruzsaz
 */
public class CountDistinctProblem3 extends Problem {

    protected CountDistinctProblem3(CountDistinctCube cube, List<Node> baseVector) {
        super(cube, baseVector);
        int numberOfDataRows = cube.getCells().length;
        initForCalculations(cube.getDimensions().size(), numberOfDataRows);
    }

    public ResultElement compute() {
        TIntArrayList[] sourceIntervals = getSourceIntervals(offlineCalculatedLowerIndexes, offlineCalculatedUpperIndexes,
                lowerIndexes, upperIndexes);
        Node[] lastDimNodes = cube.getDimensions().get(cube.getDimensions().size() - 1).getNodes()[1];
        double[] calculatedValues = countDistinctNodes(sourceIntervals[0], sourceIntervals[1], lastDimNodes);
        return new ResultElement(Problem.translateNodes(header), calculatedValues);
    }

    /**
     * Couunts the distinct nodes from a node-set inside an interval-system. The intervals within the system should be
     * ordered increasingly.
     *
     * @param lowerIndexes List of the intervals' lower indexes
     * @param upperIndexes List of the intervals' upper indexes
     * @param nodes Array of nodes to consider
     * @return An array with only 1 element: the number of distinct nodes in the interval-system
     */
    private double[] countDistinctNodes(TIntArrayList lowerIndexes, TIntArrayList upperIndexes, Node[] nodes) {
        int[][]rows = ((CountDistinctCube) cube).getCells();
        int maxCountDistinctElement = ((CountDistinctCube) cube).getMaxCountDistinctElement();
        boolean[] collector = new boolean[maxCountDistinctElement + 1];
        int size = lowerIndexes.size();
        for (int index = 0; index < size; index++) {
            int iMax = upperIndexes.getQuick(index) + 1;
            for (int i = lowerIndexes.getQuick(index); i < iMax; i++) {
                int[] row = rows[i];
                for (int value : row) {
                    collector[value] = true;
                }
            }
        }

        int trueCount = 0;
        for (int i = 0; i < maxCountDistinctElement + 1; i++) {
            if (collector[i]) {
                trueCount++;
            }
        }
        return new double[]{trueCount};
    }

}
