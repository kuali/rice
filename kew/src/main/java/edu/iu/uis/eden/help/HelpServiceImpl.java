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
package edu.iu.uis.eden.help;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.WorkflowServiceErrorException;
import edu.iu.uis.eden.WorkflowServiceErrorImpl;
import edu.iu.uis.eden.export.ExportDataSet;
import edu.iu.uis.eden.help.dao.HelpDAO;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.xml.export.HelpEntryXmlExporter;
import edu.iu.uis.eden.xml.help.HelpEntryXmlParser;


public class HelpServiceImpl implements HelpService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(HelpServiceImpl.class);
    private HelpDAO helpDAO;
    
    private static final String NAME_EMPTY = "helpentry.name.empty";
    private static final String TEXT_EMPTY = "helpentry.text.empty";
    private static final String ID_INVALID = "helpentry.id.invalid";
    private static final String KEY_EMPTY = "helpentry.key.empty";
    private static final String KEY_EXIST = "helpentry.key.exists";
    private static final String KEY_ILLEGAL = "helpentry.key.illegal";
    
    public void save(HelpEntry helpEntry){
        validateHelpEntry(helpEntry);
        getHelpDAO().save(helpEntry);
    }
    
    public void saveXmlEntry(HelpEntry helpEntry){
    	HelpEntry entry=validateXmlHelpEntry(helpEntry);
    	LOG.debug(entry.getHelpId()+", "+entry.getHelpName()+", "+entry.getHelpText()+", "+entry.getHelpKey());
    	getHelpDAO().save(entry);
    }
    
    public void delete(HelpEntry helpEntry){
    	getHelpDAO().deleteEntry(helpEntry);
    }
    
    private HelpEntry validateXmlHelpEntry(HelpEntry helpEntry){
    	LOG.debug("Enter validateXMLHelpEntry(..)");
        List errors = new ArrayList();
        
        if (helpEntry.getHelpName() == null || "".equals(helpEntry.getHelpName().trim())) {
            errors.add(new WorkflowServiceErrorImpl("Help Name empty.", NAME_EMPTY));
        }
        if (helpEntry.getHelpText() == null || "".equals(helpEntry.getHelpText().trim())) {
            errors.add(new WorkflowServiceErrorImpl("Help Text empty.", TEXT_EMPTY));
        } else {
            helpEntry.setHelpText(helpEntry.getHelpText().trim());
        }
        
        if (helpEntry.getHelpKey() == null || "".equals(helpEntry.getHelpKey().trim())) {
            errors.add(new WorkflowServiceErrorImpl("Help Key empty.", KEY_EMPTY));
        } else if (helpEntry.getHelpKey().indexOf("'") >= 0){
            errors.add(new WorkflowServiceErrorImpl("Help Key illegal character.", KEY_ILLEGAL, "'"));
        } else {
            helpEntry.setHelpKey(helpEntry.getHelpKey().trim());
            HelpEntry entry1=findByKey(helpEntry.getHelpKey());
            if(helpEntry.getHelpId() == null && entry1 != null){
            	Long id=entry1.getHelpId();
            	//LOG.debug("id of this help entry is: "+id.toString());
            	helpEntry.setHelpId(id);
                helpEntry.setLockVerNbr(entry1.getLockVerNbr());   
            	//LOG.debug(helpEntry.getHelpId()+", "+helpEntry.getHelpName()+", "+helpEntry.getHelpText()+", "+helpEntry.getHelpKey());
            }
        }
        
        LOG.debug("Exit validateXmlHelpEntry(..)");
        if (!errors.isEmpty()) {
          throw new WorkflowServiceErrorException("Help Entry validation Error", errors);
        }
        return helpEntry;
    }
    
    
    private void validateHelpEntry(HelpEntry helpEntry){
        LOG.debug("Enter validateHelpEntry(..)");
        List errors = new ArrayList();
        
        if (helpEntry.getHelpName() == null || "".equals(helpEntry.getHelpName().trim())) {
            errors.add(new WorkflowServiceErrorImpl("Help Name empty.", NAME_EMPTY));
        }
        if (helpEntry.getHelpText() == null || "".equals(helpEntry.getHelpText().trim())) {
            errors.add(new WorkflowServiceErrorImpl("Help Text empty.", TEXT_EMPTY));
        } else {
            helpEntry.setHelpText(helpEntry.getHelpText().trim());
        }
        
        if (helpEntry.getHelpKey() == null || "".equals(helpEntry.getHelpKey().trim())) {
            errors.add(new WorkflowServiceErrorImpl("Help Key empty.", KEY_EMPTY));
        } else if (helpEntry.getHelpKey().indexOf("'") >= 0){
            errors.add(new WorkflowServiceErrorImpl("Help Key illegal character.", KEY_ILLEGAL, "'"));
        } else {
            helpEntry.setHelpKey(helpEntry.getHelpKey().trim());
            if(helpEntry.getHelpId() == null && findByKey(helpEntry.getHelpKey()) != null){
                errors.add(new WorkflowServiceErrorImpl("Help Key exists.", KEY_EXIST, helpEntry.getHelpKey()));  
            }
        }
        
        LOG.debug("Exit validateHelpEntry(..)");
        if (!errors.isEmpty()) {
          throw new WorkflowServiceErrorException("Help Entry validation Error", errors);
        }
    }
    
    public HelpEntry findByKey(String helpKey){
        return getHelpDAO().findByKey(helpKey);
    }
    
    public HelpDAO getHelpDAO() {
        return helpDAO;
    }
    public void setHelpDAO(HelpDAO helpDAO) {
        this.helpDAO = helpDAO;
    }
    
    public HelpEntry findById(Long helpId){
        return getHelpDAO().findById(helpId);
    }
    
    public List search(HelpEntry helpEntry){
        List errors = new ArrayList();
        if(helpEntry.getHelpId() != null && helpEntry.getHelpId().longValue() == 0){
            errors.add(new WorkflowServiceErrorImpl("Help Id invalid", ID_INVALID));
        }
        if (!errors.isEmpty()) {
            throw new WorkflowServiceErrorException("Help Entry search criteria error", errors);
        }
        
        return getHelpDAO().search(helpEntry);
    }
    
    public void loadXml(InputStream inputStream, WorkflowUser user){
        HelpEntryXmlParser parser = new HelpEntryXmlParser();
        try { 
            parser.parseHelpEntries(inputStream);
        } catch(Exception e){
            throw new WorkflowServiceErrorException("Error parsing help  XML file", new WorkflowServiceErrorImpl("Error parsing xml file.", EdenConstants.XML_FILE_PARSE_ERROR) );
    	} 
    }

	public Element export(ExportDataSet dataSet) {
		HelpEntryXmlExporter exporter = new HelpEntryXmlExporter();
		return exporter.export(dataSet);
	}
    
    
}
