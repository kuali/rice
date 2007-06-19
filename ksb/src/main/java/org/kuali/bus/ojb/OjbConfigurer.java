package org.kuali.bus.ojb;

import org.kuali.rice.ojb.BaseOjbConfigurer;

public class OjbConfigurer extends BaseOjbConfigurer {

	private static final String DEFAULT_KSB_REPOSITORY_METADATA = "classpath:OJB-repository-ksb.xml";
	private static final String KSB_JCD_ALIAS = "ksbDataSource";

	@Override
	protected String getJcdAlias() {
		return KSB_JCD_ALIAS;
	}

	@Override
	protected String getMetadataLocation() {
		return DEFAULT_KSB_REPOSITORY_METADATA;
	}
}