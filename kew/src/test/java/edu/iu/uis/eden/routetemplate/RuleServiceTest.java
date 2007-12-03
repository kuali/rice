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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.kuali.workflow.test.KEWTestCase;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.user.AuthenticationUserId;
import edu.iu.uis.eden.user.WorkflowUser;

public class RuleServiceTest extends KEWTestCase {


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

    @Test public void testReplaceRuleInvolvement() throws Exception {
	RuleBaseValues rule = KEWServiceLocator.getRuleService().getRuleByName("DTR-NotRelated");
	assertNotNull("Rule should exist", rule);

	List<Long> ruleIds = new ArrayList<Long>();
	ruleIds.add(rule.getRuleBaseValuesId());

	KEWServiceLocator.getRuleService().replaceRuleInvolvement(new AuthenticationUserId("ewestfal"), new AuthenticationUserId("rkirkend"), ruleIds, null);

	RuleBaseValues replacedRule = KEWServiceLocator.getRuleService().getRuleByName("DTR-NotRelated");
	assertNotNull("Rule should exist.", replacedRule);
	assertFalse("Rule ids should be different", rule.getRuleBaseValuesId().equals(replacedRule.getRuleBaseValuesId()));
	assertEquals("Previous id of replaced rule should be original rule's id.", rule.getRuleBaseValuesId(), replacedRule.getPreviousVersionId());

	assertEquals("Should have 1 responsibility", 1, replacedRule.getResponsibilities().size());
	WorkflowUser rkirkend = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("rkirkend"));
	RuleResponsibility responsibility = replacedRule.getResponsibility(0);
	assertEquals(replacedRule.getRuleBaseValuesId(), responsibility.getRuleBaseValuesId());
	assertEquals(EdenConstants.RULE_RESPONSIBILITY_WORKFLOW_ID, responsibility.getRuleResponsibilityType());
	assertEquals("Rkirkend should now be on the rule.", rkirkend.getWorkflowId(), responsibility.getRuleResponsibilityName());

	// reload the old rule, verify it's no longer current
	rule = KEWServiceLocator.getRuleService().findRuleBaseValuesById(rule.getRuleBaseValuesId());
	assertFalse("Should not be current", rule.getCurrentInd());

	/**
	 * Test replacing in a rule that has extension values.
	 */

	RuleBaseValues rule2 = KEWServiceLocator.getRuleService().getRuleByName("RuleDocRuleRouting");
	assertEquals("Number of extensions should be 1.", 1, rule2.getRuleExtensions().size());
	assertNotNull(rule2);

	ruleIds.clear();
	ruleIds.add(rule.getRuleBaseValuesId());

	KEWServiceLocator.getRuleService().replaceRuleInvolvement(new AuthenticationUserId("rkirkend"), new AuthenticationUserId("bmcgough"), ruleIds, null);

