package edu.iu.uis.eden.messaging.dao;

import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import edu.iu.uis.eden.messaging.PersistedMessage;

public interface MessageQueueDAO {

	public void remove(PersistedMessage routeQueue);
	public void save(PersistedMessage routeQueue);
	public PersistedMessage findByRouteQueueId(Long routeQueueId);
	
	public List<PersistedMessage> findAll();
    public List<PersistedMessage> findAll(int maxRows);
	public List<PersistedMessage> getNextDocument();
	public List<PersistedMessage> getNextDocuments(Integer maxDocuments);
	public List<PersistedMessage> findByServiceName(QName serviceName, String methodName);
  
  /**
   * Finds the persisted messages that match the values passed into the 
   * criteriaValues Map, with an EqualTo criteria for each.
   * 
   * @param criteriaValues A Map of Key/Value pairs, where the Key is a string holding the field 
   *                       name, and the Value is a string holding the value to match.
   * @return A populated (or empty) list containing the results of the search.  If no matches are made, 
   *         an empty list will be returned.
   */
  public List<PersistedMessage> findByValues(Map<String,String> criteriaValues);
  
}