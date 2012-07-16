/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.analytics.financial.equity.variance;

import static org.testng.AssertJUnit.assertEquals;

import org.apache.commons.lang.Validate;
import org.testng.annotations.Test;

import com.opengamma.analytics.financial.equity.variance.derivative.EquityVarianceSwap;
import com.opengamma.analytics.financial.equity.variance.derivative.VarianceSwap;
import com.opengamma.analytics.financial.equity.variance.pricing.AffineDividends;
import com.opengamma.analytics.financial.equity.variance.pricing.EquityDividendsCurvesBundle;
import com.opengamma.analytics.financial.equity.variance.pricing.EquityVarianceSwapPricer;
import com.opengamma.analytics.financial.equity.variance.pricing.EquityVarianceSwapStaticReplication;
import com.opengamma.analytics.financial.model.interestrate.curve.YieldAndDiscountCurve;
import com.opengamma.analytics.financial.model.interestrate.curve.YieldCurve;
import com.opengamma.analytics.financial.model.volatility.BlackFormulaRepository;
import com.opengamma.analytics.financial.model.volatility.surface.PureImpliedVolatilitySurface;
import com.opengamma.analytics.math.curve.ConstantDoublesCurve;
import com.opengamma.analytics.math.function.Function;
import com.opengamma.analytics.math.surface.ConstantDoublesSurface;
import com.opengamma.analytics.math.surface.FunctionalDoublesSurface;
import com.opengamma.util.money.Currency;

/**
 * 
 */
public class EquityVarianceSwapPricerTest {

  private static final double SPOT = 65.4;
  private static final double[] EXPIRIES = new double[] {1. / 52, 2. / 52, 1. / 12, 3. / 12, 6. / 12, 1.0, 2.0 };
  private static final double[][] STRIKES = new double[][] {
      {50, 55, 60, 65, 70, 75, 80 },
      {50, 55, 60, 65, 70, 75, 80 },
      {50, 55, 60, 65, 70, 75, 80 },
      {40, 50, 60, 70, 80, 90 },
      {40, 50, 60, 70, 80, 90, 100 },
      {30, 50, 60, 70, 80, 90, 100 },
      {20, 40, 55, 65, 75, 90, 105, 125 } };
  private static final double[][] OTM_PRICES_FLAT;
  private static final double[][] OTM_PRICES;
  private static final double PURE_VOL = 0.45;
  private static final PureImpliedVolatilitySurface PURE_VOL_SURFACE;
  private static final double[] TAU = new double[] {5. / 12, 11. / 12, 17. / 12, 23. / 12 };
  private static final double[] ALPHA = new double[] {3.0, 2.0, 1.0, 0.0 };
  private static final double[] BETA = new double[] {0.0, 0.02, 0.03, 0.04 };
  private static final AffineDividends DIVIDENDS = new AffineDividends(TAU, ALPHA, BETA);
  private static final AffineDividends NULL_DIVIDENDS = new AffineDividends(new double[0], new double[0], new double[0]);
  private static final double R = 0.07;
  private static final YieldAndDiscountCurve DISCOUNT_CURVE = new YieldCurve("Discount", ConstantDoublesCurve.from(R));
  private static final VarianceSwap VS = new VarianceSwap(0, 0.75, 0.75, 0.0, 1.0, Currency.USD, 252, 252, 0, new double[0], new double[0]);
  private static final EquityVarianceSwap EVS_COR_FRO_DIVS = new EquityVarianceSwap(VS, true);
  private static final EquityVarianceSwap EVS = new EquityVarianceSwap(VS, false);

  static {

    OTM_PRICES_FLAT = getOptionPrices(SPOT, EXPIRIES, STRIKES, DISCOUNT_CURVE, DIVIDENDS, new PureImpliedVolatilitySurface(ConstantDoublesSurface.from(PURE_VOL)));
    final int n = 3;
    final double[] sigma = new double[] {0.2, 0.5, 1.0 };
    final double[] w = new double[] {0.8, 0.15, 0.05 };
    final double[] f = new double[n];
    f[0] = 1.0;
    f[1] = 0.95;
    double sum = 0;
    for (int i = 0; i < n - 1; i++) {
      sum += w[i] * f[i];
    }
    f[n - 1] = (1.0 - sum) / w[n - 1];
    Validate.isTrue(f[n - 1] > 0);

    final Function<Double, Double> surf = new Function<Double, Double>() {
      @Override
      public Double evaluate(final Double... tx) {
        final double expiry = tx[0];
        final double x = tx[1];
        final boolean isCall = x > 1.0;
        double price = 0;
        for (int i = 0; i < n; i++) {
          price += w[i] * BlackFormulaRepository.price(f[i], x, expiry, sigma[i], isCall);
        }
        return BlackFormulaRepository.impliedVolatility(price, 1.0, x, expiry, isCall);
      }
    };

    PURE_VOL_SURFACE = new PureImpliedVolatilitySurface(FunctionalDoublesSurface.from(surf));
    OTM_PRICES = getOptionPrices(SPOT, EXPIRIES, STRIKES, DISCOUNT_CURVE, DIVIDENDS, PURE_VOL_SURFACE);
  }

