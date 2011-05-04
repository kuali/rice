package org.kuali.rice.ksb.api.registry;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.namespace.QName;

import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;

/**
 * Defines the interface for a remotely accessible service registry.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@WebService(name = "remoteServiceRegistrySoap", targetNamespace = RegistryConstants.Namespaces.REGISTRY_NAMESPACE_2_0)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface RemoteServiceRegistry {

	@WebMethod(operationName = "getServiceEndpointsByName")
	@WebResult(name = "serviceEndpoints")
	List<ServiceEndpoint> getServiceEndpointsByName(@WebParam(name = "serviceName") QName serviceName) throws RiceIllegalArgumentException;
	
	@WebMethod(operationName = "getAllServiceEndpoints")
	@WebResult(name = "serviceEndpoints")
	List<ServiceEndpoint> getAllServiceEndpoints();
	
	@WebMethod(operationName = "getServiceEndpoint")
	@WebResult(name = "serviceEndpoint")
	ServiceEndpoint getServiceEndpoint(@WebParam(name = "serviceEndpointId") String serviceEndpointId) throws RiceIllegalArgumentException;
	
	@WebMethod(operationName = "getServiceEndpoints")
	@WebResult(name = "serviceEndpoints")
	List<ServiceEndpoint> getServiceEndpoints(@WebParam(name = "serviceEndpointIds") List<String> serviceEndpointIds) throws RiceIllegalArgumentException;
	
	@WebMethod(operationName = "getServiceHeadersByName")
	@WebResult(name = "serviceHeaders")
	List<ServiceHeader> getServiceHeadersByName(@WebParam(name = "serviceName") QName serviceName) throws RiceIllegalArgumentException;
	
	@WebMethod(operationName = "getAllServiceHeaders")
	@WebResult(name = "serviceHeaders")
	List<ServiceHeader> getAllServiceHeaders();
	
	@WebMethod(operationName = "getServiceDescriptor")
	@WebResult(name = "serviceDescriptor")
	ServiceDescriptor getServiceDescriptor(@WebParam(name = "serviceEndpointId") String serviceEndpointId) throws RiceIllegalArgumentException;
	
	@WebMethod(operationName = "getServiceDescriptors")
	@WebResult(name = "serviceDescriptors")
	List<ServiceDescriptor> getServiceDescriptors(@WebParam(name = "serviceEndpointIds") List<String> serviceEndpointIds) throws RiceIllegalArgumentException;
	
	@WebMethod(operationName = "publishService")
	void publishService(@WebParam(name = "serviceEndpoint") ServiceEndpoint serviceEndpoint) throws RiceIllegalArgumentException;

	@WebMethod(operationName = "publishServices")
	void publishServices(@WebParam(name = "serviceEndpoints") List<ServiceEndpoint> serviceEndpoints) throws RiceIllegalArgumentException;
		
	@WebMethod(operationName = "removeServiceEndpoint")
	void removeServiceEndpoint(@WebParam(name = "serviceEndpointId") String serviceEndpointId) throws RiceIllegalArgumentException;
	
	@WebMethod(operationName = "removeServiceEndpoints")
	void removeServiceEndpoints(@WebParam(name = "serviceEndpointIds") List<String> serviceEndpointIds);
	
}
