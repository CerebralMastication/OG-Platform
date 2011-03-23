/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.convention.daycount;

import static org.testng.AssertJUnit.assertEquals;
import org.testng.annotations.Test;


/**
 * Test DayCountFactory.
 */
public class DayCountFactoryTest {

  @Test
  public void testDayCountFactory() {
    final DayCount u30_360 = new ThirtyUThreeSixty();
    assertEquals(u30_360, DayCountFactory.INSTANCE.getDayCount("30/360"));
    assertEquals(u30_360, DayCountFactory.INSTANCE.getDayCount("360/360"));
    final DayCount a365f = new ActualThreeSixtyFive();
    assertEquals(a365f, DayCountFactory.INSTANCE.getDayCount("A/365F"));
    final DayCount oneone = new OneOneDayCount();
    assertEquals(oneone, DayCountFactory.INSTANCE.getDayCount("1/1"));
    final DayCount thirtyE = new ThirtyEThreeSixty();
    assertEquals(thirtyE, DayCountFactory.INSTANCE.getDayCount("30E/360"));
    assertEquals(thirtyE, DayCountFactory.INSTANCE.getDayCount("EuroBond Basis"));
  }

}
