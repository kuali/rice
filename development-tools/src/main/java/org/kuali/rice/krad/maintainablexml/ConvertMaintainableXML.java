/**
 * Copyright 2005-2012 The Kuali Foundation
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
package org.kuali.rice.krad.maintainablexml;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This is a command line utility class which upgrades the maintenance document xml stored in krns_maint_doc_t.doc_cntnt
 * to be able to still open and use any maintenance documents that were enroute at the time of an upgrade to Rice 2.0.
 *
 * <p>Instructions:</p>
 * <ol>
 *   <li>Backup database.</li>
 *   <li>Add the conversion rules to the rules xml file -
 *       ..\rice\development-tools\src\main\resources\org\kuali\rice\devtools\krad\maintainablexml\MaintainableXMLUpgradeRules.xml
 *       See comments in the xml file to setup the rules.</li>
 *   <li>Run this class.</li>
 *   <li>Enter the rice config file location that has the database connection properties. Only enter the location relative
 *       to user.dir/kuali/main.</li>
 *   <li>Select Run mode. Mode 1 will do the xml upgrade and update the krns_maint_doc_t table with the new xml. Cannot
 *       roll back after this has been done. Mode 2 will only print out the old xml and the new xml - this can be used
 *       to test the rules xml setup.</li>
 *   <li>Select the range of document numbers to upgrade. Enter % on from range prompt to upgrade all.</li>
 * </ol>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ConvertMaintainableXML {

    public static void main(String[] args) {
        //  prompt the user to enter settings file
        String settingsFile = readInput("Enter config file location relative to Kuali main user directory "
                + "(ie. dev/sample-app-config.xml OR dev/trnapp-config.xml) : ", null, false);

        String filePath = System.getProperty("user.home") + "/kuali/main/" + settingsFile;

        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("The settings file location is not valid : \n" + filePath);
            System.exit(1);
        }

        String runMode = readInput("Run mode :\n1. Update xml in DB\n2. Print old and new xml\nEnter 1 or 2\n",
                new String[]{"1", "2"}, false);

        String fromRange = readInput("Please enter document start range value ('%' or '' for all) :\n",
                new String[]{"%", ""}, true);

        String toRange = null;

        boolean hasRangeParameters = false;

        if (!"".equals(fromRange) && !"%".equals(fromRange)) {
            toRange = readInput("Please enter end range value :\n", null, true);
            System.out.println("Upgrading document numbers from " + fromRange + " to " + toRange + "\n");
            hasRangeParameters = true;
        }

        System.out.println("Looking up settings file... " + filePath + "\n");

        try {
            HashMap map = getSettings(filePath);
            System.out.println("Using the following settings : " + map + "\n");
            FileConverter fileConverter = new FileConverter();
            fileConverter.runFileConversion(map, runMode, fromRange, toRange, hasRangeParameters);
        } catch (Exception e) {
            System.out.println("Error executing conversion : " + e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * Displays message in command line and read user input. Checks that entered values are within valid input.
     *
     * @param message - the message string to print out
     * @param validOptions - the allowed user input
     * @return the string input from the user
     */
    private static String readInput(String message, String[] validOptions, boolean numeric) {
        System.out.print(message);
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        String inputString = null;

        try {
            inputString = br.readLine();
            if (numeric && (validOptions == null || !Arrays.asList(validOptions).contains(inputString))) {
                Integer.parseInt(inputString);
                return inputString;
            }
        } catch (IOException ioe) {
            System.out.println("IO error trying to read input!");
            System.exit(1);
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid Option! Must be numeric.");
            readInput(message, validOptions, numeric);
        }
        if (validOptions != null && !Arrays.asList(validOptions).contains(inputString)) {
            System.out.println("Invalid Option!");
            readInput(message, validOptions, numeric);
        }
        return inputString;
    }

    /**
     * Parses settings file and put the properties in a map.
     *
     * @param filePath - the location of the settings file
     * @return a HashMap populated with the settings
     * @throws Exception
     */
    public static HashMap getSettings(String filePath) throws Exception {
        File file = new File(filePath);
        HashMap params = new HashMap();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(file);
        doc.getDocumentElement().normalize();
        NodeList nodeLst = doc.getElementsByTagName("param");

        for (int s = 0; s < nodeLst.getLength(); s++) {
            Node fstNode = nodeLst.item(s);
            if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
                Element fstElmnt = (Element) fstNode;
                String paramName = fstElmnt.getAttribute("name");
                NodeList textFNList = fstElmnt.getChildNodes();
                String paramValue = ((Node) textFNList.item(0)).getNodeValue().trim();
                params.put(paramName, paramValue);
            }
        }
        return params;
    }

}