	RuleBaseValues replacedRule2 = KEWServiceLocator.getRuleService().getRuleByName("RuleDocRuleRouting");
	assertNotNull(replacedRule2);
	// check the extensions
	assertEquals("Number of extensions should be 1.", 1, replacedRule2.getRuleExtensions().size());
	RuleExtension extension = replacedRule2.getRuleExtension(0);
	assertEquals(replacedRule2.getRuleBaseValuesId(), extension.getRuleBaseValuesId());
	assertEquals("Should be 1 extension value.", 1, extension.getExtensionValues().size());
	RuleExtensionValue extensionValue = extension.getExtensionValues().get(0);
	assertEquals("docTypeFullName", extensionValue.getKey());
	assertEquals("TestDocumentType", extensionValue.getValue());

    }

    @Test public void testReplaceRuleInvolvementWithDelegations() throws Exception {
	loadXmlFile("RuleRemoveReplaceWithDelegations.xml");

	// load the parent rule
	RuleBaseValues parentRule = KEWServiceLocator.getRuleService().getRuleByName("RuleWithDelegations1");
	assertNotNull(parentRule);
	assertEquals(1, parentRule.getResponsibilities().size());
	assertEquals(2, parentRule.getResponsibility(0).getDelegationRules().size());
	Long parentRuleId = parentRule.getRuleBaseValuesId();
	Set<Long> delegateIds = new HashSet<Long>();
	delegateIds.add(parentRule.getResponsibility(0).getDelegationRule(0).getDelegationRuleBaseValues().getRuleBaseValuesId());
	delegateIds.add(parentRule.getResponsibility(0).getDelegationRule(1).getDelegationRuleBaseValues().getRuleBaseValuesId());

	// do a replacement on the parent rule
	List<Long> ruleIds = new ArrayList<Long>();
	ruleIds.add(parentRule.getRuleBaseValuesId());
	KEWServiceLocator.getRuleService().replaceRuleInvolvement(new AuthenticationUserId("rkirkend"), new AuthenticationUserId("natjohns"), ruleIds, null);

	// check that the delegations are still there and are still the same id
	parentRule = KEWServiceLocator.getRuleService().getRuleByName("RuleWithDelegations1");
	assertNotNull(parentRule);
	assertFalse("Parent rule should have been re-versioned.", parentRuleId.equals(parentRule.getRuleBaseValuesId()));
	assertEquals(1, parentRule.getResponsibilities().size());
	assertEquals(2, parentRule.getResponsibility(0).getDelegationRules().size());
	assertTrue("Delegation Rule Ids should be the same as before.", delegateIds.contains(parentRule.getResponsibility(0).getDelegationRule(0).getDelegationRuleBaseValues().getRuleBaseValuesId()));
	assertTrue("Delegation Rule Ids should be the same as before.", delegateIds.contains(parentRule.getResponsibility(0).getDelegationRule(1).getDelegationRuleBaseValues().getRuleBaseValuesId()));
	WorkflowUser ewestfal = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("ewestfal"));
	WorkflowUser jhopf = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("jhopf"));
	RuleBaseValues ewestfalDelegation = null;
	RuleBaseValues jhopfDelegation = null;
	for (RuleDelegation delegation : (List<RuleDelegation>)parentRule.getResponsibility(0).getDelegationRules()) {
	    if (delegation.getDelegationRuleBaseValues().getResponsibility(0).getRuleResponsibilityName().equals(ewestfal.getWorkflowId())) {
		ewestfalDelegation = delegation.getDelegationRuleBaseValues();
	    } else if (delegation.getDelegationRuleBaseValues().getResponsibility(0).getRuleResponsibilityName().equals(jhopf.getWorkflowId())) {
		jhopfDelegation = delegation.getDelegationRuleBaseValues();
	    }
	}
	assertNotNull("ewestfal should have a delegation.", ewestfalDelegation);
	assertNotNull("jhopf should have a delegation.", jhopfDelegation);

	// now lets replace someone on one of the delegations
	ruleIds.clear();
	ruleIds.add(ewestfalDelegation.getRuleBaseValuesId());
	parentRuleId = parentRule.getRuleBaseValuesId();
	KEWServiceLocator.getRuleService().replaceRuleInvolvement(new AuthenticationUserId("ewestfal"), new AuthenticationUserId("xqi"), ruleIds, null);
	// verify that the parent rule was properly re-versioned
	parentRule = KEWServiceLocator.getRuleService().getRuleByName("RuleWithDelegations1");
	assertFalse("Parent rule should have been re-versioned.", parentRuleId.equals(parentRule.getRuleBaseValuesId()));
	assertEquals("Parent rule's previous version is incorrect", parentRuleId, parentRule.getPreviousVersionId());
	assertEquals("Should still be 2 delegations", 2, parentRule.getResponsibility(0).getDelegationRules().size());

	WorkflowUser xqi = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("xqi"));
	RuleBaseValues newXqiDelegation = null;
	RuleBaseValues newJhopfDelegation = null;
	for (RuleDelegation delegation : (List<RuleDelegation>)parentRule.getResponsibility(0).getDelegationRules()) {
	    if (delegation.getDelegationRuleBaseValues().getResponsibility(0).getRuleResponsibilityName().equals(xqi.getWorkflowId())) {
		newXqiDelegation = delegation.getDelegationRuleBaseValues();
	    } else if (delegation.getDelegationRuleBaseValues().getResponsibility(0).getRuleResponsibilityName().equals(jhopf.getWorkflowId())) {
		newJhopfDelegation = delegation.getDelegationRuleBaseValues();
	    }
	}
	assertNotNull("xqi should now have a delegation.", newXqiDelegation);
	assertNotNull("jhopf should still have a delegation.", newJhopfDelegation);

	// verify the xqi delegation is a new version with the ewestfal delegation as previous version
	assertTrue("xqi delegation should have different id than ewestfal delegation.", !ewestfalDelegation.getRuleBaseValuesId().equals(newXqiDelegation.getRuleBaseValuesId()));
	assertEquals("xqi delegation should have ewestfal delegation as prevous version.", ewestfalDelegation.getRuleBaseValuesId(), newXqiDelegation.getPreviousVersionId());
	// verify that the new jhopf delegation is the same version as the original jhopf delegation
	assertTrue("new jhopf delegation should be same as orginal jhopf delegation.", jhopfDelegation.getRuleBaseValuesId().equals(newJhopfDelegation.getRuleBaseValuesId()));
    }

    /**
     * Tests a fringe case where someone is their own delegate and does a replacement.
     */
    @Test public void testReplaceRuleInvolvementDelegateToSelf() throws Exception {
	loadXmlFile("RuleRemoveReplaceWithDelegateToSelf.xml");

	// after import, verify there is only 1 of each rule
	List<RuleBaseValues> rules = (List<RuleBaseValues>)KEWServiceLocator.getRuleService().fetchAllRules(false);
	rules.addAll(KEWServiceLocator.getRuleService().fetchAllRules(true));
	int numParents = 0;
	int numDelegates = 0;
	for (RuleBaseValues rule : rules) {
	    if (rule.getRuleTemplate().getName().equals("RuleRouting2")) {
		numParents++;
	    } else if (rule.getRuleTemplate().getName().equals("SimpleTemplate")) {
		numDelegates++;
	    }
	}
	assertEquals(1, numParents);
	assertEquals(1, numDelegates);

	WorkflowUser ewestfal = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("ewestfal"));

	// load the parent rule
	RuleBaseValues parentRule = KEWServiceLocator.getRuleService().getRuleByName("RuleWithDelegateToSelf");
	assertNotNull(parentRule);
	assertEquals(1, parentRule.getResponsibilities().size());
	assertEquals(1, parentRule.getResponsibility(0).getDelegationRules().size());
	assertTrue(parentRule.getResponsibility(0).getRuleResponsibilityName().equals(ewestfal.getWorkflowId()));
	assertTrue(parentRule.getResponsibility(0).getDelegationRule(0).getDelegationRuleBaseValues().getResponsibility(0).getRuleResponsibilityName().equals(ewestfal.getWorkflowId()));
	Long parentRuleId = parentRule.getRuleBaseValuesId();
	Long ruleDelegationId = parentRule.getResponsibility(0).getDelegationRule(0).getRuleDelegationId();
	Long delegateRuleId = parentRule.getResponsibility(0).getDelegationRule(0).getDelegationRuleBaseValues().getRuleBaseValuesId();

	// do a replacement on both rules
	List<Long> ruleIds = new ArrayList<Long>();
	ruleIds.add(delegateRuleId);
	ruleIds.add(parentRuleId);

	KEWServiceLocator.getRuleService().replaceRuleInvolvement(new AuthenticationUserId("ewestfal"), new AuthenticationUserId("rkirkend"), ruleIds, new Long(10000));

	// after first re-versioning, we should have 1 new versions of each rule for a total of 4
	rules = (List<RuleBaseValues>)KEWServiceLocator.getRuleService().fetchAllRules(false);
	rules.addAll(KEWServiceLocator.getRuleService().fetchAllRules(true));
	numParents = 0;
	numDelegates = 0;
	for (RuleBaseValues rule : rules) {
	    if (rule.getRuleTemplate().getName().equals("RuleRouting2")) {
		numParents++;
	    } else if (rule.getRuleTemplate().getName().equals("SimpleTemplate")) {
		numDelegates++;
	    }
	}
	assertEquals(2, numParents);
	assertEquals(2, numDelegates);

	WorkflowUser rkirkend = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("rkirkend"));

	// check that the delegations are still there and are still the same id
	parentRule = KEWServiceLocator.getRuleService().getRuleByName("RuleWithDelegateToSelf");
	assertNotNull(parentRule);
	assertFalse("Parent rule should have been re-versioned.", parentRuleId.equals(parentRule.getRuleBaseValuesId()));
	assertEquals(1, parentRule.getResponsibilities().size());
	assertEquals(1, parentRule.getResponsibility(0).getDelegationRules().size());
	assertEquals(parentRuleId, parentRule.getPreviousVersionId());
	RuleDelegation ruleDelegation = parentRule.getResponsibility(0).getDelegationRule(0);
	assertEquals(delegateRuleId, ruleDelegation.getDelegationRuleBaseValues().getPreviousVersionId());
	assertFalse("Delegate rule should have been re-versioned.", delegateRuleId.equals(ruleDelegation.getDelegationRuleBaseValues().getRuleBaseValuesId()));
	// load both of the previous versions and verify they are no longer current
	RuleBaseValues previousParentRule = KEWServiceLocator.getRuleService().findRuleBaseValuesById(parentRule.getPreviousVersionId());
	RuleBaseValues previousDelegateRule = KEWServiceLocator.getRuleService().findRuleBaseValuesById(ruleDelegation.getDelegationRuleBaseValues().getPreviousVersionId());
	assertFalse(previousParentRule.getCurrentInd());
	assertFalse(previousDelegateRule.getCurrentInd());

	// verify that rkirkend is the new responsibility on both rules
	assertTrue(parentRule.getResponsibility(0).getRuleResponsibilityName().equals(rkirkend.getWorkflowId()));
	assertTrue(ruleDelegation.getDelegationRuleBaseValues().getResponsibility(0).getRuleResponsibilityName().equals(rkirkend.getWorkflowId()));

	// verify that the original RuleDelegation was not deleted from the database, we want to check this because we are removing it from
	// the collection inside of the RuleServiceImpl.createNewRemoveReplaceVersion method.
	RuleDelegation oldRuleDelegation = KEWServiceLocator.getRuleDelegationService().findByRuleDelegationId(ruleDelegationId);
	assertNotNull("Old rule delegation should exist.", oldRuleDelegation);
	assertFalse("rule should be non current", oldRuleDelegation.getDelegationRuleBaseValues().getCurrentInd());
	assertEquals("rule id should be equal to old delegate rule id.", delegateRuleId, oldRuleDelegation.getDelegationRuleBaseValues().getRuleBaseValuesId());

	// reset the ids for the next test
	parentRuleId = parentRule.getRuleBaseValuesId();
	delegateRuleId = parentRule.getResponsibility(0).getDelegationRule(0).getDelegationRuleBaseValues().getRuleBaseValuesId();

	// now let's replace again passing rule ids in reverse order, delegate rule first
	ruleIds.clear();
	ruleIds.add(ruleDelegation.getDelegationRuleBaseValues().getRuleBaseValuesId());
	ruleIds.add(parentRule.getRuleBaseValuesId());
	KEWServiceLocator.getRuleService().replaceRuleInvolvement(new AuthenticationUserId("rkirkend"), new AuthenticationUserId("xqi"), ruleIds, null);

	// after second re-versioning, we should have 1 new version of each rule for a total of 6
	rules = (List<RuleBaseValues>)KEWServiceLocator.getRuleService().fetchAllRules(false);
	rules.addAll(KEWServiceLocator.getRuleService().fetchAllRules(true));
	numParents = 0;
	numDelegates = 0;
	for (RuleBaseValues rule : rules) {
	    if (rule.getRuleTemplate().getName().equals("RuleRouting2")) {
		numParents++;
	    } else if (rule.getRuleTemplate().getName().equals("SimpleTemplate")) {
		numDelegates++;
	    }
	}
	assertEquals(3, numParents);
	assertEquals(3, numDelegates);

	parentRule = KEWServiceLocator.getRuleService().getRuleByName("RuleWithDelegateToSelf");
	assertNotNull(parentRule);
	assertFalse("Parent rule should have been re-versioned.", parentRuleId.equals(parentRule.getRuleBaseValuesId()));
	assertEquals(1, parentRule.getResponsibilities().size());
	assertEquals(1, parentRule.getResponsibility(0).getDelegationRules().size());
	assertEquals(parentRuleId, parentRule.getPreviousVersionId());
	ruleDelegation = parentRule.getResponsibility(0).getDelegationRule(0);
	assertEquals(delegateRuleId, ruleDelegation.getDelegationRuleBaseValues().getPreviousVersionId());
	assertFalse("Delegate rule should have been re-versioned.", delegateRuleId.equals(ruleDelegation.getDelegationRuleBaseValues().getRuleBaseValuesId()));

	parentRuleId = parentRule.getRuleBaseValuesId();
	delegateRuleId = parentRule.getResponsibility(0).getDelegationRule(0).getDelegationRuleBaseValues().getRuleBaseValuesId();

	// now let's try replacing with a user that's not on any of the rules, nothing should happen and rules should NOT be re-versioned
	ruleIds.clear();
	ruleIds.add(ruleDelegation.getDelegationRuleBaseValues().getRuleBaseValuesId());
	ruleIds.add(parentRule.getRuleBaseValuesId());
	KEWServiceLocator.getRuleService().replaceRuleInvolvement(new AuthenticationUserId("ewestfal"), new AuthenticationUserId("rkirkend"), ruleIds, null);

	parentRule = KEWServiceLocator.getRuleService().getRuleByName("RuleWithDelegateToSelf");
	assertNotNull(parentRule);
	assertTrue("Parent rule should NOT have been re-versioned.", parentRuleId.equals(parentRule.getRuleBaseValuesId()));
	ruleDelegation = parentRule.getResponsibility(0).getDelegationRule(0);
	assertTrue("Delegate rule should NOT have been re-versioned.", delegateRuleId.equals(ruleDelegation.getDelegationRuleBaseValues().getRuleBaseValuesId()));
    }

    /**
     * This tests removing involvement of a user from multiple delegation rules on a parent.  The setup includes a parent rule with 4 delegations on it.
     */
    @Test public void testRemoveRuleInvolvementMultipleDelegations() throws Exception {
	loadXmlFile("RuleRemoveReplaceMultipleDelegations.xml");

	// load the parent rule
	RuleBaseValues parentRule = KEWServiceLocator.getRuleService().getRuleByName("RuleWithMultipleDelegations1");
	assertNotNull(parentRule);
	assertEquals(2, parentRule.getResponsibilities().size());
	assertEquals(3, parentRule.getResponsibility(0).getDelegationRules().size());
	assertEquals(0, parentRule.getResponsibility(1).getDelegationRules().size());
	Long parentRuleId = parentRule.getRuleBaseValuesId();
	Long ruleDelegationId1 = parentRule.getResponsibility(0).getDelegationRule(0).getRuleDelegationId();
	Long ruleDelegationId2 = parentRule.getResponsibility(0).getDelegationRule(1).getRuleDelegationId();
	Long ruleDelegationId3 = parentRule.getResponsibility(0).getDelegationRule(2).getRuleDelegationId();
	Long delegateRuleId1 = parentRule.getResponsibility(0).getDelegationRule(0).getDelegationRuleBaseValues().getRuleBaseValuesId();
	Long delegateRuleId2 = parentRule.getResponsibility(0).getDelegationRule(1).getDelegationRuleBaseValues().getRuleBaseValuesId();
	Long delegateRuleId3 = parentRule.getResponsibility(0).getDelegationRule(2).getDelegationRuleBaseValues().getRuleBaseValuesId();

	List<Long> ruleIds = new ArrayList<Long>();
	ruleIds.add(parentRule.getRuleBaseValuesId());
	ruleIds.add(delegateRuleId1);
	ruleIds.add(delegateRuleId2);
	ruleIds.add(delegateRuleId3);

	KEWServiceLocator.getRuleService().removeRuleInvolvement(new AuthenticationUserId("jhopf"), ruleIds, new Long(10001));

	// re-fetch the parent rule and lets check it
	parentRule = KEWServiceLocator.getRuleService().getRuleByName("RuleWithMultipleDelegations1");
	assertNotNull(parentRule);

	// TODO improve this, add some more assertions
    }

}
