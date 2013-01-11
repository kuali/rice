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
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.kuali.rice.core.api.config.property.Config;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.lifecycle.BaseLifecycle;
import org.kuali.rice.core.api.lifecycle.Lifecycle;
import org.kuali.rice.core.framework.resourceloader.SpringResourceLoader;
import org.kuali.rice.test.BaselineTestCase;

import javax.xml.namespace.QName;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private String previousVersion;

    private static final List<String> ignoreBreakageRegexps = Arrays.asList(
            ".*Position of any null changed.$" // change in position of an 'any' doesn't indicate a breakage for us
    );

    public WsdlCompareTestCase(String moduleName) {
        super(moduleName);
    }

    protected List<String> verifyWsdlDifferences(Difference diff, String level) {
        List<String> results = new ArrayList<String>();

        if (diff.isBreaks()) {
            boolean ignore = false;
            for (String ignoreBreakageRegexp : ignoreBreakageRegexps) {
                if (diff.getDescription().matches(ignoreBreakageRegexp)) {
                    ignore = true;
                    break;
                }
            }

            if (ignore) {
                LOG.info(level + "non-breaking change" + diff.getDescription());
            } else {
                LOG.error(level + "breaking change: " + diff.getType() + diff.getDescription());
                results.add(level + diff.getDescription());
            }
        }


        //check for operation based sequence changes
        String opBreakageString = checkForOperationBasedChanges(diff);
        if (opBreakageString != null) {
            results.add(level + opBreakageString);
        }

        for (Difference moreDiff : diff.getDiffs())  {
            List<String> childBreakages = verifyWsdlDifferences(moreDiff, level + "  ");
            for (String childBreakage : childBreakages) {
                if (!diff.getDescription().trim().startsWith("Schema ")) {
                    results.add(level + diff.getDescription() + LINE_SEPARATOR + childBreakage);
                } else {
                    results.add(childBreakage);
                }
            }
        }

        return results;
    }

    /*
     * This method is essentially an extra check because java2ws marks parameters on methods as minOccurs=0, which means
     * as far as the wsdl comparison, adding a new parameter is ok, because it isn't required.
     *
     * Unfortunately, that adding the parameter breaks compatibility for us because it invalidates the java interface.
     *
     * So, This method goes through, and checks to see if the sequence change is on one of the services Operators.  If it
     * is on an operator, and there is a difference in type of the operator, we've broken compatibility and should fail.
     *
     * returns a string if there is a breakage, null otherwise
     */
    private String checkForOperationBasedChanges(Difference diff) {
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

                    if (operation != null) {
                        return "Element cannot be added to a sequence if sequence is an Operation " +
                                diff.getDescription();
                    }
//                    assertTrue("Element cannot be added to a sequence if sequence is an Operation " + diff
//                            .getDescription(), operation == null);
                }
            }
        }
        return null;
    }

    protected List<Difference> compareWsdlDefinitions(String oldWsdl, String newWsdl) {
        WSDLParser parser = new WSDLParser();

        Definitions wsdl1;
        Definitions wsdl2;
        try {
            wsdl1 = parser.parse(oldWsdl);
        } catch (com.predic8.xml.util.ResourceDownloadException e) {
            LOG.error("COULDN'T PARSE " + oldWsdl);
            return Collections.emptyList();
        }
        try {
            wsdl2 = parser.parse(newWsdl);
        } catch (com.predic8.xml.util.ResourceDownloadException e) {
            LOG.error("COULDN'T PARSE " + newWsdl);
            return Collections.emptyList();
        }

        WsdlDiffGenerator diffGen = new WsdlDiffGenerator(wsdl1, wsdl2);
        return diffGen.compare();
    }

    protected String getPreviousVersionWsdlUrl(String wsdlFile, MavenVersion previousVersion) {

        StringBuilder oldWsdl = new StringBuilder(buildWsdlUrlPrefix(previousVersion.getOriginalForm()));
        oldWsdl.append("rice-");
        oldWsdl.append(getModuleName());
        oldWsdl.append("-api-");
        oldWsdl.append(previousVersion.getOriginalForm());
        oldWsdl.append("-");
        oldWsdl.append(wsdlFile);

        return oldWsdl.toString();
    }

    //String oldWsdl = MAVEN_REPO_PREFIX + MODULE + "-api/" + PREVIOUS_VERSION + "/rice-" + MODULE + "-api-" + PREVIOUS_VERSION + "-" + file.getName();
    private String buildWsdlUrlPrefix(String previousVersion) {
        String wsdlUrl = ConfigContext.getCurrentContextConfig().getProperty(WSDL_URL);

        if (StringUtils.isNotBlank(wsdlUrl)
                && StringUtils.isNotBlank(previousVersion)) {
            StringBuilder urlBuilder = new StringBuilder(wsdlUrl);
            if (!wsdlUrl.endsWith("/")) {
                urlBuilder.append("/");
            }
            urlBuilder.append("rice-");
            urlBuilder.append(getModuleName());
            urlBuilder.append("-api/");
            urlBuilder.append(previousVersion);
            urlBuilder.append("/");

            return urlBuilder.toString();

        } else {
            throw new RuntimeException("Couldn't build wsdl url prefix");
        }
    }

    protected void compareWsdlFiles(File[] files) {
        List<VersionCompatibilityBreakage> breakages = new ArrayList<VersionCompatibilityBreakage>();

        assertTrue("There should be wsdls to compare", files != null  && files.length > 0);

        MavenVersion currentVersion = getCurrentMavenVersion();
        List<MavenVersion> versions = getInterveningVersions();

        for (File file : files) {
            if (file.getName().endsWith(".wsdl")) {
                LOG.info("new wsdl: " + file.getAbsolutePath());
                String newWsdl = file.getAbsolutePath();

                Iterator<MavenVersion> versionsIter = versions.iterator();
                boolean processedCurrent = false;

                MavenVersion v1;
                MavenVersion v2 = versionsIter.next();

                // walk the versions, checking diffs between each consecutive pair
                while (versionsIter.hasNext() || !processedCurrent) {
                    v1 = v2; // march down the list

                    String v1Wsdl = getPreviousVersionWsdlUrl(file.getName(), v1);
                    String v2Wsdl;

                    if (versionsIter.hasNext()) {
                        v2 = versionsIter.next();
                        v2Wsdl = getPreviousVersionWsdlUrl(file.getName(), v2);
                    } else {
                        v2 = currentVersion;
                        v2Wsdl = file.getAbsolutePath();
                        processedCurrent = true;
                    }

                    LOG.info("checking version transition: " + v1.getOriginalForm() + " -> " + v2.getOriginalForm());

                    if (v1Wsdl == null) {
                        LOG.warn("SKIPPING check, wsdl not found for " + v1Wsdl);
                    } else if (v2Wsdl == null) {
                        LOG.warn("SKIPPING check, wsdl not found for " + v2Wsdl);
                    } else {

                        List<Difference> differences = compareWsdlDefinitions(v1Wsdl, v2Wsdl);
                        for (Difference diff : differences) {
                            List<String> breakageStrings = verifyWsdlDifferences(diff, "");

                            for (String breakage : breakageStrings) {
                                breakages.add(new VersionCompatibilityBreakage(v1, v2, v1Wsdl, v2Wsdl, breakage));
                            }
                        }
                    }
                }

            }


        }

        if (!breakages.isEmpty()) {
            fail(buildBreakagesSummary(breakages));
        }
    }

    protected String buildBreakagesSummary(List<VersionCompatibilityBreakage> breakages) {
        StringBuilder errorsStringBuilder =
                new StringBuilder(LINE_SEPARATOR + "!!!!! Detected " + breakages.size() + " VC Breakages !!!!!"
                        + LINE_SEPARATOR);

        MavenVersion lastOldVersion = null;
        String lastOldWsdlUrl = "";

        for (VersionCompatibilityBreakage breakage : breakages) {
            // being lazy and using '!=' instead of '!lastOldVersion.equals(...)' to avoid NPEs and extra checks
            if (lastOldVersion != breakage.oldMavenVersion || lastOldWsdlUrl != breakage.oldWsdlUrl) {
                lastOldVersion = breakage.oldMavenVersion;
                lastOldWsdlUrl = breakage.oldWsdlUrl;

                errorsStringBuilder.append(LINE_SEPARATOR + "Old Version: " + lastOldVersion.getOriginalForm()
                        +", wsdl: " + lastOldWsdlUrl);
                errorsStringBuilder.append(LINE_SEPARATOR + "New Version: " + breakage.newMavenVersion.getOriginalForm()
                        +", wsdl: " + breakage.newWsdlUrl + LINE_SEPARATOR + LINE_SEPARATOR);
            }
            errorsStringBuilder.append(breakage.breakageMessage + LINE_SEPARATOR);
        }
        return errorsStringBuilder.toString();
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


    protected List<MavenVersion> getInterveningVersions() {
        ArrayList<MavenVersion> results = new ArrayList<MavenVersion>();

        MavenVersion previousVersion = new MavenVersion(getPreviousVersion());

        MavenVersion currentVersion = getCurrentMavenVersion();

        if (currentVersion.compareTo(previousVersion) <= 0) {
            throw new IllegalStateException("currentVersion " + currentVersion +
                    "  is <= previousVersion " + previousVersion);
        }
        String searchContent = getMavenSearchResults();

        LinkedList<MavenVersion> riceVersions = parseSearchResults(searchContent);

        for (MavenVersion riceVersion : riceVersions) {
            if ( currentVersion.compareTo(riceVersion) > 0 &&
                    previousVersion.compareTo(riceVersion) <= 0 &&
                    "".equals(riceVersion.getQualifier()) ) {
                results.add(riceVersion);
            }
        }

        return results;
    }

    private MavenVersion getCurrentMavenVersion() {
        return new MavenVersion(ConfigContext.getCurrentContextConfig().getProperty("rice.version"));
    }

    private LinkedList<MavenVersion> parseSearchResults(String searchContent) {
        LinkedList<MavenVersion> riceVersions = new LinkedList<MavenVersion>();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode;
        try {
            rootNode = mapper.readTree(searchContent);
        } catch (IOException e) {
            throw new RuntimeException("Can't parse maven search results", e);
        }
        JsonNode docsNode = rootNode.get("response").get("docs");

        for (JsonNode node : docsNode) {
            String versionStr = node.get("v").toString();
            // System.out.println(versionStr);
            riceVersions.add(new MavenVersion(versionStr.replace(/* strip out surrounding quotes */ "\"","")));
        }

        Collections.sort(riceVersions);
        return riceVersions;
    }

    private String getMavenSearchResults() {
        // using the maven search REST api specified here: http://search.maven.org/#api
        // this query gets all versions of Rice from maven central
        final String mavenSearchUrlString =
                "http://search.maven.org/solrsearch/select?q=g:%22org.kuali.rice%22+AND+a:%22rice%22&core=gav&rows=20&wt=json";

        URL mavenSearchUrl;

        try {
            mavenSearchUrl = new URL(mavenSearchUrlString);
        } catch (MalformedURLException e) {
            throw new RuntimeException("can't parse maven search url", e);
        }

        StringBuilder contentBuilder = new StringBuilder();
        BufferedReader contentReader;
        try {
            contentReader = new BufferedReader(new InputStreamReader(mavenSearchUrl.openStream()));
            String line;
            while (null != (line = contentReader.readLine())) {
                contentBuilder.append(line + LINE_SEPARATOR);
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to read search results", e);
        }
        return contentBuilder.toString();
    }

    /**
     * Utility class for parsing and comparing maven versions
     */
    protected static class MavenVersion implements Comparable<MavenVersion> {
        private static final Pattern PERIOD_PATTERN = Pattern.compile("\\.");
        private final List<Integer> numbers;
        private final String originalForm;
        private final String qualifier;

        public MavenVersion(String versionString) {
            originalForm = versionString;
            if (versionString == null || "".equals(versionString.trim())) {
                throw new IllegalArgumentException("empty or null version string");
            }
            String versionPart;
            int dashIndex = versionString.indexOf('-');
            if (dashIndex != -1 && versionString.length()-1 > dashIndex) {
                qualifier = versionString.substring(dashIndex+1).trim();
                versionPart = versionString.substring(0,dashIndex);
            } else {
                versionPart = versionString;
                qualifier = "";
            }
            String [] versionArray = PERIOD_PATTERN.split(versionPart);

            List<Integer> numbersBuilder = new ArrayList<Integer>(versionArray.length);

            for (String versionParticle : versionArray) {
                numbersBuilder.add(Integer.valueOf(versionParticle));
            }

            numbers = Collections.unmodifiableList(numbersBuilder);
        }

        @Override
        public int compareTo(MavenVersion that) {
            Iterator<Integer> thisNumbersIter = this.numbers.iterator();
            Iterator<Integer> thatNumbersIter = that.numbers.iterator();

            while (thisNumbersIter.hasNext()) {
                // all else being equal, he/she who has the most digits wins
                if (!thatNumbersIter.hasNext()) return 1;

                int numberComparison = thisNumbersIter.next().compareTo(thatNumbersIter.next());

                // if one is greater than the other, we've established primacy
                if (numberComparison != 0) return numberComparison;
            }
            // all else being equal, he/she who has the most digits wins
            if (thatNumbersIter.hasNext()) return -1;

            return compareQualifiers(this.qualifier, that.qualifier);
        }

        private static int compareQualifiers(String thisQ, String thatQ) {
            // no qualifier is considered greater than a qualifier (e.g. 1.0-SNAPSHOT is less than 1.0)
            if ("".equals(thisQ)) {
                if ("".equals(thatQ)) {
                    return 0;
                }
                return 1;
            } else if ("".equals(thatQ)) {
                return -1;
            }

            return thisQ.compareTo(thatQ);
        }

        public List<Integer> getNumbers() {
            return Collections.unmodifiableList(numbers);
        }

        public String getQualifier() {
            return qualifier;
        }

        public String getOriginalForm() {
            return originalForm;
        }

        @Override
        public String toString() {
            return "MavenVersion{" +
                    originalForm +
                    '}';
        }
    }

    /**
     * struct-ish class to hold data about a VC breakage
     */
    protected static class VersionCompatibilityBreakage {
        private final MavenVersion oldMavenVersion;
        private final MavenVersion newMavenVersion;
        private final String oldWsdlUrl;
        private final String newWsdlUrl;
        private final String breakageMessage;

        public VersionCompatibilityBreakage(MavenVersion oldMavenVersion, MavenVersion newMavenVersion, String oldWsdlUrl, String newWsdlUrl, String breakageMessage) {
            if (oldMavenVersion == null) throw new IllegalArgumentException("oldMavenVersion must not be null");
            if (newMavenVersion == null) throw new IllegalArgumentException("newMavenVersion must not be null");
            if (StringUtils.isEmpty(oldWsdlUrl)) throw new IllegalArgumentException("oldWsdlUrl must not be empty/null");
            if (StringUtils.isEmpty(newWsdlUrl)) throw new IllegalArgumentException("newWsdlUrl must not be empty/null");
            if (StringUtils.isEmpty(breakageMessage)) throw new IllegalArgumentException("breakageMessage must not be empty/null");
            this.oldWsdlUrl = oldWsdlUrl;
            this.newWsdlUrl = newWsdlUrl;
            this.oldMavenVersion = oldMavenVersion;
            this.newMavenVersion = newMavenVersion;
            this.breakageMessage = breakageMessage;
        }

        @Override
        public String toString() {
            return "VersionCompatibilityBreakage{" +
                    "oldMavenVersion=" + oldMavenVersion +
                    ", newMavenVersion=" + newMavenVersion +
                    ", oldWsdlUrl='" + oldWsdlUrl + '\'' +
                    ", newWsdlUrl='" + newWsdlUrl + '\'' +
                    ", breakageMessage='" + breakageMessage + '\'' +
                    '}';
        }
    }

}
