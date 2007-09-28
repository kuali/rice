/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.core.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.kuali.RiceConstants;
import org.kuali.RiceKeyConstants;
import org.kuali.core.bo.Parameter;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.rice.KNSServiceLocator;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class KualiConfigurationServiceImpl extends AbstractStaticConfigurationServiceImpl implements KualiConfigurationService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiConfigurationServiceImpl.class);

    public boolean isProductionEnvironment() {
        return getPropertyString( RiceConstants.PROD_ENVIRONMENT_CODE_KEY ).equals( getPropertyString( RiceConstants.ENVIRONMENT_KEY ) );
    }

    public List<Parameter> getParameters( Map<String,String> criteria ) {
    	ArrayList<Parameter> parameters = new ArrayList<Parameter>();
    	parameters.addAll( getBusinessObjectService().findMatching( Parameter.class, criteria ) );
    	return parameters;
        }
    
    public Map<String,Parameter> getParametersAsMap(Map<String, String> criteria) {
        List<Parameter> parms = getParameters(criteria);
        HashMap<String,Parameter> parmMap = new HashMap<String, Parameter>( parms.size() );
        for ( Parameter parm : parms ) {
            parmMap.put(parm.getParameterName(), parm);
        }
        return parmMap;
    }

    public List<Parameter> getParametersByDetailType( String namespaceCode, String parameterDetailTypeCode ) {
    	ArrayList<Parameter> parameters = new ArrayList<Parameter>();
    	HashMap<String,String> crit = new HashMap<String,String>( 3 );
    	crit.put( "parameterNamespaceCode", namespaceCode );
    	crit.put( "parameterDetailTypeCode", parameterDetailTypeCode );
    	parameters.addAll( getBusinessObjectService().findMatching( Parameter.class, crit ) );
    	return parameters;
    }

    public Map<String,Parameter> getParametersByDetailTypeAsMap( String namespaceCode, String parameterDetailTypeCode ) {
    	List<Parameter> parms = getParametersByDetailType( namespaceCode, parameterDetailTypeCode );
        HashMap<String,Parameter> parmMap = new HashMap<String, Parameter>( parms.size() );
        for ( Parameter parm : parms ) {
            parmMap.put(parm.getParameterName(), parm);
        }
        return parmMap;
        }
    
    public Parameter getParameter( String namespaceCode, String parameterDetailTypeCode, String parameterName ) {
    	Parameter param = null;
    	
    	HashMap<String,String> crit = new HashMap<String,String>( 3 );
    	crit.put( "parameterNamespaceCode", namespaceCode );
    	crit.put( "parameterName", parameterName );
    	if ( parameterDetailTypeCode != null ) {
    		crit.put( "parameterDetailTypeCode", parameterDetailTypeCode );
        	param = (Parameter)getBusinessObjectService().findByPrimaryKey( Parameter.class, crit );
    	} else {
        	Collection<Parameter> params = getBusinessObjectService().findMatching( Parameter.class, crit );
        	if ( params.size() == 1 ) {
        		param = params.iterator().next();
        		LOG.warn( "getParameter(): Parameter retrieved using 2 parameter version: " + namespaceCode + "/" + parameterName +"\n*****retrieved parameterDetailTypeCode=" + param.getParameterDetailTypeCode() );
        	} else if ( params.size() > 1 ) {
        		LOG.error( "getParameter(): Multiple of parameter exist using namespace/name combo: " + namespaceCode + "/" + parameterName );
        		throw new RuntimeException( "Multiple of parameter exist using namespace/name combo: " + namespaceCode + "/" + parameterName );
    }
    	}

    	if ( param == null && LOG.isInfoEnabled() ) {
    		LOG.info( "Unable to find parameter: " + namespaceCode + "/" + parameterDetailTypeCode + "/" + parameterName );
    	}
    	return param;
    }

    public String getParameterValue( String namespaceCode, String parameterDetailTypeCode, String parameterName ) {
    	Parameter parameter = getParameter( namespaceCode, parameterDetailTypeCode, parameterName ); 
    	if ( parameter != null ) {
    		return parameter.getParameterValue();
    	} else {
    		return "";
    }
    }

    public String[] getParameterValues( String namespaceCode, String parameterDetailTypeCode, String parameterName ) {
    	Parameter parameter = getParameter( namespaceCode, parameterDetailTypeCode, parameterName ); 
    	if ( parameter != null ) {
    		return getParameterValues( parameter );
    	} else {
    		return new String[] { "" };
    	}
    }

    public List<String> getParameterValuesAsList( String namespaceCode, String parameterDetailTypeCode, String parameterName ) {
        Parameter parameter = getParameter( namespaceCode, parameterDetailTypeCode, parameterName ); 
        if ( parameter != null ) {
            return getParameterValuesAsList( parameter );
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    public Set<String> getParameterValuesAsSet( String namespaceCode, String parameterDetailTypeCode, String parameterName ) {
        Parameter parameter = getParameter( namespaceCode, parameterDetailTypeCode, parameterName ); 
        if ( parameter != null ) {
            return getParameterValuesAsSet( parameter );
        } else {
            return Collections.EMPTY_SET;
    }
    }

    public String[] getParameterValues( Parameter parameter ) {
    	if ( parameter == null || StringUtils.isBlank( parameter.getParameterValue() ) ) {
    		return new String[] { "" };
    	}
    	return parameter.getParameterValue().split( ";" );
    }

    public List<String> getParameterValuesAsList( Parameter parameter ) {
    	return Arrays.asList( getParameterValues( parameter ) );
    }

    public Set<String> getParameterValuesAsSet( Parameter parameter ) {      
        return new HashSet<String>( Arrays.asList( getParameterValues( parameter ) ) );
        }
    
    public boolean getIndicatorParameter( String namespaceCode, String parameterDetailTypeCode, String parameterName ) {
    	return getParameterValue( namespaceCode, parameterDetailTypeCode, parameterName ).equals( "Y" );
        }
    
    public boolean parameterExists( String namespaceCode, String parameterDetailTypeCode, String parameterName ) {
    	return getParameter( namespaceCode, parameterDetailTypeCode, parameterName ) != null;
    }

    /**
     * Checks a given value against the rule based on the following guidelines: 1) If rule is inactive, the rule always passes 2) If
     * ruleOperator is 'A' If value is in set, rule passes else rule fails 3) If ruleOperator is 'D' if value is in set, rule fails
     * else rule passes
     * 
     * @param value - value to check
     * 
     * @return boolean indicating the rule success
     */
    public boolean failsRule( Parameter parameter, String value) {
        boolean result = false;
        if (isUsable( parameter )) {
            List parameterValues = Arrays.asList( getParameterValues( parameter ) );
            String paramValue = parameter.getParameterValue();
            if (isAllowedRule( parameter ) && !parameterValues.contains(value)) {
                result = true;
            } else if (isDeniedRule( parameter ) && parameterValues.contains(value)) {
                result = true;
        }
        }
        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "failsRule( " + parameter + ", " + value + " ) = " + result );
    }
        return result;
    }

    public boolean failsRule( String namespaceCode, String parameterDetailTypeCode, String parameterName, String value ) {
    	Parameter parameter = getParameter( namespaceCode, parameterDetailTypeCode, parameterName );
    	if ( parameter != null ) {
    		return failsRule( parameter, value );
        }
    	return true;
    }

    public boolean isUsable( Parameter parameter ) {
        return parameter != null && !StringUtils.isBlank(parameter.getParameterValue());
    }

    /**
     * @return whether ruleOperator is 'A'
     */
    public boolean isAllowedRule( Parameter parameter ) {
        return StringUtils.isBlank( parameter.getParameterConstraintCode() ) 
				|| RiceConstants.APC_ALLOWED_OPERATOR.equals(parameter.getParameterConstraintCode());
    }

    /**
     * @return whether ruleOperator is 'D'
     */
    public boolean isDeniedRule( Parameter parameter ) {
        return RiceConstants.APC_DENIED_OPERATOR.equals(parameter.getParameterConstraintCode());
        }

    /**
     * This method is a convenience method for getting the inverse of failsRule(value). It returns the opposite of whatever
     * failsRule returns for the same value.
     * 
     * @param value - a value to check
     * 
     * @return inverse of failsRule
     */
    public boolean succeedsRule(Parameter parameter, String value) {
        boolean b = !failsRule(parameter,value); 
        if ( LOG.isDebugEnabled() ) {
            if ( parameter != null ) {
                LOG.debug("succeedsRule() rule: " + parameter.getParameterName() + " Value: " + value + " op: " + parameter.getParameterConstraintCode() + " values " + parameter.getParameterValue() + " result " + b);
            }
        }
        return b;
    }

    public boolean succeedsRule( String namespaceCode, String parameterDetailTypeCode, String parameterName, String value ) {
    	Parameter parameter = getParameter( namespaceCode, parameterDetailTypeCode, parameterName );
    	if ( parameter != null ) {
    		return succeedsRule( parameter, value );
        }
                return false;
            }

    public boolean evaluateConstrainedParameter( Parameter constraint, String constrainingValue, String value ) {
    	Map<String,List<String>> constraintMap = convertParameterToConstraintMap( constraint );
    	List<String> values = constraintMap.get( constrainingValue );
    	if ( values != null ) {
	    	if ( isAllowedRule( constraint ) ) {
	    		return values.contains( value );
	    	} else {
	    		return !values.contains( value );	    		
            }
        }
    	return true;
    }

    public boolean evaluateConstrainedParameter( String namespaceCode, String parameterDetailTypeCode, String parameterName, String constrainingValue, String value ) {
    	return evaluateConstrainedParameter( getParameter( namespaceCode, parameterDetailTypeCode, parameterName ), constrainingValue, value );
    }

    public boolean evaluateConstrainedParameter( String namespaceCode, String parameterDetailTypeCode, String allowedValuesParameterName, String disallowedValuesParameterName, String constrainingValue, String value ) {
    	return evaluateConstrainedParameter( getParameter( namespaceCode, parameterDetailTypeCode, allowedValuesParameterName ), constrainingValue, value )
    			&& evaluateConstrainedParameter( getParameter( namespaceCode, parameterDetailTypeCode, disallowedValuesParameterName ), constrainingValue, value );    		
    }

    public boolean evaluateConstrainedParameter( Parameter allowedValuesParameter, Parameter disallowedValuesParameter, String constrainingValue, String value ) {
        return evaluateConstrainedParameter( allowedValuesParameter, constrainingValue, value )
                && evaluateConstrainedParameter( disallowedValuesParameter, constrainingValue, value );         
    }
    public List<String>getConstrainedValues( String namespaceCode, String parameterDetailTypeCode, String parameterName, String constrainingValue ) {
    	return getConstrainedValues( getParameter( namespaceCode, parameterDetailTypeCode, parameterName ), constrainingValue );
    }

    public List<String>getConstrainedValues( Parameter constraint, String constrainingValue ) {
    	Map<String,List<String>> constraintMap = convertParameterToConstraintMap( constraint );
    	List<String> values = constraintMap.get( constrainingValue );
    	if ( values == null ) {
    		values = new ArrayList( 1 );
    		values.add( "" );
        }
    	return values;
    }

    private Map<String,List<String>> convertParameterToConstraintMap( Parameter constraint ) {
    	// parse the value
    	String[] constraintValuePairs = getParameterValues( constraint );
    	Map<String,List<String>> map = new HashMap<String,List<String>>( constraintValuePairs.length );
    	for ( String pair : constraintValuePairs ) {
    		// parse on the "=" - before is the constraint value, after are the allowed or disallowed values
    		String key = StringUtils.substringBefore( pair, "=" );
    		// values after the "=" are delimited by commas
    		String[] values = StringUtils.substringAfter( pair, "=" ).split( "," );
    		map.put(  key, Arrays.asList( values ) );
    	}
    	return map;
    }
    
    //public boolean evaluateConstrainedParameter( Parameter allowConstraint, Parameter disallowConstraint, String constrainingValue, String value ) {
    	//
    //	return false;
    //}
    
    /**
     * @return the error key for the operator
     */
    public String getErrorMessageKey( Parameter parameter ) {
        if (isAllowedRule( parameter )) {
            return RiceKeyConstants.ERROR_APPLICATION_PARAMETERS_ALLOWED_RESTRICTION;
        }
        else {
            return RiceKeyConstants.ERROR_APPLICATION_PARAMETERS_DENIED_RESTRICTION;
    }
    }    

    public Parameter mergeParameters( Parameter...parameters ) {
    	if ( parameters.length == 1 ) {
    		return parameters[0];
    }
    	if ( parameters.length > 1 ) {
    		TreeSet<String> allowedValues = null;
    		TreeSet<String> deniedValues = new TreeSet<String>();
    		List<String> names = new ArrayList<String>( parameters.length );
    		for ( Parameter parameter : parameters ) {
    		    if ( parameter != null ) {
    		        names.add( parameter.getParameterName() );
    		    }
    			if ( isUsable( parameter ) ) {
    				if ( isAllowedRule( parameter ) ) {
    					if ( allowedValues == null ) {
    						allowedValues = new TreeSet<String>( getParameterValuesAsList( parameter ) );
    					} else {
    						allowedValues.retainAll( getParameterValuesAsList( parameter ) );
    					}
    				} else if ( isDeniedRule( parameter ) ) {
    					deniedValues.addAll( getParameterValuesAsList( parameter ) );
    				}
    			}
    		}
            if (allowedValues == null) {
                return new Parameter("and" + names, StringUtils.join(deniedValues.iterator(), ';'), RiceConstants.APC_DENIED_OPERATOR );
            } else {
                allowedValues.removeAll(deniedValues);
                // The ";" is a work-around to enforce allowing disjoint values (i.e., allowing nothing, always failing).
                // If the text were empty then the rule would be ignored, always succeeding.
                String text = allowedValues.isEmpty() ? ";" : StringUtils.join(allowedValues.iterator(), ';');
                return new Parameter("and" + names, text, RiceConstants.APC_ALLOWED_OPERATOR );
            }
    	}
    	throw new IllegalArgumentException( "You must pass at least one parameter to this method." );
    }    
    
    /**
     * Generates a String with the parameter values formatted prettily, with a comma and space between each value for multivalued
     * param values.
     * 
     * NOTE: order of original values may not be preserved (parse parameterText instead).
     * 
     * For example: value "2;3,4" would be formatted as "2, 3, 4"
     * 
     * @return a pretty string
     */
    public String getPrettyParameterValueString( Parameter parameter ) {
        StringBuffer buf = new StringBuffer();
        for ( String val : getParameterValuesAsSet( parameter ) ) {
        	if ( buf.length() > 0 ) {
        		buf.append( ", " );        		
    }
        	buf.append( val );
        }
        return buf.toString();
    }
    // using this instead of private variable with spring initialization because of recurring issues with circular references
    // resulting in this error: org.springframework.beans.factory.BeanCurrentlyInCreationException: Error creating bean with 
    // name 'businessObjectService': Bean with name 'businessObjectService' has been injected into other beans [kualiConfigurationService]
    // in its raw version as part of a circular reference, but has eventually been wrapped (for example as part of auto-proxy creation). 
    // This means that said other beans do not use the final version of the bean. This is often the result of over-eager type matching 
    // - consider using 'getBeanNamesOfType' with the 'allowEagerInit' flag turned off, for example.
    private BusinessObjectService getBusinessObjectService() {
	return KNSServiceLocator.getBusinessObjectService();
    }

}
