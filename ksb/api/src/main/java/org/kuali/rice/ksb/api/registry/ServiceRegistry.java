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
import org.kuali.rice.ksb.api.KsbConstants;

/**
 * Defines the interface for a remotely accessible service registry.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@WebService(name = "serviceRegistrySoap", targetNamespace = KsbConstants.Namespaces.KSB_NAMESPACE_2_0)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface ServiceRegistry {
	
	@WebMethod(operationName = "getOnlineServiceByName")
	@WebResult(name = "serviceInfo")
	List<ServiceInfo> getOnlineServicesByName(@WebParam(name = "serviceName") QName serviceName) throws RiceIllegalArgumentException;
	
	@WebMethod(operationName = "getAllOnlineServices")
	@WebResult(name = "serviceInfo")
	List<ServiceInfo> getAllOnlineServices();
	
	@WebMethod(operationName = "getAllServices")
	@WebResult(name = "serviceInfo")
	List<ServiceInfo> getAllServices();
	
	@WebMethod(operationName = "getServiceDescriptor")
	@WebResult(name = "serviceDescriptor")
	ServiceDescriptor getServiceDescriptor(@WebParam(name = "serviceDescriptorId") String serviceDescriptorId) throws RiceIllegalArgumentException;
	
	@WebMethod(operationName = "getServiceDescriptors")
	@WebResult(name = "serviceDescriptor")
	List<ServiceDescriptor> getServiceDescriptors(@WebParam(name = "serviceDescriptorId") List<String> serviceDescriptorIds) throws RiceIllegalArgumentException;
	
	@WebMethod(operationName = "publishService")
	void publishService(@WebParam(name = "serviceEndpoint") ServiceEndpoint serviceEndpoint) throws RiceIllegalArgumentException;

	@WebMethod(operationName = "publishServices")
	void publishServices(@WebParam(name = "serviceEndpoint") Set<ServiceEndpoint> serviceEndpoints) throws RiceIllegalArgumentException;
		
	@WebMethod(operationName = "removeServiceEndpoint")
	void removeServiceEndpoint(@WebParam(name = "serviceId") String serviceId) throws RiceIllegalArgumentException;
	
	@WebMethod(operationName = "removeServiceEndpoints")
	void removeServiceEndpoints(@WebParam(name = "serviceId") Set<String> serviceIds) throws RiceIllegalArgumentException;
	
	@WebMethod(operationName = "removeAndPublish")
	void removeAndPublish(@WebParam(name = "removeServiceId") Set<String> removeServiceIds,
			@WebParam(name = "publishServiceEndpoint") Set<ServiceEndpoint> publishServiceEndpoints);
	
	@WebMethod(operationName = "updateStatus")
	void updateStatus(@WebParam(name = "serviceId") String serviceId, @WebParam(name = "status") ServiceEndpointStatus status) throws RiceIllegalArgumentException;
	
	@WebMethod(operationName = "updateStatuses")
	void updateStatuses(@WebParam(name = "serviceId") Set<String> serviceIds, @WebParam(name = "status") ServiceEndpointStatus status) throws RiceIllegalArgumentException;
	
	@WebMethod(operationName = "takeInstanceOffline")
	void takeInstanceOffline(@WebParam(name = "instanceId") String instanceId) throws RiceIllegalArgumentException;
	
}
