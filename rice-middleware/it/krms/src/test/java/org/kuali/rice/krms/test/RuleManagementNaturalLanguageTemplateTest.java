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
import org.kuali.rice.krms.api.repository.language.NaturalLanguageTemplate;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 *   RuleManagementNaturalLanguageTemplateTest is to test the methods of
 *       ruleManagementServiceImpl relating to NaturalLanguageTemplates
 *
 *   Each test focuses on one of the methods.
 */
public class RuleManagementNaturalLanguageTemplateTest extends RuleManagementBaseTest {

    /**
     *  Test testCreateNaturalLanguageTemplate()
     *
     *  This test focuses specifically on the RuleManagementServiceImpl .createNaturalLanguageTemplate(NaturalLanguageTemplate) method
     */
    @Test
    public void testCreateNaturalLanguageTemplate() {
        // validate that the NaturalLanguageTemplate being build, does not already exist
        assertNull(ruleManagementServiceImpl.getNaturalLanguageTemplate("en-reqActive"));

        // build a NaturalLanguageTemplate for testing
        NaturalLanguageTemplate template = buildTestNaturalLanguageTemplate("KR-TEST", "en", "reqActive", "Must not be inActive");

        // validate the resulting object
        assertNotNull(ruleManagementServiceImpl.getNaturalLanguageTemplate("en-reqActive"));
        assertEquals("Unexpected language code found", "en", template.getLanguageCode());
        assertEquals("Unexpected template found", "Must not be inActive", template.getTemplate());
        assertEquals("Unexpected TypeId value",  krmsTypeRepository.getTypeByName("KR-TEST", "reqActive").getId(), template.getTypeId());
        assertEquals("Unexpected Active value", true, template.isActive());

        // try to create a NaturalLanguageTemplate with null languageCode
        try {
            NaturalLanguageTemplate.Builder.create(null,template.getNaturalLanguageUsageId(),"Ky objekt nuk duhet të jetë joaktive",template.getTypeId());
            fail("Should have thrown IllegalArgumentException: languageCode is null or blank");
        } catch (IllegalArgumentException e ) {
            // throws IllegalArgumentException: languageCode is null or blank
        }

        // try to create a NaturalLanguageTemplate with blank languageCode
        try {
            NaturalLanguageTemplate.Builder.create("  ",template.getNaturalLanguageUsageId(),"Objektu hau ezin da ez-aktiboak",template.getTypeId());
            fail("Should have thrown IllegalArgumentException: languageCode is null or blank");
        } catch (IllegalArgumentException e ) {
            // throws IllegalArgumentException: languageCode is null or blank
        }

        // try to create a NaturalLanguageTemplate with null  naturalLanguageUsageId
        try {
            NaturalLanguageTemplate.Builder.create("it",null,"Questo oggetto non deve essere inattivo",template.getTypeId());
            fail("Should have thrown IllegalArgumentException: naturalLanguageUsageId is null or blank");
        } catch (IllegalArgumentException e ) {
            // throws IllegalArgumentException: naturalLanguageUsageId is null or blank
        }

        // try to create a NaturalLanguageTemplate with blank naturalLanguageUsageId
        try {
            NaturalLanguageTemplate.Builder.create("ja","  ","このオブジェクトは、非アクティブにすることはできません",template.getTypeId());
            fail("Should have thrown IllegalArgumentException: naturalLanguageUsageId is null or blank");
        } catch (IllegalArgumentException e ) {
            // throws IllegalArgumentException: naturalLanguageUsageId is null or blank
        }

        // try to create a NaturalLanguageTemplate with null template
        try {
            NaturalLanguageTemplate.Builder.create("az",template.getNaturalLanguageUsageId(),null,template.getTypeId());
            fail("Should have thrown IllegalArgumentException: template is null or blank");
        } catch (IllegalArgumentException e ) {
            // throws IllegalArgumentException: template is null or blank
        }

        // try to create a NaturalLanguageTemplate with blank template
        try {
            NaturalLanguageTemplate.Builder.create("bg",template.getNaturalLanguageUsageId(),"   ",template.getTypeId());
            fail("Should have thrown IllegalArgumentException: template is null or blank");
        } catch (IllegalArgumentException e ) {
            // throws IllegalArgumentException: template is null or blank
        }

        // try to create a NaturalLanguageTemplate with null typeId
        try {
            NaturalLanguageTemplate.Builder.create("hr",template.getNaturalLanguageUsageId(),"Ovaj objekt ne smije biti neaktivna",null);
            fail("Should have thrown IllegalArgumentException: typeId is null or blank");
        } catch (IllegalArgumentException e ) {
            // throws IllegalArgumentException: typeId is null or blank
        }

        // try to create a NaturalLanguageTemplate with blank typeId
        try {
            NaturalLanguageTemplate.Builder.create("cs",template.getNaturalLanguageUsageId(),"Tento objekt nesmí být neaktivní","  ");
            fail("Should have thrown IllegalArgumentException: typeId is null or blank");
        } catch (IllegalArgumentException e ) {
            // throws IllegalArgumentException: typeId is null or blank
        }

        // try to create a NaturalLanguageTemplate with invalid typeId
        NaturalLanguageTemplate.Builder naturalLanguageTemplateBuilder = NaturalLanguageTemplate.Builder.create("da",template.getNaturalLanguageUsageId(),"Dette formål må ikke være inaktiv","badId");
        naturalLanguageTemplateBuilder.setId("da" + "-" + "reqActive");
        try {
            ruleManagementServiceImpl.createNaturalLanguageTemplate(naturalLanguageTemplateBuilder.build());
            fail("Should have thrown DataIntegrityViolationException: OJB operation; SQL []; Cannot add or update a child row: a foreign key constraint fails");
        } catch (DataIntegrityViolationException e) {
            // throws DataIntegrityViolationException: OJB operation; SQL []; Cannot add or update a child row: a foreign key constraint fails
        }
    }

