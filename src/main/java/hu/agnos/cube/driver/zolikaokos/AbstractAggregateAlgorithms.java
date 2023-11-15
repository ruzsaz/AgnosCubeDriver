package hu.agnos.cube.driver.zolikaokos;

import gnu.trove.list.array.TIntArrayList;
import hu.agnos.cube.Cube;
import hu.agnos.cube.dimension.Node;
import java.util.Arrays;

/**
 *
 * @author ruzsaz
 */
public class AbstractAggregateAlgorithms {

    /**
     * Kikeres egy értéket a rendezett tömbből. Ha nincs benne, akkor a nálánál
     * nagyobbak minimumát keresi ki.
     *
     * @param array Rendezett tömb.
     * @param key Keresett elem.
     * @return A megtalált elem indexe.
     */
    public static int getThisOrBiggerIndex(int[] array, int key) {
        int index = Arrays.binarySearch(array, key);
        return (index < 0) ? -index - 1 : index;
    }

    /**
     * Kikeres egy értéket a rendezett tömbből. Ha nincs benne, akkor a nálánál
     * nagyobbak minimumát keresi ki.
     *
     * @param array Rendezett tömb.
     * @param fromIndex A keresés kezdőindexe (zárt intervallum).
     * @param toIndex A keresés végindexe (zárt intervallum).
     * @param key Keresett elem.
     * @return A megtalált elem indexe.
     */
    public static int getThisOrBiggerIndex(int[] array, int fromIndex, int toIndex, int key) {
        int index = Arrays.binarySearch(array, fromIndex, toIndex + 1, key);
        return (index < 0) ? -index - 1 : index;
    }

    /**
     * Kikeres egy értéket a rendezett tömbből. Ha nincs benne, akkor a nálánál
     * kisebbek maximumát keresi ki.
     *
     * @param array Rendezett tömb.
     * @param key Keresett elem.
     * @return A megtalált elem indexe.
     */
    public static int getThisOrSmallerIndex(int[] array, int key) {
        int index = Arrays.binarySearch(array, key);
        return (index < 0) ? -index - 2 : index;
    }

    /**
     * Kikeres egy értéket a rendezett tömbből. Ha nincs benne, akkor a nálánál
     * kisebbek maximumát keresi ki.
     *
     * @param array Rendezett tömb.
     * @param fromIndex A keresés kezdőindexe (zárt intervallum).
     * @param toIndex A keresés végindexe (zárt intervallum).
     * @param key Keresett elem.
     * @return A megtalált elem indexe.
     */
    public static int getThisOrSmallerIndex(int[] array, int fromIndex, int toIndex, int key) {
        int index = Arrays.binarySearch(array, fromIndex, toIndex + 1, key);
        return (index < 0) ? -index - 2 : index;
    }

    /**
     * Monoton csökkenően egymásba ágyazott intervallumrendszer metszetét adja
     * meg. Egy szint intervallumában a következő szintben csak egy intervallum
     * lehet!
     *
     * @param lowerIndexes A bal végpontok rendezett tömbje, szintenként tömbbe
     * rendezve.
     * @param upperIndexes A jobb végpontok rendezett tömbje, szintenként tömbbe
     * rendezve.
     * @param defaultMin Ha nincs mivel elmetszeni, akkor a felvevendő minimum.
     * @param defaultMax Ha nincs mivel elmetszeni, akkor a felvevendő maximum.
     * @return A metszetintervallum [bal, jobb] végpontja.
     */
    public static int[] monotonicIntersection(int[][] lowerIndexes, int[][] upperIndexes, int defaultMin, int defaultMax) {
        if (lowerIndexes == null || lowerIndexes.length == 0) {
            return new int[]{defaultMin, defaultMax}; // Ha nincs mit elmetszeni
        }

        int min = lowerIndexes[0][0];
        int max = upperIndexes[0][0];
        for (int d = 0; d < lowerIndexes.length; d++) {
            int[] currentLower = lowerIndexes[d];
            int[] currentUpper = upperIndexes[d];
            int index = getThisOrBiggerIndex(currentUpper, min);
            if (index < currentLower.length && currentLower[index] <= max) {
                min = Math.max(currentLower[index], min);
                max = Math.min(currentUpper[index], max);
            } else {
                return new int[]{0, -1}; // Üreshalmaz
            }
        }
        return new int[]{min, max};
    }

