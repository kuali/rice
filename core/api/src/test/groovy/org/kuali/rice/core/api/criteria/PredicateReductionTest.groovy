package org.kuali.rice.core.api.criteria

import org.junit.Assert
import org.junit.Test
import static org.kuali.rice.core.api.criteria.PredicateFactory.and
import static org.kuali.rice.core.api.criteria.PredicateFactory.equal

class PredicateReductionTest {

    @Test
    void test_or() {
        def pred = and(
           and (equal("pp", "foo"))
        )

        Assert.assertTrue(pred instanceof EqualPredicate);
    }

    @Test
    void test_and() {
        def pred = and(
           and (equal("pp", "foo"))
        )

        Assert.assertTrue(pred instanceof EqualPredicate);
    }
}


