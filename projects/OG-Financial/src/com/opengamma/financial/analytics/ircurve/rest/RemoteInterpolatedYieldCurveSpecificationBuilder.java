/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.analytics.ircurve.rest;

import java.net.URI;

import javax.time.calendar.LocalDate;

import com.opengamma.financial.analytics.ircurve.InterpolatedYieldCurveSpecification;
import com.opengamma.financial.analytics.ircurve.InterpolatedYieldCurveSpecificationBuilder;
import com.opengamma.financial.analytics.ircurve.YieldCurveDefinition;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.rest.AbstractRemoteClient;

/**
 * Provides access to a remote {@link InterpolatedYieldCurveSpecificationBuilder}.
 */
public class RemoteInterpolatedYieldCurveSpecificationBuilder extends AbstractRemoteClient implements InterpolatedYieldCurveSpecificationBuilder {

  /**
   * Creates an instance.
   * 
   * @param baseUri  the base target URI for all RESTful web services, not null
   */
  public RemoteInterpolatedYieldCurveSpecificationBuilder(final URI baseUri) {
    super(baseUri);
  }

  //-------------------------------------------------------------------------
  @Override
  public InterpolatedYieldCurveSpecification buildCurve(LocalDate curveDate, YieldCurveDefinition curveDefinition) {
    ArgumentChecker.notNull(curveDate, "curveDate");
    ArgumentChecker.notNull(curveDefinition, "curveDefinition");
    
    URI uri = DataInterpolatedYieldCurveSpecificationBuilderResource.uriBuildCurve(getBaseUri(), curveDate);
    return accessRemote(uri).post(InterpolatedYieldCurveSpecification.class, curveDefinition);
  }

}