    /**
     *  Test testGetNaturalLanguageTemplate()
     *
     *  This test focuses specifically on the RuleManagementServiceImpl .getNaturalLanguageTemplate("naturalLanguageTemplateId") method
     */
    @Test
    public void testGetNaturalLanguageTemplate() {
        NaturalLanguageTemplate template = buildTestNaturalLanguageTemplate("KR-TEST", "sw", "reqActive", "Detta ändamål får inte vara inaktiv");
        assertNotNull(ruleManagementServiceImpl.getNaturalLanguageTemplate("sw-reqActive"));
        assertEquals("Unexpected language code found", "sw", template.getLanguageCode());
        // try to getNaturalLanguageTemplate with null value
        try {
            ruleManagementServiceImpl.getNaturalLanguageTemplate(null);
            fail("Should have thrown IllegalArgumentException: naturalLanguageTemplateId was null");
        } catch (IllegalArgumentException e) {
            //throws g.IllegalArgumentException: naturalLanguageTemplateId was null
        }

        // try to getNaturalLanguageTemplate with blank value
        try {
            ruleManagementServiceImpl.getNaturalLanguageTemplate("  ");
            fail("Should have thrown IllegalArgumentException: naturalLanguageTemplateId was blank");
        } catch (IllegalArgumentException e) {
            //throws IllegalArgumentException: naturalLanguageTemplateId was blank
        }

        assertNull(ruleManagementServiceImpl.getNaturalLanguageTemplate("badId"));
    }

    /**
     *  Test testUpdateNaturalLanguageTemplate()
     *
     *  This test focuses specifically on the RuleManagementServiceImpl .updateNaturalLanguageTemplate(NaturalLanguageTemplate) method
     */
    @Test
    public void testUpdateNaturalLanguageTemplate() {
        NaturalLanguageTemplate template = buildTestNaturalLanguageTemplate("KR-TEST", "pl", "reqActive", "Isthay Objectway ustmay otnay ebay inActiveway");
        NaturalLanguageTemplate.Builder naturalLanguageTemplateBuilder = NaturalLanguageTemplate.Builder.create(
                ruleManagementServiceImpl.getNaturalLanguageTemplate("pl-reqActive"));
        // update the template value  (pl is the lang_cd for polish not pig-latin so update template)
        naturalLanguageTemplateBuilder.setTemplate("Ten obiekt nie moze byc nieaktywne");
        naturalLanguageTemplateBuilder.setActive(false);
        ruleManagementServiceImpl.updateNaturalLanguageTemplate(naturalLanguageTemplateBuilder.build());

        assertEquals("Unexpected template value found", "Ten obiekt nie moze byc nieaktywne", ruleManagementServiceImpl.getNaturalLanguageTemplate("pl-reqActive").getTemplate());
        assertEquals("Unexpected isActive value found", false, ruleManagementServiceImpl.getNaturalLanguageTemplate("pl-reqActive").isActive());
    }

