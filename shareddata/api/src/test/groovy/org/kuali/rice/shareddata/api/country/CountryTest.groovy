package org.kuali.rice.shareddata.api.country

import org.junit.Test
import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller
import javax.xml.bind.Unmarshaller
import junit.framework.Assert

/**
 * Exercises the immutable Country class, including XML (un)marshalling
 */
class CountryTest {

  private final shouldFail = new GroovyTestCase().&shouldFail

  @Test
  public void testCountryBuilderPassedInParams() {
    //No assertions, just test whether the Builder gives us a Country object
    Country country = Country.Builder.create("US", null, "United States", false, true).build()
  }

  @Test
  public void testCountryBuilderPassedInCountryContract() {
    //No assertions, just test whether the Builder gives us a Country object
    Country country = Country.Builder.create(new CountryContract() {
      String getPostalCountryCode() {"US"}

      String getAlternatePostalCountryCode() { "USA" }

      String getPostalCountryName() { "United States" }

      boolean isActive() { true }

      boolean isPostalCountryRestricted() { false }
    }).build()
  }

  public void testCountryBuilderNullCountryCode() {
    shouldFail(IllegalArgumentException.class) {
      Country.Builder.create(null, null, "United States", false, true)
    }
  }

  public void testCountryBuilderEmptyCountryCode() {
    shouldFail(IllegalArgumentException.class) {
      Country.Builder.create("  ", null, "United States", false, true)
    }
  }

  @Test
  public void testXmlMarshaling() {
    JAXBContext jc = JAXBContext.newInstance(Country.class)
    Marshaller marshaller = jc.createMarshaller()
    StringWriter sw = new StringWriter()

    Country country = Country.Builder.create("US", null, "United States", false, true).build()
    marshaller.marshal(country, sw)
    String xml = sw.toString()

    String expectedCountryElementXml = """
    <country xmlns="http://rice.kuali.org/schema/shareddata">
      <postalCountryCode>US</postalCountryCode>
      <postalCountryName>United States</postalCountryName>
      <postalCountryRestricted>false</postalCountryRestricted>
      <active>true</active>
    </country>
    """

    Unmarshaller unmarshaller = jc.createUnmarshaller();
    Object actual = unmarshaller.unmarshal(new StringReader(xml))
    Object expected = unmarshaller.unmarshal(new StringReader(expectedCountryElementXml))
    Assert.assertEquals(expected, actual)
  }

  @Test
  public void testXmlUnmarshal() {
    String rawXml = """
    <country xmlns="http://rice.kuali.org/schema/shareddata">
      <postalCountryCode>AU</postalCountryCode>
      <alternatePostalCountryCode>AUS</alternatePostalCountryCode>
      <postalCountryName>Australia</postalCountryName>
    </country>
    """

    JAXBContext jc = JAXBContext.newInstance(Country.class)
    Unmarshaller unmarshaller = jc.createUnmarshaller();
    Country country = (Country) unmarshaller.unmarshal(new StringReader(rawXml))
    Assert.assertEquals("AU", country.postalCountryCode)
    Assert.assertEquals("AUS", country.alternatePostalCountryCode)
    Assert.assertEquals("Australia", country.postalCountryName)

  }
}