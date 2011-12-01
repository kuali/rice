/*
 * Copyright 2006-2011 The Kuali Foundation
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

package org.kuali.rice.kew.docsearch.xml

import org.junit.Test
import org.kuali.rice.kew.api.extension.ExtensionDefinition
import org.kuali.rice.kew.api.KewApiConstants
import org.kuali.rice.kew.api.document.attribute.WorkflowAttributeDefinition
import static org.junit.Assert.assertEquals
import static org.junit.Assert.fail
import org.kuali.rice.kew.api.document.search.DocumentSearchCriteria
import org.junit.Ignore

/**
 * Tests the StandardGenericXMLSearchableAttribute class in isolation
 */
class StandardGenericXMLSearchableAttributeUnitTest {

    // this is sort of a crummy assertion, but describes the current behavior until we decide
    // to handle this error case more gracefully
    @Test(expected=RuntimeException) // NPE
    void testXmlConfigMustBeDefined() {
        testXmlConfigValidity(null)
        fail("expected error")
    }

    @Test(expected=RuntimeException) // SAX parse: Premature end of file
    void testXmlConfigMustBeNotBeEmpty() {
        testXmlConfigValidity("")
        fail("expected error")
    }

    @Test(expected=RuntimeException) // SAX parse: Content not allowed in prologue
    void testXmlConfigMustBeWellFormedy() {
        testXmlConfigValidity("I'm not valid XML")
        fail("expected error")
    }

    @Test
    void testDefaultXmlGenerationReturnsEmptyForNoFields() {
        testGenerateSearchContent("<validXml/>", "")
    }

    @Test
    void testDefaultXmlGenerationReturnsEmptyForNoValues() {
        testGenerateSearchContent("""
        <searchingConfig>
            <field name="def1"/>
            <field name="def2"/>
        </searchingConfig>
        """)
    }

    @Test
    void testCustomXmlGenerationReturnsEmptyForNoValues() {
        testGenerateSearchContent("""
        <searchingConfig>
            <xmlSearchContent>
                <myGeneratedContent>
                    <currentImplJustReturnsEmptyIfNoAttrParamsAreSupplied/>
                </myGeneratedContent>
            </xmlSearchContent>
            <field name="def1"/>
            <field name="def2"/>
        </searchingConfig>
        """)
    }

    @Test
    void testDefaultXmlGeneration() {
        testGenerateSearchContent("""
        <searchingConfig>
            <fieldDef name="def1"/>
            <fieldDef name="def2"/>
        </searchingConfig>
        """, """<xmlRouting><field name="def1"><value>val1</value></field></xmlRouting>""", [ def1: "val1" ])
    }

    @Test
    void testCustomXmlGeneration() {
        testGenerateSearchContent("""
        <searchingConfig>
            <xmlSearchContent>
                <myGeneratedContent>
                    <version>whatever</version>
                    <anythingIWant>Once upon a %def1%...</anythingIWant>
                    <conclusion>Happily ever %def2%.</conclusion>
                    <epilogue>this var isn't set %undefined%</epilogue>
                </myGeneratedContent>
            </xmlSearchContent>
            <fieldDef name="def1"/>
            <fieldDef name="def2"/>
        </searchingConfig>
        """, """
                <myGeneratedContent>
                    <version>whatever</version>
                    <anythingIWant>Once upon a val1...</anythingIWant>
                    <conclusion>Happily ever val2.</conclusion>
                    <epilogue>this var isn't set %undefined%</epilogue>
                </myGeneratedContent>
        """, [ def1: "val1", def2: "val2", third: "doesn't matter" ])
    }

    /* if no fields are defined, validation is moot */
    @Test void testDocumentAttributeCriteriaIsAlwaysValidWhenNoFieldsAreDefined() {
        def edb = ExtensionDefinition.Builder.create("test", KewApiConstants.SEARCHABLE_XML_ATTRIBUTE_TYPE, StandardGenericXMLSearchableAttribute.class.getName())
        edb.configuration.put(KewApiConstants.ATTRIBUTE_XML_CONFIG_DATA, "<validXml/>")
        def errors = new StandardGenericXMLSearchableAttribute().validateDocumentAttributeCriteria(edb.build(), DocumentSearchCriteria.Builder.create().build())
        assertEquals("unexpected validation errors", 0, errors.size())
    }

    private static final String STRING_FIELD_SEARCH_CONFIG = """
    <searchingConfig>
            <fieldDef name="givenname" title="First name">
                <display>
                    <type>text</type>
                </display>
                <visibility>
                    <column visible="true"/>
                </visibility>
                <validation required="true">
                    <regex>^[a-zA-Z ]+\$</regex>
                    <message>Invalid first name</message>
                </validation>
                <fieldEvaluation>
                    <xpathexpression>//putWhateverWordsIwantInsideThisTag/givenname/value</xpathexpression>
                </fieldEvaluation>
            </fieldDef>
            <xmlSearchContent>
                <putWhateverWordsIwantInsideThisTag>
                    <givenname>
                        <value>%givenname%</value>
                    </givenname>
                </putWhateverWordsIwantInsideThisTag>
            </xmlSearchContent>
        </searchingConfig>
    """

