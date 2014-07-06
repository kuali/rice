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
package org.kuali.rice.core.api.criteria

import static org.junit.Assert.assertEquals
import static org.junit.Assert.fail

import org.junit.Test
import org.kuali.rice.core.api.util.type.KualiDecimal
import org.kuali.rice.core.api.util.type.KualiPercent
import org.kuali.rice.core.test.JAXBAssert

/**
 * A test for the {@link EqualPredicate} class.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ExistsSubQueryPredicateTest {

	private static final String EXPECTED_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><existsSubQuery subQueryType=\"SubQueryDataObjectClass\" xmlns=\"http://rice.kuali.org/core/v2_0\"><equal propertyPath=\"subQueryProp\"><propertyPathValue><propertyPath dataType=\"ParentObject\" propertyPath=\"parentProp\"/></propertyPathValue></equal></existsSubQuery>";

    /**
     * Test method for {@link GreaterThanPredicate#GreaterThanPredicate(java.lang.String, org.kuali.rice.core.api.criteria.CriteriaValue)}.
     * 
     * <p>GreaterThanExpression should support all of the different CriteriaValues except for {@link CriteriaStringValue}
     */
    @Test
    public void testExpression() {
        PropertyPath propertyPath = new PropertyPath("ParentObject","parentProp");
        Predicate innerPredicate = new EqualPredicate("subQueryProp", new CriteriaPropertyPathValue(propertyPath) );
        
        ExistsSubQueryPredicate predicate = new ExistsSubQueryPredicate("SubQueryDataObjectClass", innerPredicate );
        assertEquals("SubQueryDataObjectClass", predicate.getSubQueryType());
        assertEquals("subQueryProp", predicate.getSubQueryPredicate().getPropertyPath());
        assertEquals("parentProp", predicate.getSubQueryPredicate().getValue().getValue().getPropertyPath() );
        assertEquals("ParentObject", predicate.getSubQueryPredicate().getValue().getValue().getDataType() );
        
        // test failure cases, should throw IllegalArgumentException when null is passed
        try {
            new ExistsSubQueryPredicate(null, innerPredicate);
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected exception
        }

        // but should allow a null predicate
        try {
            new ExistsSubQueryPredicate("SubQueryDataObjectClass", null);
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
        
        ExistsSubQueryPredicate predicate = new ExistsSubQueryPredicate("SubQueryDataObjectClass", innerPredicate );
        JAXBAssert.assertEqualXmlMarshalUnmarshal(predicate, EXPECTED_XML, ExistsSubQueryPredicate.class);
    }

}
