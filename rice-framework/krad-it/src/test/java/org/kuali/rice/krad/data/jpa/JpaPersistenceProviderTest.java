package org.kuali.rice.krad.data.jpa;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.core.api.criteria.Predicate;
import org.kuali.rice.core.api.criteria.PredicateFactory;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.criteria.QueryResults;
import org.kuali.rice.krad.data.CompoundKey;
import org.kuali.rice.krad.data.DataObjectWrapper;
import org.kuali.rice.krad.data.KradDataServiceLocator;
import org.kuali.rice.krad.data.platform.MaxValueIncrementerFactory;
import org.kuali.rice.krad.data.provider.PersistenceProvider;
import org.kuali.rice.krad.test.KRADTestCase;
import org.kuali.rice.krad.test.document.bo.AccountType;
import org.kuali.rice.krad.test.document.bo.SimpleAccount;
import org.kuali.rice.krad.test.document.bo.SimpleAccountExtension;
import org.kuali.rice.test.BaselineTestCase;
import org.kuali.rice.test.TestHarnessServiceLocator;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.transaction.UnexpectedRollbackException;

import javax.sql.DataSource;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.*;

/**
 * Tests JPAPersistenceProvider
 */
// avoid wrapping test in rollback since JPA requires transaction boundary to flush
@BaselineTestCase.BaselineMode(BaselineTestCase.Mode.CLEAR_DB)
public class JpaPersistenceProviderTest extends KRADTestCase {

    /**
     * The PersistenceProvider being tested
     */
    protected PersistenceProvider provider;

    /**
     * Obtains PersistenceProvider.
     */
    @Before
    public void setup() {
        provider = getPersistenceProvider();
    }

    /**
     * Derives a QueryByCriteria for a test object
     */
    protected QueryByCriteria queryFor(Object a) {
        return QueryByCriteria.Builder.andAttributes(a, Arrays.asList(getPropertiesForQuery())).build();
    }

    /**
     * Creates an unsaved test object
     */
    protected Object createLinkedTestObject() {
        Object a = createTopLevelObject();
        Object saved = getPersistenceProvider().save(a);

        addLinkedReferences(saved);

        return saved;
    }

    /**
     * Creates an unsaved, unlinked test object
     */
    protected Object createUnlinkedTestObject() {
        Object a = createTopLevelObject();
        Object saved = getPersistenceProvider().save(a);

        addUnlinkedReferences(saved);

        return saved;
    }

    /**
     * Assigns the next generated primary key value to the test object
     */
    protected void assignPK(Object a) {
        setTestObjectPK(a, getNextTestObjectId());
    }

    /**
     * Creates a test object and generates a matching query for it
     */
    protected Map.Entry<Object, QueryByCriteria> createForQuery() {
        Object a = createLinkedTestObject();
        QueryByCriteria qbc = queryFor(a);
        return new AbstractMap.SimpleImmutableEntry<Object, QueryByCriteria>(a, qbc);
    }

    /**
     * Generates a batch of test objects and returns a single query that will select them all.
     * The order of the returned list of test objects should match the order of the results returned
     * by the underlying platform (i.e., if they are returned in a sorted order, then the test object
     * list should be sorted).
     */
    protected Map.Entry<List<Object>, QueryByCriteria.Builder> createForQuery(int count) {
        List<Object> objects = new ArrayList<Object>();
        List<Predicate> predicates = new ArrayList<Predicate>(count);
        for (int i = 0; i < count; i++) {
            Object a = createLinkedTestObject();
            objects.add(a);
            predicates.add(queryFor(a).getPredicate());
        }
        QueryByCriteria.Builder qbc = QueryByCriteria.Builder.create();
        qbc.setPredicates(PredicateFactory.or(predicates.toArray(new Predicate[count])));
        return new AbstractMap.SimpleImmutableEntry<List<Object>, QueryByCriteria.Builder>(objects, qbc);
    }

    @Test
    public void testSimpleSave() {
        Object a = createTopLevelObject();
        assertNull(getIdForLookup(a));

        Object saved = provider.save(a);
        assertNotNull(getIdForLookup(saved));
        assertTestObjectEquals(a, saved);
    }

    @Test
    public void testSaveLinkedSkipLinking() {
        Object a = createLinkedTestObject();
        Object id = getIdForLookup(a);

        Object saved = provider.save(a);
        assertTestObjectIdentityEquals(a, saved);

        Object found = provider.find((Class<Object>)a.getClass(), id);
        assertTestObjectIdentityEquals(a, found);
        assertTestObjectIdentityEquals(saved, found);
    }

