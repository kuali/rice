/**
 * Copyright 2005-2017 The Kuali Foundation
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
package org.kuali.rice.core.api.criteria;


import static org.junit.Assert.assertEquals
import static org.junit.Assert.fail

import org.junit.Test
import org.kuali.rice.core.test.JAXBAssert

/**
 * A test for the {@link NotLikeIgnoreCasePredicate} class.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class NotLikeIgnoreCasePredicateTest {

    private static final String STRING_XML = "<notLikeIgnoreCase propertyPath=\"property.path\" xmlns=\"http://rice.kuali.org/core/v2_0\"><stringValue>value*</stringValue></notLikeIgnoreCase>";

    /**
     * Test method for {@link NotLikeIgnoreCasePredicate#LikeIgnoreCasePredicate(java.lang.String, org.kuali.rice.core.api.criteria.CriteriaValue)}.
     *
     * <p>LikeExpression only supports CriteriaStringValue
     */
    @Test
    public void testLikeExpression() {

        // Test that it can take a CriteriaStringValue
        NotLikeIgnoreCasePredicate likeExpression = new NotLikeIgnoreCasePredicate("property.path", new CriteriaStringValue("value*"));
        assertEquals("property.path", likeExpression.getPropertyPath());
        assertEquals("value*", likeExpression.getValue().getValue());

        // Doesn't support decimal, integer, or dateTime criteria values
        try {
            new NotLikeIgnoreCasePredicate("property.path", new CriteriaDecimalValue(BigDecimal.ZERO));
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected exception
        }
        try {
            new NotLikeIgnoreCasePredicate("property.path", new CriteriaIntegerValue(BigInteger.ZERO));
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected exception
        }
        try {
            new NotLikeIgnoreCasePredicate("property.path", new CriteriaDateTimeValue(Calendar.getInstance()));
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected exception
        }

        // test failure cases, should throw IllegalArgumentException when null is passed
        try {
            new NotLikeIgnoreCasePredicate(null, null);
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected exception
        }

    }

    /**
     * Tests that the NotLikeExpression can be marshalled and unmarshalled properly via JAXB.
     */
    @Test
    public void testJAXB() {
        NotLikeIgnoreCasePredicate likeExpression = new NotLikeIgnoreCasePredicate("property.path", new CriteriaStringValue("value*"));
        JAXBAssert.assertEqualXmlMarshalUnmarshal(likeExpression, STRING_XML, NotLikeIgnoreCasePredicate.class);
    }
}
