package org.kuali.rice.kim.api.type;

import org.kuali.rice.core.api.mo.common.Attributes;
import org.kuali.rice.core.util.KeyValue;

import java.util.List;

/**
 *  This is the base service interface for handling type-specific behavior.  Types can be attached
 *  to various objects (currently groups and roles) in KIM to add additional attributes and
 *  modify their behavior.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface KimTypeServiceTemp {
    	/**
	 * Get the workflow document type which
	 * will be used for the role qualifiers when routing objects with this type.
	 *
	 * If no special document type is needed, this method must return null.
	 */
	String getWorkflowDocumentTypeName();

	/**
	 * Perform validation on the attributes of an object.  The resultant map
	 * will contain (attributeName,errorMessage) pairs from the validation process.
	 * An empty map or null indicates that there were no errors.
	 *
	 * This method can be used to perform compound validations across multiple
	 * attributes attached to an object.
	 */
	Attributes validateAttributes(String kimTypeId, Attributes attributes);

	Attributes validateAttributesAgainstExisting(String kimTypeId, Attributes newAttributes, Attributes oldAttributes);

	Attributes validateUnmodifiableAttributes(String kimTypeId, Attributes mainAttributes, Attributes delegationAttributes);

	boolean validateUniqueAttributes(String kimTypeId, Attributes newAttributes, Attributes oldAttributes);

    List<? extends KeyValue> getAttributeValidValues(String kimTypeId, String attributeName);

    List<String> getWorkflowRoutingAttributes( String routeLevel );

    List<String> getUniqueAttributes(String kimTypeId);
}

