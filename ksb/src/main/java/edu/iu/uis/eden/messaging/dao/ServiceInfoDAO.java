package edu.iu.uis.eden.messaging.dao;

import java.util.List;

import edu.iu.uis.eden.messaging.ServiceInfo;

public interface ServiceInfoDAO {

	public void addEntry(ServiceInfo entry);
	public void removeEntry(ServiceInfo entry);
	public List<ServiceInfo> fetchAll();
	public List<ServiceInfo> findLocallyPublishedServices(String ipNumber, String messageEntity);
	public void removeLocallyPublishedServices(String ipNumber, String messageEntity);
	public ServiceInfo findServiceInfo(Long serviceInfoId);
}