/*
 * Copyright 2006-2011 The Kuali Foundation
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
package org.kuali.rice.kew.rule.service.impl;

import org.junit.Test;
import org.kuali.rice.core.framework.persistence.jpa.OrmUtils;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.rule.RuleBaseValues;
import org.kuali.rice.kew.rule.RuleExtension;
import org.kuali.rice.kew.rule.RuleExtensionValue;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.service.WorkflowDocument;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.test.BaselineTestCase;
import org.springframework.dao.DataIntegrityViolationException;

import javax.persistence.PersistenceException;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

@BaselineTestCase.BaselineMode(BaselineTestCase.Mode.NONE)
public class RuleServiceTest extends KEWTestCase {

    protected void loadTestData() throws Exception {
        loadXmlFile("org/kuali/rice/kew/rule/RouteTemplateConfig.xml");
    }

    @Test public void testClearCacheWithDocumentTypeUpdate() throws Exception {
        //put the rules in the cache by routing documents
        WorkflowDocument document = WorkflowDocument.createDocument(getPrincipalIdForName("rkirkend"), "RiceDocument.child1");
        document.routeDocument("");
        document = WorkflowDocument.createDocument(getPrincipalIdForName("rkirkend"), "RiceDocument.child1child");
        document.routeDocument("");
        document = WorkflowDocument.createDocument(getPrincipalIdForName("rkirkend"), "RiceDocument.child2");
        document.routeDocument("");
        document = WorkflowDocument.createDocument(getPrincipalIdForName("rkirkend"), "RiceDocument.child3");
        document.routeDocument("");
        document = WorkflowDocument.createDocument(getPrincipalIdForName("rkirkend"), "NotRelated");
        document.routeDocument("");

        //verify the cache's contents are correct
        List<RuleBaseValues> rulesCached = getListFromCache("DocumentTypeRouting", "RiceDocument.child1");
        assertEquals("Wrong number of rules cached", 1, rulesCached.size());
        RuleBaseValues child1RuleFirstCached = (RuleBaseValues)rulesCached.get(0);

        rulesCached = getListFromCache("DocumentTypeRouting", "RiceDocument.child1child");
        assertEquals("Wrong number of rules cached", 2, rulesCached.size());
        RuleBaseValues child1childRuleFirstCached = null;
        // make sure this has the right rules which is the rule to the document type above and it's parent
        for (Iterator iter = rulesCached.iterator(); iter.hasNext();) {
            RuleBaseValues rule = (RuleBaseValues) iter.next();
            if (!(rule.getDocTypeName().equals("RiceDocument.child1") || rule.getDocTypeName().equals("RiceDocument.child1child"))) {
                fail("Wrong rule in the cache");
            }
            if (rule.getDocTypeName().equals("RiceDocument.child1child")) {
                child1childRuleFirstCached = rule;
            }
        }
        rulesCached = getListFromCache("DocumentTypeRouting", "RiceDocument.child2");
        assertEquals("Wrong number of rules cached", 1, rulesCached.size());
        RuleBaseValues child2RuleFirstCached = (RuleBaseValues)rulesCached.get(0);

        rulesCached = getListFromCache("DocumentTypeRouting", "RiceDocument.child3");
        assertEquals("Wrong number of rules cached", 1, rulesCached.size());
        RuleBaseValues child3RuleFirstCached = (RuleBaseValues)rulesCached.get(0);

        rulesCached = getListFromCache("DocumentTypeRouting", "NotRelated");
        assertEquals("Wrong number of rules cached", 1, rulesCached.size());
        RuleBaseValues notRelatedRuleFirstCached = (RuleBaseValues)rulesCached.get(0);

        /**
         * Import a replacement document type from xml.  This should clear out all rules within the
         * DocumentType hierarchy of RiceDocument.
         *
         * This will upload a new child document type to child1child and then update the child3 and child1child document types.
         */

        DocumentType root = KEWServiceLocator.getDocumentTypeService().findByName("RiceDocument");

        loadXmlFile("org/kuali/rice/kew/rule/DocumentTypeToImportForCacheTest.xml");

        root = KEWServiceLocator.getDocumentTypeService().findByName("RiceDocument");

        /**
         * Check the RiceDocument.child1 rules.
         */

        rulesCached = getListFromCache("DocumentTypeRouting", "RiceDocument.child1");
        assertNull("The rules should not be cached.", rulesCached);

        // now fetch the rules to put them in the cache
        List rulesFetched = KEWServiceLocator.getRuleService().fetchAllCurrentRulesForTemplateDocCombination("DocumentTypeRouting", "RiceDocument.child1");
        assertNotNull("List of rules should be fetched from the service.", rulesFetched);
        assertEquals("Wrong number of rules fetched", 1, rulesFetched.size());

        // now fetch from cache again
        rulesCached = getListFromCache("DocumentTypeRouting", "RiceDocument.child1");
        assertNotNull("The rules should be cached.", rulesCached);
        assertEquals("Wrong number of rules cached", 1, rulesCached.size());
        RuleBaseValues child1RuleSecondCached = (RuleBaseValues)rulesCached.get(0);
        assertFalse("These rules should be different because the cache was updated", child1RuleFirstCached.equals(child1RuleSecondCached));
        assertEquals("The rule ids should be the same.", child1RuleFirstCached.getRuleBaseValuesId(), child1RuleSecondCached.getRuleBaseValuesId());

        /**
         * Check the RiceDocument.child2 rules.
         */

        rulesCached = getListFromCache("DocumentTypeRouting", "RiceDocument.child2");
        assertNull("The rules should not be cached.", rulesCached);

        // now fetch the rules to put them in the cache
        rulesFetched = KEWServiceLocator.getRuleService().fetchAllCurrentRulesForTemplateDocCombination("DocumentTypeRouting", "RiceDocument.child2");
        assertNotNull("List of rules should be fetched from the service.", rulesFetched);
        assertEquals("Wrong number of rules fetched", 1, rulesFetched.size());

        // now fetch from cache again
        rulesCached = getListFromCache("DocumentTypeRouting", "RiceDocument.child2");
        assertNotNull("The rules should be cached.", rulesCached);
        assertEquals("Wrong number of rules cached", 1, rulesCached.size());
        RuleBaseValues child2RuleSecondCached = (RuleBaseValues)rulesCached.get(0);
        assertFalse("These rules should be different because the cache was updated", child2RuleFirstCached.equals(child2RuleSecondCached));
        assertEquals("The rule ids should be the same.", child2RuleFirstCached.getRuleBaseValuesId(), child2RuleSecondCached.getRuleBaseValuesId());

        /**
         * Check RiceDocument.child3 rules.
         */

        rulesCached = getListFromCache("DocumentTypeRouting", "RiceDocument.child3");
        assertNull("The rules should not be cached.", rulesCached);

        // now fetch the rules to put them in the cache
        rulesFetched = KEWServiceLocator.getRuleService().fetchAllCurrentRulesForTemplateDocCombination("DocumentTypeRouting", "RiceDocument.child3");
        assertNotNull("List of rules should be fetched from the service.", rulesFetched);
        assertEquals("Wrong number of rules fetched", 1, rulesFetched.size());

        // now fetch from cache again
        rulesCached = getListFromCache("DocumentTypeRouting", "RiceDocument.child3");
        assertEquals("Wrong number of rules cached", 1, rulesCached.size());
        RuleBaseValues child3RuleSecondCached = (RuleBaseValues)rulesCached.get(0);
        assertFalse("These rules should be different because the cache was updated.", child3RuleFirstCached.equals(child3RuleSecondCached));
        assertEquals("The rule ids should be the same.", child3RuleFirstCached.getRuleBaseValuesId(), child3RuleSecondCached.getRuleBaseValuesId());

        /**
         * Check the RiceDocument.child1child rules.
         */

        rulesCached = getListFromCache("DocumentTypeRouting", "RiceDocument.child1child");
        assertNull("The rules should not be cached.", rulesCached);

        // now fetch the rules to put them in the cache
        rulesFetched = KEWServiceLocator.getRuleService().fetchAllCurrentRulesForTemplateDocCombination("DocumentTypeRouting", "RiceDocument.child1child");
        assertNotNull("List of rules should be fetched from the service.", rulesFetched);
        assertEquals("Wrong number of rules fetched", 2, rulesFetched.size());

        // fetch from cache again
        rulesCached = getListFromCache("DocumentTypeRouting", "RiceDocument.child1child");
        assertEquals("Wrong number of rules cached", 2, rulesCached.size());
        RuleBaseValues child1childRuleSecondCached = null;
        //make sure this has the right rules which is the rule to the document type above and it's parent
        for (Iterator iter = rulesCached.iterator(); iter.hasNext();) {
            RuleBaseValues rule = (RuleBaseValues) iter.next();
            if (!(rule.getDocTypeName().equals("RiceDocument.child1") || rule.getDocTypeName().equals("RiceDocument.child1child"))) {
                fail("Wrong rule in the cache");
            }
            if (rule.getDocTypeName().equals("RiceDocument.child1child")) {
                child1childRuleSecondCached = rule;
            }
        }
        assertFalse("These rules should be different because the cache was updated.", child1childRuleFirstCached.equals(child1childRuleSecondCached));
        assertEquals("The rule ids should be the same.", child1childRuleFirstCached.getRuleBaseValuesId(), child1childRuleSecondCached.getRuleBaseValuesId());

        /**
         * Check the NotRelated rules these are the only ones which should not have been flushed from the cache.
         */

        rulesCached = getListFromCache("DocumentTypeRouting", "NotRelated");
        assertEquals("Wrong number of rules cached", 1, rulesCached.size());
        RuleBaseValues notRelatedRuleSecondCached = (RuleBaseValues)rulesCached.get(0);
        assertTrue("These rules should be the same because the cache was not updated", notRelatedRuleFirstCached.equals(notRelatedRuleSecondCached));

        /**
         * Grab the RiceDocument.child1child1child rules from the cache, they should initially be null since
         * this is a brand new document type.
         *
         * After fetching them we should see the rules from RiceDocument.child1, RiceDocument.child1child,
         * and RiceDocument.child1child1child.
         */

        rulesCached = getListFromCache("DocumentTypeRouting", "RiceDocument.child1child1child");
        assertNull("Rules should not be cached yet.", rulesCached);

        // routing a doc should put the rules into the cache
        document = WorkflowDocument.createDocument(getPrincipalIdForName("rkirkend"), "RiceDocument.child1child1child");
        document.routeDocument("");

        rulesCached = getListFromCache("DocumentTypeRouting", "RiceDocument.child1child1child");
        assertNotNull(rulesCached);
        assertEquals(3, rulesCached.size());
        boolean hasRule1 = false;
        boolean hasRule2 = false;
        boolean hasRule3 = false;
        for (RuleBaseValues rule : rulesCached) {
            if (rule.getDocTypeName().equals("RiceDocument.child1")) {
                hasRule1 = true;
            } else if (rule.getDocTypeName().equals("RiceDocument.child1child")) {
                hasRule2 = true;
            } else if (rule.getDocTypeName().equals("RiceDocument.child1child1child")) {
                hasRule3 = true;
            }
        }
        assertTrue("Should have had RiceDocument.child1 rule", hasRule1);
        assertTrue("Should have had RiceDocument.child1child rule", hasRule2);
        assertTrue("Should have had RiceDocument.child1child1child rule", hasRule3);

        /**
         * Import a new rule for RiceDocument and verify that the entire hierarchy is flushed
         * from the cache.
         */

        // first verify that there are no rules for RiceDocument
        rulesFetched = KEWServiceLocator.getRuleService().fetchAllCurrentRulesForTemplateDocCombination("DocumentTypeRouting", "RiceDocument");
        assertNotNull("The list should not be null.", rulesFetched);
        assertEquals("The list should be empty.", 0, rulesFetched.size());

        loadXmlFile("org/kuali/rice/kew/rule/NewEdenserviceDocsRule.xml");

        // verify that all rules for doc types in the hierarchy have been flushed from the cache

        rulesCached = getListFromCache("DocumentTypeRouting", "RiceDocument");
        assertNull("There should be no cache entry for RiceDocument", rulesCached);

        rulesCached = getListFromCache("DocumentTypeRouting", "RiceDocument.child1");
        assertNull("There should be no cache entry for RiceDocument.child1", rulesCached);

        rulesCached = getListFromCache("DocumentTypeRouting", "RiceDocument.child2");
        assertNull("There should be no cache entry for RiceDocument.child2", rulesCached);

        rulesCached = getListFromCache("DocumentTypeRouting", "RiceDocument.child3");
        assertNull("There should be no cache entry for RiceDocument.child3", rulesCached);

        rulesCached = getListFromCache("DocumentTypeRouting", "RiceDocument.child1child");
        assertNull("There should be no cache entry for RiceDocument.child1child", rulesCached);

        rulesCached = getListFromCache("DocumentTypeRouting", "RiceDocument.child1child1child");
        assertNull("There should be no cache entry for RiceDocument.child1child1child", rulesCached);

        // now fetch them from service so they are cached
        rulesFetched = KEWServiceLocator.getRuleService().fetchAllCurrentRulesForTemplateDocCombination("DocumentTypeRouting", "RiceDocument");
        assertNotNull("The list should not be null.", rulesFetched);
        assertEquals("The list should contain a single rule.", 1, rulesFetched.size());

        // fetch the cache now and verify the same
        rulesCached = getListFromCache("DocumentTypeRouting", "RiceDocument");
        assertNotNull("The list should not be null.", rulesCached);
        assertEquals("The list should contain a single rule.", 1, rulesCached.size());

        /**
         * Since we imported a rule at the top of the document hierarchy and the Lists of rules cached
         * also include all rules from parent document types, each of the rule lists should be increased by one
         */

        // RiceDocument.child1 should now have 2 rules
        rulesFetched = KEWServiceLocator.getRuleService().fetchAllCurrentRulesForTemplateDocCombination("DocumentTypeRouting", "RiceDocument.child1");
        assertEquals(2, rulesFetched.size());
        rulesCached = getListFromCache("DocumentTypeRouting", "RiceDocument.child1");
        assertEquals(2, rulesCached.size());

        // RiceDocument.child2 should now have 2 rules
        rulesFetched = KEWServiceLocator.getRuleService().fetchAllCurrentRulesForTemplateDocCombination("DocumentTypeRouting", "RiceDocument.child2");
        assertEquals(2, rulesFetched.size());
        rulesCached = getListFromCache("DocumentTypeRouting", "RiceDocument.child2");
        assertEquals(2, rulesCached.size());

        // RiceDocument.child3 should now have 2 rules
        rulesFetched = KEWServiceLocator.getRuleService().fetchAllCurrentRulesForTemplateDocCombination("DocumentTypeRouting", "RiceDocument.child3");
        assertEquals(2, rulesFetched.size());
        rulesCached = getListFromCache("DocumentTypeRouting", "RiceDocument.child3");
        assertEquals(2, rulesCached.size());

        // RiceDocument.child1child should now have 3 rules
        rulesFetched = KEWServiceLocator.getRuleService().fetchAllCurrentRulesForTemplateDocCombination("DocumentTypeRouting", "RiceDocument.child1child");
        assertEquals(3, rulesFetched.size());
        rulesCached = getListFromCache("DocumentTypeRouting", "RiceDocument.child1child");
        assertEquals(3, rulesCached.size());

        // RiceDocument.child1child1child should now have 4 rules
        rulesFetched = KEWServiceLocator.getRuleService().fetchAllCurrentRulesForTemplateDocCombination("DocumentTypeRouting", "RiceDocument.child1child1child");
        assertEquals(4, rulesFetched.size());
        rulesCached = getListFromCache("DocumentTypeRouting", "RiceDocument.child1child1child");
        assertEquals(4, rulesCached.size());

        // NotRelated should still only have 1 rule
        rulesFetched = KEWServiceLocator.getRuleService().fetchAllCurrentRulesForTemplateDocCombination("DocumentTypeRouting", "NotRelated");
        assertEquals(1, rulesFetched.size());
        rulesCached = getListFromCache("DocumentTypeRouting", "NotRelated");
        assertEquals(1, rulesCached.size());

    }

    private List<RuleBaseValues> getListFromCache(String ruleTemplateName, String documentTypeName) {
        return new RuleServiceImpl().getListFromCache(ruleTemplateName, documentTypeName);
    }

    /**
     * Tests the effect of adding a rule with an extension that has an empty value.
     * Currently, depending on database, this may yield a constraint violation as the empty
     * string may be interpreted as a NULL value by the database.
     * @see https://test.kuali.org/jira/browse/KULRNE-6182
     */
    @Test
    public void testEmptyRuleExtension() throws Exception {
        final RuleBaseValues rbv = new RuleBaseValues();
        rbv.setActiveInd(Boolean.TRUE);
        rbv.setCurrentInd(Boolean.TRUE);
        rbv.setDescription("A test rule");
        rbv.setDocTypeName("TestDocumentType");
        rbv.setForceAction(Boolean.FALSE);

        RuleExtension ext = new RuleExtension();
        RuleExtensionValue val = new RuleExtensionValue();
        val.setKey("emptyvalue");
        val.setValue("");
        ext.getExtensionValues().add(val);
        rbv.getRuleExtensions().add(ext);

        /*
         * The AssertThrows below should work with OJB, but some problems are occurring when using JPA/Hibernate (see below).
         * 
         * FIXME: When the operation below throws the expected exception (when JPA/Hibernate is used), it appears to do so while returning from the method call
         * (at which time the expected-to-fail saving operation gets committed by Hibernate). Unfortunately, the exception gets thrown at the wrong time when returning,
         * because any attempts to run subsequent unit tests or unit test methods during the same JUnit test run will fail, due to NOWAIT exceptions
         * during test case startup.
         * 
         * A temporary hack to bypass this problem is to add the following line at the end of the 3-argument RuleServiceImpl.save2() method, which will force the
         * bad saving operation to take place at the right time for a proper rollback to occur:
         * 
         * getRuleDAO().findRuleBaseValuesById(ruleBaseValues.getRuleBaseValuesId());
         * 
         * However, a longer-term solution will be needed in case there are similar areas in the system with these kinds of problems.
         */
        final boolean isKewJpaEnabled = OrmUtils.isJpaEnabled("rice.kew");
        try {
            KEWServiceLocator.getRuleService().save2(rbv);
            fail("exception did not happen");
        } catch (RuntimeException e) {
            boolean fail = !isKewJpaEnabled ? e instanceof PersistenceException : e instanceof DataIntegrityViolationException;
            if (fail) {
                fail("Did not throw exception as expected.  If rule service behavior has changed, update this test.");
            }
        }

        //fail("Saving a rule extension value with an empty string as the value yields a constraint violation");
    }
    
    /**
     * Tests the RuleService's ability to retrieve RuleBaseValues instances that lack an associated rule responsibility. 
     * @see https://test.kuali.org/jira/browse/KULRICE-3513
     */
    @Test
    public void testRetrievalOfRulesWithoutResponsibilities() throws Exception {
    	loadXmlFile("org/kuali/rice/kew/rule/RulesWithoutResponsibilities.xml");
    	final Long NULL_ID = null;
    	final String[] expectedRuleNames = {"NoResponsibilitiesRule1", "NoResponsibilitiesRule2", "NoResponsibilitiesRule3"};
    	final String[] expectedRuleDocTypes = {"RiceDocument.RuleDocument", "RiceDocument.child1", "RiceDocument.child1child"};
    	final String[] expectedRuleDescriptions = {"A rule with no responsibilities", "Another rule without responsibilities", "A third rule lacking responsibilities"};
    	final String[] personResponsibilities = {"rkirkend", "rkirkend", "user1"};
    	final String[] groupResponsibilities = {"TestWorkgroup", "NonSIT", "TestWorkgroup"};
    	int actualResponsibilitylessRuleCount = 0;
    	List<?> ruleList = null;
    	
    	// First, check that a blank search will retrieve all of the expected responsibility-less rules above.
    	ruleList = KEWServiceLocator.getRuleService().search(null, NULL_ID, null, null, null, null, null, null, null, "");
    	assertNotNull("The returned rule list should not be null", ruleList);
    	for (Iterator<?> ruleIter = ruleList.iterator(); ruleIter.hasNext();) {
    		RuleBaseValues rBaseValues = (RuleBaseValues) ruleIter.next();
    		if (rBaseValues.getResponsibilities() == null || rBaseValues.getResponsibilities().isEmpty()) {
   				actualResponsibilitylessRuleCount++;
    		}
    	}
    	assertEquals("Wrong number of responsibility-less rules found", expectedRuleNames.length, actualResponsibilitylessRuleCount);
    	
    	// Next, test the retrieval of each of these rules independently.
    	for (int i = 0; i < expectedRuleNames.length; i++) {
    		ruleList = KEWServiceLocator.getRuleService().search(expectedRuleDocTypes[i], NULL_ID, null, expectedRuleDescriptions[i], null, null, null, null, null, "");
    		assertNotNull("The returned rule list should not be null when searching for rule '" + expectedRuleNames[i] + "'", ruleList);
    		assertEquals("Exactly one rule should have been retrieved when searching for rule '" + expectedRuleNames[i] + "'", 1, ruleList.size());
    		RuleBaseValues rBaseValues = (RuleBaseValues) ruleList.get(0);
    		assertEquals("The retrieved rule has the wrong name", expectedRuleNames[i], rBaseValues.getName());
    		assertEquals("Rule '" + expectedRuleNames[i] + "' has the wrong doc type name", expectedRuleDocTypes[i], rBaseValues.getDocTypeName());
    		assertEquals("Rule '" + expectedRuleNames[i] + "' has the wrong description", expectedRuleDescriptions[i], rBaseValues.getDescription());
    		assertTrue("Rule '" + expectedRuleNames[i] + "' should not have any responsibilities",
    				rBaseValues.getResponsibilities() == null || rBaseValues.getResponsibilities().isEmpty());
    	}
    	
    	// Verify that when searching for rules with the same doc types but with a person responsibility specified, the responsibility-less rules are not retrieved.
    	for (int i = 0; i < expectedRuleNames.length; i++) {
    		ruleList = KEWServiceLocator.getRuleService().search(expectedRuleDocTypes[i], NULL_ID, null, null, null,
    				KEWServiceLocator.getIdentityHelperService().getPrincipalByPrincipalName(personResponsibilities[i]).getPrincipalId(), null, null, null, "user");
    		assertNotNull("The returned rule list should not be null for doc type '" + expectedRuleDocTypes[i] + "'", ruleList);
    		assertFalse("The returned rule list should not be empty for doc type '" + expectedRuleDocTypes[i] + "'", ruleList.isEmpty());
    		for (Iterator<?> ruleIter = ruleList.iterator(); ruleIter.hasNext();) {
        		RuleBaseValues rBaseValues = (RuleBaseValues) ruleIter.next();
        		assertTrue((new StringBuilder()).append("Found a rule without responsibilities for doc type '").append(
        				expectedRuleDocTypes[i]).append("' and principal '").append(personResponsibilities[i]).append("'").toString(),
        					rBaseValues.getResponsibilities() != null && !rBaseValues.getResponsibilities().isEmpty());
        	}
    	}
    	
    	// Verify that when searching for rules with the same doc types but with a group responsibility specified, the responsibility-less rules are not retrieved.
    	for (int i = 0; i < expectedRuleNames.length; i++) {
    		ruleList = KEWServiceLocator.getRuleService().search(expectedRuleDocTypes[i], NULL_ID, null, null,
    				KEWServiceLocator.getIdentityHelperService().getGroupByName("KR-WKFLW", groupResponsibilities[i]).getId(), null, null, null, null, "");
    		assertNotNull("The returned rule list should not be null for doc type '" + expectedRuleDocTypes[i] + "'", ruleList);
    		assertFalse("The returned rule list should not be empty for doc type '" + expectedRuleDocTypes[i] + "'", ruleList.isEmpty());
    		for (Iterator<?> ruleIter = ruleList.iterator(); ruleIter.hasNext();) {
        		RuleBaseValues rBaseValues = (RuleBaseValues) ruleIter.next();
        		assertTrue((new StringBuilder()).append("Found a rule without responsibilities for doc type '").append(
        				expectedRuleDocTypes[i]).append("' and group '").append(groupResponsibilities[i]).append("' with namespace 'KR-WKFLW'").toString(),
        					rBaseValues.getResponsibilities() != null && !rBaseValues.getResponsibilities().isEmpty());
        	}
    	}
    }
}
