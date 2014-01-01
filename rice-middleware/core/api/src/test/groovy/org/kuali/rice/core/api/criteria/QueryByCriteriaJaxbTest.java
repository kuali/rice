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
package org.kuali.rice.core.api.criteria;

import junit.framework.Assert;
import org.joda.time.DateTime;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * tests that all predicates can be marshalled and unmarshalled successfully
 */
public class QueryByCriteriaJaxbTest {

    Logger LOG = LoggerFactory.getLogger(QueryByCriteriaJaxbTest.class);

    // All the Predicates that will be magically discovered have to be under this package
    public static final String PREDICATE_BASE_PACKAGE = "org/kuali/rice/core/api/criteria";

    // Set up map FROM all known predicate classes TO example predicate instances
    static final HashMap<Class<?>, Predicate> predicateSamplesMap = new HashMap<Class<?>, Predicate>();

    static {
        predicateSamplesMap.put(EqualPredicate.class, PredicateFactory.equal("foo", "val"));
        predicateSamplesMap.put(EqualIgnoreCasePredicate.class, PredicateFactory.equalIgnoreCase("foo", "val"));
        predicateSamplesMap.put(GreaterThanPredicate.class, PredicateFactory.greaterThan("foo", 10));
        predicateSamplesMap.put(GreaterThanOrEqualPredicate.class, PredicateFactory.greaterThanOrEqual("foo", 10));
        predicateSamplesMap.put(InPredicate.class, PredicateFactory.in("foo", "val"));
        predicateSamplesMap.put(InIgnoreCasePredicate.class, PredicateFactory.inIgnoreCase("foo", "val"));
        predicateSamplesMap.put(NotNullPredicate.class, PredicateFactory.isNotNull("foo"));
        predicateSamplesMap.put(NullPredicate.class, PredicateFactory.isNull("foo"));
        predicateSamplesMap.put(LessThanPredicate.class, PredicateFactory.lessThan("foo", 10));
        predicateSamplesMap.put(LessThanOrEqualPredicate.class, PredicateFactory.lessThanOrEqual("foo", 10));
        predicateSamplesMap.put(LikePredicate.class, PredicateFactory.like("foo", "val"));
        predicateSamplesMap.put(LikeIgnoreCasePredicate.class, PredicateFactory.likeIgnoreCase("foo", "val"));
        predicateSamplesMap.put(NotEqualPredicate.class, PredicateFactory.notEqual("foo", "val"));
        predicateSamplesMap.put(NotEqualIgnoreCasePredicate.class, PredicateFactory.notEqualIgnoreCase("foo", "val"));
        predicateSamplesMap.put(NotInPredicate.class, PredicateFactory.notIn("foo", "val"));
        predicateSamplesMap.put(NotInIgnoreCasePredicate.class, PredicateFactory.notInIgnoreCase("foo", "val"));
        predicateSamplesMap.put(NotLikePredicate.class, PredicateFactory.notLike("foo", "val"));
    }

    @Test
    public void testAllPredicates() throws Exception {

        ArrayList<Class<?>> discoveredPredicateClasses = discoverSimplePredicateClasses();

        // test each predicate on their own
        for (Class<?> discoveredPredicateClass : discoveredPredicateClasses) {
            // create a query containing a sample of this predicate type
            Predicate sample = predicateSamplesMap.get(discoveredPredicateClass);
            QueryByCriteria queryByCriteria = wrapInQueryByCriteria(sample);

            String xml = marshallToString(queryByCriteria);
            String lowerCaseXml = xml.toLowerCase();

            // get the class name without the word Predicate which is our convention for naming the xml elements.
            // in other words, the element for an InIgnoreCasePredicate looks like <inIgnoreCase ...>
            String className = discoveredPredicateClass.getSimpleName();
            String shortenedClassName = className.substring(0, className.length() - "predicate".length());

            // make sure the xml contains an element for the predicate with the correct name
            Assert.assertTrue("XML doesn't appear to contain " + shortenedClassName,
                    lowerCaseXml.contains("<"+shortenedClassName.toLowerCase()+" "));

            // make sure that JAXB isn't producing xsi:type attributes for this type, that means we're doing it wrong
            Assert.assertFalse("XML is using xsi:type for " + shortenedClassName, lowerCaseXml.contains(
                    "xsi:type=\"" + shortenedClassName.toLowerCase() + "type\""));

            Assert.assertEquals("unmarshalled XML produces a non-equivalent object", unMarshall(xml), queryByCriteria);
        }


        // build a big composite predicate containing examples of every type of simple predicate
        Predicate andPredicate = buildSampleCompositePredicate(discoveredPredicateClasses);

        // build a QueryByCriteria to hold the sample predicate tree
        QueryByCriteria queryByCriteria = wrapInQueryByCriteria(andPredicate);
        // Marshal it to a String
        String xml = marshallToString(queryByCriteria);

        String lowerCaseXml = xml.toLowerCase(); // lower case makes searching simpler

        Assert.assertEquals("unmarshalled XML produces a non-equivalent object", unMarshall(xml), queryByCriteria);

        for (Class<?> discoveredPredicateClass : discoveredPredicateClasses) {
            String className = discoveredPredicateClass.getSimpleName();
            String shortenedClassName = className.substring(0, className.length() - "predicate".length());
            Assert.assertTrue("XML doesn't appear to contain " + shortenedClassName,
                    lowerCaseXml.contains("<"+shortenedClassName.toLowerCase()+" "));
            Assert.assertFalse("XML is using xsi:type for " + shortenedClassName, lowerCaseXml.contains(
                    "xsi:type=\"" + shortenedClassName.toLowerCase() + "type\""));
        }

    }

