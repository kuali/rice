/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.kns.lookup;

import java.io.Serializable;
import java.sql.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.web.format.DateFormatter;
import org.kuali.rice.kns.authorization.BusinessObjectRestrictions;
import org.kuali.rice.kns.authorization.FieldRestriction;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.service.BusinessObjectAuthorizationService;
import org.kuali.rice.kns.service.KNSServiceLocatorWeb;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * This class holds details of html data for an action url.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 */
public abstract class HtmlData implements Serializable {

	protected static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(HtmlData.class);

	public static final String ANCHOR_HTML_DATA_TYPE = AnchorHtmlData.class.getName();
	public static final String INPUT_HTML_DATA_TYPE = InputHtmlData.class.getName();
	
	protected String name = "";
	protected String title = "";
	protected String methodToCall = "";
	protected String displayText = "";
	protected String prependDisplayText = "";
	protected String appendDisplayText = "";
	protected List<HtmlData> childUrlDataList;
	protected String maxLength;
	
	/**
	 * 
	 * This method constructs the complete html tag based on the class attribute
	 * values.
	 * 
	 * @return
	 */
	public abstract String constructCompleteHtmlTag();

	/**
	 * @return the appendDisplayText
	 */
	public String getAppendDisplayText() {
		return this.appendDisplayText;
	}

	/**
	 * @param appendDisplayText the appendDisplayText to set
	 */
	public void setAppendDisplayText(String appendDisplayText) {
		this.appendDisplayText = appendDisplayText;
	}

	/**
	 * @return the childUrlDataList
	 */
	public List<HtmlData> getChildUrlDataList() {
		return this.childUrlDataList;
	}

	/**
	 * @param childUrlDataList the childUrlDataList to set
	 */
	public void setChildUrlDataList(List<HtmlData> childUrlDataList) {
		this.childUrlDataList = childUrlDataList;
	}

	/**
	 * @return the prependDisplayText
	 */
	public String getPrependDisplayText() {
		return this.prependDisplayText;
	}

