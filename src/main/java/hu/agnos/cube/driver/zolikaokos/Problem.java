package hu.agnos.cube.driver.zolikaokos;

import java.util.ArrayList;
import java.util.List;

import hu.agnos.cube.driver.util.PostfixCalculator;
import hu.agnos.cube.Cube;
import hu.agnos.cube.dimension.Dimension;
import hu.agnos.cube.dimension.Node;
import hu.agnos.cube.measure.AbstractMeasure;
import hu.agnos.cube.measure.CalculatedMeasure;
import hu.agnos.cube.meta.resultDto.NodeDTO;
import hu.agnos.cube.meta.resultDto.ResultElement;

/**
 *
 * @author ruzsaz
 */
public class Problem {

    private final Cube cube;
    protected int[][] Oa, Ob, a, b;
    private final int drillVectorId;
    private Node[] header;
    private final List<Node> baseVector;

    public Problem(Cube cube, int drillVectorId, List<Node> baseVector) {
        this.cube = cube;
        this.drillVectorId = drillVectorId;
        this.baseVector = baseVector;
    }

    public ResultElement compute() {
        uploadIntervalAndHeader(cube.getDimensions());
        double[] calculatedValues = Algorithms.calculateSumNyuszival2(Oa, Ob, a, b, cube.getCells().getCells());
        double[] measureValues = getAllMeasureAsString(calculatedValues, cube);
        return new ResultElement(translateNodes(header), measureValues, drillVectorId);
    }

    private static NodeDTO[] translateNodes(Node[] nodes) {
        int nodeNumber = nodes.length;
        NodeDTO[] result = new NodeDTO[nodeNumber];
        for (int i = 0; i < nodeNumber; i++) {
            result[i] = translateNode(nodes[i]);
        }
        return result;
    }

    private static NodeDTO translateNode(Node n) {
        return new NodeDTO(n.getCode(), n.getName());
    }

    /**
     * Ez az eljárás feltölti az intervellum rendszereket, továbbá a header részt is kitölti.
     * Mivel az intervallumok feltöltéséhez a Nodokat ki kell keresni, 
     * így célszerű ebben a lépésben a headert is kitölteni (különben újból ki kell keresni a nodot).
     * @param dimensions a kockában lévő dimenziók listája
     */
    private void uploadIntervalAndHeader(List<Dimension> dimensions) {
        int hierarchySize = dimensions.size();
        this.header = new Node[hierarchySize];

        List< int[]> OaList = new ArrayList<>();
        List< int[]> ObList = new ArrayList<>();
        List< int[]> aList = new ArrayList<>();
        List< int[]> bList = new ArrayList<>();

        for (int i = 0; i < hierarchySize; i++) {
            Dimension dimension = dimensions.get(i);
            Node n = baseVector.get(i);

            this.header[i] = baseVector.get(i);
            if (dimension.isOfflineCalculated()) {
                OaList.add(n.getIntervalsLowerIndexes());
                ObList.add(n.getIntervalsUpperIndexes());
            } else {
                aList.add(n.getIntervalsLowerIndexes());
                bList.add(n.getIntervalsUpperIndexes());
            }
        }

        int OaSize = OaList.size();
        Oa = new int[OaSize][];
        for (int i = 0; i < OaSize; i++) {
            Oa[i] = OaList.get(i);
        }

        int ObSize = ObList.size();
        Ob = new int[ObSize][];
        for (int i = 0; i < ObSize; i++) {
            Ob[i] = ObList.get(i);
        }

        int aSize = aList.size();
        a = new int[aSize][];
        for (int i = 0; i < aSize; i++) {
            a[i] = aList.get(i);            
        }
        
        int bSize = aList.size();
        b = new int[bSize][];
        for (int i = 0; i < bSize; i++) {
            b[i] = bList.get(i);
        }

    }

    /**
     * Ez az érték a megkapott valós measure értékeket a kívánt formátumra
     * alakítja. Ehhez a kalkulált measure-ök értékét meg kell határozni, majd
     * a valós és kalkulált measure-ok sorrendjét a meta-ban (Measures osztály)
     * meghatározott sorrendbe kell rendezni és végezetül a double értékeket
     * vesszővel szeparált String értékekre kell alakítani.
     *
     * @param rawValues valós measure-ök tömbje
     * @return a megkonstruált szting tömb, amelyben minden measure megfelelő
     * sorrendben szerepel.
     */
    private double[] getAllMeasureAsString(double[] rawValues, Cube cube) {
        List<AbstractMeasure> measures = cube.getMeasures();
        int measureCnt = measures.size();
        double[] result = new double[measureCnt];

        for (int i = 0; i < measureCnt; i++) {
            AbstractMeasure member = measures.get(i);

            if (member.isCalculatedMember()) {
                String calculatedFormula = ((CalculatedMeasure) member).getFormula();
                String[] formulaWithIndex = replaceMeasureNameWithIndex(calculatedFormula, cube);
                double d = PostfixCalculator.calculate(formulaWithIndex, rawValues);
                result[i] = d;
            } else {
                String memberUniqueName = member.getName();
                int idx = cube.getRealMeasureIdxByName(memberUniqueName);
                result[i] = rawValues[idx];
            }
        }
        return result;
    }

    /**
     * Ez az eljárás a Calculated formulában lévő measure neveket lecseréli azok
     * Cells -béli oszlopindexére
     *
     * @param calculatedFormula az átalakítandó formula
     * @return ez eredeti formulanak egy olyan változata, amely split-elve van
     * szőközönként és a measure nevek helyett azok indexei található
     * @throws NumberFormatException ha valami rosszul van formázva
     */
    private String[] replaceMeasureNameWithIndex(String calculatedFormula, Cube cube) throws NumberFormatException {
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
        return "Problem{" + "baseVector=" + baseVector + '}';
    }

}
