package org.kuali.rice.krad.data.provider;

import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.core.api.criteria.Predicate;
import org.kuali.rice.core.api.criteria.PredicateFactory;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.criteria.QueryResults;
import org.kuali.rice.krad.data.CompoundKey;
import org.kuali.rice.krad.data.DataObjectWrapper;
import org.kuali.rice.krad.data.KradDataServiceLocator;
import org.kuali.rice.krad.data.PersistenceOption;
import org.kuali.rice.krad.test.KRADTestCase;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.*;

/**
 * Base test class for testing PersistenceProvider
 */
public abstract class PersistenceProviderTestBase<T extends Serializable> extends KRADTestCase {

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
     * Subclasses implement to return PersistenceProvider to test
     */
    protected abstract PersistenceProvider getPersistenceProvider();

    protected abstract T createTopLevelObject();
    /**
     * Add unlinked reference objects
     */
    protected abstract void addUnlinkedReferences(T a);

    /**
     * Add linked reference objects
     */
    protected abstract void addLinkedReferences(T a);

    /**
     * Returns an object representing the primary key for the test object
     * which can be used for direct lookup/find
     */
    protected abstract Object getIdForLookup(T object);

    /**
     * Returns the next test object primary key (without saving an instance)
     */
    protected abstract Object getNextTestObjectId();

    /**
     * Sets the primary key of the test object
     */
    protected abstract void setTestObjectPK(T a, Object key);


    protected abstract String[] getPropertiesForQuery();

    /**
     * Checks test object equality including primary key
     */
    protected abstract void assertTestObjectIdentityEquals(T expected, T actual);

    /**
     * Checks test object equality excluding primary key
     */
    protected abstract void assertTestObjectEquals(T expected, T actual);

    /**
     * Derives a QueryByCriteria for a test object
     */
    protected QueryByCriteria queryFor(T a) {
        return QueryByCriteria.Builder.forAttributes(a, Arrays.asList(getPropertiesForQuery()));
    }

    /**
     * Creates an unsaved test object
     */
    protected T createLinkedTestObject() {
        T a = createTopLevelObject();
        T saved = getPersistenceProvider().save(a);

        addLinkedReferences(saved);

        return saved;
    }

    /**
     * Creates an unsaved, unlinked test object
     */
    protected T createUnlinkedTestObject() {
        T a = createTopLevelObject();
        T saved = getPersistenceProvider().save(a);

        addUnlinkedReferences(saved);

        return saved;
    }

    /**
     * Assigns the next generated primary key value to the test object
     */
    protected void assignPK(T a) {
        setTestObjectPK(a, getNextTestObjectId());
    }

    /**
     * Creates a test object and generates a matching query for it
     */
    protected Map.Entry<T, QueryByCriteria> createForQuery() {
        T a = createLinkedTestObject();
        QueryByCriteria qbc = queryFor(a);
        return new AbstractMap.SimpleImmutableEntry<T, QueryByCriteria>(a, qbc);
    }

    /**
     * Generates a batch of test objects and returns a single query that will select them all.
     * The order of the returned list of test objects should match the order of the results returned
     * by the underlying platform (i.e., if they are returned in a sorted order, then the test object
     * list should be sorted).
     */
    protected Map.Entry<List<T>, QueryByCriteria.Builder> createForQuery(int count) {
        List<T> objects = new ArrayList<T>();
        List<Predicate> predicates = new ArrayList<Predicate>(count);
        for (int i = 0; i < count; i++) {
            T a = createLinkedTestObject();
            objects.add(a);
            predicates.add(queryFor(a).getPredicate());
        }
        QueryByCriteria.Builder qbc = QueryByCriteria.Builder.create();
        qbc.setPredicates(PredicateFactory.or(predicates.toArray(new Predicate[count])));
        return new AbstractMap.SimpleImmutableEntry<List<T>, QueryByCriteria.Builder>(objects, qbc);
    }

    @Test
    public void testSimpleSave() {
        T a = createTopLevelObject();
        assertNull(getIdForLookup(a));

        T saved = provider.save(a);
        assertNotNull(getIdForLookup(saved));
        assertTestObjectEquals(a, saved);
    }

