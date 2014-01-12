/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kew.useroptions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.krad.data.DataObjectService;

import org.springframework.transaction.annotation.Transactional;

import static org.kuali.rice.core.api.criteria.PredicateFactory.*;

/**
 * /**
 * An implementation of the {@link UserOptionsService}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Transactional
public class UserOptionsServiceImpl implements UserOptionsService {

    // KRAD Data Layer API containing basic CRUD operations and access to a metadata repository.
    private DataObjectService dataObjectService;

    // default properties for this class
    private static final Properties defaultProperties = new Properties();

    // set the default properties for this class
    static {
        defaultProperties.setProperty(KewApiConstants.EMAIL_RMNDR_KEY, KewApiConstants.EMAIL_RMNDR_WEEK_VAL);
    }

    /** {@inheritDoc}
     */
    @Override
    public Collection<UserOptions> findByWorkflowUser(String principalId) {

        QueryByCriteria.Builder criteria = QueryByCriteria.Builder.create();
        criteria.setPredicates(equal("workflowId", principalId));

        return dataObjectService.findMatching(UserOptions.class, criteria.build()).getResults();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserOptions> findByUserQualified(String principalId, String likeString) {
        if ((principalId == null)) {
            return new ArrayList<UserOptions>(0);
        }
        QueryByCriteria.Builder criteria = QueryByCriteria.Builder.create();
        criteria.setPredicates(and(equal("workflowId", principalId),like("optionId", likeString)));
        return this.dataObjectService.findMatching(UserOptions.class, criteria.build()).getResults();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserOptions findByOptionId(String optionId, String principalId) {
        if (optionId == null || "".equals(optionId) || principalId == null || "".equals(principalId)) {
            return null;
        }

        return this.dataObjectService.find(UserOptions.class, new UserOptionsId(principalId, optionId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(UserOptions userOptions) {
        this.dataObjectService.save(userOptions);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(String principalId, Map<String,String> optionsMap) {

    	// build UserOptions from the principalId and optionMap and save them
        if (optionsMap != null && !optionsMap.isEmpty()) {
    		for (Entry<String, String> entry : optionsMap.entrySet()) {
    			UserOptions option = findByOptionId(entry.getKey(), principalId);
    			if (option == null) {
    				option = new UserOptions();
    				option.setWorkflowId(principalId);
    			}
    			option.setOptionId(entry.getKey());
    			option.setOptionVal(entry.getValue());
                this.save(option);
    		}
    	}
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(String principalId, String optionId, String optionValue) {
        //KULRICE-7796 Don't save where val is greater than field length
        if(optionValue.length() <= 2000)
        {
            UserOptions option = findByOptionId(optionId, principalId);
            if (option == null) {
                option = new UserOptions();
                option.setWorkflowId(principalId);
            }
            option.setOptionId(optionId);
            option.setOptionVal(optionValue);
            this.save(option);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteUserOptions(UserOptions userOptions) {
        this.dataObjectService.delete(userOptions);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserOptions> retrieveEmailPreferenceUserOptions(String emailSetting) {

        QueryByCriteria.Builder criteria = QueryByCriteria.Builder.create();
        criteria.setPredicates(
                or(
                    equal("optionId", KewApiConstants.EMAIL_RMNDR_KEY),
                    like("optionId", "%" + KewApiConstants.DOCUMENT_TYPE_NOTIFICATION_PREFERENCE_SUFFIX)
                ),
                equal("optionVal", emailSetting)
        );

        return this.dataObjectService.findMatching(UserOptions.class, criteria.build()).getResults();
    }

    /**
     * Returns an instance of the {@link DataObjectService}.
     * @return  a instance of {@link DataObjectService}
     */
    public DataObjectService getDataObjectService() {
        return dataObjectService;
    }

    /**
     * @see org.kuali.rice.kew.useroptions.UserOptionsServiceImpl#getDataObjectService()
     */
    public void setDataObjectService(DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }
}
