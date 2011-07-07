package org.kuali.rice.kim.api.type;



import java.util.List;
import java.util.Map;

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
	 * An empty attributes indicates that there were no errors.
	 *
	 * This method can be used to perform compound validations across multiple
	 * attributes attached to an object.
	 */
	Map<String, String> validateAttributes(String kimTypeId, Map<String, String> attributes);

	Map<String, String> validateAttributesAgainstExisting(String kimTypeId, Map<String, String> newAttributes, Map<String, String> oldAttributes);

	Map<String, String> validateUnmodifiableAttributes(String kimTypeId, Map<String, String> mainAttributes, Map<String, String> delegationAttributes);

	boolean validateUniqueAttributes(String kimTypeId, Map<String, String> newAttributes, Map<String, String> oldAttributes);

    Map<String, String> getAttributeValidValues(String kimTypeId, String attributeName);

    List<String> getWorkflowRoutingAttributes( String routeLevel );

    List<String> getUniqueAttributes(String kimTypeId);
}

