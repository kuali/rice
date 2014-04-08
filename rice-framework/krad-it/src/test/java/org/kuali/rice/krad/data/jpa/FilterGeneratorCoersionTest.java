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

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import static org.junit.Assert.assertTrue;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
// no rollback so we can avoid transactional cache mucking up our results
@BaselineTestCase.BaselineMode(BaselineTestCase.Mode.CLEAR_DB)
public class FilterGeneratorCoersionTest extends KRADTestCase {

    /**
     * Test to see if the {@link FilterGenerator} matches the same primitive character values.
     * @throws Exception for any test exception.
     */
    @Test
    public void testPrimitiveCharacterMatch() throws Exception {
        PrimitiveCharacterFilterGeneratorParent parent = new PrimitiveCharacterFilterGeneratorParent();
        parent.setCharProp('Y');
        getDataObjectService().save(parent);

        PrimitiveCharacterFilterGeneratorChild child = new PrimitiveCharacterFilterGeneratorChild();
        child.setCharProp(parent.getCharProp());
        getDataObjectService().save(child);

        parent = getDataObjectService().find(PrimitiveCharacterFilterGeneratorParent.class, parent.getCharProp());
        assertTrue("No parent found", parent != null);
        assertTrue("No matching child found", parent.getCharPropChild() != null);
    }

    /**
     * Test to see if the {@link FilterGenerator} does not match different primitive character values.
     * @throws Exception for any test exception.
     */
    @Test
    public void testPrimitiveCharacterNoMatch() throws Exception {
        PrimitiveCharacterFilterGeneratorParent parent = new PrimitiveCharacterFilterGeneratorParent();
        parent.setCharProp('N');
        getDataObjectService().save(parent);

        PrimitiveCharacterFilterGeneratorChild child = new PrimitiveCharacterFilterGeneratorChild();
        child.setCharProp(parent.getCharProp());
        getDataObjectService().save(child);

        parent = getDataObjectService().find(PrimitiveCharacterFilterGeneratorParent.class, parent.getCharProp());
        assertTrue("No parent found", parent != null);
        assertTrue("Matching child found", parent.getCharPropChild() == null);
    }

    /**
     * Test to see if the {@link FilterGenerator} matches the same wrapper character values.
     * @throws Exception for any test exception.
     */
    @Test
    public void testWrapperCharacterMatch() throws Exception {
        WrapperCharacterFilterGeneratorParent parent = new WrapperCharacterFilterGeneratorParent();
        parent.setCharProp(Character.valueOf('Y'));
        getDataObjectService().save(parent);

        WrapperCharacterFilterGeneratorChild child = new WrapperCharacterFilterGeneratorChild();
        child.setCharProp(parent.getCharProp());
        getDataObjectService().save(child);

        parent = getDataObjectService().find(WrapperCharacterFilterGeneratorParent.class, parent.getCharProp());
        assertTrue("No parent found", parent != null);
        assertTrue("No matching child found", parent.getCharPropChild() != null);
    }

    /**
     * Test to see if the {@link FilterGenerator} does not match different wrapper character values
     * @throws Exception for any test exception.
     */
    @Test
    public void testWrapperCharacterNoMatch() throws Exception {
        WrapperCharacterFilterGeneratorParent parent = new WrapperCharacterFilterGeneratorParent();
        parent.setCharProp(Character.valueOf('N'));
        getDataObjectService().save(parent);

        WrapperCharacterFilterGeneratorChild child = new WrapperCharacterFilterGeneratorChild();
        child.setCharProp(parent.getCharProp());
        getDataObjectService().save(child);

        parent = getDataObjectService().find(WrapperCharacterFilterGeneratorParent.class, parent.getCharProp());
        assertTrue("No parent found", parent != null);
        assertTrue("Matching child found", parent.getCharPropChild() == null);
    }

    /**
     * Simple parent entity to test primitive character mappings.
     */
    @Entity
    @Table(name = "KRTST_TYP_PARENT_T")
    public static class PrimitiveCharacterFilterGeneratorParent {

        @Id
        @Column(name = "CHAR_PROP")
        private char charProp;

        @ManyToOne(targetEntity = PrimitiveCharacterFilterGeneratorChild.class, fetch = FetchType.EAGER)
        @JoinColumn(name = "CHAR_PROP", insertable = false, updatable = false)
        @FilterGenerator(attributeName = "charProp", attributeValue = "Y")
        private PrimitiveCharacterFilterGeneratorChild charPropChild;

        public char getCharProp() {
            return charProp;
        }

        public void setCharProp(char charProp) {
            this.charProp = charProp;
        }

        public PrimitiveCharacterFilterGeneratorChild getCharPropChild() {
            return charPropChild;
        }

        public void setCharPropChild(PrimitiveCharacterFilterGeneratorChild charPropChild) {
            this.charPropChild = charPropChild;
        }

    }

    /**
     * Simple child entity to test primitive character mappings.
     */
    @Entity
    @Table(name = "KRTST_TYP_CHILD_T")
    public static class PrimitiveCharacterFilterGeneratorChild {

        @Id
        @Column(name = "CHAR_PROP")
        private char charProp;

        public char getCharProp() {
            return charProp;
        }

        public void setCharProp(char charProp) {
            this.charProp = charProp;
        }

    }

    /**
     * Simple parent entity to test wrapper character mappings.
     */
    @Entity
    @Table(name = "KRTST_TYP_PARENT_T")
    public static class WrapperCharacterFilterGeneratorParent {

        @Id
        @Column(name = "CHAR_PROP")
        private Character charProp;

