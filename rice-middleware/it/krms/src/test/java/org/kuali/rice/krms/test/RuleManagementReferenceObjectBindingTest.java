/*
 * Copyright 2006-2013 The Kuali Foundation
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

package org.kuali.rice.krms.test;

import org.junit.Test;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.krms.api.repository.reference.ReferenceObjectBinding;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.kuali.rice.core.api.criteria.PredicateFactory.equal;
import static org.kuali.rice.core.api.criteria.PredicateFactory.in;

/**
 *
 */
public class RuleManagementReferenceObjectBindingTest extends RuleManagementBaseTest {
    ////
    //// reference object binding methods
    ////

    @Test
    public void testCreateReferenceObjectBinding() {
        ReferenceObjectBinding.Builder refObjBindingBuilder =  buildReferenceObjectBinding("6000");
        ReferenceObjectBinding refObjBinding = ruleManagementServiceImpl.getReferenceObjectBinding(refObjBindingBuilder.getId());
        refObjBindingBuilder =  ReferenceObjectBinding.Builder.create(refObjBinding);

        assertNotNull("Created ReferenceObjectBinding not found", refObjBindingBuilder);
        assertEquals("Invalid CollectionName of refObjBindingBuilder found","ParkingPolicies", refObjBindingBuilder.getCollectionName());
        assertEquals("Invalid KrmsObjectId of refObjBindingBuilder found","AgendaId6000", refObjBindingBuilder.getKrmsObjectId());
        assertEquals("Invalid KrmsDiscriminatorType of refObjBindingBuilder found",
                krmsTypeRepository.getTypeByName("Namespace6000", "AgendaType6000").getId(), refObjBindingBuilder.getKrmsDiscriminatorType() );
        assertEquals("Invalid Namespace of refObjBindingBuilder found","Namespace6000", refObjBindingBuilder.getNamespace());
        assertEquals("Invalid ReferenceObjectId of refObjBindingBuilder found","PA6000", refObjBindingBuilder.getReferenceObjectId());
        assertEquals("Invalid ReferenceDiscriminatorType  of refObjBindingBuilder found","ParkingAffiliationType", refObjBindingBuilder.getReferenceDiscriminatorType());
        assertEquals("Invalid Active value of refObjBindingBuilder found",true, refObjBindingBuilder.isActive());

        try {
            ruleManagementServiceImpl.createReferenceObjectBinding(refObjBindingBuilder.build());
            fail("Should have thrown IllegalStateException: the ReferenceObjectBinding to create already exists");
        } catch (Exception e) {
            // throws IllegalStateException: the ReferenceObjectBinding to create already exists
        }

        refObjBindingBuilder.setId("RefObjBind6000");
        try {
            ruleManagementServiceImpl.createReferenceObjectBinding(refObjBindingBuilder.build());
            fail("Should have thrown DataIntegrityViolationException: OJB operation; SQL []; Duplicate entry");
        } catch (Exception e) {
            // throws DataIntegrityViolationException: OJB operation; SQL []; Duplicate entry
        }
    }


    @Test
    public void testGetReferenceObjectBinding() {
        ReferenceObjectBinding.Builder refObjBindingBuilder =  buildReferenceObjectBinding("6001");

        ReferenceObjectBinding refObjBinding = ruleManagementServiceImpl.getReferenceObjectBinding(refObjBindingBuilder.getId());
        assertNotNull("ReferenceObjectBinding should have been returned",refObjBinding);
        assertEquals("Incorrect value found on returned ReferenceObjectBinding","PA6001",refObjBinding.getReferenceObjectId());

        try {
            refObjBinding = ruleManagementServiceImpl.getReferenceObjectBinding(null);
            fail("Should have thrown IllegalArgumentException: referenceObjectBindingId was null");
        } catch (Exception e) {
            // throws IllegalArgumentException: referenceObjectBindingId was null
        }

        assertNull("ReferenceObjectBinding should not have been found",ruleManagementServiceImpl.getReferenceObjectBinding("junk_value"));

    }


