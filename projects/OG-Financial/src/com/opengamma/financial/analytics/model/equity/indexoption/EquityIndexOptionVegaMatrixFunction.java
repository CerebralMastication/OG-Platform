/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.analytics.model.equity.indexoption;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.opengamma.analytics.financial.equity.DerivativeSensitivityCalculator;
import com.opengamma.analytics.financial.equity.StaticReplicationDataBundle;
import com.opengamma.analytics.financial.equity.option.EquityIndexOption;
import com.opengamma.analytics.financial.equity.option.EquityIndexOptionPresentValueCalculator;
import com.opengamma.analytics.math.surface.NodalDoublesSurface;
import com.opengamma.engine.value.ValueRequirementNames;
import com.opengamma.financial.analytics.DoubleLabelledMatrix2D;

/**
 *
 */
public class EquityIndexOptionVegaMatrixFunction extends EquityIndexOptionFunction {
  private static final EquityIndexOptionPresentValueCalculator PVC = EquityIndexOptionPresentValueCalculator.getInstance();
  private static final DerivativeSensitivityCalculator CALCULATOR = new DerivativeSensitivityCalculator(PVC);

  public EquityIndexOptionVegaMatrixFunction() {
    super(ValueRequirementNames.VEGA_QUOTE_MATRIX);
  }

  @Override
  protected Object computeValues(EquityIndexOption derivative, StaticReplicationDataBundle market) {
    final NodalDoublesSurface vegaSurface = CALCULATOR.calcBlackVegaForEntireSurface(derivative, market);
    final Double[] xValues = vegaSurface.getXData();
    final Double[] yValues = vegaSurface.getYData();
    final Set<Double> xSet = new HashSet<Double>(Arrays.asList(xValues));
    final Set<Double> ySet = new HashSet<Double>(Arrays.asList(yValues));
    final Double[] uniqueX = xSet.toArray(new Double[0]);
    final Double[] uniqueY = ySet.toArray(new Double[0]);
    final double[][] values = new double[ySet.size()][xSet.size()];
    int i = 0;
    for (final Double x : xSet) {
      int j = 0;
      for (final Double y : ySet) {
        double vega;
        try {
          vega = vegaSurface.getZValue(x, y);
        } catch (final IllegalArgumentException e) {
          vega = 0;
        }
        values[j++][i] = vega;
      }
      i++;
    }
    final DoubleLabelledMatrix2D matrix = new DoubleLabelledMatrix2D(uniqueX, uniqueY, values);
    return matrix;
  }
}