        @ManyToOne(targetEntity = WrapperCharacterFilterGeneratorChild.class, fetch = FetchType.EAGER)
        @JoinColumn(name = "CHAR_PROP", insertable = false, updatable = false)
        @FilterGenerator(attributeName = "charProp", attributeValue = "Y")
        private WrapperCharacterFilterGeneratorChild charPropChild;

        public Character getCharProp() {
            return charProp;
        }

        public void setCharProp(Character charProp) {
            this.charProp = charProp;
        }

        public WrapperCharacterFilterGeneratorChild getCharPropChild() {
            return charPropChild;
        }

        public void setCharPropChild(WrapperCharacterFilterGeneratorChild charPropChild) {
            this.charPropChild = charPropChild;
        }

    }

    /**
     * Simple child entity to test wrapper character mappings.
     */
    @Entity
    @Table(name = "KRTST_TYP_CHILD_T")
    public static class WrapperCharacterFilterGeneratorChild {

        @Id
        @Column(name = "CHAR_PROP")
        private Character charProp;

        public Character getCharProp() {
            return charProp;
        }

        public void setCharProp(Character charProp) {
            this.charProp = charProp;
        }

    }

    /**
     * Test to see if the {@link FilterGenerator} matches the same primitive boolean values.
     * @throws Exception for any test exception.
     */
    @Test
    public void testPrimitiveBooleanMatch() throws Exception {
        PrimitiveBooleanFilterGeneratorParent parent = new PrimitiveBooleanFilterGeneratorParent();
        parent.setBoolProp(true);
        getDataObjectService().save(parent);

        PrimitiveBooleanFilterGeneratorChild child = new PrimitiveBooleanFilterGeneratorChild();
        child.setBoolProp(parent.getBoolProp());
        getDataObjectService().save(child);

        parent = getDataObjectService().find(PrimitiveBooleanFilterGeneratorParent.class, parent.getBoolProp());
        assertTrue("No parent found", parent != null);
        assertTrue("No matching child found", parent.getBoolPropChild() != null);
    }

    /**
     * Test to see if the {@link FilterGenerator} does not match different primitive boolean values.
     * @throws Exception for any test exception.
     */
    @Test
    public void testPrimitiveBooleanNoMatch() throws Exception {
        PrimitiveBooleanFilterGeneratorParent parent = new PrimitiveBooleanFilterGeneratorParent();
        parent.setBoolProp(false);
        getDataObjectService().save(parent);

        PrimitiveBooleanFilterGeneratorChild child = new PrimitiveBooleanFilterGeneratorChild();
        child.setBoolProp(parent.getBoolProp());
        getDataObjectService().save(child);

        parent = getDataObjectService().find(PrimitiveBooleanFilterGeneratorParent.class, parent.getBoolProp());
        assertTrue("No parent found", parent != null);
        assertTrue("Matching child found", parent.getBoolPropChild() == null);
    }

    /**
     * Test to see if the {@link FilterGenerator} matches the same wrapper boolean values.
     * @throws Exception for any test exception.
     */
    @Test
    public void testWrapperBooleanMatch() throws Exception {
        WrapperBooleanFilterGeneratorParent parent = new WrapperBooleanFilterGeneratorParent();
        parent.setBoolProp(Boolean.TRUE);
        getDataObjectService().save(parent);

        WrapperBooleanFilterGeneratorChild child = new WrapperBooleanFilterGeneratorChild();
        child.setBoolProp(parent.getBoolProp());
        getDataObjectService().save(child);

        parent = getDataObjectService().find(WrapperBooleanFilterGeneratorParent.class, parent.getBoolProp());
        assertTrue("No parent found", parent != null);
        assertTrue("No matching child found", parent.getBoolPropChild() != null);
    }

    /**
     * Test to see if the {@link FilterGenerator} does not match different wrapper boolean values.
     * @throws Exception for any test exception.
     */
    @Test
    public void testWrapperBooleanNoMatch() throws Exception {
        WrapperBooleanFilterGeneratorParent parent = new WrapperBooleanFilterGeneratorParent();
        parent.setBoolProp(Boolean.FALSE);
        getDataObjectService().save(parent);

        WrapperBooleanFilterGeneratorChild child = new WrapperBooleanFilterGeneratorChild();
        child.setBoolProp(parent.getBoolProp());
        getDataObjectService().save(child);

        parent = getDataObjectService().find(WrapperBooleanFilterGeneratorParent.class, parent.getBoolProp());
        assertTrue("No parent found", parent != null);
        assertTrue("Matching child found", parent.getBoolPropChild() == null);
    }

    /**
     * Simple parent entity to test primitive boolean mappings.
     */
    @Entity
    @Table(name = "KRTST_TYP_PARENT_T")
    public static class PrimitiveBooleanFilterGeneratorParent {

        @Id
        @Column(name = "BOOL_PROP")
        @Convert(converter = BooleanYNConverter.class)
        private boolean boolProp;

        @ManyToOne(targetEntity = PrimitiveBooleanFilterGeneratorChild.class, fetch = FetchType.EAGER)
        @JoinColumn(name = "BOOL_PROP", insertable = false, updatable = false)
        @FilterGenerator(attributeName = "boolProp", attributeValue = "true")
        private PrimitiveBooleanFilterGeneratorChild boolPropChild;

        public boolean getBoolProp() {
            return boolProp;
        }

        public void setBoolProp(boolean boolProp) {
            this.boolProp = boolProp;
        }

        public PrimitiveBooleanFilterGeneratorChild getBoolPropChild() {
            return boolPropChild;
        }

        public void setBoolPropChild(PrimitiveBooleanFilterGeneratorChild boolPropChild) {
            this.boolPropChild = boolPropChild;
        }

    }

