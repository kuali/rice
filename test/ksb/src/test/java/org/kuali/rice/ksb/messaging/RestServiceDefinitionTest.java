/*
 * Copyright 2007-2010 The Kuali Foundation
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
package org.kuali.rice.ksb.messaging;

import org.junit.Test;
import org.kuali.rice.ksb.test.KSBTestCase;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests equality between RESTServiceDefinition objects
 * 
 * @author James Renfro
 * @since 1.3
 *
 */
public class RestServiceDefinitionTest extends KSBTestCase {
    
    private RESTServiceDefinition restDefinition;
    private RESTServiceDefinition sameExactRestDefinition;
    private RESTServiceDefinition sameFunctionallyRestDefinition;
    private RESTServiceDefinition otherRestDefinition;
    private RESTServiceDefinition otherNameRestDefinition;
    private RESTServiceDefinition otherServiceRestDefinition;
    private RESTServiceDefinition singleResourceDefinition;
    private JavaServiceDefinition javaServiceDefinition;

    public void setUp() throws Exception {
    	super.setUp();
    	
    	String a = "a";
    	String b = "b";
    	String c = "c";
    	Long l = Long.valueOf(123l);
    	
    	List<Object> restResources = new ArrayList<Object>();
    	restResources.add(a);
    	restResources.add(b);
    	
    	List<Object> sameExactRestResources = new ArrayList<Object>();
    	sameExactRestResources.add(a);
    	sameExactRestResources.add(b);
    	
    	// It's the type that matters, not the value
    	List<Object> functionallySameResources = new ArrayList<Object>();
    	functionallySameResources.add(b);
    	functionallySameResources.add(c);
    	
    	List<Object> otherRestResources = new ArrayList<Object>();
    	otherRestResources.add(l);
    	otherRestResources.add(b);
    	
    	Object service = new ArrayList();
    	
        this.restDefinition = new RESTServiceDefinition();
        this.restDefinition.setLocalServiceName("restServiceName");
        this.restDefinition.setResources(restResources);
        this.restDefinition.validate();
        
        this.sameExactRestDefinition = new RESTServiceDefinition();
        this.sameExactRestDefinition.setLocalServiceName("restServiceName");
        this.sameExactRestDefinition.setResources(sameExactRestResources);
        this.sameExactRestDefinition.validate();
        
        this.sameFunctionallyRestDefinition = new RESTServiceDefinition();
        this.sameFunctionallyRestDefinition.setLocalServiceName("restServiceName");
        this.sameFunctionallyRestDefinition.setResources(functionallySameResources);
        this.sameFunctionallyRestDefinition.validate();
        
        this.otherRestDefinition = new RESTServiceDefinition();
        this.otherRestDefinition.setLocalServiceName("restServiceName");
        this.otherRestDefinition.setResources(otherRestResources);
        this.otherRestDefinition.validate();
        
        this.otherNameRestDefinition = new RESTServiceDefinition();
        this.otherNameRestDefinition.setLocalServiceName("anotherRestServiceName");
        this.otherNameRestDefinition.setResources(sameExactRestResources);
        this.otherNameRestDefinition.validate();
        
        this.otherServiceRestDefinition = new RESTServiceDefinition();
        this.otherServiceRestDefinition.setLocalServiceName("restServiceName");
        this.otherServiceRestDefinition.setService(service);
        this.otherServiceRestDefinition.setResources(restResources);
        this.otherServiceRestDefinition.validate();
        
        this.singleResourceDefinition = new RESTServiceDefinition();
        this.singleResourceDefinition.setLocalServiceName("restServiceName");
        this.singleResourceDefinition.setService(service);
        this.singleResourceDefinition.validate();
        
        javaServiceDefinition = new JavaServiceDefinition();
        javaServiceDefinition.setBusSecurity(Boolean.FALSE);
        javaServiceDefinition.setLocalServiceName("restServiceName");
        javaServiceDefinition.setService(service);
        javaServiceDefinition.validate();
    }
    
    @Test
    public void testIsSameSuccessWithSameDefinition() {
        assertTrue(this.restDefinition.isSame(this.restDefinition));
    }
    
    @Test
    public void testIsSameSuccessWithDifferentDefinition() throws Exception {
        assertTrue(this.restDefinition.isSame(sameExactRestDefinition));
    }
    
    @Test
    public void testIsSameSuccessWithDifferentDefinitionThatIsFunctionallySame() throws Exception {
        assertTrue(this.restDefinition.isSame(sameFunctionallyRestDefinition));
    }
    
    @Test
    public void testIsSameFailureWithDifferentServiceClass() throws Exception {
        assertFalse(this.restDefinition.isSame(otherRestDefinition));
    }
    
    @Test
    public void testIsSameFailureWithDifferentDefinitionOfSameResources() throws Exception {
    	assertFalse(this.restDefinition.isSame(otherNameRestDefinition));
    }
    
    @Test
    public void testIsSameFailureWithDifferentService() throws Exception {
    	assertFalse(this.restDefinition.isSame(otherServiceRestDefinition));
    }
    
    @Test
    public void testIsSameFailureWithSingleResourceService() throws Exception {
    	assertFalse(this.restDefinition.isSame(singleResourceDefinition));
    }
    
    @Test
    public void testIsSameFailureWithDifferentServiceDefinitionType() throws Exception {
        assertFalse(this.otherServiceRestDefinition.isSame(javaServiceDefinition));
    }

}
