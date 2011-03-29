package org.kuali.rice.krms.api.repository;

public interface KrmsAttributeDefinitionContract {
	/**
	 * This is the ID for the KrmsAttributeDefinition
	 *
	 * <p>
	 * It is a ID of a KrmsAttributeDefinition.
	 * </p>
	 * @return ID for KrmsAttributeDefinition.
	 */
	public String getId();

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
	 * This is the component name of the KrmsAttributeDefinition
	 * 
	 * @return the component name of the KrmsAttributeDefinition
	 */
	public String getComponentName();
	
	/**
	 * @return the active
	 */
	public boolean isActive();

}