	/**
	 * @param prependDisplayText the prependDisplayText to set
	 */
	public void setPrependDisplayText(String prependDisplayText) {
		this.prependDisplayText = prependDisplayText;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the displayText
	 */
	public String getDisplayText() {
		return this.displayText;
	}

	/**
	 * @param displayText the displayText to set
	 */
	public void setDisplayText(String displayText) {
		this.displayText = displayText;
	}

	/**
	 * @return the methodToCall
	 */
	public String getMethodToCall() {
		return this.methodToCall;
	}

	/**
	 * @param methodToCall the methodToCall to set
	 */
	public void setMethodToCall(String methodToCall) {
		this.methodToCall = methodToCall;
	}

	public String getTitle(String prependText, Class bo, List keys) {
		return KNSConstants.EMPTY_STRING;
	}

	/**
	 * KFSMI-658 This method gets the title text for a link/control
	 * 
	 * @param prependText
	 * @param bo
	 * @param keys
	 * @param businessObjectRestrictions
	 * @return title text
	 */
	public static String getTitleText(String prependText, BusinessObject bo, List keys, BusinessObjectRestrictions businessObjectRestrictions) {
		if (bo == null)
			return KNSConstants.EMPTY_STRING;

		Map<String, String> keyValueMap = new HashMap<String, String>();
		Iterator keysIt = keys.iterator();
		while (keysIt.hasNext()) {
			String fieldNm = (String) keysIt.next();
			Object fieldVal = ObjectUtils.getPropertyValue(bo, fieldNm);
			
			FieldRestriction fieldRestriction = null;
			if (businessObjectRestrictions != null) {
				fieldRestriction = businessObjectRestrictions.getFieldRestriction(fieldNm);
			}
			if (fieldRestriction != null && (fieldRestriction.isMasked() || fieldRestriction.isPartiallyMasked())) {
				fieldVal = fieldRestriction.getMaskFormatter().maskValue(fieldVal);
			} else if (fieldVal == null) {
				fieldVal = KNSConstants.EMPTY_STRING;
			} else if (fieldVal instanceof Date) {
				// need to format date in url
				DateFormatter dateFormatter = new DateFormatter();
				fieldVal = dateFormatter.format(fieldVal);
			}
			keyValueMap.put(fieldNm, fieldVal.toString());
		}
		return getTitleText(prependText, bo.getClass(), keyValueMap);
	}
	
	private static BusinessObjectAuthorizationService businessObjectAuthorizationService;
	private static BusinessObjectAuthorizationService getBusinessObjectAuthorizationService() {
		if (businessObjectAuthorizationService == null) {
			businessObjectAuthorizationService = KNSServiceLocatorWeb.getBusinessObjectAuthorizationService();
		}
		return businessObjectAuthorizationService;
	}

	public static String getTitleText(String prependText, Class element, Map<String, String> keyValueMap) {
		Class elementClass = element;
		StringBuffer titleText = new StringBuffer(prependText);
		for (String key : keyValueMap.keySet()) {
			String fieldVal = keyValueMap.get(key).toString();
			
			titleText.append(KNSServiceLocatorWeb.getDataDictionaryService()
					.getAttributeLabel(element, key)
					+ "=" + fieldVal.toString() + " ");
		}
		return titleText.toString();
	}

	/**
	 * 
	 * This class is an extension of HtmlData. It represents an anchor tag.
	 * 
	 * @author Kuali Rice Team (rice.collab@kuali.org)
	 * 
	 */
	public static class AnchorHtmlData extends HtmlData {
		public static final String TARGET_BLANK = "_blank";
		protected String href = "";
		protected String target = "";

		/**
		 * Needed by inquiry framework
		 */
		public AnchorHtmlData() {
		}

		public AnchorHtmlData(String href, String title) {
			this.href = href;
			this.title = title;
		}

		public AnchorHtmlData(String href, String methodToCall,
				String displayText) {
			this.href = href;
			this.methodToCall = methodToCall;
			this.displayText = displayText;
		}

		/**
		 * @param href the href to set
		 */
		public void setHref(String href) {
			this.href = href;
		}

		/**
		 * 
		 * This method generates anchor tag.
		 * 
		 * @see org.kuali.rice.kns.lookup.HtmlData#constructCompleteHtmlTag()
		 */
		public String constructCompleteHtmlTag() {
			String completeHtmlTag;
			if (StringUtils.isEmpty(getHref()))
				completeHtmlTag = getDisplayText();
			else
				completeHtmlTag = getPrependDisplayText()
						+ "<a title=\""
						+ title
						+ "\""
						+ " href=\""
						+ getHref()
						+ "\""
						+ (StringUtils.isEmpty(getTarget()) ? "" : " target=\""
								+ getTarget() + "\" ") + ">" + getDisplayText()
						+ "</a>" + getAppendDisplayText();
			return completeHtmlTag;
		}

		/**
		 * @return the target
		 */
		public String getTarget() {
			return this.target;
		}

		/**
		 * @param target
		 *            the target to set
		 */
		public void setTarget(String target) {
			this.target = target;
		}

		/**
		 * @return the href
		 */
		public String getHref() {
			return this.href;
		}

		/**
		 * @return the methodToCall
		 */
		public String getMethodToCall() {
			return this.methodToCall;
		}

	}

	/**
	 * 
	 * This class is an extension of HtmlData. It represents an input tag.
	 * 
	 * @author Kuali Rice Team (rice.collab@kuali.org)
	 * 
	 */
	public static class InputHtmlData extends HtmlData {
		public static final String CHECKBOX_INPUT_TYPE = "checkbox";
		public static final String CHECKBOX_CHECKED_VALUE = "checked";

		protected String inputType = "";
		protected String src = "";
		protected String styleClass = "";
		protected String border = "0";
		protected String checked = "";
		protected String value = "";

		public InputHtmlData(String name, String inputType) {
			this.name = name;
			this.inputType = inputType;
		}

		public InputHtmlData(String name, String inputType, String src) {
			this.name = name;
			this.inputType = inputType;
			this.src = src;
		}

		/***********************************************************************
		 * 
		 * This method contructs an input tag.
		 * 
		 * @see org.kuali.rice.kns.lookup.HtmlData#constructCompleteHtmlTag()
		 */
		public String constructCompleteHtmlTag() {
			return getPrependDisplayText()
					+ "<input title=\""
					+ title
					+ "\""
					+ " name=\""
					+ getName()
					+ "\""
					+ (StringUtils.isEmpty(src) ? ""
							: " src=\"" + src + "\" ")
					+ " type=\""
					+ getInputType()
					+ "\""
					+ (StringUtils.isEmpty(value) ? ""
							: " value=\"" + value + "\" ")
					+ (StringUtils.isEmpty(checked) ? ""
							: " checked=\"" + checked + "\" ")
					+ (StringUtils.isEmpty(getStyleClass()) ? ""
							: " styleClass=\"" + getStyleClass() + "\" ")
					+ " border=\"" + getBorder() + "\"" + " value=\""
					+ getDisplayText() + "\"" + "/>" + getAppendDisplayText();
		}

		/**
		 * @return the inputType
		 */
		public String getInputType() {
			return this.inputType;
		}

		/**
		 * @return the src
		 */
		public String getSrc() {
			return this.src;
		}

		/**
		 * @return the border
		 */
		public String getBorder() {
			return this.border;
		}

		/**
		 * @param border
		 *            the border to set
		 */
		public void setBorder(String border) {
			this.border = border;
		}

		/**
		 * @return the styleClass
		 */
		public String getStyleClass() {
			return this.styleClass;
		}

		/**
		 * @param styleClass
		 *            the styleClass to set
		 */
		public void setStyleClass(String styleClass) {
			this.styleClass = styleClass;
		}

		/**
		 * @param checked the checked to set
		 */
		public void setChecked(String checked) {
			this.checked = checked;
		}

		/**
		 * @param value the value to set
		 */
		public void setValue(String value) {
			this.value = value;
		}

	}

	public static class MultipleAnchorHtmlData extends AnchorHtmlData {
		protected List<AnchorHtmlData> anchorHtmlData;
		protected static final String ANCHORS_SEPARATOR = ", ";
		
		/**
		 * Needed by inquiry framework
		 */
		public MultipleAnchorHtmlData(List<AnchorHtmlData> anchorHtmlData) {
			this.anchorHtmlData = anchorHtmlData;
		}
		
		/**
		 * 
		 * This method generates anchor tag.
		 * 
		 * @see org.kuali.rice.kns.lookup.HtmlData#constructCompleteHtmlTag()
		 */
		public String constructCompleteHtmlTag() {
			StringBuffer completeHtmlTag = new StringBuffer();
			for(AnchorHtmlData anchor: anchorHtmlData){
				completeHtmlTag.append(anchor.constructCompleteHtmlTag()+",");
			}
	        if(completeHtmlTag.toString().endsWith(ANCHORS_SEPARATOR))
	        	completeHtmlTag.delete(completeHtmlTag.length()-ANCHORS_SEPARATOR.length(), completeHtmlTag.length());
			return completeHtmlTag.toString();
		}

		/**
		 * @return the anchorHtmlData
		 */
		public List<AnchorHtmlData> getAnchorHtmlData() {
			return this.anchorHtmlData;
		}

	}

	/**
	 * @return the maxLength
	 */
	public int getMaxLength() {
		try{
			return Integer.parseInt(this.maxLength);
		} catch(Exception ex){
			return -1;
		}
	}

	/**
	 * @param maxLength the maxLength to set
	 */
	public void setMaxLength(String maxLength) {
		this.maxLength = maxLength;
	}

}
