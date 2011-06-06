package org.kuali.rice.ksb.api.registry;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;

import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.util.jaxb.QNameAsStringAdapter;
import org.kuali.rice.ksb.api.KsbApiConstants;

/**
 * Defines the interface for a remotely accessible service registry.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@WebService(name = "serviceRegistrySoap", targetNamespace = KsbApiConstants.Namespaces.KSB_NAMESPACE_2_0)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface ServiceRegistry {
	
	@WebMethod(operationName = "getOnlineServiceByName")
	@WebResult(name = "serviceInfos")
	@XmlElementWrapper(name = "serviceInfos", required = true)
	@XmlElement(name = "serviceInfo", required = false)
	List<ServiceInfo> getOnlineServicesByName(
			@XmlJavaTypeAdapter(QNameAsStringAdapter.class)
			@WebParam(name = "serviceName")
			QName serviceName) throws RiceIllegalArgumentException;
	
	@WebMethod(operationName = "getAllOnlineServices")
	@WebResult(name = "serviceInfo")
	@XmlElementWrapper(name = "serviceInfos", required = true)
	@XmlElement(name = "serviceInfo", required = false)
	List<ServiceInfo> getAllOnlineServices();
	
	@WebMethod(operationName = "getAllServices")
	@WebResult(name = "serviceInfo")
	@XmlElementWrapper(name = "serviceInfos", required = true)
	@XmlElement(name = "serviceInfo", required = false)
	List<ServiceInfo> getAllServices();
	
	@WebMethod(operationName = "getAllServicesForInstance")
	@WebResult(name = "serviceInfos")
	@XmlElementWrapper(name = "serviceInfos", required = true)
	@XmlElement(name = "serviceInfo", required = false)
	List<ServiceInfo> getAllServicesForInstance(@WebParam(name = "instanceId") String instanceId) throws RiceIllegalArgumentException;
	
	@WebMethod(operationName = "getServiceDescriptor")
	@WebResult(name = "serviceDescriptor")
	@XmlElement(name = "serviceDescriptor", required = false)
	ServiceDescriptor getServiceDescriptor(@WebParam(name = "serviceDescriptorId") String serviceDescriptorId) throws RiceIllegalArgumentException;
	
	@WebMethod(operationName = "getServiceDescriptors")
	@WebResult(name = "serviceDescriptors")
	@XmlElementWrapper(name = "serviceDescriptors", required = true)
	@XmlElement(name = "serviceDescriptor", required = false)
	List<ServiceDescriptor> getServiceDescriptors(@WebParam(name = "serviceDescriptorId") List<String> serviceDescriptorIds) throws RiceIllegalArgumentException;
	
	@WebMethod(operationName = "publishService")
	@WebResult(name = "serviceEndpoint")
	@XmlElement(name = "serviceEndpoint", required = true)
	ServiceEndpoint publishService(@WebParam(name = "serviceEndpoint") ServiceEndpoint serviceEndpoint) throws RiceIllegalArgumentException;

	@WebMethod(operationName = "publishServices")
	@WebResult(name = "serviceEndpoints")
	@XmlElementWrapper(name = "serviceEndpoints", required = true)
	@XmlElement(name = "serviceEndpoint", required = false)
	List<ServiceEndpoint> publishServices(@WebParam(name = "serviceEndpoint") List<ServiceEndpoint> serviceEndpoints) throws RiceIllegalArgumentException;
		
	@WebMethod(operationName = "removeServiceEndpoint")
	@WebResult(name = "serviceEndpoint")
	@XmlElement(name = "serviceEndpoint", required = false)
	ServiceEndpoint removeServiceEndpoint(@WebParam(name = "serviceId") String serviceId) throws RiceIllegalArgumentException;
	
	@WebMethod(operationName = "removeServiceEndpoints")
	@WebResult(name = "serviceEndpoints")
	@XmlElementWrapper(name = "serviceEndpoints", required = true)
	@XmlElement(name = "serviceEndpoint", required = false)
	List<ServiceEndpoint> removeServiceEndpoints(@WebParam(name = "serviceId") List<String> serviceIds) throws RiceIllegalArgumentException;
	
	@WebMethod(operationName = "removeAndPublish")
	@WebResult(name = "removeAndPublishResult")
	@XmlElement(name = "removeAndPublishResult", required = true)
	RemoveAndPublishResult removeAndPublish(@WebParam(name = "removeServiceId") List<String> removeServiceIds,
			@WebParam(name = "publishServiceEndpoint") List<ServiceEndpoint> publishServiceEndpoints);
	
	@WebMethod(operationName = "updateStatus")
	void updateStatus(@WebParam(name = "serviceId") String serviceId, @WebParam(name = "status") String status) throws RiceIllegalArgumentException;
	
	@WebMethod(operationName = "updateStatuses")
	void updateStatuses(@WebParam(name = "serviceId") List<String> serviceIds, @WebParam(name = "status") String status) throws RiceIllegalArgumentException;
	
	@WebMethod(operationName = "takeInstanceOffline")
	void takeInstanceOffline(@WebParam(name = "instanceId") String instanceId) throws RiceIllegalArgumentException;
	
}
