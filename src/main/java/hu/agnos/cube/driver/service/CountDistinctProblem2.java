package hu.agnos.cube.driver.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import gnu.trove.list.array.TIntArrayList;

import hu.agnos.cube.Cube;
import hu.agnos.cube.dimension.Dimension;
import hu.agnos.cube.dimension.Node;
import hu.agnos.cube.driver.util.IntervalAlgorithms;
import hu.agnos.cube.meta.resultDto.ResultElement;

/**
 * @author ruzsaz
 */
public class CountDistinctProblem2 extends Problem {

    // TODO: tesztelni, hogy tényleg az utolsó dimenzió-e a countdistinctdime.
    protected CountDistinctProblem2(Cube cube, int drillVectorId, List<Node> baseVector) {
        super(cube, drillVectorId, baseVector);
        Dimension countDistinctDimension = cube.getDimensions().get(cube.getDimensions().size() - 1);
        int numberOfDataRows = countDistinctDimension.getNode(0, 0).getIntervalsUpperIndexes()[0];
        initForCalculations(cube.getDimensions().size() - 1, numberOfDataRows);
    }

    public ResultElement compute() {
        TIntArrayList[] sourceIntervals = getSourceIntervals(offlineCalculatedLowerIndexes, offlineCalculatedUpperIndexes,
                lowerIndexes, upperIndexes);
        Node[] lastDimNodes = cube.getDimensions().get(cube.getDimensions().size() - 1).getNodes()[1];
        double[] calculatedValues = countDistinctNodes(sourceIntervals[0], sourceIntervals[1], lastDimNodes);
        return new ResultElement(Problem.translateNodes(header), calculatedValues, drillVectorId);
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
        int result = 0;
        int nodeLength = nodes.length; //Math.min(nodes.length, 10);
        Set<Integer> collector = new HashSet<>(10000);
        for (int index = 0; index < lowerIndexes.size(); index++) {
            int iMax = upperIndexes.getQuick(index) + 1;
            for (int i = lowerIndexes.getQuick(index); i < iMax; i++) {
                collector.add(cube.getKecske()[i]);
            }
        }

        return new double[]{collector.size()};
    }



}