    // EclipseLink consumes the underlying exception itself and explicitly rolls back the transaction
    // resulting in just an opaque UnexpectedRollbackException coming out of Spring
    // (underlying exception is never translated by the PersistenceExceptionTranslator)
    // Internal Exception: com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException: Column 'ACCT_TYPE' cannot be null
    @Test(expected=UnexpectedRollbackException.class)
    public void testSaveUnlinkedSkipLinking() {
        Object a = createUnlinkedTestObject();

        provider.save(a);

        fail("save should have resulted in an exception as references have not been linked correctly");
    }


    @Test
    public void testFindMatching() {
        Map.Entry<Object, QueryByCriteria> pair = createForQuery();

        Object a = pair.getKey();
        QueryByCriteria qbc = pair.getValue();

        Object saved = provider.save(a);
        assertTestObjectEquals(a, saved);

        QueryResults<Object> found = provider.findMatching((Class<Object>)a.getClass(), qbc);
        assertEquals(1, found.getResults().size());
        assertTestObjectIdentityEquals(a, found.getResults().get(0));

        provider.delete(found.getResults().get(0));

        found = provider.findMatching((Class<Object>)a.getClass(), qbc);
        assertEquals(0, found.getResults().size());
    }

    /**
     * Ensures an IllegalArgumentException is thrown when a null value is passed in as the second parameter.
     */
    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void testFindMatchingNullCriteria() {
        Map.Entry<Object, QueryByCriteria> pair = createForQuery();

        Object a = pair.getKey();

        provider.findMatching(a.getClass(), null);
    }

    /**
     * Ensures no errors or exceptions occur when the second parameter's predicate value is null
     */
    @Test
    public void testFindMatchingEmptyCriteria() {
        Map.Entry<Object, QueryByCriteria> pair = createForQuery();

        Object a = pair.getKey();

        provider.findMatching(a.getClass(), QueryByCriteria.Builder.create().build());
    }

    @Test
    public void testFindBySingleKey() {
        Object a = createLinkedTestObject();

        Object saved = provider.save(a);
        assertTestObjectEquals(a, saved);
        Object id = getIdForLookup(saved);

        Object found = provider.find((Class<Object>)a.getClass(), id);
        assertTestObjectIdentityEquals(a, found);

        provider.delete(found);

        assertNull(provider.find((Class<Object>)a.getClass(), id));
    }

    @Test
    public void testFindByCompoundKey() {
        Object a = createLinkedTestObject();

        Object saved = provider.save(a);
        assertTestObjectEquals(a, saved);

        Map<String, Object> keys = new TreeMap<String, Object>();
        DataObjectWrapper<Object> wrap = KradDataServiceLocator.getDataObjectService().wrap(saved);
        for (String propertyName : getPropertiesForQuery()) {
            keys.put(propertyName, wrap.getPropertyValue(propertyName));
        }
        CompoundKey id = new CompoundKey(keys);

        Object found = provider.find((Class<Object>)a.getClass(), id);
        assertTestObjectIdentityEquals(a, found);

        provider.delete(found);

        assertNull(provider.find((Class<Object>)a.getClass(), id));
    }

    @Test
    public void testFindWithResultsWindow() {
        Map.Entry<List<Object>, QueryByCriteria.Builder> fixture = createForQuery(10);
        List<Object> objects = fixture.getKey();
        for (Object a: objects) {
            provider.save(a);
        }

        QueryByCriteria.Builder query = fixture.getValue();
        query.setStartAtIndex(2);
        query.setMaxResults(5);
        QueryResults<Object> results = provider.findMatching((Class<Object>) objects.get(0).getClass(), query.build());

        assertEquals(5, results.getResults().size());
        assertTestObjectIdentityEquals(objects.get(2), results.getResults().get(0));
        assertTestObjectIdentityEquals(objects.get(3), results.getResults().get(1));
        assertTestObjectIdentityEquals(objects.get(4), results.getResults().get(2));
        assertTestObjectIdentityEquals(objects.get(5), results.getResults().get(3));
        assertTestObjectIdentityEquals(objects.get(6), results.getResults().get(4));
    }

