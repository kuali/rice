/**
 * Copyright 2005-2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
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
 * Locates workflow XML documents available on the classpath and ingests them.
 * 
 * <p>
 * No file system access is required. The XML documents are ingested using Spring's {@code classpath:} notation to
 * locate them, open an {@link java.io.InputStream}, and feed them to the ingester service. Any workflow document
 * failing to be ingested correctly results in an exception being thrown.
 * </p>
 * 
 * <p>
 * If an explicit {@link XmlIngesterService} instance is not provided,
 * {@code CoreApiServiceLocator.getXmlIngesterService()} must be able to correctly locate {@link XmlIngesterService}.
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public final class IngestXmlExecutable implements Executable {

	private static final Logger logger = LoggerUtils.make();

	private static final String XML_SUFFIX = ".xml";

	private final List<String> xmlDocumentLocations;
	private final boolean skip;

	private final Optional<XmlIngesterService> xmlIngesterService;

    /**
     * {@inheritDoc}
     */
	@Override
	public void execute() {
		if (skip) {
			logger.info("Skipping XML ingestion");
			return;
		}

		long start = System.currentTimeMillis();
		logger.info("Starting XML Ingester.");

        for (String xmlDocumentLocation : xmlDocumentLocations) {
		    logger.info("Ingesting XML documents listed in [{}]", xmlDocumentLocation);
        }

		List<XmlDocCollection> xmlDocumentCollections = getXmlDocCollectionList(xmlDocumentLocations);
		logger.info("Found {} files to ingest.", Integer.valueOf(xmlDocumentCollections.size()));

		Collection<XmlDocCollection> failedXmlDocumentCollections = ingest(xmlDocumentCollections);
		validateNoFailures(failedXmlDocumentCollections);
		logger.info("There were zero failures ingesting {} XML documents", Integer.valueOf(xmlDocumentCollections.size()));

        long end = System.currentTimeMillis() - start;
		logger.info("Finished ingesting bootstrap XML - {}", FormatUtils.getTime(end));
	}

    /**
     * Gets the list of XML documents to ingest.
     *
     * @param locationListings the locations to search for XML documents to ingest
     *
     * @return the list of XML documents to ingest
     */
	private List<XmlDocCollection> getXmlDocCollectionList(List<String> locationListings) {
		List<XmlDocCollection> xmlDocCollectionList = Lists.newArrayList();
		List<String> locations = LocationUtils.getLocations(locationListings);

		for (String location : locations) {
			Preconditions.checkState(StringUtils.endsWith(location.toLowerCase(), XML_SUFFIX), "[%s] is not an XML document", location);
			Preconditions.checkState(LocationUtils.exists(location), "[%s] does not exist", location);

            logger.info("[{}]", location);

            xmlDocCollectionList.add(new LocationXmlDocCollection(location));
		}

		return xmlDocCollectionList;
	}

    /**
     * Ingests the documents in {@code collections}.
     *
     * @param collections the list of XML documents to ingest
     *
     * @return the list of XML documents that failed to ingest
     */
    private Collection<XmlDocCollection> ingest(List<XmlDocCollection> collections) {
        try {
            return getXmlIngesterService().ingest(collections);
        } catch (Exception e) {
            throw new IllegalStateException("Unexpected error ingesting XML documents", e);
        }
    }

    /**
     * Verifies whether there are any failures in {@code failedXmlDocumentCollections} and lists them if there are.
     *
     * @param failedXmlDocumentCollections the list of failures from the ingestion process
     */
	private void validateNoFailures(Collection<XmlDocCollection> failedXmlDocumentCollections) {
		if (failedXmlDocumentCollections.isEmpty()) {
			return;
		}

        List<String> failureNamesList = Lists.newArrayList();
		for (XmlDocCollection failedXmlDocumentCollection : failedXmlDocumentCollections) {
            failureNamesList.add(failedXmlDocumentCollection.getFile().getName());
		}

        String failureNames = StringUtils.join(failureNamesList, ", ");
		Preconditions.checkState(false, "%s XML documents failed to ingest -> [%s]", Integer.valueOf(failedXmlDocumentCollections.size()), failureNames);
	}

    /**
     * Returns the {@link XmlIngesterService}.
     *
     * @return the {@link XmlIngesterService}
     */
    public XmlIngesterService getXmlIngesterService() {
        return xmlIngesterService.isPresent() ? xmlIngesterService.get() : CoreApiServiceLocator.getXmlIngesterService();
    }

    private IngestXmlExecutable(Builder builder) {
        this.xmlDocumentLocations = builder.xmlDocumentLocations;
        this.skip = builder.skip;
        this.xmlIngesterService = builder.xmlIngesterService;
    }

    /**
     * Returns the builder for this {@code IngestXmlExecutable}.
     *
     * @param xmlDocumentLocations the list of locations with XML documents to ingest
     *
     * @return the builder for this {@code IngestXmlExecutable}
     */
    public static Builder builder(List<String> xmlDocumentLocations) {
        return new Builder(xmlDocumentLocations);
    }

    /**
     * Builds this {@link IngestXmlExecutable}.
     */
	public static class Builder {

		// Required
		private final List<String> xmlDocumentLocations;

		// Optional
		private Optional<XmlIngesterService> xmlIngesterService = Optional.absent();
		private boolean skip;

        /**
         * Builds the {@link IngestXmlExecutable} with a single {@code xmlDocumentLocation}.
         *
         * @param xmlDocumentLocation the location with an XML document to ingest
         */
		public Builder(String xmlDocumentLocation) {
			this.xmlDocumentLocations = Collections.singletonList(xmlDocumentLocation);
        }

        /**
         * Builds the {@link IngestXmlExecutable} with multiple {@code xmlDocumentLocations}.
         *
         * @param xmlDocumentLocations the list of locations with XML documents to ingest
         */
        public Builder(List<String> xmlDocumentLocations) {
            this.xmlDocumentLocations = xmlDocumentLocations;
        }

        /**
         * Sets the {@link XmlIngesterService}.
         *
         * @param service the {@link XmlIngesterService} to set
         *
         * @return this {@code Builder}
         */
		public Builder service(XmlIngesterService service) {
			this.xmlIngesterService = Optional.of(service);
			return this;
		}

        /**
         * Sets whether to skip this executable or not.
         *
         * @param skip whether to skip this executable or not
         *
         * @return this {@code Builder}
         */
		public Builder skip(boolean skip) {
			this.skip = skip;
			return this;
		}

        /**
         * Builds the {@link IngestXmlExecutable}.
         *
         * @return the built {@link IngestXmlExecutable}
         */
		public IngestXmlExecutable build() {
			IngestXmlExecutable instance = new IngestXmlExecutable(this);

            validate(instance);

            return instance;
		}

		private static void validate(IngestXmlExecutable instance) {
			Preconditions.checkNotNull(instance.xmlIngesterService, "service cannot be null");
			Preconditions.checkArgument(!CollectionUtils.isEmpty(instance.xmlDocumentLocations), "locationListings cannot be empty");

            for (String locationListing : instance.xmlDocumentLocations) {
                Preconditions.checkArgument(!StringUtils.isBlank(locationListing), "locationListings cannot have blank entries");
			    Preconditions.checkArgument(LocationUtils.exists(locationListing), "[%s] does not exist", locationListing);
            }
		}

	}

}