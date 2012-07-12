/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.web.server.push.analytics.formatting;

import com.opengamma.engine.value.ValueSpecification;

/**
 *
 */
/* package */ class NullFormatter extends NoHistoryFormatter<Object> {

  @Override
  public String formatForDisplay(Object value, ValueSpecification valueSpec) {
    return null;
  }

  @Override
  public Object formatForExpandedDisplay(Object value, ValueSpecification valueSpec) {
    return null;
  }

  @Override
  public FormatType getFormatType() {
    return FormatType.PRIMITIVE;
  }
}