package org.kuali.rice.krms.api.repository.type;

import org.kuali.rice.core.api.mo.common.Identifiable;
import org.kuali.rice.core.api.mo.common.Versioned;
import org.kuali.rice.core.api.mo.common.active.Inactivatable;

public interface KrmsAttributeDefinitionContract extends Identifiable, Inactivatable, Versioned {

	/**
	 * This is the name for the KrmsAttributeDefinition
	 *
	 * <p>
	 * It is a name of a KrmsAttributeDefinition.
	 * </p>
	 * @return name for KrmsAttributeDefinition.
	 */
	public String getName();

	/**
	 * This is the namespace code. 
	 *
	 * <p>
	 * It provides scope of the KrmsAttributeDefinition.
	 * </p>
	 * @return the namespace code of the KrmsAttributeDefinition.
	 */
	public String getNamespace();

	/**
	 * This is the label of the KrmsAttributeDefinition
	 * 
	 * @return the label of the KrmsAttributeDefinition
	 */
	public String getLabel();

    /**
     * this is the optional description for the {@link KrmsAttributeDefinition}
     * @return the description text
     */
    public String getDescription();
	
	/**
	 * This is the component name of the KrmsAttributeDefinition
	 * 
	 * @return the component name of the KrmsAttributeDefinition
	 */
	public String getComponentName();
}
