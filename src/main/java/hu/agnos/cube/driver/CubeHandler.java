/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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

    
//    private final Cells cells;
//    private final Cube cube;
//    
    /**
     * A cube-on hierarchiák egyedi nevét tartalmazó tömb, a sorrend kötött.
     */
    private final String[] hierarchyHeader;
//    
//    /**
//     * A cube-on belüli hiearachia sorszámához (amely a hierarchyHeader-ből tudható meg),
//     * megmondja, hogy az hányadik dimenzió, hányadik hierarchia eleme. Ez egy
//     * mátrix, amelynek fejléce az alábbi: sorszáma, dimenzióIdx, hierarchiaIdx
//     */
//    private final int[][] hierarchyIndex;
//    
    /**
     * A cube-ban szereplő measure-ök (a kalkuláltaké is) neveit tartalmazó tömb
     */
    private final String[] measureHeader;
//    
//    private final List<Dimension> dimensions;
//    
    private final int hierarchySize;
//    
//    private final Measures measures;
//

    public CubeHandler(String[] hierarchyHeader, String[] measureHeader) {
//        this.cells = cube.getCells();
        this.hierarchyHeader = hierarchyHeader;
        this.measureHeader = measureHeader;
        this.hierarchySize = this.hierarchyHeader.length;
//        this.dimensions = cube.getDimensions();
//        this.measures = cube.getMeasures();
//        this.hierarchyIndex = cube.getHierarchyIndex();
//        this.cube = cube;
    }
