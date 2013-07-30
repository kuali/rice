/*
 * Copyright 2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krad.data.jpa;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kuali.rice.krad.data.KradDataServiceLocator;
import org.kuali.rice.krad.data.metadata.DataObjectMetadata;
import org.kuali.rice.krad.data.provider.MetadataProvider;
import org.kuali.rice.krad.data.provider.impl.CompositeMetadataProviderImpl;
import org.kuali.rice.krad.test.KRADTestCase;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

/**
 * TODO kellerj don't forget to fill this in.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class JpaMetadataProviderTest extends KRADTestCase {

    /**
     * This method ...
     *
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {}

    /**
     * This overridden method ...
     *
     * @see org.kuali.rice.test.BaselineTestCase#setUp()
     */
    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void verifyInRegistry() {
        assertNotNull( "Unable to obtain the registry", KradDataServiceLocator.getProviderRegistry() );
        List<MetadataProvider> metadataProviders = KradDataServiceLocator.getProviderRegistry().getMetadataProviders();
        assertNotNull( "metadata provider list was null", metadataProviders );
        Assert.assertEquals( "There should be only one metadata provider defined", 1, metadataProviders.size() );
        Assert.assertTrue( "entry should be the CompositeMetadataProviderImpl: " + metadataProviders.get(0), metadataProviders.get(0) instanceof CompositeMetadataProviderImpl );
    }

    @Test
    public void verifyRetrievalOfMetadata() {
        assertNotNull( "Unable to obtain the registry", KradDataServiceLocator.getProviderRegistry() );
        List<MetadataProvider> metadataProviders = KradDataServiceLocator.getProviderRegistry().getMetadataProviders();
        Map<Class<?>, DataObjectMetadata> provideMetadata = metadataProviders.get(0).provideMetadata();
        assertNotNull("returned metadata map should not be null", provideMetadata);
        Assert.assertFalse("returned metadata map should not be empty", provideMetadata.isEmpty());
    }
}
