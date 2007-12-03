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
package edu.iu.uis.eden.help;

import java.util.List;

import edu.iu.uis.eden.XmlLoader;
import edu.iu.uis.eden.xml.export.XmlExporter;

/**
 * A service which provides data access for {@link HelpEntry} objects.
 * 
 * @see HelpEntry
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface HelpService extends XmlLoader, XmlExporter {
    public void save(HelpEntry helpEntry);    
    public void saveXmlEntry(HelpEntry helpEntry);
    public void delete(HelpEntry helpEntry);
    public HelpEntry findById(Long helpId);
    public List search(HelpEntry helpEntry);
    public HelpEntry findByKey(String helpKey);
}
