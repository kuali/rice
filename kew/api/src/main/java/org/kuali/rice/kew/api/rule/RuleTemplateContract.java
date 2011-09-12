package org.kuali.rice.kew.api.rule;

import org.kuali.rice.core.api.mo.common.GloballyUnique;
import org.kuali.rice.core.api.mo.common.Identifiable;
import org.kuali.rice.core.api.mo.common.Versioned;

import java.util.List;

public interface RuleTemplateContract extends Identifiable, Versioned, GloballyUnique {
    String getName();
    String getDescription();
    RuleTemplateContract getDelegationTemplate();
    List<? extends RuleTemplateAttributeContract> getRuleTemplateAttributes();
    List<? extends RuleTemplateOptionContract> getRuleTemplateOptions();
}
