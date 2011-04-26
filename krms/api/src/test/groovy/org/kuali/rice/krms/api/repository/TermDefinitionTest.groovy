package org.kuali.rice.krms.api.repository

import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller
import javax.xml.bind.Unmarshaller

import org.junit.Assert
import org.junit.Ignore
import org.junit.Test
import org.kuali.rice.krms.api.repository.term.TermDefinition
import org.kuali.rice.krms.api.repository.term.TermParameterDefinition
import org.kuali.rice.krms.api.repository.term.TermSpecificationDefinition


/**
 * This class tests out the buiding of a KrmsTypeAttribute object.
 * It also tests XML marshalling / unmarshalling
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
class TermDefinitionTest {
	
	private static final String NAMESPACE = "KRMS_UNIT_TEST"
	
	private static final String ID="123"
	private static final String TERM_SPEC_CONTEXT_ID ="1234CONTEXT"
	private static final String TERM_SPEC_NAME="termSpec-name"
	private static final String TERM_SPEC_TYPE="term.spec.Type"
	private static final String PARAM_NAME="paramName"
	private static final String PARAM_VALUE="paramValue"
	
	private static final String EXPECTED_XML = """
	<TermDefinition xmlns="http://rice.kuali.org/krms/repository/v2_0">
	    <id>123DEF</id>
	    <specification>
	        <id>123SPEC</id>
	        <contextId>1234CONTEXT</contextId>
	        <name>termSpec-name</name>
	        <type>term.spec.Type</type>
	    </specification>
	    <parameters>
	        <parameter>
	            <id>123PARAM</id>
	            <name>paramName</name>
	            <value>paramValue</value>
	        </parameter>
	    </parameters>
	</TermDefinition>
	"""
	
	@Test
	public void testXmlMarshaling() {
		// create(String termSpecificationId, String contextId, String name, String type)
		TermSpecificationDefinition.Builder spec = TermSpecificationDefinition.Builder.create(ID+"SPEC", TERM_SPEC_CONTEXT_ID, TERM_SPEC_NAME, TERM_SPEC_TYPE);
		
		// create(String id, String name, String value) {
		TermParameterDefinition.Builder param = TermParameterDefinition.Builder.create(ID+"PARAM", ID+"SPEC", PARAM_NAME, PARAM_VALUE);
		
		// create(String id, TermSpecificationDefinition termSpecificationDefinition, 
		//		Set<TermParameterDefinition> termParameters) {
		TermDefinition termDef = TermDefinition.Builder.create(ID+"DEF", spec, Collections.singleton(param)).build();
		
		JAXBContext jc = JAXBContext.newInstance(TermDefinition.class)
		Marshaller marshaller = jc.createMarshaller()
		StringWriter sw = new StringWriter()
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
		marshaller.marshal(termDef, sw)
		String xml = sw.toString()
		
		print xml;

		Unmarshaller unmarshaller = jc.createUnmarshaller();
		Object actual = unmarshaller.unmarshal(new StringReader(xml))
		Object expected = unmarshaller.unmarshal(new StringReader(EXPECTED_XML))
		Assert.assertEquals(expected, actual)
	}

	// TODO: test builder validations, etc

}
