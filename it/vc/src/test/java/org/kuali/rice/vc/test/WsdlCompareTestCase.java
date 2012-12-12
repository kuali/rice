/*
 * Copyright 2006-2012 The Kuali Foundation
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

package org.kuali.rice.vc.test;

import com.predic8.schema.ComplexType;
import com.predic8.schema.Sequence;
import com.predic8.soamodel.Difference;
import com.predic8.wsdl.Definitions;
import com.predic8.wsdl.Operation;
import com.predic8.wsdl.PortType;
import com.predic8.wsdl.WSDLParser;
import com.predic8.wsdl.diff.WsdlDiffGenerator;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.config.property.Config;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.lifecycle.BaseLifecycle;
import org.kuali.rice.core.api.lifecycle.Lifecycle;
import org.kuali.rice.core.framework.resourceloader.SpringResourceLoader;
import org.kuali.rice.test.BaselineTestCase;

import javax.xml.namespace.QName;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertTrue;


/*
 *  Compatible Changes
 *   - adding a new WSDL operation definition and associated message definitions
 *   - adding a new WSDL port type definition and associated operation definitions
 *   - adding new WSDL binding and service definitions
 *   - adding a new optional XML Schema element or attribute declaration to a message definition
 *   - reducing the constraint granularity of an XML Schema element or attribute of a message definition type
 *   - adding a new XML Schema wildcard to a message definition type
 *   - adding a new optional WS-Policy assertion
 *   - adding a new WS-Policy alternative
 *
 * Incompatible Changes
 *   - renaming an existing WSDL operation definition
 *   - removing an existing WSDL operation definition
 *   - changing the MEP of an existing WSDL operation definition
 *   - adding a fault message to an existing WSDL operation definition
 *   - adding a new required XML Schema element or attribute declaration to a message definition
 *   - increasing the constraint granularity of an XML Schema element or attribute declaration of a message definition
 *   - renaming an optional or required XML Schema element or attribute in a message definition
 *   - removing an optional or required XML Schema element or attribute or wildcard from a message definition
 *   - adding a new required WS-Policy assertion or expression
 *   - adding a new ignorable WS-Policy expression (most of the time)
 */
@BaselineTestCase.BaselineMode(BaselineTestCase.Mode.ROLLBACK)
public abstract class WsdlCompareTestCase extends BaselineTestCase {
    private static final Logger LOG = Logger.getLogger(WsdlCompareTestCase.class);
    private static final String WSDL_URL = "wsdl.test.previous.url";
    private static final String WSDL_PREVIOUS_VERSION = "wsdl.test.previous.version";
    private String wsdlUrlPrefix;
    private String previousVersion;
    //private static final String MODULE_NAME = "vc";

    public WsdlCompareTestCase(String moduleName) {
        super(moduleName);
    }

    protected void verifyWsdlDifferences(Difference diff, String level, String previousDescription) {
        LOG.info(level + "change: " + diff.getDescription());
        if (!diff.isSafe() && !diff.isBreaks()) {
            LOG.info(level + "non-breaking change" + diff.getDescription());
        } else {
            if (diff.isBreaks()) {
                LOG.error(level + "breaking change: " + diff.getType() + diff.getDescription());
            }
        }

        assertTrue("Difference must be determined to not be breaking: " + diff.getDescription(), !diff.isBreaks());

        //check for operation based sequence changes
        checkForOperationBasedChanges(diff);

        for (Difference moreDiff : diff.getDiffs())  {
            verifyWsdlDifferences(moreDiff, level + "  ", diff.getDescription());
        }
    }

    /*
     * This method is essentially an extra check because java2ws marks parameters on methods as minOccurs=0, which means
     * as far as the wsdl comparison, adding a new parameter is ok, because it isn't required.
     *
     * Unfortunately, that adding the parameter breaks compatibility for us because it invalidates the java interface.
     *
     * So, This method goes through, and checks to see if the sequence change is on one of the services Operators.  If it
     * is on an operator, and there is a difference in type of the operator, we've broken compatibility and should fail.
     */
    private void checkForOperationBasedChanges(Difference diff) {
        if ("sequence".equals(diff.getType())
                && diff.getA() != null
                && diff.getB() != null) {
            Sequence oldSequence = (Sequence)diff.getA();
            Sequence newSequence = (Sequence)diff.getB();
            if (newSequence.getParent() instanceof ComplexType) {
                ComplexType parent = (ComplexType)newSequence.getParent();
                String serviceName = newSequence.getSchema().getDefinitions().getName();
                PortType portType = newSequence.getSchema().getDefinitions().getPortType(serviceName);
                if (portType != null) {
                    Operation operation = portType.getOperation(parent.getName());

                    assertTrue("Element cannot be added to a sequence if sequence is an Operation " + diff
                            .getDescription(), operation == null);
                }
            }
        }
    }

