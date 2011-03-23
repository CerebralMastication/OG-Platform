/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.model.option.pricing.analytic;

import org.testng.annotations.Test;

/**
 * 
 */
public class JuZhongModelTest extends AmericanAnalyticOptionModelTest {

  @Test
  public void test() {
    super.test(new JuZhongModel(), 10);
  }
}
