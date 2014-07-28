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
package org.kuali.rice.kcb.dao;

import org.junit.Test;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.kcb.bo.RecipientPreference;
import org.kuali.rice.kcb.test.KCBTestCase;
import org.kuali.rice.krad.bo.DataObjectBase;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.data.PersistenceOption;
import org.kuali.rice.krad.service.KRADServiceLocator;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import static org.kuali.rice.core.api.criteria.PredicateFactory.equal;

/**
 * This class test basic persistence for the RecipientPreference business object.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class RecipientPreferenceDaoTest extends KCBTestCase {

    private String[] recipientIds = {"unit_test_recip1", "unit_test_recip2"};
    private String[] propertys = {"Property A", "Property B"};
    private String[] values = {"Value A", "Value B"};
    private String[] updatedValues = {"Value C", "Value D"};

    /**
     * Tests creating a {@link RecipientPreference}.
     */
    @Test
    public void testCreate() {
        RecipientPreference pref1 = createRecipientPreference(0);
        assertNotNull(pref1.getId());

        RecipientPreference pref2 = createRecipientPreference(1);
        assertNotNull(pref2.getId());
    }
    
    /**
     * Tests finding a {@link RecipientPreference}.
     */
    @Test
    public void testFind() {
        RecipientPreference pref1 = createRecipientPreference(0);
        RecipientPreference pref2 = createRecipientPreference(1);

        QueryByCriteria.Builder criteria1 = QueryByCriteria.Builder.create();
        criteria1.setPredicates(equal(RecipientPreference.RECIPIENT_FIELD, recipientIds[0]),
                equal(RecipientPreference.PROPERTY_FIELD, propertys[0]));
        List<RecipientPreference> prefs1 =
                getDataObjectService().findMatching(RecipientPreference.class, criteria1.build()).getResults();

        assertNotNull(pref1);
        assertEquals(prefs1.size(), 1);

        pref1 = prefs1.get(0);
        assertNotNull(pref1);
        assertEquals(recipientIds[0], pref1.getRecipientId());

        QueryByCriteria.Builder criteria2 = QueryByCriteria.Builder.create();
        criteria2.setPredicates(equal(RecipientPreference.RECIPIENT_FIELD, recipientIds[1]),
                equal(RecipientPreference.PROPERTY_FIELD, propertys[1]));
        List<RecipientPreference> prefs2 =
                getDataObjectService().findMatching(RecipientPreference.class, criteria2.build()).getResults();

        assertNotNull(pref2);
        assertEquals(prefs2.size(), 1);
        pref2 = prefs2.get(0);
        assertNotNull(pref2);
        assertEquals(recipientIds[1], pref2.getRecipientId());
    }
    
    /**
     * Tests updating a {@link RecipientPreference}.
     */
    @Test
    public void testUpdate() {
        RecipientPreference pref1 = createRecipientPreference(0);
        RecipientPreference pref2 = createRecipientPreference(1);

        pref1.setValue(updatedValues[0]);
        pref2.setValue(updatedValues[1]);
    
        pref1 = KRADServiceLocator.getDataObjectService().save(pref1, PersistenceOption.FLUSH);
        pref2 = KRADServiceLocator.getDataObjectService().save(pref2, PersistenceOption.FLUSH);
        
        assertEquals(updatedValues[0], pref1.getValue());
        assertEquals(updatedValues[1], pref2.getValue());
    }

    /**
     * Tests deleting a {@link RecipientPreference}.
     */
    @Test
    public void testDelete() {
        RecipientPreference pref1 = createRecipientPreference(0);
        RecipientPreference pref2 = createRecipientPreference(1);

        KRADServiceLocator.getDataObjectService().delete(pref1);
        KRADServiceLocator.getDataObjectService().delete(pref2);

        assertEquals(0, getDataObjectService().findAll(RecipientPreference.class).getResults().size());

    }

    private RecipientPreference createRecipientPreference(int index) {
        RecipientPreference recipientPreference = new RecipientPreference();
        recipientPreference.setRecipientId(recipientIds[index]);
        recipientPreference.setProperty(propertys[index]);
        recipientPreference.setValue(values[index]);

        return getDataObjectService().save(recipientPreference, PersistenceOption.FLUSH);
    }

    private DataObjectService getDataObjectService() {
        return KRADServiceLocator.getDataObjectService();
    }

}