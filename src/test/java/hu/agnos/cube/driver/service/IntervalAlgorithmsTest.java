package hu.agnos.cube.driver.service;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import gnu.trove.list.array.TIntArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import hu.agnos.cube.driver.util.IntervalAlgorithms;

public class IntervalAlgorithmsTest {

    static int s = 10000000;
    static int numberOfFacts = 10;
    static int[] intArr = new int[s];
    static float[][] facts = new float[numberOfFacts][s];
    int numberOfTests = 10000;

    public IntervalAlgorithmsTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        for (int i = 0; i < s; i++) {
            intArr[i] = i * 3;
        }

        for (int f = 0; f < numberOfFacts; f++) {
            for (int i = 0; i < s; i++) {
                facts[f][i] = (float) Math.random();
            }
        }

        System.out.println("Táblaméret: " + ((s + 0.0) / 1000000) + " M sor");
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testGetThisOrBiggersIndex() {
        assertEquals(4, IntervalAlgorithms.getThisOrBiggerIndex(new int[]{2, 4, 6, 8, 10, 12}, 10));
        assertEquals(2, IntervalAlgorithms.getThisOrBiggerIndex(new int[]{2, 4, 6, 8, 10, 12}, 5));
        assertEquals(0, IntervalAlgorithms.getThisOrBiggerIndex(new int[]{2, 4, 6, 8, 10, 12}, -3));
        assertEquals(6, IntervalAlgorithms.getThisOrBiggerIndex(new int[]{2, 4, 6, 8, 10, 12}, 15));
    }

    @Test
    public void testGetThisOrSmallersIndex() {
        assertEquals(4, IntervalAlgorithms.getThisOrSmallerIndex(new int[]{2, 4, 6, 8, 10, 12}, 10));
        assertEquals(1, IntervalAlgorithms.getThisOrSmallerIndex(new int[]{2, 4, 6, 8, 10, 12}, 5));
        assertEquals(-1, IntervalAlgorithms.getThisOrSmallerIndex(new int[]{2, 4, 6, 8, 10, 12}, -3));
        assertEquals(5, IntervalAlgorithms.getThisOrSmallerIndex(new int[]{2, 4, 6, 8, 10, 12}, 15));
    }

    @Test
    public void testGetThisOrBiggersIndex2() {
        assertEquals(4, IntervalAlgorithms.getThisOrBiggerIndex(new int[]{2, 4, 6, 8, 10, 12}, 3, 5, 10));
        assertEquals(4, IntervalAlgorithms.getThisOrBiggerIndex(new int[]{2, 4, 6, 8, 10, 12}, 0, 3, 12));
        assertEquals(3, IntervalAlgorithms.getThisOrBiggerIndex(new int[]{2, 4, 6, 8, 10, 12}, 3, 5, -3));
        assertEquals(5, IntervalAlgorithms.getThisOrBiggerIndex(new int[]{2, 4, 6, 8, 10, 12}, 2, 4, 15));
    }

    @Test
    public void testGetThisOrSmallersIndex2() {
        assertEquals(4, IntervalAlgorithms.getThisOrSmallerIndex(new int[]{2, 4, 6, 8, 10, 12}, 3, 4, 10));
        assertEquals(2, IntervalAlgorithms.getThisOrSmallerIndex(new int[]{2, 4, 6, 8, 10, 12}, 3, 5, 5));
        assertEquals(0, IntervalAlgorithms.getThisOrSmallerIndex(new int[]{2, 4, 6, 8, 10, 12}, 1, 3, -3));
        assertEquals(4, IntervalAlgorithms.getThisOrSmallerIndex(new int[]{2, 4, 6, 8, 10, 12}, 2, 4, 15));
    }

