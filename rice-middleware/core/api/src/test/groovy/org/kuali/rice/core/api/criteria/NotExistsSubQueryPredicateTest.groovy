/*
 * Copyright 2006-2015 The Kuali Foundation
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
package org.kuali.rice.core.api.criteria

import org.junit.Test
import org.kuali.rice.core.test.JAXBAssert

import static org.junit.Assert.assertEquals
import static org.junit.Assert.fail

/**
 * A test for the {@link NotExistsSubQueryPredicate} class.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class NotExistsSubQueryPredicateTest {

	private static final String EXPECTED_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><notExistsSubQuery subQueryType=\"SubQueryDataObjectClass\" xmlns=\"http://rice.kuali.org/core/v2_0\"><equal propertyPath=\"subQueryProp\"><propertyPathValue><propertyPath dataType=\"ParentObject\" propertyPath=\"parentProp\"/></propertyPathValue></equal></notExistsSubQuery>";

    /**
     * Test method for {@link GreaterThanPredicate#GreaterThanPredicate(java.lang.String, org.kuali.rice.core.api.criteria.CriteriaValue)}.
     * 
     * <p>GreaterThanExpression should support all of the different CriteriaValues except for {@link CriteriaStringValue}
     */
    @Test
    public void testExpression() {
        PropertyPath propertyPath = new PropertyPath("ParentObject","parentProp");
        Predicate innerPredicate = new EqualPredicate("subQueryProp", new CriteriaPropertyPathValue(propertyPath) );
        
        NotExistsSubQueryPredicate predicate = new NotExistsSubQueryPredicate("SubQueryDataObjectClass", innerPredicate );
        assertEquals("SubQueryDataObjectClass", predicate.getSubQueryType());
        assertEquals("subQueryProp", predicate.getSubQueryPredicate().getPropertyPath());
        assertEquals("parentProp", predicate.getSubQueryPredicate().getValue().getValue().getPropertyPath() );
        assertEquals("ParentObject", predicate.getSubQueryPredicate().getValue().getValue().getDataType() );
        
        // test failure cases, should throw IllegalArgumentException when null is passed
        try {
            new NotExistsSubQueryPredicate(null, innerPredicate);
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected exception
        }

        // but should allow a null predicate
        try {
            new NotExistsSubQueryPredicate("SubQueryDataObjectClass", null);
        } catch (IllegalArgumentException e) {
            fail("Should NOT have thrown an IllegalArgumentException");
        }
    }
    
    /**
     * Tests that the GreaterThanExpression can be marshalled and unmarshalled properly via JAXB.
     */
    @Test
    public void testJAXB() {
        PropertyPath propertyPath = new PropertyPath("ParentObject","parentProp");
        Predicate innerPredicate = new EqualPredicate("subQueryProp", new CriteriaPropertyPathValue(propertyPath) );
        
        NotExistsSubQueryPredicate predicate = new NotExistsSubQueryPredicate("SubQueryDataObjectClass", innerPredicate );
        JAXBAssert.assertEqualXmlMarshalUnmarshal(predicate, EXPECTED_XML, NotExistsSubQueryPredicate.class);
    }

}