//    
//    

    public void printDims(Cube cube) {
        for (Dimension dim : cube.getDimensions()) {
            for (Hierarchy hier : dim.getHierarchies()) {
                System.out.println("Hier name : " + hier.getHierarchyUniqueName());

                Node[][] nodes = hier.getNodes();

                for (int i = 0; i < nodes.length; i++) {
                    for (int j = 0; j < nodes[i].length; j++) {
                        System.out.println("" + i + "," + j + ": " + nodes[i][j]);
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
            for (int i = 0; i < cArray.length; i++) {

                System.out.print("\t" + cArray[i][j]);
            }
            System.out.println("");
        }
    }

//
//    public int[][] getHierarchyIndex() {
//        return hierarchyIndex;
//    }
//
    public int getHierarchySize() {
        return hierarchySize;
    }
//
//    
//    
//    

    public int getHierarchyIndexByUniqueName(String uniqueName) {
        for (int i = 0; i < hierarchyHeader.length; i++) {
            if (hierarchyHeader[i].equals(uniqueName)) {
                return i;
            }
        }
        return -1;
    }
//
//    /**
//     * Ez az eljárás egy konkrét baseVector-hoz tartozó értéket ad vissza
//     *
//     * @param baseVector
//     * @return
//     */
//    public String[][] selectOneRow(String baseVector) {
//        
//        //ez egy 2 soros tömb lesz,
//        //  az első sorban a header
//        //  a második sorban a measure-ök
//        String[][] result = new String[2][];
//
//        String[] baseVectorArray = baseVector.split(":", -1);
//
////        System.out.println("baseVectorArray: " + baseVectorArray.length);
//
//       
//        String[] hierarchyHeaderResult = new String[this.hierarchySize];
//
//        HashMap<Integer, int[]> OaList = new HashMap<>();
//        HashMap<Integer, int[]> ObList = new HashMap<>();
//        HashMap<Integer, int[]> aList = new HashMap<>();
//        HashMap<Integer, int[]> bList = new HashMap<>();
//
//        for (int i = 0; i < this.hierarchySize; i++) {
//            
//            int dimIdx = hierarchyIndex[i][0];
//            int hierIdx = hierarchyIndex[i][1];
//
//            Dimension dimension = this.dimensions.get(dimIdx);
//            Hierarchy hierarchy = dimension.getHierarchyById(hierIdx);
//            Node member = null;
//            if (baseVectorArray[i] != null && !baseVectorArray[i].isEmpty()) {
//                
//                String[] splitedNodePath = baseVectorArray[i].split(",");
//
//                int depth = splitedNodePath.length;               
//                
//                int huntedNodeId = Integer.parseInt(splitedNodePath[depth - 1]);               
//                
//                //base level szinten vagyunk?
//                if (depth == hierarchy.getMaxDepth() ) {                    
//                    member =  dimension.getBaseLevelNodes()[huntedNodeId];
//                } else {
//                    member = hierarchy.getNode(depth, huntedNodeId);
//                }
//
//            } else {
//                member = hierarchy.getRoot();
//            }
//            hierarchyHeaderResult[i] = member.getDataAsString();
//
//            if (hierarchy.isPartitioned()) {
//                OaList.put(i, member.getIntervalsLowerIndexes());
//                ObList.put(i, member.getIntervalsUpperIndexes());
//            } else {
//                aList.put(i, member.getIntervalsLowerIndexes());
//                bList.put(i, member.getIntervalsUpperIndexes());
//            }
//
//        }
//
//        result[0] = hierarchyHeaderResult;
//
//        int[][] Oa = new int[OaList.size()][];
//
//        for (Integer i : OaList.keySet()) {
//            Oa[i] = OaList.get(i);
//        }
//
//        int[][] Ob = new int[ObList.size()][];
//        for (Integer i : ObList.keySet()) {
//            Ob[i] = ObList.get(i);
//        }
//
//        int[][] a = new int[aList.size()][];
//        int idx = 0;
//        for (Integer i : aList.keySet()) {
//            a[idx] = aList.get(i);
//            idx++;
//        }
//        idx = 0;
//        int[][] b = new int[bList.size()][];
//        for (Integer i : bList.keySet()) {
//            b[idx] = bList.get(i);
//            idx++;
//        }
//
//       
//        DataRetriever retriever = new DataRetriever(cube.getCells().getCells());
//        
//        System.out.println("Oa.length: " + Oa.length);
//        System.out.println("Ob.length: " + Ob.length);
//        System.out.println("a.length: " + a.length);
//        System.out.println("b.length: " + b.length);
//        
//        retriever.addProblem(new Problem(Oa, Ob, a, b));
//        
//        List<Future<Result>> futures = retriever.computeAll();
//        
//          
//        try {
//          
//            double[] measureValues = futures.get(0).get().getResult();
//         
//            result[1] = getResultAsString(measureValues);
//        } catch (InterruptedException | ExecutionException ex) {
//            Logger.getLogger(CubeHandler.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        return result;
//    }
//
//    /**
//     * Ez az érték a megkapott valós measure értékeket a kívánt formátumra alakítja.
//     * Ehhez a kalkulált measure-ök értékét meg kell határozni, 
//     * majd az valós és kalkulált measure-ok sorrendjét a meta-ban (Measures osztály) meghatározott sorrendbe kell rendezni
//     * és végezetül a double értékekel vesszővel szeparált String értékekre kell alakítani. 
//     * @param measureValues valós measure-ök tömbje
//     * @return a megkonstruált szting, amelyben minden measure megfelelő sorrendben, vesszővel szeparálva szerepel.
//     */
//    private String[] getResultAsString(double[] measureValues) {
//
//        int measureCnt = measures.getMeasures().size();
//        String[] result = new String[measureCnt];
//
//        int[] calculatedMeasureFlags = new int[measureCnt];
//        for (int i = 0; i < measureCnt; i++) {
//            AbstractMeasure member = this.measures.getMember(i);
//
//            if (member.isCalculatedMember()) {
//                String calculatedFormula = ((CalculatedMeasure) member).getFormula();
//                String[] formulaWithIndex = replaceMeasurUniqeNameToIndex(calculatedFormula);
//                PostfixCalculator calculator = new PostfixCalculator();
//                Double d = calculator.calculate(formulaWithIndex, measureValues);
//                result[i] = d.toString();
//            } else {
//                String memberUniqeName = member.getName();
//                int idx = measures.getRealMeasureIdxByUniquName(memberUniqeName);
//                result[i] = Double.toString(measureValues[idx]);
//            }
//        }
//        return result;
//    }
//
//    
//    /**
//     * Ez az eljárás a Calculated formulában lévő measur neveket lecseréli azok Cells -béli oszlopindexére
//     * @param calculatedFormula az átalakítandó formula
//     * @return ez eredeti formulanak egy olyan változata, amely split-elve van szőközönként és
//     *          a measure nevek helyett azok indexei található
//     * @throws NumberFormatException 
//     */
//    private String[] replaceMeasurUniqeNameToIndex(String calculatedFormula) throws NumberFormatException {
//        String[] calculatedFormulaSegments = calculatedFormula.split(" ");
//        String[] result = new String[calculatedFormulaSegments.length];
//        for (int i = 0; i < calculatedFormulaSegments.length; i++) {
//            if (calculatedFormulaSegments[i].equals(PostfixCalculator.ADD) || calculatedFormulaSegments[i].equals(PostfixCalculator.SUB) || calculatedFormulaSegments[i].equals(PostfixCalculator.MUL) || calculatedFormulaSegments[i].equals(PostfixCalculator.DIV)) {
//                result[i] = calculatedFormulaSegments[i];
//            } else {
//                int idx = measures.getRealMeasureIdxByUniquName(calculatedFormulaSegments[i]);
//                if (idx == -1) {
//                    Double d = Double.parseDouble(calculatedFormulaSegments[i]);
//                    result[i] = d.toString();
//                } else {
//                    result[i] = Integer.toString(idx);
//                }
//            }
//        }
//        return result;
//    }
//
//    public List<Dimension> getDimensions() {
//        return dimensions;
//    }
//

    /**
     * Egy measure (akár kalkulált is) Measures-béli indexét adja meg
     *
     * @param measureName a keresett measure neve
     * @return a measure Measures-béli indexe, vagy ha az nem található meg,
     * akkor -1
     */
    public int getMeasureIndexByUniqueName(String measureName) {
        for (int i = 0; i < this.measureHeader.length; i++) {
            if (this.measureHeader[i].toUpperCase().equals(measureName.toUpperCase())) {
                return i;
            }
        }
        return -1;
    }
//    
}