    /**
     * Egész számokból álló zárt intervallumrendszerek metszetét határozza meg.
     *
     * @param min A meghatározandó számok minimuma. Ennél kisebb szám nem lesz
     * az eredményben.
     * @param max A meghatározandó számok maximuma. Ennél nagyobb szám nem lesz
     * az eredményben.
     * @param lowerIndexes A bal végpontok rendezett tömbje, szintenként tömbbe
     * rendezve.
     * @param upperIndexes A jobb végpontok rendezett tömbje, szintenként tömbbe
     * rendezve.
     * @param minIndex Ettől az idenxtől kezdve (ezt is beleértve) kell
     * figyelembevenni a az intervallumokat. Szintenként tömbbe rendezve.
     * @param maxIndex Eddig az idenxig kezdve (ezt is beleértve) kell
     * figyelembevenni a az intervallumokat. Szintenként tömbbe rendezve.
     * @return A metszet intervallumrendszer (zárt intervallumok).
     */
    public static TIntArrayList[] intersection(int min, int max, int[][] lowerIndexes, int[][] upperIndexes, int[] minIndex, int[] maxIndex) {
        TIntArrayList Rlower = new TIntArrayList();
        TIntArrayList Rupper = new TIntArrayList();
        int depth = lowerIndexes.length;
        int[] index = Arrays.copyOf(minIndex, depth);

        for (int d = 0; d < depth; d++) {
            if (lowerIndexes[d].length == 0) {
                return new TIntArrayList[]{Rlower, Rupper};
            }
        }

        while (true) {
            int maxlower = min, minUpper = max, minUpperPos = -1;

            for (int d = 0; d < depth; d++) {
                int thisLower = lowerIndexes[d][index[d]];
                int thisUpper = upperIndexes[d][index[d]];
                if (thisLower > maxlower) {
                    maxlower = thisLower;
                }
                if (thisUpper < minUpper) {
                    minUpper = thisUpper;
                    minUpperPos = d;
                }
            }
            if (maxlower <= minUpper) {
                Rlower.add(maxlower);
                Rupper.add(minUpper);
            }

            if (minUpperPos > -1) {
                index[minUpperPos]++;
                if (index[minUpperPos] > maxIndex[minUpperPos]) {
                    break;
                }
            } else {
                break;
            }
        }
        return new TIntArrayList[]{Rlower, Rupper};
    }

    /**
     * Elmetsz egy intervallumrendszert egy intervallummal. Az eredmény az a
     * minimális és maximális index, amely indexűek belelógnak a metszetbe. (Az
     * eredmény legkisebb és legnagyobb indexű tagja TÚLNYÚLHAT a metsző
     * intervallumon, lefelé illetve felfelé.)
     *
     * @param lowerIndexes A bal végpontok rendezett tömbje.
     * @param upperIndexes A jobb végpontok rendezett tömbje.
     * @param min A metsző intervallum bal végpontja.
     * @param max A metsző intervallum jobb végpontja.
     * @return Az eredménybe tartozó indexhalmaz [minimuma, maximuma]
     */
    public static int[] trimIntervals(int[] lowerIndexes, int[] upperIndexes, int min, int max) {
        if (lowerIndexes == null) {
            return null;
        }

        int minIndex = getThisOrBiggerIndex(upperIndexes, min);
        int maxIndex = getThisOrSmallerIndex(lowerIndexes, max);

        if (minIndex > maxIndex) {
            return new int[]{0, -1}; // Üres a válasz
        }

        return new int[]{minIndex, maxIndex};
    }


    public static double[] getContainedSumNyuszival2(TIntArrayList lowerIndexes, TIntArrayList upperIndexes, float[][] facts) {
        int numberOfFacts = facts.length;
        double[] result = new double[numberOfFacts];
        int intvIndexMax = lowerIndexes.size();
        for (int intvIndex = 0; intvIndex < intvIndexMax; intvIndex++) {
            int iMax = upperIndexes.getQuick(intvIndex);
            for (int i = lowerIndexes.getQuick(intvIndex); i <= iMax; i++) {
                for (int f = 0; f < numberOfFacts; f++) {
                    result[f] += facts[f][i];
                }
            }
        }
        return result;
    }

    public static double[] calculateSumNyuszival2(int[][] offlineCalculatedLowerIndexes,
            int[][] offlineCalculatedUpperIndexes,
            int[][] lowerIndexes,
            int[][] upperIndexes,
            Cube cube) {
        double[] result;
        // Az olapos dimenziókkal való metszőintervallum megállapítása.
        int minSource = 0;
        int maxSource = cube.getCells().getCells()[0].length - 1;
        int[] offlineCalculatedIntersection = monotonicIntersection(offlineCalculatedLowerIndexes,
                offlineCalculatedUpperIndexes, minSource, maxSource);

        // A menet közben aggregálandó intervallumok elmetszése az olap-sávval.
        int numberOfOnTheFlyCalculatedDimension = lowerIndexes.length;
        int[] minTrimIndex = new int[numberOfOnTheFlyCalculatedDimension];
        int[] maxTrimIndex = new int[numberOfOnTheFlyCalculatedDimension];
        for (int d = 0; d < numberOfOnTheFlyCalculatedDimension; d++) {
            int[] trimIndexes = trimIntervals(lowerIndexes[d],
                    upperIndexes[d],
                    offlineCalculatedIntersection[0],
                    offlineCalculatedIntersection[1]);
            minTrimIndex[d] = trimIndexes[0];
            maxTrimIndex[d] = trimIndexes[1];
        }
        // Menet közben aggregálandó intervallumok metszete.
        TIntArrayList[] lowerAndUpperIndexes = intersection(offlineCalculatedIntersection[0], offlineCalculatedIntersection[1], lowerIndexes, upperIndexes, minTrimIndex, maxTrimIndex);

        // Mindent meghatározunk most.
        result = getContainedSumNyuszival2(lowerAndUpperIndexes[0], lowerAndUpperIndexes[1], facts);

        return result;
    }

    /**
     *
     * @param lowerIndexes
     * @param upperIndexes
     * @param cube
     * @return
     */
     abstract double[] doCalculate(TIntArrayList lowerIndexes, TIntArrayList upperIndexes, Cube cube);
}
