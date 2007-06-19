package edu.iu.uis.eden.messaging.bam.dao;

import org.kuali.rice.config.Config;
import org.kuali.rice.config.ConfigDAOSupport;

public class BAMConfigDAOImpl extends ConfigDAOSupport implements BAMConfigDAO {

	public void saveEnabledState(boolean enabled) {
		getNodeSettings().setSetting(Config.BAM_ENABLED, String.valueOf(enabled));
	}

	public boolean getEnabledState() {
		return getBooleanProperty(Config.BAM_ENABLED, false);
	}

}