    /**
     *  Test testDeleteNaturalLanguageTemplate()
     *
     *  This test focuses specifically on the RuleManagementServiceImpl .deleteNaturalLanguageTemplate("naturalLanguageTemplateId") method
     */
    @Test
    public void testDeleteNaturalLanguageTemplate() {
        NaturalLanguageTemplate template = buildTestNaturalLanguageTemplate("KR-TEST", "pt", "reqActive", "Este objeto nao deve ser inativo");
        assertNotNull("Should have found NaturalLanguageTemplate", ruleManagementServiceImpl.getNaturalLanguageTemplate("pt-reqActive"));

        ruleManagementServiceImpl.deleteNaturalLanguageTemplate("pt-reqActive");

        // verify it was deleted
        assertNull("Should not have found NaturalLanguageTemplate",ruleManagementServiceImpl.getNaturalLanguageTemplate("pt-reqActive"));

        // test delete using null
        try {
            ruleManagementServiceImpl.deleteNaturalLanguageTemplate(null);
            fail("Should have thrown IllegalArgumentException: naturalLanguageTemplateId was null");
        } catch (IllegalArgumentException e) {
            // throws IllegalArgumentException: naturalLanguageTemplateId was null
        }

        // test delete using blank
        try {
            ruleManagementServiceImpl.deleteNaturalLanguageTemplate("    ");
            fail("Should have thrown IllegalArgumentException: naturalLanguageTemplateId was blank");
        } catch (IllegalArgumentException e) {
            // throws IllegalArgumentException: naturalLanguageTemplateId was blank
        }

        // test delete using bad value
        try {
            ruleManagementServiceImpl.deleteNaturalLanguageTemplate("badValue");
            fail("Should have thrown IllegalStateException: the NaturalLanguageTemplate to delete does not exists: badValue");
        } catch (IllegalStateException e) {
            // throws IllegalStateException: the NaturalLanguageTemplate to delete does not exists: badValue
        }
    }


    /**
     *  Test testFindNaturalLanguageTemplatesByLanguageCode()
     *
     *  This test focuses specifically on the RuleManagementServiceImpl .findNaturalLanguageTemplatesByLanguageCode("languageCode") method
     */
    @Test
    public void testFindNaturalLanguageTemplatesByLanguageCode() {
        NaturalLanguageTemplate template = buildTestNaturalLanguageTemplate("KR-TEST", "ro", "reqActive", "Acest obiect nu trebuie sa fie inactiv");
        assertNotNull("Should have found NaturalLanguageTemplate", ruleManagementServiceImpl.getNaturalLanguageTemplate("ro-reqActive"));

        List<NaturalLanguageTemplate> nlTemplates = ruleManagementServiceImpl.findNaturalLanguageTemplatesByLanguageCode("ro");
        assertEquals("Unexpected number of templates returned ",1,nlTemplates.size());
        assertEquals("Unexpected template id returned","ro-reqActive",nlTemplates.get(0).getId());

        // test find with null LanguageCode
        try {
            ruleManagementServiceImpl.findNaturalLanguageTemplatesByLanguageCode(null);
            fail("Should have thrown IllegalArgumentException: languageCode is null or blank");
        } catch (IllegalArgumentException e) {
            // throws IllegalArgumentException: languageCode is null or blank
        }

        // test find with blank LanguageCode
        try {
            ruleManagementServiceImpl.findNaturalLanguageTemplatesByLanguageCode("  ");
            fail("Should have thrown IllegalArgumentException: languageCode is null or blank");
        } catch (IllegalArgumentException e) {
            // throws IllegalArgumentException: languageCode is null or blank
        }

        // test find with non-existing LanguageCode
        assertEquals("Unexpected number of templates returned ",0,
                ruleManagementServiceImpl.findNaturalLanguageTemplatesByLanguageCode("badValue").size());
    }


