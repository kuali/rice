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
package mocks;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import mocks.elements.UniversityOrganizationElement;

import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import edu.iu.uis.eden.WorkflowServiceErrorImpl;
import edu.iu.uis.eden.docsearch.SearchableAttributeStringValue;
import edu.iu.uis.eden.docsearch.SearchableAttributeValue;
import edu.iu.uis.eden.lookupable.Field;
import edu.iu.uis.eden.lookupable.Row;
import edu.iu.uis.eden.plugin.attributes.WorkflowAttribute;
import edu.iu.uis.eden.routeheader.DocumentContent;
import edu.iu.uis.eden.routetemplate.RuleExtension;
import edu.iu.uis.eden.routetemplate.RuleExtensionValue;
import edu.iu.uis.eden.util.XmlHelper;

/**
 * A Mock attribute which mimics the concept of a Chart/Org hierarchy as it exists at Indiana University.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class MockChartOrgAttribute implements WorkflowAttribute {

	private MockChartOrgService orgService = new MockChartOrgService();
    private List<Row> rows;
    private static final String FIN_COA_CD_KEY = "fin_coa_cd";
    private static final String CHART_ORG_FIN_COA_CD_KEY = "chart_org_fin_coa_cd";

    private static final String ORG_CD_KEY = "org_cd";
    private static final String CHART_ORG_ORG_CD_KEY = "chart_org_org_cd";

    private String finCoaCd;
    private String orgCd;
    private boolean required;

    public MockChartOrgAttribute(String finCoaCd, String orgCd) {
        this();
        this.finCoaCd = finCoaCd;
        this.orgCd = orgCd;
    }

    public MockChartOrgAttribute() {
        rows = new ArrayList<Row>();

        List<Field> fields = new ArrayList<Field>();
        fields.add(new Field("Chart", "", Field.TEXT, true, CHART_ORG_FIN_COA_CD_KEY, "", null, "ChartOrgLookupableImplService", FIN_COA_CD_KEY));
        rows.add(new Row(fields, "Chart & Org", 2));

        fields = new ArrayList<Field>();
        fields.add(new Field("Org", "", Field.TEXT, true, CHART_ORG_ORG_CD_KEY, "", null, "ChartOrgLookupableImplService", ORG_CD_KEY));
        fields.add(new Field("", "", Field.QUICKFINDER, false, "", "", null, "ChartOrgLookupableImplService"));
        rows.add(new Row(fields, "Chart & Org", 2));
    }

    public List getRuleExtensionValues() {
        List extensions = new ArrayList();

        if (finCoaCd != null && !finCoaCd.equals("")) {
            RuleExtensionValue extensionFinCoaCd = new RuleExtensionValue();
            extensionFinCoaCd.setKey(FIN_COA_CD_KEY);
            extensionFinCoaCd.setValue(this.finCoaCd);
            extensions.add(extensionFinCoaCd);
        }
        if (orgCd != null && !orgCd.equals("")) {
            RuleExtensionValue extensionOrgCd = new RuleExtensionValue();
            extensionOrgCd.setKey(ORG_CD_KEY);
            extensionOrgCd.setValue(this.orgCd);
            extensions.add(extensionOrgCd);
        }
        return extensions;
    }

    public List validateRoutingData(Map paramMap) {
        List errors = new ArrayList();
        this.finCoaCd = (String) paramMap.get(CHART_ORG_FIN_COA_CD_KEY);
        this.orgCd = (String) paramMap.get(CHART_ORG_ORG_CD_KEY);
        if (isRequired() && (this.finCoaCd == null || "".equals(finCoaCd) || (this.orgCd == null || "".equals(orgCd)))) {
            errors.add(new WorkflowServiceErrorImpl("Chart/org is required.", "routetemplate.chartorgattribute.chartorg.required"));
        } else if ((this.finCoaCd != null && !"".equals(finCoaCd) && ((this.orgCd == null || "".equals(orgCd)))) || ((this.finCoaCd == null || "".equals(finCoaCd)) && this.orgCd != null && !"".equals(orgCd))) {
            errors.add(new WorkflowServiceErrorImpl("Chart/org is invalid.", "routetemplate.chartorgattribute.chartorg.invalid"));
        }

        if (this.finCoaCd != null && !"".equals(finCoaCd) && this.orgCd != null && !"".equals(orgCd)) {
            MockOrganization org = getOrgService().findOrganization(finCoaCd, orgCd);
            if (org == null) {
                errors.add(new WorkflowServiceErrorImpl("Chart/org is invalid.", "routetemplate.chartorgattribute.chartorg.invalid"));
            }
        }
        return errors;
    }

    public List validateRuleData(Map paramMap) {
        return validateRoutingData(paramMap);
    }

    public String getDocContent() {
        if (getFinCoaCd() != null && !getFinCoaCd().equals("") && getOrgCd() != null && !getOrgCd().equals("")) {
            UniversityOrganizationElement univOrgElement = new UniversityOrganizationElement();
            univOrgElement.setChart(getFinCoaCd());
            univOrgElement.setOrgCode(getOrgCd());
            return new XMLOutputter().outputString(univOrgElement.getXMLContent());
        } else {
            throw new RuntimeException("Missing chart or org properties on attribute when getting document content");
        }
    }

    public List parseDocContent(String docContent) {
        List chartOrgModels = new ArrayList();
        UniversityOrganizationElement univOrgElement = new UniversityOrganizationElement();
        Element rootElement = null;
        try {
            rootElement = XmlHelper.buildJDocument(new StringReader(docContent)).getRootElement();
        } catch (Exception e) {
            throw new RuntimeException("Invalid XML submitted", e);
        }
        List chartOrgElements = XmlHelper.findElements(rootElement, univOrgElement.getElementName());
        for (Iterator iter = chartOrgElements.iterator(); iter.hasNext();) {
            Element chartOrgElement = (Element) iter.next();
            try {
                univOrgElement.loadFromXMLContent(chartOrgElement, false);
            } catch (Exception e) {
                throw new RuntimeException("Problems loading chart org element from string " + docContent, e);
            }
            chartOrgModels.add(new MockChartOrgAttribute(univOrgElement.getChart(), univOrgElement.getOrgCode()));
        }
        return chartOrgModels;
    }

    public boolean isMatch(DocumentContent docContent, List ruleExtensions) {
    	boolean foundChartOrgExtension = false;
        for (Iterator iter = ruleExtensions.iterator(); iter.hasNext();) {
            RuleExtension extension = (RuleExtension) iter.next();
            String className = getClass().getName();
            if (extension.getRuleTemplateAttribute().getRuleAttribute().getClassName().equals(className)) {
                for (Iterator iterator = extension.getExtensionValues().iterator(); iterator.hasNext();) {
                    RuleExtensionValue value = (RuleExtensionValue) iterator.next();
                    if (value.getKey().equals(FIN_COA_CD_KEY)) {
                    	foundChartOrgExtension = true;
                        setFinCoaCd(value.getValue());
                    }
                    if (value.getKey().equals(ORG_CD_KEY)) {
                    	foundChartOrgExtension = true;
                    	setOrgCd(value.getValue());
                    }
                }
            }
        }

        List chartOrgValues = null;//populateFromDocContent(docContent);
        for (Iterator iter = chartOrgValues.iterator(); iter.hasNext();) {
            MockChartOrgAttribute attribute = (MockChartOrgAttribute) iter.next();
            if (attribute.getFinCoaCd().equals(this.getFinCoaCd()) && attribute.getOrgCd().equals(this.getOrgCd())) {
                return true;
            }
        }

        if (ruleExtensions.isEmpty() || !foundChartOrgExtension) {
            return true;
        }
        return false;
    }

//    private void buildOrgReviewHierarchy(List chartOrgList, MockChartOrgAttribute chartOrg, Set visitedOrgs) {
//
//        Organization org = IUServiceLocator.getFISDataService().findOrganization(chartOrg.getFinCoaCd(), chartOrg.getOrgCd());
//
//        if (! org.hasParent()) {
//            return;
//        }
//        String orgVisitedKey = org.getFinCoaCd() + "-" + org.getOrgCd();
//        if (visitedOrgs.contains(orgVisitedKey)) {
//            throw new RuntimeException("Org hierarchy loop detected. org " + orgVisitedKey);
//        } else {
//            visitedOrgs.add(orgVisitedKey);
//        }
//        MockChartOrgAttribute parent = new MockChartOrgAttribute();
//        parent.setFinCoaCd(org.getReportsToChart());
//        parent.setOrgCd(org.getReportsToOrg());
//        //TODO this list could probably be whacked and the ojb mappings used instead...
//        chartOrgList.add(parent);
//        buildOrgReviewHierarchy(chartOrgList, parent, visitedOrgs);
//    }

//    private List populateFromDocContent(DocumentContent docContent) {
//        List chartOrgValues = new ArrayList();
//        UniversityOrganizationElement univOrgElement = new UniversityOrganizationElement();
//        Element rootElement = null;
//        try {
//            rootElement = XmlHelper.buildJDocument(docContent.getDocument()).getRootElement();
//        } catch (Exception e) {
//            throw new WorkflowServiceErrorException("Invalid XML submitted", new ArrayList());
//        }
//        List chartOrgElements = XmlHelper.findElements(rootElement, univOrgElement.getElementName());
//        for (Iterator iter = chartOrgElements.iterator(); iter.hasNext();) {
//            Element chartOrgElement = (Element) iter.next();
//            try {
//                univOrgElement.loadFromXMLContent(chartOrgElement, false);
//            } catch (Exception e) {
//                throw new WorkflowServiceErrorException("Problems loading chart org element from string " + docContent, new ArrayList());
//            }
//            MockChartOrgAttribute chartOrg = new MockChartOrgAttribute();
//            chartOrg.setFinCoaCd(univOrgElement.getChart());
//            chartOrg.setOrgCd(univOrgElement.getOrgCode());
//            chartOrgValues.add(chartOrg);
//            buildOrgReviewHierarchy(chartOrgValues, chartOrg, new HashSet());
//        }
//        return chartOrgValues;
//    }

    public List getRuleRows() {
        return rows;
    }

    public List getRoutingDataRows() {
        return rows;
    }

    public String getFinCoaCd() {
        return this.finCoaCd;
    }

    public void setFinCoaCd(String finCoaCd) {
        this.finCoaCd = finCoaCd;
    }

    public String getOrgCd() {
        return this.orgCd;
    }

    public void setOrgCd(String orgCd) {
        this.orgCd = orgCd;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

	/* (non-Javadoc)
	 * @see edu.iu.uis.eden.docsearch.SearchableAttribute#getSearchContent()
	 */
	public String getSearchContent() {
		return getDocContent();
	}

	/* (non-Javadoc)
	 * @see edu.iu.uis.eden.docsearch.SearchableAttribute#getSearchStorageValues()
	 */
	public List getSearchStorageValues(String docContent) {
        List searchStorageValues = new ArrayList();
//TODO the variables finCoaCd and orgCd may not be populated.  Values may only be in the incoming doc content.
        if (finCoaCd != null && !finCoaCd.equals("")) {
        	SearchableAttributeValue searchableFinCoaCd = new SearchableAttributeStringValue();
        	searchableFinCoaCd.setSearchableAttributeKey(FIN_COA_CD_KEY);
        	searchableFinCoaCd.setupAttributeValue(this.finCoaCd);
            searchStorageValues.add(searchableFinCoaCd);
        }
        if (orgCd != null && !orgCd.equals("")) {
        	SearchableAttributeValue searchableOrgCd = new SearchableAttributeStringValue();
            searchableOrgCd.setSearchableAttributeKey(ORG_CD_KEY);
            searchableOrgCd.setupAttributeValue(this.orgCd);
            searchStorageValues.add(searchableOrgCd);
        }
        return searchStorageValues;
	}

	public List validateClientRoutingData() {
        List errors = new ArrayList();
//        if (isRequired() && (this.finCoaCd == null || "".equals(finCoaCd) || (this.orgCd == null || "".equals(orgCd)))) {
//            errors.add(new WorkflowAttributeValidationError("invalid.org", "Organization is invalid"));
//        } else if ((this.finCoaCd != null && !"".equals(finCoaCd) && ((this.orgCd == null || "".equals(orgCd)))) || ((this.finCoaCd == null || "".equals(finCoaCd)) && this.orgCd != null && !"".equals(orgCd))) {
//            errors.add(new WorkflowAttributeValidationError("invalid.org", "Organization is invalid"));
//        }
//
//        if (this.finCoaCd != null && !"".equals(finCoaCd) && this.orgCd != null && !"".equals(orgCd)) {
//            Organization org = null;//IUServiceLocator.getFISDataService().findOrganization(finCoaCd, orgCd);
//            if (org == null) {
//                errors.add(new WorkflowAttributeValidationError("invalid.org", "Organization is invalid"));
//            }
//        }
        return errors;
    }

    public List getSearchingRows() {
		return rows;
	}

	public List validateUserSearchInputs(Map paramMap) {
		return validateRoutingData(paramMap);
	}

	protected MockChartOrgService getOrgService() {
		return orgService;
	}
}