    @Test
    public void testSaveLinkedSkipLinking() {
        T a = createLinkedTestObject();
        Object id = getIdForLookup(a);

        T saved = provider.save(a, PersistenceOption.SKIP_LINKING);
        assertTestObjectIdentityEquals(a, saved);

        T found = provider.find((Class<T>)a.getClass(), id);
        assertTestObjectIdentityEquals(a, found);
        assertTestObjectIdentityEquals(saved, found);
    }

    @Test
    public void testSaveUnlinkedSkipLinking() {
        T a = createUnlinkedTestObject();

        provider.save(a, PersistenceOption.SKIP_LINKING);

        fail("save should have resulted in an exception as references have not been linked correctly");
    }


    @Test
    public void testFindMatching() {
        Map.Entry<T, QueryByCriteria> pair = createForQuery();

        T a = pair.getKey();
        QueryByCriteria qbc = pair.getValue();

        T saved = provider.save(a);
        assertTestObjectEquals(a, saved);

        QueryResults<T> found = provider.findMatching((Class<T>)a.getClass(), qbc);
        assertEquals(1, found.getResults().size());
        assertTestObjectIdentityEquals(a, found.getResults().get(0));

        provider.delete(found.getResults().get(0));

        found = provider.findMatching((Class<T>)a.getClass(), qbc);
        assertEquals(0, found.getResults().size());
    }

    @Test
    public void testFindBySingleKey() {
        T a = createLinkedTestObject();

        T saved = provider.save(a);
        assertTestObjectEquals(a, saved);
        Object id = getIdForLookup(saved);

        T found = provider.find((Class<T>)a.getClass(), id);
        assertTestObjectIdentityEquals(a, found);

        provider.delete(found);

        assertNull(provider.find((Class<T>)a.getClass(), id));
    }

    @Test
    public void testFindByCompoundKey() {
        T a = createLinkedTestObject();

        T saved = provider.save(a);
        assertTestObjectEquals(a, saved);

        Map<String, Object> keys = new TreeMap<String, Object>();
        DataObjectWrapper<T> wrap = KradDataServiceLocator.getDataObjectService().wrap(saved);
        for (String propertyName : getPropertiesForQuery()) {
            keys.put(propertyName, wrap.getPropertyValue(propertyName));
        }
        CompoundKey id = new CompoundKey(keys);

        T found = provider.find((Class<T>)a.getClass(), id);
        assertTestObjectIdentityEquals(a, found);

        provider.delete(found);

        assertNull(provider.find((Class<T>)a.getClass(), id));
    }

    @Test
    public void testFindWithResultsWindow() {
        Map.Entry<List<T>, QueryByCriteria.Builder> fixture = createForQuery(10);
        List<T> objects = fixture.getKey();
        for (T a: objects) {
            provider.save(a);
        }

        QueryByCriteria.Builder query = fixture.getValue();
        query.setStartAtIndex(2);
        query.setMaxResults(5);
        QueryResults<T> results = provider.findMatching((Class<T>) objects.get(0).getClass(), query.build());

        assertEquals(5, results.getResults().size());
        assertTestObjectIdentityEquals(objects.get(2), results.getResults().get(0));
        assertTestObjectIdentityEquals(objects.get(3), results.getResults().get(1));
        assertTestObjectIdentityEquals(objects.get(4), results.getResults().get(2));
        assertTestObjectIdentityEquals(objects.get(5), results.getResults().get(3));
        assertTestObjectIdentityEquals(objects.get(6), results.getResults().get(4));
    }

    /**
     * Tests that deletion of a non-existent detached object does not result in a save of the object
     * via merge.
     */
    @Test
    public void testDeleteNonExistentEntity() {
        T a = createTopLevelObject();
        assignPK(a);
        Object id = getIdForLookup(a);

        assertNull(provider.find((Class<T>)a.getClass(), id));

        provider.delete(a);

        assertNull(provider.find((Class<T>)a.getClass(), id));
    }

    @Test
    public void testHandles() {
        T a = createTopLevelObject();
        assertTrue(provider.handles((Class<T>)a.getClass()));
        Class guaranteedNotToBeMappedClass = this.getClass();
      //  assertFalse(provider.handles(guaranteedNotToBeMappedClass));
    }
}