    /**
     *  Test testFindNaturalLanguageTemplateByLanguageCodeTypeIdAndNluId()
     *
     *  This test focuses specifically on the RuleManagementServiceImpl
     *     .findNaturalLanguageTemplateByLanguageCodeTypeIdAndNluId(String languageCode, String typeId, String naturalLanguageUsageId) method
     */
    @Test
    public void testFindNaturalLanguageTemplateByLanguageCodeTypeIdAndNluId() {
        NaturalLanguageTemplate template = buildTestNaturalLanguageTemplate("KR-TEST", "sk", "reqActive", "Tento objekt nesmie byt neaktívne");
        assertNotNull("Should have found NaturalLanguageTemplate", ruleManagementServiceImpl.getNaturalLanguageTemplate("sk-reqActive"));

        // test find
        template = ruleManagementServiceImpl.findNaturalLanguageTemplateByLanguageCodeTypeIdAndNluId(
                template.getLanguageCode(),template.getTypeId(),template.getNaturalLanguageUsageId());

        // validate the returned object
        assertEquals("Unexpected template id returned","sk-reqActive",template.getId());
        assertEquals("Unexpected language code found", "sk", template.getLanguageCode());
        assertEquals("Unexpected template found", "Tento objekt nesmie byt neaktívne", template.getTemplate());
        assertEquals("Unexpected TypeId value",  krmsTypeRepository.getTypeByName("KR-TEST", "reqActive").getId(), template.getTypeId());
        // change test to true after isActive is functional KULRICE-10653
        assertEquals("Unexpected Active value", false, template.isActive());

        // test find with null language code
        try {
             ruleManagementServiceImpl.findNaturalLanguageTemplateByLanguageCodeTypeIdAndNluId(
                null,template.getTypeId(),template.getNaturalLanguageUsageId());
             fail("Should have thrown IllegalArgumentException: languageCode is null or blank");
        } catch (IllegalArgumentException e) {
            // throws IllegalArgumentException: languageCode is null or blank
        }

        // test find with null TypeId
        ruleManagementServiceImpl.findNaturalLanguageTemplateByLanguageCodeTypeIdAndNluId(
                template.getLanguageCode(),null,template.getNaturalLanguageUsageId());

        // test find with null NaturalLanguageUsageId
        ruleManagementServiceImpl.findNaturalLanguageTemplateByLanguageCodeTypeIdAndNluId(
                template.getLanguageCode(),template.getTypeId(),null);

        // test find with blank language code
        try {
            ruleManagementServiceImpl.findNaturalLanguageTemplateByLanguageCodeTypeIdAndNluId(
                    "  ",template.getTypeId(),template.getNaturalLanguageUsageId());
            fail("Should have thrown IllegalArgumentException: languageCode is null or blank");
        } catch (IllegalArgumentException e) {
            // throws IllegalArgumentException: languageCode is null or blank
        }
    }


    /**
     *  Test testFindNaturalLanguageTemplateByLanguageCodeTypeIdAndNluId()
     *
     *  This test focuses specifically on the RuleManagementServiceImpl
     *     .findNaturalLanguageTemplateByLanguageCodeTypeIdAndNluId(String languageCode, String typeId, String naturalLanguageUsageId) method
     */
    @Test
    public void testFindNaturalLanguageTemplatesByNaturalLanguageUsage() {
        // build test template (change seed value reqActive to reqActive-SL to discriminate nl usage from other tests
        NaturalLanguageTemplate template = buildTestNaturalLanguageTemplate("KR-TEST", "sl", "reqActive-SL", "Ta predmet ne sme biti neaktiven");
        assertNotNull("Should have found NaturalLanguageTemplate", ruleManagementServiceImpl.getNaturalLanguageTemplate("sl-reqActive-SL"));

        // test find
        List<NaturalLanguageTemplate> templates = ruleManagementServiceImpl.findNaturalLanguageTemplatesByNaturalLanguageUsage(template.getNaturalLanguageUsageId());
        assertEquals("Unexpected number of templates returned ",1,templates.size());
        template = templates.get(0);

        // validate the returned object
        assertEquals("Unexpected template id returned","sl-reqActive-SL",template.getId());
        assertEquals("Unexpected language code found", "sl", template.getLanguageCode());
        assertEquals("Unexpected template found", "Ta predmet ne sme biti neaktiven", template.getTemplate());
        assertEquals("Unexpected TypeId value",  krmsTypeRepository.getTypeByName("KR-TEST", "reqActive-SL").getId(), template.getTypeId());
        // change test to true after isActive is functional KULRICE-10653
        assertEquals("Unexpected Active value", false, template.isActive());

        // test find with null NaturalLanguageUsage
        try {
            ruleManagementServiceImpl.findNaturalLanguageTemplatesByNaturalLanguageUsage(null);
            fail("Should have thrown IllegalArgumentException: naturalLanguageUsageId is null or blank");
        } catch (IllegalArgumentException e) {
            // throws IllegalArgumentException: naturalLanguageUsageId is null or blank
        }

        // test find with blank NaturalLanguageUsage
        try {
            ruleManagementServiceImpl.findNaturalLanguageTemplatesByNaturalLanguageUsage("   ");
            fail("Should have thrown IllegalArgumentException: naturalLanguageUsageId is null or blank");
        } catch (IllegalArgumentException e) {
            // throws IllegalArgumentException: naturalLanguageUsageId is null or blank
        }

        // test find with nonexistant NaturalLanguageUsage code
        assertEquals("Unexpected number of templates returned ",0,
                ruleManagementServiceImpl.findNaturalLanguageTemplatesByNaturalLanguageUsage("badValue").size());
    }


