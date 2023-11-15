package hu.agnos.cube.driver.zolikaokos;

import gnu.trove.list.array.TIntArrayList;
import hu.agnos.cube.dimension.Node;
import java.util.Arrays;

/**
 *
 * @author ruzsaz
 */
public class CountDistinctAggregateAlgorithms {

    private static boolean ujabbFv(TIntArrayList lowerIndexes, TIntArrayList upperIndexes, int value) {

        int index = lowerIndexes.binarySearch(value);
        if (index > 0) {
            return true;
        }
        return upperIndexes.getQuick(-index - 2) >= value;

    }

    private static boolean isMatch(TIntArrayList lowerIndexes, TIntArrayList upperIndexes, Node node) {

        int[] nodeLowerIndexes = node.getIntervalsLowerIndexes();
        int[] nodeUpperIndexes = node.getIntervalsUpperIndexes();
        for (int i = 0; i < nodeLowerIndexes.length; i++) {
            for (int j = nodeLowerIndexes[i]; j <= nodeUpperIndexes[i]; j++) {
                if (ujabbFv(lowerIndexes, upperIndexes, j)) {
                    return true;
                }
            }

        }
        return false;
    }

    public static int getContainedSumKecske(TIntArrayList lowerIndexes, TIntArrayList upperIndexes, Node[] cntDistNodes) {
        int result = 0;
        int nodeLength = cntDistNodes.length;
        for (int i = 0; i < nodeLength; i++) {
            if (isMatch(lowerIndexes, upperIndexes, cntDistNodes[i])) {
                result++;
            }
        }
        return result;
    }

}
