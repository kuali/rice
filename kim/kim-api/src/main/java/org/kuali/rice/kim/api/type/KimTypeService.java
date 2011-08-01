package org.kuali.rice.kim.api.type;

import org.kuali.rice.core.api.uif.RemotableAttributeError;

import java.util.List;
import java.util.Map;

/**
 * This is the base service interface for handling type-specific behavior.  Types can be attached
 * to various objects (currently groups and roles) in KIM to add additional attributes and
 * modify their behavior.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface KimTypeService {

    /**
     * Returns the name of a workflow document type that should be passed as a qualifier with
     * the "documentTypeName" key when resolving responsibilities when routing a KIM document
     * which uses this KIM type (such as a group, role, or person document).
     *
     * return null to indicate that there is no custom workflow document needed for this type.
     *
     * @return the doc type name or null.
     */
    String getWorkflowDocumentTypeName();

    /**
     * Returns an unmodifiable list of strings identifying the name of the attributes of the KIM object which uses this
     * type that should be included in the Map of qualifiers that are provided to the KIM responsibility service when
     * resolving responsibility-based routing at the node with the given name.
     *
     * Returns an empty list, indicating that no attributes from this
     * type should be passed to workflow.
     *
     * @return an unmodifiable list should not return null.
     */
    List<String> getWorkflowRoutingAttributes(String nodeName);

    List<KimAttributeField> getAttributeDefinitions(String kimTypeId);

    /**
     * Perform validation on the attributes of an object.
     * An empty list indicates that there were no errors.
     *
     * This method can be used to perform compound validations across multiple
     * attributes attached to an object.
     */
    List<RemotableAttributeError> validateAttributes(String kimTypeId, Map<String, String> attributes);

    /**
     * Perform validation on the attributes of an object.
     * An empty list indicates that there were no errors.
     */
    List<RemotableAttributeError> validateAttributesAgainstExisting(String kimTypeId, Map<String, String> newAttributes, Map<String, String> oldAttributes);
}

