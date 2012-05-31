/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.core.jpa.criteria;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.kuali.rice.core.jpa.criteria.QueryByCriteria.QueryByCriteriaType;

/**
 * A criteria builder for JPQL Query objects.
 * 
 * TODO: Rewrite this class with a better criteria building algorithm.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@SuppressWarnings("unchecked")
public class Criteria {

	private Integer searchLimit;
	
	private String entityName;

	private String alias;
	
	private int bindParamCount;
	
	private boolean distinct = false;

	protected List tokens = new ArrayList();

	private List orderByTokens = new ArrayList();

	protected Map<String, Object> params = new LinkedHashMap<String, Object>();

	public Criteria(String entityName) {
		this(entityName, "a");
	}

	public Criteria(String entityName, String alias) {
		this.entityName = entityName;
		this.alias = alias;
	}

	public void between(String attribute, Object value1, Object value2) {
		String fixedAttr = fixAttr(attribute);
		tokens.add(" (" + alias + "." + attribute + " BETWEEN :" + fixedAttr + "-b1 AND :" + fixedAttr + "-b2) ");
		params.put(fixedAttr+ "-b1", value1);
		params.put(fixedAttr+ "-b2", value2);
	}
	
	/**
	 * This method ...
	 * 
	 * @param string
	 * @return
	 */
	private String fixAttr(String string) {
		return string.replace(".", "_");
	}

	public void eq(String attribute, Object value) {
		String fixedAttr = fixAttr(attribute);
		tokens.add(alias + "." + attribute + " = :" + fixedAttr + " ");
		params.put(fixedAttr, value);
	}

	public void gt(String attribute, Object value) {
		String fixedAttr = fixAttr(attribute);
		tokens.add(alias + "." + attribute + " > :" + fixedAttr + " ");
		params.put(fixedAttr, value);
	}

	public void gte(String attribute, Object value) {
		String fixedAttr = fixAttr(attribute);
		tokens.add(alias + "." + attribute + " >= :" + fixedAttr + " ");
		params.put(fixedAttr, value);
	}

	public void like(String attribute, Object value) {
		String fixedAttr = fixAttr(attribute);
		if (attribute.contains("__JPA_ALIAS__")) {
			String bind = "BIND_PARAM_" + (++bindParamCount);
			tokens.add(attribute + " LIKE :" + bind + " ");
			params.put(bind, value);
		} else {
			tokens.add(alias + "." + attribute + " LIKE :" + stripFunctions(fixedAttr) + " ");
			params.put(fixedAttr, value);
		}
	}

	public void notLike(String attribute, Object value) {
		String fixedAttr = fixAttr(attribute);
		tokens.add(alias + "." + attribute + " NOT LIKE :" + fixedAttr + " ");
		params.put(fixedAttr, value);
	}

	public void lt(String attribute, Object value) {
		String fixedAttr = fixAttr(attribute);
		tokens.add(alias + "." + attribute + " < :" + fixedAttr + " ");
		params.put(fixedAttr, value);
	}

	public void lte(String attribute, Object value) {
		String fixedAttr = fixAttr(attribute);
		tokens.add(alias + "." + attribute + " <= :" + fixedAttr + " ");
		params.put(fixedAttr, value);
	}

	public void ne(String attribute, Object value) {
		String fixedAttr = fixAttr(attribute);
		tokens.add(alias + "." + attribute + " != :" + fixedAttr + " ");
		params.put(fixedAttr, value);
	}

	public void isNull(String attribute) {
		tokens.add(alias + "." + attribute + " IS NULL ");
	}

	public void rawJpql(String jpql) {
		tokens.add(" " + jpql + " ");
	}

	public void in(String attribute, List values) {
		String in = "";
		for (Object object : values) {
			in += "'"+object + "',";
		}
		if (!"".equals(in)) {
			in = in.substring(0, in.length()-1);
		}
		tokens.add(alias + "." + attribute + " IN (" + in + ") ");
	}

	public void notIn(String attribute, List values) {
		String in = "";
		for (Object object : values) {
			in += "'"+object + "',";
		}
		if (!"".equals(in)) {
			in = in.substring(in.length()-1);
		}
		tokens.add(alias + "." + attribute + " NOT IN (" + in + ") ");
	}

	public void orderBy(String attribute, boolean sortAscending) {
		String sort = (sortAscending ? "ASC" : "DESC");
		orderByTokens.add(alias + "." + attribute + " " + sort + " ");
	}

	public void and(Criteria and) {
		tokens.add(new AndCriteria(and));
	}

	public void or(Criteria or) {
		tokens.add(new OrCriteria(or));
	}

	public String toQuery(QueryByCriteriaType type) {
		String queryType = type.toString();
		if (type.equals(QueryByCriteriaType.SELECT)) {
			if(distinct){
				queryType += " " + "DISTINCT";
			}
			
			queryType += " " + alias;
		}
		String queryString = queryType + " FROM " + entityName + " AS " + alias;
		if (!tokens.isEmpty()) {
			queryString += " WHERE " + buildWhere();
		}
		if (!orderByTokens.isEmpty()) {
			queryString += " ORDER BY ";
			int count = 0;
			for (Iterator iterator = orderByTokens.iterator(); iterator.hasNext();) {
				Object token = (Object) iterator.next();
				if (count == 0) {
					count++;
				} else {
					queryString += ", ";
				}
				queryString += (String) token;
			}
		}		
		return fix(queryString);
	}
	
	public String toCountQuery() {
		String queryString = "SELECT COUNT(*) FROM " + entityName + " AS " + alias;
		if (!tokens.isEmpty()) {
			queryString += " WHERE " + buildWhere();
		}
		return fix(queryString);
	}

	private String fix(String queryString) {
		queryString = queryString.replaceAll("__JPA_ALIAS__", alias);
		return queryString;
	}
	
	private String buildWhere() {
		String queryString = "";
		int i = 0;
		for (Iterator iterator = tokens.iterator(); iterator.hasNext();) {
			Object token = (Object) iterator.next();
			if (token instanceof Criteria) {
				String logic = "";
				if (i>0 && token instanceof AndCriteria) {
					logic = " AND ";
				} else if (i>0 && token instanceof OrCriteria) {
					logic = " OR ";
				}
			queryString += logic + " (" + ((Criteria) token).buildWhere() + ") ";
			} else {
				if(i>0){
					queryString += " AND " + (String) token;
				}else{
					queryString += (String) token;
				}
			}
			i++;
		}
		return queryString;
	}

	// Keep this package access so the QueryByCriteria can call it from this package.
	void prepareParameters(Query query) {
		prepareParameters(query, tokens, params);
	}
	
	void prepareParameters(Query query, List tokens, Map<String, Object> params) {
		for (Map.Entry<String, Object> param : params.entrySet()) {
			Object value = param.getValue();
			if (value instanceof BigDecimal) {
				value = new Long(((BigDecimal)value).longValue());
			}
			if (value instanceof String) {
				value = ((String)value).replaceAll("\\*", "%");
			}
			query.setParameter(param.getKey(), value);
		}
		for (Iterator iterator = tokens.iterator(); iterator.hasNext();) {
			Object token = (Object) iterator.next();
			if (token instanceof Criteria) {
				prepareParameters(query, ((Criteria)token).tokens, ((Criteria)token).params);
			}
		}
	}
	
	private class AndCriteria extends Criteria {
		public AndCriteria(Criteria and) {
			super(and.entityName, and.alias);
			this.tokens = new ArrayList(and.tokens);
			this.params = new HashMap(and.params);
		}		
	}

	private class OrCriteria extends Criteria {
		public OrCriteria(Criteria or) {
			super(or.entityName, or.alias);
			this.tokens = new ArrayList(or.tokens);
			this.params = new HashMap(or.params);
		}		
	}
	
	public Integer getSearchLimit() {
		return this.searchLimit;
	}

	public void setSearchLimit(Integer searchLimit) {
		this.searchLimit = searchLimit;
	}


	public void notNull(String attribute) {
		tokens.add(alias + "." + attribute + " IS NOT NULL ");
	}

	public void distinct(boolean distinct){
		this.distinct = distinct;
	}
	
	/**
	 * This method ...
	 * 
	 * @param string
	 * @param timestamp
	 * @param timestamp2
	 */
	public void notBetween(String attribute, Object value1,
			Object value2) {
		String fixedAttr = fixAttr(attribute);
		tokens.add(" (" + alias + "." + attribute + " NOT BETWEEN :" + fixedAttr + "-b1 AND :" + fixedAttr + "-b2) ");
		params.put(fixedAttr + "-b1", value1);
		params.put(fixedAttr + "-b2", value2);
	}

	/**
	 * This method ...
	 * 
	 * @param string
	 * @param responsibilitySubQuery
	 */
	public void in(String match, Criteria subQuery, String attribute) {
		if("a".equals(subQuery.alias)){
			subQuery.alias="b";
		}
		String whereClause = "";
		if(subQuery.tokens.isEmpty()){
			whereClause = "WHERE ";
		}else{
			whereClause = "AND ";
		}
		whereClause += subQuery.alias+"."+attribute + " = " + alias+"."+match;
		
		tokens.add("EXISTS (" + subQuery.toQuery(QueryByCriteriaType.SELECT) + whereClause + " ) ");
		
	}
	
	private String stripFunctions(String attribute) {
	    int index = attribute.lastIndexOf('(');
	    if(index != -1) {
	        return attribute.substring(index+1, attribute.indexOf(')'));
	    }
	    
	    return attribute;
	}

	public String getAlias() {
		return this.alias;
	}
	
}
