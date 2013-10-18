package org.kuali.rice.sql;

import org.kuali.common.util.project.model.KualiGroup;
import org.kuali.common.util.project.model.ProjectIdentifier;

public abstract class SqlProjectConstants {

	// The groupId and artifactId used here must exactly match what is in the pom
	public static final ProjectIdentifier ID = new ProjectIdentifier(KualiGroup.RICE.getId(), "rice-sql");

}
