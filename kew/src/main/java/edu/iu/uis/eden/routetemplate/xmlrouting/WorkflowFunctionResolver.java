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
package edu.iu.uis.eden.routetemplate.xmlrouting;

import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFunction;
import javax.xml.xpath.XPathFunctionResolver;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Node;

import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.routetemplate.RuleExtension;
import edu.iu.uis.eden.routetemplate.RuleExtensionValue;
import edu.iu.uis.eden.support.xstream.XStreamSafeSearchFunction;

/**
 * A function resolver for XPath functions provided by KEW.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class WorkflowFunctionResolver implements XPathFunctionResolver {
	
	private RuleExtension ruleExtension;
	private Node rootNode;
	private XPath xpath;


	public XPathFunction resolveFunction(QName fname, int arity) {
		if (fname == null) {
			throw new NullPointerException("The function name cannot be null.");
		}
		if (fname.equals(new QName("http://nothingfornowwf.com", "ruledata", "wf"))) {
			if (ruleExtension == null) {
				throw new NullPointerException("There are no rule extensions.");
			}
			return new XPathFunction() {
				public Object evaluate(java.util.List args) {
					if (args.size() == 1) {
						String name = (String) args.get(0);
						for (Iterator iter = ruleExtension.getExtensionValues().iterator(); iter.hasNext();) {
							RuleExtensionValue value = (RuleExtensionValue) iter.next();
							if (value.getKey().equals(name)) {
								return value.getValue();
							}
						}
					}
					return "";
				}
			};
		} else if (fname.equals(new QName("http://nothingfornowwf.com", "xstreamsafe", "wf"))) {
			return new XStreamSafeSearchFunction(rootNode, this.getXpath());
		} else if (fname.equals(new QName("http://nothingfornowwf.com", "upper-case", "wf"))) {
			return new UpperCaseFunction();
		} else if (fname.equals(new QName("http://nothingfornowwf.com", "field", "wf"))) {
			return new XPathFunction() {
				public Object evaluate(java.util.List args) {
					if (args.size() == 1) {
						String name = (String) args.get(0);
						try {
							return field(name);
						} catch (Exception e) {
							throw new WorkflowRuntimeException("Failed to find field to validate.", e);
						}
					}
					return "";
				}
			};
		} else if (fname.equals(new QName("http://nothingfornowwf.com", "empty", "wf"))) {
			return new XPathFunction() {
				public Object evaluate(java.util.List args) {
					return empty(args.get(0));
				}
			};
		} else {
			return null;
		}
	}

	public String field(String fieldName) throws Exception {
	    return xpath.evaluate("//edlContent/data/version[@current='true']/field[@name='" + fieldName + "']/value", rootNode);
	}
	    
	    
	public boolean empty(Object object) {
	    if (object instanceof String) {
	    	return StringUtils.isBlank((String)object);
	    }
	    return object == null;
	}

	public RuleExtension getRuleExtension() {
		return ruleExtension;
	}

	public void setRuleExtension(RuleExtension ruleExtension) {
		this.ruleExtension = ruleExtension;
	}

	public Node getRootNode() {
		return rootNode;
	}

	public void setRootNode(Node rootNode) {
		this.rootNode = rootNode;
	}
	

	public XPath getXpath() {
		return xpath;
	}

	public void setXpath(XPath xpath) {
		this.xpath = xpath;
	}
}