package org.kuali.rice.kim.test.bo.group.impl.GroupHistoryBoTest;

import edu.emory.mathcs.backport.java.util.Collections;
import org.joda.time.DateTime;
import org.junit.Ignore;
import org.junit.Test;
import org.kuali.rice.core.api.criteria.OrderByField;
import org.kuali.rice.core.api.criteria.OrderDirection;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.criteria.QueryResults;
import org.kuali.rice.kim.api.group.GroupHistory;
import org.kuali.rice.kim.api.group.GroupHistoryQueryResults;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.impl.group.GroupBo;
import org.kuali.rice.kim.impl.group.GroupHistoryBo;
import org.kuali.rice.kim.test.KIMTestCase;
import org.kuali.rice.krad.service.KRADServiceLocator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.*;

public class GroupHistoryBoTest extends KIMTestCase {

    @Ignore // https://jira.kuali.org/browse/KULRICE-9182 Kim Effective dating
    @Test
    public void getGroupHistory() {
        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("id", "g1");
        //Collection<GroupHistoryBo> groupHistoryBos =
        //        KRADServiceLocator.getBusinessObjectService().findMatching(GroupHistoryBo.class, criteria);

        QueryByCriteria qbc = QueryByCriteria.Builder.forAttribute("id", "g1").build();

        QueryResults<GroupBo> groups =
                KRADServiceLocator.getDataObjectService().findMatching(GroupBo.class, qbc);
        assertTrue("should be one value in collection, but was " + groups.getResults().size(), groups.getResults().size() == 1);

        QueryResults<GroupHistoryBo> groupHistories =
                KRADServiceLocator.getDataObjectService().findMatching(GroupHistoryBo.class, qbc);
        assertTrue("should be one value in collection, but was " + groupHistories.getResults().size(), groupHistories.getResults().size() == 1);

    }

    @Ignore // https://jira.kuali.org/browse/KULRICE-9182 Kim Effective dating
    @Test
    public void getGroupHistoryByGroupService() {
        DateTime oldDate = new DateTime(2005, 1, 1, 12, 0, 0, 0);
        DateTime newDate = new DateTime();
        GroupHistory groupHistory = KimApiServiceLocator.getGroupService().getGroupHistory("g1", oldDate);

        assertNull("groupHHistory is not null", groupHistory);

        groupHistory = KimApiServiceLocator.getGroupService().getGroupHistory("g1", newDate);
        assertNotNull("groupHistory is null", groupHistory);


    }

    @Ignore // https://jira.kuali.org/browse/KULRICE-9182 Kim Effective dating
    @Test
    public void findGroupsAndSort() {
        QueryByCriteria.Builder criteria = QueryByCriteria.Builder.create();
        criteria.setMaxResults(new Integer(6));
        //by not setting a predicate, every row should be returned up to the max results.
        OrderByField orderBy = OrderByField.Builder.create("name", OrderDirection.ASCENDING).build();
        criteria.setOrderByFields(Collections.singletonList(orderBy));

        GroupHistoryQueryResults results = KimApiServiceLocator.getGroupService().findGroupHistories(criteria.build());

        assertTrue("Expected 6 results, but collection has " + results.getTotalRowCount(), results.getResults().size() == 6);

    }
}