    @Test
    public void testGetReferenceObjectBindings() {
        List<String> referenceObjectBindingIds = new ArrayList<String>();
        referenceObjectBindingIds.add(buildReferenceObjectBinding("6002").getId());
        referenceObjectBindingIds.add(buildReferenceObjectBinding("6003").getId());
        referenceObjectBindingIds.add(buildReferenceObjectBinding("6004").getId());

        List<ReferenceObjectBinding> referenceObjectBindings = ruleManagementServiceImpl.getReferenceObjectBindings(referenceObjectBindingIds);
        int objectsFound = 0;
        for ( ReferenceObjectBinding referenceObjectBinding : referenceObjectBindings) {
            if ( referenceObjectBindingIds.contains( referenceObjectBinding.getId())) {
                objectsFound++;
            } else {
                fail("Unexpected object returned");
            }
        }
        assertEquals("Expected number of objects not returned",3,objectsFound);

        try {
            ruleManagementServiceImpl.getReferenceObjectBindings(null);
            fail("Should have thrown IllegalArgumentException: reference binding object ids must not be null");
        } catch (Exception e) {
            // throws IllegalArgumentException: reference binding object ids must not be null
        }

        assertEquals("Incorrect number of objects returned", 0, ruleManagementServiceImpl.getReferenceObjectBindings(new ArrayList<String>()).size());

        // try requesting a list of objects with a bad value in it
        referenceObjectBindingIds.add("junkValue");
        referenceObjectBindings = ruleManagementServiceImpl.getReferenceObjectBindings(referenceObjectBindingIds);
        objectsFound = 0;
        for ( ReferenceObjectBinding referenceObjectBinding : referenceObjectBindings) {
            if ( referenceObjectBindingIds.contains( referenceObjectBinding.getId())) {
                objectsFound++;
            } else {
                fail("Unexpected object returned");
            }
        }
        assertEquals("Expected number of objects not returned",3,objectsFound);

    }


    @Test
    public void testFindReferenceObjectBindingsByReferenceObject() {
        ReferenceObjectBinding.Builder refObjBindingBuilder =  buildReferenceObjectBinding("6005");

        //assertEquals("",refObjBindingBuilder.getId(),
        List<ReferenceObjectBinding> referenceObjectBindings = ruleManagementServiceImpl.findReferenceObjectBindingsByReferenceObject(refObjBindingBuilder.getReferenceDiscriminatorType(),refObjBindingBuilder.getReferenceObjectId());
        assertEquals("Incorrect number of objects returned",1,referenceObjectBindings.size());

        try {
            ruleManagementServiceImpl.findReferenceObjectBindingsByReferenceObject(null,refObjBindingBuilder.getReferenceObjectId());
            fail("should have thrown RiceIllegalArgumentException: reference binding object discriminator type must not be null");
        } catch (Exception e) {
            // throws RiceIllegalArgumentException: reference binding object discriminator type must not be null
        }

        try {
            ruleManagementServiceImpl.findReferenceObjectBindingsByReferenceObject(refObjBindingBuilder.getReferenceDiscriminatorType(),null);
            fail("should have thrown RiceIllegalArgumentException: reference object id must not be null");
        } catch (Exception e) {
            // throws RiceIllegalArgumentException: reference object id must not be null
        }

        referenceObjectBindings = ruleManagementServiceImpl.findReferenceObjectBindingsByReferenceObject("junkvalue","junkvalue");
        assertEquals("Incorrect number of objects returned",0,referenceObjectBindings.size());
    }


