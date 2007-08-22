package edu.iu.uis.eden.doctype;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.routetemplate.xmlrouting.XPathHelper;
import edu.iu.uis.eden.util.Utilities;
import edu.iu.uis.eden.web.KeyValue;

public class DocumentTypeSecurity {
  private Boolean active;

  private Boolean initiatorOk;
  private Boolean routeLogAuthenticatedOk;
  private List<KeyValue> searchableAttributes = new ArrayList<KeyValue>();
  private List<String> workgroups = new ArrayList<String>();
  private List<String> allowedRoles = new ArrayList<String>();
  private List<String> disallowedRoles = new ArrayList<String>();

  private static XPath xpath = XPathHelper.newXPath();

  public DocumentTypeSecurity() {}

  /** parse <security> XML to populate security object
   * @throws ParserConfigurationException
   * @throws IOException
   * @throws SAXException */
  public DocumentTypeSecurity(String documentTypeSecurityXml)
  {
    try {
      if (Utilities.isEmpty(documentTypeSecurityXml)) {
        return;
      }

      InputSource inputSource = new InputSource(new BufferedReader(new StringReader(documentTypeSecurityXml)));
      Element securityElement = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputSource).getDocumentElement();

      String active = (String) xpath.evaluate("./@active", securityElement, XPathConstants.STRING);
      if (Utilities.isEmpty(active) || "true".equals(active.toLowerCase())) {
        // true is the default
        this.setActive(Boolean.valueOf(true));
      }
      else {
        this.setActive(Boolean.valueOf(false));
      }

      // there should only be one <initiator> tag
      NodeList initiatorNodes = (NodeList) xpath.evaluate("./initiator", securityElement, XPathConstants.NODESET);
      if (initiatorNodes != null && initiatorNodes.getLength()>0) {
        Node initiatorNode = initiatorNodes.item(0);
        String value = initiatorNode.getTextContent();
        if (Utilities.isEmpty(value) || value.toLowerCase().equals("true")) {
          this.setInitiatorOk(Boolean.valueOf(true));
        }
        else {
          this.initiatorOk = Boolean.valueOf(false);
        }
      }

      // there should only be one <routeLogAuthenticated> tag
      NodeList routeLogAuthNodes = (NodeList) xpath.evaluate("./routeLogAuthenticated", securityElement, XPathConstants.NODESET);
      if (routeLogAuthNodes != null && routeLogAuthNodes.getLength()>0) {
        Node routeLogAuthNode = routeLogAuthNodes.item(0);
        String value = routeLogAuthNode.getTextContent();
        if (Utilities.isEmpty(value) || value.toLowerCase().equals("true")) {
          this.routeLogAuthenticatedOk = Boolean.valueOf(true);
        }
        else {
          this.routeLogAuthenticatedOk = Boolean.valueOf(false);
        }
      }

      NodeList searchableAttributeNodes = (NodeList) xpath.evaluate("./searchableAttribute", securityElement, XPathConstants.NODESET);
      if (searchableAttributeNodes != null && searchableAttributeNodes.getLength()>0) {
        for (int i = 0; i < searchableAttributeNodes.getLength(); i++) {
          Node searchableAttributeNode = searchableAttributeNodes.item(i);
          String name = (String) xpath.evaluate("./@name", searchableAttributeNode, XPathConstants.STRING);
          String idType = (String) xpath.evaluate("./@idType", searchableAttributeNode, XPathConstants.STRING);
          if (!Utilities.isEmpty(name) && !Utilities.isEmpty(idType)) {
            KeyValue searchableAttribute = new KeyValue(name, idType);
            searchableAttributes.add(searchableAttribute);
          }
        }
      }

      NodeList workgroupNodes = (NodeList) xpath.evaluate("./workgroup", securityElement, XPathConstants.NODESET);
      if (workgroupNodes != null && workgroupNodes.getLength()>0) {
        for (int i = 0; i < workgroupNodes.getLength(); i++) {
          Node workgroupNode = workgroupNodes.item(i);
          String value = workgroupNode.getTextContent().trim();
          if (!Utilities.isEmpty(value)) {
            workgroups.add(value);
          }
        }
      }

      NodeList roleNodes = (NodeList) xpath.evaluate("./role", securityElement, XPathConstants.NODESET);
      if (roleNodes != null && roleNodes.getLength()>0) {
        for (int i = 0; i < roleNodes.getLength(); i++) {
          Element roleElement = (Element)roleNodes.item(i);
          String value = roleElement.getTextContent().trim();
          String allowedValue = roleElement.getAttribute("allowed");
          if (StringUtils.isBlank(allowedValue)) {
        	  allowedValue = "true";
          }
          if (!Utilities.isEmpty(value)) {
        	  if (Boolean.parseBoolean(allowedValue)) {
        		  allowedRoles.add(value);
        	  } else {
        		  disallowedRoles.add(value);
        	  }
          }
        }
      }
    } catch (Exception err) {
      throw new WorkflowRuntimeException(err);
    }
  }

  public Boolean getInitiatorOk() {
    return initiatorOk;
  }
  public void setInitiatorOk(Boolean initiatorOk) {
    this.initiatorOk = initiatorOk;
  }

  public Boolean getRouteLogAuthenticatedOk() {
    return routeLogAuthenticatedOk;
  }
  public void setRouteLogAuthenticatedOk(Boolean routeLogAuthenticatedOk) {
    this.routeLogAuthenticatedOk = routeLogAuthenticatedOk;
  }

  public List<String> getAllowedRoles() {
	return allowedRoles;
  }

  public void setAllowedRoles(List<String> allowedRoles) {
	this.allowedRoles = allowedRoles;
  }

  public List<String> getDisallowedRoles() {
	return disallowedRoles;
  }

  public void setDisallowedRoles(List<String> disallowedRoles) {
	this.disallowedRoles = disallowedRoles;
  }

  public List<KeyValue> getSearchableAttributes() {
	return searchableAttributes;
  }

  public void setSearchableAttributes(List<KeyValue> searchableAttributes) {
	this.searchableAttributes = searchableAttributes;
  }

  public List<String> getWorkgroups() {
	return workgroups;
  }

  public void setWorkgroups(List<String> workgroups) {
	this.workgroups = workgroups;
  }

  public Boolean getActive() {
    return active;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }

  public boolean isActive() {
    if (active != null) {
      return active.booleanValue();
    }
    else {
      return false;
    }
  }

}
