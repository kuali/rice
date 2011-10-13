package org.kuali.rice.krad.service;

import org.kuali.rice.core.api.uif.RemotableAttributeField;

/**
 * Provides service methods for building and validate {@link RemotableAttributeField} definitions from data
 * dictionary {@link org.kuali.rice.krad.datadictionary.AttributeDefinition} configurations
 *
 * <p>
 * Used by the default type services {@link org.kuali.rice.krad.workflow.DataDictionaryPeopleFlowTypeServiceImpl} to
 * build the remotable fields for the type attributes
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface DataDictionaryRemoteFieldService {

    /**
     * Builds and returns an {@link RemotableAttributeField} instance based on the data dictionary attribute definition
     * that is associated with the given component class name (business object or data object entry) and the given
     * attribute name
     *
     * <p>
     * If an attribute definition is not found a runtime exception should be thrown
     * </p>
     *
     * @param componentClassName - class name for the attribute, used to find the data dictionary entry
     * @param attributeName - name of the attribute whose definition should be used
     * @return RemotableAttributeField instance built
     */
    public RemotableAttributeField buildRemotableFieldFromAttributeDefinition(String componentClassName,
            String attributeName);

}
