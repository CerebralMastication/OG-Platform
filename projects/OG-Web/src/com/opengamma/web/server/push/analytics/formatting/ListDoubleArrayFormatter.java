/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.web.server.push.analytics.formatting;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opengamma.engine.value.ValueSpecification;

/**
 *
 */
/* package */ class ListDoubleArrayFormatter extends NoHistoryFormatter<List> {

  private static final Logger s_logger = LoggerFactory.getLogger(ListDoubleArrayFormatter.class);

  @Override
  public Object formatForDisplay(List value, ValueSpecification valueSpec) {
    int rowCount = value.size();
    int colCount;
    if (rowCount == 0) {
      colCount = 0;
    } else {
      if (value.get(0).getClass().equals(double[].class)) {
        colCount = ((double[]) value.get(0)).length;
      } else if (value.get(0).getClass().equals(Double[].class)) {
        colCount = ((Double[]) value.get(0)).length;
      } else {
        s_logger.warn("Unexpected type in list: {}", value.get(0).getClass());
        return FORMATTING_ERROR;
      }
    }
    return "Matrix (" + rowCount + " x " + colCount + ")";
  }

  @Override
  public Object formatForExpandedDisplay(List value, ValueSpecification valueSpec) {
    // TODO implement formatForExpandedDisplay()
    throw new UnsupportedOperationException("formatForExpandedDisplay not implemented");
  }

  @Override
  public FormatType getFormatType() {
    return FormatType.LABELLED_MATRIX_2D;
  }
}
