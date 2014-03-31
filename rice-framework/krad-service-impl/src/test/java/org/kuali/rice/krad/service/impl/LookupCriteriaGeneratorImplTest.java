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
package org.kuali.rice.krad.service.impl;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kuali.rice.core.api.criteria.AndPredicate;
import org.kuali.rice.core.api.criteria.GreaterThanOrEqualPredicate;
import org.kuali.rice.core.api.criteria.GreaterThanPredicate;
import org.kuali.rice.core.api.criteria.LessThanOrEqualPredicate;
import org.kuali.rice.core.api.criteria.LessThanPredicate;
import org.kuali.rice.core.api.criteria.LikePredicate;
import org.kuali.rice.core.api.criteria.OrPredicate;
import org.kuali.rice.core.api.criteria.Predicate;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.search.SearchOperator;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.data.DataObjectWrapper;
import org.kuali.rice.krad.data.metadata.DataObjectMetadata;
import org.kuali.rice.krad.data.provider.impl.DataObjectWrapperBase;
import org.kuali.rice.krad.data.util.ReferenceLinker;
import org.kuali.rice.krad.datadictionary.DataDictionary;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.format.datetime.joda.DateTimeFormatterFactory;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the functionality of the LookupCriteriaGeneratorImpl.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@RunWith(MockitoJUnitRunner.class)
public class LookupCriteriaGeneratorImplTest {

    @Mock DataDictionary dataDictionary;
    @Mock DataDictionaryService dataDictionaryService;
    @Mock DataObjectService dataObjectService;
    @Mock ReferenceLinker referenceLinker;
    @Mock DateTimeService dateTimeService;

    @InjectMocks private LookupCriteriaGeneratorImpl generator = new LookupCriteriaGeneratorImpl();

    private static final DateTimeFormatter formatter = new DateTimeFormatterFactory("mm/dd/yyyy").createDateTimeFormatter();

    @Before
    public void setUp() throws Exception {
        when(dataDictionaryService.getDataDictionary()).thenReturn(dataDictionary);
        when(dataObjectService.wrap(any(TestClass.class))).thenAnswer(new Answer<DataObjectWrapper<TestClass>>() {
            @Override
            public DataObjectWrapper<TestClass> answer(InvocationOnMock invocation) throws Throwable {
                return new DataObjectWrapperImpl<TestClass>((TestClass)invocation.getArguments()[0],
                        mock(DataObjectMetadata.class), dataObjectService, referenceLinker);
            }
        });
        when(dateTimeService.convertToSqlDate(any(String.class))).thenAnswer(new Answer<Date>() {
            @Override
            public Date answer(InvocationOnMock invocation) throws Throwable {
                String date = (String) invocation.getArguments()[0];
                return new Date(LocalDate.parse(date, formatter).toDateTimeAtStartOfDay().getMillis());
            }
        });
        when(dateTimeService.convertToSqlDateUpperBound(any(String.class))).thenAnswer(new Answer<Date>() {
            @Override
            public Date answer(InvocationOnMock invocation) throws Throwable {
                String date = (String) invocation.getArguments()[0];
                return new Date(LocalDate.parse(date, formatter).plusDays(1).toDateTimeAtStartOfDay().getMillis());
            }
        });
    }