    /**
     * Exercises the findAll method to ensure expected behavior
     */
    @Test
    public void testFindAll() {
        Object a = createTopLevelObject();
        QueryResults<Object> results =  provider.findAll((Class<Object>)a.getClass());
        assertEquals(0, results.getResults().size());

        Object savedA = provider.save(a);
        results = provider.findAll((Class<Object>)a.getClass());
        assertEquals(1, results.getResults().size());

        provider.delete(savedA);
        results = provider.findAll((Class<Object>)a.getClass());
        assertEquals(0, results.getResults().size());
    }

    /**
     * Tests that deletion of a non-existent detached object does not result in a save of the object
     * via merge.
     */
    @Test
    public void testDeleteNonExistentEntity() {
        Object a = createTopLevelObject();
        assignPK(a);
        Object id = getIdForLookup(a);

        assertNull(provider.find((Class<Object>)a.getClass(), id));

        provider.delete(a);

        assertNull(provider.find((Class<Object>)a.getClass(), id));
    }

    /**
     * Test delete matching with null criteria, should throw an exception
     */
    @Test(expected=InvalidDataAccessApiUsageException.class)
    public void testDeleteMatchingNullCriteria() {
        provider.deleteMatching(SimpleAccount.class, null);
    }

    /**
     * Test delete matching with empty criteria, should throw an exception
     */
    @Test(expected=InvalidDataAccessApiUsageException.class)
    public void testDeleteMatchingEmptyCriteria() {
        provider.deleteMatching(SimpleAccount.class, QueryByCriteria.Builder.create().build());
    }

    /**
     * Tests the deletion of non-existent detached objects.
     */
    @Test
    public void testDeleteMatchingNonExistentEntity() {
        List<String> nameList = new ArrayList<String>();

        // build three objects to test with
        Object a = createTopLevelObject();
        assignPK(a);
        nameList.add(((SimpleAccount)a).getName());
        Object b = createTopLevelObject();
        assignPK(b);
        nameList.add(((SimpleAccount) b).getName());
        Object c = createTopLevelObject();
        assignPK(c);
        nameList.add(((SimpleAccount)c).getName());

        // build the criteria for these three objects
        QueryByCriteria.Builder builder = QueryByCriteria.Builder.create();
        builder.setPredicates(PredicateFactory.in("name", nameList));

        QueryResults<Object> found = provider.findMatching((Class<Object>)a.getClass(), builder.build());
        assertEquals(0, found.getResults().size());

        provider.deleteMatching(a.getClass(), builder.build());

        found = (provider.findMatching((Class<Object>)a.getClass(), builder.build()));
        assertEquals(0, found.getResults().size());
    }

    /**
     * Tests the deletion of saved objects.
     */
    @Test
    public void testDeleteMatchingAllSavedEntities() {
        List<String> nameList = new ArrayList<String>();

        // build and save three objects to test with
        Object a = createTopLevelObject();
        assignPK(a);
        Object savedA = provider.save(a);
        nameList.add(((SimpleAccount)savedA).getName());

        Object b = createTopLevelObject();
        assignPK(b);
        Object savedB = provider.save(b);
        nameList.add(((SimpleAccount) savedB).getName());

        Object c = createTopLevelObject();
        assignPK(c);
        Object savedC = provider.save(c);

        // did all three objects get saved?
        QueryResults<Object> found = provider.findAll((Class<Object>) savedA.getClass());
        assertEquals(3, found.getResults().size());

        // now delete part of the saved objects
        QueryByCriteria.Builder builder = QueryByCriteria.Builder.create();
        builder.setPredicates(PredicateFactory.in("name", nameList));
        provider.deleteMatching(a.getClass(), builder.build());

        // were the two objects deleted
        found = provider.findAll((Class<Object>) savedA.getClass());
        assertEquals(1, found.getResults().size());
        Object lastObject = found.getResults().get(0);
        assertEquals(((SimpleAccount) lastObject).getName(), ((SimpleAccount)savedC).getName());

        // clear the list and add the last object
        nameList.clear();
        nameList.add(((SimpleAccount)savedC).getName());

        // now delete the last object.
        builder = QueryByCriteria.Builder.create();
        builder.setPredicates(PredicateFactory.in("name", nameList));
        provider.deleteMatching(a.getClass(), builder.build());

        // were all objects deleted?
        found = provider.findAll((Class<Object>) savedA.getClass());
        assertEquals(0, found.getResults().size());
    }

