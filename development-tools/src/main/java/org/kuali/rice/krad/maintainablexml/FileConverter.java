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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kuali.rice.krad.maintainablexml;

import java.io.StringReader;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Used to upgrade the maintenance document xml stored in krns_maint_doc_t.doc_cntnt
 * to be able to still open and use any maintenance documents that were enroute at the time of an upgrade to Rice 2.0.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class FileConverter {
    
    private HashMap<String, String> classNameRuleMap;
    private HashMap<String, String> maintImplRuleMap;
    private HashMap<String, HashMap<String, String>> classPropertyRuleMap;
    private ArrayList<ArrayList<String>> newMaintDocXml = new ArrayList();

    /**
     * Selects all the encrypted xml documents from krns_maint_doc_t, decrypts them, runs the rules to upgrade them,
     * encrypt them and update krns_maint_doc_t with the upgraded xml.
     *
     * @param settingsMap - the settings
     * @throws Exception
     */
    public  void runFileConversion(HashMap settingsMap) throws Exception {

        final EncryptionService encryptService = new EncryptionService((String) settingsMap.get("encryption.key"));
        
        if (classNameRuleMap == null) {
            setRuleMaps();
        }
        
        JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSource(settingsMap));        
        jdbcTemplate.query("SELECT DOC_HDR_ID, DOC_CNTNT FROM krns_maint_doc_t ",// WHERE DOC_HDR_ID = '3158' WHERE DOC_HDR_ID > '2394'",
                new RowCallbackHandler() {

            public void processRow(ResultSet rs) throws SQLException {
                while(rs.next()) {
                    processDocumentRow(rs.getString(1), rs.getString(2), encryptService);
                }
            }
        });

        // TODO : uncomment only when ready to overwrite old xml
//        int[] updateCounts = jdbcTemplate.batchUpdate(
//                "update krns_maint_doc_t set DOC_CNTNT = ? where DOC_HDR_ID = ?",
//                new BatchPreparedStatementSetter() {
//                    public void setValues(PreparedStatement ps, int i) throws SQLException {                        
//                        ps.setString(1, newMaintDocXml.get(i).get(1));
//                        ps.setString(2, newMaintDocXml.get(i).get(0));
//                    }
//
//                    public int getBatchSize() {
//                        return newMaintDocXml.size();
//                    }
//                } );
//        
//        for (int updt : updateCounts) {
//           System.out.println("updt = " + updt); 
//        }
        
//        System.out.println("newMaintDocXml = " + newMaintDocXml);

    }

    /**
     * Creates the datasource from the settings map
     *
     * @param settingsMap - the settingMap containing the db connection settings
     * @return the DataSource object
     */
    public static DataSource getDataSource(HashMap settingsMap)  {
        String driver = "";
        if ("MySQL".equals(settingsMap.get("datasource.ojb.platform"))) {
           driver = "com.mysql.jdbc.Driver";  
        }else if("Oracle".equals(settingsMap.get("datasource.driver.name"))){
           driver = "com.mysql.jdbc.Driver";  
        }else{
           driver = (String)settingsMap.get("datasource.driver.name");
        }
        
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driver);
        dataSource.setUrl((String)settingsMap.get("datasource.url"));
        dataSource.setUsername((String)settingsMap.get("datasource.username"));
        dataSource.setPassword((String)settingsMap.get("datasource.password"));
        return dataSource;
    }

    /**
     * Called for each row in the processRow method of the spring query. Upgrades the xml and add the new xml
     * and the document number to the upgrade map.
     *
     * @param docId - the document id string
     * @param docCntnt - the old xml string
     * @param encryptServ - the encryption service used to encrypt/decrypt the xml
     */
    public  void processDocumentRow(String docId, String docCntnt, EncryptionService encryptServ) {
        System.out.println(docId);
        try {
            String oldXml = encryptServ.decrypt(docCntnt);
            System.out.println(oldXml);
            oldXml = transformToNewXML(oldXml);
            System.out.println("\n" + oldXml);
            ArrayList<String> rowData = new ArrayList();
            rowData.add(docId);
            rowData.add(encryptServ.encrypt(oldXml));
            newMaintDocXml.add(rowData);
        } catch (Exception ex) {
            Logger.getLogger(FileConverter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Upgrades the xml using the rule maps
     *
     * @param oldXML - the old xml that must be upgraded
     * @return the upgraded xml string
     * @throws Exception
     */
    public String transformToNewXML(String oldXML) throws Exception {      
       
        // Replace classnames
        for (String key : classNameRuleMap.keySet()) {
            oldXML = oldXML.replaceAll(key, classNameRuleMap.get(key));
        }
        
        
        // Replace and remove the property names of the classes
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputSource is = new InputSource( new StringReader( oldXML ) );
        Document doc = db.parse(is);
        doc.getDocumentElement().normalize();
        XPath xpath = XPathFactory.newInstance().newXPath();
        for (String key : classPropertyRuleMap.keySet()) {
            HashMap<String, String> properties = classPropertyRuleMap.get(key);
            for (String keyProperties : properties.keySet()) {
                XPathExpression exprMaintainableObject = xpath.compile("//" + key + "/" + keyProperties);
                NodeList propertyNodeList = (NodeList) exprMaintainableObject.evaluate(doc, XPathConstants.NODESET);
                for (int s = 0; s < propertyNodeList.getLength(); s++) {
                    if (properties.get(keyProperties).equals("")) {
                        propertyNodeList.item(s).getParentNode().removeChild(propertyNodeList.item(s));
                    }else{
                        doc.renameNode(propertyNodeList.item(s), null, properties.get(keyProperties));
                    }
                }
            }
        }
        
        
        // Replace MaintainableImplClass names
        for (String key : maintImplRuleMap.keySet()) {
            // Only do replace for files containing the maintainable object class
            if (oldXML.contains(key)) {
                String maintImpl = maintImplRuleMap.get(key);
                XPathExpression exprMaintainableTest = xpath.compile("//maintainableDocumentContents/newMaintainableObject/" + key );
                NodeList exprMaintainableTestList = (NodeList) exprMaintainableTest.evaluate(doc, XPathConstants.NODESET);
                if (exprMaintainableTestList.getLength() > 0) {
                    XPathExpression exprMaintainableObject = xpath.compile("//maintainableDocumentContents");
                    NodeList exprMaintainableNodeList = (NodeList) exprMaintainableObject.evaluate(doc, XPathConstants.NODESET);
                    if (exprMaintainableNodeList.getLength() > 0) {
                        ((Element)exprMaintainableNodeList.item(0)).setAttribute("maintainableImplClass", maintImpl);
                    }
                }
            }
        
        }
        
        // Dom to string
        
        doc.getDocumentElement().normalize();
        
        TransformerFactory transfac = TransformerFactory.newInstance();
        Transformer trans = transfac.newTransformer();
        trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        trans.setOutputProperty(OutputKeys.INDENT, "yes");
        
        StringWriter sw = new StringWriter();
        StreamResult result = new StreamResult(sw);
        DOMSource source = new DOMSource(doc);
        trans.transform(source, result);
        // Remove empty lines where properties has been removed
        oldXML = sw.toString().replaceAll("(?m)^\\s+\\n", "");;
        
        return oldXML;
    }

    /**
     *
     * @param oldXML
     * @throws Exception
     */
    private void upgradeBONotes(String oldXML) throws Exception {
    // Replace and remove the property names of the classes
//        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//        DocumentBuilder db = dbf.newDocumentBuilder();
//        InputSource is = new InputSource( new StringReader( oldXML ) );
//        Document doc = db.parse(is);
//        doc.getDocumentElement().normalize();
//        XPath xpath = XPathFactory.newInstance().newXPath();
//        XPathExpression exprMaintainableObject = xpath.compile("///");
//                NodeList propertyNodeList = (NodeList) exprMaintainableObject.evaluate(doc, XPathConstants.NODESET);
//                for (int s = 0; s < propertyNodeList.getLength(); s++) {
//                    if (properties.get(keyProperties).equals("")) {
//                        propertyNodeList.item(s).getParentNode().removeChild(propertyNodeList.item(s));
//                    }else{
//                        doc.renameNode(propertyNodeList.item(s), null, properties.get(keyProperties));
//                    }
//                } 
//        
//        
    }

    /**
     *  Reads the rule xml and sets up the rule maps that will be used to transform the xml
     */
    public void setRuleMaps()  {
        classNameRuleMap = new HashMap();
        classPropertyRuleMap = new HashMap();
        maintImplRuleMap = new HashMap();
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            
            Document doc = db.parse(getClass().getResourceAsStream("/org/kuali/rice/devtools/krad/maintainablexml/MaintDocRules.xml"));
            doc.getDocumentElement().normalize();
            XPath xpath = XPathFactory.newInstance().newXPath();
            
            // Get the moved classes rules
            
            XPathExpression exprClassNames = xpath.compile("//*[@name='maint_doc_moved_classes']/pattern");
            NodeList classNamesList = (NodeList) exprClassNames.evaluate(doc, XPathConstants.NODESET);
            for (int s = 0; s < classNamesList.getLength(); s++) {
                String matchText = xpath.evaluate("match/text()", classNamesList.item(s));
                String replaceText =  xpath.evaluate("replacement/text()", classNamesList.item(s));
                classNameRuleMap.put(matchText, replaceText);
            }
            
            // Get the property changed rules
            
            XPathExpression exprClassProperties = xpath.compile("//*[@name='maint_doc_changed_properties']/pattern");
            XPathExpression exprClassPropertiesPatterns = xpath.compile("pattern");
            NodeList propertyClassList = (NodeList) exprClassProperties.evaluate(doc, XPathConstants.NODESET);
            for (int s = 0; s < propertyClassList.getLength(); s++) {
                String classText = xpath.evaluate("class/text()", propertyClassList.item(s));
                HashMap propertyRuleMap = new HashMap();
                NodeList classPropertiesPatterns = (NodeList) exprClassPropertiesPatterns.evaluate(propertyClassList.item(s), XPathConstants.NODESET);
                for (int c = 0; c < classPropertiesPatterns.getLength(); c++) {
                    String matchText = xpath.evaluate("match/text()", classPropertiesPatterns.item(c));
                    String replaceText =  xpath.evaluate("replacement/text()", classPropertiesPatterns.item(c));
                    propertyRuleMap.put(matchText, replaceText);
                }
                classPropertyRuleMap.put(classText, propertyRuleMap);
            }
            
            // Get the maint impl class rule
            
            XPathExpression exprMaintImpl = xpath.compile("//*[@name='maint_doc_impl_classes']/pattern");
            NodeList maintImplList = (NodeList) exprMaintImpl.evaluate(doc, XPathConstants.NODESET);
            for (int s = 0; s < maintImplList.getLength(); s++) {
                String maintainableText = xpath.evaluate("maintainable/text()", maintImplList.item(s));
                String maintainableImplText =  xpath.evaluate("maintainableImpl/text()", maintImplList.item(s));
                maintImplRuleMap.put(maintainableText, maintainableImplText);
            }            
            
        }catch(Exception e){
            System.out.println("Error parsing rule xml file. Please check file. : " + e.getMessage());
            System.exit(1);
        }
    }
}
