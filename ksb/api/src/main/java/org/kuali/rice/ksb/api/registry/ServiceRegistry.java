package org.kuali.rice.ksb.api.registry;

import java.util.List;
import java.util.Set;

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
@WebService(name = "serviceRegistrySoap", targetNamespace = RegistryConstants.Namespaces.REGISTRY_NAMESPACE_2_0)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface ServiceRegistry {
	
	@WebMethod(operationName = "getOnlineServiceByName")
	@WebResult(name = "services")
	List<ServiceInfo> getOnlineServicesByName(@WebParam(name = "serviceName") QName serviceName) throws RiceIllegalArgumentException;
	
	@WebMethod(operationName = "getAllOnlineServices")
	@WebResult(name = "services")
	List<ServiceInfo> getAllOnlineServices();
	
	@WebMethod(operationName = "getAllServices")
	@WebResult(name = "services")
	List<ServiceInfo> getAllServices();
	
	@WebMethod(operationName = "getServiceDescriptor")
	@WebResult(name = "serviceDescriptor")
	ServiceDescriptor getServiceDescriptor(@WebParam(name = "serviceDescriptorId") String serviceDescriptorId) throws RiceIllegalArgumentException;
	
	@WebMethod(operationName = "getServiceDescriptors")
	@WebResult(name = "serviceDescriptors")
	List<ServiceDescriptor> getServiceDescriptors(@WebParam(name = "serviceDescriptorIds") List<String> serviceDescriptorIds) throws RiceIllegalArgumentException;
	
	@WebMethod(operationName = "publishService")
	void publishService(@WebParam(name = "serviceEndpoint") ServiceEndpoint serviceEndpoint) throws RiceIllegalArgumentException;

	@WebMethod(operationName = "publishServices")
	void publishServices(@WebParam(name = "serviceEndpoints") Set<ServiceEndpoint> serviceEndpoints) throws RiceIllegalArgumentException;
		
	@WebMethod(operationName = "removeServiceEndpoint")
	void removeServiceEndpoint(@WebParam(name = "serviceId") String serviceId) throws RiceIllegalArgumentException;
	
	@WebMethod(operationName = "removeServiceEndpoints")
	void removeServiceEndpoints(@WebParam(name = "serviceIds") Set<String> serviceIds) throws RiceIllegalArgumentException;
	
	@WebMethod(operationName = "removeAndPublish")
	void removeAndPublish(@WebParam(name = "removeServiceIds") Set<String> removeServiceIds,
			@WebParam(name = "publishServiceEndpoints") Set<ServiceEndpoint> publishServiceEndpoints);
	
	@WebMethod(operationName = "updateStatus")
	void updateStatus(@WebParam(name = "serviceId") String serviceId, @WebParam(name = "status") ServiceEndpointStatus status) throws RiceIllegalArgumentException;
	
	@WebMethod(operationName = "updateStatuses")
	void updateStatuses(@WebParam(name = "serviceIds") Set<String> serviceIds, @WebParam(name = "status") ServiceEndpointStatus status) throws RiceIllegalArgumentException;
	
	@WebMethod(operationName = "takeInstanceOffline")
	void takeInstanceOffline(@WebParam(name = "instanceId") String instanceId) throws RiceIllegalArgumentException;
	
}
