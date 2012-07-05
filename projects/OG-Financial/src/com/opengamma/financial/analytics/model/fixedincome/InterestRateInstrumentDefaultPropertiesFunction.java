/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.analytics.model.fixedincome;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opengamma.engine.ComputationTarget;
import com.opengamma.engine.ComputationTargetType;
import com.opengamma.engine.function.FunctionCompilationContext;
import com.opengamma.engine.value.ValuePropertyNames;
import com.opengamma.engine.value.ValueRequirement;
import com.opengamma.engine.value.ValueRequirementNames;
import com.opengamma.financial.analytics.OpenGammaFunctionExclusions;
import com.opengamma.financial.analytics.fixedincome.InterestRateInstrumentType;
import com.opengamma.financial.property.DefaultPropertyFunction;
import com.opengamma.financial.security.FinancialSecurity;
import com.opengamma.financial.security.FinancialSecurityUtils;
import com.opengamma.financial.security.future.InterestRateFutureSecurity;
import com.opengamma.financial.security.swap.SwapSecurity;
import com.opengamma.util.ArgumentChecker;

/**
 * Dummy function for injecting default curve names into the dependency graph.
 */
public class InterestRateInstrumentDefaultPropertiesFunction extends DefaultPropertyFunction {
  private static final Logger s_logger = LoggerFactory.getLogger(InterestRateInstrumentDefaultPropertiesFunction.class);
  private static final String[] s_valueNames = new String[] {
    ValueRequirementNames.PRESENT_VALUE,
    ValueRequirementNames.PAR_RATE,
    ValueRequirementNames.PAR_RATE_CURVE_SENSITIVITY,
    ValueRequirementNames.PAR_RATE_PARALLEL_CURVE_SHIFT,
    ValueRequirementNames.PV01,
    ValueRequirementNames.YIELD_CURVE_NODE_SENSITIVITIES,
    ValueRequirementNames.VALUE_THETA};
  private final PriorityClass _priority;
  private final boolean _includeIRFutures;
  private final Map<String, String> _currencyAndCurveConfigNames;

  public InterestRateInstrumentDefaultPropertiesFunction(final String priority, final String includeIRFutures, final String... currencyAndCurveConfigNames) {
    super(ComputationTargetType.SECURITY, true);
    ArgumentChecker.notNull(priority, "priority");
    ArgumentChecker.notNull(includeIRFutures, "include IR futures field");
    ArgumentChecker.notNull(currencyAndCurveConfigNames, "currency and curve config names");
    final int nPairs = currencyAndCurveConfigNames.length;
    ArgumentChecker.isTrue(nPairs % 2 == 0, "Must have one curve config name per currency");
    _priority = PriorityClass.valueOf(priority);
    _includeIRFutures = Boolean.parseBoolean(includeIRFutures);
    _currencyAndCurveConfigNames = new HashMap<String, String>();
    for (int i = 0; i < currencyAndCurveConfigNames.length; i += 2) {
      _currencyAndCurveConfigNames.put(currencyAndCurveConfigNames[i], currencyAndCurveConfigNames[i + 1]);
    }
  }

  @Override
  public boolean canApplyTo(final FunctionCompilationContext context, final ComputationTarget target) {
    if (!(target.getSecurity() instanceof FinancialSecurity)) {
      return false;
    }
    final FinancialSecurity security = (FinancialSecurity) target.getSecurity();
    if (!_includeIRFutures && security instanceof InterestRateFutureSecurity) {
      return false;
    }
    if (!InterestRateInstrumentType.isFixedIncomeInstrumentType(security)) {
      return false;
    }
    final String currencyName = FinancialSecurityUtils.getCurrency(security).getCode();
    if (!_currencyAndCurveConfigNames.containsKey(currencyName)) {
      return false;
    }
    if (security instanceof SwapSecurity) {
      final InterestRateInstrumentType type = InterestRateInstrumentType.getInstrumentTypeFromSecurity(security);
      if (type == InterestRateInstrumentType.SWAP_FIXED_IBOR || type == InterestRateInstrumentType.SWAP_FIXED_IBOR_WITH_SPREAD
          || type == InterestRateInstrumentType.SWAP_IBOR_IBOR || type == InterestRateInstrumentType.SWAP_FIXED_OIS) {
        return true;
      }
    }
    return true;
  }

  @Override
  protected void getDefaults(final PropertyDefaults defaults) {
    for (final String valueName : s_valueNames) {
      defaults.addValuePropertyName(valueName, ValuePropertyNames.CURVE_CALCULATION_CONFIG);
    }
  }

  @Override
  protected Set<String> getDefaultValue(final FunctionCompilationContext context, final ComputationTarget target, final ValueRequirement desiredValue, final String propertyName) {
    if (ValuePropertyNames.CURVE_CALCULATION_CONFIG.equals(propertyName)) {
      final String currencyName = FinancialSecurityUtils.getCurrency(target.getSecurity()).getCode();
      final String configName = _currencyAndCurveConfigNames.get(currencyName);
      if (configName == null) {
        s_logger.error("Could not get config for currency " + currencyName + "; should never happen");
        return null;
      }
      return Collections.singleton(configName);
    }
    return null;
  }

  @Override
  public PriorityClass getPriority() {
    return _priority;
  }

  @Override
  public String getMutualExclusionGroup() {
    return OpenGammaFunctionExclusions.INTEREST_RATE_INSTRUMENT_DEFAULTS;
  }
}