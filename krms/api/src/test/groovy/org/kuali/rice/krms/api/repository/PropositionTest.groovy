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

import org.kuali.rice.krms.api.engine.LogicalOperator;

/**
 * This is a description of what this class does - dseibert don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
class PropositionTest {
	
	private static final String PROP_ID = "202"
	private static final String DESCRIPTION = "is Campus Bloomington"
	private static final String TYPE_ID = "1"
	private static final String PROPOSITION_TYPE_CD_S = "S"
	private static final String PROPOSITION_TYPE_CD_C = "C"
	private static final String PROPOSITION_TYPE_CD_BAD = "X"
	private static final List<PropositionParameter.Builder> EMPTY_PARM_LIST = Collections.unmodifiableList(new ArrayList<PropositionParameter.Builder>())
	private static final List<PropositionParameter.Builder> PARM_LIST_1 = createPropositionParametersSet1()
	
	// Compound Proposition Data Structures
	private static final List <PropositionParameter.Builder> PARM_LIST_A = createPropositionParametersSet2()
	private static final List <PropositionParameter.Builder> PARM_LIST_B = createPropositionParametersSet3()
	private static final PropositionDefinition.Builder PROP_A_BUILDER = createPropositionABuilder()
	private static final PropositionDefinition.Builder PROP_B_BUILDER = createPropositionBBuilder()

	private static final Integer SEQUENCE_NUMBER_1 = new Integer(1)
	private static final String SIMPLE_PROP_XML = """
		<Proposition xmlns="http://rice.kuali.org/krms">
			<propId>202</propId>
			<description>is Campus Bloomington</description>
			<typeId>1</typeId>
			<propositionTypeCode>S</propositionTypeCode>
			<parameter>
				<id>1000</id>
				<propId>2001</propId>
				<value>campusCode</value>
				<parameterType>T</parameterType>
				<sequenceNumber>0</sequenceNumber>
			</parameter>
			<parameter>
				<id>1001</id>
				<propId>2001</propId>
				<value>BL</value>
				<parameterType>C</parameterType>
				<sequenceNumber>1</sequenceNumber>
			</parameter>
			<parameter>
				<id>1003</id>
				<propId>2001</propId>
				<value>EQUALS</value>
				<parameterType>F</parameterType>
				<sequenceNumber>2</sequenceNumber>
			</parameter>
		</Proposition>
		"""

	private static final String COMPOUND_PROP_XML = """
		<Proposition xmlns="http://rice.kuali.org/krms">
			<propId>111</propId>
			<description>Compound: Campus is Muir or Thurgood Marshall</description>
			<typeId>1</typeId>
			<propositionTypeCode>C</propositionTypeCode>
			<compoundOpCode>|</compoundOpCode>
			<proposition>
				<propId>100</propId>
				<description>Is campus type = Muir</description>
				<typeId>1</typeId>
				<propositionTypeCode>S</propositionTypeCode>
					<parameter>
						<id>2000</id>
						<propId>100</propId>
						<value>campusCode</value>
						<parameterType>T</parameterType>
						<sequenceNumber>0</sequenceNumber>
					</parameter>
					<parameter>
						<id>2001</id>
						<propId>100</propId>
						<value>Muir</value>
						<parameterType>C</parameterType>
						<sequenceNumber>1</sequenceNumber>
					</parameter>
					<parameter>
						<id>2002</id>
						<propId>100</propId>
						<value>EQUALS</value>
						<parameterType>F</parameterType>
						<sequenceNumber>2</sequenceNumber>
					</parameter>
				</proposition>
				<proposition>
					<propId>101</propId>
					<description>Is campus type = Thurgood Marshall</description>
					<typeId>1</typeId>
					<propositionTypeCode>S</propositionTypeCode>
					<parameter>
						<id>2010</id>
						<propId>101</propId>
						<value>campusCode</value>
						<parameterType>T</parameterType>
						<sequenceNumber>0</sequenceNumber>
					</parameter>
					<parameter>
						<id>2011</id>
						<propId>101</propId>
						<value>Thurgood Marshall</value>
						<parameterType>C</parameterType>
						<sequenceNumber>1</sequenceNumber>
					</parameter>
					<parameter>
						<id>2012</id>
						<propId>101</propId>
						<value>EQUALS</value>
						<parameterType>F</parameterType>
						<sequenceNumber>2</sequenceNumber>
					</parameter>
				</proposition>
			</Proposition>
	"""
		
	@Test(expected=IllegalArgumentException.class)
	void test_Builder_create_fail_all_null() {
		PropositionDefinition.Builder.create(null, null, null, null, null)
	}
	
	@Test(expected=IllegalArgumentException.class)
	void test_Builder_create_fail_null_id() {
		PropositionDefinition.Builder.create(null, DESCRIPTION, TYPE_ID, PROPOSITION_TYPE_CD_S, PARM_LIST_1)
	}
	
	@Test(expected=IllegalArgumentException.class)
	void test_Builder_create_fail_empty_id() {
		PropositionDefinition.Builder.create("", DESCRIPTION, TYPE_ID, PROPOSITION_TYPE_CD_S, PARM_LIST_1)
	}
	
	@Test(expected=IllegalArgumentException.class)
	void test_Builder_create_fail_whitespace_id() {
		PropositionDefinition.Builder.create("   ", DESCRIPTION, TYPE_ID, PROPOSITION_TYPE_CD_S, PARM_LIST_1)
	}
	
	@Test(expected=IllegalArgumentException.class)
	void test_Builder_create_fail_null_description() {
		PropositionDefinition.Builder.create(PROP_ID, null, TYPE_ID, PROPOSITION_TYPE_CD_S, PARM_LIST_1)
	}
	
	@Test(expected=IllegalArgumentException.class)
	void test_Builder_create_fail_empty_description() {
		PropositionDefinition.Builder.create(PROP_ID, "", TYPE_ID, PROPOSITION_TYPE_CD_S, PARM_LIST_1)
	}
	
	@Test(expected=IllegalArgumentException.class)
	void test_Builder_create_fail_whitespace_description() {
		PropositionDefinition.Builder.create(PROP_ID, "	  ", TYPE_ID, PROPOSITION_TYPE_CD_S, PARM_LIST_1)
	}
	
	@Test(expected=IllegalArgumentException.class)
	void test_Builder_create_fail_null_krms_type_id() {
		PropositionDefinition.Builder.create(PROP_ID, DESCRIPTION, null, PROPOSITION_TYPE_CD_S, PARM_LIST_1)
	}
	
	@Test(expected=IllegalArgumentException.class)
	void test_Builder_create_fail_empty_krms_type_id() {
		PropositionDefinition.Builder.create(PROP_ID, DESCRIPTION, "", PROPOSITION_TYPE_CD_S, PARM_LIST_1)
	}
	
	@Test(expected=IllegalArgumentException.class)
	void test_Builder_create_fail_whitespace_krms_type_id() {
		PropositionDefinition.Builder.create(PROP_ID, DESCRIPTION, "  ", PROPOSITION_TYPE_CD_S, PARM_LIST_1)
	}
	
	@Test(expected=IllegalArgumentException.class)
	void test_Builder_create_fail_null_proposition_type() {
		PropositionDefinition.Builder.create(PROP_ID, DESCRIPTION, TYPE_ID, null, PARM_LIST_1)
	}

	@Test(expected=IllegalArgumentException.class)
	void test_Builder_create_fail_empty_proposition_type() {
		PropositionDefinition.Builder.create(PROP_ID, DESCRIPTION, TYPE_ID, "", PARM_LIST_1)
	}

	@Test(expected=IllegalArgumentException.class)
	void test_Builder_create_fail_whitespace_proposition_type() {
		PropositionDefinition.Builder.create(PROP_ID, DESCRIPTION, TYPE_ID, "    ", PARM_LIST_1)
	}

	@Test(expected=IllegalArgumentException.class)
	void test_Builder_create_fail_invalid_proposition_type() {
		PropositionDefinition.Builder.create(PROP_ID, DESCRIPTION, TYPE_ID, PROPOSITION_TYPE_CD_BAD, PARM_LIST_1)
	}

	@Test(expected=IllegalArgumentException.class)
	void test_Builder_create_fail_null_parameter_list() {
		PropositionDefinition.Builder.create(PROP_ID, DESCRIPTION, TYPE_ID, PROPOSITION_TYPE_CD_S, null)
	}
	
	@Test(expected=IllegalArgumentException.class)
	void test_Builder_create_fail_empty_parameter_list() {
		PropositionDefinition.Builder.create(PROP_ID, DESCRIPTION, TYPE_ID, PROPOSITION_TYPE_CD_S, EMPTY_PARM_LIST)
	}

	@Test
	void test_Builder_create_simple_proposition_success() {
		PropositionDefinition.Builder.create(PROP_ID, DESCRIPTION, TYPE_ID, PROPOSITION_TYPE_CD_S, PARM_LIST_1)
	}

	@Test
	void test_Builder_create_and_build_simple_proposition_success() {
		PropositionDefinition.Builder.create(PROP_ID, DESCRIPTION, TYPE_ID, PROPOSITION_TYPE_CD_S, PARM_LIST_1).build()
	}

	@Test
	void test_Builder_create_compound_proposition_sucess() {
		PropositionDefinition.Builder.create(new PropositionDefinitionContract () {
			def String propId = "111"
			def String description = "Compound: Campus is Muir or Thurgood Marshall"
			def String typeId = "1"
			def String propositionTypeCode = "C"
			def List<? extends PropositionParameterContract> parameters = new ArrayList<PropositionParameter.Builder>()
			def String compoundOpCode = LogicalOperator.OR.opCode()
			def List<? extends PropositionDefinition> compoundComponents = Arrays.asList(PropositionTest.PROP_A_BUILDER, PropositionTest.PROP_B_BUILDER)
		})
	}
	
	@Test
	void test_Builder_create_and_build_compound_proposition_success() {
		PropositionDefinition.Builder.create(new PropositionDefinitionContract () {
			def String propId = "111"
			def String description = "Compound: Campus is Muir or Thurgood Marshall"
			def String typeId = "1"
			def String propositionTypeCode = "C"
			def List<? extends PropositionParameterContract> parameters = new ArrayList<PropositionParameter.Builder>()
			def String compoundOpCode = LogicalOperator.OR.opCode()
			def List<? extends PropositionDefinition> compoundComponents = Arrays.asList(PropositionTest.PROP_A_BUILDER, PropositionTest.PROP_B_BUILDER)
		}).build()
	}

	@Test
	public void testXmlMarshaling_simple_proposition() {
 		PropositionDefinition myProp = PropositionDefinition.Builder.create(PROP_ID, DESCRIPTION, TYPE_ID, PROPOSITION_TYPE_CD_S, PARM_LIST_1).build()
        JAXBContext jc = JAXBContext.newInstance(PropositionDefinition.class, PropositionParameter.class)
	    Marshaller marshaller = jc.createMarshaller()
	    StringWriter sw = new StringWriter()
	    marshaller.marshal(myProp, sw)
	    String xml = sw.toString()
  
	    Unmarshaller unmarshaller = jc.createUnmarshaller();
	    Object actual = unmarshaller.unmarshal(new StringReader(xml))
	    Object expected = unmarshaller.unmarshal(new StringReader(SIMPLE_PROP_XML))
	    Assert.assertEquals(expected, actual)
	}

	@Test
	public void testXmlUnmarshal_simple_proposition() {
	  JAXBContext jc = JAXBContext.newInstance(PropositionDefinition.class, PropositionParameter.class)
	  Unmarshaller unmarshaller = jc.createUnmarshaller();
	  PropositionDefinition myProp = (PropositionDefinition) unmarshaller.unmarshal(new StringReader(SIMPLE_PROP_XML))
	  Assert.assertEquals(PROP_ID, myProp.propId)
	  Assert.assertEquals(DESCRIPTION, myProp.description)
	  Assert.assertEquals(TYPE_ID, myProp.typeId)
	  Assert.assertEquals(PROPOSITION_TYPE_CD_S, myProp.propositionTypeCode)
	  List<PropositionParameter> pList = new ArrayList<PropositionParameter>()
	  for (pb in PARM_LIST_1){
		  pList.add (pb.build())
	  }
	  Assert.assertEquals(pList, myProp.parameters)
	}

	@Test
	public void testXmlMarshaling_compound_proposition() {
		PropositionDefinition myProp = PropositionDefinition.Builder.create(new PropositionDefinitionContract () {
			def String propId = "111"
			def String description = "Compound: Campus is Muir or Thurgood Marshall"
			def String typeId = "1"
			def String propositionTypeCode = "C"
			def List<? extends PropositionParameterContract> parameters = new ArrayList<PropositionParameter.Builder>()
			def String compoundOpCode = LogicalOperator.OR.opCode()
			def List<? extends PropositionDefinition> compoundComponents = Arrays.asList(PropositionTest.PROP_A_BUILDER, PropositionTest.PROP_B_BUILDER)
		}).build()

		JAXBContext jc = JAXBContext.newInstance(PropositionDefinition.class, PropositionParameter.class)
		Marshaller marshaller = jc.createMarshaller()
		StringWriter sw = new StringWriter()
		marshaller.marshal(myProp, sw)
		String xml = sw.toString()
  
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		Object actual = unmarshaller.unmarshal(new StringReader(xml))
		Object expected = unmarshaller.unmarshal(new StringReader(COMPOUND_PROP_XML))
		Assert.assertEquals(expected, actual)
	}

	@Test
	public void testXmlUnmarshal_compound_proposition() {
	  JAXBContext jc = JAXBContext.newInstance(PropositionDefinition.class, PropositionParameter.class)
	  Unmarshaller unmarshaller = jc.createUnmarshaller();
	  PropositionDefinition myProp = (PropositionDefinition) unmarshaller.unmarshal(new StringReader(COMPOUND_PROP_XML))
	  Assert.assertEquals("111", myProp.propId)
	  Assert.assertEquals("Compound: Campus is Muir or Thurgood Marshall", myProp.description)
	  Assert.assertEquals("1", myProp.typeId)
	  Assert.assertEquals(PROPOSITION_TYPE_CD_C, myProp.propositionTypeCode)
	  Assert.assertEquals("|", myProp.compoundOpCode)
	  Assert.assertEquals(2, myProp.compoundComponents.size())
	  List<PropositionDefinition> pList = new ArrayList<PropositionDefinition>()
	  for (pb in [PROP_A_BUILDER, PROP_B_BUILDER]){
		  pList.add (pb.build())
	  }
	  Assert.assertEquals(pList.get(0).propId, myProp.compoundComponents.get(0).propId)
	  Assert.assertEquals(pList.get(0).description, myProp.compoundComponents.get(0).description)
	  Assert.assertEquals(pList.get(0).typeId, myProp.compoundComponents.get(0).typeId)
	  Assert.assertEquals(pList.get(0).propositionTypeCode, myProp.compoundComponents.get(0).propositionTypeCode)
	  Assert.assertEquals(pList.get(1).propId, myProp.compoundComponents.get(1).propId)
	  Assert.assertEquals(pList.get(1).description, myProp.compoundComponents.get(1).description)
	  Assert.assertEquals(pList.get(1).typeId, myProp.compoundComponents.get(1).typeId)
	  Assert.assertEquals(pList.get(1).propositionTypeCode, myProp.compoundComponents.get(1).propositionTypeCode)
	  Assert.assertEquals(pList.get(0).parameters, myProp.compoundComponents.get(0).parameters)
	  Assert.assertEquals(pList.get(1).parameters, myProp.compoundComponents.get(1).parameters)
	}

  
  private static createPropositionParametersSet1(){
	  List <PropositionParameter.Builder> propParms = new ArrayList <PropositionParameter.Builder> ()
	  PropositionParameter.Builder ppBuilder1 = PropositionParameter.Builder.create(new PropositionParameterContract() {
		  def String id = "1000"
		  def String propId = "2001"
		  def String value = "campusCode"
		  def String parameterType = "T"
		  def Integer sequenceNumber = new Integer(0)
	  })
	  PropositionParameter.Builder ppBuilder2 = PropositionParameter.Builder.create(new PropositionParameterContract() {
		  def String id = "1001"
		  def String propId = "2001"
		  def String value = "BL"
		  def String parameterType = "C"
		  def Integer sequenceNumber = new Integer(1)
	  })
	  PropositionParameter.Builder ppBuilder3 = PropositionParameter.Builder.create(new PropositionParameterContract() {
		  def String id = "1003"
		  def String propId = "2001"
		  def String value = "EQUALS"
		  def String parameterType = "F"
		  def Integer sequenceNumber = new Integer(2)
	  })
	  for ( ppb in [ppBuilder1, ppBuilder2, ppBuilder3]){
		  propParms.add (ppb)
	  }
	  return propParms;
  }
  private static createPropositionParametersSet2(){
	  List <PropositionParameter.Builder> propParms = new ArrayList <PropositionParameter.Builder> ()
	  PropositionParameter.Builder ppBuilder1 = PropositionParameter.Builder.create(new PropositionParameterContract() {
		  def String id = "2000"
		  def String propId = "100"
		  def String value = "campusCode"
		  def String parameterType = "T"
		  def Integer sequenceNumber = new Integer(0)
	  })
	  PropositionParameter.Builder ppBuilder2 = PropositionParameter.Builder.create(new PropositionParameterContract() {
		  def String id = "2001"
		  def String propId = "100"
		  def String value = "Muir"
		  def String parameterType = "C"
		  def Integer sequenceNumber = new Integer(1)
	  })
	  PropositionParameter.Builder ppBuilder3 = PropositionParameter.Builder.create(new PropositionParameterContract() {
		  def String id = "2002"
		  def String propId = "100"
		  def String value = "EQUALS"
		  def String parameterType = "F"
		  def Integer sequenceNumber = new Integer(2)
	  })
	  for ( ppb in [ppBuilder1, ppBuilder2, ppBuilder3]){
		  propParms.add (ppb)
	  }
	  return propParms;
  }
  private static createPropositionParametersSet3(){
	  List <PropositionParameter.Builder> propParms = new ArrayList <PropositionParameter.Builder> ()
	  PropositionParameter.Builder ppBuilder1 = PropositionParameter.Builder.create(new PropositionParameterContract() {
		  def String id = "2010"
		  def String propId = "101"
		  def String value = "campusCode"
		  def String parameterType = "T"
		  def Integer sequenceNumber = new Integer("0")
	  })
	  PropositionParameter.Builder ppBuilder2 = PropositionParameter.Builder.create(new PropositionParameterContract() {
		  def String id = "2011"
		  def String propId = "101"
		  def String value = "Thurgood Marshall"
		  def String parameterType = "C"
		  def Integer sequenceNumber = new Integer("1")
	  })
	  PropositionParameter.Builder ppBuilder3 = PropositionParameter.Builder.create(new PropositionParameterContract() {
		  def String id = "2012"
		  def String propId = "101"
		  def String value = "EQUALS"
		  def String parameterType = "F"
		  def Integer sequenceNumber = new Integer("2")
	  })
	  for ( ppb in [ppBuilder1, ppBuilder2, ppBuilder3]){
		  propParms.add (ppb)
	  }
	  return propParms;
  }
  private static createPropositionABuilder() {
	  return PropositionDefinition.Builder.create(new PropositionDefinitionContract () {
		  def String propId = "100"
		  def String description = "Is campus type = Muir"
		  def String typeId = "1"
		  def String propositionTypeCode = "S"
		  def List<? extends PropositionParameterContract> parameters = PropositionTest.PARM_LIST_A
		  def String compoundOpCode = null
		  def List<? extends PropositionDefinition> compoundComponents = new ArrayList<PropositionDefinition>()
	  })
  }
	  
  private static createPropositionBBuilder() {
	  return PropositionDefinition.Builder.create(new PropositionDefinitionContract () {
		  def String propId = "101"
		  def String description = "Is campus type = Thurgood Marshall"
		  def String typeId = "1"
		  def String propositionTypeCode = "S"
		  def List<? extends PropositionParameterContract> parameters = PropositionTest.PARM_LIST_B
		  def String compoundOpCode = null
		  def List<? extends PropositionDefinition> compoundComponents = new ArrayList<PropositionDefinition>()
	  })
  }
	  

}
