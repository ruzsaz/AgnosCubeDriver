package hu.agnos.cube.driver.service;

import java.util.ArrayList;
import java.util.List;

import gnu.trove.list.array.TIntArrayList;
import lombok.Getter;
import lombok.Setter;

import hu.agnos.cube.Cube;
import hu.agnos.cube.dimension.Dimension;
import hu.agnos.cube.dimension.Node;
import hu.agnos.cube.driver.util.IntervalAlgorithms;
import hu.agnos.cube.driver.util.PostfixCalculator;
import hu.agnos.cube.measure.AbstractMeasure;
import hu.agnos.cube.measure.CalculatedMeasure;

import hu.agnos.cube.meta.resultDto.NodeDTO;
import hu.agnos.cube.meta.resultDto.ResultElement;

/**
 * Abstract class to represent a problem to solve in the cube. The problem is to answer a single measure-array retrieval
 * from the cube. The created problem can be either a SumProblem, where data values are determined from the leaf-level
 * nodes by summary, or a CountDistinctProblem, where the single indicator is the count distinct result of the elements
 * defined by the value nodes in each dimension.
 */
public abstract class Problem {

    protected final Cube cube;
    protected final List<Node> baseVector;
    protected int[][] offlineCalculatedLowerIndexes;
    protected int[][] offlineCalculatedUpperIndexes;
    protected int[][] lowerIndexes;
    protected int[][] upperIndexes;
    protected Node[] header;
    private int numberOfRows; // Number of rows in the cube's dataTable

    @Getter @Setter
    private ResultElement cachedResult;

    protected Problem(Cube cube, List<Node> baseVector) {
        this.cube = cube;
        this.baseVector = baseVector;
    }

    static NodeDTO[] translateNodes(List<Node> nodes) {
        int nodeNumber = nodes.size();
        NodeDTO[] result = new NodeDTO[nodeNumber];
        for (int i = 0; i < nodeNumber; i++) {
            result[i] = NodeDTO.fromNode(nodes.get(i));
        }
        return result;
    }

    static NodeDTO[] translateNodes(Node[] nodes) {
        int nodeNumber = nodes.length;
        NodeDTO[] result = new NodeDTO[nodeNumber];
        for (int i = 0; i < nodeNumber; i++) {
            result[i] = NodeDTO.fromNode(nodes[i]);
        }
        return result;
    }

    public int getNumberOfAffectedRows() {
        TIntArrayList[] affectedIntervals = getSourceIntervals(offlineCalculatedLowerIndexes, offlineCalculatedUpperIndexes, lowerIndexes, upperIndexes);
        int numberOfintervals = affectedIntervals[0].size();
        int result = 0;
        for (int i = 0; i < numberOfintervals; i++) {
            result += affectedIntervals[1].get(i) - affectedIntervals[0].get(i) + 1;
        }
        return result;
    }

    public int getNumberOfAffectedIntervals() {
        TIntArrayList[] affectedIntervals = getSourceIntervals(offlineCalculatedLowerIndexes, offlineCalculatedUpperIndexes, lowerIndexes, upperIndexes);
        return affectedIntervals[0].size();
    }

    /**
     * Gets the intervals to aggregate the values from to get the indicators at the requested aggregation level.
     *
     * @param offlineCalculatedLowerIndexes Array of the lower indexes in the dimensions along the values are
     *         pre-calculated (First index is the index of the dimension, second is the index of the interval)
     * @param offlineCalculatedUpperIndexes Array of the upper indexes in the dimensions along the values are
     *         pre-calculated (First index is the index of the dimension, second is the index of the interval)
     * @param lowerIndexes Array of the lower indexes in the on-the-fly aggregating dimensions (First index is
     *         the index of the dimension, second is the index of the interval)
     * @param upperIndexes Array of the upper indexes in the on-the-fly aggregating dimensions (First index is
     *         the index of the dimension, second is the index of the interval)
     * @return The monotonic increasing lower indexes and corresponding upper indexes in an array of 2 dimensions
     */
    protected TIntArrayList[] getSourceIntervals(int[][] offlineCalculatedLowerIndexes,
                                                        int[][] offlineCalculatedUpperIndexes,
                                                        int[][] lowerIndexes,
                                                        int[][] upperIndexes) {
        double[] result;
        // Az olapos dimenziókkal való metszőintervallum megállapítása.
        int[] offlineCalculatedIntersection = IntervalAlgorithms.monotonicIntersection(offlineCalculatedLowerIndexes,
                offlineCalculatedUpperIndexes, 0, numberOfRows - 1);

        // A menet közben aggregálandó intervallumok elmetszése az olap-sávval.
        int numberOfOnTheFlyDimensions = lowerIndexes.length;
        int[] minTrimIndex = new int[numberOfOnTheFlyDimensions];
        int[] maxTrimIndex = new int[numberOfOnTheFlyDimensions];
        for (int onTheFlyCalculatedDimIndex = 0; onTheFlyCalculatedDimIndex < numberOfOnTheFlyDimensions; onTheFlyCalculatedDimIndex++) {
            int[] trimIndexes = IntervalAlgorithms.trimIntervals(lowerIndexes[onTheFlyCalculatedDimIndex],
                    upperIndexes[onTheFlyCalculatedDimIndex],
                    offlineCalculatedIntersection[0],
                    offlineCalculatedIntersection[1]);
            minTrimIndex[onTheFlyCalculatedDimIndex] = trimIndexes[0];
            maxTrimIndex[onTheFlyCalculatedDimIndex] = trimIndexes[1];
        }
        // Menet közben aggregálandó intervallumok metszete.
        return IntervalAlgorithms.intersection(offlineCalculatedIntersection[0], offlineCalculatedIntersection[1], lowerIndexes, upperIndexes, minTrimIndex, maxTrimIndex);
    }