    /**
     *  Test testFindNaturalLanguageTemplatesByType()
     *
     *  This test focuses specifically on the RuleManagementServiceImpl
     *     .findNaturalLanguageTemplatesByType(String typeId) method
     */
    @Test
    public void testFindNaturalLanguageTemplatesByType() {
        // build test template (change seed value reqActive to reqActive-SL to discriminate "Type" from other tests
        NaturalLanguageTemplate template = buildTestNaturalLanguageTemplate("KR-TEST", "es", "reqActive-ES", "Este objeto no debe estar inactivo");
        assertNotNull("Should have found NaturalLanguageTemplate", ruleManagementServiceImpl.getNaturalLanguageTemplate("es-reqActive-ES"));

        // test find
        List<NaturalLanguageTemplate> templates = ruleManagementServiceImpl.findNaturalLanguageTemplatesByType(template.getTypeId());
        assertEquals("Unexpected number of templates returned ",1,templates.size());

        // test find with null typeId
        try {
            ruleManagementServiceImpl.findNaturalLanguageTemplatesByType(null);
            fail("Should have thrown IllegalArgumentException: typeId is null or blank");
        } catch (IllegalArgumentException e) {
            // throws IllegalArgumentException: typeId is null or blank
        }

        // test find with null typeId
        try {
            ruleManagementServiceImpl.findNaturalLanguageTemplatesByType("   ");
            fail("Should have thrown IllegalArgumentException: typeId is null or blank");
        } catch (IllegalArgumentException e) {
            // throws IllegalArgumentException: typeId is null or blank
        }

        // test find non-existent  value for typeId
        assertEquals("Unexpected number of templates returned ",0,
                ruleManagementServiceImpl.findNaturalLanguageTemplatesByType("badValue").size());
    }


    /**
     *  Test testFindNaturalLanguageTemplatesByTemplate()
     *
     *  This test focuses specifically on the RuleManagementServiceImpl
     *     .findNaturalLanguageTemplatesByTemplate(String template) method
     */
    @Test
    public void testFindNaturalLanguageTemplatesByTemplate() {
        NaturalLanguageTemplate template = buildTestNaturalLanguageTemplate("KR-TEST", "sv", "reqActive", "Detta ändamal far inte vara inaktiv");
        assertNotNull("Should have found NaturalLanguageTemplate", ruleManagementServiceImpl.getNaturalLanguageTemplate("sv-reqActive"));

        // test find
        List<NaturalLanguageTemplate> templates =
                ruleManagementServiceImpl.findNaturalLanguageTemplatesByTemplate("Detta ändamal far inte vara inaktiv");
        assertEquals("Unexpected number of templates returned ",1,templates.size());

        // test find with null Template
        try {
            ruleManagementServiceImpl.findNaturalLanguageTemplatesByTemplate(null);
            fail("Should have thrown IllegalArgumentException: template is null or blank");
        } catch (IllegalArgumentException e) {
            // throws IllegalArgumentException: template is null or blank
        }

        // test find with null Template
        try {
            ruleManagementServiceImpl.findNaturalLanguageTemplatesByTemplate("   ");
            fail("Should have thrown IllegalArgumentException: template is null or blank");
        } catch (IllegalArgumentException e) {
            // throws IllegalArgumentException: template is null or blank
        }

        // test find non-existent value for Template
        assertEquals("Unexpected number of templates returned ",0,
                ruleManagementServiceImpl.findNaturalLanguageTemplatesByTemplate("badValue").size());
    }
}