    @Test
    public void testFindReferenceObjectBindingsByReferenceDiscriminatorType() {
        // create two ReferenceObjectBindings with same ReferenceDiscriminatorType
        ReferenceObjectBinding.Builder refObjBindingBuilder =  buildReferenceObjectBinding("6006");
        refObjBindingBuilder.setReferenceDiscriminatorType("ParkingAffiliationType6006");
        ruleManagementServiceImpl.updateReferenceObjectBinding(refObjBindingBuilder.build());

        refObjBindingBuilder =  buildReferenceObjectBinding("6007");
        refObjBindingBuilder.setReferenceDiscriminatorType("ParkingAffiliationType6006");
        ruleManagementServiceImpl.updateReferenceObjectBinding(refObjBindingBuilder.build());

        List<ReferenceObjectBinding> referenceObjectBindings = ruleManagementServiceImpl.findReferenceObjectBindingsByReferenceDiscriminatorType(refObjBindingBuilder.getReferenceDiscriminatorType());
        assertEquals("Incorrect number of objects returned",2,referenceObjectBindings.size());

        // check with blank ReferenceDiscriminatorType
        try {
            ruleManagementServiceImpl.findReferenceObjectBindingsByReferenceDiscriminatorType("   ");
            fail("Should have thrown IllegalArgumentException: referenceDiscriminatorType is null or blank");
        } catch (Exception e) {
            // throws IllegalArgumentException: referenceDiscriminatorType is null or blank
        }

        // check with null ReferenceDiscriminatorType
        try {
            ruleManagementServiceImpl.findReferenceObjectBindingsByReferenceDiscriminatorType(null);
            fail("Should have thrown IllegalArgumentException: referenceDiscriminatorType is null or blank");
        } catch (Exception e) {
            // throws IllegalArgumentException: referenceDiscriminatorType is null or blank
        }
    }


    @Test
    public void testFindReferenceObjectBindingsByKrmsDiscriminatorType() {
        // create two ReferenceObjectBindings with same KrmsDiscriminatorType
        ReferenceObjectBinding.Builder refObjBindingBuilder6008 =  buildReferenceObjectBinding("6008");

        ReferenceObjectBinding.Builder refObjBindingBuilder6009 =  buildReferenceObjectBinding("6009");
        refObjBindingBuilder6009.setKrmsDiscriminatorType(refObjBindingBuilder6008.getKrmsDiscriminatorType());
        ruleManagementServiceImpl.updateReferenceObjectBinding(refObjBindingBuilder6009.build());

        List<ReferenceObjectBinding> referenceObjectBindings = ruleManagementServiceImpl.findReferenceObjectBindingsByKrmsDiscriminatorType(refObjBindingBuilder6008.getKrmsDiscriminatorType());
        assertEquals("Incorrect number of objects returned",2,referenceObjectBindings.size());

        // check with blank KrmsDiscriminatorType
        try {
            ruleManagementServiceImpl.findReferenceObjectBindingsByKrmsDiscriminatorType("   ");
            fail("Should have thrown IllegalArgumentException: krmsDiscriminatorType is null or blank");
        } catch (Exception e) {
            // throwsIllegalArgumentException: krmsDiscriminatorType is null or blank
        }

        // check with null KrmsDiscriminatorType
        try {
            ruleManagementServiceImpl.findReferenceObjectBindingsByKrmsDiscriminatorType(null);
            fail("Should have thrown IllegalArgumentException: krmsDiscriminatorType is null or blank");
        } catch (Exception e) {
            // throws IllegalArgumentException: krmsDiscriminatorType is null or blank
        }
    }


    @Test
    public void testFindReferenceObjectBindingsByKrmsObject() {
        // create two ReferenceObjectBindings with same KrmsObjectId
        ReferenceObjectBinding.Builder refObjBindingBuilder6008 =  buildReferenceObjectBinding("6010");

        ReferenceObjectBinding.Builder refObjBindingBuilder6009 =  buildReferenceObjectBinding("6011");
        refObjBindingBuilder6009.setKrmsObjectId(refObjBindingBuilder6008.getKrmsObjectId());
        ruleManagementServiceImpl.updateReferenceObjectBinding(refObjBindingBuilder6009.build());

        List<ReferenceObjectBinding> referenceObjectBindings = ruleManagementServiceImpl.findReferenceObjectBindingsByKrmsObject(
                refObjBindingBuilder6008.getKrmsObjectId());
        assertEquals("Incorrect number of objects returned",2,referenceObjectBindings.size());

        // check with blank KrmsObjectId
        try {
            ruleManagementServiceImpl.findReferenceObjectBindingsByKrmsObject("   ");
            fail("Should have thrown IllegalArgumentException: krmsObjectId is null or blank");
        } catch (Exception e) {
            // throwsIllegalArgumentException: krmsObjectId is null or blank
        }

        // check with null KrmsObjectId
        try {
            ruleManagementServiceImpl.findReferenceObjectBindingsByKrmsObject(null);
            fail("Should have thrown IllegalArgumentException: krmsObjectId is null or blank");
        } catch (Exception e) {
            // throws IllegalArgumentException: krmsObjectId is null or blank
        }
    }


