package edu.iu.uis.eden.messaging.bam.dao;

public interface BAMConfigDAO {
	public void saveEnabledState(boolean enabled);
	public boolean getEnabledState();
}
