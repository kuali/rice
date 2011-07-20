package org.kuali.rice.core.api.uif.control;

import org.kuali.rice.core.api.uif.control.widget.Widget;

import java.util.Collection;

public interface AttributeFieldContract {
    
    String getName();
    String getShortLabel();
    String getLongLabel();
    String getHelpSummary();
    String getHelpConstraint();
    String getHelpDescription();
    boolean isForceUpperCase();
    Integer getMinLength();
    Integer getMaxLength();
    Integer getMinValue();
    Integer getMaxValue();
    String getRegexConstraint();
    String getRegexContraintMsg();
    boolean isRequired();
    Collection<String> getDefaultValues();
    Control getControl();
    Collection<? extends Widget> getWidgets();

}