    @Test
    public void testUpdateReferenceObjectBinding() {
        ReferenceObjectBinding.Builder refObjBindingBuilder =  buildReferenceObjectBinding("6012");

        ReferenceObjectBinding refObjBinding = ruleManagementServiceImpl.getReferenceObjectBinding(refObjBindingBuilder.getId());
        refObjBindingBuilder =  ReferenceObjectBinding.Builder.create(refObjBinding);

        // verify all current values
        assertNotNull("Created ReferenceObjectBinding not found", refObjBindingBuilder);
        assertEquals("Invalid CollectionName of refObjBindingBuilder found","ParkingPolicies", refObjBindingBuilder.getCollectionName());
        assertEquals("Invalid KrmsObjectId of refObjBindingBuilder found","AgendaId6012", refObjBindingBuilder.getKrmsObjectId());
        assertEquals("Invalid KrmsDiscriminatorType of refObjBindingBuilder found",
                krmsTypeRepository.getTypeByName("Namespace6012", "AgendaType6012").getId(), refObjBindingBuilder.getKrmsDiscriminatorType() );
        assertEquals("Invalid Namespace of refObjBindingBuilder found","Namespace6012", refObjBindingBuilder.getNamespace());
        assertEquals("Invalid ReferenceObjectId of refObjBindingBuilder found","PA6012", refObjBindingBuilder.getReferenceObjectId());
        assertEquals("Invalid ReferenceDiscriminatorType  of refObjBindingBuilder found","ParkingAffiliationType", refObjBindingBuilder.getReferenceDiscriminatorType());
        assertEquals("Invalid Active value of refObjBindingBuilder found",true, refObjBindingBuilder.isActive());

        // change everything but the id and submit update
        refObjBindingBuilder.setCollectionName("ParkingPolicies6012Changed");
        refObjBindingBuilder.setKrmsObjectId("AgendaId6012Changed");
        refObjBindingBuilder.setKrmsDiscriminatorType("KDTtype6012Changed");
        refObjBindingBuilder.setNamespace("Namespace6012Changed");
        refObjBindingBuilder.setReferenceObjectId("PA6012Changed");
        refObjBindingBuilder.setReferenceDiscriminatorType("ParkingAffiliationTypeChanged");
        refObjBindingBuilder.setActive(false);
        ruleManagementServiceImpl.updateReferenceObjectBinding(refObjBindingBuilder.build());

        // verify updated values
        refObjBinding = ruleManagementServiceImpl.getReferenceObjectBinding(refObjBindingBuilder.getId());
        refObjBindingBuilder =  ReferenceObjectBinding.Builder.create(refObjBinding);
        assertNotNull("Created ReferenceObjectBinding not found", refObjBindingBuilder);
        assertEquals("Invalid CollectionName of refObjBindingBuilder found", "ParkingPolicies6012Changed",
                refObjBindingBuilder.getCollectionName());
        assertEquals("Invalid KrmsObjectId of refObjBindingBuilder found","AgendaId6012Changed", refObjBindingBuilder.getKrmsObjectId());
        assertEquals("Invalid KrmsDiscriminatorType of refObjBindingBuilder found","KDTtype6012Changed", refObjBindingBuilder.getKrmsDiscriminatorType() );
        assertEquals("Invalid Namespace of refObjBindingBuilder found","Namespace6012Changed", refObjBindingBuilder.getNamespace());
        assertEquals("Invalid ReferenceObjectId of refObjBindingBuilder found","PA6012Changed", refObjBindingBuilder.getReferenceObjectId());
        assertEquals("Invalid ReferenceDiscriminatorType  of refObjBindingBuilder found","ParkingAffiliationTypeChanged", refObjBindingBuilder.getReferenceDiscriminatorType());
        assertEquals("Invalid Active value of refObjBindingBuilder found",false, refObjBindingBuilder.isActive());

        // update a object which does not exist
        refObjBindingBuilder.setId("junkValue6012");
        try {
            ruleManagementServiceImpl.updateReferenceObjectBinding(refObjBindingBuilder.build());
            fail("Should have thrown IllegalStateException: the ReferenceObjectBinding to update does not exists");
        } catch (Exception e) {
            // throws IllegalStateException: the ReferenceObjectBinding to update does not exists
        }
    }


