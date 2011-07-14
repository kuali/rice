package org.kuali.rice.core.api.uif.control;

import org.kuali.rice.core.api.uif.control.widget.AbstractWidget;

import java.util.Collection;

public interface AbstractControlContract {
    
    String getName();
    String getShortLabel();
    String getLongLabel();
    String getHelpSummary();
    String getHelpConstraInteger();
    String getHelpDescription();
    boolean isForceUpperCase();
    Integer getMinLength();
    Integer getMaxLength();
    Integer getMinValue();
    Integer getMaxValue();
    String getRegexConstraint();
    String getRegexContraintMsg();
    boolean isRequired();
    Collection<? extends AbstractWidget> getWidgets();
}