    @Test
    public void testMonotonicIntersection() {
        int[] a0_0 = new int[]{566};
        int[] b0_0 = new int[]{789};
        int[] a0_1 = new int[]{4, 165, 454, 588, 998};
        int[] b0_1 = new int[]{9, 265, 554, 712, 1034};
        int[] a0_2 = new int[]{4, 17, 149, 211, 454, 570, 579, 647, 745, 999, 1111};
        int[] b0_2 = new int[]{6, 19, 158, 235, 499, 578, 582, 678, 812, 1000, 1217};
        int[][] a0 = new int[][]{a0_0, a0_1, a0_2};
        int[][] b0 = new int[][]{b0_0, b0_1, b0_2};
        int[] result0 = IntervalAlgorithms.monotonicIntersection(a0, b0, 0, 2000);
        assertArrayEquals(new int[]{647, 678}, result0);

        int[] a1_0 = new int[]{566};
        int[] b1_0 = new int[]{789};
        int[] a1_1 = new int[]{4, 165, 454, 888, 998};
        int[] b1_1 = new int[]{9, 265, 554, 912, 1034};
        int[] a1_2 = new int[]{4, 17, 149, 211, 454, 570, 579, 647, 745, 999, 1111};
        int[] b1_2 = new int[]{6, 19, 158, 235, 499, 578, 582, 678, 812, 1000, 1217};
        int[][] a1 = new int[][]{a1_0, a1_1, a1_2};
        int[][] b1 = new int[][]{b1_0, b1_1, b1_2};
        int[] result1 = IntervalAlgorithms.monotonicIntersection(a1, b1, 0, 2000);
        assertArrayEquals(new int[]{0, -1}, result1);

        int[] a2_0 = new int[]{566};
        int[] b2_0 = new int[]{789};
        int[][] a2 = new int[][]{a2_0};
        int[][] b2 = new int[][]{b2_0};
        int[] result2 = IntervalAlgorithms.monotonicIntersection(a2, b2, 0, 2000);
        assertArrayEquals(new int[]{566, 789}, result2);

        int[][] a3 = new int[][]{};
        int[][] b3 = new int[][]{};
        int[] result3 = IntervalAlgorithms.monotonicIntersection(a3, b3, 0, 2000);
        assertArrayEquals(new int[]{0, 2000}, result3);

        assertArrayEquals(new int[]{0, 1}, IntervalAlgorithms.monotonicIntersection(null, null, 0, 1));

        int[] a4_0 = new int[]{100};
        int[] b4_0 = new int[]{200};
        int[] a4_1 = new int[]{4, 230, 454, 588, 998};
        int[] b4_1 = new int[]{150, 265, 554, 712, 1034};
        int[][] a4 = new int[][]{a4_0, a4_1};
        int[][] b4 = new int[][]{b4_0, b4_1};
        int[] result4 = IntervalAlgorithms.monotonicIntersection(a4, b4, 0, 2000);
        assertArrayEquals(new int[]{100, 150}, result4);

        int[] a5_0 = new int[]{100};
        int[] b5_0 = new int[]{200};
        int[] a5_1 = new int[]{4, 170, 454, 588, 998};
        int[] b5_1 = new int[]{89, 220, 554, 712, 1034};
        int[][] a5 = new int[][]{a5_0, a5_1};
        int[][] b5 = new int[][]{b5_0, b5_1};
        int[] result5 = IntervalAlgorithms.monotonicIntersection(a5, b5, 0, 2000);
        assertArrayEquals(new int[]{170, 200}, result5);

        int[] a6_0 = new int[]{100};
        int[] b6_0 = new int[]{200};
        int[] a6_1 = new int[]{300, 588, 998};
        int[] b6_1 = new int[]{400, 712, 1034};
        int[][] a6 = new int[][]{a6_0, a6_1};
        int[][] b6 = new int[][]{b6_0, b6_1};
        int[] result6 = IntervalAlgorithms.monotonicIntersection(a6, b6, 0, 2000);
        assertArrayEquals(new int[]{0, -1}, result6);

        int[] a7_0 = new int[]{100};
        int[] b7_0 = new int[]{200};
        int[] a7_1 = new int[]{4, 240, 454, 588, 998};
        int[] b7_1 = new int[]{230, 260, 554, 712, 1034};
        int[][] a7 = new int[][]{a7_0, a7_1};
        int[][] b7 = new int[][]{b7_0, b7_1};
        int[] result7 = IntervalAlgorithms.monotonicIntersection(a7, b7, 0, 2000);
        assertArrayEquals(new int[]{100, 200}, result7);

    }

