/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.math.curve;

import static org.testng.AssertJUnit.assertTrue;
import org.testng.annotations.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.opengamma.math.ParallelArrayBinarySort;
import com.opengamma.util.tuple.DoublesPair;
import com.opengamma.util.tuple.FirstThenSecondDoublesPairComparator;

/**
 * 
 */
public class DoublesCurveTestCase {
  protected static final String NAME1 = "a";
  protected static final String NAME2 = "b";
  protected static final double[] X_PRIMITIVE;
  protected static final double[] Y_PRIMITIVE;
  protected static final double[] X_PRIMITIVE_SORTED;
  protected static final double[] Y_PRIMITIVE_SORTED;
  protected static final Double[] X_OBJECT;
  protected static final Double[] Y_OBJECT;
  protected static final Double[] X_OBJECT_SORTED;
  protected static final Double[] Y_OBJECT_SORTED;
  protected static final Map<Double, Double> MAP;
  protected static final Map<Double, Double> MAP_SORTED;
  protected static final DoublesPair[] PAIR_ARRAY;
  protected static final DoublesPair[] PAIR_ARRAY_SORTED;
  protected static final Set<DoublesPair> PAIR_SET;
  protected static final Set<DoublesPair> PAIR_SET_SORTED;
  protected static final List<Double> X_LIST;
  protected static final List<Double> Y_LIST;
  protected static final List<Double> X_LIST_SORTED;
  protected static final List<Double> Y_LIST_SORTED;
  protected static final List<DoublesPair> PAIR_LIST;
  protected static final List<DoublesPair> PAIR_LIST_SORTED;

  static {
    final int n = 10;
    X_PRIMITIVE = new double[n];
    Y_PRIMITIVE = new double[n];
    X_OBJECT = new Double[n];
    Y_OBJECT = new Double[n];
    MAP = new HashMap<Double, Double>();
    PAIR_ARRAY = new DoublesPair[n];
    PAIR_SET = new HashSet<DoublesPair>();
    X_LIST = new ArrayList<Double>();
    Y_LIST = new ArrayList<Double>();
    X_LIST_SORTED = new ArrayList<Double>();
    Y_LIST_SORTED = new ArrayList<Double>();
    PAIR_LIST = new ArrayList<DoublesPair>();
    PAIR_LIST_SORTED = new ArrayList<DoublesPair>();
    double x, y;
    for (int i = 0; i < 5; i++) {
      x = 2 * i;
      y = 3 * x;
      X_PRIMITIVE[i] = x;
      Y_PRIMITIVE[i] = y;
      X_OBJECT[i] = x;
      Y_OBJECT[i] = y;
      MAP.put(x, y);
      PAIR_ARRAY[i] = DoublesPair.of(x, y);
      PAIR_SET.add(DoublesPair.of(x, y));
      X_LIST.add(x);
      Y_LIST.add(y);
      PAIR_LIST.add(DoublesPair.of(x, y));
    }
    for (int i = 5, j = 0; i < 10; i++, j++) {
      x = 2 * j + 1;
      y = 3 * x;
      X_PRIMITIVE[i] = x;
      Y_PRIMITIVE[i] = y;
      X_OBJECT[i] = x;
      Y_OBJECT[i] = y;
      MAP.put(x, y);
      PAIR_ARRAY[i] = DoublesPair.of(x, y);
      PAIR_SET.add(DoublesPair.of(x, y));
      X_LIST.add(x);
      Y_LIST.add(y);
      PAIR_LIST.add(DoublesPair.of(x, y));
    }
    X_PRIMITIVE_SORTED = Arrays.copyOf(X_PRIMITIVE, n);
    Y_PRIMITIVE_SORTED = Arrays.copyOf(Y_PRIMITIVE, n);
    ParallelArrayBinarySort.parallelBinarySort(X_PRIMITIVE_SORTED, Y_PRIMITIVE_SORTED);
    X_OBJECT_SORTED = Arrays.copyOf(X_OBJECT, n);
    Y_OBJECT_SORTED = Arrays.copyOf(Y_OBJECT, n);
    ParallelArrayBinarySort.parallelBinarySort(X_OBJECT_SORTED, Y_OBJECT_SORTED);
    MAP_SORTED = new TreeMap<Double, Double>(MAP);
    PAIR_SET_SORTED = new TreeSet<DoublesPair>(FirstThenSecondDoublesPairComparator.INSTANCE);
    PAIR_SET_SORTED.addAll(PAIR_SET);
    PAIR_ARRAY_SORTED = PAIR_SET_SORTED.toArray(new DoublesPair[0]);
    for (int i = 0; i < n; i++) {
      x = X_PRIMITIVE_SORTED[i];
      y = Y_PRIMITIVE_SORTED[i];
      X_LIST_SORTED.add(x);
      Y_LIST_SORTED.add(y);
      PAIR_LIST_SORTED.add(DoublesPair.of(x, y));
    }
  }

