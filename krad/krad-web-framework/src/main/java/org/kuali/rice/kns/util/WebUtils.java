/*
 * Copyright 2005-2007 The Kuali Foundation
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
package org.kuali.rice.kns.util;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionServletWrapper;
import org.apache.struts.upload.CommonsMultipartRequestHandler;
import org.apache.struts.upload.FormFile;
import org.apache.struts.upload.MultipartRequestHandler;
import org.apache.struts.upload.MultipartRequestWrapper;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.encryption.EncryptionService;
import org.kuali.rice.core.api.services.CoreApiServiceLocator;
import org.kuali.rice.core.framework.parameter.ParameterConstants;
import org.kuali.rice.core.framework.parameter.ParameterService;
import org.kuali.rice.core.framework.services.CoreFrameworkServiceLocator;
import org.kuali.rice.core.util.RiceKeyConstants;
import org.kuali.rice.kns.UserSession;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.datadictionary.AttributeDefinition;
import org.kuali.rice.kns.datadictionary.AttributeSecurity;
import org.kuali.rice.kns.datadictionary.DataDictionary;
import org.kuali.rice.kns.datadictionary.DataDictionaryEntryBase;
import org.kuali.rice.kns.datadictionary.DocumentEntry;
import org.kuali.rice.kns.datadictionary.mask.MaskFormatter;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.authorization.DocumentAuthorizer;
import org.kuali.rice.kns.exception.FileUploadLimitExceededException;
import org.kuali.rice.kns.exception.ValidationException;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.KNSServiceLocatorWeb;
import org.kuali.rice.kns.web.struts.action.KualiMultipartRequestHandler;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.kns.web.struts.form.KualiForm;
import org.kuali.rice.kns.web.struts.form.KualiMaintenanceForm;
import org.kuali.rice.kns.web.struts.pojo.PojoFormBase;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.PageContext;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * General helper methods for handling requests.
 */
public class WebUtils {
	private static final Logger LOG = Logger.getLogger(WebUtils.class);

	private static final String IMAGE_COORDINATE_CLICKED_X_EXTENSION = ".x";
	private static final String IMAGE_COORDINATE_CLICKED_Y_EXTENSION = ".y";

	private static final String APPLICATION_IMAGE_URL_PROPERTY_PREFIX = "application.custom.image.url";
	private static final String DEFAULT_IMAGE_URL_PROPERTY_NAME = "kr.externalizable.images.url";

	/**
	 * A request attribute name that indicates that a
	 * {@link FileUploadLimitExceededException} has already been thrown for the
	 * request.
	 */
	public static final String FILE_UPLOAD_LIMIT_EXCEEDED_EXCEPTION_ALREADY_THROWN = "fileUploadLimitExceededExceptionAlreadyThrown";

	private static ConfigurationService configurationService;

	/**
	 * Checks for methodToCall parameter, and picks off the value using set dot
	 * notation. Handles the problem of image submits.
	 * 
	 * @param request
	 * @return methodToCall String
	 */
	public static String parseMethodToCall(ActionForm form, HttpServletRequest request) {
		String methodToCall = null;

		// check if is specified cleanly
		if (StringUtils.isNotBlank(request.getParameter(KNSConstants.DISPATCH_REQUEST_PARAMETER))) {
			if (form instanceof KualiForm
					&& !((KualiForm) form).shouldMethodToCallParameterBeUsed(KNSConstants.DISPATCH_REQUEST_PARAMETER,
							request.getParameter(KNSConstants.DISPATCH_REQUEST_PARAMETER), request)) {
				throw new RuntimeException("Cannot verify that the methodToCall should be "
						+ request.getParameter(KNSConstants.DISPATCH_REQUEST_PARAMETER));
			}
			methodToCall = request.getParameter(KNSConstants.DISPATCH_REQUEST_PARAMETER);
			// include .x at the end of the parameter to make it consistent w/
			// other parameters
			request.setAttribute(KNSConstants.METHOD_TO_CALL_ATTRIBUTE, KNSConstants.DISPATCH_REQUEST_PARAMETER + "."
					+ methodToCall + IMAGE_COORDINATE_CLICKED_X_EXTENSION);
		}

		/**
		 * The reason why we are checking for a ".x" at the end of the parameter
		 * name: It is for the image names that in addition to sending the form
		 * data, the web browser sends the x,y coordinate of where the user
		 * clicked on the image. If the image input is not given a name then the
		 * browser sends the x and y coordinates as the "x" and "y" input
		 * fields. If the input image does have a name, the x and y coordinates
		 * are sent using the format name.x and name.y.
		 */
		if (methodToCall == null) {
			// iterate through parameters looking for methodToCall
			for (Enumeration i = request.getParameterNames(); i.hasMoreElements();) {
				String parameterName = (String) i.nextElement();

				// check if the parameter name is a specifying the methodToCall
				if (isMethodToCall(parameterName)) {
					methodToCall = getMethodToCallSettingAttribute(form, request, parameterName);
					break;
				}
				else {
					// KULRICE-1218: Check if the parameter's values match (not
					// just the name)
					for (String value : request.getParameterValues(parameterName)) {
						// adding period to startsWith check - don't want to get
						// confused with methodToCallFoobar
						if (isMethodToCall(value)) {
							methodToCall = getMethodToCallSettingAttribute(form, request, value);
							// why is there not a break outer loop here?
						}
					}
				}
			}
		}

		return methodToCall;
	}

