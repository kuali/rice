package org.kuali.rice.kim.impl.responsibility

/*
 * Copyright 2007-2009 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.kuali.rice.core.api.mo.common.Attributes
import org.kuali.rice.kim.util.KimConstants
import org.kuali.rice.kns.bo.BusinessObject

class ReviewResponsibilityBo implements BusinessObject {

    private static final long serialVersionUID = 1L

    public static final String ACTION_DETAILS_AT_ROLE_MEMBER_LEVEL_FIELD_NAME = "actionDetailsAtRoleMemberLevel"

    String responsibilityId
    String namespaceCode
    String name
    String description
    boolean active

    String documentTypeName
    String routeNodeName
    boolean actionDetailsAtRoleMemberLevel
    boolean required
    String qualifierResolverProvidedIdentifier

    public ReviewResponsibilityBo() {
    }

    public ReviewResponsibilityBo(ResponsibilityBo resp) {
        loadFromKimResponsibility(resp)
    }

    public void loadFromKimResponsibility(ResponsibilityBo resp) {
        setResponsibilityId(resp.getId())
        setNamespaceCode(resp.getNamespaceCode())
        setName(resp.getName())
        setDescription(resp.getDescription())
        setActive(resp.isActive())
        Attributes respDetails = resp.getAttributes()
        setDocumentTypeName(respDetails.get(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME))
        setRouteNodeName(respDetails.get(KimConstants.AttributeConstants.ROUTE_NODE_NAME))
        setActionDetailsAtRoleMemberLevel(Boolean.valueOf(respDetails.get(KimConstants.AttributeConstants.ACTION_DETAILS_AT_ROLE_MEMBER_LEVEL)))
        setRequired(Boolean.valueOf(respDetails.get(KimConstants.AttributeConstants.REQUIRED)))
        setQualifierResolverProvidedIdentifier(respDetails.get(KimConstants.AttributeConstants.QUALIFIER_RESOLVER_PROVIDED_IDENTIFIER))
    }

    public void refresh() {
    }
}
