/*
 * Copyright 2005-2008 The Kuali Foundation
 *
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
package org.kuali.rice.kew.help.service.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;
import org.kuali.rice.core.api.impex.ExportDataSet;
import org.kuali.rice.core.framework.persistence.jta.TransactionalNoValidationExceptionRollback;
import org.kuali.rice.kew.exception.WorkflowServiceErrorException;
import org.kuali.rice.kew.exception.WorkflowServiceErrorImpl;
import org.kuali.rice.kew.help.HelpEntry;
import org.kuali.rice.kew.help.dao.HelpDAO;
import org.kuali.rice.kew.help.service.HelpService;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.xml.HelpEntryXmlParser;
import org.kuali.rice.kew.xml.export.HelpEntryXmlExporter;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.util.GlobalVariables;


@TransactionalNoValidationExceptionRollback
public class HelpServiceImpl implements HelpService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(HelpServiceImpl.class);
    private HelpDAO helpDAO;

    private static final String HELP_NAME_KEY = "helpEntry.helpName";
    private static final String HELP_KEY_KEY = "helpEntry.helpKey";
    private static final String HELP_TEXT_KEY = "helpEntry.helpText";
    private static final String HELP_ID_KEY = "helpEntry.helpId";
    private static final String ID_INVALID = "helpentry.id.invalid";
    private static final String NAME_EMPTY = "helpentry.name.empty";
    private static final String TEXT_EMPTY = "helpentry.text.empty";
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

        if (helpEntry.getHelpName() == null || "".equals(helpEntry.getHelpName().trim())) {
            GlobalVariables.getMessageMap().putError(HELP_NAME_KEY, NAME_EMPTY);
        }
        if (helpEntry.getHelpText() == null || "".equals(helpEntry.getHelpText().trim())) {
            GlobalVariables.getMessageMap().putError(HELP_TEXT_KEY, TEXT_EMPTY);
        } else {
            helpEntry.setHelpText(helpEntry.getHelpText().trim());
        }

        if (helpEntry.getHelpKey() == null || "".equals(helpEntry.getHelpKey().trim())) {
            GlobalVariables.getMessageMap().putError(HELP_KEY_KEY, KEY_EMPTY);
        } else if (helpEntry.getHelpKey().contains("'")) {
            GlobalVariables.getMessageMap().putError(HELP_KEY_KEY, KEY_ILLEGAL, "'");
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
        if (GlobalVariables.getMessageMap().hasErrors()) {
            throw new ValidationException("errors in help");
        }
        return helpEntry;
    }


    private void validateHelpEntry(HelpEntry helpEntry){
        LOG.debug("Enter validateHelpEntry(..)");
        List errors = new ArrayList();

        if (helpEntry.getHelpName() == null || "".equals(helpEntry.getHelpName().trim())) {
            GlobalVariables.getMessageMap().putError(HELP_NAME_KEY, NAME_EMPTY);
        }
        if (helpEntry.getHelpText() == null || "".equals(helpEntry.getHelpText().trim())) {
            GlobalVariables.getMessageMap().putError(HELP_TEXT_KEY, TEXT_EMPTY);
        } else {
            helpEntry.setHelpText(helpEntry.getHelpText().trim());
        }

        if (helpEntry.getHelpKey() == null || "".equals(helpEntry.getHelpKey().trim())) {
            GlobalVariables.getMessageMap().putError(HELP_KEY_KEY, KEY_EMPTY);
        } else if (helpEntry.getHelpKey().contains("'")){
            GlobalVariables.getMessageMap().putError(HELP_KEY_KEY, KEY_ILLEGAL, "'");
        } else {
            helpEntry.setHelpKey(helpEntry.getHelpKey().trim());
            if(helpEntry.getHelpId() == null && findByKey(helpEntry.getHelpKey()) != null){
                GlobalVariables.getMessageMap().putError(HELP_KEY_KEY, KEY_EXIST, helpEntry.getHelpKey());
            }
        }

        LOG.debug("Exit validateHelpEntry(..)");
        if (GlobalVariables.getMessageMap().hasErrors()) {
            throw new ValidationException("errors in help");
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
        if(helpEntry.getHelpId() != null && helpEntry.getHelpId().longValue() == 0){
            GlobalVariables.getMessageMap().putError(HELP_ID_KEY, ID_INVALID);
        }
        if (GlobalVariables.getMessageMap().hasErrors()) {
            throw new ValidationException("errors in help");
        }

        return getHelpDAO().search(helpEntry);
    }

    public void loadXml(InputStream inputStream, String principalId){
        HelpEntryXmlParser parser = new HelpEntryXmlParser();
        try {
            parser.parseHelpEntries(inputStream);
        } catch(Exception e){
            throw new WorkflowServiceErrorException("Error parsing help  XML file", new WorkflowServiceErrorImpl("Error parsing xml file.", KEWConstants.XML_FILE_PARSE_ERROR) );
    	}
    }

	public Element export(ExportDataSet dataSet) {
		HelpEntryXmlExporter exporter = new HelpEntryXmlExporter();
		return exporter.export(dataSet);
	}

	@Override
	public boolean supportPrettyPrint() {
		return true;
	}

}