    @Test
    public void testTrimIntervals() {
        int[] a0 = new int[]{0, 2, 4, 6, 8, 10, 12};
        int[] b0 = new int[]{1, 3, 5, 7, 9, 11, 13};
        int[] resultIndex0 = IntervalAlgorithms.trimIntervals(a0, b0, 4, 11);
        String r0 = "";
        for (int i = resultIndex0[0]; i <= resultIndex0[1]; i++) {
            r0 = r0 + " [" + a0[i] + "," + b0[i] + "]";
        }
        assertArrayEquals(new int[]{2, 5}, resultIndex0);

        int[] a1 = new int[]{0, 20, 40, 60, 80, 100, 120};
        int[] b1 = new int[]{10, 30, 50, 70, 90, 110, 130};
        int[] resultIndex1 = IntervalAlgorithms.trimIntervals(a1, b1, 43, 100);
        String r1 = "";
        for (int i = resultIndex1[0]; i < resultIndex1[1] + 1; i++) {
            r1 = r1 + " [" + a1[i] + "," + b1[i] + "]";
        }
        assertArrayEquals(new int[]{2, 5}, resultIndex1);

        int[] a2 = new int[]{0, 20, 40, 60, 80, 100, 120};
        int[] b2 = new int[]{10, 30, 50, 70, 90, 110, 130};
        int[] resultIndex2 = IntervalAlgorithms.trimIntervals(a2, b2, 53, 57);
        assertArrayEquals(new int[]{0, -1}, resultIndex2);

        int[] a3 = new int[]{0, 20, 40, 60, 80, 100, 120};
        int[] b3 = new int[]{0, 20, 40, 60, 80, 100, 120};
        int[] resultIndex3 = IntervalAlgorithms.trimIntervals(a3, b3, 43, 999);
        String r3 = "";
        for (int i = resultIndex3[0]; i < resultIndex3[1] + 1; i++) {
            r3 = r3 + " [" + a3[i] + "," + b3[i] + "]";
        }
        assertArrayEquals(new int[]{3, 6}, resultIndex3);

        int[] a4 = new int[]{};
        int[] b4 = new int[]{};
        int[] resultIndex4 = IntervalAlgorithms.trimIntervals(a4, b4, 53, 57);
        assertArrayEquals(new int[]{0, -1}, resultIndex4);

    }

    @Test
    public void testIntersection() {
        int min = 100;
        int max = 900;

        int[] Oa0_0 = new int[]{60};
        int[] Ob0_0 = new int[]{1200};
        int[] Oa0_1 = new int[]{4, 165, 454, 588, 998};
        int[] Ob0_1 = new int[]{9, 265, 554, 712, 1034};
        int[] Oa0_2 = new int[]{4, 17, 149, 211, 452, 570, 589, 745, 999, 1111};
        int[] Ob0_2 = new int[]{6, 19, 158, 235, 499, 588, 720, 812, 1000, 1217};
        int[][] Oa0 = new int[][]{Oa0_0, Oa0_1, Oa0_2};
        int[][] Ob0 = new int[][]{Ob0_0, Ob0_1, Ob0_2};

        int[] minIndex = new int[]{0, 1, 2};
        int[] maxIndex = new int[]{1, 3, 7};

        TIntArrayList[] result = IntervalAlgorithms.intersection(min, max, Oa0, Ob0, minIndex, maxIndex);

        assertEquals(211, result[0].get(0));
        assertEquals(235, result[1].get(0));
        assertEquals(454, result[0].get(1));
        assertEquals(499, result[1].get(1));
        assertEquals(588, result[0].get(2));
        assertEquals(588, result[1].get(2));
        assertEquals(589, result[0].get(3));
        assertEquals(712, result[1].get(3));
        assertEquals(4, result[0].size());
    }

}
