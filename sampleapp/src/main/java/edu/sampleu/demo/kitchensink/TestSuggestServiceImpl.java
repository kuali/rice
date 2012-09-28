package edu.sampleu.demo.kitchensink;

import edu.sampleu.travel.bo.TravelAccount;
import org.kuali.rice.core.api.search.SearchOperator;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class TestSuggestServiceImpl {

    public List<TravelAccount> retrieveTravelAccounts(String term) {
        List<TravelAccount> matchingAccounts = new ArrayList<TravelAccount>();

        Map<String, String> lookupCriteria = new HashMap<String, String>();
        lookupCriteria.put("subAccountName", term + SearchOperator.LIKE_MANY.op());

        matchingAccounts = (List<TravelAccount>) KRADServiceLocatorWeb.getLookupService().findCollectionBySearch(
                TravelAccount.class, lookupCriteria);

        return matchingAccounts;
    }
}