    @Test
    public void testAndAttributes_Null() {
        QueryByCriteria criteria = QueryByCriteria.Builder.andAttributes(new HashMap<String, String>()).build();
        assertEmptyCriteria(criteria);
    }

    @Test
    public void testAndAttributes_Empty() {
        QueryByCriteria criteria = QueryByCriteria.Builder.andAttributes(new HashMap<String, String>()).build();
        assertEmptyCriteria(criteria);
    }

    @Test
    public void testAndAttributes() {
        Map<String, Object> attributes = new HashMap<String, Object>();

        DateTime now = new DateTime();

        // 7 attributes, including an empty list and empty set
        attributes.put("nonNullString", "abcdefg");
        attributes.put("null", null);
        attributes.put("list", Arrays.asList("a", "b", "c"));
        attributes.put("emptyList", Collections.emptyList());
        attributes.put("emptySet", Collections.emptySet());
        attributes.put("set", new HashSet<Integer>(Arrays.asList(5)));
        attributes.put("date", now);

        // now create the criteria
        QueryByCriteria criteria = QueryByCriteria.Builder.andAttributes(attributes).build();
        Predicate predicate = criteria.getPredicate();
        assertTrue(predicate instanceof AndPredicate);
        AndPredicate and = (AndPredicate)predicate;

        // there should be 5 predicates because the emptyList and emptySet will simply be dropped since they represents no real criteria
        assertEquals("Should be 5 predicates in the AND", 5, and.getPredicates().size());

        // now let's pick out the individual predicates, since there's only a single value in the set predicate, it should get reduced down to a simple equal
        EqualPredicate nonNullStringPredicate = null;
        NullPredicate nullPredicate = null;
        OrPredicate listPredicate = null;
        EqualPredicate setPredicate = null;
        EqualPredicate datePredicate = null;
        for (Predicate pred : and.getPredicates()) {
            if (pred instanceof PropertyPathPredicate) {
                String propertyPath = ((PropertyPathPredicate)pred).getPropertyPath();
                if (propertyPath.equals("nonNullString")) {
                    nonNullStringPredicate = (EqualPredicate)pred;
                } else if (propertyPath.equals("null")) {
                    nullPredicate = (NullPredicate)pred;
                } else if (propertyPath.equals("date")) {
                    datePredicate = (EqualPredicate)pred;
                } else if (propertyPath.equals("set")) {
                    setPredicate = (EqualPredicate)pred;
                } else {
                    fail("Encountered an invalid PropertyPathPredicate with propertyPath=" + propertyPath);
                }
            } else if (pred instanceof CompositePredicate) {
                CompositePredicate composite = (CompositePredicate)pred;
                String propertyPath = ((PropertyPathPredicate)composite.getPredicates().iterator().next()).getPropertyPath();
                if (propertyPath.equals("list")) {
                    listPredicate = (OrPredicate)pred;
                } else {
                    fail("Encountered an invalid CompositePredicate with first entry with propertyPath=" + propertyPath);
                }
            } else {
                fail("Invalid predicate encountered: " + pred);
            }
        }

        // check non-null predicate
        assertNotNull(nonNullStringPredicate);
        assertTrue(nonNullStringPredicate.getValue() instanceof CriteriaStringValue);
        assertEquals("abcdefg", nonNullStringPredicate.getValue().getValue());

        // check null predicate
        assertNotNull(nullPredicate);

        // check date predicate
        assertNotNull(datePredicate);
        assertTrue(datePredicate.getValue() instanceof CriteriaDateTimeValue);
        assertEquals(now, datePredicate.getValue().getValue());

        // check list predicate
        assertNotNull(listPredicate);
        assertEquals(3, listPredicate.getPredicates().size());
        Set<String> listValues = new HashSet<String>();
        for (Predicate listEntry : listPredicate.getPredicates()) {
            assertTrue(listEntry instanceof EqualPredicate);
            EqualPredicate le = (EqualPredicate)listEntry;
            assertEquals("list", le.getPropertyPath());
            assertTrue(le.getValue() instanceof CriteriaStringValue);
            String value = (String)le.getValue().getValue();
            listValues.add(value);
        }
        assertTrue(listValues.contains("a"));
        assertTrue(listValues.contains("b"));
        assertTrue(listValues.contains("c"));

        // check set predicate
        assertNotNull(setPredicate);
        // since set predicate only had a size of 1, it will be reduced down to a simple equal predicate instead of an OR
        assertEquals("set", setPredicate.getPropertyPath());
        assertTrue(setPredicate.getValue() instanceof CriteriaIntegerValue);
        assertEquals(BigInteger.valueOf(5), (BigInteger)setPredicate.getValue().getValue());
    }

