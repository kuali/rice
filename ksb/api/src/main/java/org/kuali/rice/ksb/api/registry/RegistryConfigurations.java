package org.kuali.rice.ksb.api.registry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.ksb.api.bus.support.SoapServiceConfiguration;
import org.w3c.dom.Element;

@XmlRootElement(name = RegistryConfigurations.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = RegistryConfigurations.Constants.TYPE_NAME, propOrder = {
		RegistryConfigurations.Elements.SOAP_SERVICE_CONFIGURATIONS,
		CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public class RegistryConfigurations {

	@XmlElementWrapper(name = Elements.SOAP_SERVICE_CONFIGURATIONS)
	@XmlElement(name = Elements.SOAP_SERVICE_CONFIGURATION)
	private final List<SoapServiceConfiguration> soapServiceConfigurations;

	@SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;
	
	@SuppressWarnings("unused")
	private RegistryConfigurations() {
		this.soapServiceConfigurations = null;
	}
	
	public RegistryConfigurations(List<SoapServiceConfiguration> soapServiceConfigurations) {
		this.soapServiceConfigurations = soapServiceConfigurations == null ? new ArrayList<SoapServiceConfiguration>() : soapServiceConfigurations;
	}
	
	public List<SoapServiceConfiguration> getSoapServiceConfigurations() {
		return soapServiceConfigurations;
	}
	
	/**
     * Defines some internal constants used on this class.
     */
    static class Constants {
    	final static String ROOT_ELEMENT_NAME = "registryConfigurations";
        final static String TYPE_NAME = "RegistryConfigurationsType";
    }

    /**
     * A private class which exposes constants which define the XML element names to use
     * when this object is marshalled to XML.
     */
     static class Elements {
    	protected final static String SOAP_SERVICE_CONFIGURATIONS = "soapServiceConfigurations";
    	protected final static String SOAP_SERVICE_CONFIGURATION = "soapServiceConfiguration";
    }

}