	/**
	 * gets the UserSession object from the HttpServletRequest object's
	 * associated session.
	 * 
	 * <p>
	 * In some cases (different threads) the UserSession cannot be retrieved
	 * from GlobalVariables but can still be accessed via the session object
	 * </p>
	 */
	public static final UserSession getUserSessionFromRequest(HttpServletRequest request) {
		return (UserSession) request.getSession().getAttribute(KNSConstants.USER_SESSION_KEY);
	}

	/**
	 * Checks if a string signifies a methodToCall string
	 * 
	 * @param string
	 *            the string to check
	 * @return true if is a methodToCall
	 */
	private static boolean isMethodToCall(String string) {
		// adding period to startsWith check - don't want to get confused with
		// methodToCallFoobar
		return string.startsWith(KNSConstants.DISPATCH_REQUEST_PARAMETER + ".");
	}

	/**
	 * Parses out the methodToCall command and also sets the request attribute
	 * for the methodToCall.
	 * 
	 * @param form
	 *            the ActionForm
	 * @param request
	 *            the request to set the attribute on
	 * @param string
	 *            the methodToCall string
	 * @return the methodToCall command
	 */
	private static String getMethodToCallSettingAttribute(ActionForm form, HttpServletRequest request, String string) {

		if (form instanceof ActionForm
				&& !((KualiForm) form).shouldMethodToCallParameterBeUsed(string, request.getParameter(string), request)) {
			throw new RuntimeException("Cannot verify that the methodToCall should be " + string);
		}
		// always adding a coordinate even if not an image
		final String attributeValue = endsWithCoordinates(string) ? string : string
				+ IMAGE_COORDINATE_CLICKED_X_EXTENSION;
		final String methodToCall = StringUtils.substringBetween(attributeValue,
				KNSConstants.DISPATCH_REQUEST_PARAMETER + ".", ".");
		request.setAttribute(KNSConstants.METHOD_TO_CALL_ATTRIBUTE, attributeValue);
		return methodToCall;
	}

	/**
	 * Iterates through and logs (at the given level) all attributes and
	 * parameters of the given request onto the given Logger
	 * 
	 * @param request
	 * @param logger
	 */
	public static void logRequestContents(Logger logger, Level level, HttpServletRequest request) {
		if (logger.isEnabledFor(level)) {
			logger.log(level, "--------------------");
			logger.log(level, "HttpRequest attributes:");
			for (Enumeration e = request.getAttributeNames(); e.hasMoreElements();) {
				String attrName = (String) e.nextElement();
				Object attrValue = request.getAttribute(attrName);

				if (attrValue.getClass().isArray()) {
					logCollection(logger, level, attrName, Arrays.asList((Object[]) attrValue));
				}
				else if (attrValue instanceof Collection) {
					logCollection(logger, level, attrName, (Collection) attrValue);
				}
				else if (attrValue instanceof Map) {
					logMap(logger, level, attrName, (Map) attrValue);
				}
				else {
					logObject(logger, level, attrName, attrValue);
				}
			}

			logger.log(level, "--------------------");
			logger.log(level, "HttpRequest parameters:");
			for (Enumeration i = request.getParameterNames(); i.hasMoreElements();) {
				String paramName = (String) i.nextElement();
				String[] paramValues = (String[]) request.getParameterValues(paramName);

				logArray(logger, level, paramName, paramValues);
			}

			logger.log(level, "--------------------");
		}
	}

	private static void logArray(Logger logger, Level level, String arrayName, Object[] array) {
		StringBuffer value = new StringBuffer("[");
		for (int i = 0; i < array.length; ++i) {
			if (i > 0) {
				value.append(",");
			}
			value.append(array[i]);
		}
		value.append("]");

		logThing(logger, level, arrayName, value);
	}

