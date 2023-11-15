package hu.agnos.cube.driver.zolikaokos;

import java.util.List;

import gnu.trove.list.array.TIntArrayList;

import hu.agnos.cube.Cube;
import hu.agnos.cube.dimension.Node;
import hu.agnos.cube.meta.resultDto.ResultElement;

/**
 * @author ruzsaz
 */
public class CountDistinctProblem extends Problem {

    protected CountDistinctProblem(Cube cube, int drillVectorId, List<Node> baseVector) {
        super(cube, drillVectorId, baseVector);
        initForCalculations(cube.getDimensions().size() - 1);
    }

    public ResultElement compute() {
        TIntArrayList[] sourceIntervals = Problem.getSourceIntervals(offlineCalculatedLowerIndexes, offlineCalculatedUpperIndexes,
                lowerIndexes, upperIndexes, cube.getCells().getCells()[0].length - 1);
        Node[] lastDimNodes = cube.getDimensions().get(cube.getDimensions().size() - 1).getNodes()[1];
        double[] calculatedValues = CountDistinctProblem.countDistinctNodes(sourceIntervals[0], sourceIntervals[1], lastDimNodes);
        double[] measureValues = getAllMeasureAsString(calculatedValues);
        return new ResultElement(Problem.translateNodes(header), measureValues, drillVectorId);
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
    private static double[] countDistinctNodes(TIntArrayList lowerIndexes, TIntArrayList upperIndexes, Node[] nodes) {
        int result = 0;
        int nodeLength = nodes.length;
        for (int i = 0; i < nodeLength; i++) {
            if (CountDistinctProblem.isNodeContained(lowerIndexes, upperIndexes, nodes[i])) {
                result++;
            }
        }
        return new double[]{result};
    }

    /**
     * Determines if a node (coordinate value in a single dimension) is contained in an interval-system. The elements in
     * the interval-system should be monotonic increasing order.
     *
     * @param lowerIndexes List of the intervals' lower indexes
     * @param upperIndexes List of the intervals' upper indexes
     * @param node Node to inspect
     * @return True if the intervals contain at least one occurrence of the node, false if not
     */
    private static boolean isNodeContained(TIntArrayList lowerIndexes, TIntArrayList upperIndexes, Node node) {

        int[] nodeLowerIndexes = node.getIntervalsLowerIndexes();
        int[] nodeUpperIndexes = node.getIntervalsUpperIndexes();
        int iMax = nodeLowerIndexes.length;
        for (int i = 0; i < iMax; i++) {
            for (int j = nodeLowerIndexes[i]; j <= nodeUpperIndexes[i]; j++) {
                if (Algorithms.isIntervalSystemContains(lowerIndexes, upperIndexes, j)) {
                    return true;
                }
            }

        }
        return false;
    }

}