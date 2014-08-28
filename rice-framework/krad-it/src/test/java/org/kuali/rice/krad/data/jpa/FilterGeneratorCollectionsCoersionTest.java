/*
 * Copyright 2006-2014 The Kuali Foundation
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

package org.kuali.rice.krad.data.jpa;

import org.junit.Test;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.test.KRADTestCase;
import org.kuali.rice.test.BaselineTestCase;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

// no rollback so we can avoid transactional cache mucking up our results
@BaselineTestCase.BaselineMode(BaselineTestCase.Mode.CLEAR_DB)
public class FilterGeneratorCollectionsCoersionTest extends KRADTestCase {

    /**
     * Method ensures two items are returned on the child list when our filter generator is filtering by true values.
     * @throws Exception if errors while testing
     */
    @Test
    public void testBooleanCollectionMatch() throws Exception {
        BooleanParent parent = new BooleanParent();
        
        // save the parent so we get an id
        parent = getDataObjectService().save(parent);

        // create some child objects and persist them to the data store
        parent.setChildren(getBoolChildrenMatchList(parent));
        parent = getDataObjectService().save(parent);

        // retrieve the parent object form the data store
        BooleanParent persistedParent =
                getDataObjectService().find(BooleanParent.class, parent.getId());


        // validate the filtering works
        assertTrue("No parent class found.", persistedParent != null);
        assertEquals("There should be 2 child items in the collection.", 2, persistedParent.getChildren().size());
        assertEquals("First child item value is incorrect", Boolean.TRUE,
                persistedParent.getChildren().get(0).getBoolProp());
        assertEquals("Second child item value is incorrect", Boolean.TRUE,
                persistedParent.getChildren().get(1).getBoolProp());

        // lets clean up after ourselves
        getDataObjectService().delete(parent);
        getDataObjectService().flush(BooleanParent.class);
    }

    /**
     * Method ensures no items are returned on the child list when our filter generator is filtering by true values.
     * @throws Exception if errors while testing
     */
    @Test
    public void testBooleanCollectionNoMatch() throws Exception {
        BooleanParent parent = new BooleanParent();

        // save the parent so we get an id
        parent = getDataObjectService().save(parent);

        // create some child objects and persist them to the data store
        parent.setChildren(getBoolChildrenNoMatchList(parent));
        parent = getDataObjectService().save(parent);

        // retrieve the parent object from the data store
        BooleanParent persistedParent =
                getDataObjectService().find(BooleanParent.class, parent.getId());

        // validate the filtering works
        assertNotNull("Parent class should not be null.", persistedParent);
        assertTrue("Collection of child items should be empty.", persistedParent.getChildren().size() < 1);

        // lets clean up after ourselves
        getDataObjectService().delete(parent);
        getDataObjectService().flush(BooleanParent.class);
    }

    @Test
    public void testIntegerCollectionMatch() {
        IntegerParent parent = new IntegerParent();

        // save the parent so we get an id
        parent = getDataObjectService().save(parent);

        // create some child objects and persist them to the data store
        parent.setChildren(getIntChildMatchList(parent));
        parent = getDataObjectService().save(parent);

        // retrieve the parent object from the data store
        IntegerParent persistedParent =
                getDataObjectService().find(IntegerParent.class, parent.getId());

        // validate the filtering works
        assertTrue("The parent class should not be null.", persistedParent != null);
        assertEquals("There should be two child items in the collection.", 2, persistedParent.getChildren().size());
        assertEquals("First child item value is incorrect", 35, persistedParent.getChildren().get(0).getIntegerProp());
        assertEquals("Second child item value is incorrect", 35, persistedParent.getChildren().get(1).getIntegerProp());

        // lets clean up after ourselves
        getDataObjectService().delete(parent);
        getDataObjectService().flush(IntegerParent.class);
    }

    @Test
    public void testIntegerCollectionNoMatch() {
        IntegerParent parent = new IntegerParent();

        // save the parent so we get an id
        parent = getDataObjectService().save(parent);

        // create some child objects and persist them to the data store
        parent.setChildren(getIntChildNoMatchList(parent));
        parent = getDataObjectService().save(parent);

        // retrieve the parent object from the data store
        IntegerParent persistedParent =
                getDataObjectService().find(IntegerParent.class, parent.getId());

        // validate the filtering works
        assertNotNull("Parent class should not be null.", persistedParent);
        assertTrue("Collection of child items should be empty.", persistedParent.getChildren().size() < 1);

        // lets clean up after ourselves
        getDataObjectService().delete(parent);
        getDataObjectService().flush(IntegerParent.class);
    }

    @Test
    public void testCharCollectionMatch() {
        CharParent parent = new CharParent();

        // save the parent so we get an id
        parent = getDataObjectService().save(parent);

        // create some child objects and persist them to the data store
        parent.setChildren(getCharChildMatchList(parent));
        parent = getDataObjectService().save(parent);

        // retrieve the parent object from the data store
        CharParent persistedParent =
                getDataObjectService().find(CharParent.class, parent.getId());

        // validate the filtering works
        assertTrue("The parent class should not be null.", persistedParent != null);
        assertEquals("There should be two child items in the collection.", 2, persistedParent.getChildren().size());
        assertEquals("First child item value is incorrect", 'k', persistedParent.getChildren().get(0).getCharProp());
        assertEquals("Second child item value is incorrect", 'k', persistedParent.getChildren().get(1).getCharProp());

        // lets clean up after ourselves
        getDataObjectService().delete(parent);
        getDataObjectService().flush(IntegerParent.class);
    }

    @Test
    public void testCharCollectionNoMatch() {
        CharParent parent = new CharParent();

        // save the parent so we get an id
        parent = getDataObjectService().save(parent);

        // create some child objects and persist them to the data store
        parent.setChildren(getCharChildNoMatchList(parent));
        parent = getDataObjectService().save(parent);

        // retrieve the parent object from the data store
        CharParent persistedParent =
                getDataObjectService().find(CharParent.class, parent.getId());

        // validate the filtering works
        assertNotNull("Parent class should not be null.", persistedParent);
        assertTrue("Collection of child items should be empty.", persistedParent.getChildren().size() < 1);

        // lets clean up after ourselves
        getDataObjectService().delete(parent);
        getDataObjectService().flush(CharParent.class);
    }

    @Test
    public void testShortCollectionMatch() {
        ShortParent parent = new ShortParent();

        // save the parent so we get an id
        parent = getDataObjectService().save(parent);

        // create some child objects and persist them to the data store
        parent.setChildren(getShortChildMatchList(parent));
        parent = getDataObjectService().save(parent);

        // retrieve the parent object from the data store
        ShortParent persistedParent =
                getDataObjectService().find(ShortParent.class, parent.getId());

        // validate the filtering works
        assertTrue("The parent class should not be null.", persistedParent != null);
        assertEquals("There should be two child items in the collection.", 2, persistedParent.getChildren().size());
        assertEquals("First child item value is incorrect", (short) 255, persistedParent.getChildren().get(0).getShortProp());
        assertEquals("Second child item value is incorrect", (short) 255, persistedParent.getChildren().get(1).getShortProp());

        // lets clean up after ourselves
        getDataObjectService().delete(parent);
        getDataObjectService().flush(ShortParent.class);
    }

    @Test
    public void testShortCollectionNoMatch() {
        ShortParent parent = new ShortParent();

        // save the parent so we get an id
        parent = getDataObjectService().save(parent);

        // create some child objects and persist them to the data store
        parent.setChildren(getShortChildNoMatchList(parent));
        parent = getDataObjectService().save(parent);

        // retrieve the parent object from the data store
        ShortParent persistedParent =
                getDataObjectService().find(ShortParent.class, parent.getId());

        // validate the filtering works
        assertNotNull("Parent class should not be null.", persistedParent);
        assertTrue("Collection of child items should be empty.", persistedParent.getChildren().size() < 1);

        // lets clean up after ourselves
        getDataObjectService().delete(parent);
        getDataObjectService().flush(ShortParent.class);
    }

    @Test
    public void testLongCollectionMatch() {
        LongParent parent = new LongParent();

        // save the parent so we get an id
        parent = getDataObjectService().save(parent);

        // create some child objects and persist them to the data store
        parent.setChildren(getLongChildMatchList(parent));
        parent = getDataObjectService().save(parent);

        // retrieve the parent object from the data store
        LongParent persistedParent =
                getDataObjectService().find(LongParent.class, parent.getId());

        // validate the filtering works
        assertTrue("The parent class should not be null.", persistedParent != null);
        assertEquals("There should be two child items in the collection.", 2, persistedParent.getChildren().size());
        assertEquals("First child item value is incorrect", 10L, persistedParent.getChildren().get(0).getLongProp());
        assertEquals("Second child item value is incorrect", 10L, persistedParent.getChildren().get(1).getLongProp());

        // lets clean up after ourselves
        getDataObjectService().delete(parent);
        getDataObjectService().flush(LongParent.class);
    }

    @Test
    public void testLongCollectionNoMatch() {
        LongParent parent = new LongParent();

        // save the parent so we get an id
        parent = getDataObjectService().save(parent);

        // create some child objects and persist them to the data store
        parent.setChildren(getLongChildNoMatchList(parent));
        parent = getDataObjectService().save(parent);

        // retrieve the parent object from the data store
        LongParent persistedParent =
                getDataObjectService().find(LongParent.class, parent.getId());

        // validate the filtering works
        assertNotNull("Parent class should not be null.", persistedParent);
        assertTrue("Collection of child items should be empty.", persistedParent.getChildren().size() < 1);

        // lets clean up after ourselves
        getDataObjectService().delete(parent);
        getDataObjectService().flush(LongParent.class);
    }

    @Test
    public void testFloatCollectionMatch() {
        FloatParent parent = new FloatParent();

        // save the parent so we get an id
        parent = getDataObjectService().save(parent);

        // create some child objects and persist them to the data store
        parent.setChildren(getFloatChildMatchList(parent));
        parent = getDataObjectService().save(parent);

        // retrieve the parent object from the data store
        FloatParent persistedParent =
                getDataObjectService().find(FloatParent.class, parent.getId());

        // validate the filtering works
        assertTrue("The parent class should not be null.", persistedParent != null);
        assertEquals("There should be two child items in the collection.", 2, persistedParent.getChildren().size());
        assertEquals("First child item value is incorrect", (float) 3.14, persistedParent.getChildren().get(0).getFloatProp(), 0.00);
        assertEquals("Second child item value is incorrect", (float) 3.14, persistedParent.getChildren().get(1).getFloatProp(), 0.00);

        // lets clean up after ourselves
        getDataObjectService().delete(parent);
        getDataObjectService().flush(FloatParent.class);
    }

    @Test
    public void testFloatCollectionNoMatch() {
        FloatParent parent = new FloatParent();

        // save the parent so we get an id
        parent = getDataObjectService().save(parent);

        // create some child objects and persist them to the data store
        parent.setChildren(getFloatChildNoMatchList(parent));
        parent = getDataObjectService().save(parent);

        // retrieve the parent object from the data store
        FloatParent persistedParent =
                getDataObjectService().find(FloatParent.class, parent.getId());

        // validate the filtering works
        assertNotNull("Parent class should not be null.", persistedParent);
        assertTrue("Collection of child items should be empty.", persistedParent.getChildren().size() < 1);

        // lets clean up after ourselves
        getDataObjectService().delete(parent);
        getDataObjectService().flush(FloatParent.class);
    }

    @Test
    public void testDoubleCollectionMatch() {
        DoubleParent parent = new DoubleParent();

        // save the parent so we get an id
        parent = getDataObjectService().save(parent);

        // create some child objects and persist them to the data store
        parent.setChildren(getDoubleChildMatchList(parent));
        parent = getDataObjectService().save(parent);

        // retrieve the parent object from the data store
        DoubleParent persistedParent =
                getDataObjectService().find(DoubleParent.class, parent.getId());

        // validate the filtering works
        assertTrue("The parent class should not be null.", persistedParent != null);
        assertEquals("There should be two child items in the collection.", 2, persistedParent.getChildren().size());
        assertEquals("First child item value is incorrect",  6.28, persistedParent.getChildren().get(0).getDoubleProp(), 0.00);
        assertEquals("Second child item value is incorrect", 6.28, persistedParent.getChildren().get(1).getDoubleProp(), 0.00);

        // lets clean up after ourselves
        getDataObjectService().delete(parent);
        getDataObjectService().flush(DoubleParent.class);
    }

    @Test
    public void testDoubleCollectionNoMatch() {
        DoubleParent parent = new DoubleParent();

        // save the parent so we get an id
        parent = getDataObjectService().save(parent);

        // create some child objects and persist them to the data store
        parent.setChildren(getDoubleChildNoMatchList(parent));
        parent = getDataObjectService().save(parent);

        // retrieve the parent object from the data store
        DoubleParent persistedParent =
                getDataObjectService().find(DoubleParent.class, parent.getId());

        // validate the filtering works
        assertNotNull("Parent class should not be null.", persistedParent);
        assertTrue("Collection of child items should be empty.", persistedParent.getChildren().size() < 1);

        // lets clean up after ourselves
        getDataObjectService().delete(parent);
        getDataObjectService().flush(DoubleParent.class);
    }

    /**
     * Creates a list of four BooleanChildren all items have the boolProp value set to false.  The parent class is
     *  passed in to set the parentKey value.
     * @param parent the parent class
     * @return a collection of {@link org.kuali.rice.krad.data.jpa.FilterGeneratorCollectionsCoersionTest.BooleanChild}
     */
    private List<BooleanChild> getBoolChildrenNoMatchList(BooleanParent parent) {
        List<BooleanChild> theChildren = new ArrayList<BooleanChild>();

        for(int c = 0; c<4;c++) { theChildren.add(createBoolChild(parent.getId(), Boolean.FALSE)); }

        return theChildren;
    }

    /**
     * Creates a list of four BooleanChildren two items with false boolProp value and two items with true boolProp value
     *  the parent class is passed in to set the parentKey value.
     * @param parent the parent class.
     * @return a collection of {@link org.kuali.rice.krad.data.jpa.FilterGeneratorCollectionsCoersionTest.BooleanChild}
     */
    private List<BooleanChild> getBoolChildrenMatchList(BooleanParent parent) {
        List<BooleanChild> theChildren = new ArrayList<BooleanChild>();

        theChildren.add(createBoolChild(parent.getId(), Boolean.FALSE));
        theChildren.add(createBoolChild(parent.getId(), Boolean.TRUE));
        theChildren.add(createBoolChild(parent.getId(), Boolean.FALSE));
        theChildren.add(createBoolChild(parent.getId(), Boolean.TRUE));

        return theChildren;
    }

    private List<IntegerChild> getIntChildMatchList(IntegerParent parent) {
        List<IntegerChild> theChildren = new ArrayList<IntegerChild>();

        theChildren.add(createIntChild(parent.getId(), 1234));
        theChildren.add(createIntChild(parent.getId(), 35));
        theChildren.add(createIntChild(parent.getId(), -6789));
        theChildren.add(createIntChild(parent.getId(), 35));

        return theChildren;
    }

    private List<IntegerChild> getIntChildNoMatchList(IntegerParent parent) {
        List<IntegerChild> theChildren = new ArrayList<IntegerChild>();

        theChildren.add(createIntChild(parent.getId(), 1234));
        theChildren.add(createIntChild(parent.getId(), 6789));
        theChildren.add(createIntChild(parent.getId(), -1234));
        theChildren.add(createIntChild(parent.getId(), -6789));

        return theChildren;
    }

    private List<CharChild> getCharChildMatchList(CharParent parent) {
        List<CharChild> theChildren = new ArrayList<CharChild>();

        theChildren.add(createCharChild(parent.getId(), 'k'));
        theChildren.add(createCharChild(parent.getId(), 'b'));
        theChildren.add(createCharChild(parent.getId(), 'k'));
        theChildren.add(createCharChild(parent.getId(), 'c'));

        return theChildren;
    }

    private List<CharChild> getCharChildNoMatchList(CharParent parent) {
        List<CharChild> theChildren = new ArrayList<CharChild>();

        theChildren.add(createCharChild(parent.getId(), 'a'));
        theChildren.add(createCharChild(parent.getId(), 'b'));
        theChildren.add(createCharChild(parent.getId(), 'c'));
        theChildren.add(createCharChild(parent.getId(), 'd'));

        return theChildren;
    }

    private List<ShortChild> getShortChildMatchList(ShortParent parent) {
        List<ShortChild> theChildren = new ArrayList<ShortChild>();

        theChildren.add(createShortChild(parent.getId(), (short) 1024));
        theChildren.add(createShortChild(parent.getId(), (short) 255));
        theChildren.add(createShortChild(parent.getId(), (short) -512));
        theChildren.add(createShortChild(parent.getId(), (short) 255));

        return theChildren;
    }

    private List<ShortChild> getShortChildNoMatchList(ShortParent parent) {
        List<ShortChild> theChildren = new ArrayList<ShortChild>();

        theChildren.add(createShortChild(parent.getId(), (short) 1024));
        theChildren.add(createShortChild(parent.getId(), (short) 2056));
        theChildren.add(createShortChild(parent.getId(), (short) 128));
        theChildren.add(createShortChild(parent.getId(), (short) -512));

        return theChildren;
    }

    private List<LongChild> getLongChildMatchList(LongParent parent) {
        List<LongChild> theChildren = new ArrayList<LongChild>();

        theChildren.add(createLongChild(parent.getId(), 10L));
        theChildren.add(createLongChild(parent.getId(), 20L));
        theChildren.add(createLongChild(parent.getId(), 30L));
        theChildren.add(createLongChild(parent.getId(), 10L));
        return theChildren;
    }

    private List<LongChild> getLongChildNoMatchList(LongParent parent) {
        List<LongChild> theChildren = new ArrayList<LongChild>();

        theChildren.add(createLongChild(parent.getId(), 20L));
        theChildren.add(createLongChild(parent.getId(), 30L));
        theChildren.add(createLongChild(parent.getId(), 40L));
        theChildren.add(createLongChild(parent.getId(), 50L));
        return theChildren;
    }

    private List<FloatChild> getFloatChildMatchList(FloatParent parent) {
        List<FloatChild> theChildren = new ArrayList<FloatChild>();

        theChildren.add(createFloatChild(parent.getId(), (float) 3.14));
        theChildren.add(createFloatChild(parent.getId(), (float) 6.28));
        theChildren.add(createFloatChild(parent.getId(), (float) 1.732));
        theChildren.add(createFloatChild(parent.getId(), (float) 3.14));
        return theChildren;
    }

    private List<FloatChild> getFloatChildNoMatchList(FloatParent parent) {
        List<FloatChild> theChildren = new ArrayList<FloatChild>();

        theChildren.add(createFloatChild(parent.getId(), (float) 6.28));
        theChildren.add(createFloatChild(parent.getId(), (float) 1.732));
        theChildren.add(createFloatChild(parent.getId(), (float) 1.618));
        theChildren.add(createFloatChild(parent.getId(), (float) 42.0001));

        return theChildren;
    }

    private List<DoubleChild> getDoubleChildMatchList(DoubleParent parent) {
        List<DoubleChild> theChildren = new ArrayList<DoubleChild>();

        theChildren.add(createDoubleChild(parent.getId(), 3.14));
        theChildren.add(createDoubleChild(parent.getId(), 6.28));
        theChildren.add(createDoubleChild(parent.getId(), 1.732));
        theChildren.add(createDoubleChild(parent.getId(), 6.28));

        return theChildren;
    }

    private List<DoubleChild> getDoubleChildNoMatchList(DoubleParent parent) {
        List<DoubleChild> theChildren = new ArrayList<DoubleChild>();

        theChildren.add(createDoubleChild(parent.getId(), 1.732));
        theChildren.add(createDoubleChild(parent.getId(), 3.14));
        theChildren.add(createDoubleChild(parent.getId(), 1.732));
        theChildren.add(createDoubleChild(parent.getId(), 42.0001));

        return theChildren;
    }

    /**
     *  Gets a new instance of a BooleanChild class with the boolProp set to the given value.
     * @param parentId the unique identifier for the parent class
     * @return a {@link BooleanChild} with the value of boolProp set to true;
     */
    private BooleanChild createBoolChild(String parentId, boolean boolVal) {
        BooleanChild child = new BooleanChild();
        child.setBoolProp(boolVal);
        child.setParentKey(parentId);

        return child;
    }

    private IntegerChild createIntChild(String parentId, int intVal) {
        IntegerChild child = new IntegerChild();
        child.setParentKey(parentId);
        child.setIntegerProp(intVal);

        return child;
    }

    private CharChild createCharChild(String parentId, char charVal) {
        CharChild child = new CharChild();
        child.setParentKey(parentId);
        child.setCharProp(charVal);

        return child;
    }

    private ShortChild createShortChild(String parentId, short shortVal) {
        ShortChild child = new ShortChild();
        child.setParentKey(parentId);
        child.setShortProp(shortVal);

        return child;
    }

    private LongChild createLongChild(String parentId, long longVal) {
        LongChild child = new LongChild();
        child.setParentKey(parentId);
        child.setLongProp(longVal);

        return child;
    }

    private FloatChild createFloatChild(String parentId, float floatVal) {
        FloatChild child = new FloatChild();
        child.setParentKey(parentId);
        child.setFloatProp(floatVal);

        return child;
    }

    private DoubleChild createDoubleChild(String parentId, double doubleVal) {
        DoubleChild child = new DoubleChild();
        child.setParentKey(parentId);
        child.setDoubleProp(doubleVal);

        return child;
    }

    /**
     * The parent boolean entity
     */
    @Entity
    @Table(name = "KRTST_COLL_PRNT_T")
    private static class BooleanParent {
        @Id
        @Column(name = "PK_PROP")
        @GeneratedValue(generator = "KRTST_GENERATED_PK_S")
        @PortableSequenceGenerator(name="KRTST_GENERATED_PK_S")
        private String id;

        @OneToMany(orphanRemoval = true, cascade = { CascadeType.ALL })
        @JoinColumn(name = "PRNT_KEY", referencedColumnName = "PK_PROP")
        @FilterGenerator(attributeName = "boolProp", attributeValue = "true")
        private List<BooleanChild> children;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public List<BooleanChild> getChildren() {
            return children;
        }

        public void setChildren(List<BooleanChild> children) {
            this.children = children;
        }
    }

    /**
     * The child boolean entity
     */
    @Entity
    @Table(name = "KRTST_COLL_CHLD_T")
    private static class BooleanChild {

        @Id
        @Column(name = "PK_PROP")
        @GeneratedValue(generator = "KRTST_GENERATED_PK_S")
        @PortableSequenceGenerator(name="KRTST_GENERATED_PK_S")
        private String id;

        @Column(name = "PRNT_KEY")
        private String parentKey;

        @Column(name = "BOOL_PROP")
        @Convert(converter = BooleanYNConverter.class)
        private boolean boolProp;

        @ManyToOne
        @JoinColumn(name = "PK_PROP" ,insertable=false, updatable=false)
        BooleanParent parent;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getParentKey() {
            return parentKey;
        }

        public void setParentKey(String parentKey) {
            this.parentKey = parentKey;
        }

        public boolean getBoolProp() {
            return boolProp;
        }

        public void setBoolProp(boolean boolProp) {
            this.boolProp = boolProp;
        }

        public BooleanParent getParent() {
            return parent;
        }

        public void setParent(BooleanParent parent) {
            this.parent = parent;
        }
    }

    /**
     * The parent integer entity
     */
    @Entity
    @Table(name = "KRTST_COLL_PRNT_T")
    private static class IntegerParent {
        @Id
        @Column(name = "PK_PROP")
        @GeneratedValue(generator = "KRTST_GENERATED_PK_S")
        @PortableSequenceGenerator(name="KRTST_GENERATED_PK_S")
        private String id;

        @OneToMany(orphanRemoval = true, cascade = { CascadeType.ALL })
        @JoinColumn(name = "PRNT_KEY", referencedColumnName = "PK_PROP")
        @FilterGenerator(attributeName = "integerProp", attributeValue = "35")
        private List<IntegerChild> children;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public List<IntegerChild> getChildren() {
            return children;
        }

        public void setChildren(List<IntegerChild> children) {
            this.children = children;
        }
    }

    /**
     * The child integer entity
     */
    @Entity
    @Table(name = "KRTST_COLL_CHLD_T")
    private static class IntegerChild {

        @Id
        @Column(name = "PK_PROP")
        @GeneratedValue(generator = "KRTST_GENERATED_PK_S")
        @PortableSequenceGenerator(name="KRTST_GENERATED_PK_S")
        private String id;

        @Column(name = "PRNT_KEY")
        private String parentKey;

        @Column(name = "INT_PROP")
        private int integerProp;

        @ManyToOne
        @JoinColumn(name = "PK_PROP" ,insertable=false, updatable=false)
        IntegerParent parent;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getParentKey() {
            return parentKey;
        }

        public void setParentKey(String parentKey) {
            this.parentKey = parentKey;
        }

        public int getIntegerProp() {
            return integerProp;
        }

        public void setIntegerProp(int integerProp) {
            this.integerProp = integerProp;
        }

        public IntegerParent getParent() {
            return parent;
        }

        public void setParent(IntegerParent parent) {
            this.parent = parent;
        }
    }

    /**
     * The parent char entity
     */
    @Entity
    @Table(name = "KRTST_COLL_PRNT_T")
    private static class CharParent {
        @Id
        @Column(name = "PK_PROP")
        @GeneratedValue(generator = "KRTST_GENERATED_PK_S")
        @PortableSequenceGenerator(name="KRTST_GENERATED_PK_S")
        private String id;

        @OneToMany(orphanRemoval = true, cascade = { CascadeType.ALL })
        @JoinColumn(name = "PRNT_KEY", referencedColumnName = "PK_PROP")
        @FilterGenerator(attributeName = "charProp", attributeValue = "k")
        private List<CharChild> children;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public List<CharChild> getChildren() {
            return children;
        }

        public void setChildren(List<CharChild> children) {
            this.children = children;
        }
    }

    /**
     * The child char entity
     */
    @Entity
    @Table(name = "KRTST_COLL_CHLD_T")
    private static class CharChild {

        @Id
        @Column(name = "PK_PROP")
        @GeneratedValue(generator = "KRTST_GENERATED_PK_S")
        @PortableSequenceGenerator(name="KRTST_GENERATED_PK_S")
        private String id;

        @Column(name = "PRNT_KEY")
        private String parentKey;

        @Column(name = "CHAR_PROP")
        private char charProp;

        @ManyToOne
        @JoinColumn(name = "PK_PROP" ,insertable=false, updatable=false)
        CharParent parent;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getParentKey() {
            return parentKey;
        }

        public void setParentKey(String parentKey) {
            this.parentKey = parentKey;
        }

        public char getCharProp() {
            return charProp;
        }

        public void setCharProp(char charProp) {
            this.charProp = charProp;
        }

        public CharParent getParent() {
            return parent;
        }

        public void setParent(CharParent parent) {
            this.parent = parent;
        }
    }

    /**
     * The parent long entity
     */
    @Entity
    @Table(name = "KRTST_COLL_PRNT_T")
    private static class LongParent {
        @Id
        @Column(name = "PK_PROP")
        @GeneratedValue(generator = "KRTST_GENERATED_PK_S")
        @PortableSequenceGenerator(name="KRTST_GENERATED_PK_S")
        private String id;

        @OneToMany(orphanRemoval = true, cascade = { CascadeType.ALL })
        @JoinColumn(name = "PRNT_KEY", referencedColumnName = "PK_PROP")
        @FilterGenerator(attributeName = "longProp", attributeValue = "10")
        private List<LongChild> children;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public List<LongChild> getChildren() {
            return children;
        }

        public void setChildren(List<LongChild> children) {
            this.children = children;
        }
    }

    /**
     * The child long entity
     */
    @Entity
    @Table(name = "KRTST_COLL_CHLD_T")
    private static class LongChild {

        @Id
        @Column(name = "PK_PROP")
        @GeneratedValue(generator = "KRTST_GENERATED_PK_S")
        @PortableSequenceGenerator(name="KRTST_GENERATED_PK_S")
        private String id;

        @Column(name = "PRNT_KEY")
        private String parentKey;

        @Column(name = "LONG_PROP")
        private long longProp;

        @ManyToOne
        @JoinColumn(name = "PK_PROP" ,insertable=false, updatable=false)
        LongParent parent;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getParentKey() {
            return parentKey;
        }

        public void setParentKey(String parentKey) {
            this.parentKey = parentKey;
        }

        public long getLongProp() {
            return longProp;
        }

        public void setLongProp(long longProp) {
            this.longProp = longProp;
        }

        public LongParent getParent() {
            return parent;
        }

        public void setParent(LongParent parent) {
            this.parent = parent;
        }
    }

    /**
     * The parent short entity
     */
    @Entity
    @Table(name = "KRTST_COLL_PRNT_T")
    private static class ShortParent {
        @Id
        @Column(name = "PK_PROP")
        @GeneratedValue(generator = "KRTST_GENERATED_PK_S")
        @PortableSequenceGenerator(name="KRTST_GENERATED_PK_S")
        private String id;

        @OneToMany(orphanRemoval = true, cascade = { CascadeType.ALL })
        @JoinColumn(name = "PRNT_KEY", referencedColumnName = "PK_PROP")
        @FilterGenerator(attributeName = "shortProp", attributeValue = "255")
        private List<ShortChild> children;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public List<ShortChild> getChildren() {
            return children;
        }

        public void setChildren(List<ShortChild> children) {
            this.children = children;
        }
    }

    /**
     * The child short entity
     */
    @Entity
    @Table(name = "KRTST_COLL_CHLD_T")
    private static class ShortChild {

        @Id
        @Column(name = "PK_PROP")
        @GeneratedValue(generator = "KRTST_GENERATED_PK_S")
        @PortableSequenceGenerator(name="KRTST_GENERATED_PK_S")
        private String id;

        @Column(name = "PRNT_KEY")
        private String parentKey;

        @Column(name = "SHORT_PROP")
        private short shortProp;

        @ManyToOne
        @JoinColumn(name = "PK_PROP" ,insertable=false, updatable=false)
        ShortParent parent;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getParentKey() {
            return parentKey;
        }

        public void setParentKey(String parentKey) {
            this.parentKey = parentKey;
        }

        public long getShortProp() {
            return shortProp;
        }

        public void setShortProp(short shortProp) {
            this.shortProp = shortProp;
        }

        public ShortParent getParent() {
            return parent;
        }

        public void setParent(ShortParent parent) {
            this.parent = parent;
        }
    }

    /**
     * The parent float entity
     */
    @Entity
    @Table(name = "KRTST_COLL_PRNT_T")
    private static class FloatParent {
        @Id
        @Column(name = "PK_PROP")
        @GeneratedValue(generator = "KRTST_GENERATED_PK_S")
        @PortableSequenceGenerator(name="KRTST_GENERATED_PK_S")
        private String id;

        @OneToMany(orphanRemoval = true, cascade = { CascadeType.ALL })
        @JoinColumn(name = "PRNT_KEY", referencedColumnName = "PK_PROP")
        @FilterGenerator(attributeName = "floatProp", attributeValue = "3.14")
        private List<FloatChild> children;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public List<FloatChild> getChildren() {
            return children;
        }

        public void setChildren(List<FloatChild> children) {
            this.children = children;
        }
    }

    /**
     * The child float entity
     */
    @Entity
    @Table(name = "KRTST_COLL_CHLD_T")
    private static class FloatChild {

        @Id
        @Column(name = "PK_PROP")
        @GeneratedValue(generator = "KRTST_GENERATED_PK_S")
        @PortableSequenceGenerator(name="KRTST_GENERATED_PK_S")
        private String id;

        @Column(name = "PRNT_KEY")
        private String parentKey;

        @Column(name = "FLOAT_PROP")
        private float floatProp;

        @ManyToOne
        @JoinColumn(name = "PK_PROP" ,insertable=false, updatable=false)
        FloatParent parent;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getParentKey() {
            return parentKey;
        }

        public void setParentKey(String parentKey) {
            this.parentKey = parentKey;
        }

        public float getFloatProp() {
            return floatProp;
        }

        public void setFloatProp(float floatProp) {
            this.floatProp = floatProp;
        }

        public FloatParent getParent() {
            return parent;
        }

        public void setParent(FloatParent parent) {
            this.parent = parent;
        }
    }

    /**
     * The parent double entity
     */
    @Entity
    @Table(name = "KRTST_COLL_PRNT_T")
    private static class DoubleParent {
        @Id
        @Column(name = "PK_PROP")
        @GeneratedValue(generator = "KRTST_GENERATED_PK_S")
        @PortableSequenceGenerator(name="KRTST_GENERATED_PK_S")
        private String id;

        @OneToMany(orphanRemoval = true, cascade = { CascadeType.ALL })
        @JoinColumn(name = "PRNT_KEY", referencedColumnName = "PK_PROP")
        @FilterGenerator(attributeName = "doubleProp", attributeValue = "6.28")
        private List<DoubleChild> children;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public List<DoubleChild> getChildren() {
            return children;
        }

        public void setChildren(List<DoubleChild> children) {
            this.children = children;
        }
    }

    /**
     * The child double entity
     */
    @Entity
    @Table(name = "KRTST_COLL_CHLD_T")
    private static class DoubleChild {

        @Id
        @Column(name = "PK_PROP")
        @GeneratedValue(generator = "KRTST_GENERATED_PK_S")
        @PortableSequenceGenerator(name="KRTST_GENERATED_PK_S")
        private String id;

        @Column(name = "PRNT_KEY")
        private String parentKey;

        @Column(name = "DOUBLE_PROP")
        private double doubleProp;

        @ManyToOne
        @JoinColumn(name = "PK_PROP" ,insertable=false, updatable=false)
        DoubleParent parent;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getParentKey() {
            return parentKey;
        }

        public void setParentKey(String parentKey) {
            this.parentKey = parentKey;
        }

        public double getDoubleProp() {
            return doubleProp;
        }

        public void setDoubleProp(double doubleProp) {
            this.doubleProp = doubleProp;
        }

        public DoubleParent getParent() {
            return parent;
        }

        public void setParent(DoubleParent parent) {
            this.parent = parent;
        }
    }

    /**
     * Gets a reference to the data object service
     * @return a reference to the data object service
     */
    private DataObjectService getDataObjectService() {
        return KRADServiceLocator.getDataObjectService();
    }

}
