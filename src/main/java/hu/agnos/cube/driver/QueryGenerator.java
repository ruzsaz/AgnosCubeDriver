package hu.agnos.cube.driver;

import hu.agnos.cube.dimension.Dimension;
import hu.agnos.cube.dimension.Node;
import hu.agnos.cube.driver.util.SetFunctions;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author parisek
 */
public class QueryGenerator {

    /**
     * Calculates all the dimension element combinations required to get.
     *
     * @param dimensions Dimensions of the cube
     * @param baseNodeCodes Base drill coordinates, like ["2016,06", ""]
     * @param drillVector 0-1 vector, where 1 shows a drill is required in the given coordinate
     * @return All the required [node, node, ... node] coordinate values
     */
    public static List<List<Node>> getCoordinatesOfDrill(List<Dimension> dimensions, String[] baseNodeCodes, String[] drillVector) {
        int drillVectorLength = drillVector.length;

        // For each dimension get the required nodes (the base node, or the children, if drill is required).
        List<List<Node>> childrenList = new ArrayList<>();
        for (int i = 0; i < drillVectorLength; i++) {
            Node baseNode = dimensions.get(i).getNodeByKnownIdPath(baseNodeCodes[i]);

            if (drillVector[i].equals("0")) {
                childrenList.add(List.of(baseNode));
            } else {
                childrenList.add(List.of(dimensions.get(i).getChildrenOf(baseNode)));
            }
        }

        List<List<Node>> result = SetFunctions.cartesianProductFromList(childrenList);

        // Only for test
        for (List<Node> nodes : result) {
            System.out.println(nodes.stream().map(Node::getCode).collect(Collectors.joining(":")));
        }

        return result;
    }
}
