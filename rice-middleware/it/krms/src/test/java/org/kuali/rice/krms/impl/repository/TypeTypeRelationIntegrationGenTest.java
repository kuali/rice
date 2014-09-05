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
package org.kuali.rice.krms.impl.repository;

import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.krms.api.repository.type.KrmsTypeDefinition;
import org.kuali.rice.krms.api.repository.typerelation.RelationshipType;
import org.kuali.rice.krms.api.repository.typerelation.TypeTypeRelation;
import org.kuali.rice.krms.test.AbstractBoTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public final class TypeTypeRelationIntegrationGenTest extends AbstractBoTest{
    private TypeTypeRelationBoServiceImpl typeTypeRelationBoServiceImpl;
    private TypeTypeRelation typeTypeRelation;
    private KrmsTypeBoServiceImpl krmsTypeBoServiceImpl;

    /**
     * Note lower case u, do not override superclasses setUp
     *
     */
    @Before
    public void setup() {
        typeTypeRelationBoServiceImpl = new TypeTypeRelationBoServiceImpl();
        typeTypeRelationBoServiceImpl.setDataObjectService(getDataObjectService());
        krmsTypeBoServiceImpl = new KrmsTypeBoServiceImpl();
        krmsTypeBoServiceImpl.setDataObjectService(getDataObjectService());
    }

    @Test
    public void test_from_null_yields_null() {
        (TypeTypeRelationBoServiceImplGenTest.create(typeTypeRelationBoServiceImpl)).test_from_null_yields_null();
    }

    @Test
    public void test_to() {
        (TypeTypeRelationBoServiceImplGenTest.create(typeTypeRelationBoServiceImpl)).test_to();
    }

    @Test
    public void test_createTypeTypeRelation() {
        KrmsTypeIntegrationGenTest krmsTypeTest = new KrmsTypeIntegrationGenTest();
        krmsTypeTest.setup(); // Note lowercase u
        krmsTypeTest.test_createKrmsType();
        KrmsTypeDefinition krmsType = krmsTypeTest.getKrmsType();
        TypeTypeRelationBoServiceImplGenTest test = TypeTypeRelationBoServiceImplGenTest.create(typeTypeRelationBoServiceImpl);
        test.createTypeTypeRelation(krmsType, krmsType); // TODO gen handle more than 1 of the same type
        typeTypeRelation = test.getTypeTypeRelation();
        assertNotNull(typeTypeRelation);
        assertNotNull(typeTypeRelation.getId());
    }

    @Test
    public void test_createTypeTypeRelationGeneratedId() {
        KrmsTypeIntegrationGenTest krmsTypeTest = new KrmsTypeIntegrationGenTest();
        krmsTypeTest.setup();
        krmsTypeTest.test_createKrmsType();
        KrmsTypeDefinition krmsType = krmsTypeTest.getKrmsType();
        TypeTypeRelationBoServiceImplGenTest test = TypeTypeRelationBoServiceImplGenTest.create(typeTypeRelationBoServiceImpl);
        test.createTypeTypeRelationGeneratedId(krmsType, krmsType);
        typeTypeRelation = test.getTypeTypeRelation();
        assertNotNull(typeTypeRelation);
        assertNotNull(typeTypeRelation.getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_findTypeTypeRelationsByFromType_null_fail() {
        (TypeTypeRelationBoServiceImplGenTest.create(typeTypeRelationBoServiceImpl)).test_findTypeTypeRelationsByFromType_null_fail();
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_findTypeTypeRelationsByToType_null_fail() {
        (TypeTypeRelationBoServiceImplGenTest.create(typeTypeRelationBoServiceImpl)).test_findTypeTypeRelationsByToType_null_fail();
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_findTypeTypeRelationsByRelationshipType_null_fail() {
        (TypeTypeRelationBoServiceImplGenTest.create(typeTypeRelationBoServiceImpl)).test_findTypeTypeRelationsByRelationshipType_null_fail();
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_findTypeTypeRelationsBySequenceNumber_null_fail() {
        (TypeTypeRelationBoServiceImplGenTest.create(typeTypeRelationBoServiceImpl)).test_findTypeTypeRelationsBySequenceNumber_null_fail();
    }

    @Test(expected = IllegalStateException.class)
    public void test_createTypeTypeRelation_fail_existing() {
        test_createTypeTypeRelation();
        test_createTypeTypeRelation();
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_createTypeTypeRelation_null_fail() {
        (TypeTypeRelationBoServiceImplGenTest.create(typeTypeRelationBoServiceImpl)).test_createTypeTypeRelation_null_fail();
    }

    @Test
    public void test_getTypeTypeRelation() {
        test_createTypeTypeRelation();
        TypeTypeRelation def = getTypeTypeRelation();
        TypeTypeRelation def2 = typeTypeRelationBoServiceImpl.getTypeTypeRelation(def.getId());
        assertNotNull(def2);
        assertEquals(def2, def);
    }

    @Test
    public void test_updateTypeTypeRelation() {
        test_createTypeTypeRelation();
        TypeTypeRelation def = getTypeTypeRelation();
        String id = def.getId();
        assertNotEquals(RelationshipType.USAGE_ALLOWED, def.getRelationshipType());
        TypeTypeRelationBo bo = typeTypeRelationBoServiceImpl.from(def);
        bo.setRelationshipType(RelationshipType.USAGE_ALLOWED);
        TypeTypeRelation updatedData = typeTypeRelationBoServiceImpl.updateTypeTypeRelation(TypeTypeRelation.Builder.create(bo).build());
        assertNotNull(updatedData);
        TypeTypeRelation def2 = typeTypeRelationBoServiceImpl.getTypeTypeRelation(id);
        assertEquals(RelationshipType.USAGE_ALLOWED,def2.getRelationshipType());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_updateTypeTypeRelation_null_fail() {
        (TypeTypeRelationBoServiceImplGenTest.create(typeTypeRelationBoServiceImpl)).test_updateTypeTypeRelation_null_fail();
    }

    @Test
    public void test_deleteTypeTypeRelation() {
        test_createTypeTypeRelation();
        TypeTypeRelation def = getTypeTypeRelation();
        String id = def.getId();
        typeTypeRelationBoServiceImpl.deleteTypeTypeRelation(id);
        TypeTypeRelation def2 = typeTypeRelationBoServiceImpl.getTypeTypeRelation(id);
        assert(def2 == null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_deleteTypeTypeRelation_null_fail() {
        (TypeTypeRelationBoServiceImplGenTest.create(typeTypeRelationBoServiceImpl)).test_deleteTypeTypeRelation_null_fail();
    }

    private TypeTypeRelation getTypeTypeRelation() {
        return typeTypeRelation;
    }
}