	private static void logCollection(Logger logger, Level level, String collectionName, Collection c) {
		StringBuffer value = new StringBuffer("{");
		for (Iterator i = c.iterator(); i.hasNext();) {
			value.append(i.next());
			if (i.hasNext()) {
				value.append(",");
			}
		}
		value.append("}");

		logThing(logger, level, collectionName, value);
	}

	private static void logMap(Logger logger, Level level, String mapName, Map m) {
		StringBuffer value = new StringBuffer("{");
		for (Iterator i = m.entrySet().iterator(); i.hasNext();) {
			Map.Entry e = (Map.Entry) i.next();
			value.append("('" + e.getKey() + "','" + e.getValue() + "')");
		}
		value.append("}");

		logThing(logger, level, mapName, value);
	}

	private static void logObject(Logger logger, Level level, String objectName, Object o) {
		logThing(logger, level, objectName, "'" + o + "'");
	}

	private static void logThing(Logger logger, Level level, String thingName, Object thing) {
		logger.log(level, "    '" + thingName + "' => " + thing);
	}

	/**
	 * A file that is not of type text/plain or text/html can be output through
	 * the response using this method.
	 * 
	 * @param response
	 * @param contentType
	 * @param outStream
	 * @param fileName
	 */
	public static void saveMimeOutputStreamAsFile(HttpServletResponse response, String contentType,
			ByteArrayOutputStream byteArrayOutputStream, String fileName) throws IOException {

		// set response
		response.setContentType(contentType);
		response.setHeader("Content-disposition", "attachment; filename=" + fileName);
		response.setHeader("Expires", "0");
		response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
		response.setHeader("Pragma", "public");
		response.setContentLength(byteArrayOutputStream.size());

		// write to output
		OutputStream outputStream = response.getOutputStream();
		byteArrayOutputStream.writeTo(response.getOutputStream());
		outputStream.flush();
		outputStream.close();
	}

	/**
	 * A file that is not of type text/plain or text/html can be output through
	 * the response using this method.
	 * 
	 * @param response
	 * @param contentType
	 * @param outStream
	 * @param fileName
	 */
	public static void saveMimeInputStreamAsFile(HttpServletResponse response, String contentType,
			InputStream inStream, String fileName, int fileSize) throws IOException {

		// set response
		response.setContentType(contentType);
		response.setHeader("Content-disposition", "attachment; filename=" + fileName);
		response.setHeader("Expires", "0");
		response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
		response.setHeader("Pragma", "public");
		response.setContentLength(fileSize);

		// write to output
		OutputStream out = response.getOutputStream();
		while (inStream.available() > 0) {
			out.write(inStream.read());
		}
		out.flush();
	}

	/**
	 * JSTL function to return the tab state of the tab from the form.
	 * 
	 * @param form
	 * @param tabKey
	 * @return
	 */
	public static String getTabState(KualiForm form, String tabKey) {
		return form.getTabState(tabKey);
	}

	public static void incrementTabIndex(KualiForm form, String tabKey) {
		form.incrementTabIndex();
	}

	/**
	 * Generates a String from the title that can be used as a Map key.
	 * 
	 * @param tabTitle
	 * @return
	 */
	public static String generateTabKey(String tabTitle) {
		String key = "";
		if (!StringUtils.isBlank(tabTitle)) {
			key = tabTitle.replaceAll("\\W", "");
			// if (key.length() > 25) {
			// key = key.substring(0, 24);
			// }
		}

		return key;
	}

	public static void getMultipartParameters(HttpServletRequest request, ActionServletWrapper servletWrapper,
			ActionForm form, ActionMapping mapping) {
		Map params = new HashMap();

		// Get the ActionServletWrapper from the form bean
		// ActionServletWrapper servletWrapper = getServletWrapper();

		try {
			CommonsMultipartRequestHandler multipartHandler = new CommonsMultipartRequestHandler();
			if (multipartHandler != null) {
				// Set servlet and mapping info
				if (servletWrapper != null) {
					// from pojoformbase
					// servlet only affects tempdir on local disk
					servletWrapper.setServletFor(multipartHandler);
				}
				multipartHandler.setMapping((ActionMapping) request.getAttribute(Globals.MAPPING_KEY));
				// Initialize multipart request class handler
				multipartHandler.handleRequest(request);

				Collection<FormFile> files = multipartHandler.getFileElements().values();
				Enumeration keys = multipartHandler.getFileElements().keys();

				while (keys.hasMoreElements()) {
					Object key = keys.nextElement();
					FormFile file = (FormFile) multipartHandler.getFileElements().get(key);
					long maxSize = WebUtils.getMaxUploadSize(form);
					if (LOG.isDebugEnabled()) {
						LOG.debug(file.getFileSize());
					}
					if (maxSize > 0 && Long.parseLong(file.getFileSize() + "") > maxSize) {

						GlobalVariables.getMessageMap().putError(key.toString(),
								RiceKeyConstants.ERROR_UPLOADFILE_SIZE,
								new String[] { file.getFileName(), Long.toString(maxSize) });

					}
				}

				// get file elements for kualirequestprocessor
				if (servletWrapper == null) {
					request.setAttribute(KNSConstants.UPLOADED_FILE_REQUEST_ATTRIBUTE_KEY,
							getFileParametersForMultipartRequest(request, multipartHandler));
				}
			}
		}
		catch (ServletException e) {
			throw new ValidationException("unable to handle multipart request " + e.getMessage(), e);
		}
	}