    @Test
    public void testOrAttributes_Null() {
        QueryByCriteria criteria = QueryByCriteria.Builder.orAttributes(new HashMap<String, String>()).build();
        assertEmptyCriteria(criteria);
    }

    @Test
    public void testOrAttributes_Empty() {
        QueryByCriteria criteria = QueryByCriteria.Builder.orAttributes(new HashMap<String, String>()).build();
        assertEmptyCriteria(criteria);
    }

    @Test
    public void testOrAttributes() {
        Map<String, Object> attributes = new HashMap<String, Object>();

        DateTime now = new DateTime();

        // 7 attributes, including an empty list and empty set
        attributes.put("nonNullString", "abcdefg");
        attributes.put("null", null);
        attributes.put("list", Arrays.asList("a", "b", "c"));
        attributes.put("emptyList", Collections.emptyList());
        attributes.put("emptySet", Collections.emptySet());
        attributes.put("set", new HashSet<Integer>(Arrays.asList(5)));
        attributes.put("date", now);

        // now create the criteria
        QueryByCriteria criteria = QueryByCriteria.Builder.orAttributes(attributes).build();
        Predicate predicate = criteria.getPredicate();
        assertTrue(predicate instanceof OrPredicate);
        OrPredicate or = (OrPredicate)predicate;

        // there should be 7 predicates because the entries in the list will get converted to top-level predicates since we are doing an or anyway (no need to nest the OR's)
        assertEquals("Should be 7 predicates in the AND", 7, or.getPredicates().size());

        // now let's pick out the individual predicates
        EqualPredicate nonNullStringPredicate = null;
        NullPredicate nullPredicate = null;
        EqualPredicate listA = null;
        EqualPredicate listB = null;
        EqualPredicate listC = null;
        EqualPredicate setPredicate = null;
        EqualPredicate datePredicate = null;
        for (Predicate pred : or.getPredicates()) {
            if (pred instanceof PropertyPathPredicate) {
                String propertyPath = ((PropertyPathPredicate)pred).getPropertyPath();
                if (propertyPath.equals("nonNullString")) {
                    nonNullStringPredicate = (EqualPredicate)pred;
                } else if (propertyPath.equals("null")) {
                    nullPredicate = (NullPredicate)pred;
                } else if (propertyPath.equals("list")) {
                    EqualPredicate listPredicate = (EqualPredicate)pred;
                    assertTrue(listPredicate.getValue() instanceof CriteriaStringValue);
                    String value = (String)listPredicate.getValue().getValue();
                    if (value.equals("a")) {
                        listA = listPredicate;
                    } else if (value.equals("b")) {
                        listB = listPredicate;
                    } else if (value.equals("c")) {
                        listC = listPredicate;
                    } else {
                        fail("Encountered invalid value for list predicate: " + value);
                    }
                } else if (propertyPath.equals("date")) {
                    datePredicate = (EqualPredicate)pred;
                } else if (propertyPath.equals("set")) {
                    setPredicate = (EqualPredicate)pred;
                } else {
                    fail("Encountered an invalid PropertyPathPredicate with propertyPath=" + propertyPath);
                }
            } else {
                fail("Invalid predicate encountered: " + pred);
            }
        }

        // check non-null predicate
        assertNotNull(nonNullStringPredicate);
        assertTrue(nonNullStringPredicate.getValue() instanceof CriteriaStringValue);
        assertEquals("abcdefg", nonNullStringPredicate.getValue().getValue());

        // check null predicate
        assertNotNull(nullPredicate);

        // check list predicates, we already checked everything we needed in the for loop above, let's just make sure it found them
        assertNotNull(listA);
        assertNotNull(listB);
        assertNotNull(listC);

        // check date predicate
        assertNotNull(datePredicate);
        assertTrue(datePredicate.getValue() instanceof CriteriaDateTimeValue);
        assertEquals(now, datePredicate.getValue().getValue());

        // check set predicate
        assertNotNull(setPredicate);
        // since set predicate only had a size of 1, it will be reduced down to a simple equal predicate instead of an OR
        assertEquals("set", setPredicate.getPropertyPath());
        assertTrue(setPredicate.getValue() instanceof CriteriaIntegerValue);
        assertEquals(BigInteger.valueOf(5), (BigInteger)setPredicate.getValue().getValue());
    }