  private static double[][] getOptionPrices(final double spot, final double[] expiries, final double[][] strikes, final YieldAndDiscountCurve discountCurve, final AffineDividends dividends,
      final PureImpliedVolatilitySurface surf) {
    int nExp = expiries.length;
    double[][] prices = new double[nExp][];
    EquityDividendsCurvesBundle divCurves = new EquityDividendsCurvesBundle(spot, discountCurve, dividends);
    for (int i = 0; i < nExp; i++) {
      final int n = strikes[i].length;
      prices[i] = new double[n];
      double t = expiries[i];
      double f = divCurves.getF(t);
      double d = divCurves.getD(t);
      double p = discountCurve.getDiscountFactor(t);
      for (int j = 0; j < n; j++) {
        double x = (strikes[i][j] - d) / (f - d);
        boolean isCall = x >= 1.0;
        double vol = surf.getVolatility(t, x);
        prices[i][j] = p * (f - d) * BlackFormulaRepository.price(1.0, x, t, vol, isCall);
      }
    }
    return prices;
  }

  /**
   * This is really just testing whether we can recover the flat pure implied volatility surface from the option prices - the code paths after that point are identical 
   */
  @Test
  public void flatSurfaceeTest() {
    EquityVarianceSwapPricer pricer = new EquityVarianceSwapPricer();
    double rv1 = pricer.price(EVS_COR_FRO_DIVS, SPOT, DISCOUNT_CURVE, DIVIDENDS, EXPIRIES, STRIKES, OTM_PRICES_FLAT);
    double rv2 = pricer.price(EVS, SPOT, DISCOUNT_CURVE, DIVIDENDS, EXPIRIES, STRIKES, OTM_PRICES_FLAT);

    //   System.out.println(rv1 + "\t" + rv2);

    EquityVarianceSwapStaticReplication staRep = new EquityVarianceSwapStaticReplication();
    double[] rv = staRep.expectedVariance(SPOT, DISCOUNT_CURVE, DIVIDENDS, EVS.getTimeToObsEnd(), new PureImpliedVolatilitySurface(ConstantDoublesSurface.from(PURE_VOL)));
    //    System.out.println(rv[0] + "\t" + rv[1]);
    assertEquals("div corrected", rv[0], rv1, 1e-10);
    assertEquals("div corrected", rv[1], rv2, 1e-10);
  }

  @Test(enabled = false)
  public void nonFlatSurfaceeTest() {

    // PDEUtilityTools.printSurface("pure", PURE_VOL_SURFACE.getSurface(), 0.01, 2.0, 0.2, 2.5);

    //    ShiftedLogNormalTailExtrapolationFitter fitter = new ShiftedLogNormalTailExtrapolationFitter();
    //  fitter.fitVolatilityAndGrad(1.0,1.2430807745879624 ,vol,volGrad,0.019230769230769232);

    EquityVarianceSwapPricer pricer = new EquityVarianceSwapPricer();
    double rv1 = pricer.price(EVS_COR_FRO_DIVS, SPOT, DISCOUNT_CURVE, DIVIDENDS, EXPIRIES, STRIKES, OTM_PRICES);
    double rv2 = pricer.price(EVS, SPOT, DISCOUNT_CURVE, DIVIDENDS, EXPIRIES, STRIKES, OTM_PRICES);
    double rv3 = pricer.price(EVS, SPOT, DISCOUNT_CURVE, NULL_DIVIDENDS, EXPIRIES, STRIKES, OTM_PRICES);

    System.out.println(rv1 + "\t" + rv2 + "\t" + rv3);

    EquityVarianceSwapStaticReplication staRep = new EquityVarianceSwapStaticReplication();
    double[] rv = staRep.expectedVariance(SPOT, DISCOUNT_CURVE, DIVIDENDS, EVS.getTimeToObsEnd(), PURE_VOL_SURFACE);
    System.out.println(rv[0] + "\t" + rv[1]);
    assertEquals("div corrected", rv[0], rv1, 1e-10);
    assertEquals("div corrected", rv[1], rv2, 1e-10);
  }
}