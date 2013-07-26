package org.kuali.rice.krad.data.provider.jpa;

import org.kuali.rice.krad.data.provider.MetadataProvider;

/**
 * Subclass of the {@link MetadataProvider} which contains the additional methods needed.
 */
public interface JpaMetadataProvider extends MetadataProvider {

	/**
	 * This method needs to, given the parameters, inject into the JPA repository a 1:1 relationship between the parent
	 * entity and the extension entity via the given property name. (Which must exist on the entityClass.)
	 * 
	 * @param entityClass
	 *            The parent (owning) class which must be already known to the JPA persistence unit. This one's metadata
	 *            will be modified within the internals of the JPA metadata.
	 * @param extensionPropertyName
	 *            The property on the parent class which will hold the extensionEntity. This property must be of the
	 *            type of the extension entity or a superclass. (Object will work.)
	 * @param extensionEntity
	 *            The child/extension class which needs to be linked. It must also already be known to JPA.
	 */
	void addExtensionRelationship(Class<?> entityClass, String extensionPropertyName, Class<?> extensionEntity);

}
