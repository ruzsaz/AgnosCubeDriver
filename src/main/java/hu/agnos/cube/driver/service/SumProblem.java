package hu.agnos.cube.driver.service;

import java.util.List;

import gnu.trove.list.array.TIntArrayList;

import hu.agnos.cube.Cube;
import hu.agnos.cube.dimension.Node;
import hu.agnos.cube.meta.resultDto.ResultElement;

/**
 *
 * @author ruzsaz
 */
public class SumProblem extends Problem {

    protected SumProblem(Cube cube, int drillVectorId, List<Node> baseVector) {
        super(cube, drillVectorId, baseVector);
        initForCalculations(cube.getDimensions().size());
    }

    public ResultElement compute() {
        TIntArrayList[] calculateSumNyuszival2 = Problem.getSourceIntervals(offlineCalculatedLowerIndexes, offlineCalculatedUpperIndexes,
                lowerIndexes, upperIndexes, cube.getCells().getCells()[0].length - 1);
        double[] calculatedValues = SumProblem.getContainedSumNyuszival2(calculateSumNyuszival2[0], calculateSumNyuszival2[1], cube);
        double[] measureValues = getAllMeasureAsString(calculatedValues);
        return new ResultElement(Problem.translateNodes(header), measureValues, drillVectorId);
    }

    private static double[] getContainedSumNyuszival2(TIntArrayList lowerIndexes, TIntArrayList upperIndexes, Cube cube) {
        float[][] facts = cube.getCells().getCells();
        int numberOfFacts = facts.length;
        double[] result = new double[numberOfFacts];
        int intvIndexMax = lowerIndexes.size();
        for (int intvIndex = 0; intvIndex < intvIndexMax; intvIndex++) {
            int iMax = upperIndexes.getQuick(intvIndex);
            for (int i = lowerIndexes.getQuick(intvIndex); i <= iMax; i++) {
                for (int factIndex = 0; factIndex < numberOfFacts; factIndex++) {
                    result[factIndex] += facts[factIndex][i];
                }
            }
        }
        return result;
    }
    
}