    /**
     * Simple child entity to test primitive boolean mappings.
     */
    @Entity
    @Table(name = "KRTST_TYP_CHILD_T")
    public static class PrimitiveBooleanFilterGeneratorChild {

        @Id
        @Column(name = "BOOL_PROP")
        @Convert(converter = BooleanYNConverter.class)
        private boolean boolProp;

        public boolean getBoolProp() {
            return boolProp;
        }

        public void setBoolProp(boolean boolProp) {
            this.boolProp = boolProp;
        }

    }

    /**
     * Simple parent entity to test wrapper boolean mappings.
     */
    @Entity
    @Table(name = "KRTST_TYP_PARENT_T")
    public static class WrapperBooleanFilterGeneratorParent {

        @Id
        @Column(name = "BOOL_PROP")
        @Convert(converter = BooleanYNConverter.class)
        private Boolean boolProp;

        @ManyToOne(targetEntity = WrapperBooleanFilterGeneratorChild.class, fetch = FetchType.EAGER)
        @JoinColumn(name = "BOOL_PROP", insertable = false, updatable = false)
        @FilterGenerator(attributeName = "boolProp", attributeValue = "true")
        private WrapperBooleanFilterGeneratorChild boolPropChild;

        public Boolean getBoolProp() {
            return boolProp;
        }

        public void setBoolProp(Boolean boolProp) {
            this.boolProp = boolProp;
        }

        public WrapperBooleanFilterGeneratorChild getBoolPropChild() {
            return boolPropChild;
        }

        public void setBooleanPropChild(WrapperBooleanFilterGeneratorChild boolPropChild) {
            this.boolPropChild = boolPropChild;
        }

    }

    /**
     * Simple child entity to test wrapper boolean mappings.
     */
    @Entity
    @Table(name = "KRTST_TYP_CHILD_T")
    public static class WrapperBooleanFilterGeneratorChild {

        @Id
        @Column(name = "BOOL_PROP")
        @Convert(converter = BooleanYNConverter.class)
        private Boolean boolProp;

        public Boolean getBoolProp() {
            return boolProp;
        }

        public void setBoolProp(Boolean boolProp) {
            this.boolProp = boolProp;
        }

    }

    /**
     * Test to see if the {@link FilterGenerator} matches the same primitive short values.
     * @throws Exception for any test exception.
     */
    @Test
    public void testPrimitiveShortMatch() throws Exception {
        PrimitiveShortFilterGeneratorParent parent = new PrimitiveShortFilterGeneratorParent();
        parent.setShortProp((short) 10);
        getDataObjectService().save(parent);

        PrimitiveShortFilterGeneratorChild child = new PrimitiveShortFilterGeneratorChild();
        child.setShortProp(parent.getShortProp());
        getDataObjectService().save(child);

        parent = getDataObjectService().find(PrimitiveShortFilterGeneratorParent.class, parent.getShortProp());
        assertTrue("No parent found", parent != null);
        assertTrue("No matching child found", parent.getShortPropChild() != null);
    }

    /**
     * Test to see if the {@link FilterGenerator} does not match different primitive short values.
     * @throws Exception for any test exception.
     */
    @Test
    public void testPrimitiveShortNoMatch() throws Exception {
        PrimitiveShortFilterGeneratorParent parent = new PrimitiveShortFilterGeneratorParent();
        parent.setShortProp((short) 20);
        getDataObjectService().save(parent);

        PrimitiveShortFilterGeneratorChild child = new PrimitiveShortFilterGeneratorChild();
        child.setShortProp(parent.getShortProp());
        getDataObjectService().save(child);

        parent = getDataObjectService().find(PrimitiveShortFilterGeneratorParent.class, parent.getShortProp());
        assertTrue("No parent found", parent != null);
        assertTrue("Matching child found", parent.getShortPropChild() == null);
    }

    /**
     * Test to see if the {@link FilterGenerator} matches the same wrapper short values.
     * @throws Exception for any test exception.
     */
    @Test
    public void testWrapperShortMatch() throws Exception {
        WrapperShortFilterGeneratorParent parent = new WrapperShortFilterGeneratorParent();
        parent.setShortProp(Short.valueOf((short) 10));
        getDataObjectService().save(parent);

        WrapperShortFilterGeneratorChild child = new WrapperShortFilterGeneratorChild();
        child.setShortProp(parent.getShortProp());
        getDataObjectService().save(child);

        parent = getDataObjectService().find(WrapperShortFilterGeneratorParent.class, parent.getShortProp());
        assertTrue("No parent found", parent != null);
        assertTrue("No matching child found", parent.getShortPropChild() != null);
    }

    /**
     * Test to see if the {@link FilterGenerator} does not match different wrapper short values.
     * @throws Exception for any test exception.
     */
    @Test
    public void testWrapperShortNoMatch() throws Exception {
        WrapperShortFilterGeneratorParent parent = new WrapperShortFilterGeneratorParent();
        parent.setShortProp(Short.valueOf((short) 20));
        getDataObjectService().save(parent);

        WrapperShortFilterGeneratorChild child = new WrapperShortFilterGeneratorChild();
        child.setShortProp(parent.getShortProp());
        getDataObjectService().save(child);

        parent = getDataObjectService().find(WrapperShortFilterGeneratorParent.class, parent.getShortProp());
        assertTrue("No parent found", parent != null);
        assertTrue("Matching child found", parent.getShortPropChild() == null);
    }

    /**
     * Simple parent entity to test primitive short mappings.
     */
    @Entity
    @Table(name = "KRTST_TYP_PARENT_T")
    public static class PrimitiveShortFilterGeneratorParent {