  @Test
  public void testObjectArrays() {
    final DoublesCurve curve = new DummyCurve(PAIR_SET, false);
    final Double[] x = curve.getXData();
    assertTrue(x == curve.getXData());
    final Double[] y = curve.getYData();
    assertTrue(y == curve.getYData());
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNull1() {
    new DummyCurve((double[]) null, Y_PRIMITIVE, true);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNull2() {
    new DummyCurve(X_PRIMITIVE, (double[]) null, true);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNull3() {
    new DummyCurve((Double[]) null, Y_OBJECT, true);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNull4() {
    new DummyCurve(X_OBJECT, (Double[]) null, true);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNull5() {
    new DummyCurve((Map<Double, Double>) null, true);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNull6() {
    final Map<Double, Double> m = new HashMap<Double, Double>();
    m.put(null, 3.);
    m.put(1., 2.);
    new DummyCurve(m, true);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNull7() {
    final Map<Double, Double> m = new HashMap<Double, Double>();
    m.put(3., null);
    m.put(1., 2.);
    new DummyCurve(m, true);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNull8() {
    new DummyCurve((DoublesPair[]) null, true);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNull9() {
    final DoublesPair[] p = new DoublesPair[2];
    p[0] = DoublesPair.of(2., 3.);
    p[1] = null;
    new DummyCurve(p, true);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNull10() {
    new DummyCurve((List<Double>) null, Y_LIST, true);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNull11() {
    new DummyCurve(X_LIST, (List<Double>) null, true);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNull12() {
    final List<Double> l = Arrays.asList(3., null);
    new DummyCurve(l, Y_LIST, true);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNull13() {
    final List<Double> l = Arrays.asList(3., null);
    new DummyCurve(X_LIST, l, true);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNull14() {
    new DummyCurve((List<DoublesPair>) null, true);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNull15() {
    final List<DoublesPair> l = Arrays.asList(null, DoublesPair.of(1., 2.));
    new DummyCurve(l, true);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNull16() {
    new DummyCurve((double[]) null, Y_PRIMITIVE, true, NAME1);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNull17() {
    new DummyCurve(X_PRIMITIVE, (double[]) null, true, NAME1);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNull18() {
    new DummyCurve((Double[]) null, Y_OBJECT, true, NAME1);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNull19() {
    new DummyCurve(X_OBJECT, (Double[]) null, true, NAME1);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNull20() {
    new DummyCurve((Map<Double, Double>) null, true, NAME1);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNull21() {
    final Map<Double, Double> m = new HashMap<Double, Double>();
    m.put(null, 3.);
    m.put(1., 2.);
    new DummyCurve(m, true, NAME1);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNull22() {
    final Map<Double, Double> m = new HashMap<Double, Double>();
    m.put(3., null);
    m.put(1., 2.);
    new DummyCurve(m, true, NAME1);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNull23() {
    new DummyCurve((DoublesPair[]) null, true, NAME1);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNull24() {
    final DoublesPair[] p = new DoublesPair[2];
    p[0] = DoublesPair.of(2., 3.);
    p[1] = null;
    new DummyCurve(p, true, NAME1);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNull25() {
    new DummyCurve((List<Double>) null, Y_LIST, true, NAME1);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNull26() {
    new DummyCurve(X_LIST, (List<Double>) null, true, NAME1);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNull27() {
    final List<Double> l = Arrays.asList(3., null);
    new DummyCurve(l, Y_LIST, true, NAME1);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNull28() {
    final List<Double> l = Arrays.asList(3., null);
    new DummyCurve(X_LIST, l, true, NAME1);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNull29() {
    new DummyCurve((List<DoublesPair>) null, true, NAME1);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNull30() {
    final List<DoublesPair> l = Arrays.asList(null, DoublesPair.of(1., 2.));
    new DummyCurve(l, true, NAME1);
  }

  private static class DummyCurve extends DoublesCurve {

    public DummyCurve(final double[] xData, final double[] yData, final boolean isSorted) {
      super(xData, yData, isSorted);
    }

    public DummyCurve(final Double[] xData, final Double[] yData, final boolean isSorted) {
      super(xData, yData, isSorted);
    }

    public DummyCurve(final Map<Double, Double> data, final boolean isSorted) {
      super(data, isSorted);
    }

    public DummyCurve(final DoublesPair[] data, final boolean isSorted) {
      super(data, isSorted);
    }

    public DummyCurve(final Set<DoublesPair> data, final boolean isSorted) {
      super(data, isSorted);
    }

    public DummyCurve(final List<Double> xData, final List<Double> yData, final boolean isSorted) {
      super(xData, yData, isSorted);
    }

    public DummyCurve(final List<DoublesPair> data, final boolean isSorted) {
      super(data, isSorted);
    }

    public DummyCurve(final double[] xData, final double[] yData, final boolean isSorted, final String name) {
      super(xData, yData, isSorted, name);
    }

    public DummyCurve(final Double[] xData, final Double[] yData, final boolean isSorted, final String name) {
      super(xData, yData, isSorted, name);
    }

    public DummyCurve(final Map<Double, Double> data, final boolean isSorted, final String name) {
      super(data, isSorted, name);
    }

    public DummyCurve(final DoublesPair[] data, final boolean isSorted, final String name) {
      super(data, isSorted, name);
    }

    public DummyCurve(final Set<DoublesPair> data, final boolean isSorted, final String name) {
      super(data, isSorted, name);
    }

    public DummyCurve(final List<Double> xData, final List<Double> yData, final boolean isSorted, final String name) {
      super(xData, yData, isSorted, name);
    }

    public DummyCurve(final List<DoublesPair> data, final boolean isSorted, final String name) {
      super(data, isSorted, name);
    }

    @Override
    public Double getYValue(final Double x) {
      return null;
    }

  }
}
