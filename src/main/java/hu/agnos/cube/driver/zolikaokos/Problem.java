package hu.agnos.cube.driver.zolikaokos;

import java.util.ArrayList;
import java.util.List;

import hu.agnos.cube.driver.util.PostfixCalculator;
import hu.agnos.molap.Cube;
import hu.agnos.molap.dimension.Dimension;
import hu.agnos.molap.dimension.Hierarchy;
import hu.agnos.molap.dimension.Node;
import hu.agnos.molap.measure.AbstractMeasure;
import hu.agnos.molap.measure.CalculatedMeasure;
import hu.agnos.molap.measure.Measures;

/**
 *
 * @author ruzsaz
 */
public class Problem {

    public int[][] Oa, Ob, a, b;
    private final int drillVectorId;
    private String[] header;
    private final String baseVector;

    public Problem(int drillVectorId, String baseVector) {
        this.drillVectorId = drillVectorId;
        this.baseVector = baseVector;
    }

    public ResultElement compute(Cube cube) throws InterruptedException {
        uploadIntervalAndHeader(cube.getDimensions(), cube.getHierarchyHeader().length, cube.getHierarchyIndex());
        Algorithms algorithms = new Algorithms();

        double[] calculatedValues = algorithms.calculateSumNyuszival2(Oa, Ob, a, b, cube.getCells().getCells());
        double[] measureValues = getAllMeasureAsString(calculatedValues, cube.getMeasures());
        return new ResultElement(header, measureValues, drillVectorId);
    }

    /**
     * Ez az eljárás feltölti az intervellum rendszereket, továbbá a header részt is kitölti.
     * Mivel az intervallumok feltöltéséhez a Nodokat ki kell keresni, 
     * így célszerű ebben a lépésben a headert is kitölteni (különben újból ki kell keresni a nodot).
     * @param dimensions a kockában lévő dimenziók listája
     * @param hierarchySize a kockában lévő hierarchiák száma
     * @param hierarchyIndex egy olyan mátrix amelyben az első dimenzió elemszáma megegyezik a hierarchiák számával
     *      a második dimenzió pedig megmondja, hogy az adott sorszámú hierarchia a kocka melyik dimenziójába, 
     *      és dimenzión belül melyik hierarchiába esik.
     */
    private void uploadIntervalAndHeader(List<Dimension> dimensions, int hierarchySize, int[][] hierarchyIndex) {
//        System.out.println("baseVector: " +baseVector);
        String[] baseVectorArray = baseVector.split(":", -1);

//        System.out.println("baseVectorArray: " + baseVectorArray.length);
        this.header = new String[hierarchySize];

        List< int[]> OaList = new ArrayList<>();
        List< int[]> ObList = new ArrayList<>();
        List< int[]> aList = new ArrayList<>();
        List< int[]> bList = new ArrayList<>();

        for (int i = 0; i < hierarchySize; i++) {

            int dimIdx = hierarchyIndex[i][0];
            int hierIdx = hierarchyIndex[i][1];

            Dimension dimension = dimensions.get(dimIdx);
            Hierarchy hierarchy = dimension.getHierarchyById(hierIdx);
            Node hierarchyNode = null;
            if (baseVectorArray[i] != null && !baseVectorArray[i].isEmpty()) {
//                System.out.print("baseVectorArray[i] != null:  ");
                String[] splitedNodePath = baseVectorArray[i].split(",");

                int depth = splitedNodePath.length;

                int huntedNodeId = Integer.parseInt(splitedNodePath[depth - 1]);

                //base level szinten vagyunk?
                if (depth == hierarchy.getMaxDepth()) {
                    hierarchyNode = dimension.getBaseLevelNodes()[huntedNodeId];
                } else {
                    hierarchyNode = hierarchy.getNode(depth, huntedNodeId);
                }

            } else {
//                System.out.print("baseVectorArray[i] == null:  ");
                hierarchyNode = hierarchy.getRoot();
            }
//            System.out.println(hierarchyNode.getDataAsString());
            this.header[i] = hierarchyNode.getDataAsString();

            if (hierarchy.isPartitioned()) {
                OaList.add(hierarchyNode.getIntervalsLowerIndexes());
                ObList.add(hierarchyNode.getIntervalsUpperIndexes());
            } else {
                aList.add(hierarchyNode.getIntervalsLowerIndexes());
                bList.add(hierarchyNode.getIntervalsUpperIndexes());
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
     * @param measureValues valós measure-ök tömbje
     * @return a megkonstruált szting tömb, amelyben minden measure megfelelő
     * sorrendben szerepel.
     */
    private double[] getAllMeasureAsString(double[] measureValues, Measures measures) {

        int measureCnt = measures.getMeasures().size();
        double[] result = new double[measureCnt];

        for (int i = 0; i < measureCnt; i++) {
            AbstractMeasure member = measures.getMember(i);

            if (member.isCalculatedMember()) {
                String calculatedFormula = ((CalculatedMeasure) member).getFormula();
                String[] formulaWithIndex = replaceMeasurUniqeNameToIndex(calculatedFormula, measures);
                PostfixCalculator calculator = new PostfixCalculator();
                double d = calculator.calculate(formulaWithIndex, measureValues);
                result[i] = d;
            } else {
                String memberUniqueName = member.getName();
                int idx = measures.getRealMeasureIdxByUniquName(memberUniqueName);
                result[i] = measureValues[idx];
            }
        }
        return result;
    }

    /**
     * Ez az eljárás a Calculated formulában lévő measur neveket lecseréli azok
     * Cells -béli oszlopindexére
     *
     * @param calculatedFormula az átalakítandó formula
     * @return ez eredeti formulanak egy olyan változata, amely split-elve van
     * szőközönként és a measure nevek helyett azok indexei található
     * @throws NumberFormatException ha valami rosszul van formázva
     */
    private String[] replaceMeasurUniqeNameToIndex(String calculatedFormula, Measures measures) throws NumberFormatException {
        String[] calculatedFormulaSegments = calculatedFormula.split(" ");
        String[] result = new String[calculatedFormulaSegments.length];
        PostfixCalculator calculator = new PostfixCalculator();
        for (int i = 0; i < calculatedFormulaSegments.length; i++) {
            if ( calculator.isOperator(calculatedFormulaSegments[i]) ) {
                result[i] = calculatedFormulaSegments[i];
            } else {
                int idx = measures.getRealMeasureIdxByUniquName(calculatedFormulaSegments[i]);
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