        @Id
        @Column(name = "SHORT_PROP")
        private short shortProp;

        @ManyToOne(targetEntity = PrimitiveShortFilterGeneratorChild.class, fetch = FetchType.EAGER)
        @JoinColumn(name = "SHORT_PROP", insertable = false, updatable = false)
        @FilterGenerator(attributeName = "shortProp", attributeValue = "10")
        private PrimitiveShortFilterGeneratorChild shortPropChild;

        public short getShortProp() {
            return shortProp;
        }

        public void setShortProp(short shortProp) {
            this.shortProp = shortProp;
        }

        public PrimitiveShortFilterGeneratorChild getShortPropChild() {
            return shortPropChild;
        }

        public void setShortPropChild(PrimitiveShortFilterGeneratorChild shortPropChild) {
            this.shortPropChild = shortPropChild;
        }

    }

    /**
     * Simple child entity to test primitive short mappings.
     */
    @Entity
    @Table(name = "KRTST_TYP_CHILD_T")
    public static class PrimitiveShortFilterGeneratorChild {

        @Id
        @Column(name = "SHORT_PROP")
        private short shortProp;

        public short getShortProp() {
            return shortProp;
        }

        public void setShortProp(short shortProp) {
            this.shortProp = shortProp;
        }

    }

    /**
     * Simple parent entity to test wrapper short mappings.
     */
    @Entity
    @Table(name = "KRTST_TYP_PARENT_T")
    public static class WrapperShortFilterGeneratorParent {

        @Id
        @Column(name = "SHORT_PROP")
        private Short shortProp;

        @ManyToOne(targetEntity = WrapperShortFilterGeneratorChild.class, fetch = FetchType.EAGER)
        @JoinColumn(name = "SHORT_PROP", insertable = false, updatable = false)
        @FilterGenerator(attributeName = "shortProp", attributeValue = "10")
        private WrapperShortFilterGeneratorChild shortPropChild;

        public Short getShortProp() {
            return shortProp;
        }

        public void setShortProp(Short shortProp) {
            this.shortProp = shortProp;
        }

        public WrapperShortFilterGeneratorChild getShortPropChild() {
            return shortPropChild;
        }

        public void setShortPropChild(WrapperShortFilterGeneratorChild shortPropChild) {
            this.shortPropChild = shortPropChild;
        }

    }

    /**
     * Simple child entity to test wrapper short mappings.
     */
    @Entity
    @Table(name = "KRTST_TYP_CHILD_T")
    public static class WrapperShortFilterGeneratorChild {

        @Id
        @Column(name = "SHORT_PROP")
        private Short shortProp;

        public Short getShortProp() {
            return shortProp;
        }

        public void setShortProp(Short shortProp) {
            this.shortProp = shortProp;
        }

    }

    /**
     * Test to see if the {@link FilterGenerator} matches the same primitive integer values.
     * @throws Exception for any test exception.
     */
    @Test
    public void testPrimitiveIntegerMatch() throws Exception {
        PrimitiveIntegerFilterGeneratorParent parent = new PrimitiveIntegerFilterGeneratorParent();
        parent.setIntProp(10);
        getDataObjectService().save(parent);

        PrimitiveIntegerFilterGeneratorChild child = new PrimitiveIntegerFilterGeneratorChild();
        child.setIntProp(parent.getIntProp());
        getDataObjectService().save(child);

        parent = getDataObjectService().find(PrimitiveIntegerFilterGeneratorParent.class, parent.getIntProp());
        assertTrue("No parent found", parent != null);
        assertTrue("No matching child found", parent.getIntPropChild() != null);
    }

    /**
     * Test to see if the {@link FilterGenerator} does not match different primitive integer values.
     * @throws Exception for any test exception.
     */
    @Test
    public void testPrimitiveIntegerNoMatch() throws Exception {
        PrimitiveIntegerFilterGeneratorParent parent = new PrimitiveIntegerFilterGeneratorParent();
        parent.setIntProp(20);
        getDataObjectService().save(parent);

        PrimitiveIntegerFilterGeneratorChild child = new PrimitiveIntegerFilterGeneratorChild();
        child.setIntProp(parent.getIntProp());
        getDataObjectService().save(child);

        parent = getDataObjectService().find(PrimitiveIntegerFilterGeneratorParent.class, parent.getIntProp());
        assertTrue("No parent found", parent != null);
        assertTrue("Matching child found", parent.getIntPropChild() == null);
    }

    /**
     * Test to see if the {@link FilterGenerator} matches the same wrapper integer values.
     * @throws Exception for any test exception.
     */
    @Test
    public void testWrapperIntegerMatch() throws Exception {
        WrapperIntegerFilterGeneratorParent parent = new WrapperIntegerFilterGeneratorParent();
        parent.setIntProp(Integer.valueOf(10));
        getDataObjectService().save(parent);

        WrapperIntegerFilterGeneratorChild child = new WrapperIntegerFilterGeneratorChild();
        child.setIntProp(parent.getIntProp());
        getDataObjectService().save(child);

        parent = getDataObjectService().find(WrapperIntegerFilterGeneratorParent.class, parent.getIntProp());
        assertTrue("No parent found", parent != null);
        assertTrue("No matching child found", parent.getIntPropChild() != null);
    }

