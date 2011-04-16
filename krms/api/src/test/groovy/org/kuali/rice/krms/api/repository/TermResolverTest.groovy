/*
 * Copyright 2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krms.api.repository

import java.util.List;

import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller
import javax.xml.bind.Unmarshaller
import org.junit.Test
import org.junit.Assert
import org.kuali.rice.krms.api.engine.TermSpecification;
import org.kuali.rice.krms.api.repository.TermResolverDefinition.Builder;


/**
 * This class tests out the buiding of a KrmsTypeAttribute object.
 * It also tests XML marshalling / unmarshalling
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
class TermResolverTest {
	
	private static final String NAMESPACE = "KRMS_UNIT_TEST"
	
	private static final String ID="123TERMRESOLVER"
	private static final String NAMESPACE_CODE="foo-namespace"
	private static final String NAME="termResolver-name"
	private static final String TYPE_ID="1234TYPE"
	private static final Integer SEQUENCE_NUMBER_1 = new Integer(1)
	private static final String TERM_SPEC_CONTEXT_ID ="1234CONTEXT"
	private static final String TERM_SPEC_NAME="termSpec-name"
	private static final String TERM_SPEC_TYPE="term.spec.Type"
	private static final String ATTR_ID="123ATTR"
	private static final String ATTR_NAME="attrName"
	private static final String ATTR_DEF_ID ="123ATTR_DEF"
	private static final String ATTR_VALUE="attrValue"
	private static final String PARAM_NAME="paramName"
	
	private static final String EXPECTED_XML = """
	<TermResolverDefintion xmlns="http://rice.kuali.org/krms">
	    <id>123TERMRESOLVER</id>
	    <namespaceCode>foo-namespace</namespaceCode>
	    <name>termResolver-name</name>
	    <typeId>1234TYPE</typeId>
	    <output>
	        <id>123TERMRESOLVER</id>
	        <contextId>1234CONTEXT</contextId>
	        <name>termSpec-name</name>
	        <type>term.spec.Type</type>
	    </output>
	    <prerequisites>
	        <id>PREREQ123TERMRESOLVER</id>
	        <contextId>1234CONTEXT</contextId>
	        <name>PREREQtermSpec-name</name>
	        <type>PREREQterm.spec.Type</type>
	    </prerequisites>
	    <attributes>
	        <id>123ATTR</id>
	        <termResolverId>123TERMRESOLVER</termResolverId>
	        <attributeDefinitionId>123ATTR_DEF</attributeDefinitionId>
	        <value>attrValue</value>
	    </attributes>
	    <parameterNames>paramName</parameterNames>
	</TermResolverDefintion>
	"""
	
	@Test
	public void testXmlMarshaling() {
		// create(String termSpecificationId, String contextId, String name, String type)
		TermSpecificationDefinition.Builder output = TermSpecificationDefinition.Builder.create(ID, TERM_SPEC_CONTEXT_ID, TERM_SPEC_NAME, TERM_SPEC_TYPE); 
		
		TermSpecificationDefinition.Builder prereq = TermSpecificationDefinition.Builder.create("PREREQ"+ID, TERM_SPEC_CONTEXT_ID, "PREREQ"+TERM_SPEC_NAME, "PREREQ"+TERM_SPEC_TYPE); 
		
		// create(String id, String termResolverId, String attributeDefinitionId, String value) {
		TermResolverAttribute.Builder attribute = TermResolverAttribute.Builder.create(ATTR_ID, ID, ATTR_DEF_ID, ATTR_VALUE);
		
		/*
		public Builder create(String id,
		   String namespaceCode,
		   String name,
		   String typeId,
		   TermSpecificationDefinition.Builder output,
		   List<TermSpecificationDefinition.Builder> prerequisites,
		   List<TermResolverAttribute.Builder> attributes,
		   List<String> parameterNames) {
*/
		java.util.Set<TermSpecificationDefinition.Builder> prereqs = [prereq];
		java.util.Set<TermResolverAttribute.Builder> attributes = [attribute];
		java.util.Set<String> params = [PARAM_NAME];
		
		//create(java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.kuali.rice.krms.api.repository.TermSpecificationDefinition$Builder, java.util.List, java.util.List, java.util.List)
		TermResolverDefinition termResolver = TermResolverDefinition.Builder.create(ID, NAMESPACE_CODE, NAME, TYPE_ID, output, prereqs, attributes, params).build()
				
		JAXBContext jc = JAXBContext.newInstance(TermResolverDefinition.class)
		Marshaller marshaller = jc.createMarshaller()
		StringWriter sw = new StringWriter()
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
		marshaller.marshal(termResolver, sw)
		String xml = sw.toString()
		
		print xml;

		Unmarshaller unmarshaller = jc.createUnmarshaller();
		Object actual = unmarshaller.unmarshal(new StringReader(xml))
		Object expected = unmarshaller.unmarshal(new StringReader(EXPECTED_XML))
		Assert.assertEquals(expected, actual)
	}

	// TODO: test builder validations, etc

}
