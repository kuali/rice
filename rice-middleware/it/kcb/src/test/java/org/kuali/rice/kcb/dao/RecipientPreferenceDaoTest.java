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

import org.junit.Ignore;
import org.junit.Test;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.kcb.bo.RecipientPreference;
import org.kuali.rice.kcb.test.KCBTestCase;
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

    RecipientPreference pref1 = new RecipientPreference();
    RecipientPreference pref2 = new RecipientPreference();
    
    private String[] recipientTypes = {"Type 1", "Type 2"};
    private String[] recipientIds = {"unit_test_recip1", "unit_test_recip2"};
    private String[] propertys = {"Property A", "Property B"};
    private String[] values = {"Value A", "Value B"};
    private String[] updatedValues = {"Value C", "Value D"};
    
    /**
     *
     */
    @Test
    public void testDelete() {
        testCreate();
        KRADServiceLocator.getDataObjectService().delete(pref1);
        KRADServiceLocator.getDataObjectService().delete(pref2);
    }
    
    /**
     *
     */
    @Test
    public void testReadByQuery() {
        testCreate();

        QueryByCriteria.Builder criteria1 = QueryByCriteria.Builder.create();
        criteria1.setPredicates(equal(RecipientPreference.RECIPIENT_FIELD, recipientIds[0]),
                equal(RecipientPreference.PROPERTY_FIELD, propertys[0]));
        List<RecipientPreference> prefs1 =
                KRADServiceLocator.getDataObjectService().findMatching(RecipientPreference.class, criteria1.build()).getResults();

        assertNotNull(pref1);
        assertEquals(prefs1.size(), 1);

        pref1 = prefs1.get(0);
        assertNotNull(pref1);
        assertEquals(recipientIds[0], pref1.getRecipientId());


        QueryByCriteria.Builder criteria2 = QueryByCriteria.Builder.create();
        criteria2.setPredicates(equal(RecipientPreference.RECIPIENT_FIELD, recipientIds[1]),
                equal(RecipientPreference.PROPERTY_FIELD, propertys[1]));
        List<RecipientPreference> prefs2 =
                KRADServiceLocator.getDataObjectService().findMatching(RecipientPreference.class, criteria2.build()).getResults();

        assertNotNull(pref2);
        assertEquals(prefs2.size(), 1);
        pref2 = prefs2.get(0);
        assertNotNull(pref2);
        assertEquals(recipientIds[1], pref2.getRecipientId());
    
    }
    
    /**
     *
     */
    @Test
    public void testCreate() {
        pref1.setRecipientId(recipientIds[0]);
        pref1.setProperty(propertys[0]);
        pref1.setValue(values[0]);
        
        pref2.setRecipientId(recipientIds[1]);
        pref2.setProperty(propertys[1]);
        pref2.setValue(values[1]);
        
        pref1 = KRADServiceLocator.getDataObjectService().save(pref1, PersistenceOption.FLUSH);
        pref2 = KRADServiceLocator.getDataObjectService().save(pref2, PersistenceOption.FLUSH);
    }
    
    /**
     *
     */
    @Test
    @Ignore // until I fix how this test uses test data
    public void testUpdate() {
        testCreate();
        pref1.setValue(updatedValues[0]);
        
        pref2.setValue(updatedValues[1]);
    
        pref1 = KRADServiceLocator.getDataObjectService().save(pref1, PersistenceOption.FLUSH);
        pref2 = KRADServiceLocator.getDataObjectService().save(pref2, PersistenceOption.FLUSH);
        
        testReadByQuery();
        
        assertEquals(updatedValues[0], pref1.getValue());
        assertEquals(updatedValues[1], pref2.getValue());
    }
}
