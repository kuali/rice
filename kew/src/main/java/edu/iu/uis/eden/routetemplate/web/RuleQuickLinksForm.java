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
package edu.iu.uis.eden.routetemplate.web;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.util.Utilities;

/**
 * A Struts ActionForm for the {@link RuleQuickLinksAction}.
 * 
 * @see RuleQuickLinksAction
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RuleQuickLinksForm extends ActionForm {

    private static final long serialVersionUID = 3632283509506923869L;
    private String rootDocTypeName;
    private List rootDocuments;
    private List documentTypeQuickLinksStructures = new ArrayList();
    
    public String getRootDocTypeName() {
        return rootDocTypeName;
    }
    public void setRootDocTypeName(String rootDocTypeName) {
        this.rootDocTypeName = rootDocTypeName;
    }
    public List getRootDocuments() {
        return rootDocuments;
    }
	public boolean isUseOneStartPortalUrl() {
		String useOneStartPortalUrl = Utilities.getApplicationConstant(EdenConstants.APP_CONST_USE_ONESTART_URL);
		return (StringUtils.isEmpty(useOneStartPortalUrl) ? false : new Boolean(useOneStartPortalUrl).booleanValue());
	}
    public List getDocumentTypeQuickLinksStructures() {
        return this.documentTypeQuickLinksStructures;
    }
	public void setDocumentTypeQuickLinksStructures(
			List documentTypeQuickLinksStructures) {
		this.documentTypeQuickLinksStructures = documentTypeQuickLinksStructures;
	}
    

}