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
package edu.iu.uis.eden.actionlist.web;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.struts.action.ActionForm;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.WorkflowServiceErrorException;
import edu.iu.uis.eden.WorkflowServiceErrorImpl;
import edu.iu.uis.eden.actionlist.ActionListFilter;

/**
 * Struts form class for ActionListFilterAction
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * 
 */
public class ActionListFilterForm extends ActionForm {

	private static final long serialVersionUID = -1149636352016711445L;

	private ActionListFilter filter;
    private String createDateFrom;
    private String createDateTo;
    private String lastAssignedDateTo;
    private String lastAssignedDateFrom;
    private String methodToCall = "";
    private String lookupableImplServiceName;
    private String lookupType;
    private String docTypeFullName;
    private List userWorkgroups;
    
    public ActionListFilterForm() {
        filter = new ActionListFilter();
    }
    
    public String getCreateDateTo() {
        return createDateTo;
    }
    public void setCreateDateTo(String createDateTo) {
        this.createDateTo = createDateTo.trim();
    }
    public String getLastAssignedDateFrom() {
        return lastAssignedDateFrom;
    }
    public void setLastAssignedDateFrom(String lastAssignedDateFrom) {
        this.lastAssignedDateFrom = lastAssignedDateFrom.trim();
    }
    public String getCreateDateFrom() {
        return createDateFrom;
    }
    public void setCreateDateFrom(String createDate) {
        this.createDateFrom = createDate.trim();
    }

    public ActionListFilter getFilter() {
        return filter;
    }

    public String getMethodToCall() {
        return methodToCall;
    }

    public void setMethodToCall(String methodToCall) {
        this.methodToCall = methodToCall;
    }

    public void setFilter(ActionListFilter filter) {
        this.filter = filter;
        if (filter.getCreateDateFrom() != null) {
            setCreateDateFrom(EdenConstants.getDefaultDateFormat().format(filter.getCreateDateFrom()));    
        }
        if (filter.getCreateDateTo() != null) {
            setCreateDateTo(EdenConstants.getDefaultDateFormat().format(filter.getCreateDateTo()));    
        }
        if (filter.getLastAssignedDateFrom() != null) {
            setLastAssignedDateFrom(EdenConstants.getDefaultDateFormat().format(filter.getLastAssignedDateFrom()));    
        }
        if (filter.getLastAssignedDateTo() != null) {
            setLastAssignedDateTo(EdenConstants.getDefaultDateFormat().format(filter.getLastAssignedDateTo()));    
        }
    }

    public String getLastAssignedDateTo() {
        return lastAssignedDateTo;
    }

    public void setLastAssignedDateTo(String lastAssignedDate) {
        this.lastAssignedDateTo = lastAssignedDate.trim();
    }

    public void validateDates() {
        List errors = new ArrayList();
        if (getCreateDateFrom() != null && getCreateDateFrom().length() != 0) {
            try {
                EdenConstants.getDefaultDateFormat().parse(getCreateDateFrom());
            } catch (ParseException e) {
                errors.add(new WorkflowServiceErrorImpl("Error with Create Date From", "general.error.fieldinvalid", "Create Date From"));
            }
        }
        if (getCreateDateTo() != null && getCreateDateTo().length() != 0) {
            try {
                EdenConstants.getDefaultDateFormat().parse(getCreateDateTo());
            } catch (ParseException e) {
                errors.add(new WorkflowServiceErrorImpl("Error with Create Date To", "general.error.fieldinvalid", "Create Date To"));
            }
        }
        if (getLastAssignedDateFrom() != null && getLastAssignedDateFrom().length() != 0) {
            try {
                EdenConstants.getDefaultDateFormat().parse(getLastAssignedDateFrom());
            } catch (ParseException e1) {
                errors.add(new WorkflowServiceErrorImpl("Error with Last Assigned Date From", "general.error.fieldinvalid", "Last Assigned Date From"));
            }
        }
        if (getLastAssignedDateTo() != null && getLastAssignedDateTo().length() != 0) {
            try {
                EdenConstants.getDefaultDateFormat().parse(getLastAssignedDateTo());
            } catch (ParseException e1) {
                errors.add(new WorkflowServiceErrorImpl("Error with Last Assigned Date To", "general.error.fieldinvalid", "Last Assigned Date To"));
            }
        }
        if (!errors.isEmpty()) {
            throw new WorkflowServiceErrorException("Action List Filter Dates Validation Error", errors);
        }
    }
    
    public ActionListFilter getLoadedFilter() throws ParseException {
        if (getCreateDateFrom() != null && getCreateDateFrom().length() != 0) {
            filter.setCreateDateFrom(EdenConstants.getDefaultDateFormat().parse(getCreateDateFrom()));    
        }
        if (getCreateDateTo() != null && getCreateDateTo().length() != 0) {
            filter.setCreateDateTo(EdenConstants.getDefaultDateFormat().parse(getCreateDateTo()));
        }
        if (getLastAssignedDateFrom() != null && getLastAssignedDateFrom().length() != 0) {
            filter.setLastAssignedDateFrom(EdenConstants.getDefaultDateFormat().parse(getLastAssignedDateFrom()));    
        }
        if (getLastAssignedDateTo() != null && getLastAssignedDateTo().length() != 0) {
            filter.setLastAssignedDateTo(EdenConstants.getDefaultDateFormat().parse(getLastAssignedDateTo()));    
        }
        if (getDocTypeFullName() != null && ! "".equals(getDocTypeFullName())) {
            filter.setDocumentType(getDocTypeFullName());
        }
        
        return filter;
    }

    public String getLookupableImplServiceName() {
        return lookupableImplServiceName;
    }

    public void setLookupableImplServiceName(String lookupableImplServiceName) {
        this.lookupableImplServiceName = lookupableImplServiceName;
    }

    public String getLookupType() {
        return lookupType;
    }

    public void setLookupType(String lookupType) {
        this.lookupType = lookupType;
    }

    public String getDocTypeFullName() {
        return docTypeFullName;
    }

    public void setDocTypeFullName(String docTypeFullName) {
        this.docTypeFullName = docTypeFullName;
    }

    public List getUserWorkgroups() {
        return userWorkgroups;
    }

    public void setUserWorkgroups(List userWorkgroups) {
        this.userWorkgroups = userWorkgroups;
    }

}