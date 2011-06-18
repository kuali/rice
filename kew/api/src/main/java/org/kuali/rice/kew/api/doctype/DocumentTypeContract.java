package org.kuali.rice.kew.api.doctype;

import org.kuali.rice.core.api.mo.common.Identifiable;

public interface DocumentTypeContract extends Identifiable {

    String getId();
    
    String getName();
    
    Integer getDocumentTypeVersion();
    
    String getLabel();
    
    String getDescription();
    
	String getParentId();
	    
    boolean isActive();

    String getDocHandlerUrl();

    String getHelpDefinitionUrl();
    
    String getDocSearchHelpUrl();
    
    String getPostProcessorName();

    String getApplicationId();
    
    boolean isCurrent();
    
    // TODO - doc type policies
    // TODO - blanket approve group
    // TODO - super user group
    // TODO - RoutePathDTO
	
}