	public static long getMaxUploadSize(ActionForm form) {
		long max = 0L;
		KualiMultipartRequestHandler multipartHandler = new KualiMultipartRequestHandler();
		if (form instanceof PojoFormBase) {
			max = multipartHandler.calculateMaxUploadSizeToMaxOfList(((PojoFormBase) form).getMaxUploadSizes());
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("Max File Upload Size: " + max);
		}
		return max;
	}

	private static Map getFileParametersForMultipartRequest(HttpServletRequest request,
			MultipartRequestHandler multipartHandler) {
		Map parameters = new HashMap();
		Hashtable elements = multipartHandler.getFileElements();
		Enumeration e = elements.keys();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			parameters.put(key, elements.get(key));
		}

		if (request instanceof MultipartRequestWrapper) {
			request = (HttpServletRequest) ((MultipartRequestWrapper) request).getRequest();
			e = request.getParameterNames();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				parameters.put(key, request.getParameterValues(key));
			}
		}
		else {
			LOG.debug("Gathering multipart parameters for unwrapped request");
		}
		return parameters;
	}

	// end multipart

	public static void registerEditableProperty(PojoFormBase form, String editablePropertyName) {
		form.registerEditableProperty(editablePropertyName);
	}

	public static boolean isDocumentSession(Document document, PojoFormBase docForm) {
		boolean sessionDoc = document instanceof org.kuali.rice.kns.document.SessionDocument;
		boolean dataDictionarySessionDoc = false;
		if (!sessionDoc) {
			DocumentEntry documentEntry = null;
			DataDictionary dataDictionary = KNSServiceLocatorWeb.getDataDictionaryService().getDataDictionary();
			if (docForm instanceof KualiMaintenanceForm) {
				KualiMaintenanceForm maintenanceForm = (KualiMaintenanceForm) docForm;
				if (dataDictionary != null) {
					if (maintenanceForm.getDocTypeName() != null) {
						documentEntry = dataDictionary.getDocumentEntry(maintenanceForm.getDocTypeName());
						dataDictionarySessionDoc = documentEntry.isSessionDocument();
					}
				}
			}
			else {
				if (document != null && dataDictionary != null) {
					documentEntry = dataDictionary.getDocumentEntry(document.getClass().getName());
					dataDictionarySessionDoc = documentEntry.isSessionDocument();
				}
			}
		}
		return sessionDoc || dataDictionarySessionDoc;
	}

	public static boolean isFormSessionDocument(PojoFormBase form) {
		Document document = null;
		if (KualiDocumentFormBase.class.isAssignableFrom(form.getClass())) {
			KualiDocumentFormBase docForm = (KualiDocumentFormBase) form;
			document = docForm.getDocument();
		}
		return isDocumentSession(document, form);
	}

	public static String KEY_KUALI_FORM_IN_SESSION = "KualiForm";

	public static ActionForm getKualiForm(PageContext pageContext) {
		return getKualiForm((HttpServletRequest) pageContext.getRequest());
	}

	public static ActionForm getKualiForm(HttpServletRequest request) {
		if (request.getAttribute(KEY_KUALI_FORM_IN_SESSION) != null) {
			return (ActionForm) request.getAttribute(KEY_KUALI_FORM_IN_SESSION);
		}
		else {
			final HttpSession session = request.getSession(false);
			return session != null ? (ActionForm) session.getAttribute(KEY_KUALI_FORM_IN_SESSION) : null;
		}
	}

	public static boolean isPropertyEditable(Set<String> editableProperties, String propertyName) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("isPropertyEditable(" + propertyName + ")");
		}

		boolean returnVal = editableProperties == null
				|| editableProperties.contains(propertyName)
				|| (getIndexOfCoordinateExtension(propertyName) == -1 ? false : editableProperties
						.contains(propertyName.substring(0, getIndexOfCoordinateExtension(propertyName))));
		if (!returnVal) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("isPropertyEditable(" + propertyName + ") == false / editableProperties: "
						+ editableProperties);
			}
		}
		return returnVal;
	}

	public static boolean endsWithCoordinates(String parameter) {
		return parameter.endsWith(WebUtils.IMAGE_COORDINATE_CLICKED_X_EXTENSION)
				|| parameter.endsWith(WebUtils.IMAGE_COORDINATE_CLICKED_Y_EXTENSION);
	}

	public static int getIndexOfCoordinateExtension(String parameter) {
		int indexOfCoordinateExtension = parameter.lastIndexOf(WebUtils.IMAGE_COORDINATE_CLICKED_X_EXTENSION);
		if (indexOfCoordinateExtension == -1)
			indexOfCoordinateExtension = parameter.lastIndexOf(WebUtils.IMAGE_COORDINATE_CLICKED_Y_EXTENSION);
		return indexOfCoordinateExtension;
	}

    public static boolean isInquiryHiddenField(String className, String fieldName, Object formObject, String propertyName) {
    	boolean isHidden = false;
    	String hiddenInquiryFields = getKualiConfigurationService().getPropertyString(className+".hidden");
    	if (StringUtils.isEmpty(hiddenInquiryFields)) {
    		return isHidden;
    	}
    	List hiddenFields = Arrays.asList(hiddenInquiryFields.replaceAll(" ", "").split(","));
    	if (hiddenFields.contains(fieldName.trim())) {
    		isHidden = true;
    	}
    	return isHidden;
    }

    public static boolean isHiddenKimObjectType(String type, String configParameter) {
    	boolean hideType = false;
    	String hiddenTypes = getKualiConfigurationService().getPropertyString(configParameter);
    	if (StringUtils.isEmpty(hiddenTypes)) {
    		return hideType;
    	}
    	List hiddenTypeValues = Arrays.asList(hiddenTypes.replaceAll(" ", "").split(","));
    	if (hiddenTypeValues.contains(type.trim())) {
    		hideType = true;
    	}
    	return hideType;
    }

	public static String getFullyMaskedValue(String className, String fieldName, Object formObject, String propertyName) {
		String displayMaskValue = null;
		Object propertyValue = ObjectUtils.getPropertyValue(formObject, propertyName);

		DataDictionaryEntryBase entry = (DataDictionaryEntryBase) KNSServiceLocatorWeb.getDataDictionaryService()
				.getDataDictionary().getDictionaryObjectEntry(className);
		AttributeDefinition a = entry.getAttributeDefinition(fieldName);

		AttributeSecurity attributeSecurity = a.getAttributeSecurity();
		if (attributeSecurity != null && attributeSecurity.isMask()) {
			MaskFormatter maskFormatter = attributeSecurity.getMaskFormatter();
			displayMaskValue = maskFormatter.maskValue(propertyValue);

		}
		return displayMaskValue;
	}

	public static String getPartiallyMaskedValue(String className, String fieldName, Object formObject,
			String propertyName) {
		String displayMaskValue = null;
		Object propertyValue = ObjectUtils.getPropertyValue(formObject, propertyName);

		DataDictionaryEntryBase entry = (DataDictionaryEntryBase) KNSServiceLocatorWeb.getDataDictionaryService()
				.getDataDictionary().getDictionaryObjectEntry(className);
		AttributeDefinition a = entry.getAttributeDefinition(fieldName);

		AttributeSecurity attributeSecurity = a.getAttributeSecurity();
		if (attributeSecurity != null && attributeSecurity.isPartialMask()) {
			MaskFormatter partialMaskFormatter = attributeSecurity.getPartialMaskFormatter();
			displayMaskValue = partialMaskFormatter.maskValue(propertyValue);

		}
		return displayMaskValue;
	}

	public static boolean canFullyUnmaskField(String businessObjectClassName, String fieldName, KualiForm form) {
		Class businessObjClass = null;
		try {
			businessObjClass = Class.forName(businessObjectClassName);
		}
		catch (Exception e) {
			throw new RuntimeException("Unable to resolve class name: " + businessObjectClassName);
		}
		if (form instanceof KualiDocumentFormBase) {
			return KNSServiceLocatorWeb.getBusinessObjectAuthorizationService().canFullyUnmaskField(
					GlobalVariables.getUserSession().getPerson(), businessObjClass, fieldName,
					((KualiDocumentFormBase) form).getDocument());
		}
		else {
			return KNSServiceLocatorWeb.getBusinessObjectAuthorizationService().canFullyUnmaskField(
					GlobalVariables.getUserSession().getPerson(), businessObjClass, fieldName, null);
		}
	}

	public static boolean canPartiallyUnmaskField(String businessObjectClassName, String fieldName, KualiForm form) {
		Class businessObjClass = null;
		try {
			businessObjClass = Class.forName(businessObjectClassName);
		}
		catch (Exception e) {
			throw new RuntimeException("Unable to resolve class name: " + businessObjectClassName);
		}
		if (form instanceof KualiDocumentFormBase) {
			return KNSServiceLocatorWeb.getBusinessObjectAuthorizationService().canPartiallyUnmaskField(
					GlobalVariables.getUserSession().getPerson(), businessObjClass, fieldName,
					((KualiDocumentFormBase) form).getDocument());
		}
		else {
			return KNSServiceLocatorWeb.getBusinessObjectAuthorizationService().canPartiallyUnmaskField(
					GlobalVariables.getUserSession().getPerson(), businessObjClass, fieldName, null);
		}
	}

	public static boolean canAddNoteAttachment(Document document) {
		boolean canViewNoteAttachment = false;
		DocumentAuthorizer documentAuthorizer = KNSServiceLocatorWeb.getDocumentHelperService().getDocumentAuthorizer(
				document);
		canViewNoteAttachment = documentAuthorizer.canAddNoteAttachment(document, null, GlobalVariables
				.getUserSession().getPerson());
		return canViewNoteAttachment;
	}

	public static boolean canViewNoteAttachment(Document document, String attachmentTypeCode) {
		boolean canViewNoteAttachment = false;
		DocumentAuthorizer documentAuthorizer = KNSServiceLocatorWeb.getDocumentHelperService().getDocumentAuthorizer(
				document);
		canViewNoteAttachment = documentAuthorizer.canViewNoteAttachment(document, attachmentTypeCode, GlobalVariables
				.getUserSession().getPerson());
		return canViewNoteAttachment;
	}

	public static boolean canDeleteNoteAttachment(Document document, String attachmentTypeCode,
			String authorUniversalIdentifier) {
		boolean canDeleteNoteAttachment = false;
		DocumentAuthorizer documentAuthorizer = KNSServiceLocatorWeb.getDocumentHelperService().getDocumentAuthorizer(
				document);
		canDeleteNoteAttachment = documentAuthorizer.canDeleteNoteAttachment(document, attachmentTypeCode, "false",
				GlobalVariables.getUserSession().getPerson());
		if (canDeleteNoteAttachment) {
			return canDeleteNoteAttachment;
		}
		else {
			canDeleteNoteAttachment = documentAuthorizer.canDeleteNoteAttachment(document, attachmentTypeCode, "true",
					GlobalVariables.getUserSession().getPerson());
			if (canDeleteNoteAttachment
					&& !authorUniversalIdentifier.equals(GlobalVariables.getUserSession().getPerson().getPrincipalId())) {
				canDeleteNoteAttachment = false;
			}
		}
		return canDeleteNoteAttachment;
	}

	public static void reuseErrorMapFromPreviousRequest(KualiDocumentFormBase kualiDocumentFormBase) {
		if (kualiDocumentFormBase.getMessageMapFromPreviousRequest() == null) {
			LOG.error("Error map from previous request is null!");
			return;
		}
		MessageMap errorMapFromGlobalVariables = GlobalVariables.getMessageMap();
		if (kualiDocumentFormBase.getMessageMapFromPreviousRequest() == errorMapFromGlobalVariables) {
			// if we've switched them already, then return early and do nothing
			return;
		}
		if (!errorMapFromGlobalVariables.hasNoErrors()) {
			throw new RuntimeException("Cannot replace error map because it is not empty");
		}
		GlobalVariables.setMessageMap(kualiDocumentFormBase.getMessageMapFromPreviousRequest());
		GlobalVariables.getMessageMap().clearErrorPath();
	}

	/**
	 * Excapes out HTML to prevent XSS attacks, and replaces the following
	 * strings to allow for a limited set of HTML tags
	 * 
	 * <li>[X] and [/X], where X represents any 1 or 2 letter string may be used
	 * to specify the equivalent tag in HTML (i.e. &lt;X&gt; and &lt;/X&gt;) <li>
	 * [font COLOR], where COLOR represents any valid html color (i.e. color
	 * name or hexcode preceeded by #) will be filtered into &lt;font
	 * color="COLOR"/&gt; <li>[/font] will be filtered into &lt;/font&gt; <li>
	 * [table CLASS], where CLASS gives the style class to use, will be filter
	 * into &lt;table class="CLASS"/&gt; <li>[/table] will be filtered into
	 * &lt;/table&gt; <li>[td CLASS], where CLASS gives the style class to use,
	 * will be filter into &lt;td class="CLASS"/&gt;
	 * 
	 * @param inputString
	 * @return
	 */
	public static String filterHtmlAndReplaceRiceMarkup(String inputString) {
		String outputString = StringEscapeUtils.escapeHtml(inputString);
		// string has been escaped of all <, >, and & (and other characters)

		Map<String, String> findAndReplacePatterns = new HashMap<String, String>();

		// now replace our rice custom markup into html

		// DON'T ALLOW THE SCRIPT TAG OR ARBITRARY IMAGES/URLS/ETC. THROUGH

		// filter any one character tags
		findAndReplacePatterns.put("\\[([A-Za-z])\\]", "<$1>");
		findAndReplacePatterns.put("\\[/([A-Za-z])\\]", "</$1>");
		// filter any two character tags
		findAndReplacePatterns.put("\\[([A-Za-z]{2})\\]", "<$1>");
		findAndReplacePatterns.put("\\[/([A-Za-z]{2})\\]", "</$1>");
		// filter the font tag
		findAndReplacePatterns.put("\\[font (#[0-9A-Fa-f]{1,6}|[A-Za-z]+)\\]", "<font color=\"$1\">");
		findAndReplacePatterns.put("\\[/font\\]", "</font>");
		// filter the table tag
		findAndReplacePatterns.put("\\[table\\]", "<table>");
		findAndReplacePatterns.put("\\[table ([A-Za-z]+)\\]", "<table class=\"$1\">");
		findAndReplacePatterns.put("\\[/table\\]", "</table>");
		// fiter td with class
		findAndReplacePatterns.put("\\[td ([A-Za-z]+)\\]", "<td class=\"$1\">");

		for (String findPattern : findAndReplacePatterns.keySet()) {
			Pattern p = Pattern.compile(findPattern);
			Matcher m = p.matcher(outputString);
			if (m.find()) {
				String replacePattern = findAndReplacePatterns.get(findPattern);
				outputString = m.replaceAll(replacePattern);
			}
		}

		return outputString;
	}

	public static boolean containsSensitiveDataPatternMatch(String fieldValue) {
		if (StringUtils.isBlank(fieldValue)) {
			return false;
		}
		ParameterService parameterService = CoreFrameworkServiceLocator.getParameterService();
		Collection<String> sensitiveDataPatterns = parameterService.getParameterValuesAsString(KNSConstants.KNS_NAMESPACE,
				ParameterConstants.ALL_COMPONENT, KNSConstants.SystemGroupParameterNames.SENSITIVE_DATA_PATTERNS);
		for (String pattern : sensitiveDataPatterns) {
			if (Pattern.compile(pattern).matcher(fieldValue).find()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Determines and returns the URL for question button images; looks first
	 * for a property "application.custom.image.url", and if that is missing,
	 * uses the image url returned by getDefaultButtonImageUrl()
	 * 
	 * @param imageName
	 *            the name of the image to find a button for
	 * @return the URL where question button images are located
	 */
	public static String getButtonImageUrl(String imageName) {
		String buttonImageUrl = getKualiConfigurationService().getPropertyString(
				WebUtils.APPLICATION_IMAGE_URL_PROPERTY_PREFIX + "." + imageName);
		if (StringUtils.isBlank(buttonImageUrl)) {
			buttonImageUrl = getDefaultButtonImageUrl(imageName);
		}
		return buttonImageUrl;
	}

	/**
	 * Generates a default button image URL, in the form of:
	 * ${kr.externalizable.images.url}buttonsmall_${imageName}.gif
	 * 
	 * @param imageName
	 *            the image name to generate a default button name for
	 * @return the default button image url
	 */
	public static String getDefaultButtonImageUrl(String imageName) {
		return getKualiConfigurationService().getPropertyString(WebUtils.DEFAULT_IMAGE_URL_PROPERTY_NAME)
				+ "buttonsmall_" + imageName + ".gif";
	}

	/**
	 * @return whether the deploy environment is production
	 */
	public static boolean isProductionEnvironment() {
		return getKualiConfigurationService().isProductionEnvironment();
	}

	/**
	 * @return an implementation of the KualiConfigurationService
	 */
	public static ConfigurationService getKualiConfigurationService() {
		if (configurationService == null) {
			configurationService = KNSServiceLocator.getKualiConfigurationService();
		}
		return configurationService;
	}

	/**
	 * Translates the given Map of String keys and String array values to a Map
	 * of String key and values. If the String array contains more than one
	 * value, the single string is built by joining the values with the vertical
	 * bar character
	 * 
	 * @param requestParameters
	 *            - Map of request parameters to translate
	 * @return Map<String, String> translated Map
	 */
	public static Map<String, String> translateRequestParameterMap(Map<String, String[]> requestParameters) {
		Map<String, String> parameters = new HashMap<String, String>();

		for (Map.Entry<String, String[]> parameter : requestParameters.entrySet()) {
			String parameterValue = "";
			if (parameter.getValue().length > 1) {
				parameterValue = StringUtils.join(parameter.getValue(), "|");
			}
			else {
				parameterValue = parameter.getValue()[0];
			}
			parameters.put(parameter.getKey(), parameterValue);
		}

		return parameters;
	}

	/**
	 * Retrieves parameter values from the request that match the requested
	 * names. In addition, based on the object class an authorization check is
	 * performed to determine if the values are secure and should be decrypted.
	 * If true, the value is decrypted before returning
	 * 
	 * @param parameterNames
	 *            - names of the parameters whose values should be retrieved
	 *            from the request
	 * @param parentObjectClass
	 *            - object class that contains the parameter names as properties
	 *            and should be consulted for security checks
	 * @param requestParameters
	 *            - all request parameters to pull from
	 * @return Map<String, String> populated with parameter name/value pairs
	 *         pulled from the request
	 */
	public static Map<String, String> getParametersFromRequest(List<String> parameterNames,
			Class<? extends BusinessObject> parentObjectClass, Map<String, String> requestParameters) {
		Map<String, String> parameterValues = new HashMap<String, String>();
		
		for (Iterator<String> iter = parameterNames.iterator(); iter.hasNext();) {
			String keyPropertyName = iter.next();

			if (requestParameters.get(keyPropertyName) != null) {
				String keyValue = requestParameters.get(keyPropertyName);

				// Check if this element was encrypted, if it was decrypt it
				if (KNSServiceLocatorWeb.getBusinessObjectAuthorizationService()
						.attributeValueNeedsToBeEncryptedOnFormsAndLinks(parentObjectClass, keyPropertyName)) {
					try {
						keyValue = StringUtils.removeEnd(keyValue, EncryptionService.ENCRYPTION_POST_PREFIX);
						keyValue = CoreApiServiceLocator.getEncryptionService().decrypt(keyValue);
					}
					catch (GeneralSecurityException e) {
						throw new RuntimeException(e);
					}
				}

				parameterValues.put(keyPropertyName, keyValue);
			}
		}

		return parameterValues;
	}
	
	/**
	 * Translates characters in the given string like brackets that will cause
	 * problems with binding to characters that do not affect the binding
	 * 
	 * @param key
	 *            - string to translate
	 * @return String translated string
	 */
	public static String translateToMapSafeKey(String key) {
		String safeKey = key;
		
		safeKey = StringUtils.replace(safeKey, "[", "_");
		safeKey = StringUtils.replace(safeKey, "]", "_");

		return safeKey;
	}
	
	/**
	 * Builds a string from the given map by joining each entry with a comma and
	 * each key/value pair with a colon
	 * 
	 * @param map
	 *            - map instance to build string for
	 * @return String of map entries
	 */
	public static String buildMapParameterString(Map<String, String> map) {
		String parameterString = "";

		for (Entry<String, String> entry : map.entrySet()) {
			if (StringUtils.isNotBlank(parameterString)) {
				parameterString += ",";
			}

			parameterString += entry.getKey() + ":" + entry.getValue();
		}

		return parameterString;
	}
	
	/**
	 * Parses the given string into a Map by splitting on the comma to get the
	 * map entries and within each entry splitting by colon to get the key/value
	 * pairs
	 * 
	 * @param parameterString
	 *            - string to parse into map
	 * @return Map<String, String> map from string
	 */
	public static Map<String, String> getMapFromParameterString(String parameterString) {
		Map<String, String> map = new HashMap<String, String>();

		String[] entries = parameterString.split(".");
		for (int i = 0; i < entries.length; i++) {
			String[] keyValue = entries[i].split(":");
			if (keyValue.length != 2) {
				throw new RuntimeException("malformed field conversion pair: " + Arrays.toString(keyValue));
			}

			map.put(keyValue[0], keyValue[1]);
		}

		return map;
	}
	
    /**
     * Converts a set to a map by creating a new map entry for each set entry
     * where the map key is the set entry value and the map value is the boolean
     * true
     * 
     * @param setToConvert
     *            - set instance to convert
     * @return Map<String, Boolean> map converted from set
     */
    public static  Map<String, Boolean> convertSetToBoolenMap(Set<String> setToConvert) {
        Map<String, Boolean> map = new HashMap<String, Boolean>();

        for (String str : setToConvert) {
            map.put(str, Boolean.TRUE);
        }

        return map;
    }
}
