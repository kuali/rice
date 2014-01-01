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
package org.kuali.rice.kew.actionitem;

import org.kuali.rice.core.api.delegation.DelegationType;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * This is the model for action items. These are displayed as the action list as well.  Mapped to ActionItemService.
 * NOTE: This object contains denormalized fields that have been copied from related ActionRequestValue and
 * DocumentRouteHeaderValue
 * objects for performance reasons.  These should be preserved and their related objects should not be added to the OJB
 * mapping as we do not want them loaded for each ActionItem object.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name = "KREW_ACTN_ITM_T")
@NamedQueries({@NamedQuery(name = "ActionItem.DistinctDocumentsForPrincipalId", query =
        "SELECT COUNT(DISTINCT(ai.documentId)) FROM ActionItem ai"
                + "  WHERE ai.principalId = :principalId AND (ai.delegationType IS NULL OR ai.delegationType = 'P')"),
        @NamedQuery(name = "ActionItem.GetMaxDateAndCountForPrincipalId", query =
                "SELECT MAX(ai.dateAssigned) AS max_date, COUNT(DISTINCT(ai.documentId)) AS total_records FROM ActionItem ai"
                        + "  WHERE ai.principalId = :principalId"),
        @NamedQuery(name = "ActionItem.GetQuickLinksDocumentTypeNameAndCount", query =
                "select ai.docName, COUNT(ai) from ActionItem ai where ai.principalId = :principalId " +
                        "and (ai.delegationType is null or ai.delegationType != :delegationType)"
                        + " group by ai.docName")})
public class ActionItem extends ActionItemBase {

    private static final long serialVersionUID = -1079562205125660151L;

    public ActionItem deepCopy(Map<Object, Object> visited) {
        return super.deepCopy(visited, ActionItem.class);
    }

    public static org.kuali.rice.kew.api.action.ActionItem to(ActionItem bo) {
        if (bo == null) {
            return null;
        }
        return org.kuali.rice.kew.api.action.ActionItem.Builder.create(bo).build();
    }

    public static List<org.kuali.rice.kew.api.action.ActionItem> to(Collection<ActionItem> bos) {
        if (bos == null) {
            return null;
        }
        if (bos.isEmpty()) {
            return new ArrayList<org.kuali.rice.kew.api.action.ActionItem>();
        }

        List<org.kuali.rice.kew.api.action.ActionItem> dtos = new ArrayList<org.kuali.rice.kew.api.action.ActionItem>(
                bos.size());
        for (ActionItem bo : bos) {
            dtos.add(ActionItem.to(bo));
        }
        return dtos;
    }
}
