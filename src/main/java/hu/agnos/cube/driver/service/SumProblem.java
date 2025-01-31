package hu.agnos.cube.driver.service;

import java.util.List;

import gnu.trove.list.array.TIntArrayList;

import hu.agnos.cube.Cube;
import hu.agnos.cube.ClassicalCube;
import hu.agnos.cube.dimension.Node;
import hu.agnos.cube.meta.queryDto.CacheKey;
import hu.agnos.cube.meta.resultDto.ResultElement;

/**
 *
 * @author ruzsaz
 */
public final class SumProblem extends Problem {

    protected SumProblem(ClassicalCube cube, List<Node> baseVector) {
        super(cube, baseVector);
        setCachedResult(lookupInCache(cube));
        if (getCachedResult() == null) {
            int numberOfDataRows = cube.getCells()[0].length;
            initForCalculations(numberOfDataRows);
        }
    }

    private ResultElement lookupInCache(ClassicalCube classicalCube) {
        if (classicalCube.getCache() != null) {
            CacheKey key = CacheKey.fromNodeList(baseVector);
            double[] value = classicalCube.getCache().get(key);
            if (value != null) {
                return new ResultElement(Problem.translateNodes(baseVector), value);
            }
        }
        return null;
    }

    public ResultElement compute() {
        if (getCachedResult() != null) {
            return getCachedResult();
        }
        TIntArrayList[] calculateSumNyuszival2 = getSourceIntervals(offlineCalculatedLowerIndexes, offlineCalculatedUpperIndexes,
                lowerIndexes, upperIndexes);
        double[] calculatedValues = SumProblem.getContainedSumNyuszival2(calculateSumNyuszival2[0], calculateSumNyuszival2[1], (ClassicalCube)cube);
        double[] measureValues = getAllMeasureAsString(calculatedValues);
        return new ResultElement(Problem.translateNodes(header), measureValues);
    }

    /**
     * Calculates the sum of the facts in the given intervals.
     *
     * @param lowerIndexes the lower indexes of the intervals
     * @param upperIndexes the upper indexes of the intervals
     * @param cube the cube
     * @return the sum of the facts in the given intervals
     */
    private static double[] getContainedSumNyuszival2(TIntArrayList lowerIndexes, TIntArrayList upperIndexes, ClassicalCube cube) {
        float[][] facts = cube.getCells();
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