    @Test
    public void testDeleteReferenceObjectBinding() {
        ReferenceObjectBinding.Builder refObjBindingBuilder =  buildReferenceObjectBinding("6013");
        ReferenceObjectBinding refObjBinding = ruleManagementServiceImpl.getReferenceObjectBinding(refObjBindingBuilder.getId());
        refObjBindingBuilder =  ReferenceObjectBinding.Builder.create(refObjBinding);

        assertNotNull("Created ReferenceObjectBinding not found", refObjBindingBuilder);
        ruleManagementServiceImpl.deleteReferenceObjectBinding(refObjBinding.getId());
        assertNull("Deleted ReferenceObjectBinding found", ruleManagementServiceImpl.getReferenceObjectBinding(
                refObjBindingBuilder.getId()));

        // try to delete it a second time
        try {
            ruleManagementServiceImpl.deleteReferenceObjectBinding(refObjBinding.getId());
            fail("should have thrown IllegalStateException: the ReferenceObjectBinding to delete does not exists");
        } catch (Exception e) {
            // throws IllegalStateException: the ReferenceObjectBinding to delete does not exists
        }

        // try to delete using null
        try {
            ruleManagementServiceImpl.deleteReferenceObjectBinding(null);
            fail("should have thrown IllegalArgumentException: referenceObjectBindingId was null");
        } catch (Exception e) {
            // throws IllegalArgumentException: referenceObjectBindingId was null
        }
    }


    @Test
    public void testFindReferenceObjectBindingIds() {
        // build three objects to search for.  Two active and one not active
        List<String> refObjBindingBuilderIds = new ArrayList<String>();
        ReferenceObjectBinding.Builder refObjBindingBuilder =  buildReferenceObjectBinding("6014");
        refObjBindingBuilderIds.add(refObjBindingBuilder.getId());
        refObjBindingBuilder =  buildReferenceObjectBinding("6015");
        refObjBindingBuilderIds.add(refObjBindingBuilder.getId());
        refObjBindingBuilder =  buildReferenceObjectBinding("6016");
        refObjBindingBuilderIds.add(refObjBindingBuilder.getId());
        refObjBindingBuilder.setActive(false);
        ruleManagementServiceImpl.updateReferenceObjectBinding(refObjBindingBuilder.build());

        // Find the three ReferenceObjectBindings by id list
        QueryByCriteria.Builder query = QueryByCriteria.Builder.create();
        query.setPredicates(in("id", refObjBindingBuilderIds.toArray(new String[]{})));

        List<String> referenceObjectBindingIds = ruleManagementServiceImpl.findReferenceObjectBindingIds(query.build());
        for (String referenceObjectBindingId : referenceObjectBindingIds ) {
            assertTrue(refObjBindingBuilderIds.contains(referenceObjectBindingId));
        }

        assertEquals("incorrect number of ReferenceObjectBindingIds found", 3, referenceObjectBindingIds.size());

        // find the two active ReferenceObjectBindings in the list
        query = QueryByCriteria.Builder.create();
        query.setPredicates( equal("active","Y"), in("id", refObjBindingBuilderIds.toArray(new String[]{})));

        referenceObjectBindingIds = ruleManagementServiceImpl.findReferenceObjectBindingIds(query.build());
        for (String referenceObjectBindingId : referenceObjectBindingIds ) {
            assertTrue(refObjBindingBuilderIds.contains(referenceObjectBindingId));
        }

        assertEquals("incorrect number of ReferenceObjectBindingIds found", 2, referenceObjectBindingIds.size());
    }
}
