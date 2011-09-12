package org.kuali.rice.kew.api.rule;

import org.kuali.rice.core.api.mo.common.GloballyUnique;
import org.kuali.rice.core.api.mo.common.Identifiable;
import org.kuali.rice.core.api.mo.common.Versioned;
import org.kuali.rice.core.api.mo.common.active.Inactivatable;
import org.kuali.rice.kew.api.extension.ExtensionDefinitionContract;

import java.util.Map;

public interface RuleTemplateAttributeContract extends Identifiable, Versioned, GloballyUnique, Inactivatable {
	String getRuleTemplateId();
	boolean isRequired();
	Integer getDisplayOrder();
	String getDefaultValue();

	ExtensionDefinitionContract getRuleAttribute();
	Map<String, String> getRuleExtensionMap();
}
