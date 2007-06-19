package org.kuali.rice.kns.config;

import org.kuali.rice.ojb.BaseOjbConfigurer;

public class OJBConfigurer extends BaseOjbConfigurer {

	@Override
	protected String getJcdAlias() {
		return "knsDataSource";
	}

	@Override
	protected String getMetadataLocation() {
		return "classpath:OJB-repository-kns.xml";
	}
}