/**
 * Copyright 2005-2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
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
package org.kuali.rice.krad.demo.uif.lookup;

import org.apache.commons.collections.MapUtils;
import org.kuali.rice.kim.impl.KIMPropertyConstants;
import org.kuali.rice.kim.impl.group.GroupBo;
import org.kuali.rice.kim.impl.identity.principal.PrincipalBo;
import org.kuali.rice.kim.impl.role.RoleBo;
import org.kuali.rice.krad.lookup.LookupUtils;
import org.kuali.rice.krad.lookup.LookupableImpl;
import org.kuali.rice.krad.lookup.LookupForm;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UIRoleMemberLookupableImpl extends LookupableImpl {

    @Override
    public Collection<?> performSearch(LookupForm form, Map<String, String> searchCriteria, boolean bounded) {
        // removed blank search values and decrypt any encrypted search values
        Map<String, String> nonBlankSearchCriteria = processSearchCriteria(form, searchCriteria);
        List<String> wildcardAsLiteralSearchCriteria = identifyWildcardDisabledFields(form, nonBlankSearchCriteria);

        Integer searchResultsLimit = null;

        if (bounded) {
            searchResultsLimit = LookupUtils.getSearchResultsLimit(getDataObjectClass(), form);
        }

        Class<?> dataObjectClass = null;

        if ("P".equals(MapUtils.getString(searchCriteria, KIMPropertyConstants.KimMember.MEMBER_TYPE_CODE))) {
            dataObjectClass = PrincipalBo.class;
        } else if ("G".equals(MapUtils.getString(searchCriteria, KIMPropertyConstants.KimMember.MEMBER_TYPE_CODE))) {
            dataObjectClass = GroupBo.class;
        } else if ("R".equals(MapUtils.getString(searchCriteria, KIMPropertyConstants.KimMember.MEMBER_TYPE_CODE))) {
            dataObjectClass = RoleBo.class;
        }

        return getLookupService().findCollectionBySearchHelper(dataObjectClass, nonBlankSearchCriteria,
                wildcardAsLiteralSearchCriteria, !bounded, searchResultsLimit);
    }
}
