/*
 * Copyright 2011 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 1.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kuali.rice.krad.uif.authorization;

import org.kuali.rice.core.util.AttributeSet;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.PermissionService;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADUtils;
import org.kuali.rice.krad.web.form.UifFormBase;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class AuthorizerBase implements Authorizer {

    private static PermissionService permissionService;
    private static PersonService personService;
    private static KualiModuleService kualiModuleService;
    private static DataDictionaryService dataDictionaryService;

    /**
     * @see org.kuali.rice.krad.uif.authorization.Authorizer#getActionFlags(org.kuali.rice.krad.web.form.UifFormBase,
     *      org.kuali.rice.kim.bo.Person, java.util.Set)
     */
    public Set<String> getActionFlags(UifFormBase model, Person user, Set<String> documentActions) {
        return documentActions;
    }

    /**
     * @see org.kuali.rice.krad.uif.authorization.Authorizer#getEditModes(org.kuali.rice.krad.web.form.UifFormBase,
     *      org.kuali.rice.kim.bo.Person, java.util.Set)
     */
    public Set<String> getEditModes(UifFormBase model, Person user, Set<String> editModes) {
        return editModes;
    }

    /**
     * @see org.kuali.rice.krad.uif.authorization.Authorizer#getSecurePotentiallyHiddenGroupIds()
     */
    public Set<String> getSecurePotentiallyHiddenGroupIds() {
        return new HashSet<String>();
    }

    /**
     * @see org.kuali.rice.krad.uif.authorization.Authorizer#getSecurePotentiallyReadOnlyGroupIds()
     */
    public Set<String> getSecurePotentiallyReadOnlyGroupIds() {
        return new HashSet<String>();
    }

    /**
     * Override this method to populate the role qualifier attributes from the
     * primary data object or document. This will only be called once per
     * request.
     * 
     * @param primaryDataObjectOrDocument
     *            - the primary data object (i.e. the main object instance
     *            behind the lookup result row or inquiry) or the document
     * @param attributes
     *            - role qualifiers will be added to this map
     */
    protected void addRoleQualification(Object primaryDataObjectOrDocument, Map<String, String> attributes) {
        addStandardAttributes(primaryDataObjectOrDocument, attributes);
    }

    /**
     * Override this method to populate the permission details from the primary
     * data object or document. This will only be called once per request.
     * 
     * @param primaryDataObjectOrDocument
     *            - the primary data object (i.e. the main object instance
     *            behind the lookup result row or inquiry) or the document
     * @param attributes
     *            - permission details will be added to this map
     */
    protected void addPermissionDetails(Object primaryDataObjectOrDocument, Map<String, String> attributes) {
        addStandardAttributes(primaryDataObjectOrDocument, attributes);
    }

    /**
     * @param primaryDataObjectOrDocument
     *            - the primary data object (i.e. the main object instance
     *            behind the lookup result row or inquiry) or the document
     * @param attributes
     *            - attributes (i.e. role qualifications or permission details)
     *            will be added to this map
     */
    private void addStandardAttributes(Object primaryDataObjectOrDocument, Map<String, String> attributes) {
        attributes.putAll(KRADUtils.getNamespaceAndComponentSimpleName(primaryDataObjectOrDocument.getClass()));
    }

    protected final boolean permissionExistsByTemplate(Object dataObject, String namespaceCode,
            String permissionTemplateName) {
        return getPermissionService().isPermissionDefinedForTemplateName(namespaceCode, permissionTemplateName,
                new AttributeSet(getPermissionDetailValues(dataObject)));
    }

    protected final boolean permissionExistsByTemplate(String namespaceCode, String permissionTemplateName,
            Map<String, String> permissionDetails) {
        return getPermissionService().isPermissionDefinedForTemplateName(namespaceCode, permissionTemplateName,
                new AttributeSet(permissionDetails));
    }

    protected final boolean permissionExistsByTemplate(Object dataObject, String namespaceCode,
            String permissionTemplateName, Map<String, String> permissionDetails) {
        AttributeSet combinedPermissionDetails = new AttributeSet(getPermissionDetailValues(dataObject));
        combinedPermissionDetails.putAll(permissionDetails);

        return getPermissionService().isPermissionDefinedForTemplateName(namespaceCode, permissionTemplateName,
                combinedPermissionDetails);
    }

    public final boolean isAuthorized(Object dataObject, String namespaceCode, String permissionName, String principalId) {
        return getPermissionService().isAuthorized(principalId, namespaceCode, permissionName,
                new AttributeSet(getPermissionDetailValues(dataObject)),
                new AttributeSet(getRoleQualification(dataObject, principalId)));
    }

    public final boolean isAuthorizedByTemplate(Object dataObject, String namespaceCode, String permissionTemplateName,
            String principalId) {
        return getPermissionService().isAuthorizedByTemplateName(principalId, namespaceCode,
                permissionTemplateName, new AttributeSet(getPermissionDetailValues(dataObject)),
                new AttributeSet((getRoleQualification(dataObject, principalId))));
    }

    public final boolean isAuthorized(Object dataObject, String namespaceCode, String permissionName,
            String principalId, Map<String, String> collectionOrFieldLevelPermissionDetails,
            Map<String, String> collectionOrFieldLevelRoleQualification) {
        AttributeSet roleQualifiers;
        AttributeSet permissionDetails;
        if (collectionOrFieldLevelRoleQualification != null) {
            roleQualifiers = new AttributeSet(getRoleQualification(dataObject, principalId));
            roleQualifiers.putAll(collectionOrFieldLevelRoleQualification);
        }
        else {
            roleQualifiers = new AttributeSet(getRoleQualification(dataObject, principalId));
        }
        if (collectionOrFieldLevelPermissionDetails != null) {
            permissionDetails = new AttributeSet(getPermissionDetailValues(dataObject));
            permissionDetails.putAll(collectionOrFieldLevelPermissionDetails);
        }
        else {
            permissionDetails = new AttributeSet(getPermissionDetailValues(dataObject));
        }

        return getPermissionService().isAuthorized(principalId, namespaceCode, permissionName,
                permissionDetails, roleQualifiers);
    }

    public final boolean isAuthorizedByTemplate(Object dataObject, String namespaceCode, String permissionTemplateName,
            String principalId, Map<String, String> collectionOrFieldLevelPermissionDetails,
            Map<String, String> collectionOrFieldLevelRoleQualification) {
        AttributeSet roleQualifiers = new AttributeSet(getRoleQualification(dataObject, principalId));
        AttributeSet permissionDetails = new AttributeSet(getPermissionDetailValues(dataObject));

        if (collectionOrFieldLevelRoleQualification != null) {
            roleQualifiers.putAll(collectionOrFieldLevelRoleQualification);
        }
        if (collectionOrFieldLevelPermissionDetails != null) {
            permissionDetails.putAll(collectionOrFieldLevelPermissionDetails);
        }

        return getPermissionService().isAuthorizedByTemplateName(principalId, namespaceCode,
                permissionTemplateName, permissionDetails, roleQualifiers);
    }

    /**
     * Returns a role qualification map based off data from the primary business
     * object or the document. DO NOT MODIFY THE MAP RETURNED BY THIS METHOD
     * 
     * @param primaryDataObjectOrDocument
     *            the primary data object (i.e. the main object instance behind
     *            the lookup result row or inquiry) or the document
     * @return a Map containing role qualifications
     */
    protected final Map<String, String> getRoleQualification(Object primaryDataObjectOrDocument) {
        return getRoleQualification(primaryDataObjectOrDocument, GlobalVariables.getUserSession().getPerson()
                .getPrincipalId());
    }

    protected final Map<String, String> getRoleQualification(Object primaryDataObjectOrDocument, String principalId) {
        Map<String, String> roleQualification = new HashMap<String, String>();
        addRoleQualification(primaryDataObjectOrDocument, roleQualification);
        roleQualification.put(KimConstants.AttributeConstants.PRINCIPAL_ID, principalId);

        return roleQualification;
    }

    /**
     * Returns a permission details map based off data from the primary business
     * object or the document. DO NOT MODIFY THE MAP RETURNED BY THIS METHOD
     * 
     * @param primaryDataObjectOrDocument
     *            the primary data object (i.e. the main object instance behind
     *            the lookup result row or inquiry) or the document
     * @return a Map containing permission details
     */
    protected final Map<String, String> getPermissionDetailValues(Object primaryDataObjectOrDocument) {
        Map<String, String> permissionDetails = new HashMap<String, String>();
        addPermissionDetails(primaryDataObjectOrDocument, permissionDetails);

        return permissionDetails;
    }

    protected static final PermissionService getPermissionService() {
        if (permissionService == null) {
            permissionService = KimApiServiceLocator.getPermissionService();
        }
        return permissionService;
    }

    protected static final PersonService getPersonService() {
        if (personService == null) {
            personService = KimApiServiceLocator.getPersonService();
        }
        return personService;
    }

    protected static final KualiModuleService getKualiModuleService() {
        if (kualiModuleService == null) {
            kualiModuleService = KRADServiceLocatorWeb.getKualiModuleService();
        }
        return kualiModuleService;
    }

    protected static final DataDictionaryService getDataDictionaryService() {
        if (dataDictionaryService == null) {
            dataDictionaryService = KRADServiceLocatorWeb.getDataDictionaryService();
        }
        return dataDictionaryService;
    }

}
