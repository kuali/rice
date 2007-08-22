/*
 * Copyright 2005-2007 The Kuali Foundation.
 * 
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.iu.uis.eden.routetemplate;

import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.kuali.workflow.test.WorkflowTestCase;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.doctype.DocumentType;

public class RuleServiceTest extends WorkflowTestCase {

	
    protected void loadTestData() throws Exception {
        loadXmlFile("RouteTemplateConfig.xml");
    }
    
    @Test public void testClearCacheWithDocumentTypeUpdate() throws Exception {
    	//put the rules in the cache by routing documents
    	WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("rkirkend"), "EDENSERVICE-DOCS.child1");
    	document.routeDocument("");
    	document = new WorkflowDocument(new NetworkIdVO("rkirkend"), "EDENSERVICE-DOCS.child1child");
    	document.routeDocument("");
    	document = new WorkflowDocument(new NetworkIdVO("rkirkend"), "EDENSERVICE-DOCS.child2");
    	document.routeDocument("");
    	document = new WorkflowDocument(new NetworkIdVO("rkirkend"), "EDENSERVICE-DOCS.child3");
    	document.routeDocument("");
    	document = new WorkflowDocument(new NetworkIdVO("rkirkend"), "NotRelated");
    	document.routeDocument("");
    	
    	//verify the cache's contents are correct
    	List<RuleBaseValues> rulesCached = getListFromCache("DocumentTypeRouting", "EDENSERVICE-DOCS.child1");
    	assertEquals("Wrong number of rules cached", 1, rulesCached.size());
    	RuleBaseValues child1RuleFirstCached = (RuleBaseValues)rulesCached.get(0);
    	
    	rulesCached = getListFromCache("DocumentTypeRouting", "EDENSERVICE-DOCS.child1child");
    	assertEquals("Wrong number of rules cached", 2, rulesCached.size());
    	RuleBaseValues child1childRuleFirstCached = null;
    	// make sure this has the right rules which is the rule to the document type above and it's parent
    	for (Iterator iter = rulesCached.iterator(); iter.hasNext();) {
			RuleBaseValues rule = (RuleBaseValues) iter.next();
			if (!(rule.getDocTypeName().equals("EDENSERVICE-DOCS.child1") || rule.getDocTypeName().equals("EDENSERVICE-DOCS.child1child"))) {
				fail("Wrong rule in the cache");
			}
			if (rule.getDocTypeName().equals("EDENSERVICE-DOCS.child1child")) {
				child1childRuleFirstCached = rule;
			}
		}
    	rulesCached = getListFromCache("DocumentTypeRouting", "EDENSERVICE-DOCS.child2");
    	assertEquals("Wrong number of rules cached", 1, rulesCached.size());
    	RuleBaseValues child2RuleFirstCached = (RuleBaseValues)rulesCached.get(0);
    	
    	rulesCached = getListFromCache("DocumentTypeRouting", "EDENSERVICE-DOCS.child3");
    	assertEquals("Wrong number of rules cached", 1, rulesCached.size());
    	RuleBaseValues child3RuleFirstCached = (RuleBaseValues)rulesCached.get(0);
    	
    	rulesCached = getListFromCache("DocumentTypeRouting", "NotRelated");
    	assertEquals("Wrong number of rules cached", 1, rulesCached.size());
    	RuleBaseValues notRelatedRuleFirstCached = (RuleBaseValues)rulesCached.get(0);
    	
    	/**
    	 * Import a replacement document type from xml.  This should clear out all rules within the
    	 * DocumentType hierarchy of EDENSERVICE-DOCS.
    	 * 
    	 * This will upload a new child document type to child1child and then update the child3 and child1child document types.
    	 */
    	
    	DocumentType root = KEWServiceLocator.getDocumentTypeService().findByName("EDENSERVICE-DOCS");
    	
    	loadXmlFile("DocumentTypeToImportForCacheTest.xml");
    	
    	root = KEWServiceLocator.getDocumentTypeService().findByName("EDENSERVICE-DOCS");
    	
    	/**
    	 * Check the EDENSERVICE-DOCS.child1 rules.
    	 */
    	
    	rulesCached = getListFromCache("DocumentTypeRouting", "EDENSERVICE-DOCS.child1");
    	assertNull("The rules should not be cached.", rulesCached);
    	
    	// now fetch the rules to put them in the cache
    	List rulesFetched = KEWServiceLocator.getRuleService().fetchAllCurrentRulesForTemplateDocCombination("DocumentTypeRouting", "EDENSERVICE-DOCS.child1");
    	assertNotNull("List of rules should be fetched from the service.", rulesFetched);
    	assertEquals("Wrong number of rules fetched", 1, rulesFetched.size());    	
    	
    	// now fetch from cache again
    	rulesCached = getListFromCache("DocumentTypeRouting", "EDENSERVICE-DOCS.child1");
    	assertNotNull("The rules should be cached.", rulesCached);
    	assertEquals("Wrong number of rules cached", 1, rulesCached.size());    	
    	RuleBaseValues child1RuleSecondCached = (RuleBaseValues)rulesCached.get(0);
    	assertFalse("These rules should be different because the cache was updated", child1RuleFirstCached.equals(child1RuleSecondCached));
    	assertEquals("The rule ids should be the same.", child1RuleFirstCached.getRuleBaseValuesId(), child1RuleSecondCached.getRuleBaseValuesId());

    	/**
    	 * Check the EDENSERVICE-DOCS.child2 rules.
    	 */
    	
    	rulesCached = getListFromCache("DocumentTypeRouting", "EDENSERVICE-DOCS.child2");
    	assertNull("The rules should not be cached.", rulesCached);
    	
    	// now fetch the rules to put them in the cache
    	rulesFetched = KEWServiceLocator.getRuleService().fetchAllCurrentRulesForTemplateDocCombination("DocumentTypeRouting", "EDENSERVICE-DOCS.child2");
    	assertNotNull("List of rules should be fetched from the service.", rulesFetched);
    	assertEquals("Wrong number of rules fetched", 1, rulesFetched.size());
    	
    	// now fetch from cache again
    	rulesCached = getListFromCache("DocumentTypeRouting", "EDENSERVICE-DOCS.child2");
    	assertNotNull("The rules should be cached.", rulesCached);
    	assertEquals("Wrong number of rules cached", 1, rulesCached.size());
    	RuleBaseValues child2RuleSecondCached = (RuleBaseValues)rulesCached.get(0);
    	assertFalse("These rules should be different because the cache was updated", child2RuleFirstCached.equals(child2RuleSecondCached));
    	assertEquals("The rule ids should be the same.", child2RuleFirstCached.getRuleBaseValuesId(), child2RuleSecondCached.getRuleBaseValuesId());
    	
    	/**
    	 * Check EDENSERVICE-DOCS.child3 rules.
    	 */
    	
    	rulesCached = getListFromCache("DocumentTypeRouting", "EDENSERVICE-DOCS.child3");
    	assertNull("The rules should not be cached.", rulesCached);
    	
    	// now fetch the rules to put them in the cache
    	rulesFetched = KEWServiceLocator.getRuleService().fetchAllCurrentRulesForTemplateDocCombination("DocumentTypeRouting", "EDENSERVICE-DOCS.child3");
    	assertNotNull("List of rules should be fetched from the service.", rulesFetched);
    	assertEquals("Wrong number of rules fetched", 1, rulesFetched.size());
    	
    	// now fetch from cache again
    	rulesCached = getListFromCache("DocumentTypeRouting", "EDENSERVICE-DOCS.child3");
    	assertEquals("Wrong number of rules cached", 1, rulesCached.size());
    	RuleBaseValues child3RuleSecondCached = (RuleBaseValues)rulesCached.get(0);
    	assertFalse("These rules should be different because the cache was updated.", child3RuleFirstCached.equals(child3RuleSecondCached));
    	assertEquals("The rule ids should be the same.", child3RuleFirstCached.getRuleBaseValuesId(), child3RuleSecondCached.getRuleBaseValuesId());

    	/**
    	 * Check the EDENSERVICE-DOCS.child1child rules.
    	 */
    	
    	rulesCached = getListFromCache("DocumentTypeRouting", "EDENSERVICE-DOCS.child1child");
    	assertNull("The rules should not be cached.", rulesCached);
    	
    	// now fetch the rules to put them in the cache
    	rulesFetched = KEWServiceLocator.getRuleService().fetchAllCurrentRulesForTemplateDocCombination("DocumentTypeRouting", "EDENSERVICE-DOCS.child1child");
    	assertNotNull("List of rules should be fetched from the service.", rulesFetched);
    	assertEquals("Wrong number of rules fetched", 2, rulesFetched.size());
    	
    	// fetch from cache again
    	rulesCached = getListFromCache("DocumentTypeRouting", "EDENSERVICE-DOCS.child1child");
    	assertEquals("Wrong number of rules cached", 2, rulesCached.size());
    	RuleBaseValues child1childRuleSecondCached = null;
    	//make sure this has the right rules which is the rule to the document type above and it's parent
    	for (Iterator iter = rulesCached.iterator(); iter.hasNext();) {
			RuleBaseValues rule = (RuleBaseValues) iter.next();
			if (!(rule.getDocTypeName().equals("EDENSERVICE-DOCS.child1") || rule.getDocTypeName().equals("EDENSERVICE-DOCS.child1child"))) {
				fail("Wrong rule in the cache");
			}
			if (rule.getDocTypeName().equals("EDENSERVICE-DOCS.child1child")) {
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
    	 * Grab the EDENSERVICE-DOCS.child1child1child rules from the cache, they should initially be null since
    	 * this is a brand new document type.
    	 * 
    	 * After fetching them we should see the rules from EDENSERVICE-DOCS.child1, EDENSERVICE-DOCS.child1child,
    	 * and EDENSERVICE-DOCS.child1child1child.
    	 */
    	
    	rulesCached = getListFromCache("DocumentTypeRouting", "EDENSERVICE-DOCS.child1child1child");
    	assertNull("Rules should not be cached yet.", rulesCached);
    	
    	// routing a doc should put the rules into the cache
    	document = new WorkflowDocument(new NetworkIdVO("rkirkend"), "EDENSERVICE-DOCS.child1child1child");
    	document.routeDocument("");
    	
    	rulesCached = getListFromCache("DocumentTypeRouting", "EDENSERVICE-DOCS.child1child1child");
    	assertNotNull(rulesCached);
    	assertEquals(3, rulesCached.size());
    	boolean hasRule1 = false;
    	boolean hasRule2 = false;
    	boolean hasRule3 = false;
    	for (RuleBaseValues rule : rulesCached) {
    		if (rule.getDocTypeName().equals("EDENSERVICE-DOCS.child1")) {
    			hasRule1 = true;
    		} else if (rule.getDocTypeName().equals("EDENSERVICE-DOCS.child1child")) {
    			hasRule2 = true;
    		} else if (rule.getDocTypeName().equals("EDENSERVICE-DOCS.child1child1child")) {
    			hasRule3 = true;
    		}
    	}
    	assertTrue("Should have had EDENSERVICE-DOCS.child1 rule", hasRule1);
    	assertTrue("Should have had EDENSERVICE-DOCS.child1child rule", hasRule2);
    	assertTrue("Should have had EDENSERVICE-DOCS.child1child1child rule", hasRule3);
    	
    	/**
    	 * Import a new rule for EDENSERVICE-DOCS and verify that the entire hierarchy is flushed
    	 * from the cache.
    	 */
    	
    	// first verify that there are no rules for EDENSERVICE-DOCS
    	rulesFetched = KEWServiceLocator.getRuleService().fetchAllCurrentRulesForTemplateDocCombination("DocumentTypeRouting", "EDENSERVICE-DOCS");
    	assertNotNull("The list should not be null.", rulesFetched);
    	assertEquals("The list should be empty.", 0, rulesFetched.size());
    	
    	loadXmlFile("NewEdenserviceDocsRule.xml");
    	
    	// verify that all rules for doc types in the hierarchy have been flushed from the cache
    	
    	rulesCached = getListFromCache("DocumentTypeRouting", "EDENSERVICE-DOCS");
    	assertNull("There should be no cache entry for EDENSERVICE-DOCS", rulesCached);
    	
    	rulesCached = getListFromCache("DocumentTypeRouting", "EDENSERVICE-DOCS.child1");
    	assertNull("There should be no cache entry for EDENSERVICE-DOCS.child1", rulesCached);
    	
    	rulesCached = getListFromCache("DocumentTypeRouting", "EDENSERVICE-DOCS.child2");
    	assertNull("There should be no cache entry for EDENSERVICE-DOCS.child2", rulesCached);
    	
    	rulesCached = getListFromCache("DocumentTypeRouting", "EDENSERVICE-DOCS.child3");
    	assertNull("There should be no cache entry for EDENSERVICE-DOCS.child3", rulesCached);
    	
    	rulesCached = getListFromCache("DocumentTypeRouting", "EDENSERVICE-DOCS.child1child");
    	assertNull("There should be no cache entry for EDENSERVICE-DOCS.child1child", rulesCached);
    	
    	rulesCached = getListFromCache("DocumentTypeRouting", "EDENSERVICE-DOCS.child1child1child");
    	assertNull("There should be no cache entry for EDENSERVICE-DOCS.child1child1child", rulesCached);
    	    	
    	// now fetch them from service so they are cached
    	rulesFetched = KEWServiceLocator.getRuleService().fetchAllCurrentRulesForTemplateDocCombination("DocumentTypeRouting", "EDENSERVICE-DOCS");
    	assertNotNull("The list should not be null.", rulesFetched);
    	assertEquals("The list should contain a single rule.", 1, rulesFetched.size());
    	
    	// fetch the cache now and verify the same
    	rulesCached = getListFromCache("DocumentTypeRouting", "EDENSERVICE-DOCS");
    	assertNotNull("The list should not be null.", rulesCached);
    	assertEquals("The list should contain a single rule.", 1, rulesCached.size());
    	
    	/**
    	 * Since we imported a rule at the top of the document hierarchy and the Lists of rules cached
    	 * also include all rules from parent document types, each of the rule lists should be increased by one
    	 */
    	
    	// EDENSERVICE-DOCS.child1 should now have 2 rules
    	rulesFetched = KEWServiceLocator.getRuleService().fetchAllCurrentRulesForTemplateDocCombination("DocumentTypeRouting", "EDENSERVICE-DOCS.child1");
    	assertEquals(2, rulesFetched.size());
    	rulesCached = getListFromCache("DocumentTypeRouting", "EDENSERVICE-DOCS.child1");
    	assertEquals(2, rulesCached.size());
    	
    	// EDENSERVICE-DOCS.child2 should now have 2 rules
    	rulesFetched = KEWServiceLocator.getRuleService().fetchAllCurrentRulesForTemplateDocCombination("DocumentTypeRouting", "EDENSERVICE-DOCS.child2");
    	assertEquals(2, rulesFetched.size());
    	rulesCached = getListFromCache("DocumentTypeRouting", "EDENSERVICE-DOCS.child2");
    	assertEquals(2, rulesCached.size());
    	
    	// EDENSERVICE-DOCS.child3 should now have 2 rules
    	rulesFetched = KEWServiceLocator.getRuleService().fetchAllCurrentRulesForTemplateDocCombination("DocumentTypeRouting", "EDENSERVICE-DOCS.child3");
    	assertEquals(2, rulesFetched.size());
    	rulesCached = getListFromCache("DocumentTypeRouting", "EDENSERVICE-DOCS.child3");
    	assertEquals(2, rulesCached.size());
    	
    	// EDENSERVICE-DOCS.child1child should now have 3 rules
    	rulesFetched = KEWServiceLocator.getRuleService().fetchAllCurrentRulesForTemplateDocCombination("DocumentTypeRouting", "EDENSERVICE-DOCS.child1child");
    	assertEquals(3, rulesFetched.size());
    	rulesCached = getListFromCache("DocumentTypeRouting", "EDENSERVICE-DOCS.child1child");
    	assertEquals(3, rulesCached.size());
    	
    	// EDENSERVICE-DOCS.child1child1child should now have 4 rules
    	rulesFetched = KEWServiceLocator.getRuleService().fetchAllCurrentRulesForTemplateDocCombination("DocumentTypeRouting", "EDENSERVICE-DOCS.child1child1child");
    	assertEquals(4, rulesFetched.size());
    	rulesCached = getListFromCache("DocumentTypeRouting", "EDENSERVICE-DOCS.child1child1child");
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
    
}
