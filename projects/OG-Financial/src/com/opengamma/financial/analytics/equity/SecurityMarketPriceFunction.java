/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.analytics.equity;

import java.util.Collections;
import java.util.Set;

import com.opengamma.core.value.MarketDataRequirementNames;
import com.opengamma.engine.ComputationTarget;
import com.opengamma.engine.ComputationTargetType;
import com.opengamma.engine.function.AbstractFunction;
import com.opengamma.engine.function.FunctionCompilationContext;
import com.opengamma.engine.function.FunctionExecutionContext;
import com.opengamma.engine.function.FunctionInputs;
import com.opengamma.engine.value.ComputedValue;
import com.opengamma.engine.value.ValueProperties;
import com.opengamma.engine.value.ValuePropertyNames;
import com.opengamma.engine.value.ValueRequirement;
import com.opengamma.engine.value.ValueRequirementNames;
import com.opengamma.engine.value.ValueSpecification;
import com.opengamma.financial.security.FinancialSecurity;
import com.opengamma.financial.security.FinancialSecurityUtils;
import com.opengamma.financial.security.MarketSecurityVisitor;
import com.opengamma.util.money.Currency;

/**
 * Provides the market price for the security of a position as a value on the position
 */
public class SecurityMarketPriceFunction extends AbstractFunction.NonCompiledInvoker {

  private static MarketSecurityVisitor s_judgeOfMarketSecurities = new MarketSecurityVisitor();

  @Override
  public Set<ComputedValue> execute(FunctionExecutionContext executionContext, FunctionInputs inputs,
      ComputationTarget target, Set<ValueRequirement> desiredValues) {
    double marketValue = (Double) inputs.getValue(getRequirement(target));
    return Collections.singleton(new ComputedValue(getSpecification(target), marketValue));
  }

  @Override
  public ComputationTargetType getTargetType() {
    return ComputationTargetType.POSITION;
  }

  @Override
  public boolean canApplyTo(FunctionCompilationContext context, ComputationTarget target) {
    if (!(target.getPosition().getSecurity() instanceof FinancialSecurity)) {
      return false;
    }
    final FinancialSecurity security = (FinancialSecurity) target.getPosition().getSecurity();
    return security.accept(s_judgeOfMarketSecurities);
  }

  @Override
  public Set<ValueRequirement> getRequirements(FunctionCompilationContext context, ComputationTarget target, ValueRequirement desiredValue) {
    ValueRequirement valueRequirement = getRequirement(target);
    return Collections.singleton(valueRequirement);
  }

  @Override
  public Set<ValueSpecification> getResults(FunctionCompilationContext context, ComputationTarget target) {
    ValueSpecification spec = getSpecification(target);
    return Collections.singleton(spec);
  }

  private ValueSpecification getSpecification(ComputationTarget target) {
    final Currency ccy = FinancialSecurityUtils.getCurrency(target.getPosition().getSecurity());
    ValueProperties valueProperties;
    if (ccy == null) {
      valueProperties = createValueProperties().get();
    } else {
      valueProperties = createValueProperties().with(ValuePropertyNames.CURRENCY, ccy.getCode()).get();
    }
    return new ValueSpecification(new ValueRequirement(ValueRequirementNames.SECURITY_MARKET_PRICE,
        target.getPosition(), valueProperties), getUniqueId());
  }

  private ValueRequirement getRequirement(ComputationTarget target) {
    return new ValueRequirement(MarketDataRequirementNames.MARKET_VALUE, target.getPosition().getSecurity());
    // TESTING: The following will use BLOOMBERG_TICKER_WEAK, hence will not put a strain on the amount of data requested from BBG in a day
    // ValueRequirement(MarketDataRequirementNames.MARKET_VALUE, 
    //     ExternalId.of(ExternalSchemes.BLOOMBERG_TICKER_WEAK, target.getPosition().getSecurity().getExternalIdBundle().getValue(ExternalSchemes.BLOOMBERG_TICKER)));
  }

}