    /**
     * Test to see if the {@link FilterGenerator} does not match different wrapper integer values.
     * @throws Exception for any test exception.
     */
    @Test
    public void testWrapperIntegerNoMatch() throws Exception {
        WrapperIntegerFilterGeneratorParent parent = new WrapperIntegerFilterGeneratorParent();
        parent.setIntProp(Integer.valueOf(20));
        getDataObjectService().save(parent);

        WrapperIntegerFilterGeneratorChild child = new WrapperIntegerFilterGeneratorChild();
        child.setIntProp(parent.getIntProp());
        getDataObjectService().save(child);

        parent = getDataObjectService().find(WrapperIntegerFilterGeneratorParent.class, parent.getIntProp());
        assertTrue("No parent found", parent != null);
        assertTrue("Matching child found", parent.getIntPropChild() == null);
    }

    /**
     * Simple parent entity to test primitive integer mappings.
     */
    @Entity
    @Table(name = "KRTST_TYP_PARENT_T")
    public static class PrimitiveIntegerFilterGeneratorParent {

        @Id
        @Column(name = "INT_PROP")
        private int intProp;

        @ManyToOne(targetEntity = PrimitiveIntegerFilterGeneratorChild.class, fetch = FetchType.EAGER)
        @JoinColumn(name = "INT_PROP", insertable = false, updatable = false)
        @FilterGenerator(attributeName = "intProp", attributeValue = "10")
        private PrimitiveIntegerFilterGeneratorChild intPropChild;

        public int getIntProp() {
            return intProp;
        }

        public void setIntProp(int intProp) {
            this.intProp = intProp;
        }

        public PrimitiveIntegerFilterGeneratorChild getIntPropChild() {
            return intPropChild;
        }

        public void setIntPropChild(PrimitiveIntegerFilterGeneratorChild intPropChild) {
            this.intPropChild = intPropChild;
        }

    }

    /**
     * Simple child entity to test primitive integer mappings.
     */
    @Entity
    @Table(name = "KRTST_TYP_CHILD_T")
    public static class PrimitiveIntegerFilterGeneratorChild {

        @Id
        @Column(name = "INT_PROP")
        private int intProp;

        public int getIntProp() {
            return intProp;
        }

        public void setIntProp(int intProp) {
            this.intProp = intProp;
        }

    }

    /**
     * Simple parent entity to test wrapper integer mappings.
     */
    @Entity
    @Table(name = "KRTST_TYP_PARENT_T")
    public static class WrapperIntegerFilterGeneratorParent {

        @Id
        @Column(name = "INT_PROP")
        private Integer intProp;

        @ManyToOne(targetEntity = WrapperIntegerFilterGeneratorChild.class, fetch = FetchType.EAGER)
        @JoinColumn(name = "INT_PROP", insertable = false, updatable = false)
        @FilterGenerator(attributeName = "intProp", attributeValue = "10")
        private WrapperIntegerFilterGeneratorChild intPropChild;

        public Integer getIntProp() {
            return intProp;
        }

        public void setIntProp(Integer intProp) {
            this.intProp = intProp;
        }

        public WrapperIntegerFilterGeneratorChild getIntPropChild() {
            return intPropChild;
        }

        public void setIntPropChild(WrapperIntegerFilterGeneratorChild intPropChild) {
            this.intPropChild = intPropChild;
        }

    }

    /**
     * Simple child entity to test wrapper integer mappings.
     */
    @Entity
    @Table(name = "KRTST_TYP_CHILD_T")
    public static class WrapperIntegerFilterGeneratorChild {

        @Id
        @Column(name = "INT_PROP")
        private Integer intProp;

        public Integer getIntProp() {
            return intProp;
        }

        public void setIntProp(Integer intProp) {
            this.intProp = intProp;
        }

    }

    /**
     * Test to see if the {@link FilterGenerator} matches the same primitive long values.
     * @throws Exception for any test exception.
     */
    @Test
    public void testPrimitiveLongMatch() throws Exception {
        PrimitiveLongFilterGeneratorParent parent = new PrimitiveLongFilterGeneratorParent();
        parent.setLongProp(10L);
        getDataObjectService().save(parent);

        PrimitiveLongFilterGeneratorChild child = new PrimitiveLongFilterGeneratorChild();
        child.setLongProp(parent.getLongProp());
        getDataObjectService().save(child);

        parent = getDataObjectService().find(PrimitiveLongFilterGeneratorParent.class, parent.getLongProp());
        assertTrue("No parent found", parent != null);
        assertTrue("No matching child found", parent.getLongPropChild() != null);
    }

    /**
     * Test to see if the {@link FilterGenerator} does not match different primitive long values.
     * @throws Exception for any test exception.
     */
    @Test
    public void testPrimitiveLongNoMatch() throws Exception {
        PrimitiveLongFilterGeneratorParent parent = new PrimitiveLongFilterGeneratorParent();
        parent.setLongProp(20L);
        getDataObjectService().save(parent);

        PrimitiveLongFilterGeneratorChild child = new PrimitiveLongFilterGeneratorChild();
        child.setLongProp(parent.getLongProp());
        getDataObjectService().save(child);

        parent = getDataObjectService().find(PrimitiveLongFilterGeneratorParent.class, parent.getLongProp());
        assertTrue("No parent found", parent != null);
        assertTrue("Matching child found", parent.getLongPropChild() == null);
    }

    /**
     * Test to see if the {@link FilterGenerator} matches the same wrapper long values.
     * @throws Exception for any test exception.
     */
    @Test
    public void testWrapperLongMatch() throws Exception {
        WrapperLongFilterGeneratorParent parent = new WrapperLongFilterGeneratorParent();
        parent.setLongProp(Long.valueOf(10L));
        getDataObjectService().save(parent);

        WrapperLongFilterGeneratorChild child = new WrapperLongFilterGeneratorChild();
        child.setLongProp(parent.getLongProp());
        getDataObjectService().save(child);

        parent = getDataObjectService().find(WrapperLongFilterGeneratorParent.class, parent.getLongProp());
        assertTrue("No parent found", parent != null);
        assertTrue("No matching child found", parent.getLongPropChild() != null);
    }