    public abstract ResultElement compute();

    /**
     * Ez az eljárás feltölti az intervellum rendszereket, továbbá a header részt is kitölti. Mivel az intervallumok
     * feltöltéséhez a Nodokat ki kell keresni, így célszerű ebben a lépésben a headert is kitölteni (különben újból ki
     * kell keresni a nodot).
     */
    void initForCalculations(int numberOfDataRows) {
        this.numberOfRows = numberOfDataRows;
        int numberOfDimensionsToUse = cube.getDimensions().size();
        List<Dimension> dimensions = cube.getDimensions();
        this.header = new Node[numberOfDimensionsToUse];

        List<int[]> offlineCalculatedLowerIndexesList = new ArrayList<>();
        List<int[]> offlineCalculatedUpperIndexesList = new ArrayList<>();
        List<int[]> lowerIndexesList = new ArrayList<>();
        List<int[]> upperIndexesList = new ArrayList<>();

        for (int i = 0; i < numberOfDimensionsToUse; i++) {
            Dimension dimension = dimensions.get(i);
            Node n = baseVector.get(i);

            header[i] = n;
            if (dimension.isOfflineCalculated()) {
                offlineCalculatedLowerIndexesList.add(n.getIntervalsLowerIndexes());
                offlineCalculatedUpperIndexesList.add(n.getIntervalsUpperIndexes());
            } else {
                lowerIndexesList.add(n.getIntervalsLowerIndexes());
                upperIndexesList.add(n.getIntervalsUpperIndexes());
            }
        }

        int offlineCalculatedLowerIndexesSize = offlineCalculatedLowerIndexesList.size();
        offlineCalculatedLowerIndexes = new int[offlineCalculatedLowerIndexesSize][];
        for (int i = 0; i < offlineCalculatedLowerIndexesSize; i++) {
            offlineCalculatedLowerIndexes[i] = offlineCalculatedLowerIndexesList.get(i);
        }

        int offlineCalculatedUpperIndexesSize = offlineCalculatedUpperIndexesList.size();
        offlineCalculatedUpperIndexes = new int[offlineCalculatedUpperIndexesSize][];
        for (int i = 0; i < offlineCalculatedUpperIndexesSize; i++) {
            offlineCalculatedUpperIndexes[i] = offlineCalculatedUpperIndexesList.get(i);
        }

        int lowerIndexesSize = lowerIndexesList.size();
        lowerIndexes = new int[lowerIndexesSize][];
        for (int i = 0; i < lowerIndexesSize; i++) {
            lowerIndexes[i] = lowerIndexesList.get(i);
        }

        int upperIndexesSize = lowerIndexesList.size();
        upperIndexes = new int[upperIndexesSize][];
        for (int i = 0; i < upperIndexesSize; i++) {
            upperIndexes[i] = upperIndexesList.get(i);
        }
    }

    /**
     * Ez az érték a megkapott valós measure értékeket a kívánt formátumra alakítja. Ehhez a kalkulált measure-ök
     * értékét meg kell határozni, majd a valós és kalkulált measure-ok sorrendjét a meta-ban (Measures osztály)
     * meghatározott sorrendbe kell rendezni és végezetül a double értékeket vesszővel szeparált String értékekre kell
     * alakítani.
     *
     * @param rawValues valós measure-ök tömbje
     * @return lowerIndexes megkonstruált szting tömb, amelyben minden measure megfelelő sorrendben szerepel.
     */
    double[] getAllMeasureAsString(double[] rawValues) {
        List<AbstractMeasure> measures = cube.getMeasures();
        int measureCnt = measures.size();
        double[] result = new double[measureCnt];

        for (int i = 0; i < measureCnt; i++) {
            AbstractMeasure measure = measures.get(i);

            if (measure.isCalculated()) {
                String calculatedFormula = ((CalculatedMeasure) measure).getFormula();
                String[] formulaWithIndex = replaceMeasureNameWithIndex(calculatedFormula);
                double d = PostfixCalculator.calculate(formulaWithIndex, rawValues);
                result[i] = d;
            } else {
                String memberUniqueName = measure.getName();
                int idx = cube.getRealMeasureIdxByName(memberUniqueName);
                result[i] = rawValues[idx];
            }
        }
        return result;
    }

    /**
     * Ez az eljárás lowerIndexes Calculated formulában lévő measure neveket lecseréli azok Cells -béli oszlopindexére
     *
     * @param calculatedFormula az átalakítandó formula
     * @return ez eredeti formulanak egy olyan változata, amely split-elve van szőközönként és lowerIndexes measure
     *         nevek helyett azok indexei található
     * @throws NumberFormatException ha valami rosszul van formázva
     */
    private String[] replaceMeasureNameWithIndex(String calculatedFormula) {
        String[] calculatedFormulaSegments = calculatedFormula.split(" ");
        String[] result = new String[calculatedFormulaSegments.length];
        for (int i = 0; i < calculatedFormulaSegments.length; i++) {
            if (PostfixCalculator.isOperator(calculatedFormulaSegments[i])) {
                result[i] = calculatedFormulaSegments[i];
            } else {
                int idx = cube.getRealMeasureIdxByName(calculatedFormulaSegments[i]);
                if (idx == -1) {
                    double d = Double.parseDouble(calculatedFormulaSegments[i]);
                    result[i] = Double.toString(d);
                } else {
                    result[i] = Integer.toString(idx);
                }
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "Problem{" + "baseVector=" + baseVector + "}";
    }

}
