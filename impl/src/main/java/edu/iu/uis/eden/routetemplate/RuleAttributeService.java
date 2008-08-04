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

import java.util.List;

import edu.iu.uis.eden.XmlLoader;
import edu.iu.uis.eden.xml.export.XmlExporter;

/**
 * A service which provides data access for {@link RuleAttribute}s.
 * 
 * @see RuleAttribute
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface RuleAttributeService extends XmlLoader, XmlExporter {

    public void save(RuleAttribute ruleAttribute);
    public void delete(Long ruleAttributeId);
    public List findByRuleAttribute(RuleAttribute ruleAttribute);
    public RuleAttribute findByRuleAttributeId(Long ruleAttributeId);
    public List findAll();
    public RuleAttribute findByName(String name);
    public RuleAttribute findByClassName(String className);
    
}
