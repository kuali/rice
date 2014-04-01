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
package org.kuali.rice.kew.docsearch.dao.impl;

import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.kew.docsearch.SearchableAttributeDateTimeValue;
import org.kuali.rice.kew.docsearch.SearchableAttributeFloatValue;
import org.kuali.rice.kew.docsearch.SearchableAttributeLongValue;
import org.kuali.rice.kew.docsearch.SearchableAttributeStringValue;
import org.kuali.rice.kew.docsearch.dao.SearchableAttributeDAO;
import org.kuali.rice.krad.data.DataObjectService;
import org.springframework.beans.factory.annotation.Required;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.kuali.rice.core.api.criteria.PredicateFactory.equal;

/**
 * JPA implementation of SearchableAttributeDAO
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class SearchableAttributeDAOJpa implements SearchableAttributeDAO {

	private EntityManager entityManager;
    private DataObjectService dataObjectService;

	/**
	 * This overridden method queries the SearchableAttributeDateTimeValue persistence class
	 *
	 * @see org.kuali.rice.kew.docsearch.dao.SearchableAttributeDAO#getSearchableAttributeDateTimeValuesByKey(
     * java.lang.String,java.lang.String)
	 */
	public List<Timestamp> getSearchableAttributeDateTimeValuesByKey(
			String documentId, String key) {

		List<Timestamp> lRet = null;
        QueryByCriteria.Builder builder = QueryByCriteria.Builder.create();
        builder.setPredicates(equal("documentId",documentId),
                               equal("searchableAttributeKey",key));


        List<SearchableAttributeDateTimeValue> results = getDataObjectService().findMatching(
                            SearchableAttributeDateTimeValue.class,builder.build()).getResults();
        if (!results.isEmpty()) {
        	lRet = new ArrayList<Timestamp>();
            for (SearchableAttributeDateTimeValue attribute: results) {
            	lRet.add(attribute.getSearchableAttributeValue());
            }
        }
		return lRet;
	}

	/**
	 * This overridden method queries the SearchableAttributeFloatValue persistence class
	 *
	 * @see org.kuali.rice.kew.docsearch.dao.SearchableAttributeDAO#getSearchableAttributeFloatValuesByKey(
     * java.lang.String,java.lang.String)
	 */
	public List<BigDecimal> getSearchableAttributeFloatValuesByKey(
			String documentId, String key) {
		List<BigDecimal> lRet = null;
        QueryByCriteria.Builder builder = QueryByCriteria.Builder.create();
        builder.setPredicates(equal("documentId",documentId),
                equal("searchableAttributeKey",key));
        List<SearchableAttributeFloatValue> results = getDataObjectService().findMatching(
                SearchableAttributeFloatValue.class,builder.build()).getResults();
        if (!results.isEmpty()) {
        	lRet = new ArrayList<BigDecimal>();
            for (SearchableAttributeFloatValue attribute: results) {
            	lRet.add(attribute.getSearchableAttributeValue());
            }
        }
		return lRet;
	}

	/**
	 * This overridden method queries the searchableAttributeKey persistence class
	 *
	 * @see org.kuali.rice.kew.docsearch.dao.SearchableAttributeDAO#getSearchableAttributeLongValuesByKey(
     * java.lang.String,java.lang.String)
	 */
	public List<Long> getSearchableAttributeLongValuesByKey(String documentId,
			String key) {
		List<Long> lRet = null;
        QueryByCriteria.Builder builder = QueryByCriteria.Builder.create();
        builder.setPredicates(equal("documentId",documentId),
                equal("searchableAttributeKey",key));
        List<SearchableAttributeLongValue> results = getDataObjectService().findMatching(
                SearchableAttributeLongValue.class,builder.build()).getResults();
        if (!results.isEmpty()) {
        	lRet = new ArrayList<Long>();
            for (SearchableAttributeLongValue attribute: results) {
            	lRet.add(attribute.getSearchableAttributeValue());
            }
        }
		return lRet;
	}

	/**
	 * This overridden method queries the SearchableAttributeStringValue persistence class
	 *
	 * @see org.kuali.rice.kew.docsearch.dao.SearchableAttributeDAO#getSearchableAttributeStringValuesByKey(
     * java.lang.String,java.lang.String)
	 */
	public List<String> getSearchableAttributeStringValuesByKey(
			String documentId, String key) {
		List<String> lRet = null;
        QueryByCriteria.Builder builder = QueryByCriteria.Builder.create();
        builder.setPredicates(equal("documentId",documentId),
                equal("searchableAttributeKey",key));
        List<SearchableAttributeStringValue> results = getDataObjectService().findMatching(
                SearchableAttributeStringValue.class,builder.build()).getResults();
        if (!results.isEmpty()) {
        	lRet = new ArrayList<String>();
            for (SearchableAttributeStringValue attribute: results) {
            	lRet.add(attribute.getSearchableAttributeValue());
            }
        }
		return lRet;
	}

    public EntityManager getEntityManager() {
        return this.entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }


    public DataObjectService getDataObjectService() {
        return dataObjectService;
    }

    @Required
    public void setDataObjectService(DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }


}
