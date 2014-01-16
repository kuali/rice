/**
 * Copyright 2005-2013 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you cannot use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.xml.ingest;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.kuali.common.util.FormatUtils;
import org.kuali.common.util.LocationUtils;
import org.kuali.common.util.execute.Executable;
import org.kuali.common.util.log.LoggerUtils;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.impex.xml.LocationXmlDocCollection;
import org.kuali.rice.core.api.impex.xml.XmlDocCollection;
import org.kuali.rice.core.api.impex.xml.XmlIngesterService;
import org.slf4j.Logger;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/**
 * <p>
 * Locates workflow XML documents available on the classpath and ingests them.
 * </p>
 * 
 * <p>
 * No file system access is required. The XML documents are ingested using Spring's {@code classpath:} notation to
 * locate them, open an {@code InputStream}, and feed them to the ingester service. Any workflow document failing to be
 * ingested correctly results in an exception being thrown.
 * </p>
 * 
 * <p>
 * If an explicit {@code XmlIngesterService} instance is not provided,
 * {@code CoreApiServiceLocator.getXmlIngesterService()} must be able to correctly locate XmlIngesterService.
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public final class IngestXmlExecutable implements Executable {

	private static final Logger logger = LoggerUtils.make();
	private static final String XML_SUFFIX = ".xml";

	private final String locationListing;
	private final boolean skip;
	private final Optional<XmlIngesterService> service;

	@Override
	public void execute() {
		if (skip) {
			logger.info("Skipping XML ingestion");
			return;
		}
		long start = System.currentTimeMillis();
		logger.info("Starting bootstrap XML Ingester.");
		logger.info("Ingesting XML documents listed in [{}]", locationListing);
		List<XmlDocCollection> collections = getXmlDocCollectionList(locationListing);
		logger.info("Found {} files to ingest.", collections.size());
		Collection<XmlDocCollection> failures = ingest(collections);
		validateNoFailures(failures);
		logger.info("There were zero failures ingesting {} XML documents", collections.size());
		logger.info("Finished ingesting bootstrap XML - {}", FormatUtils.getTime(System.currentTimeMillis() - start));
	}

	protected List<XmlDocCollection> getXmlDocCollectionList(String locationListing) {
		List<XmlDocCollection> list = Lists.newArrayList();
		List<String> locations = LocationUtils.getLocations(locationListing);
		for (String location : locations) {
			Preconditions.checkState(StringUtils.endsWith(location.toLowerCase(), XML_SUFFIX), "[%s] is not an XML document", location);
			Preconditions.checkState(LocationUtils.exists(location), "[%s] does not exist", location);
			logger.info("[{}]", location);
			XmlDocCollection element = new LocationXmlDocCollection(location);
			list.add(element);
		}
		return list;
	}

	protected void validateNoFailures(Collection<XmlDocCollection> failures) {
		if (failures.size() == 0) {
			return;
		}
		StringBuilder sb = new StringBuilder();
		for (XmlDocCollection failure : failures) {
			sb.append(failure.getFile().getName());
			sb.append(",");
		}
		String docs = sb.substring(0, sb.length() - 1); // Trim off the trailing comma
		Preconditions.checkState(false, "%s XML documents failed to ingest -> [%s]", failures.size(), docs);
	}

	protected Collection<XmlDocCollection> ingest(List<XmlDocCollection> collections) {
		XmlIngesterService service = this.service.isPresent() ? this.service.get() : CoreApiServiceLocator.getXmlIngesterService();
		try {
			return service.ingest(collections);
		} catch (Exception e) {
			throw new IllegalStateException("Unexpected error ingesting XML documents", e);
		}
	}

	private IngestXmlExecutable(Builder builder) {
		this.locationListing = builder.locationListing;
		this.skip = builder.skip;
		this.service = builder.service;
	}

	public static Builder builder(String locationListing) {
		return new Builder(locationListing);
	}

	public static class Builder {

		// Required
		private final String locationListing;

		// Optional
		private Optional<XmlIngesterService> service = Optional.absent();
		private boolean skip = false;

		public Builder(String locationListing) {
			this.locationListing = locationListing;
		}

		public Builder service(XmlIngesterService service) {
			this.service = Optional.of(service);
			return this;
		}

		public Builder skip(boolean skip) {
			this.skip = skip;
			return this;
		}

		public IngestXmlExecutable build() {
			IngestXmlExecutable instance = new IngestXmlExecutable(this);
			validate(instance);
			return instance;
		}

		private static void validate(IngestXmlExecutable instance) {
			Preconditions.checkNotNull(instance.service, "service cannot be null");
			Preconditions.checkArgument(!StringUtils.isBlank(instance.locationListing), "locationListing cannot be blank");
			Preconditions.checkArgument(LocationUtils.exists(instance.locationListing), "[%s] does not exist", instance.locationListing);
		}
	}

	public Optional<XmlIngesterService> getService() {
		return service;
	}

	public String getLocationListing() {
		return locationListing;
	}

	public boolean isSkip() {
		return skip;
	}

}