/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.analytics.financial.instrument.payment;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;

import javax.time.calendar.Period;
import javax.time.calendar.ZonedDateTime;

import org.testng.annotations.Test;

import com.opengamma.analytics.financial.instrument.index.IborIndex;
import com.opengamma.analytics.financial.interestrate.payments.derivative.CouponFixed;
import com.opengamma.analytics.financial.interestrate.payments.derivative.CouponIbor;
import com.opengamma.analytics.financial.interestrate.payments.derivative.Payment;
import com.opengamma.analytics.financial.schedule.ScheduleCalculator;
import com.opengamma.analytics.util.time.TimeCalculator;
import com.opengamma.financial.convention.businessday.BusinessDayConvention;
import com.opengamma.financial.convention.businessday.BusinessDayConventionFactory;
import com.opengamma.financial.convention.calendar.Calendar;
import com.opengamma.financial.convention.calendar.MondayToFridayCalendar;
import com.opengamma.financial.convention.daycount.DayCount;
import com.opengamma.financial.convention.daycount.DayCountFactory;
import com.opengamma.util.money.Currency;
import com.opengamma.util.time.DateUtils;
import com.opengamma.util.timeseries.DoubleTimeSeries;
import com.opengamma.util.timeseries.zoneddatetime.ArrayZonedDateTimeDoubleTimeSeries;

/**
 * 
 */
public class CouponIborDefinitionTest {

  private static final Period TENOR = Period.ofMonths(3);
  private static final int SETTLEMENT_DAYS = 2;
  private static final Calendar CALENDAR = new MondayToFridayCalendar("A");
  private static final DayCount DAY_COUNT_INDEX = DayCountFactory.INSTANCE.getDayCount("Actual/360");
  private static final BusinessDayConvention BUSINESS_DAY = BusinessDayConventionFactory.INSTANCE.getBusinessDayConvention("Modified Following");
  private static final boolean IS_EOM = true;
  private static final Currency CUR = Currency.USD;
  private static final IborIndex INDEX = new IborIndex(CUR, TENOR, SETTLEMENT_DAYS, CALENDAR, DAY_COUNT_INDEX, BUSINESS_DAY, IS_EOM);

  private static final ZonedDateTime FIXING_DATE = DateUtils.getUTCDate(2011, 1, 3);
  private static final ZonedDateTime ACCRUAL_START_DATE = DateUtils.getUTCDate(2011, 1, 6);
  private static final ZonedDateTime ACCRUAL_END_DATE = DateUtils.getUTCDate(2011, 4, 4);
  private static final ZonedDateTime PAYMENT_DATE = DateUtils.getUTCDate(2011, 4, 6);
  // The above dates are not standard but selected for insure correct testing.
  private static final ZonedDateTime FIXING_START_DATE = ScheduleCalculator.getAdjustedDate(FIXING_DATE, SETTLEMENT_DAYS, CALENDAR);
  private static final ZonedDateTime FIXING_END_DATE = ScheduleCalculator.getAdjustedDate(FIXING_START_DATE, TENOR, BUSINESS_DAY, CALENDAR, IS_EOM);

  private static final DayCount DAY_COUNT_PAYMENT = DayCountFactory.INSTANCE.getDayCount("Actual/365");
  private static final double ACCRUAL_FACTOR = DAY_COUNT_PAYMENT.getDayCountFraction(ACCRUAL_START_DATE, ACCRUAL_END_DATE);
  private static final double ACCRUAL_FACTOR_FIXING = DAY_COUNT_INDEX.getDayCountFraction(FIXING_START_DATE, FIXING_END_DATE);
  private static final double NOTIONAL = 1000000; //1m

