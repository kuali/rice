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

package org.kuali.rice.krms.api.repository

import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller
import javax.xml.bind.Unmarshaller
import junit.framework.Assert
import org.junit.Test
import org.kuali.rice.krms.api.repository.KrmsType
import org.kuali.rice.krms.api.repository.KrmsTypeContract

/**
 * Exercises the immutable Country class, including XML (un)marshalling
 */
class KrmsTypeTest {

  private final shouldFail = new GroovyTestCase().&shouldFail

    @Test
    void test_create_only_required() {
        KrmsType.Builder.create(KrmsType.Builder.create("1", "Default", "KRMS_TEST")).build();
    }

    @Test
    void test_create_with_optional() {
        KrmsType.Builder.create(KrmsType.Builder.create("1", "Default", "KRMS_TEST")).serviceName("MyTestService").build();
    }

  @Test
  public void testKrmsTypeBuilderPassedInParams() {
    //No assertions, just test whether the Builder gives us a KRMS KrmsType object
    KrmsType myType = KrmsType.Builder.create("1", "Default", "KRMS_TEST").build()
  }

  @Test
  public void testKrmsTypeBuilderPassedInParamsAndServiceName() {
    //No assertions, just test whether the Builder gives us a KRMS KrmsType object
    KrmsType myType = KrmsType.Builder.create("1", "Default", "KRMS_TEST").serviceName("MyFictionalService").build()
  }

  @Test
  public void testTypeBuilderPassedInContract() {
    //No assertions, just test whether the Builder gives us a KRMS KrmsType object
    KrmsType type = KrmsType.Builder.create(new KrmsTypeContract() {
      String getId() {"1"}
      String getName() { "Student" }
	  String getNamespace() {"KRMS_TEST" }
	  String getServiceName() {"TypeServiceImpl"}
      boolean isActive() { true }
	  List<KrmsTypeAttribute> getAttributes() {null}
    }).build()
  }

  @Test
  public void testTypeBuilderNullTypeId() {
    shouldFail(IllegalArgumentException.class) {
      KrmsType.Builder.create(null, "United States", "KRMS_TEST")
    }
  }

  @Test
  public void testTypeBuilderEmptyTypeId() {
    shouldFail(IllegalArgumentException.class) {
      KrmsType.Builder.create("", "United States", "KRMS_TEST")
    }
  }

  @Test
  public void testXmlMarshaling() {
    JAXBContext jc = JAXBContext.newInstance(KrmsType.class)
    Marshaller marshaller = jc.createMarshaller()
    StringWriter sw = new StringWriter()

    KrmsType myType = KrmsType.Builder.create("2", "United States", "KRMS_TEST").build()
    marshaller.marshal(myType, sw)
    String xml = sw.toString()

    String expectedTypeElementXml = """
    <KRMSType xmlns="http://rice.kuali.org/krms">
      <id>2</id>
      <name>United States</name>
      <namespace>KRMS_TEST</namespace>
      <active>true</active>
      <serviceName></serviceName>
    </KRMSType>
    """

    Unmarshaller unmarshaller = jc.createUnmarshaller();
    Object actual = unmarshaller.unmarshal(new StringReader(xml))
    Object expected = unmarshaller.unmarshal(new StringReader(expectedTypeElementXml))
    Assert.assertEquals(expected, actual)
  }

  @Test
  public void testXmlUnmarshal() {
    String rawXml = """
    <KRMSType xmlns="http://rice.kuali.org/krms">
      <id>3</id>
      <name>Student</name>
      <namespace>KRMS_TEST</namespace>
      <active>true</active>
    </KRMSType>
    """

    JAXBContext jc = JAXBContext.newInstance(KrmsType.class)
    Unmarshaller unmarshaller = jc.createUnmarshaller();
    KrmsType myType = (KrmsType) unmarshaller.unmarshal(new StringReader(rawXml))
    Assert.assertEquals("3",myType.id)
    Assert.assertEquals("Student",myType.name)
	Assert.assertEquals("KRMS_TEST", myType.namespace)
	Assert.assertEquals (true, myType.active)
		

  }
}