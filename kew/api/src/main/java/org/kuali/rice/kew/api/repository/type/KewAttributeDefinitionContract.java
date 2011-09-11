package org.kuali.rice.kew.api.repository.type;

import org.kuali.rice.core.api.mo.common.Identifiable;
import org.kuali.rice.core.api.mo.common.Versioned;
import org.kuali.rice.core.api.mo.common.active.Inactivatable;

public interface KewAttributeDefinitionContract extends Identifiable, Inactivatable, Versioned {

	/**
	 * This is the name for the KewAttributeDefinition
	 *
	 * <p>
	 * It is a name of a KewAttributeDefinition.
	 * </p>
	 * @return name for KewAttributeDefinition.
	 */
	public String getName();

	/**
	 * This is the namespace code. 
	 *
	 * <p>
	 * It provides scope of the KewAttributeDefinition.
	 * </p>
	 * @return the namespace code of the KewAttributeDefinition.
	 */
	public String getNamespace();

	/**
	 * This is the label of the KewAttributeDefinition
	 * 
	 * @return the label of the KewAttributeDefinition
	 */
	public String getLabel();

    /**
     * this is the optional description for the {@link KewAttributeDefinition}
     * @return the description text
     */
    public String getDescription();
	
	/**
	 * This is the component name of the KewAttributeDefinition
	 * 
	 * @return the component name of the KewAttributeDefinition
	 */
	public String getComponentName();
}
