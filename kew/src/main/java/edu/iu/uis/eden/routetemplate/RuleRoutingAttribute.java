/*
 * Copyright 2005-2006 The Kuali Foundation.
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.WorkflowServiceErrorImpl;
import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.doctype.DocumentTypeService;
import edu.iu.uis.eden.lookupable.Field;
import edu.iu.uis.eden.lookupable.Row;
import edu.iu.uis.eden.plugin.attributes.WorkflowAttribute;
import edu.iu.uis.eden.routeheader.DocumentContent;
import edu.iu.uis.eden.util.Utilities;
import edu.iu.uis.eden.util.XmlHelper;

/**
 * A {@link WorkflowAttribute} which is used to route a rule based on the
 * {@link DocumentType} of the rule which is created.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RuleRoutingAttribute implements WorkflowAttribute {

	private static final long serialVersionUID = -8884711461398770563L;

	private static final String DOC_TYPE_NAME_PROPERTY = "doc_type_name";
    private static final String DOC_TYPE_NAME_KEY = "docTypeFullName";

    private static final String LOOKUPABLE_CLASS = "DocumentTypeLookupableImplService";
    private static final String DOC_TYPE_NAME_LABEL = "Document type name";

    private String doctypeName;
    private List rows;
    private boolean required;

    public RuleRoutingAttribute(String docTypeName) {
        this();
        setDoctypeName(docTypeName);
    }

    public RuleRoutingAttribute() {
        buildRows();
    }

    private void buildRows() {
        rows = new ArrayList();

        List fields = new ArrayList();
        fields.add(new Field(DOC_TYPE_NAME_LABEL, "", Field.TEXT, true, DOC_TYPE_NAME_PROPERTY, "", null, LOOKUPABLE_CLASS, DOC_TYPE_NAME_KEY));
        fields.add(new Field("", "", Field.QUICKFINDER, false, "", "", null, LOOKUPABLE_CLASS));
        rows.add(new Row(fields));
    }

    public boolean isMatch(DocumentContent docContent, List ruleExtensions) {
	setDoctypeName(getRuleDocumentTypeFromRuleExtensions(ruleExtensions));
        DocumentTypeService service = (DocumentTypeService) KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE);
        List documentTypeValues = parseDocContent(docContent);
        for (Iterator iterator = documentTypeValues.iterator(); iterator.hasNext();) {
            RuleRoutingAttribute attribute = (RuleRoutingAttribute) iterator.next();
            if (attribute.getDoctypeName().equals(getDoctypeName())) {
                return true;
            }
            DocumentType documentType = service.findByName(attribute.getDoctypeName());
            while (documentType != null && documentType.getParentDocType() != null) {
                documentType = documentType.getParentDocType();
                if(documentType.getName().equals(getDoctypeName())){
                    return true;
                }
            }
        }

        if (ruleExtensions.isEmpty()) {
            return true;
        }

        return false;
    }

    protected String getRuleDocumentTypeFromRuleExtensions(List ruleExtensions) {
	for (Iterator extensionsIterator = ruleExtensions.iterator(); extensionsIterator.hasNext();) {
            RuleExtension extension = (RuleExtension) extensionsIterator.next();
            if (extension.getRuleTemplateAttribute().getRuleAttribute().getClassName().equals(getClass().getName())) {
                for (Iterator valuesIterator = extension.getExtensionValues().iterator(); valuesIterator.hasNext();) {
                    RuleExtensionValue extensionValue = (RuleExtensionValue) valuesIterator.next();
                    String key = extensionValue.getKey();
                    String value = extensionValue.getValue();
                    if (key.equals(DOC_TYPE_NAME_KEY)) {
                        return value;
                    }
                }
            }
        }
	return null;
    }

    public List getRuleRows() {
        return rows;
    }

    public List getRoutingDataRows() {
        return rows;
    }

    public String getDocContent() {
        if (!Utilities.isEmpty(getDoctypeName())) {
            return "<ruleRouting><doctype>" + getDoctypeName() + "</doctype></ruleRouting>";
        } else {
            return "";
        }
    }

    public List<RuleRoutingAttribute> parseDocContent(DocumentContent docContent) {
        try {
            Document doc = XmlHelper.buildJDocument(docContent.getDocument());

            List<RuleRoutingAttribute> doctypeAttributes = new ArrayList<RuleRoutingAttribute>();
            List ruleRoutings = XmlHelper.findElements(doc.getRootElement(), "ruleRouting");
            for (Iterator iter = ruleRoutings.iterator(); iter.hasNext();) {
                Element ruleRoutingElement = (Element) iter.next();

                Element docTypeElement = ruleRoutingElement.getChild("doctype");
                if (docTypeElement != null) {
                    doctypeAttributes.add(new RuleRoutingAttribute(docTypeElement.getText()));
                }
            }

            return doctypeAttributes;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List getRuleExtensionValues() {
        List extensions = new ArrayList();

        if (!Utilities.isEmpty(getDoctypeName())) {
            RuleExtensionValue extension = new RuleExtensionValue();
            extension.setKey(DOC_TYPE_NAME_KEY);
            extension.setValue(getDoctypeName());
            extensions.add(extension);
        }

        return extensions;
    }

    public List validateRoutingData(Map paramMap) {
        List errors = new ArrayList();
        setDoctypeName((String) paramMap.get(DOC_TYPE_NAME_PROPERTY));
        if (isRequired() && Utilities.isEmpty(getDoctypeName())) {
            errors.add(new WorkflowServiceErrorImpl("doc type is not valid.", "routetemplate.ruleroutingattribute.doctype.invalid"));
        }

        if (!Utilities.isEmpty(getDoctypeName())) {
            DocumentTypeService service = (DocumentTypeService) KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE);
            DocumentType documentType = service.findByName(getDoctypeName());
            if (documentType == null) {
                errors.add(new WorkflowServiceErrorImpl("doc type is not valid", "routetemplate.ruleroutingattribute.doctype.invalid"));
            }
        }
        return errors;
    }

    public List validateRuleData(Map paramMap) {
        return validateRoutingData(paramMap);
    }

    public String getDoctypeName() {
        return this.doctypeName;
    }

    public void setDoctypeName(String docTypeName) {
        this.doctypeName = docTypeName;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isRequired() {
        return required;
    }
}