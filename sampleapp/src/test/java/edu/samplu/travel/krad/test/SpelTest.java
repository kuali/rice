/*
 * Copyright 2006-2012 The Kuali Foundation
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

package edu.samplu.travel.krad.test;

import org.junit.Before;
import org.junit.Test;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

/**
 * test spel expressions used in krad
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class SpelTest {
    private SpelExpressionParser parser;

    @Before
    public void setUp() throws Exception {
        parser = new SpelExpressionParser();
    }

    @Test
    public void testSpelWithDotPrefix() {
        Expression expression = parser.parseExpression(".field88 eq 'none'");
    }

    @Test
    public void testSpelWithoutDotPrefix() {
        Expression expression = parser.parseExpression("field88 eq 'none'");
    }

    @Test
    public void testSpelWithObjectPrefix() {
        Expression expression = parser.parseExpression("form.field88 eq 'none'");
    }
}