    private void assertEmptyCriteria(QueryByCriteria criteria) {
        assertNotNull(criteria);
        assertTrue(criteria.getPredicate() instanceof CompositePredicate);
        assertTrue(((CompositePredicate)criteria.getPredicate()).getPredicates().isEmpty());
    }

    private String marshallToString(QueryByCriteria queryByCriteria) throws JAXBException {
        StringWriter stringWriter = new StringWriter();
        JAXBContext context = JAXBContext.newInstance(queryByCriteria.getClass());
        context.createMarshaller().marshal(queryByCriteria, stringWriter);

        String xml = stringWriter.toString();
        LOG.debug(xml);
        return xml;
    }

    private QueryByCriteria unMarshall(String xml) throws JAXBException {
        StringReader stringReader = new StringReader(xml);
        JAXBContext context = JAXBContext.newInstance(QueryByCriteria.class);
        return (QueryByCriteria)context.createUnmarshaller().unmarshal(stringReader);
    }

    private QueryByCriteria wrapInQueryByCriteria(Predicate predicate) {
        QueryByCriteria.Builder queryByCriteriaBuilder = QueryByCriteria.Builder.create();
        queryByCriteriaBuilder.setPredicates(predicate);
        return queryByCriteriaBuilder.build();
    }

    /**
     *
     * @param discoveredPredicateClasses
     * @return
     */
    private Predicate buildSampleCompositePredicate(ArrayList<Class<?>> discoveredPredicateClasses) {// add samples of all simple predicate types to our argument array
        Predicate[] predicateArray = new Predicate[discoveredPredicateClasses.size()];

        for (int i=0; i<predicateArray.length; i++) {
            Class<?> discoveredPredicateClass = discoveredPredicateClasses.get(i);
            Predicate p = predicateSamplesMap.get(discoveredPredicateClass);
            Assert.assertNotNull("no sample predicate for " + discoveredPredicateClass, p);
            predicateArray[i] = p;
        }

        return PredicateFactory.and(predicateArray);
    }

    /**
     * Search the classes in the PREDICATE_BASE_PACKAGE and build a list of all simple (non-composite) Predicate classes
     * @return a list of simple Predicate classes
     * @throws ClassNotFoundException
     */
    private ArrayList<Class<?>> discoverSimplePredicateClasses() throws ClassNotFoundException {
        ArrayList<Class<?>> discoveredPredicateClasses = new ArrayList<Class<?>>();

        // This technique was copped from:
        // http://stackoverflow.com/questions/520328/can-you-find-all-classes-in-a-package-using-reflection

        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(true);
        provider.addIncludeFilter(new AssignableTypeFilter(Predicate.class));

        // scan in org.example.package
        Set<BeanDefinition> components = provider.findCandidateComponents(PREDICATE_BASE_PACKAGE);
        for (BeanDefinition component : components)
        {
            Class cls = Class.forName(component.getBeanClassName());
            if (!cls.isMemberClass()                             // filter out inner class predicates from test packages
                && Predicate.class.isAssignableFrom(cls)         // filter out any non-predicate classes
                && !CompositePredicate.class.isAssignableFrom(cls)) // filter out 'and' and 'or' predicates
            {
                discoveredPredicateClasses.add(cls);
                // use class cls found
                LOG.debug("discovered " + cls.toString());
            }
        }
        return discoveredPredicateClasses;
    }

}