    /**
     * Test to see if the {@link FilterGenerator} does not match different wrapper long values.
     * @throws Exception for any test exception.
     */
    @Test
    public void testWrapperLongNoMatch() throws Exception {
        WrapperLongFilterGeneratorParent parent = new WrapperLongFilterGeneratorParent();
        parent.setLongProp(Long.valueOf(20L));
        getDataObjectService().save(parent);

        WrapperLongFilterGeneratorChild child = new WrapperLongFilterGeneratorChild();
        child.setLongProp(parent.getLongProp());
        getDataObjectService().save(child);

        parent = getDataObjectService().find(WrapperLongFilterGeneratorParent.class, parent.getLongProp());
        assertTrue("No parent found", parent != null);
        assertTrue("Matching child found", parent.getLongPropChild() == null);
    }

    /**
     * Simple parent entity to test primitive long mappings.
     */
    @Entity
    @Table(name = "KRTST_TYP_PARENT_T")
    public static class PrimitiveLongFilterGeneratorParent {

        @Id
        @Column(name = "LONG_PROP")
        private long longProp;

        @ManyToOne(targetEntity = PrimitiveLongFilterGeneratorChild.class, fetch = FetchType.EAGER)
        @JoinColumn(name = "LONG_PROP", insertable = false, updatable = false)
        @FilterGenerator(attributeName = "longProp", attributeValue = "10")
        private PrimitiveLongFilterGeneratorChild longPropChild;

        public long getLongProp() {
            return longProp;
        }

        public void setLongProp(long longProp) {
            this.longProp = longProp;
        }

        public PrimitiveLongFilterGeneratorChild getLongPropChild() {
            return longPropChild;
        }

        public void setLongPropChild(PrimitiveLongFilterGeneratorChild longPropChild) {
            this.longPropChild = longPropChild;
        }

    }

    /**
     * Simple child entity to test primitive long mappings.
     */
    @Entity
    @Table(name = "KRTST_TYP_CHILD_T")
    public static class PrimitiveLongFilterGeneratorChild {

        @Id
        @Column(name = "LONG_PROP")
        private long longProp;

        public long getLongProp() {
            return longProp;
        }

        public void setLongProp(long longProp) {
            this.longProp = longProp;
        }

    }

    /**
     * Simple parent entity to test wrapper long mappings.
     */
    @Entity
    @Table(name = "KRTST_TYP_PARENT_T")
    public static class WrapperLongFilterGeneratorParent {

        @Id
        @Column(name = "LONG_PROP")
        private Long longProp;

        @ManyToOne(targetEntity = WrapperLongFilterGeneratorChild.class, fetch = FetchType.EAGER)
        @JoinColumn(name = "LONG_PROP", insertable = false, updatable = false)
        @FilterGenerator(attributeName = "longProp", attributeValue = "10")
        private WrapperLongFilterGeneratorChild longPropChild;

        public Long getLongProp() {
            return longProp;
        }

        public void setLongProp(Long longProp) {
            this.longProp = longProp;
        }

        public WrapperLongFilterGeneratorChild getLongPropChild() {
            return longPropChild;
        }

        public void setLongPropChild(WrapperLongFilterGeneratorChild longPropChild) {
            this.longPropChild = longPropChild;
        }

    }

    /**
     * Simple child entity to test wrapper long mappings.
     */
    @Entity
    @Table(name = "KRTST_TYP_CHILD_T")
    public static class WrapperLongFilterGeneratorChild {

        @Id
        @Column(name = "LONG_PROP")
        private Long longProp;

        public Long getLongProp() {
            return longProp;
        }

        public void setLongProp(Long longProp) {
            this.longProp = longProp;
        }

    }

    /**
     * Test to see if the {@link FilterGenerator} matches the same primitive float values.
     * @throws Exception for any test exception.
     */
    @Test
    public void testPrimitiveFloatMatch() throws Exception {
        PrimitiveFloatFilterGeneratorParent parent = new PrimitiveFloatFilterGeneratorParent();
        parent.setFloatProp(10f);
        getDataObjectService().save(parent);

        PrimitiveFloatFilterGeneratorChild child = new PrimitiveFloatFilterGeneratorChild();
        child.setFloatProp(parent.getFloatProp());
        getDataObjectService().save(child);

        parent = getDataObjectService().find(PrimitiveFloatFilterGeneratorParent.class, parent.getFloatProp());
        assertTrue("No parent found", parent != null);
        assertTrue("No matching child found", parent.getFloatPropChild() != null);
    }

    /**
     * Test to see if the {@link FilterGenerator} does not match different primitive float values.
     * @throws Exception for any test exception.
     */
    @Test
    public void testPrimitiveFloatNoMatch() throws Exception {
        PrimitiveFloatFilterGeneratorParent parent = new PrimitiveFloatFilterGeneratorParent();
        parent.setFloatProp(20f);
        getDataObjectService().save(parent);

        PrimitiveFloatFilterGeneratorChild child = new PrimitiveFloatFilterGeneratorChild();
        child.setFloatProp(parent.getFloatProp());
        getDataObjectService().save(child);

        parent = getDataObjectService().find(PrimitiveFloatFilterGeneratorParent.class, parent.getFloatProp());
        assertTrue("No parent found", parent != null);
        assertTrue("Matching child found", parent.getFloatPropChild() == null);
    }

