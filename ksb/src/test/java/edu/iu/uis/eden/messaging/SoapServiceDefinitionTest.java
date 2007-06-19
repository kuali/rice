package edu.iu.uis.eden.messaging;

import java.net.URL;
import java.util.ArrayList;

import org.junit.Test;
import org.kuali.bus.test.KSBTestCase;

/**
 * 
 * @author Scott Battaglia
 * @version $Revision: 1.2 $ $Date: 2007-06-19 14:35:12 $
 * @since 0.9
 *
 */
public class SoapServiceDefinitionTest extends KSBTestCase {
    
    private SOAPServiceDefinition soapDefinition;

    public void setUp() throws Exception {
    	super.setUp();
        this.soapDefinition = new SOAPServiceDefinition();
        this.soapDefinition.setLocalServiceName("testServiceName");
        this.soapDefinition.setServiceEndPoint(new URL("http://www.rutgers.edu"));
        this.soapDefinition.setService(new ArrayList<String>());
        this.soapDefinition.validate();
    }
    
    @Test
    public void testIsSameSuccessWithSameDefinition() {
        assertTrue(this.soapDefinition.isSame(this.soapDefinition));
    }
    
    @Test
    public void testIsSameSuccessWithDifferentDefinition() throws Exception {
        final SOAPServiceDefinition soapServiceDefinition = new SOAPServiceDefinition();
        soapServiceDefinition.setLocalServiceName("testServiceName");
        soapServiceDefinition.setServiceEndPoint(new URL("http://www.rutgers.edu"));
        soapServiceDefinition.setService(new ArrayList<String>());
        soapServiceDefinition.validate();
    	
        assertTrue(this.soapDefinition.isSame(soapServiceDefinition));
    }
    
    @Test
    public void testIsSameFailureWithDifferentClass() throws Exception {
        final JavaServiceDefinition javaServiceDefinition = new JavaServiceDefinition();
        javaServiceDefinition.setBusSecurity(Boolean.FALSE);
        javaServiceDefinition.setLocalServiceName("testServiceName");
        javaServiceDefinition.setServiceEndPoint(new URL("http://www.rutgers.edu"));
        javaServiceDefinition.setService(new ArrayList<String>());
        javaServiceDefinition.validate();
        assertFalse(this.soapDefinition.isSame(javaServiceDefinition));
    }
}
