package org.kuali.rice.kew.api.rule;

import org.kuali.rice.core.api.mo.common.GloballyUnique;
import org.kuali.rice.core.api.mo.common.Identifiable;
import org.kuali.rice.core.api.mo.common.Versioned;

public interface RuleExpressionContract extends Identifiable, Versioned, GloballyUnique {
    String getType();
    String getExpression();
}
