/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.agnos.cube.driver.zolikaokos;

import gnu.trove.list.array.TIntArrayList;
import java.util.Arrays;

/**
 *
 * @author ruzsaz
 */
public class Algorithms {


    /**
     * Kikeres egy értéket a rendezett tömbből. Ha nincs benne, akkor a nálánál
     * nagyobbak minimumát keresi ki.
     *
     * @param array Rendezett tömb.
     * @param key Keresett elem.
     * @return A megtalált elem indexe.
     */
    public int getThisOrBiggersIndex(int[] array, int key) {
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
    public int getThisOrBiggersIndex(int[] array, int fromIndex, int toIndex, int key) {
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
    public int getThisOrSmallersIndex(int[] array, int key) {
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
    public int getThisOrSmallersIndex(int[] array, int fromIndex, int toIndex, int key) {
        int index = Arrays.binarySearch(array, fromIndex, toIndex + 1, key);
        return (index < 0) ? -index - 2 : index;
    }

    /**
     * Monoton csökkenően egymásba ágyazott intervallumrendszer metszetét adja
     * meg. Egy szint intervallumában a következő szintben csak egy intervallum
     * lehet!
     *
     * @param a A bal végpontok rendezett tömbje, szintenként tömbbe rendezve.
     * @param b A jobb végpontok rendezett tömbje, szintenként tömbbe rendezve.
     * @param defaultMin Ha nincs mivel elmetszeni, akkor a felvevendő minimum.
     * @param defaultMax Ha nincs mivel elmetszeni, akkor a felvevendő maximum.
     * @return A metszetintervallum [bal, jobb] végpontja.
     */
    public int[] monotonicIntersection(int[][] a, int[][] b, int defaultMin, int defaultMax) {
        if (a == null || a.length == 0) {
            return new int[]{defaultMin, defaultMax}; // Ha nincs mit elmetszeni
        }

        int min = a[0][0];
        int max = b[0][0];
        for (int d = 0; d < a.length; d++) {
            int[] currentA = a[d];
            int[] currentB = b[d];
            int index = getThisOrBiggersIndex(currentB, min);
            if (index < currentA.length && currentA[index] <= max) {
                min = Math.max(currentA[index], min);
                max = Math.min(currentB[index], max);
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
     * @param a A bal végpontok rendezett tömbje, szintenként tömbbe rendezve.
     * @param b A jobb végpontok rendezett tömbje, szintenként tömbbe rendezve.
     * @param minIndex Ettől az idenxtől kezdve (ezt is beleértve) kell
     * figyelembevenni a az intervallumokat. Szintenként tömbbe rendezve.
     * @param maxIndex Eddig az idenxig kezdve (ezt is beleértve) kell
     * figyelembevenni a az intervallumokat. Szintenként tömbbe rendezve.
     * @return A metszet intervallumrendszer (zárt intervallumok).
     */
    public TIntArrayList[] intersection(int min, int max, int[][] a, int[][] b, int[] minIndex, int[] maxIndex) {
        TIntArrayList Ra = new TIntArrayList();
        TIntArrayList Rb = new TIntArrayList();
        int depth = a.length;
        int[] index = Arrays.copyOf(minIndex, depth);

        for (int d = 0; d < depth; d++) {
            if (a[d].length == 0) {
                return new TIntArrayList[]{Ra, Rb};
            }
        }

        while (true) {
            int maxA = min, minB = max, minBPos = -1;

            for (int d = 0; d < depth; d++) {
                int thisA = a[d][index[d]];
                int thisB = b[d][index[d]];
                if (thisA > maxA) {
                    maxA = thisA;
                }
                if (thisB < minB) {
                    minB = thisB;
                    minBPos = d;
                }
            }
            if (maxA <= minB) {
                Ra.add(maxA);
                Rb.add(minB);
            }

            if (minBPos > -1) {
                index[minBPos]++;
                if (index[minBPos] > maxIndex[minBPos]) {
                    break;
                }
            } else {
                break;
            }
        }
        return new TIntArrayList[]{Ra, Rb};
    }

    /**
     * Elmetsz egy intervallumrendszert egy intervallummal. Az eredmény az a
     * minimális és maximális index, amely indexűek belelógnak a metszetbe. (Az
     * eredmény legkisebb és legnagyobb indexű tagja TÚLNYÚLHAT a metsző
     * intervallumon, lefelé illetve felfelé.)
     *
     * @param a A bal végpontok rendezett tömbje.
     * @param b A jobb végpontok rendezett tömbje.
     * @param min A metsző intervallum bal végpontja.
     * @param max A metsző intervallum jobb végpontja.
     * @return Az eredménybe tartozó indexhalmaz [minimuma, maximuma]
     */
    public int[] trimIntervals(int[] a, int[] b, int min, int max) {
        if (a == null) {
            return null;
        }

        int minIndex = getThisOrBiggersIndex(b, min);
        int maxIndex = getThisOrSmallersIndex(a, max);

        if (minIndex > maxIndex) {
            return new int[]{0, -1}; // Üres a válasz
        }

        return new int[]{minIndex, maxIndex};
    }


    public double[] getContainedSumNyuszival2(TIntArrayList a, TIntArrayList b, double[][] facts) {
        int numberOfFacts = facts.length;
        double[] result = new double[numberOfFacts];
        for (int intvIndex = 0, intvIndexMax = a.size(); intvIndex < intvIndexMax; intvIndex++) {
            for (int i = a.getQuick(intvIndex), iMax = b.getQuick(intvIndex); i <= iMax; i++) {
                for (int f = 0; f < numberOfFacts; f++) {
                    result[f] += facts[f][i];
                }
            }
        }
        return result;
    }


    public double[] calculateSumNyuszival2(int[][] Oa, int[][] Ob, int[][] a, int[][] b, double[][] facts) {
        double[] result;
            // Az olapos dimenziókkal való metszőintervallum megállapítása.
            int minSource = 0;
            int maxSource = facts[0].length - 1;
            int[] olapIntersection = monotonicIntersection(Oa, Ob, minSource, maxSource);
            
                // A menet közben aggregálandó intervallumok elmetszése az olap-sávval.
                int onTheFlyDimension = a.length;
                int[] minTrimIndex = new int[onTheFlyDimension];
                int[] maxTrimIndex = new int[onTheFlyDimension];
                for (int d = 0; d < onTheFlyDimension; d++) {
                    int[] trimIndexes = trimIntervals(a[d], b[d], olapIntersection[0], olapIntersection[1]);
                    minTrimIndex[d] = trimIndexes[0];
                    maxTrimIndex[d] = trimIndexes[1];
                }
                // Menet közben aggregálandó intervallumok metszete.
                TIntArrayList[] nyusz = intersection(olapIntersection[0], olapIntersection[1], a, b, minTrimIndex, maxTrimIndex);

            // Mindent meghatározunk most.
            result = getContainedSumNyuszival2(nyusz[0], nyusz[1], facts);

        return result;
    }

}
