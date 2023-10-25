/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.agnos.cube.driver;


import hu.agnos.molap.dimension.Dimension;
import hu.agnos.molap.dimension.Node;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author parisek
 */
public class NativeQueryGenerator {

    private List<Dimension> dimensions;
    private int[][] hierarchyInfo;

    public NativeQueryGenerator() {
    }

    public NativeQueryGenerator(List<Dimension> dimensions) {
        this.dimensions = dimensions;
    }

    public NativeQueryGenerator(List<Dimension> dimensions, int[][] hierarchyInfo) {
        this.dimensions = dimensions;
        this.hierarchyInfo = hierarchyInfo;
    }

    public void setDimensions(List<Dimension> dimensions) {
        this.dimensions = dimensions;
    }

    public void setHierarchyInfo(int[][] hierarchyInfo) {
        this.hierarchyInfo = hierarchyInfo;
    }

    private List<StringBuilder> cartesianProduct(List<StringBuilder> list1, List<StringBuilder> list2) {
        List<StringBuilder> result = new ArrayList<>();
        for (StringBuilder sb1 : list1) {
            for (StringBuilder sb2 : list2) {
                StringBuilder newStringBuilder = new StringBuilder(sb1.toString());
                newStringBuilder.append(":").append(sb2.toString());
                result.add(newStringBuilder);
            }
        }
        return result;
    }

    public String[] getBaseVectorsFromDrillVector(String[] baseVectorAsKnownIds, String[] baseVector, String[] drillVector) {
        String[] result = null;
        int drillVectorLength = drillVector.length;

        //ebben a tömben annyi lista lesz, ahány eleme van a drillVectornak
        List<StringBuilder>[] childrenList = new ArrayList[drillVectorLength];

        for (int i = 0; i < drillVectorLength; i++) {
            List<StringBuilder> children = new ArrayList<>();
            String oldPath = baseVector[i];
            StringBuilder tempPath = new StringBuilder();


            int dimIdx = this.hierarchyInfo[i][0];
            int hierarchyIdx = this.hierarchyInfo[i][1];
            Dimension dimension = (Dimension) this.dimensions.get(dimIdx);


            
            //ha van lefúrás
            if (!drillVector[i].equals("0")) {

                if (dimension != null) {
                    if (oldPath != null) {
                        Node node = dimension.getNode(hierarchyIdx, oldPath);
                        if (node.isLeaf()) {
                            tempPath.append(oldPath);
                            children.add(tempPath);

                        //ha nem levélelemről van szó, akkor lefúrunk a gyerekekre
                        } else {
                            int[] childrenId = node.getChildrenId();

                            if (!oldPath.isEmpty()) {
                                tempPath = tempPath.append(oldPath).append(",");
                            }
                            for (int childId : childrenId) {
                                StringBuilder sb = new StringBuilder(tempPath.toString());
                                sb.append(childId);
                                children.add(sb);
                            }
                        }
                    }
                }

            } //ha nem fúrtunk bele az adott dimenzióba
            else {
                //a régi path-t átmásoljuk
                tempPath.append(oldPath);
                children.add(tempPath);

            }
            childrenList[i] = children;

            // Requested elements' depth in the i.th dimension
            int requestedDepth = ((Objects.equals(baseVectorAsKnownIds[i], "")) ? 0 : (baseVectorAsKnownIds[i].split(",").length)) + (drillVector[i].equals("0") ? 0 : 1);
            int availableDepth = dimension.getHierarchies()[hierarchyIdx].getMaxDepth();
            int reachedDepth = (children.isEmpty() || children.get(0).isEmpty()) ? 0 : children.get(0).toString().split(",").length;

            System.out.println("REQ: " + requestedDepth + " RES: " + reachedDepth + " AVA: " + availableDepth);

        }
        List<StringBuilder> list1 = childrenList[0];
        for (int i = 1; i < childrenList.length; i++) {
            list1 = this.cartesianProduct(list1, childrenList[i]);
        }
        int list1Size = list1.size();
        if (list1Size > 0) {
            result = new String[list1Size];
            for (int i = 0; i < list1Size; i++) {
                result[i] = list1.get(i).toString();
                System.out.println("RESULT " + i + ": " + result[i]);
            }
        }
        return result;
    }


    public String[] convertIdBaseVectorFromKnowIdBaseVector(String[] baseVectorAsKnownIds) {
        int baseVectorLength = baseVectorAsKnownIds.length;
        String[] result = new String[baseVectorLength];

        for (int i = 0; i < baseVectorLength; i++) {
            int dimIdx = this.hierarchyInfo[i][0];
            int hierarchyIdx = this.hierarchyInfo[i][1];
            Dimension dimension = this.dimensions.get(dimIdx);

            String[] baseVectorPieces = baseVectorAsKnownIds[i].split(",");

            int baseVectorPieceLength = baseVectorPieces.length;
            int dimensionDepth = dimension.getHierarchyById(hierarchyIdx).getMaxDepth();
            System.out.println(dimension.getHierarchyById(hierarchyIdx).getHierarchyUniqueName() + ": depth:" + dimensionDepth + " basevectorLegth: " + baseVectorPieceLength);


            String[] newIds = new String[baseVectorPieces.length];
            for (int j = 0; j < baseVectorPieces.length; j++) {
                String partial = String.join(",", Arrays.copyOf(baseVectorPieces, j + 1));
                if (partial.equals("")) { // @Top level
                    newIds[j] = "";
                } else {
                    Node n = dimension.getNodeByKnowIdPath(hierarchyIdx, partial);
                    if (n != null) {
                        newIds[j] = n.getId().toString();
                    } else {
                        newIds[j] = "";
                    }
                }
            }
            result[i] = String.join(",", newIds);
        }
        System.out.println(String.join(", ",baseVectorAsKnownIds));
        System.out.println(String.join(", ", result));

        return result;
    }

}