    /**
     * Exercises the deleteAll method to ensure expected behavior
     */
    @Test
    public void testDeleteAll() {
        Object a = createTopLevelObject();
        QueryResults<Object> results = provider.findAll((Class<Object>)a.getClass());
        assertEquals(0, results.getResults().size());
        provider.deleteAll(a.getClass());
        results = provider.findAll((Class<Object>)a.getClass());
        assertEquals(0, results.getResults().size());

        Object savedA = provider.save(a);
        Object b = createTopLevelObject();
        provider.save(b);
        results = provider.findAll((Class<Object>) a.getClass());
        assertEquals(2, results.getResults().size());

        provider.deleteAll(savedA.getClass());
        results = provider.findAll((Class<Object>)a.getClass());
        assertEquals(0, results.getResults().size());
    }

    @Test
    public void testHandles() {
        Object a = createTopLevelObject();
        assertTrue(provider.handles((Class<Object>)a.getClass()));
        Class guaranteedNotToBeMappedClass = this.getClass();
        //  assertFalse(provider.handles(guaranteedNotToBeMappedClass));
    }

    protected Object createTopLevelObject() {
        SimpleAccount a = new SimpleAccount();
        String name = RandomStringUtils.randomAlphanumeric(10);
        a.setName(name);
        return a;
    }

    protected void addLinkedReferences(Object o) {
        SimpleAccount a = (SimpleAccount)o;
        addUnlinkedReferences(a);
        //a.getAccountManager().setAmId(Long.parseLong(a.getNumber()));
        SimpleAccountExtension e = (SimpleAccountExtension) a.getExtension();
        e.setAccountTypeCode(e.getAccountType().getAccountTypeCode());
        e.setAccount(a);
    }

    protected void addUnlinkedReferences(Object o) {
        SimpleAccount a = (SimpleAccount)o;
        //AccountManager am = new AccountManager();
        //am.setUserName(RandomStringUtils.randomAlphanumeric(10));
        //a.setAccountManager(am);
        SimpleAccountExtension extension = new SimpleAccountExtension();
        AccountType at = new AccountType();
        at.setName(RandomStringUtils.randomAlphanumeric(10));
        at.setAccountTypeCode(RandomStringUtils.randomAlphanumeric(2));
        extension.setAccountType(at);
        a.setExtension(extension);
    }

    protected String[] getPropertiesForQuery() {
        return new String[] { "number", "name" };
    }

    protected Object getIdForLookup(Object o) {
        SimpleAccount a = (SimpleAccount)o;
        return a.getNumber();
    }

    protected String getNextTestObjectId() {
        DataSource dataSource = TestHarnessServiceLocator.getDataSource();
        return MaxValueIncrementerFactory.getIncrementer(dataSource, "trvl_id_seq").nextStringValue();
    }

    protected void setTestObjectPK(Object o, Object key) {
        SimpleAccount a = (SimpleAccount)o;
        a.setNumber((String) key);
    }

    protected void assertTestObjectIdentityEquals(Object oExpected, Object oActual) {
        SimpleAccount expected = (SimpleAccount)oExpected;
        SimpleAccount actual = (SimpleAccount)oActual;
        assertTestObjectEquals(expected, actual);
        assertEquals(expected.getNumber(), actual.getNumber());
    }

    protected void assertTestObjectEquals(Object oExpected, Object oActual) {
        SimpleAccount expected = (SimpleAccount)oExpected;
        SimpleAccount actual = (SimpleAccount)oActual;
        assertEquals(expected.getAmId(), actual.getAmId());
        assertEquals(expected.getName(), actual.getName());
        if (expected.getExtension() != null) {
            SimpleAccountExtension e1 = (SimpleAccountExtension) expected.getExtension();
            SimpleAccountExtension e2 = (SimpleAccountExtension) actual.getExtension();
            assertEquals(e1.getAccount().getNumber(), e2.getAccount().getNumber());
            assertEquals(e1.getAccountTypeCode(), e2.getAccountTypeCode());

            if (e1.getAccountType() != null) {
                AccountType at1 = e1.getAccountType();
                AccountType at2 = e2.getAccountType();
                assertEquals(at1.getName(), at2.getName());
                assertEquals(at1.getAccountTypeCode(), at2.getAccountTypeCode());
            }
        }

    }

    protected PersistenceProvider getPersistenceProvider() {
        return getKRADTestHarnessContext().getBean("kradTestJpaPersistenceProvider", PersistenceProvider.class);
    }

}