    /**
     * Test to see if the {@link FilterGenerator} matches the same wrapper float values.
     * @throws Exception for any test exception.
     */
    @Test
    public void testWrapperFloatMatch() throws Exception {
        WrapperFloatFilterGeneratorParent parent = new WrapperFloatFilterGeneratorParent();
        parent.setFloatProp(Float.valueOf(10f));
        getDataObjectService().save(parent);

        WrapperFloatFilterGeneratorChild child = new WrapperFloatFilterGeneratorChild();
        child.setFloatProp(parent.getFloatProp());
        getDataObjectService().save(child);

        parent = getDataObjectService().find(WrapperFloatFilterGeneratorParent.class, parent.getFloatProp());
        assertTrue("No parent found", parent != null);
        assertTrue("No matching child found", parent.getFloatPropChild() != null);
    }

    /**
     * Test to see if the {@link FilterGenerator} does not match different wrapper float values.
     * @throws Exception for any test exception.
     */
    @Test
    public void testWrapperFloatNoMatch() throws Exception {
        WrapperFloatFilterGeneratorParent parent = new WrapperFloatFilterGeneratorParent();
        parent.setFloatProp(Float.valueOf(20f));
        getDataObjectService().save(parent);

        WrapperFloatFilterGeneratorChild child = new WrapperFloatFilterGeneratorChild();
        child.setFloatProp(parent.getFloatProp());
        getDataObjectService().save(child);

        parent = getDataObjectService().find(WrapperFloatFilterGeneratorParent.class, parent.getFloatProp());
        assertTrue("No parent found", parent != null);
        assertTrue("Matching child found", parent.getFloatPropChild() == null);
    }

    /**
     * Simple parent entity to test primitive float mappings.
     */
    @Entity
    @Table(name = "KRTST_TYP_PARENT_T")
    public static class PrimitiveFloatFilterGeneratorParent {

        @Id
        @Column(name = "FLOAT_PROP")
        private float floatProp;

        @ManyToOne(targetEntity = PrimitiveFloatFilterGeneratorChild.class, fetch = FetchType.EAGER)
        @JoinColumn(name = "FLOAT_PROP", insertable = false, updatable = false)
        @FilterGenerator(attributeName = "floatProp", attributeValue = "10")
        private PrimitiveFloatFilterGeneratorChild floatPropChild;

        public float getFloatProp() {
            return floatProp;
        }

        public void setFloatProp(float floatProp) {
            this.floatProp = floatProp;
        }

        public PrimitiveFloatFilterGeneratorChild getFloatPropChild() {
            return floatPropChild;
        }

        public void setFloatPropChild(PrimitiveFloatFilterGeneratorChild floatPropChild) {
            this.floatPropChild = floatPropChild;
        }

    }

    /**
     * Simple child entity to test primitive float mappings.
     */
    @Entity
    @Table(name = "KRTST_TYP_CHILD_T")
    public static class PrimitiveFloatFilterGeneratorChild {

        @Id
        @Column(name = "FLOAT_PROP")
        private float floatProp;

        public float getFloatProp() {
            return floatProp;
        }

        public void setFloatProp(float floatProp) {
            this.floatProp = floatProp;
        }

    }

    /**
     * Simple parent entity to test wrapper float mappings.
     */
    @Entity
    @Table(name = "KRTST_TYP_PARENT_T")
    public static class WrapperFloatFilterGeneratorParent {

        @Id
        @Column(name = "FLOAT_PROP")
        private Float floatProp;

        @ManyToOne(targetEntity = WrapperFloatFilterGeneratorChild.class, fetch = FetchType.EAGER)
        @JoinColumn(name = "FLOAT_PROP", insertable = false, updatable = false)
        @FilterGenerator(attributeName = "floatProp", attributeValue = "10")
        private WrapperFloatFilterGeneratorChild floatPropChild;

        public Float getFloatProp() {
            return floatProp;
        }

        public void setFloatProp(Float floatProp) {
            this.floatProp = floatProp;
        }

        public WrapperFloatFilterGeneratorChild getFloatPropChild() {
            return floatPropChild;
        }

        public void setFloatPropChild(WrapperFloatFilterGeneratorChild floatPropChild) {
            this.floatPropChild = floatPropChild;
        }

    }

    /**
     * Simple child entity to test wrapper float mappings.
     */
    @Entity
    @Table(name = "KRTST_TYP_CHILD_T")
    public static class WrapperFloatFilterGeneratorChild {

        @Id
        @Column(name = "FLOAT_PROP")
        private Float floatProp;

        public Float getFloatProp() {
            return floatProp;
        }

        public void setFloatProp(Float floatProp) {
            this.floatProp = floatProp;
        }

    }
    
    /**
     * Test to see if the {@link FilterGenerator} matches the same primitive double values.
     * @throws Exception for any test exception.
     */
    @Test
    public void testPrimitiveDoubleMatch() throws Exception {
        PrimitiveDoubleFilterGeneratorParent parent = new PrimitiveDoubleFilterGeneratorParent();
        parent.setDoubleProp(10d);
        getDataObjectService().save(parent);

        PrimitiveDoubleFilterGeneratorChild child = new PrimitiveDoubleFilterGeneratorChild();
        child.setDoubleProp(parent.getDoubleProp());
        getDataObjectService().save(child);

        parent = getDataObjectService().find(PrimitiveDoubleFilterGeneratorParent.class, parent.getDoubleProp());
        assertTrue("No parent found", parent != null);
        assertTrue("No matching child found", parent.getDoublePropChild() != null);
    }