    protected List<Difference> compareWsdlDefinitions(String oldWsdl, String newWsdl) {
        WSDLParser parser = new WSDLParser();

        Definitions wsdl1;
        Definitions wsdl2 = parser.parse(newWsdl);
        try {
            wsdl1 = parser.parse(oldWsdl);
        } catch (com.predic8.xml.util.ResourceDownloadException e) {
            wsdl1 = wsdl2;
        }

        WsdlDiffGenerator diffGen = new WsdlDiffGenerator(wsdl1, wsdl2);
        return diffGen.compare();
    }

    protected String getPreviousVersionWsdlUrl(String wsdlFile) {
        if (getWsdlUrlPrefix() == null
                || getPreviousVersion() == null) {
            populateWsdlUrlPrefix();
        }
        if (StringUtils.isBlank(getWsdlUrlPrefix()) ||
                StringUtils.isBlank(getPreviousVersion())) {
            return null;
        }
        StringBuilder oldWsdl = new StringBuilder(getWsdlUrlPrefix());
        oldWsdl.append("rice-");
        oldWsdl.append(getModuleName());
        oldWsdl.append("-api-");
        oldWsdl.append(getPreviousVersion());
        oldWsdl.append("-");
        oldWsdl.append(wsdlFile);

        return oldWsdl.toString();
    }

    //String oldWsdl = MAVEN_REPO_PREFIX + MODULE + "-api/" + PREVIOUS_VERSION + "/rice-" + MODULE + "-api-" + PREVIOUS_VERSION + "-" + file.getName();
    private void populateWsdlUrlPrefix() {
        String wsdlUrl = ConfigContext.getCurrentContextConfig().getProperty(WSDL_URL);

        if (StringUtils.isNotBlank(wsdlUrl)
                && StringUtils.isNotBlank(getPreviousVersion())) {
            StringBuilder urlBuilder = new StringBuilder(wsdlUrl);
            if (!wsdlUrl.endsWith("/")) {
                urlBuilder.append("/");
            }
            urlBuilder.append("rice-");
            urlBuilder.append(getModuleName());
            urlBuilder.append("-api/");
            urlBuilder.append(getPreviousVersion());
            urlBuilder.append("/");
            setWsdlUrlPrefix(urlBuilder.toString());
        } else {
            setWsdlUrlPrefix("");
        }
    }

    protected void compareWsdlFiles(File[] files) {
        assertTrue("There should be wsdls to compare", files != null  && files.length > 0);
        for (File file : files) {
            if (file.getName().endsWith(".wsdl")) {
                LOG.info("new wsdl: " + file.getAbsolutePath());
                String newWsdl = file.getAbsolutePath();
                String oldWsdl = getPreviousVersionWsdlUrl(file.getName());
                if (oldWsdl == null) {
                    LOG.warn("Old wsdl not found.  Comparing against same version");
                    oldWsdl = newWsdl;
                }
                LOG.info("old wsdl: " + oldWsdl + "\n");
                List<Difference> differences = compareWsdlDefinitions(oldWsdl, newWsdl);
                for (Difference diff : differences) {
                    verifyWsdlDifferences(diff, "", diff.getDescription());
                }
            }
        }
    }

    public String getWsdlUrlPrefix() {
        return wsdlUrlPrefix;
    }

    public void setWsdlUrlPrefix(String wsdlUrlPrefix) {
        this.wsdlUrlPrefix = wsdlUrlPrefix;
    }

    public String getPreviousVersion() {
        if (StringUtils.isEmpty(this.previousVersion)) {
            this.previousVersion = ConfigContext.getCurrentContextConfig().getProperty(WSDL_PREVIOUS_VERSION);
        }
        return this.previousVersion;
    }

    public void setPreviousVersion(String previousVersion) {
        this.previousVersion = previousVersion;
    }

    @Override
    protected Lifecycle getLoadApplicationLifecycle() {
        SpringResourceLoader springResourceLoader = new SpringResourceLoader(new QName("VCTestHarnessResourceLoader"), "classpath:VCTestHarnessSpringBeans.xml", null);
        springResourceLoader.setParentSpringResourceLoader(getTestHarnessSpringResourceLoader());
        return springResourceLoader;
    }

    @Override
    protected List<Lifecycle> getPerTestLifecycles() {
        return new ArrayList<Lifecycle>();
    }

    @Override
    protected List<Lifecycle> getSuiteLifecycles() {
        List<Lifecycle> lifecycles = new LinkedList<Lifecycle>();

        /**
         * Initializes Rice configuration from the test harness configuration file.
         */
        lifecycles.add(new BaseLifecycle() {
            @Override
            public void start() throws Exception {
                Config config = getTestHarnessConfig();
                ConfigContext.init(config);
                super.start();
            }
        });

        return lifecycles;
    }
}
