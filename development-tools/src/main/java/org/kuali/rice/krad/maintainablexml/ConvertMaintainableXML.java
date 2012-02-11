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
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ConvertMaintainableXML {

    public static void main(String[] args) {
        //  prompt the user to enter settings file
        String settingsFile = readInput("Enter settings file location relative to Kuali main user directory "
                + "(ie. prd/sample-app-config.xml OR dev/trnapp-config.xml) : ", null);

        String filePath = System.getProperty("user.home") + "/kuali/main/" + settingsFile;
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("The settings file location is not valid : \n" + filePath);
            System.exit(1);
        }

        String runMode = readInput("Run mode :\n1. Update xml in DB\n2. Print old and new xml\nEnter 1 or 2\n", 
                new String[]{"1", "2"});


        System.out.println("Looking up settings file... " + filePath);

        try {
            HashMap map = getSettings(filePath);
            System.out.println("Using settings : " + map);

            FileConverter fileConverter = new FileConverter();
            fileConverter.runFileConversion(map);
        } catch (Exception e) {
            System.out.println("Error executing conversion : " + e.getMessage());
        }

    }

    /**
     *  Displays message in command line and read user input. Checks that entered values are within valid input.
     *
     * @param message - the message string to print out
     * @param validOptions - the allowed user input
     * @return the string input from the user
     */
    private static String readInput(String message, String[] validOptions) {
        System.out.print(message);
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        String inputString = null;

        try {
            inputString = br.readLine();
        } catch (IOException ioe) {
            System.out.println("IO error trying to read settings file location!");
            System.exit(1);
        }
        if (validOptions != null && !Arrays.asList(validOptions).contains(inputString)) {
            System.out.println("Invalid Option!");
            readInput(message, validOptions);
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
                String paramValue = ((Node)textFNList.item(0)).getNodeValue().trim();
                params.put(paramName, paramValue);
            }
        }
        return params;
    }    
    
    
}
