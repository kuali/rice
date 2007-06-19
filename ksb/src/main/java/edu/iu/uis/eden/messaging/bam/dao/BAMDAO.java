package edu.iu.uis.eden.messaging.bam.dao;

import java.util.List;

import javax.xml.namespace.QName;

import org.kuali.rice.definition.ObjectDefinition;

import edu.iu.uis.eden.messaging.bam.BAMTargetEntry;

public interface BAMDAO {
	public List<BAMTargetEntry> getCallsForService(QName serviceName);
	public List<BAMTargetEntry> getCallsForRemotedClasses(ObjectDefinition objDef);
	public void save(BAMTargetEntry bamEntry);
	public void clearBAMTables();
	public List<BAMTargetEntry> getCallsForService(QName serviceName, String methodName);
	public List<BAMTargetEntry> getCallsForRemotedClasses(ObjectDefinition objDef, String methodName);
}