    @Test
    public void testGenerateCriteria_MultipleOr() throws Exception {
        Map<String, String> mapCriteria = new HashMap<String, String>();
        mapCriteria.put("prop1", "a|b");
        mapCriteria.put("prop2", "c");
        mapCriteria.put("prop3", "d");

        QueryByCriteria.Builder qbcBuilder = generator.generateCriteria(TestClass.class, mapCriteria, false);
        assertNotNull(qbcBuilder);
        QueryByCriteria qbc = qbcBuilder.build();

        // now walk the tree, it should come out as:
        // and(
        //   or(
        //     like(prop1, "a"),
        //     like(prop1, "b"),
        //   ),
        //   like(prop2, "c"),
        //   like(prop3, "d")
        // )

        Predicate and = qbc.getPredicate();
        assertTrue(and instanceof AndPredicate);
        Set<Predicate> predicates = ((AndPredicate) and).getPredicates();

        assertEquals(3, predicates.size());

        boolean foundProp1 = false;
        boolean foundProp2 = false;
        boolean foundProp3 = false;
        for (Predicate predicate : predicates) {
            if (predicate instanceof LikePredicate) {
                LikePredicate like = (LikePredicate)predicate;
                if (like.getPropertyPath().equals("prop2")) {
                    assertEquals("c", like.getValue().getValue());
                    foundProp2 = true;
                } else if (like.getPropertyPath().equals("prop3")) {
                    assertEquals("d", like.getValue().getValue());
                    foundProp3 = true;
                } else {
                    fail("Invalid like predicate encountered.");
                }
            } else if (predicate instanceof OrPredicate) {
                foundProp1 = true;
                // under the or predicate we should have 2 likes, one for each component of the OR
                OrPredicate orPredicate = (OrPredicate)predicate;
                assertEquals(2, orPredicate.getPredicates().size());
                for (Predicate orSubPredicate : orPredicate.getPredicates()) {
                    if (orSubPredicate instanceof LikePredicate) {
                        LikePredicate likeInternal = (LikePredicate)orSubPredicate;
                        if (likeInternal.getPropertyPath().equals("prop1")) {
                            assertTrue("a".equals(likeInternal.getValue().getValue()) ||
                                    "b".equals(likeInternal.getValue().getValue()));
                        } else {
                            fail("Invalid predicate, does not have a propertypath of prop1");
                        }
                    } else {
                        fail("Invalid predicate: " + orSubPredicate);
                    }
                }
            } else {
                fail("Invalid predicate: " + predicate);
            }
        }
        assertTrue(foundProp1);
        assertTrue(foundProp2);
        assertTrue(foundProp3);


    }

    /**
     * Criteria for BETWEEN dates should range from the start of the day on the lower date to the end of the day on the
     * upper date.
     *
     * <p>
     * Since the end of the day is defined as the moment before the next day, then the range that should be checked is
     * [1/1/2010,1/2/2010), or in SQL, approximately >=2010-01-01 00:00:00 AND <=2010-01-03 00:00:00.
     * </p>
     */
    @Test
    public void testGenerateCriteria_BetweenDate() {
        String lowerDateString = "1/1/2010";
        DateTime lowerDate = DateTime.parse(lowerDateString, formatter).withTimeAtStartOfDay();
        String upperDateString = "1/2/2010";
        DateTime upperDate = DateTime.parse(upperDateString, formatter).withTimeAtStartOfDay();

        Map<String, String> mapCriteria = new HashMap<String, String>();
        mapCriteria.put("prop4", lowerDateString + SearchOperator.BETWEEN.op() + upperDateString);

        QueryByCriteria.Builder qbcBuilder = generator.generateCriteria(TestClass.class, mapCriteria, false);
        assertNotNull(qbcBuilder);
        QueryByCriteria qbc = qbcBuilder.build();

        Predicate and = qbc.getPredicate();
        assertTrue(and instanceof AndPredicate);
        Set<Predicate> predicates = ((AndPredicate) and).getPredicates();

        assertEquals(2, predicates.size());

        boolean foundProp4Lower = false;
        boolean foundProp4Upper = false;
        for (Predicate predicate : predicates) {
            if (predicate instanceof GreaterThanOrEqualPredicate) {
                foundProp4Lower = true;
                GreaterThanOrEqualPredicate greaterThanOrEqual = (GreaterThanOrEqualPredicate) predicate;
                assertEquals(greaterThanOrEqual.getValue().getValue(), lowerDate);
            } else if (predicate instanceof LessThanOrEqualPredicate) {
                foundProp4Upper = true;
                LessThanOrEqualPredicate lessThanOrEqual = (LessThanOrEqualPredicate) predicate;
                assertEquals(lessThanOrEqual.getValue().getValue(), upperDate.plusDays(1));
            }
        }
        assertTrue(foundProp4Lower);
        assertTrue(foundProp4Upper);
    }

    /**
     * Criteria for GREATER THAN OR EQUAL dates should be equal to or after the start of the day on the date.
     *
     * <p>
     * The value that should be checked is [1/1/2010,END_OF_TIME), or in SQL, >=2010-01-01 00:00:00.
     * </p>
     */
    @Test
    public void testGenerateCriteria_GreaterThanEqualDate() {
        String dateString = "1/1/2010";
        DateTime date = DateTime.parse(dateString, formatter).withTimeAtStartOfDay();

        Map<String, String> mapCriteria = new HashMap<String, String>();
        mapCriteria.put("prop4", SearchOperator.GREATER_THAN_EQUAL.op() + dateString);

        QueryByCriteria.Builder qbcBuilder = generator.generateCriteria(TestClass.class, mapCriteria, false);
        assertNotNull(qbcBuilder);
        QueryByCriteria qbc = qbcBuilder.build();

        Predicate greaterThanEqual = qbc.getPredicate();
        assertTrue(greaterThanEqual instanceof GreaterThanOrEqualPredicate);
        assertEquals(((GreaterThanOrEqualPredicate) greaterThanEqual).getValue().getValue(), date);
    }

