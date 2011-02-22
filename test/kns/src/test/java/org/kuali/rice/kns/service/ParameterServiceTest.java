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
package org.kuali.rice.kns.service;

import org.junit.Test;
import org.kuali.rice.core.api.parameter.Parameter;
import org.kuali.rice.core.framework.services.CoreFrameworkServiceLocator;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.test.KNSTestCase;
import org.kuali.test.KNSWithTestSpringContext;

import static org.junit.Assert.*;

/**
 * This class is used to test the {@link NoteService} implementation 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@KNSWithTestSpringContext
public class ParameterServiceTest extends KNSTestCase {

    /**
     * This method tests saving notes when using the {@link RiceKNSDefaultUserDAOImpl} as the implementation of {@link PersonDao}
     * 
     * @throws Exception
     */
    @Test
    public void testRetrieveParameter_WithDatabaseDetailType() throws Exception {
    	String namespaceCode = "KR-NS";
    	String parameterDetailTypeCode = "Lookup";
    	String parameterName = "RESULTS_LIMIT";
    	String parameterValue = "200";
    	
    	Parameter resultsLimitParam = CoreFrameworkServiceLocator.getClientParameterService().getParameter(namespaceCode, parameterDetailTypeCode, parameterName);
    	assertNotNull("RESULTS_LIMIT should be non-null", resultsLimitParam);
    	assertEquals(parameterValue, resultsLimitParam.getValue());
    	
    	String detailType = resultsLimitParam.getComponentCode();
    	assertNotNull("Should have a detail type: " + detailType);
    	
    }
    
    @Test    
    public void testRetrieveParameter_WithNonDatabaseDetailType() throws Exception {
    	String namespaceCode = "KR-IDM";
    	String parameterDetailTypeCode = "EntityNameImpl";
    	String parameterName = "PREFIXES";
    	String parameterValue = "Ms;Mrs;Mr;Dr";
    	
    	Parameter parameter = CoreFrameworkServiceLocator.getClientParameterService().getParameter(namespaceCode, parameterDetailTypeCode, parameterName);
    	assertNotNull("Parameter should be non-null", parameter);
    	assertEquals(parameterValue, parameter.getValue());
    	
    	String detailType = parameter.getComponentCode();

    	assertTrue("Should not have a detail type because no record in database", ObjectUtils.isNull(detailType));
    	
    }

    
}

