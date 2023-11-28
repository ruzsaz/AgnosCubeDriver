package hu.agnos.cube.driver.service;

import java.util.List;

import gnu.trove.list.array.TIntArrayList;

import hu.agnos.cube.CountDistinctCube;
import hu.agnos.cube.dimension.Node;
import hu.agnos.cube.meta.resultDto.ResultElement;

/**
 *
 */
public class CountDistinctProblem extends Problem {

    protected CountDistinctProblem(CountDistinctCube cube, List<Node> baseVector) {
        super(cube, baseVector);
        int numberOfDataRows = cube.getCells().length;
        initForCalculations(numberOfDataRows);
    }

    public ResultElement compute() {
        TIntArrayList[] sourceIntervals = getSourceIntervals(offlineCalculatedLowerIndexes, offlineCalculatedUpperIndexes,
                lowerIndexes, upperIndexes);
        double[] calculatedValues = countDistinctNodes(sourceIntervals[0], sourceIntervals[1]);
        return new ResultElement(Problem.translateNodes(header), calculatedValues);
    }

    /**
     * Counts the distinct id values in the data rows inside an interval-system. The intervals within the system should
     * be ordered increasingly.
     *
     * @param tLowerIndexes List of the intervals' lower indexes
     * @param tUpperIndexes List of the intervals' upper indexes
     * @return An array with only 1 element: the number of distinct nodes in the interval-system
     */
    private double[] countDistinctNodes(TIntArrayList tLowerIndexes, TIntArrayList tUpperIndexes) {
        int[][] rows = ((CountDistinctCube) cube).getCells();
        int maxCountDistinctElement = ((CountDistinctCube) cube).getMaxCountDistinctElement();
        boolean[] collector = new boolean[maxCountDistinctElement + 1];
        int size = tLowerIndexes.size();
        for (int index = 0; index < size; index++) {
            int iMax = tUpperIndexes.getQuick(index) + 1;
            for (int i = tLowerIndexes.getQuick(index); i < iMax; i++) {
                int[] row = rows[i];
                for (int value : row) {
                    collector[value] = true;
                }
            }
        }
        return new double[]{CountDistinctProblem.numberOfTrues(collector)};
    }

    private static int numberOfTrues(boolean[] booleanArray) {
        int trueCount = 0;
        int arraySize = booleanArray.length;
        for (int i = 0; i < arraySize; i++) {
            if (booleanArray[i]) {
                trueCount++;
            }
        }
        return trueCount;
    }

}
