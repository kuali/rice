package edu.iu.uis.eden.messaging;

import java.util.ArrayList;
import java.util.List;

import org.kuali.bus.services.KSBServiceLocator;
import org.kuali.rice.core.Core;
import org.kuali.rice.exceptions.RiceRuntimeException;

import edu.iu.uis.eden.messaging.dao.ServiceInfoDAO;

public class ServiceRegistryImpl implements ServiceRegistry {

	private ServiceInfoDAO dao;

	public void saveEntry(ServiceInfo entry) {
		try {
			Object service = entry.getServiceDefinition().getService();
			entry.getServiceDefinition().setService(null);
			entry.setSerializedMessageEntity(KSBServiceLocator.getMessageHelper().serializeObject(entry.getServiceDefinition()));
			entry.getServiceDefinition().setService(service);
		} catch (Exception e) {
			throw new RiceRuntimeException(e);
		}
		getDao().addEntry(entry);
	}

	public List<ServiceInfo> fetchAll() {
		return getDao().fetchAll();
	}

	public List<ServiceInfo> findLocallyPublishedServices(String ipNumber, String messageEntity) {
		if (Core.getCurrentContextConfig().getDevMode()) {
			return new ArrayList<ServiceInfo>();
		}
		return getDao().findLocallyPublishedServices(ipNumber, messageEntity);
	}

	public void removeEntry(ServiceInfo entry) {
		getDao().removeEntry(entry);
	}

	public void removeLocallyPublishedServices(String ipNumber, String messageEntity) {
		getDao().removeLocallyPublishedServices(ipNumber, messageEntity);
	}

	public ServiceInfoDAO getDao() {
		return this.dao;
	}

	public void setDao(ServiceInfoDAO dao) {
		this.dao = dao;
	}

	public void remove(List<ServiceInfo> serviceEntries) {
		for (ServiceInfo info : serviceEntries) {
			removeEntry(info);
		}
	}

	public void save(List<ServiceInfo> serviceEntries) {
		for (ServiceInfo info : serviceEntries) {
			saveEntry(info);
		}
	}

	public void markServicesDead(List<ServiceInfo> serviceEntries) {
		for (ServiceInfo info : serviceEntries) {
			// there is contention on these records from multiple nodes and odds
			// are the
			// one we have in memory is stale. refetch and mork dead.
			ServiceInfo currentInfo = getDao().findServiceInfo(info.getMessageEntryId());
			currentInfo.setAlive(false);
			try {
				saveEntry(currentInfo);
			} catch (Exception e) {
				boolean isOptimisticLockExp = KSBServiceLocator.getOptimisticLockFailureService().checkForOptimisticLockFailure(e);
				// suppress optimistic lock exceptions, it's collision with
				// other nodes
				if (!isOptimisticLockExp) {
					throw (RuntimeException) e;
				}
			}

		}
	}
}