    @Test void testValidateDocumentAttributeCriteriaSimpleValue() {
        def edb = ExtensionDefinition.Builder.create("test", KewApiConstants.SEARCHABLE_XML_ATTRIBUTE_TYPE, StandardGenericXMLSearchableAttribute.class.getName())
        edb.configuration.put(KewApiConstants.ATTRIBUTE_XML_CONFIG_DATA, STRING_FIELD_SEARCH_CONFIG)
        def c = DocumentSearchCriteria.Builder.create();
        c.documentAttributeValues.put("givenname", [ "jack" ] as List<String>)
        def errors = new StandardGenericXMLSearchableAttribute().validateDocumentAttributeCriteria(edb.build(), c.build())
        assertEquals("unexpected validation errors", 0, errors.size())
    }

    @Test void testValidateDocumentAttributeCriteriaExpression() {
        def edb = ExtensionDefinition.Builder.create("test", KewApiConstants.SEARCHABLE_XML_ATTRIBUTE_TYPE, StandardGenericXMLSearchableAttribute.class.getName())
        edb.configuration.put(KewApiConstants.ATTRIBUTE_XML_CONFIG_DATA, STRING_FIELD_SEARCH_CONFIG)
        def c = DocumentSearchCriteria.Builder.create();
        c.documentAttributeValues.put("givenname", [ ">= jack" ] as List<String>)
        def errors = new StandardGenericXMLSearchableAttribute().validateDocumentAttributeCriteria(edb.build(), c.build())
        println errors
        assertEquals("unexpected validation errors", 0, errors.size())
    }

    @Test void testGetSearchFields() {
        def edb = ExtensionDefinition.Builder.create("test", KewApiConstants.SEARCHABLE_XML_ATTRIBUTE_TYPE, StandardGenericXMLSearchableAttribute.class.getName())
        edb.configuration.put(KewApiConstants.ATTRIBUTE_XML_CONFIG_DATA, STRING_FIELD_SEARCH_CONFIG)
        def fields= new StandardGenericXMLSearchableAttribute().getSearchFields(edb.build(), "not used")
        println fields
        // TODO: test something more substantial
        assertEquals(1, fields.size())
    }

    private static final String RANGE_FIELD_SEARCH_CONFIG = """
    <searchingConfig>
        <fieldDef name="givenname" title="First name">
            <display>
                <type>text</type>
            </display>
            <searchDefinition rangeSearch="true"/>
            <validation required="true">
                <regex>^[a-zA-Z ]+\$</regex>
                <message>Invalid first name</message>
            </validation>
            <fieldEvaluation>
                <xpathexpression>//putWhateverWordsIwantInsideThisTag/givenname/value</xpathexpression>
            </fieldEvaluation>
        </fieldDef>
        <xmlSearchContent>
            <putWhateverWordsIwantInsideThisTag>
                <givenname>
                    <value>%givenname%</value>
                </givenname>
            </putWhateverWordsIwantInsideThisTag>
        </xmlSearchContent>
    </searchingConfig>
    """

    @Test void testGetRangeSearchFields() {
        def edb = ExtensionDefinition.Builder.create("test", KewApiConstants.SEARCHABLE_XML_ATTRIBUTE_TYPE, StandardGenericXMLSearchableAttribute.class.getName())
        edb.configuration.put(KewApiConstants.ATTRIBUTE_XML_CONFIG_DATA, RANGE_FIELD_SEARCH_CONFIG)
        def fields= new StandardGenericXMLSearchableAttribute().getSearchFields(edb.build(), "not used")
        println fields
        // TODO: test something more substantial
        assertEquals(1, fields.size())
    }


    protected void testXmlConfigValidity(String xmlConfig) {
        testGenerateSearchContent(xmlConfig)
    }

    protected void testGenerateSearchContent(String config, String expected = null, params = [:]) {
        def edb = ExtensionDefinition.Builder.create("test", KewApiConstants.SEARCHABLE_XML_ATTRIBUTE_TYPE, StandardGenericXMLSearchableAttribute.class.getName())
        edb.configuration.put(KewApiConstants.ATTRIBUTE_XML_CONFIG_DATA, config)

        def ad = WorkflowAttributeDefinition.Builder.create("test_attr")
        params.each { k,v -> ad.addPropertyDefinition(k, v) }

        def generated = new StandardGenericXMLSearchableAttribute().generateSearchContent(edb.build(), "no document type", ad.build())
        if (expected) {
            assertEquals(expected.trim(), generated.trim())
        }
    }
}