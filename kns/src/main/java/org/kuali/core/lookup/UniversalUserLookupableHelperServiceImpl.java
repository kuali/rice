/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.core.lookup;

import org.kuali.RiceConstants;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.GlobalVariables;

public class UniversalUserLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(UniversalUserLookupableHelperServiceImpl.class);
    
    private KualiConfigurationService configService;
    private String userEditWorkgroupName;
    private boolean usersMaintainedByKuali;
        
    /**
     * Determines if underlying lookup bo has associated maintenance document that allows new or copy maintenance actions.
     * 
     * @return true if bo has maint doc that allows new or copy actions
     */
    public boolean allowsMaintenanceNewOrCopyAction() {
        // get the group name that we need here
        if ( userEditWorkgroupName == null ) {
            userEditWorkgroupName = configService.getParameterValue(RiceConstants.KNS_NAMESPACE, RiceConstants.DetailTypes.UNIVERSAL_USER_DETAIL_TYPE, RiceConstants.CoreApcParms.UNIVERSAL_USER_EDIT_WORKGROUP);
            // check whether users are editable within Kuali
            usersMaintainedByKuali = configService.getPropertyAsBoolean( RiceConstants.MAINTAIN_USERS_LOCALLY_KEY );
        }
        
        if ( usersMaintainedByKuali && GlobalVariables.getUserSession().getUniversalUser().isMember( userEditWorkgroupName ) ) {
            return super.allowsMaintenanceNewOrCopyAction();
        }
        return false;
    }

    public KualiConfigurationService getConfigService() {
        return configService;
    }

    public void setConfigService(KualiConfigurationService configService) {
        this.configService = configService;
    }
/*
    @Override
    protected List<? extends BusinessObject> getSearchResultsHelper(Map<String, String> fieldValues, boolean unbounded) {
        List<UniversalUser> results = (List<UniversalUser>)super.getSearchResultsHelper(fieldValues, unbounded);
        List<UniversalUser> filteredResults = results;
        if ( results.size() > 0 ) {
            String moduleCode = fieldValues.get( "activeModuleCodeString" );
            if ( StringUtils.isNotBlank( moduleCode ) ) {
                filteredResults = new ArrayList<UniversalUser>();
                for ( UniversalUser user : results ) {
                    if ( user.getActiveModuleCodeString().contains( moduleCode ) ) {
                        filteredResults.add( user ) ;
                    }
                }
            }
        }
        if ( filteredResults instanceof CollectionIncomplete ) {
            return filteredResults;
        } else {
            // TODO: this isn't correct, but I don't think we want to fix it since it would require that
            // all rows be loaded in and tested
            return new CollectionIncomplete( filteredResults, ((CollectionIncomplete)results).getActualSizeIfTruncated() );
        }
    }
*/
    
}