    /**
     * Criteria for LESS THAN OR EQUAL dates should be equal to or before the end of the day on the date.
     *
     * <p>
     * Since the end of the day is defined as the moment before the next day, then the value that should be
     * checked is (BEGINNING_OF_TIME,1/2/2010), or in SQL, approximately <=2010-01-03 00:00:00.
     * </p>
     */
    @Test
    public void testGenerateCriteria_LessThanEqualDate() {
        String dateString = "1/2/2010";
        DateTime date = DateTime.parse(dateString, formatter).withTimeAtStartOfDay();

        Map<String, String> mapCriteria = new HashMap<String, String>();
        mapCriteria.put("prop4", SearchOperator.LESS_THAN_EQUAL.op() + dateString);

        QueryByCriteria.Builder qbcBuilder = generator.generateCriteria(TestClass.class, mapCriteria, false);
        assertNotNull(qbcBuilder);
        QueryByCriteria qbc = qbcBuilder.build();

        Predicate lessThanEqual = qbc.getPredicate();
        assertTrue(lessThanEqual instanceof LessThanOrEqualPredicate);
        assertEquals(((LessThanOrEqualPredicate) lessThanEqual).getValue().getValue(), date.plusDays(1));
    }

    /**
     * Criteria for GREATER THAN dates should be after the start of the day on the date.
     *
     * <p>
     * The value that should be checked is >2010-01-01 00:00:00.
     * </p>
     */
    @Test
    public void testGenerateCriteria_GreaterThanDate() {
        String dateString = "1/1/2010";
        DateTime date = DateTime.parse(dateString, formatter).withTimeAtStartOfDay();

        Map<String, String> mapCriteria = new HashMap<String, String>();
        mapCriteria.put("prop4", SearchOperator.GREATER_THAN.op() + dateString);

        QueryByCriteria.Builder qbcBuilder = generator.generateCriteria(TestClass.class, mapCriteria, false);
        assertNotNull(qbcBuilder);
        QueryByCriteria qbc = qbcBuilder.build();

        Predicate greaterThan = qbc.getPredicate();
        assertTrue(greaterThan instanceof GreaterThanPredicate);
        assertEquals(((GreaterThanPredicate) greaterThan).getValue().getValue(), date);
    }

    /**
     * Criteria for LESS THAN dates should be before the start of the day on the date.
     *
     * <p>
     * The value that should be checked is <2010-02-01 00:00:00.
     * </p>
     */
    @Test
    public void testGenerateCriteria_LessThanDate() {
        String dateString = "1/2/2010";
        DateTime date = DateTime.parse(dateString, formatter).withTimeAtStartOfDay();

        Map<String, String> mapCriteria = new HashMap<String, String>();
        mapCriteria.put("prop4", SearchOperator.LESS_THAN.op() + dateString);

        QueryByCriteria.Builder qbcBuilder = generator.generateCriteria(TestClass.class, mapCriteria, false);
        assertNotNull(qbcBuilder);
        QueryByCriteria qbc = qbcBuilder.build();

        Predicate lessThan = qbc.getPredicate();
        assertTrue(lessThan instanceof LessThanPredicate);
        assertEquals(((LessThanPredicate) lessThan).getValue().getValue(), date);
    }

    public static final class TestClass {

        private String prop1;
        private String prop2;
        private String prop3;
        private Date prop4;

        public String getProp1() {
            return prop1;
        }

        public void setProp1(String prop1) {
            this.prop1 = prop1;
        }

        public String getProp2() {
            return prop2;
        }

        public void setProp2(String prop2) {
            this.prop2 = prop2;
        }

        public String getProp3() {
            return prop3;
        }

        public void setProp3(String prop3) {
            this.prop3 = prop3;
        }

        public Date getProp4() {
            return prop4;
        }

        public void setProp4(Date prop4) {
            this.prop4 = prop4;
        }

    }

    private static final class DataObjectWrapperImpl<T> extends DataObjectWrapperBase<T> {
        private DataObjectWrapperImpl(T dataObject, DataObjectMetadata metadata, DataObjectService dataObjectService,
                ReferenceLinker referenceLinker) {
            super(dataObject, metadata, dataObjectService, referenceLinker);
        }
    }

}
