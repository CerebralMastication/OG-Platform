/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.model.volatility.smile.fitting;

import java.util.BitSet;

import com.opengamma.financial.model.volatility.smile.function.MixedLogNormalModelData;
import com.opengamma.financial.model.volatility.smile.function.VolatilityFunctionProvider;
import com.opengamma.math.function.Function1D;
import com.opengamma.math.matrix.DoubleMatrix1D;
import com.opengamma.math.minimization.NonLinearParameterTransforms;
import com.opengamma.math.minimization.NullTransform;
import com.opengamma.math.minimization.ParameterLimitsTransform;
import com.opengamma.math.minimization.UncoupledParameterTransforms;

/**
 * 
 */
public class MixedLogNormalModelFitter extends SmileModelFitter<MixedLogNormalModelData> {

  private final ParameterLimitsTransform[] _transforms;
  private final boolean _useShiftedMean;
  private final int _nNormals;

  public MixedLogNormalModelFitter(double forward, double[] strikes, double timeToExpiry, double[] impliedVols, double[] error, VolatilityFunctionProvider<MixedLogNormalModelData> model,
      int numNormals, boolean useShiftedMeans) {
    super(forward, strikes, timeToExpiry, impliedVols, error, model);

    final int n = useShiftedMeans ? 3 * numNormals - 2 : 2 * numNormals - 1;
    _transforms = new ParameterLimitsTransform[n];
    //    for (int i = 0; i < numNormals; i++) {
    //      _transforms[i] = new SingleRangeLimitTransform(0.0, LimitType.GREATER_THAN);
    //    }
    //TODO investigate whether it is better to restrict the range of angles
    for (int i = 0; i < n; i++) {
      _transforms[i] = new NullTransform();
    }

    _useShiftedMean = useShiftedMeans;
    _nNormals = numNormals;
  }

  @Override
  protected NonLinearParameterTransforms getTransform(DoubleMatrix1D start) {
    BitSet fixed = new BitSet();
    return new UncoupledParameterTransforms(start, _transforms, fixed);
  }

  @Override
  protected NonLinearParameterTransforms getTransform(DoubleMatrix1D start, BitSet fixed) {
    return new UncoupledParameterTransforms(start, _transforms, fixed);
  }

  @Override
  protected MixedLogNormalModelData toSmileModelData(DoubleMatrix1D modelParameters) {
    return new MixedLogNormalModelData(modelParameters.getData(), _useShiftedMean);
  }

  @Override
  protected Function1D<DoubleMatrix1D, Boolean> getConstraintFunction(final NonLinearParameterTransforms t) {
    return new Function1D<DoubleMatrix1D, Boolean>() {
      @Override
      public Boolean evaluate(DoubleMatrix1D x) {
        for (int i = 0; i < _nNormals; i++) {
          if (x.getEntry(i) < 0.0) {
            return true;
          }
        }
        return false;
      }
    };
  }

}