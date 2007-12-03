/*
 * Copyright 2005-2007 The Kuali Foundation.
 *
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
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
package org.kuali.workflow.tools.xml;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * A utility class to generate Workgroup and Rule XML from an Excel spreadsheet.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class XmlGen {

	// arguments
	private String outputDirectoryPath;
	private String inputDirectoryPath;
	private XmlGenHelper xmlGenHelper;

	private XPath xpath;
	private Map<String, Attribute> attributes = new HashMap<String, Attribute>();
	private Map<String, Template> templates = new HashMap<String, Template>();
	private Map<String, DocumentType> documentTypes = new HashMap<String, DocumentType>();
	private SpreadsheetOutput output = new SpreadsheetOutput();

	private static final String IGNORE_PREVIOUS = "IGNORE_PREVIOUS";
	private static final String ACTION_REQUESTED = "ACTION_REQUESTED";
	private static final String DESCRIPTION = "DESCRIPTION";
	private static final String COMMENT = "COMMENT";

	public static void main(String[] args) throws Exception {
		String helperClassName = null;
		String outputDir = null;
		String inputDir = null;
		for (int index = 0; index < args.length; index++) {
			String arg = args[index];
			if (arg.equals("-helper")) {
				if (index+1 >= args.length) {
					fail("Failed to parse arguments.");
				}
				helperClassName = args[++index];
			} else if (arg.equals("-o")) {
				if (index+1 >= args.length) {
					fail("Failed to parse arguments.");
				}
				outputDir = args[++index];
			} else if (arg.equals("-h")) {
				usage();
				System.exit(0);
			} else if (index == (args.length-1)) {
				inputDir = args[index];
			}
		}
		if (isBlank(inputDir)) {
			fail("No input directory was specified.");
		}
		XmlGenHelper helper = null;
		if (!isBlank(helperClassName)) {
			try {
				Class helperClass = Class.forName(helperClassName);
				Object newHelperInstance = helperClass.newInstance();
				if (!(newHelperInstance instanceof XmlGenHelper)) {
					fail("Given helper class '" + helperClassName + "' is not a valid instance of '" + XmlGenHelper.class.getName() + "'");
				} else {
					helper = (XmlGenHelper)newHelperInstance;
				}
			} catch (ClassNotFoundException e) {
				fail("Given helper class '" + helperClassName + "' could not be found.  Please ensure that it is available on the classpath.");
			}
		}
		XmlGen xmlGen = new XmlGen();
		xmlGen.setInputDirectoryPath(inputDir);
		xmlGen.setOutputDirectoryPath(outputDir);
		xmlGen.setXmlGenHelper(helper);
		xmlGen.run();
	}

	private static void fail(String errorMessage) {
		System.err.println("\n>>>>> " + errorMessage);
		usage();
		System.exit(-1);
	}

	public Map<String, Attribute> getAttributes() {
		return attributes;
	}

	public Map<String, DocumentType> getDocumentTypes() {
		return documentTypes;
	}

	public Map<String, Template> getTemplates() {
		return templates;
	}

	public String getInputDirectoryPath() {
		return inputDirectoryPath;
	}

	public void setInputDirectoryPath(String inputDirectory) {
		this.inputDirectoryPath = inputDirectory;
	}

	public String getOutputDirectoryPath() {
		return outputDirectoryPath;
	}

	public void setOutputDirectoryPath(String outputDirectory) {
		this.outputDirectoryPath = outputDirectory;
	}

	public XmlGenHelper getXmlGenHelper() {
		return xmlGenHelper;
	}

	public void setXmlGenHelper(XmlGenHelper xmlGenHelper) {
		this.xmlGenHelper = xmlGenHelper;
	}

	public SpreadsheetOutput getOutput() {
		return output;
	}

	public XmlGen() throws Exception {
		XPathFactory factory = XPathFactory.newInstance();
		this.xpath = factory.newXPath();
	}

	public void run() throws Exception {
		if (isBlank(inputDirectoryPath)) {
			throw new RuntimeException("No input directory was specified.");
		}
		if (isBlank(outputDirectoryPath)) {
			outputDirectoryPath = inputDirectoryPath+"/xmlgen-output";
		}
		File inputDirectory = new File(inputDirectoryPath);
		if (!inputDirectory.exists()) {
			throw new FileNotFoundException("Could not locate the given input directory '" + inputDirectoryPath + "'");
		} else if (!inputDirectory.isDirectory()) {
			throw new RuntimeException("Given input directory is not a directory '" + inputDirectoryPath + "'");
		} else if (!inputDirectory.canRead()) {
			throw new RuntimeException("Not permitted to read from '" + inputDirectoryPath + "'.  Please check permissions.");
		}
		indexInput(inputDirectory);

		File outputDirectory = new File(outputDirectoryPath);
		if (!outputDirectory.exists()) {
			if (!outputDirectory.mkdirs()) {
				throw new RuntimeException("Failed to create the output directory '" + outputDirectoryPath + "'");
			}
		}
		if (!outputDirectory.isDirectory()) {
			throw new RuntimeException("Given output directory is not a directory '" + outputDirectoryPath + "'");
		} else if (!outputDirectory.canWrite()) {
			throw new RuntimeException("Not permitted to write to '" + outputDirectoryPath + "'.  Please check permissions.");
		}
		generateRules(inputDirectory, outputDirectory);
	}

	public static void usage() {
		String usage = "\nusage: java [-java options] org.kuali.workflow.tools.xml.XmlGen [-options] input-directory\n"+
			"\n"+
			"Generate KEW XML from the files located in the given input directory.  All files used for input must end with a .xml extension.\n"+
			"There will be 2 resulting files in the output directory:\n" +
			"     Rules.xml      - contains the rules that were generated from the spreadsheet\n"+
			"     Workgroups.xml - contains the workgroups that were used in the rules on the spreadsheet\n"+
			"\n"+
			"Where java options include options passed to the java runtime (most importantly -classpath or -cp)\n"+
			"\n"+
			"The options for XmlGen include:\n"+
			"     -h                  display this help message\n"+
			"     -helper             the fully qualified classname for the org.kuali.workflow.tools.xml.XmlGenHelper to use to generate rule descriptions and field names\n"+
			"     -o                  the directory to place the output files into.  By default, they are created in a subdirectory of the given directory named 'xmlgen-output'";
		System.err.println(usage);
	}

	protected void indexInput(File inputDirectory) throws Exception {
		File[] xmlFiles = inputDirectory.listFiles(new XmlFilenameFilter());
		List<File> sortedFiles = new ArrayList<File>();
		sortedFiles.addAll(Arrays.asList(xmlFiles));
		Collections.sort(sortedFiles, new FilenameComparator());
		for (File xmlFile : xmlFiles) {
			InputStream is = new BufferedInputStream(new FileInputStream(xmlFile));
			try {
				indexWorkflowXmlFile(is);
			} finally {
				is.close();
			}
		}
	}

	protected void indexWorkflowXmlFile(InputStream inputStream) throws Exception {
		Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(inputStream));
		if (!(Boolean)xpath.evaluate("/data", document, XPathConstants.BOOLEAN)) {
			return;
		}
		NodeList attributeNodes = (NodeList)xpath.evaluate("/data/ruleAttributes/ruleAttribute", document, XPathConstants.NODESET);
		if (attributeNodes != null) {
			for (int index = 0; index < attributeNodes.getLength(); index++) {
				Element attributeElem = (Element)attributeNodes.item(index);
				indexAttribute(attributeElem);
			}
		}
		NodeList templateNodes = (NodeList)xpath.evaluate("/data/ruleTemplates/ruleTemplate", document, XPathConstants.NODESET);
		if (templateNodes != null) {
			for (int index = 0; index < templateNodes.getLength(); index++) {
				Element templateElem = (Element)templateNodes.item(index);
				indexTemplate(templateElem);
			}
		}
		NodeList docTypeNodes = (NodeList)xpath.evaluate("/data/documentTypes/documentType", document, XPathConstants.NODESET);
		if (docTypeNodes != null) {
			for (int index = 0; index < docTypeNodes.getLength(); index++) {
				Element docTypeElem = (Element)docTypeNodes.item(index);
				indexDocumentType(docTypeElem);
			}
		}
	}

	protected void indexAttribute(Element attributeElem) throws Exception {
		Attribute attribute = new Attribute(xmlGenHelper);
		String name = getChildNodeTextValue(attributeElem, "name", true);
		attribute.setName(name);
		String className = getChildNodeTextValue(attributeElem, "className", true);
		attribute.setClassName(className);
		NodeList routingConfig = (NodeList)xpath.evaluate("./routingConfig", attributeElem, XPathConstants.NODESET);
		if (!isEmpty(routingConfig)) {
			if (routingConfig.getLength() > 1) {
				throw new RuntimeException("More than one routing config was found on the given xml attribute '" + name + "'");
			}
			attribute.setXmlConfigData(getXmlContent(routingConfig.item(0)));
		}
		attributes.put(name, attribute);
	}

	protected void indexTemplate(Element templateElem) throws Exception {
		Template template = new Template();
		String name = getChildNodeTextValue(templateElem, "name", true);
		template.setName(name);
		NodeList attributeNodes = (NodeList)xpath.evaluate("./attributes/attribute", templateElem, XPathConstants.NODESET);
		if (attributeNodes != null) {
			for (int index = 0; index < attributeNodes.getLength(); index++) {
				Element attributeElem = (Element)attributeNodes.item(index);
				String attributeName =  getChildNodeTextValue(attributeElem, "name", true);
				Attribute attribute = attributes.get(attributeName);
				if (attribute == null) {
					throw new RuntimeException("Could not locate rule attribute with name '" + attributeName + "' referenced from rule template '" + name + "'");
				}
				template.getAttributes().add(attribute);
			}
		}
		templates.put(name, template);
	}

	protected void indexDocumentType(Element docTypeElem) throws Exception {
		DocumentType documentType = new DocumentType();
		documentType.setName(getChildNodeTextValue(docTypeElem, "name", true));
		NodeList routeNodes = (NodeList)xpath.evaluate("./routeNodes/*", docTypeElem, XPathConstants.NODESET);
		if (isEmpty(routeNodes)) {
			String parentName = getChildNodeTextValue(docTypeElem, "parent", false);
			if (!isBlank(parentName)) {
				DocumentType parentDocumentType = documentTypes.get(parentName);
				if (parentDocumentType == null) {
					throw new RuntimeException("Could not locate parent document type with the name '" + parentName + "' for child document type '" + documentType.getName() + "'");
				}
				documentType.setParent(parentDocumentType);
			}
		} else {
			for (int index = 0; index < routeNodes.getLength(); index++) {
				Element routeNodeNode = (Element)routeNodes.item(index);
				String nodeName = getAttributeValue(routeNodeNode, "name", true);
				RouteNode routeNode = new RouteNode();
				routeNode.setName(nodeName);
				String ruleTemplateName = getChildNodeTextValue(routeNodeNode, "ruleTemplate", false);
				if (!isBlank(ruleTemplateName)) {
					Template template = templates.get(ruleTemplateName);
					if (template == null) {
						throw new RuntimeException("Could not locate rule template with name '" + ruleTemplateName + "' for node '" + nodeName +"'");
					}
					routeNode.setTemplate(template);
				}
				documentType.getNodes().add(routeNode);
			}
		}
		documentTypes.put(documentType.getName(), documentType);
	}

	protected void generateRules(File inputDirectory, File outputDirectory) throws Exception {
		File[] xmlFiles = inputDirectory.listFiles(new XmlFilenameFilter());
		List<File> sortedFiles = new ArrayList<File>();
		sortedFiles.addAll(Arrays.asList(xmlFiles));
		Collections.sort(sortedFiles, new FilenameComparator());
		for (File xmlFile : xmlFiles) {
			InputStream is = new BufferedInputStream(new FileInputStream(xmlFile));
			try {
				processSpreadsheetXmlFile(is);
			} finally {
				is.close();
			}
		}
		writeXmlOutput(output, outputDirectory);
	}

	protected void processSpreadsheetXmlFile(InputStream is) throws Exception {
		Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(is));
		if (!(Boolean)xpath.evaluate("/Workbook", document, XPathConstants.BOOLEAN)) {
			return;
		}
		NodeList worksheets = (NodeList)xpath.evaluate("/Workbook/Worksheet", document, XPathConstants.NODESET);
		for (int index = 0; index < worksheets.getLength(); index++) {
			processWorksheet((Element)worksheets.item(index));
		}
	}

	protected void processWorksheet(Element worksheetElem) throws Exception {
		DocumentType documentType = null;
		RouteNode routeNode = null;
		List<String> columnHeaders = null;
		int columnHeadersRowIndex = -1;
		List<List<String>> dataRows = new ArrayList<List<String>>();
		List<String> currentDataRow = null;
		NodeList rows = (NodeList)xpath.evaluate("./Table/Row", worksheetElem, XPathConstants.NODESET);
		outerRow:for (int rowIndex = 0; rowIndex < rows.getLength(); rowIndex++) {
			Element rowElem = (Element)rows.item(rowIndex);
			NodeList childNodes = rowElem.getChildNodes();
			List<Element> cellNodes = new ArrayList<Element>();
			for (int childIndex = 0; childIndex < childNodes.getLength(); childIndex++) {
				Node childNode = childNodes.item(childIndex);
				if (Node.ELEMENT_NODE == childNode.getNodeType() && childNode.getNodeName().equals("Cell")) {
					cellNodes.add((Element)childNode);
				}
			}
			int currentSSIndex = 0;
			for (int columnIndex = 0; columnIndex < cellNodes.size(); columnIndex++) {
				Element column = cellNodes.get(columnIndex);
				String ssIndex = column.getAttribute("ss:Index");
				String columnValue = getChildNodeTextValue(column, "Data", false);
				if (columnValue != null) {
					columnValue = columnValue.trim();
				}
				if (documentType == null) {
					if (isBlank(columnValue)) {
						continue outerRow;
					}
					documentType = documentTypes.get(columnValue);
					if (documentType == null) {
						System.out.println("Not processing sheet.  No document type with name '" + columnValue + "'");
						return;
					}
					continue outerRow;
				} else if (routeNode == null) {
					if (isBlank(columnValue)) {
						continue outerRow;
					}
					routeNode = documentType.getNodeByName(columnValue);
					if (routeNode == null) {
						throw new RuntimeException("Failed to identify node with name '" + columnValue + "' on Document Type named '" + documentType.getName() + "'");
					}
					continue outerRow;
				} else if (columnHeaders == null) {
					if (isBlank(columnValue)) {
						continue outerRow;
					}
					columnHeaders = new ArrayList<String>();
					columnHeadersRowIndex = rowIndex;
				}
				if (columnHeadersRowIndex == rowIndex) {
					if (columnValue == null) {
						columnValue = "";
					}
					columnHeaders.add(columnValue);
				} else {
					if (columnIndex == 0) {
						currentDataRow = new ArrayList<String>();
					}
					if (!isBlank(ssIndex)) {
						int ssIndexInt = Integer.parseInt(ssIndex);
						while (currentSSIndex < (ssIndexInt-1)) {
							currentDataRow.add(null);
							currentSSIndex++;
						}
					}
					currentDataRow.add(columnValue);
				}
				currentSSIndex++;
			}
			System.out.println("processing row: " + rowIndex);
			if (currentDataRow != null) {
				dataRows.add(currentDataRow);
			}
			currentDataRow = null;
		}
		if (!dataRows.isEmpty()) {
			removeEmptyRows(dataRows);
			int numberOfExtensionValues = getNumberOfExtensionValues(routeNode.getTemplate());
		for (List<String> dataRow : dataRows) {
			Rule rule = new Rule();
			rule.setDocumentType(documentType);
			rule.setTemplate(routeNode.getTemplate());
			String actionRequestedCode = "A";
			String workgroupName = null;
			List<String> users = new ArrayList<String>();
			int attributeIndex = 0;
			int nonAttributeColumns = -1;
			for (int index = 0; index < columnHeaders.size(); index++) {
				String columnHeader = columnHeaders.get(index);
				String columnValue = null;
				if (index < dataRow.size()) {
					columnValue = dataRow.get(index);
				}
				if (columnHeader.equalsIgnoreCase(IGNORE_PREVIOUS)) {
					if ("Y".equalsIgnoreCase(columnValue) || "T".equalsIgnoreCase(columnValue) || Boolean.valueOf(columnValue)) {
						rule.setIgnorePrevious(true);
					} else {
						rule.setIgnorePrevious(false);
					}
				} else if (columnHeader.equalsIgnoreCase(ACTION_REQUESTED)) {
					actionRequestedCode = processActionRequestedCode(columnValue);
				} else if (columnHeader.equalsIgnoreCase(DESCRIPTION)) {
					rule.setDescription(columnValue);
				} else if (columnHeader.equalsIgnoreCase(COMMENT)) {
					continue;
				} else if (attributeIndex >= numberOfExtensionValues) {
					if (nonAttributeColumns == -1) {
						nonAttributeColumns = index;
						// first column after attributes is the workgroup
						workgroupName = columnValue;
					} else {
						if (columnValue != null) {
							users.add(columnValue);
						}
					}
				} else {
					addExtension(attributeIndex++, rule, columnValue);
				}
			}
			if (isBlank(actionRequestedCode)) {
				actionRequestedCode = "A";
			}
			addResponsibility(rule, workgroupName, users, actionRequestedCode);
			if (isBlank(rule.getDescription())) {
				rule.setDescription(generateRuleDescription(rule));
			}
			output.getRules().add(rule);
		}
		}
	}

	protected int getNumberOfExtensionValues(Template template) {
		int num = 0;
		for (Attribute attribute :  template.getAttributes()) {
			num += attribute.getFieldNames().size();
		}
		return num;
	}

	protected void removeEmptyRows(List<List<String>> dataRows) {
		int totalSize = dataRows.size();
		int emptyRows = 0;
		outer:for (Iterator<List<String>> iterator = dataRows.iterator(); iterator.hasNext();) {
			List<String> row = iterator.next();
			for (String data : row) {
				if (!isBlank(data)) {
					continue outer;
				}
			}
			iterator.remove();
			emptyRows++;
		}
		System.out.println("Removed " + emptyRows + " empty rows out of a total of " + totalSize + " rows.");
	}

	protected String processActionRequestedCode(String columnValue) {
		if (isBlank(columnValue)) {
			return null;
		} else if ("C".equalsIgnoreCase(columnValue) || "complete".equalsIgnoreCase(columnValue)) {
			return "C";
		} else if ("A".equalsIgnoreCase(columnValue) || "approve".equalsIgnoreCase(columnValue)) {
			return "A";
		} else if ("K".equalsIgnoreCase(columnValue) || "acknowledge".equalsIgnoreCase(columnValue) || "ack".equalsIgnoreCase(columnValue)) {
			return "K";
		} else if ("F".equalsIgnoreCase(columnValue) || "fyi".equalsIgnoreCase(columnValue)) {
			return "F";
		} else {
			throw new RuntimeException("Could not determine the action requested code for the given value '" + columnValue + "'");
		}
	}

	protected void addExtension(int attributeIndex, Rule rule, String value) {
		int tempIndex = 0;
		for (Attribute attribute :  rule.getTemplate().getAttributes()) {
			for (String fieldName : attribute.getFieldNames()) {
				if (tempIndex++ == attributeIndex) {
					RuleExtension ruleExtension = rule.getRuleExtensionForAttribute(attribute.getName());
					if (ruleExtension == null) {
						ruleExtension = new RuleExtension();
						ruleExtension.setAttribute(attribute);
						ruleExtension.setTemplate(rule.getTemplate());
						rule.getRuleExtensions().add(ruleExtension);
					}
					RuleExtensionValue ruleExtensionValue = new RuleExtensionValue();
					ruleExtensionValue.setKey(fieldName);
					ruleExtensionValue.setValue(value);
					ruleExtension.getExtensionValues().add(ruleExtensionValue);
				}
			}
		}
	}

	protected void addResponsibility(Rule rule, String workgroupName, List<String> users, String actionRequestedCode) {

		if (workgroupName != null) {
			Responsibility responsibility = new Responsibility();
			responsibility.setActionRequested(actionRequestedCode);
			Workgroup workgroup = output.getWorkgroups().get(workgroupName);
			if (workgroup != null && workgroup.getMembers().isEmpty() && !users.isEmpty()) {
				workgroup.setMembers(users);
			} else if (workgroup == null) {
				workgroup = new Workgroup();
				workgroup.setName(workgroupName);
				workgroup.setMembers(users);
				output.getWorkgroups().put(workgroupName, workgroup);
			}
			responsibility.setWorkgroup(workgroup);
			rule.getResponsibilities().add(responsibility);
		} else if (!users.isEmpty()) {
			for (String user : users) {
				Responsibility responsibility = new Responsibility();
				responsibility.setActionRequested(actionRequestedCode);
				responsibility.setUser(user);
				rule.getResponsibilities().add(responsibility);
			}
		} else {
			throw new RuntimeException("No workgroup or users defined on the Rule.  " + rule.toString());
		}

	}

	protected String generateRuleDescription(Rule rule) {
		if (xmlGenHelper != null) {
			String description = xmlGenHelper.generateRuleDescription(rule);
			if (!isBlank(description)) {
				return description;
			}
		}
		// generate the default description
		String defaultDescription = rule.getDocumentType().getName();
		for (RuleExtension extension : rule.getRuleExtensions()) {
			for (RuleExtensionValue value : extension.getExtensionValues()) {
				defaultDescription += ", " + value.getKey()+": " + value.getValue();
			}
		}
		if (rule.getResponsibilities().size() > 0 && rule.getResponsibilities().get(0).getWorkgroup() != null) {
			defaultDescription += " for " + rule.getResponsibilities().get(0).getWorkgroup().getName();
		}
		return defaultDescription;
	}

	protected void writeXmlOutput(SpreadsheetOutput output, File outputDirectory) throws Exception {
		writeWorkgroupXmlOutput(output, outputDirectory);
		writeRuleXmlOutput(output, outputDirectory);
	}

	protected void writeWorkgroupXmlOutput(SpreadsheetOutput output, File outputDirectory) throws Exception {
		if (!output.getWorkgroups().isEmpty()) {
			validateWorkgroups(output);
			File workgroupFile = new File(outputDirectory, "Workgroups.xml");
			workgroupFile.createNewFile();
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Element dataElement = createDataElement(document);
			document.appendChild(dataElement);
			Element workgroupsElement = document.createElement("workgroups");
			workgroupsElement.setAttribute("xmlns", "ns:workflow/Workgroup");
			workgroupsElement.setAttribute("xsi:schemaLocation", "ns:workflow/Workgroup resource:Workgroup");
			dataElement.appendChild(workgroupsElement);
			for (Workgroup workgroup : output.getWorkgroups().values()) {
				Element workgroupElement = document.createElement("workgroup");
				workgroupElement.setAttribute("allowOverwrite", "true");
				workgroupsElement.appendChild(workgroupElement);
				Element workgroupNameElement = document.createElement("workgroupName");
				workgroupElement.appendChild(workgroupNameElement);
				workgroupNameElement.appendChild(document.createTextNode(workgroup.getName()));
				Element membersElement = document.createElement("members");
				workgroupElement.appendChild(membersElement);
				for (String member : workgroup.getMembers()) {
					Element memberElement = document.createElement("authenticationId");
					membersElement.appendChild(memberElement);
					memberElement.appendChild(document.createTextNode(member));
				}
			}
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(new DOMSource(document), new StreamResult(new FileOutputStream(workgroupFile)));
		}
	}

	protected void validateWorkgroups(SpreadsheetOutput output) {
		for (Workgroup workgroup : output.getWorkgroups().values()) {
			if (isBlank(workgroup.getName())) {
				throw new RuntimeException("Found a workgroup in the output without a name!");
			}
			if (workgroup.getMembers() == null || workgroup.getMembers().isEmpty()) {
				throw new RuntimeException("No members declared for workgroup '" + workgroup.getName() + "'");
			}
			Set<String> foundMembers = new HashSet<String>();
			for (Iterator iterator = workgroup.getMembers().iterator(); iterator.hasNext();) {
				String member = (String) iterator.next();
				if (foundMembers.contains(member)) {
					iterator.remove();
				} else {
					foundMembers.add(member);
				}
			}
		}
	}

	protected void writeRuleXmlOutput(SpreadsheetOutput output, File outputDirectory) throws Exception {
		if (!output.getRules().isEmpty()) {
			File ruleFile = new File(outputDirectory, "Rules.xml");
			ruleFile.createNewFile();
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Element dataElement = createDataElement(document);
			document.appendChild(dataElement);
			Element rulesElement = document.createElement("rules");
			rulesElement.setAttribute("xmlns", "ns:workflow/Rule");
			rulesElement.setAttribute("xsi:schemaLocation", "ns:workflow/Rule resource:Rule");
			dataElement.appendChild(rulesElement);
			for (Rule rule : output.getRules()) {
				Element ruleElement = document.createElement("rule");
				rulesElement.appendChild(ruleElement);
				Element documentTypeElem = document.createElement("documentType");
				Element ruleTemplateElem = document.createElement("ruleTemplate");
				Element descriptionElem = document.createElement("description");
				Element ignorePreviousElem = document.createElement("ignorePrevious");
				Element ruleExtensionsElem = document.createElement("ruleExtensions");
				Element responsibilitiesElem = document.createElement("responsibilities");

				ruleElement.appendChild(documentTypeElem);
				ruleElement.appendChild(ruleTemplateElem);
				ruleElement.appendChild(descriptionElem);
				ruleElement.appendChild(ignorePreviousElem);
				if (rule.getRuleExtensions() != null && !rule.getRuleExtensions().isEmpty()) {
					ruleElement.appendChild(ruleExtensionsElem);
				}
				ruleElement.appendChild(responsibilitiesElem);

				documentTypeElem.appendChild(document.createTextNode(rule.getDocumentType().getName()));
				ruleTemplateElem.appendChild(document.createTextNode(rule.getTemplate().getName()));
				descriptionElem.appendChild(document.createTextNode(rule.getDescription()));
				ignorePreviousElem.appendChild(document.createTextNode(rule.getIgnorePrevious().toString()));

				if (rule.getRuleExtensions() != null) {
					for (RuleExtension extension : rule.getRuleExtensions()) {
						if (!ruleExtensionHasValues(extension)) {
							continue;
						}
						Element ruleExtensionElem = document.createElement("ruleExtension");
						ruleExtensionsElem.appendChild(ruleExtensionElem);

						Element attributeElem = document.createElement("attribute");
						ruleExtensionElem.appendChild(attributeElem);
						attributeElem.appendChild(document.createTextNode(extension.getAttribute().getName()));

						Element ruleExtensionTemplateElem = document.createElement("ruleTemplate");
						ruleExtensionElem.appendChild(ruleExtensionTemplateElem);
						ruleExtensionTemplateElem.appendChild(document.createTextNode(extension.getTemplate().getName()));

						Element ruleExtensionValuesElem = document.createElement("ruleExtensionValues");
						ruleExtensionElem.appendChild(ruleExtensionValuesElem);

						for (RuleExtensionValue extensionValue : extension.getExtensionValues()) {
							if (isBlank(extensionValue.getValue())) {
								continue;
							}
							Element ruleExtensionValueElem = document.createElement("ruleExtensionValue");
							ruleExtensionValuesElem.appendChild(ruleExtensionValueElem);

							Element keyElem = document.createElement("key");
							keyElem.appendChild(document.createTextNode(extensionValue.getKey()));
							ruleExtensionValueElem.appendChild(keyElem);

							Element valueElem = document.createElement("value");
							valueElem.appendChild(document.createTextNode(extensionValue.getValue()));
							ruleExtensionValueElem.appendChild(valueElem);
						}
					}
				}

				for (Responsibility responsibility : rule.getResponsibilities()) {
					Element responsibilityElem = document.createElement("responsibility");
					responsibilitiesElem.appendChild(responsibilityElem);

					if (responsibility.getWorkgroup() != null) {
						Element workgroupElem = document.createElement("workgroup");
						workgroupElem.appendChild(document.createTextNode(responsibility.getWorkgroup().getName()));
						responsibilityElem.appendChild(workgroupElem);
					} else {
						Element userElem = document.createElement("user");
						userElem.appendChild(document.createTextNode(responsibility.getUser()));
						responsibilityElem.appendChild(userElem);
					}

					Element actionRequestedElem = document.createElement("actionRequested");
					actionRequestedElem.appendChild(document.createTextNode(responsibility.getActionRequested()));
					responsibilityElem.appendChild(actionRequestedElem);

					Element priorityElem = document.createElement("priority");
					priorityElem.appendChild(document.createTextNode("1"));
					responsibilityElem.appendChild(priorityElem);
				}
			}
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(new DOMSource(document), new StreamResult(new FileOutputStream(ruleFile)));
		}

	}

	protected boolean ruleExtensionHasValues(RuleExtension ruleExtension) {
		for (RuleExtensionValue extensionValue : ruleExtension.getExtensionValues()) {
			if (!isBlank(extensionValue.getValue())) {
				return true;
			}
		}
		return false;
	}

	protected Element createDataElement(Document document) {
		Element dataElement = document.createElement("data");
		dataElement.setAttribute("xmlns", "ns:workflow");
		dataElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		dataElement.setAttribute("xsi:schemaLocation", "ns:workflow resource:WorkflowData");
		return dataElement;
	}

	protected String getAttributeValue(Element parentElement, String attributeName, boolean required) {
		String attributeValue = parentElement.getAttribute(attributeName);
		if (isBlank(attributeValue)) {
			if (required) {
				throw new RuntimeException("Could not locate value for attribute '" + attributeName + "' on element '" + parentElement.getTagName() + "'");
			}
			return null;
		}
		return attributeValue;
	}

	protected String getChildNodeTextValue(Element parentElement, String childElementName, boolean required) {
		for (int index = 0; index < parentElement.getChildNodes().getLength(); index++) {
			if (parentElement.getChildNodes().item(index).getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element)parentElement.getChildNodes().item(index);
				if (childElement.getTagName().equals(childElementName)) {
					String textValue = getTextContent(childElement);
					if (isBlank(textValue)) {
						if (required) {
							throw new RuntimeException("Could not locate value for element '" + childElementName + "' in parent element '" + parentElement.getTagName() + "'");
						}
						return null;
					}
					return textValue;
				}
			}
		}
		if (required) {
			throw new RuntimeException("Could not locate a child element named '" + childElementName + "' in parent element '" + parentElement.getTagName() + "'");
		}
		return null;
	}

	protected String getTextContent(Element element) {
		NodeList children = element.getChildNodes();
		Node node = children.item(0);
		return node.getNodeValue();
	}

	protected String getXmlContent(Node node) throws Exception {
		Source source = new DOMSource(node);
		StringWriter writer = new StringWriter();
		Result result = new StreamResult(writer);
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.transform(source, result);
		return writer.toString();
	}

	public static boolean isBlank(String string) {
		return string == null || string.trim().equals("");
	}

	public static boolean isEmpty(NodeList nodes) {
		return nodes == null || nodes.getLength() == 0;
	}

	private class XmlFilenameFilter implements FilenameFilter {

		public boolean accept(File file, String fileName) {
			return fileName.endsWith(".xml");
		}

	}

	private class FilenameComparator implements Comparator<File> {

		public int compare(File file1, File file2) {
			return file1.getName().compareTo(file2.getName());
		}

	}

}