  // Coupon with specific payment and accrual dates.
  private static final CouponIborDefinition IBOR_COUPON_DEFINITION = CouponIborDefinition.from(PAYMENT_DATE, ACCRUAL_START_DATE, ACCRUAL_END_DATE, ACCRUAL_FACTOR, NOTIONAL, FIXING_DATE, INDEX);
  // Coupon with standard payment and accrual dates.
  private static final CouponIborDefinition IBOR_COUPON_DEFINITION_2 = CouponIborDefinition.from(NOTIONAL, FIXING_DATE, INDEX);
  private static final double FIXING_RATE = 0.04;
  private static final DoubleTimeSeries<ZonedDateTime> FIXING_TS = new ArrayZonedDateTimeDoubleTimeSeries(new ZonedDateTime[] {FIXING_DATE}, new double[] {FIXING_RATE});
  private static final ZonedDateTime REFERENCE_DATE = DateUtils.getUTCDate(2010, 12, 27); //For conversion to derivative

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testDifferentCurrencies() {
    new CouponIborDefinition(Currency.AUD, PAYMENT_DATE, ACCRUAL_START_DATE, ACCRUAL_END_DATE, ACCRUAL_FACTOR, NOTIONAL, FIXING_DATE, INDEX);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNullIndex1() {
    new CouponIborDefinition(CUR, PAYMENT_DATE, ACCRUAL_START_DATE, ACCRUAL_END_DATE, ACCRUAL_FACTOR, NOTIONAL, FIXING_DATE, null);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNullPaymentDate() {
    CouponIborDefinition.from(null, ACCRUAL_START_DATE, ACCRUAL_END_DATE, ACCRUAL_FACTOR, NOTIONAL, FIXING_DATE, INDEX);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNullAccrualStartDate() {
    CouponIborDefinition.from(PAYMENT_DATE, null, ACCRUAL_END_DATE, ACCRUAL_FACTOR, NOTIONAL, FIXING_DATE, INDEX);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNullAccrualEndDate() {
    CouponIborDefinition.from(PAYMENT_DATE, ACCRUAL_START_DATE, null, ACCRUAL_FACTOR, NOTIONAL, FIXING_DATE, INDEX);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNullFixingDate() {
    CouponIborDefinition.from(PAYMENT_DATE, ACCRUAL_START_DATE, ACCRUAL_END_DATE, ACCRUAL_FACTOR, NOTIONAL, null, INDEX);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNullIndex2() {
    CouponIborDefinition.from(PAYMENT_DATE, ACCRUAL_START_DATE, ACCRUAL_END_DATE, ACCRUAL_FACTOR, NOTIONAL, FIXING_DATE, null);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testFromNullFixingDate() {
    CouponIborDefinition.from(NOTIONAL, null, INDEX);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testFromNullIndex() {
    CouponIborDefinition.from(NOTIONAL, FIXING_DATE, null);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testFixingAfterPayment() {
    CouponIborDefinition.from(FIXING_DATE.minusDays(1), ACCRUAL_START_DATE, ACCRUAL_END_DATE, ACCRUAL_FACTOR, NOTIONAL, FIXING_DATE, INDEX);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testConversionAfterFixingNoData() {
    IBOR_COUPON_DEFINITION.toDerivative(FIXING_DATE.plusDays(3), new String[] {"A", "B"});
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testConversionNullFixingData() {
    IBOR_COUPON_DEFINITION.toDerivative(FIXING_DATE, null, new String[] {"A", "S"});
  }

  @Test
  public void test() {
    assertEquals(IBOR_COUPON_DEFINITION.getPaymentDate(), PAYMENT_DATE);
    assertEquals(IBOR_COUPON_DEFINITION.getAccrualStartDate(), ACCRUAL_START_DATE);
    assertEquals(IBOR_COUPON_DEFINITION.getAccrualEndDate(), ACCRUAL_END_DATE);
    assertEquals(IBOR_COUPON_DEFINITION.getPaymentYearFraction(), ACCRUAL_FACTOR, 1E-10);
    assertEquals(IBOR_COUPON_DEFINITION.getNotional(), NOTIONAL, 1E-2);
    assertEquals(IBOR_COUPON_DEFINITION.getFixingDate(), FIXING_DATE);
    assertEquals(IBOR_COUPON_DEFINITION.getFixingPeriodStartDate(), FIXING_START_DATE);
    assertEquals(IBOR_COUPON_DEFINITION.getFixingPeriodEndDate(), FIXING_END_DATE);
    assertEquals(IBOR_COUPON_DEFINITION.getFixingPeriodAccrualFactor(), ACCRUAL_FACTOR_FIXING, 1E-10);
    assertEquals(IBOR_COUPON_DEFINITION_2.getPaymentDate(), FIXING_END_DATE);
    assertEquals(IBOR_COUPON_DEFINITION_2.getAccrualStartDate(), FIXING_START_DATE);
    assertEquals(IBOR_COUPON_DEFINITION_2.getAccrualEndDate(), FIXING_END_DATE);
    assertEquals(IBOR_COUPON_DEFINITION_2.getPaymentYearFraction(), ACCRUAL_FACTOR_FIXING, 1E-10);
    assertEquals(IBOR_COUPON_DEFINITION_2.getNotional(), NOTIONAL, 1E-2);
    assertEquals(IBOR_COUPON_DEFINITION_2.getFixingDate(), FIXING_DATE);
    assertEquals(IBOR_COUPON_DEFINITION_2.getFixingPeriodStartDate(), FIXING_START_DATE);
    assertEquals(IBOR_COUPON_DEFINITION_2.getFixingPeriodEndDate(), FIXING_END_DATE);
    assertEquals(IBOR_COUPON_DEFINITION_2.getFixingPeriodAccrualFactor(), ACCRUAL_FACTOR_FIXING, 1E-10);
  }

  @Test
  public void testEqualHash() {
    CouponIborDefinition other = new CouponIborDefinition(CUR, PAYMENT_DATE, ACCRUAL_START_DATE, ACCRUAL_END_DATE, ACCRUAL_FACTOR, NOTIONAL, FIXING_DATE, INDEX);
    assertEquals(IBOR_COUPON_DEFINITION, other);
    assertEquals(IBOR_COUPON_DEFINITION.hashCode(), other.hashCode());
    other = new CouponIborDefinition(Currency.AUD, PAYMENT_DATE, ACCRUAL_START_DATE, ACCRUAL_END_DATE, ACCRUAL_FACTOR, NOTIONAL, FIXING_DATE, new IborIndex(Currency.AUD, TENOR, SETTLEMENT_DAYS,
        CALENDAR, DAY_COUNT_INDEX, BUSINESS_DAY, IS_EOM));
    assertFalse(IBOR_COUPON_DEFINITION.equals(other));
    other = new CouponIborDefinition(CUR, PAYMENT_DATE.plusDays(1), ACCRUAL_START_DATE, ACCRUAL_END_DATE, ACCRUAL_FACTOR, NOTIONAL, FIXING_DATE, INDEX);
    assertFalse(IBOR_COUPON_DEFINITION.equals(other));
    other = new CouponIborDefinition(CUR, PAYMENT_DATE, ACCRUAL_START_DATE.plusDays(1), ACCRUAL_END_DATE, ACCRUAL_FACTOR, NOTIONAL, FIXING_DATE, INDEX);
    assertFalse(IBOR_COUPON_DEFINITION.equals(other));
    other = new CouponIborDefinition(CUR, PAYMENT_DATE, ACCRUAL_START_DATE, ACCRUAL_END_DATE.plusDays(1), ACCRUAL_FACTOR, NOTIONAL, FIXING_DATE, INDEX);
    assertFalse(IBOR_COUPON_DEFINITION.equals(other));
    other = new CouponIborDefinition(CUR, PAYMENT_DATE, ACCRUAL_START_DATE, ACCRUAL_END_DATE, ACCRUAL_FACTOR + 0.01, NOTIONAL, FIXING_DATE, INDEX);
    assertFalse(IBOR_COUPON_DEFINITION.equals(other));
    other = new CouponIborDefinition(CUR, PAYMENT_DATE, ACCRUAL_START_DATE, ACCRUAL_END_DATE, ACCRUAL_FACTOR, NOTIONAL + 100, FIXING_DATE, INDEX);
    assertFalse(IBOR_COUPON_DEFINITION.equals(other));
    other = new CouponIborDefinition(CUR, PAYMENT_DATE, ACCRUAL_START_DATE, ACCRUAL_END_DATE, ACCRUAL_FACTOR, NOTIONAL, FIXING_DATE.plusDays(1), INDEX);
    assertFalse(IBOR_COUPON_DEFINITION.equals(other));
    other = new CouponIborDefinition(CUR, PAYMENT_DATE, ACCRUAL_START_DATE, ACCRUAL_END_DATE, ACCRUAL_FACTOR, NOTIONAL, FIXING_DATE, new IborIndex(CUR, TENOR, SETTLEMENT_DAYS + 1, CALENDAR,
        DAY_COUNT_INDEX, BUSINESS_DAY, IS_EOM));
    assertFalse(IBOR_COUPON_DEFINITION.equals(other));
    other = new CouponIborDefinition(CUR, FIXING_END_DATE, FIXING_START_DATE, FIXING_END_DATE, ACCRUAL_FACTOR_FIXING, NOTIONAL, FIXING_DATE, INDEX);
    assertEquals(IBOR_COUPON_DEFINITION_2, other);
    assertEquals(IBOR_COUPON_DEFINITION_2.hashCode(), other.hashCode());
  }

  @Test
  public void testToDerivativeBeforeFixing() {
    final DayCount actAct = DayCountFactory.INSTANCE.getDayCount("Actual/Actual ISDA");
    final double paymentTime = actAct.getDayCountFraction(REFERENCE_DATE, PAYMENT_DATE);
    final double fixingTime = actAct.getDayCountFraction(REFERENCE_DATE, FIXING_DATE);
    final double fixingPeriodStartTime = actAct.getDayCountFraction(REFERENCE_DATE, IBOR_COUPON_DEFINITION.getFixingPeriodStartDate());
    final double fixingPeriodEndTime = actAct.getDayCountFraction(REFERENCE_DATE, IBOR_COUPON_DEFINITION.getFixingPeriodEndDate());
    final String fundingCurve = "Funding";
    final String forwardCurve = "Forward";
    final String[] curves = {fundingCurve, forwardCurve};
    final CouponIbor couponIbor = new CouponIbor(CUR, paymentTime, fundingCurve, ACCRUAL_FACTOR, NOTIONAL, fixingTime, INDEX, fixingPeriodStartTime, fixingPeriodEndTime, ACCRUAL_FACTOR_FIXING,
        forwardCurve);
    CouponIbor convertedDefinition = (CouponIbor) IBOR_COUPON_DEFINITION.toDerivative(REFERENCE_DATE, curves);
    assertEquals(couponIbor, convertedDefinition);
    convertedDefinition = (CouponIbor) IBOR_COUPON_DEFINITION.toDerivative(REFERENCE_DATE, FIXING_TS, curves);
    assertEquals(couponIbor, convertedDefinition);
  }

  @Test
  /**
   * Tests the toDerivative method where the fixing date before the current date.
   */
  public void testToDerivativeAfterFixing() {
    final ZonedDateTime referenceDate = DateUtils.getUTCDate(2011, 1, 10, 12, 0);
    final double paymentTime = TimeCalculator.getTimeBetween(referenceDate, IBOR_COUPON_DEFINITION.getPaymentDate());
    final String fundingCurve = "Funding";
    final String forwardCurve = "Forward";
    final String[] curves = {fundingCurve, forwardCurve};
    final CouponFixed coupon = new CouponFixed(CUR, paymentTime, fundingCurve, ACCRUAL_FACTOR, NOTIONAL, FIXING_RATE);
    final Payment couponConverted = IBOR_COUPON_DEFINITION.toDerivative(referenceDate, FIXING_TS, curves);
    assertEquals("CouponIborDefinition: toDerivative", coupon, couponConverted);
  }

  @Test
  /**
   * Tests the toDerivative method where the fixing date is equal to the current date.
   */
  public void testToDerivativeOnFixing() {
    final ZonedDateTime referenceDate = DateUtils.getUTCDate(2011, 1, 3, 11, 11);
    final double paymentTime = TimeCalculator.getTimeBetween(referenceDate, IBOR_COUPON_DEFINITION.getPaymentDate());
    final double fixingTime = TimeCalculator.getTimeBetween(referenceDate, FIXING_DATE);
    final double fixingPeriodStartTime = TimeCalculator.getTimeBetween(referenceDate, IBOR_COUPON_DEFINITION.getFixingPeriodStartDate());
    final double fixingPeriodEndTime = TimeCalculator.getTimeBetween(referenceDate, IBOR_COUPON_DEFINITION.getFixingPeriodEndDate());
    final String fundingCurve = "Funding";
    final String forwardCurve = "Forward";
    final String[] curves = {fundingCurve, forwardCurve};
    // The fixing is known
    final CouponFixed coupon = new CouponFixed(CUR, paymentTime, fundingCurve, ACCRUAL_FACTOR, NOTIONAL, FIXING_RATE);
    final Payment couponConverted = IBOR_COUPON_DEFINITION.toDerivative(referenceDate, FIXING_TS, curves);
    assertEquals(coupon, couponConverted);
    // The fixing is not known
    final DoubleTimeSeries<ZonedDateTime> fixingTS2 = new ArrayZonedDateTimeDoubleTimeSeries(new ZonedDateTime[] {ScheduleCalculator.getAdjustedDate(FIXING_DATE, -1, CALENDAR)},
        new double[] {FIXING_RATE});
    final CouponIbor coupon2 = new CouponIbor(CUR, paymentTime, fundingCurve, ACCRUAL_FACTOR, NOTIONAL, fixingTime, INDEX, fixingPeriodStartTime, fixingPeriodEndTime,
        IBOR_COUPON_DEFINITION.getFixingPeriodAccrualFactor(), forwardCurve);
    final Payment couponConverted2 = IBOR_COUPON_DEFINITION.toDerivative(referenceDate, fixingTS2, curves);
    assertEquals("CouponIborDefinition: toDerivative", coupon2, couponConverted2);
    final Payment couponConverted3 = IBOR_COUPON_DEFINITION.toDerivative(referenceDate, curves);
    assertEquals("CouponIborDefinition: toDerivative", coupon2, couponConverted3);
  }

  //  @Test
  //  public void testToDerivativeAfterFixing() {
  //    final ZonedDateTime date = FIXING_DATE.plusDays(2);
  //    double paymentTime = TimeCalculator.getTimeBetween(date, PAYMENT_DATE);
  //    final String fundingCurve = "Funding";
  //    final String forwardCurve = "Forward";
  //    final String[] curves = {fundingCurve, forwardCurve};
  //
  //    final double fixingTime = 0.0;
  //    double fixingPeriodStartTime;
  //    if (date.isBefore(IBOR_COUPON.getFixingPeriodStartDate())) {
  //      fixingPeriodStartTime = TimeCalculator.getTimeBetween(date, IBOR_COUPON.getFixingPeriodStartDate());
  //    } else {
  //      fixingPeriodStartTime = 0.0;
  //    }
  //    double fixingPeriodEndTime = TimeCalculator.getTimeBetween(date, IBOR_COUPON.getFixingPeriodEndDate());
  //
  //    CouponIborFixed couponIborFixed = new CouponIborFixed(CUR, paymentTime, fundingCurve, ACCRUAL_FACTOR, NOTIONAL, FIXING_RATE, fixingTime, INDEX, fixingPeriodStartTime, fixingPeriodEndTime,
  //        ACCRUAL_FACTOR_FIXING, 0, forwardCurve);
  //    CouponFixed convertedDefinition = (CouponFixed) IBOR_COUPON.toDerivative(date, FIXING_TS, curves);
  //    assertEquals("CouponIbor: toDerivative", couponIborFixed, convertedDefinition);
  //
  //    paymentTime = TimeCalculator.getTimeBetween(FIXING_DATE, PAYMENT_DATE);
  //    if (FIXING_DATE.isBefore(IBOR_COUPON.getFixingPeriodStartDate())) {
  //      fixingPeriodStartTime = TimeCalculator.getTimeBetween(FIXING_DATE, IBOR_COUPON.getFixingPeriodStartDate());
  //    } else {
  //      fixingPeriodStartTime = 0.0;
  //    }
  //    fixingPeriodEndTime = TimeCalculator.getTimeBetween(FIXING_DATE, IBOR_COUPON.getFixingPeriodEndDate());
  //    couponIborFixed = new CouponIborFixed(CUR, paymentTime, fundingCurve, ACCRUAL_FACTOR, NOTIONAL, FIXING_RATE, fixingTime, INDEX, fixingPeriodStartTime, fixingPeriodEndTime, ACCRUAL_FACTOR_FIXING,
  //        0, forwardCurve);
  //    convertedDefinition = (CouponFixed) IBOR_COUPON.toDerivative(FIXING_DATE, FIXING_TS, curves);
  //    assertEquals(couponIborFixed, convertedDefinition);
  //  }
  //
  //  @SuppressWarnings("unused")
  //  @Test
  //  /**
  //   * Tests the toDerivative method where the fixing date is equal to the current date.
  //   */
  //  public void testToDerivativeOnFixing() {
  //    final DayCount actAct = DayCountFactory.INSTANCE.getDayCount("Actual/Actual ISDA");
  //    final ZonedDateTime referenceDate = FIXING_DATE;
  //    final double paymentTime = actAct.getDayCountFraction(referenceDate, PAYMENT_DATE);
  //    final double fixingTime = actAct.getDayCountFraction(referenceDate, FIXING_DATE);
  //    final double fixingPeriodStartTime = actAct.getDayCountFraction(referenceDate, FIXING_START_DATE);
  //    final double fixingPeriodEndTime = actAct.getDayCountFraction(referenceDate, FIXING_END_DATE);
  //    final String fundingCurve = "Funding";
  //    final String forwardCurve = "Forward";
  //    final String[] curves = {fundingCurve, forwardCurve};
  //    // The fixing is known
  //    // final CouponFixed coupon = new CouponFixed(CUR, paymentTime, fundingCurve, ACCRUAL_FACTOR, NOTIONAL, FIXING_RATE);
  //    final CouponIborFixed coupon = new CouponIborFixed(CUR, paymentTime, fundingCurve, ACCRUAL_FACTOR, NOTIONAL, FIXING_RATE, fixingTime, INDEX, fixingPeriodStartTime, fixingPeriodEndTime,
  //        ACCRUAL_FACTOR_FIXING, 0, forwardCurve);
  //    final Payment couponConverted = IBOR_COUPON.toDerivative(referenceDate, FIXING_TS, curves);
  //    assertEquals(coupon, couponConverted);
  //    //    // The fixing is not known
  //    final DoubleTimeSeries<ZonedDateTime> fixingTS2 = new ArrayZonedDateTimeDoubleTimeSeries(new ZonedDateTime[] {ScheduleCalculator.getAdjustedDate(FIXING_DATE, -1, CALENDAR)},
  //        new double[] {FIXING_RATE});
  //    //final CouponIbor coupon2 = new CouponIbor(CUR, paymentTime, fundingCurve, ACCRUAL_FACTOR, NOTIONAL, fixingTime, fixingPeriodStartTime, fixingPeriodEndTime, ACCRUAL_FACTOR_FIXING, forwardCurve);
  //    final Payment couponConverted2 = IBOR_COUPON.toDerivative(referenceDate, fixingTS2, curves);
  //    //assertEquals(coupon2, couponConverted2);
  //    final Payment couponConverted3 = IBOR_COUPON.toDerivative(referenceDate, curves);
  //    //assertEquals(coupon2, couponConverted3);
  //  }

}