    /**
     * Test to see if the {@link FilterGenerator} does not match different primitive double values.
     * @throws Exception for any test exception.
     */
    @Test
    public void testPrimitiveDoubleNoMatch() throws Exception {
        PrimitiveDoubleFilterGeneratorParent parent = new PrimitiveDoubleFilterGeneratorParent();
        parent.setDoubleProp(20d);
        getDataObjectService().save(parent);

        PrimitiveDoubleFilterGeneratorChild child = new PrimitiveDoubleFilterGeneratorChild();
        child.setDoubleProp(parent.getDoubleProp());
        getDataObjectService().save(child);

        parent = getDataObjectService().find(PrimitiveDoubleFilterGeneratorParent.class, parent.getDoubleProp());
        assertTrue("No parent found", parent != null);
        assertTrue("Matching child found", parent.getDoublePropChild() == null);
    }

    /**
     * Test to see if the {@link FilterGenerator} matches the same wrapper double values.
     * @throws Exception for any test exception.
     */
    @Test
    public void testWrapperDoubleMatch() throws Exception {
        WrapperDoubleFilterGeneratorParent parent = new WrapperDoubleFilterGeneratorParent();
        parent.setDoubleProp(Double.valueOf(10d));
        getDataObjectService().save(parent);

        WrapperDoubleFilterGeneratorChild child = new WrapperDoubleFilterGeneratorChild();
        child.setDoubleProp(parent.getDoubleProp());
        getDataObjectService().save(child);

        parent = getDataObjectService().find(WrapperDoubleFilterGeneratorParent.class, parent.getDoubleProp());
        assertTrue("No parent found", parent != null);
        assertTrue("No matching child found", parent.getDoublePropChild() != null);
    }

    /**
     * Test to see if the {@link FilterGenerator} does not match different wrapper double values.
     * @throws Exception for any test exception.
     */
    @Test
    public void testWrapperDoubleNoMatch() throws Exception {
        WrapperDoubleFilterGeneratorParent parent = new WrapperDoubleFilterGeneratorParent();
        parent.setDoubleProp(Double.valueOf(20d));
        getDataObjectService().save(parent);

        WrapperDoubleFilterGeneratorChild child = new WrapperDoubleFilterGeneratorChild();
        child.setDoubleProp(parent.getDoubleProp());
        getDataObjectService().save(child);

        parent = getDataObjectService().find(WrapperDoubleFilterGeneratorParent.class, parent.getDoubleProp());
        assertTrue("No parent found", parent != null);
        assertTrue("Matching child found", parent.getDoublePropChild() == null);
    }

    /**
     * Simple parent entity to test primitive double mappings.
     */
    @Entity
    @Table(name = "KRTST_TYP_PARENT_T")
    public static class PrimitiveDoubleFilterGeneratorParent {

        @Id
        @Column(name = "DOUBLE_PROP")
        private double doubleProp;

        @ManyToOne(targetEntity = PrimitiveDoubleFilterGeneratorChild.class, fetch = FetchType.EAGER)
        @JoinColumn(name = "DOUBLE_PROP", insertable = false, updatable = false)
        @FilterGenerator(attributeName = "doubleProp", attributeValue = "10")
        private PrimitiveDoubleFilterGeneratorChild doublePropChild;

        public double getDoubleProp() {
            return doubleProp;
        }

        public void setDoubleProp(double doubleProp) {
            this.doubleProp = doubleProp;
        }

        public PrimitiveDoubleFilterGeneratorChild getDoublePropChild() {
            return doublePropChild;
        }

        public void setDoublePropChild(PrimitiveDoubleFilterGeneratorChild doublePropChild) {
            this.doublePropChild = doublePropChild;
        }

    }

    /**
     * Simple child entity to test primitive double mappings.
     */
    @Entity
    @Table(name = "KRTST_TYP_CHILD_T")
    public static class PrimitiveDoubleFilterGeneratorChild {

        @Id
        @Column(name = "DOUBLE_PROP")
        private double doubleProp;

        public double getDoubleProp() {
            return doubleProp;
        }

        public void setDoubleProp(double doubleProp) {
            this.doubleProp = doubleProp;
        }

    }

    /**
     * Simple parent entity to test wrapper double mappings.
     */
    @Entity
    @Table(name = "KRTST_TYP_PARENT_T")
    public static class WrapperDoubleFilterGeneratorParent {

        @Id
        @Column(name = "DOUBLE_PROP")
        private Double doubleProp;

        @ManyToOne(targetEntity = WrapperDoubleFilterGeneratorChild.class, fetch = FetchType.EAGER)
        @JoinColumn(name = "DOUBLE_PROP", insertable = false, updatable = false)
        @FilterGenerator(attributeName = "doubleProp", attributeValue = "10")
        private WrapperDoubleFilterGeneratorChild doublePropChild;

        public Double getDoubleProp() {
            return doubleProp;
        }

        public void setDoubleProp(Double doubleProp) {
            this.doubleProp = doubleProp;
        }

        public WrapperDoubleFilterGeneratorChild getDoublePropChild() {
            return doublePropChild;
        }

        public void setDoublePropChild(WrapperDoubleFilterGeneratorChild doublePropChild) {
            this.doublePropChild = doublePropChild;
        }

    }

    /**
     * Simple child entity to test wrapper double mappings.
     */
    @Entity
    @Table(name = "KRTST_TYP_CHILD_T")
    public static class WrapperDoubleFilterGeneratorChild {

        @Id
        @Column(name = "DOUBLE_PROP")
        private Double doubleProp;

        public Double getDoubleProp() {
            return doubleProp;
        }

        public void setDoubleProp(Double doubleProp) {
            this.doubleProp = doubleProp;
        }

    }

    private DataObjectService getDataObjectService() {
        return KRADServiceLocator.getDataObjectService();
    }
}