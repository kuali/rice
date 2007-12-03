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
package org.kuali.workflow.tools.xml;

import java.util.List;

/**
 * Can be "plugged-in" to the XmlGen tool to assist with rule description generation and attribute
 * field resolution.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public abstract class XmlGenHelper {

	/**
	 * Return a description for the given rule.  If a null or empty value is returned, then XmlGen will generate
	 * a default description for the rule using rule data.  This method is only called if a description is
	 * not defined for the rule on the spreadsheet.
	 */
	public abstract String generateRuleDescription(Rule rule);

	/**
	 * Return a List of field names for the given attribute.  This is only invoked for those attributes which
	 * aren't defined in XML.  In those cases we need to be able to determine what data fields are
	 * available on the attribute and, therefore, the rules.
	 */
	public abstract List<String> resolveFieldNames(Attribute attribute);

}
