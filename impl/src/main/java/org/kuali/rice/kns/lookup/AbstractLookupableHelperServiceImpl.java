/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.rice.kns.lookup;

import java.security.GeneralSecurityException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.service.EncryptionService;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.datadictionary.mask.Mask;
import org.kuali.rice.kns.exception.ValidationException;
import org.kuali.rice.kns.inquiry.Inquirable;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.lookup.HtmlData.InputHtmlData;
import org.kuali.rice.kns.service.AuthorizationService;
import org.kuali.rice.kns.service.BusinessObjectDictionaryService;
import org.kuali.rice.kns.service.BusinessObjectMetaDataService;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.LookupService;
import org.kuali.rice.kns.service.MaintenanceDocumentDictionaryService;
import org.kuali.rice.kns.service.PersistenceStructureService;
import org.kuali.rice.kns.service.SequenceAccessorService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.util.FieldUtils;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.RiceKeyConstants;
import org.kuali.rice.kns.util.UrlFactory;
import org.kuali.rice.kns.util.cache.CopiedObject;
import org.kuali.rice.kns.web.comparator.CellComparatorHelper;
import org.kuali.rice.kns.web.format.BooleanFormatter;
import org.kuali.rice.kns.web.format.CollectionFormatter;
import org.kuali.rice.kns.web.format.DateFormatter;
import org.kuali.rice.kns.web.format.Formatter;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.struts.form.MultipleValueLookupForm;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.kns.web.ui.Row;

/**
 * This class declares many of the common spring injected properties, the get/set-ers for them,
 * and some common util methods that require the injected services
 */
public abstract class AbstractLookupableHelperServiceImpl implements LookupableHelperService {

    protected static final String TITLE_RETURN_URL_PREPENDTEXT_PROPERTY = "title.return.url.value.prependtext";
    protected static final String TITLE_ACTION_URL_PREPENDTEXT_PROPERTY = "title.action.url.value.prependtext";
    protected static final String ACTION_URLS_CHILDREN_SEPARATOR = "&nbsp;|&nbsp;";
    protected static final String ACTION_URLS_CHILDREN_STARTER = "&nbsp;[";
    protected static final String ACTION_URLS_CHILDREN_END = "]";
    protected static final String ACTION_URLS_SEPARATOR = "&nbsp;&nbsp;";
    
    protected static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AbstractLookupableHelperServiceImpl.class);

    private Class businessObjectClass;
    private Map parameters;
    private BusinessObjectDictionaryService businessObjectDictionaryService;
    private BusinessObjectMetaDataService businessObjectMetaDataService;
    private DataDictionaryService dataDictionaryService;
    private PersistenceStructureService persistenceStructureService;
    private EncryptionService encryptionService;
    private List<String> readOnlyFieldsList;
    private String backLocation;
    private String docFormKey;
    private Map fieldConversions;
    private LookupService lookupService;
    private org.kuali.rice.kim.service.PersonService personService;
    private List<Row> rows;
    private String referencesToRefresh;
    private SequenceAccessorService sequenceAccessorService;
    private BusinessObjectService businessObjectService;
    private LookupResultsService lookupResultsService;
    private String docNum;

    /**
	 * @return the docNum
	 */
	public String getDocNum() {
		return this.docNum;
	}

	/**
	 * @param docNum the docNum to set
	 */
	public void setDocNum(String docNum) {
		this.docNum = docNum;
	}

	public AbstractLookupableHelperServiceImpl() {
        rows = null;
    }

    /**
     * This implementation always returns false.
     *
     * @see org.kuali.core.lookup.LookupableHelperService#checkForAdditionalFields(java.util.Map)
     */
    public boolean checkForAdditionalFields(Map fieldValues) {
        return false;
    }

    /**
     * @see org.kuali.core.lookup.LookupableHelperService#getBusinessObjectClass()
     */
    public Class getBusinessObjectClass() {
        return businessObjectClass;
    }

    /**
     * @see org.kuali.core.lookup.LookupableHelperService#setBusinessObjectClass(java.lang.Class)
     */
    public void setBusinessObjectClass(Class businessObjectClass) {
        this.businessObjectClass = businessObjectClass;
        setRows();
    }

    /**
     * @see org.kuali.core.lookup.LookupableHelperService#getParameters()
     */
    public Map getParameters() {
        return parameters;
    }

    /**
     * @see org.kuali.core.lookup.LookupableHelperService#setParameters(java.util.Map)
     */
    public void setParameters(Map parameters) {
        this.parameters = parameters;
    }

    /**
     * Gets the dataDictionaryService attribute.
     * @return Returns the dataDictionaryService.
     */
    public DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService != null ? dataDictionaryService : KNSServiceLocator.getDataDictionaryService();
    }

    /**
     * Sets the dataDictionaryService attribute value.
     * @param dataDictionaryService The dataDictionaryService to set.
     */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    /**
     * Gets the businessObjectDictionaryService attribute.
     * @return Returns the businessObjectDictionaryService.
     */
    public BusinessObjectDictionaryService getBusinessObjectDictionaryService() {
        return businessObjectDictionaryService != null ? businessObjectDictionaryService : KNSServiceLocator.getBusinessObjectDictionaryService();
    }

    /**
     * Sets the businessObjectDictionaryService attribute value.
     * @param businessObjectDictionaryService The businessObjectDictionaryService to set.
     */
    public void setBusinessObjectDictionaryService(BusinessObjectDictionaryService businessObjectDictionaryService) {
        this.businessObjectDictionaryService = businessObjectDictionaryService;
    }

    /**
     * Gets the businessObjectMetaDataService attribute.
     * @return Returns the businessObjectMetaDataService.
     */
    public BusinessObjectMetaDataService getBusinessObjectMetaDataService() {
        return businessObjectMetaDataService != null ? businessObjectMetaDataService : KNSServiceLocator.getBusinessObjectMetaDataService();
    }

    /**
     * Sets the businessObjectMetaDataService attribute value.
     * @param businessObjectMetaDataService The businessObjectMetaDataService to set.
     */
    public void setBusinessObjectMetaDataService(BusinessObjectMetaDataService businessObjectMetaDataService) {
        this.businessObjectMetaDataService = businessObjectMetaDataService;
    }

    /**
     * Gets the persistenceStructureService attribute.
     * @return Returns the persistenceStructureService.
     */
    protected PersistenceStructureService getPersistenceStructureService() {
        return persistenceStructureService != null ? persistenceStructureService : KNSServiceLocator.getPersistenceStructureService();
    }

    /**
     * Sets the persistenceStructureService attribute value.
     * @param persistenceStructureService The persistenceStructureService to set.
     */
    public void setPersistenceStructureService(PersistenceStructureService persistenceStructureService) {
        this.persistenceStructureService = persistenceStructureService;
    }

    /**
     * Gets the encryptionService attribute. 
     * @return Returns the encryptionService.
     */
    protected EncryptionService getEncryptionService() {
        return encryptionService != null ? encryptionService : KNSServiceLocator.getEncryptionService();
    }

    /**
     * Sets the encryptionService attribute value.
     * @param encryptionService The encryptionService to set.
     */
    public void setEncryptionService(EncryptionService encryptionService) {
        this.encryptionService = encryptionService;
    }

    private MaintenanceDocumentDictionaryService maintenanceDocumentDictionaryService;

    public MaintenanceDocumentDictionaryService getMaintenanceDocumentDictionaryService() {
	if ( maintenanceDocumentDictionaryService == null ) {
	    maintenanceDocumentDictionaryService = KNSServiceLocator.getMaintenanceDocumentDictionaryService();
	}
	return maintenanceDocumentDictionaryService;
    }

    private Inquirable kualiInquirable;

    public Inquirable getKualiInquirable() {
	if ( kualiInquirable == null ) {
	    kualiInquirable = KNSServiceLocator.getKualiInquirable();
	}
	return kualiInquirable;
    }

    public void setMaintenanceDocumentDictionaryService(MaintenanceDocumentDictionaryService maintenanceDocumentDictionaryService) {
        this.maintenanceDocumentDictionaryService = maintenanceDocumentDictionaryService;
    }

    public void setKualiInquirable(Inquirable kualiInquirable) {
        this.kualiInquirable = kualiInquirable;
    }

    private KualiConfigurationService kualiConfigurationService;

    public KualiConfigurationService getKualiConfigurationService() {
	if ( kualiConfigurationService == null ) {
	    kualiConfigurationService = KNSServiceLocator.getKualiConfigurationService();
	}
        return kualiConfigurationService;
    }

    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    AuthorizationService authorizationService;

    public AuthorizationService getAuthorizationService() {
	if ( authorizationService == null ) {
	    authorizationService = KNSServiceLocator.getAuthorizationService();
	}
        return this.authorizationService;
    }

    public void setAuthorizationService(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    /**
     * Determines if underlying lookup bo has associated maintenance document that allows new or copy maintenance actions.
     *
     * @return true if bo has maint doc that allows new or copy actions
     */
    public boolean allowsMaintenanceNewOrCopyAction() {
        boolean allowsNewOrCopy = false;

        String maintDocTypeName = getMaintenanceDocumentTypeName();
        if (StringUtils.isNotBlank(maintDocTypeName)) {
            allowsNewOrCopy = getMaintenanceDocumentDictionaryService().getAllowsNewOrCopy(maintDocTypeName);
        }

        return allowsNewOrCopy;
    }

    /**
     * Build a maintenance url.
     *
     * @param bo - business object representing the record for maint.
     * @param methodToCall - maintenance action
     * @return
     */
    final public String getMaintenanceUrl(BusinessObject businessObject, HtmlData htmlData, List pkNames) {
    	htmlData.setTitle(getActionUrlTitleText(businessObject, htmlData.getDisplayText(), pkNames));
		return htmlData.constructCompleteHtmlTag();
    }

    /**
     * This method is called by performLookup method to generate action urls. 
     * It calls the method getCustomActionUrls to get html data, calls getMaintenanceUrl to get the actual html tag, 
     * and returns a formatted/concatenated string of action urls.
     * 
     * @see org.kuali.core.lookup.LookupableHelperService#getActionUrls(org.kuali.core.bo.BusinessObject)
     */
    final public String getActionUrls(BusinessObject businessObject, List pkNames) {
        StringBuffer actions = new StringBuffer();
        List<HtmlData> htmlDataList = getCustomActionUrls(businessObject, pkNames);
        for(HtmlData htmlData: htmlDataList){
        	actions.append(getMaintenanceUrl(businessObject, htmlData, pkNames));
            if(htmlData.getChildUrlDataList()!=null){
            	if(htmlData.getChildUrlDataList().size()>0){
                    actions.append(ACTION_URLS_CHILDREN_STARTER);
            		for(HtmlData childURLData: htmlData.getChildUrlDataList()){
	                	actions.append(getMaintenanceUrl(businessObject, childURLData, pkNames));
	                    actions.append(ACTION_URLS_CHILDREN_SEPARATOR);
	            	}
            		if(actions.toString().endsWith(ACTION_URLS_CHILDREN_SEPARATOR))
            			actions.delete(actions.length()-ACTION_URLS_CHILDREN_SEPARATOR.length(), actions.length());
                    actions.append(ACTION_URLS_CHILDREN_END);
            	}
            }
        	actions.append(ACTION_URLS_SEPARATOR);
        }
        if(actions.toString().endsWith(ACTION_URLS_SEPARATOR))
        	actions.delete(actions.length()-ACTION_URLS_SEPARATOR.length(), actions.length());
        return actions.toString();
    }

    /**
     * Child classes should override this method if they want to return some other action urls.
     * 
     * @returns This default implementation returns links to edit and copy maintenance action for 
     * the current maintenance record if the business object class has an associated maintenance document. 
     * Also checks value of allowsNewOrCopy in maintenance document xml before rendering the copy link.
     * 
     * @see org.kuali.rice.kns.lookup.LookupableHelperService#getCustomActionUrls(org.kuali.rice.kns.bo.BusinessObject, java.util.List, java.util.List pkNames)
     */
    public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames){
    	List<HtmlData> htmlDataList = new ArrayList<HtmlData>();
        if (StringUtils.isNotBlank(getMaintenanceDocumentTypeName())) {
        	htmlDataList.add(getUrlData(businessObject, KNSConstants.MAINTENANCE_EDIT_METHOD_TO_CALL, pkNames));
        }
        if (allowsMaintenanceNewOrCopyAction()) {
        	htmlDataList.add(getUrlData(businessObject, KNSConstants.MAINTENANCE_COPY_METHOD_TO_CALL, pkNames));
        }
        return htmlDataList;
    }

    /**
     * 
     * This method constructs an AnchorHtmlData. 
     * This method can be overriden by child classes if they want to construct the html data in a different way.
     * Foe example, if they want different type of html tag, like input/image.
     * 
     * @param businessObject
     * @param methodToCall
     * @param displayText
     * @param pkNames
     * @return
     */
    protected AnchorHtmlData getUrlData(BusinessObject businessObject, String methodToCall, String displayText, List pkNames){

    	String href = getActionUrlHref(businessObject, methodToCall, pkNames);
    	//String title = StringUtils.isBlank(href)?"":getActionUrlTitleText(businessObject, displayText, pkNames);
    	AnchorHtmlData anchorHtmlData = new AnchorHtmlData(href, methodToCall, displayText);
    	return anchorHtmlData;
    }

    /**
     * 
     * This method calls its overloaded method with displayText as methodToCall
     * 
     * @param businessObject
     * @param methodToCall
     * @param pkNames
     * @return
     */
    protected AnchorHtmlData getUrlData(BusinessObject businessObject, String methodToCall, List pkNames){
      	return getUrlData(businessObject, methodToCall, methodToCall, pkNames);
    }

    /**
     * 
     * A utility method that returns an empty list of action urls.
     * 
     * @return
     */
    protected List<HtmlData> getEmptyActionUrls(){
    	return new ArrayList<HtmlData>();
    }

    protected HtmlData getEmptyAnchorHtmlData(){
    	return new AnchorHtmlData();
    }
    
    /**
     * 
     * This method generates and returns href for the given parameters. 
     * This method can be overridden by child classes if they have to generate href differently. 
     * For example, refer to IntendedIncumbentLookupableHelperServiceImpl
     * 
     * @param businessObject
     * @param methodToCall
     * @param pkNames
     * @return
     */
    protected String getActionUrlHref(BusinessObject businessObject, String methodToCall, List pkNames){
        Properties parameters = new Properties();
        parameters.put(KNSConstants.DISPATCH_REQUEST_PARAMETER, methodToCall);
        parameters.put(KNSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, this.businessObjectClass.getName());

        for (Iterator iter = pkNames.iterator(); iter.hasNext();) {
            String fieldNm = (String) iter.next();

            Object fieldVal = ObjectUtils.getPropertyValue(businessObject, fieldNm);
            if (fieldVal == null) {
                fieldVal = KNSConstants.EMPTY_STRING;
            }
            if (fieldVal instanceof java.sql.Date) {
                String formattedString = "";
                if (Formatter.findFormatter(fieldVal.getClass()) != null) {
                    Formatter formatter = Formatter.getFormatter(fieldVal.getClass());
                    formattedString = (String) formatter.format(fieldVal);
                    fieldVal = formattedString;
                }
            }

            // Encrypt value if it is a secure field
            String displayWorkgroup = getDataDictionaryService().getAttributeDisplayWorkgroup(businessObject.getClass(), fieldNm);
            if (StringUtils.isNotBlank(displayWorkgroup)) {
                try {
                    fieldVal = getEncryptionService().encrypt(fieldVal);
                }
                catch (GeneralSecurityException e) {
                    LOG.error("Exception while trying to encrypted value for inquiry framework.", e);
                    throw new RuntimeException(e);
                }

            }

            parameters.put(fieldNm, fieldVal.toString());
        }

        return UrlFactory.parameterizeUrl(KNSConstants.MAINTENANCE_ACTION, parameters);
    }

    /**
     * 
     * This method generates and returns title text for action urls.
     * Child classes can override this if they want to generate the title text differently.
     * For example, refer to BatchJobStatusLookupableHelperServiceImpl
     * 
     * @param businessObject
     * @param displayText
     * @param pkNames
     * @return
     */
    protected String getActionUrlTitleText(BusinessObject businessObject, String displayText, List pkNames){
        String prependTitleText = displayText+" "
    		+getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(getBusinessObjectClass().getName()).getObjectLabel()
    		+" "
    		+KNSServiceLocator.getKualiConfigurationService().getPropertyString(TITLE_ACTION_URL_PREPENDTEXT_PROPERTY);
        return HtmlData.getTitleText(prependTitleText, businessObject, pkNames);
    }

    /**
     * Returns the maintenance document type associated with the business object class or null if one does not
     * exist.
     * @return String representing the maintenance document type name
     */
    protected String getMaintenanceDocumentTypeName() {
        MaintenanceDocumentDictionaryService dd = getMaintenanceDocumentDictionaryService();
        String maintDocTypeName = dd.getDocumentTypeName(getBusinessObjectClass());
        return maintDocTypeName;
    }

    /**
     * Gets the readOnlyFieldsList attribute.
     *
     * @return Returns the readOnlyFieldsList.
     */
    public List<String> getReadOnlyFieldsList() {
        return readOnlyFieldsList;
    }


    /**
     * Sets the readOnlyFieldsList attribute value.
     *
     * @param readOnlyFieldsList The readOnlyFieldsList to set.
     */
    public void setReadOnlyFieldsList(List<String> readOnlyFieldsList) {
        this.readOnlyFieldsList = readOnlyFieldsList;
    }

    private HashMap<String,Boolean> noLookupResultFieldInquiryCache = new HashMap<String, Boolean>();
    private HashMap<Class,Class> inquirableClassCache = new HashMap<Class, Class>();
    private HashMap<String,Boolean> forceLookupResultFieldInquiryCache = new HashMap<String, Boolean>();
    /**
     * Returns the inquiry url for a field if one exist.
     *
     * @param bo the business object instance to build the urls for
     * @param propertyName the property which links to an inquirable
     * @return String url to inquiry
     */
    public HtmlData getInquiryUrl(BusinessObject bo, String propertyName) {
        HtmlData inquiryUrl = new AnchorHtmlData();

        String cacheKey = bo.getClass().getName()+"."+propertyName;
        Boolean noLookupResultFieldInquiry = noLookupResultFieldInquiryCache.get( cacheKey );
        if ( noLookupResultFieldInquiry == null ) {
            noLookupResultFieldInquiry = getBusinessObjectDictionaryService().noLookupResultFieldInquiry(bo.getClass(), propertyName);
            if ( noLookupResultFieldInquiry == null ) {
        	noLookupResultFieldInquiry = Boolean.TRUE;
            }
            noLookupResultFieldInquiryCache.put(cacheKey, noLookupResultFieldInquiry);
        }
        if ( !noLookupResultFieldInquiry ) {

            Class<Inquirable> inquirableClass = inquirableClassCache.get( bo.getClass() );
            if ( !inquirableClassCache.containsKey( bo.getClass() ) ) {
        	inquirableClass = getBusinessObjectDictionaryService().getInquirableClass(bo.getClass());
        	inquirableClassCache.put(bo.getClass(), inquirableClass);
            }
            Inquirable inq = null;
            try {
                if ( inquirableClass != null ) {
                    inq = inquirableClass.newInstance();
                } else {
                    inq = getKualiInquirable();
                    if ( LOG.isDebugEnabled() ) {
                        LOG.debug( "Default Inquirable Class: " + inq.getClass() );
                    }
                }
                Boolean forceLookupResultFieldInquiry = forceLookupResultFieldInquiryCache.get( cacheKey );
                if ( forceLookupResultFieldInquiry == null ) {
                    forceLookupResultFieldInquiry = getBusinessObjectDictionaryService().forceLookupResultFieldInquiry(bo.getClass(), propertyName);
                    if ( forceLookupResultFieldInquiry == null ) {
                	forceLookupResultFieldInquiry = Boolean.FALSE;
                    }
                    forceLookupResultFieldInquiryCache.put(cacheKey, forceLookupResultFieldInquiry);
                }
                inquiryUrl = inq.getInquiryUrl(bo, propertyName, forceLookupResultFieldInquiry);
            } catch ( Exception ex ) {
                LOG.error("unable to create inquirable to get inquiry URL", ex );
            }
        }

        return inquiryUrl;
    }

    private CopiedObject<ArrayList<Column>> resultColumns = null;

    /**
     * Constructs the list of columns for the search results. All properties for the column objects come from the DataDictionary.
     */
    public List<Column> getColumns() {
        if ( resultColumns == null ) {
            ArrayList<Column> columns = new ArrayList<Column>();
            for (String attributeName : getBusinessObjectDictionaryService().getLookupResultFieldNames(getBusinessObjectClass())) {
                Column column = new Column();
                column.setPropertyName(attributeName);
                String columnTitle = getDataDictionaryService().getAttributeLabel(getBusinessObjectClass(), attributeName);
                Boolean useShortLabel = getBusinessObjectDictionaryService().getLookupResultFieldUseShortLabel(businessObjectClass, attributeName);
                if(useShortLabel != null && useShortLabel){
                	columnTitle = getDataDictionaryService().getAttributeShortLabel(getBusinessObjectClass(), attributeName);
                }
                if (StringUtils.isBlank(columnTitle)) {
                    columnTitle = getDataDictionaryService().getCollectionLabel(getBusinessObjectClass(), attributeName);
                }
                column.setColumnTitle(columnTitle);
                column.setMaxLength(getColumnMaxLength(attributeName));

                Class formatterClass = getDataDictionaryService().getAttributeFormatter(getBusinessObjectClass(), attributeName);
                if (formatterClass != null) {
                    try {
                        column.setFormatter((Formatter) formatterClass.newInstance());
                    }
                    catch (InstantiationException e) {
                        LOG.error("Unable to get new instance of formatter class: " + formatterClass.getName());
                        throw new RuntimeException("Unable to get new instance of formatter class: " + formatterClass.getName());
                    }
                    catch (IllegalAccessException e) {
                        LOG.error("Unable to get new instance of formatter class: " + formatterClass.getName());
                        throw new RuntimeException("Unable to get new instance of formatter class: " + formatterClass.getName());
                    }
                }

                columns.add(column);
            }
            resultColumns = ObjectUtils.deepCopyForCaching(columns);
            return columns;
	    }
        return resultColumns.getContent();
    }

    private static Integer RESULTS_DEFAULT_MAX_COLUMN_LENGTH = null;
    
    protected int getColumnMaxLength(String attributeName) {
    	Integer fieldDefinedMaxLength = getBusinessObjectDictionaryService().getLookupResultFieldMaxLength(getBusinessObjectClass(), attributeName);
    	if (fieldDefinedMaxLength == null) {
    		if ( RESULTS_DEFAULT_MAX_COLUMN_LENGTH == null ) {
    			try {
    				RESULTS_DEFAULT_MAX_COLUMN_LENGTH = Integer.valueOf( getKualiConfigurationService().getParameterValue(KNSConstants.KNS_NAMESPACE, KNSConstants.DetailTypes.LOOKUP_PARM_DETAIL_TYPE, KNSConstants.RESULTS_DEFAULT_MAX_COLUMN_LENGTH) );
    			} catch ( NumberFormatException ex ) {
    				LOG.error("Lookup field max length parameter not found and unable to parse default set in system parameters (RESULTS_DEFAULT_MAX_COLUMN_LENGTH).");
    			}
    		}
            return RESULTS_DEFAULT_MAX_COLUMN_LENGTH.intValue();
	}
        return fieldDefinedMaxLength.intValue();
    }

    /**
     * @return Returns the backLocation.
     */
    public String getBackLocation() {
        return backLocation;
    }

    /**
     * @param backLocation The backLocation to set.
     */
    public void setBackLocation(String backLocation) {
        this.backLocation = backLocation;
    }

    /**
     * @see org.kuali.core.lookup.LookupableHelperService#getReturnLocation()
     */
    public String getReturnLocation() {
        return backLocation;
    }

    /**
     * This method is for lookupable implementations
     * 
     * @see org.kuali.rice.kns.lookup.LookupableHelperService#getReturnUrl(org.kuali.rice.kns.bo.BusinessObject, java.util.Map, java.lang.String, java.util.List)
     */
    final public HtmlData getReturnUrl(BusinessObject businessObject, Map fieldConversions, String lookupImpl, List returnKeys){
    	String href = getReturnHref(businessObject, fieldConversions, lookupImpl, returnKeys);
    	String returnUrlAnchorLabel = 
        	KNSServiceLocator.getKualiConfigurationService().getPropertyString(TITLE_RETURN_URL_PREPENDTEXT_PROPERTY);
        AnchorHtmlData anchor = new AnchorHtmlData(href, HtmlData.getTitleText(returnUrlAnchorLabel, businessObject, returnKeys));
        anchor.setDisplayText(returnUrlAnchorLabel);
        return anchor;
    }

    /**
     * 
     * This method is for lookupable implementations
     * 
     * @param businessObject
     * @param fieldConversions
     * @param lookupImpl
     * @param returnKeys
     * @return
     */
    final protected String getReturnHref(BusinessObject businessObject, Map fieldConversions, String lookupImpl, List returnKeys) {
        return UrlFactory.parameterizeUrl(backLocation, getParameters(
        		businessObject, fieldConversions, lookupImpl, returnKeys));
    }

    /**
     * @see org.kuali.core.lookup.LookupableHelperService#getReturnUrl(org.kuali.core.bo.BusinessObject, java.util.Map, java.lang.String)
     */
    public HtmlData getReturnUrl(BusinessObject businessObject, LookupForm lookupForm, List returnKeys) {
    	Properties parameters = getParameters(
        		businessObject, lookupForm.getFieldConversions(), lookupForm.getLookupableImplServiceName(), returnKeys);
    	if(StringUtils.isEmpty(lookupForm.getHtmlDataType()) || HtmlData.ANCHOR_HTML_DATA_TYPE.equals(lookupForm.getHtmlDataType()))
    		return getReturnAnchorHtmlData(businessObject, parameters, lookupForm, returnKeys);
    	else 
    		return getReturnInputHtmlData(businessObject, parameters, lookupForm, returnKeys);
    }

    protected HtmlData getReturnInputHtmlData(BusinessObject businessObject, Properties parameters, LookupForm lookupForm, List returnKeys){
    	String returnUrlAnchorLabel = 
        	KNSServiceLocator.getKualiConfigurationService().getPropertyString(TITLE_RETURN_URL_PREPENDTEXT_PROPERTY);
    	String name = KNSConstants.MULTIPLE_VALUE_LOOKUP_SELECTED_OBJ_ID_PARAM_PREFIX+lookupForm.getLookupObjectId();
        InputHtmlData input = new InputHtmlData(name, InputHtmlData.CHECKBOX_INPUT_TYPE);
        input.setTitle(HtmlData.getTitleText(returnUrlAnchorLabel, businessObject, returnKeys));
    	if(((MultipleValueLookupForm)lookupForm).getCompositeObjectIdMap()==null || 
    			((MultipleValueLookupForm)lookupForm).getCompositeObjectIdMap().get(
    			((PersistableBusinessObject)businessObject).getObjectId())==null){
    		input.setChecked("");
    	} else{
    		input.setChecked(InputHtmlData.CHECKBOX_CHECKED_VALUE);
    	}
    	input.setValue(InputHtmlData.CHECKBOX_CHECKED_VALUE);
        return input;
    }
    
    protected HtmlData getReturnAnchorHtmlData(BusinessObject businessObject, Properties parameters, LookupForm lookupForm, List returnKeys){
    	String returnUrlAnchorLabel = 
        	KNSServiceLocator.getKualiConfigurationService().getPropertyString(TITLE_RETURN_URL_PREPENDTEXT_PROPERTY);
        AnchorHtmlData anchor = new AnchorHtmlData(
        		getReturnHref(parameters, lookupForm, returnKeys), 
        		HtmlData.getTitleText(returnUrlAnchorLabel, businessObject, returnKeys));
        anchor.setDisplayText(returnUrlAnchorLabel);
        return anchor;
    }

    protected String getReturnHref(Properties parameters, LookupForm lookupForm, List returnKeys) {
        String href = UrlFactory.parameterizeUrl(backLocation, parameters);
    	return addToReturnHref(href, lookupForm);
    }

    protected String addToReturnHref(String href, LookupForm lookupForm){
    	String lookupAnchor = "";
        if (StringUtils.isNotEmpty(lookupForm.getAnchor())) {
        	lookupAnchor = lookupForm.getAnchor();
        }
    	href += "&anchor="+lookupAnchor+"&docNum="+(StringUtils.isEmpty(getDocNum())?"":getDocNum());
    	return href;
    }
    
    protected Properties getParameters(BusinessObject bo, Map fieldConversions, String lookupImpl, List returnKeys) {
        Properties parameters = new Properties();
        parameters.put(KNSConstants.DISPATCH_REQUEST_PARAMETER, KNSConstants.RETURN_METHOD_TO_CALL);
        parameters.put(KNSConstants.DOC_FORM_KEY, getDocFormKey());
        parameters.put(KNSConstants.REFRESH_CALLER, lookupImpl);
        if(getDocNum() != null){
        	parameters.put(KNSConstants.DOC_NUM, getDocNum());
        }

        if (getReferencesToRefresh() != null) {
            parameters.put(KNSConstants.REFERENCES_TO_REFRESH, getReferencesToRefresh());
        }

        Iterator returnKeysIt = getReturnKeys().iterator();
        while (returnKeysIt.hasNext()) {
            String fieldNm = (String) returnKeysIt.next();

            Object fieldVal = ObjectUtils.getPropertyValue(bo, fieldNm);
            if (fieldVal == null) {
                fieldVal = KNSConstants.EMPTY_STRING;
            }

            // Encrypt value if it is a secure field
            String displayWorkgroup = getDataDictionaryService().getAttributeDisplayWorkgroup(bo.getClass(), fieldNm);

            if (fieldConversions.containsKey(fieldNm)) {
                fieldNm = (String) fieldConversions.get(fieldNm);
            }

            if (StringUtils.isNotBlank(displayWorkgroup) && !KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(GlobalVariables.getUserSession().getPerson().getPrincipalId(), org.kuali.rice.kim.util.KimConstants.TEMP_GROUP_NAMESPACE, displayWorkgroup )) {
                try {
                    fieldVal = getEncryptionService().encrypt(fieldVal);
                }
                catch (GeneralSecurityException e) {
                    LOG.error("Exception while trying to encrypted value for inquiry framework.", e);
                    throw new RuntimeException(e);
                }

            }

            //need to format date in url
            if (fieldVal instanceof Date) {
            	DateFormatter dateFormatter = new DateFormatter();
            	fieldVal = dateFormatter.format(fieldVal);
            }

            parameters.put(fieldNm, fieldVal.toString());
        }

        return parameters;
    }

    /**
     * @return a List of the names of fields which are marked in data dictionary as return fields.
     */
    public List getReturnKeys() {
        List returnKeys;
        if (fieldConversions != null && !fieldConversions.isEmpty()) {
            returnKeys = new ArrayList(fieldConversions.keySet());
        }
        else {
            returnKeys = getBusinessObjectMetaDataService().listPrimaryKeyFieldNames(getBusinessObjectClass());
        }

        return returnKeys;
    }

    /**
     * Gets the docFormKey attribute.
     * @return Returns the docFormKey.
     */
    public String getDocFormKey() {
        return docFormKey;
    }

    /**
     * Sets the docFormKey attribute value.
     * @param docFormKey The docFormKey to set.
     */
    public void setDocFormKey(String docFormKey) {
        this.docFormKey = docFormKey;
    }

    /**
     * @see org.kuali.core.lookup.LookupableHelperService#setFieldConversions(java.util.Map)
     */
    public void setFieldConversions(Map fieldConversions) {
        this.fieldConversions = fieldConversions;
    }

    /**
     * Gets the lookupService attribute.
     * @return Returns the lookupService.
     */
    protected LookupService getLookupService() {
        return lookupService != null ? lookupService : KNSServiceLocator.getLookupService();
    }

    /**
     * Sets the lookupService attribute value.
     * @param lookupService The lookupService to set.
     */
    public void setLookupService(LookupService lookupService) {
        this.lookupService = lookupService;
    }

    /**
     * Uses the DD to determine which is the default sort order.
     *
     * @return property names that will be used to sort on by default
     */
    public List getDefaultSortColumns() {
        return getBusinessObjectDictionaryService().getLookupDefaultSortFieldNames(getBusinessObjectClass());
    }

    /**
     * Checks that any required search fields have value.
     *
     * @see org.kuali.core.lookup.LookupableHelperService#validateSearchParameters(java.util.Map)
     */
    public void validateSearchParameters(Map fieldValues) {
        List<String> lookupFieldAttributeList = null;
        if(getBusinessObjectMetaDataService().isLookupable(getBusinessObjectClass())) {
            lookupFieldAttributeList = getBusinessObjectMetaDataService().getLookupableFieldNames(getBusinessObjectClass());
        }
        if (lookupFieldAttributeList == null) {
            throw new RuntimeException("Lookup not defined for business object " + getBusinessObjectClass());
        }
        for (Iterator iter = lookupFieldAttributeList.iterator(); iter.hasNext();) {
            String attributeName = (String) iter.next();
            if (fieldValues.containsKey(attributeName)) {
                // get label of attribute for message
                String attributeLabel = getDataDictionaryService().getAttributeLabel(getBusinessObjectClass(), attributeName);

                String attributeValue = (String) fieldValues.get(attributeName);

                boolean isSecureField = !StringUtils.isBlank(getDataDictionaryService().getAttributeDisplayWorkgroup(getBusinessObjectClass(), attributeName));

                // check for required if field does not have value
                if (StringUtils.isBlank(attributeValue)) {
                    if ((getBusinessObjectDictionaryService().getLookupAttributeRequired(getBusinessObjectClass(), attributeName)).booleanValue()) {
                        GlobalVariables.getErrorMap().putError(attributeName, RiceKeyConstants.ERROR_REQUIRED, attributeLabel);
                    }
                }
                else if (isSecureField) {
                    // following loop would be trivial if Constants.QUERY_CHARACTERS would implement CharSequence but not so
                    // sure if that makes sense...
                    for (int i = 0; i < KNSConstants.QUERY_CHARACTERS.length; i++) {
                        String queryCharacter = KNSConstants.QUERY_CHARACTERS[i];

                        if (attributeValue.contains(queryCharacter)) {
                            GlobalVariables.getErrorMap().putError(attributeName, RiceKeyConstants.ERROR_SECURE_FIELD, attributeLabel);
                        }
                    }
                }
            }
        }

        if (!GlobalVariables.getErrorMap().isEmpty()) {
            throw new ValidationException("errors in search criteria");
        }
    }

    protected org.kuali.rice.kim.service.PersonService getPersonService() {
        return personService != null ? personService : org.kuali.rice.kim.service.KIMServiceLocator.getPersonService();
    }

    public void setPersonService(org.kuali.rice.kim.service.PersonService personService) {
        this.personService = personService;
    }

    /**
     * Constructs the list of rows for the search fields. All properties for the field objects come from the DataDictionary.
     * To be called by setBusinessObject
     */
    protected void setRows() {
        List localRows = new ArrayList();
        List<String> lookupFieldAttributeList = null;
        if(getBusinessObjectMetaDataService().isLookupable(getBusinessObjectClass())) {
            lookupFieldAttributeList = getBusinessObjectMetaDataService().getLookupableFieldNames(getBusinessObjectClass());
        }
        if (lookupFieldAttributeList == null) {
            throw new RuntimeException("Lookup not defined for business object " + getBusinessObjectClass());
        }

        // construct field object for each search attribute
        List fields = new ArrayList();
        try {
            fields = FieldUtils.createAndPopulateFieldsForLookup(lookupFieldAttributeList, getReadOnlyFieldsList(), getBusinessObjectClass());

        }
        catch (InstantiationException e) {
            throw new RuntimeException("Unable to create instance of business object class" + e.getMessage());
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to create instance of business object class" + e.getMessage());
        }
        this.rows = FieldUtils.wrapFields(fields);
    }

    public List<Row> getRows() {
        return rows;
    }

    public abstract List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues);


    /**
     * This implementation of this method throws an UnsupportedOperationException, since not every implementation
     * may actually want to use this operation.  Subclasses desiring other behaviors
     * will need to override this.
     *
     * @see org.kuali.core.lookup.LookupableHelperService#getSearchResultsUnbounded(java.util.Map)
     */
    public List<? extends BusinessObject> getSearchResultsUnbounded(Map<String, String> fieldValues) {
        throw new UnsupportedOperationException("Lookupable helper services do not always support getSearchResultsUnbounded");
    }

    /**
     *
     * This method performs the lookup and returns a collection of lookup items
     * @param lookupForm
     * @param kualiLookupable
     * @param resultTable
     * @param bounded
     * @return
     */
    public Collection performLookup(LookupForm lookupForm, Collection resultTable, boolean bounded) {
        setBackLocation((String) lookupForm.getFieldsForLookup().get(KNSConstants.BACK_LOCATION));
        setDocFormKey((String) lookupForm.getFieldsForLookup().get(KNSConstants.DOC_FORM_KEY));
        Collection displayList;

        // call search method to get results
        if (bounded) {
            displayList = getSearchResults(lookupForm.getFieldsForLookup());
        }
        else {
            displayList = getSearchResultsUnbounded(lookupForm.getFieldsForLookup());
        }

        HashMap<String,Class> propertyTypes = new HashMap<String, Class>();

        boolean hasReturnableRow = false;

        List returnKeys = getReturnKeys();
        List pkNames = getBusinessObjectMetaDataService().listPrimaryKeyFieldNames(getBusinessObjectClass());
        // iterate through result list and wrap rows with return url and action urls
        for (Iterator iter = displayList.iterator(); iter.hasNext();) {
            BusinessObject element = (BusinessObject) iter.next();
        	if(element instanceof PersistableBusinessObject){
                lookupForm.setLookupObjectId(((PersistableBusinessObject)element).getObjectId());
            }
            HtmlData returnUrl = getReturnUrl(element, lookupForm, returnKeys);

            String actionUrls = getActionUrls(element, pkNames);

            List<Column> columns = getColumns();
            for (Iterator iterator = columns.iterator(); iterator.hasNext();) {

                Column col = (Column) iterator.next();
                Formatter formatter = col.getFormatter();

                // pick off result column from result list, do formatting
                String propValue = KNSConstants.EMPTY_STRING;
                Object prop = ObjectUtils.getPropertyValue(element, col.getPropertyName());

                // set comparator and formatter based on property type
                Class propClass = propertyTypes.get(col.getPropertyName());
                if ( propClass == null ) {
                    try {
                    	propClass = ObjectUtils.getPropertyType( element, col.getPropertyName(), getPersistenceStructureService() );
                    	propertyTypes.put( col.getPropertyName(), propClass );
                    } catch (Exception e) {
                        throw new RuntimeException("Cannot access PropertyType for property " + "'" + col.getPropertyName() + "' " + " on an instance of '" + element.getClass().getName() + "'.", e);
                    }
                }

                // formatters
                if (prop != null) {
                    // for Booleans, always use BooleanFormatter
                    if (prop instanceof Boolean) {
                        formatter = new BooleanFormatter();
                    }

                    // for Dates, always use DateFormatter
                    if (prop instanceof Date) {
                        formatter = new DateFormatter();
                    }

                    // for collection, use the list formatter if a formatter hasn't been defined yet
                    if (prop instanceof Collection && formatter == null) {
                	formatter = new CollectionFormatter();
                    }

                    if (formatter != null) {
                        propValue = (String) formatter.format(prop);
                    }
                    else {
                        propValue = prop.toString();
                    }
                }

                // comparator
                col.setComparator(CellComparatorHelper.getAppropriateComparatorForPropertyClass(propClass));
                col.setValueComparator(CellComparatorHelper.getAppropriateValueComparatorForPropertyClass(propClass));

                // check security on field and do masking if necessary
                boolean viewAuthorized = getAuthorizationService().isAuthorizedToViewAttribute(GlobalVariables.getUserSession().getPerson(), element.getClass().getName(), col.getPropertyName());
                if (!viewAuthorized) {
                    Mask displayMask = getDataDictionaryService().getAttributeDisplayMask(element.getClass().getName(), col.getPropertyName());
                    propValue = displayMask.maskValue(propValue);
                }
                col.setPropertyValue(propValue);


                if (StringUtils.isNotBlank(propValue)) {
                    col.setColumnAnchor(getInquiryUrl(element, col.getPropertyName()));
                }
            }

            ResultRow row = new ResultRow(columns, returnUrl.constructCompleteHtmlTag(), actionUrls);

        	// because of concerns of the BO being cached in session on the ResultRow,
        	// let's only attach it when needed (currently in the case of export)
            if (getBusinessObjectDictionaryService().isExportable(getBusinessObjectClass())) {
            	row.setBusinessObject(element);
            }
            
            boolean rowReturnable = isResultReturnable(element);
            row.setRowReturnable(rowReturnable);
            if (rowReturnable) {
                hasReturnableRow = true;
            }
            resultTable.add(row);
        }

        lookupForm.setHasReturnableRow(hasReturnableRow);

        return displayList;
    }


    protected void setReferencesToRefresh(String referencesToRefresh) {
        this.referencesToRefresh = referencesToRefresh;
    }

    public String getReferencesToRefresh() {
        return referencesToRefresh;
    }

    protected SequenceAccessorService getSequenceAccessorService() {
        return sequenceAccessorService != null ? sequenceAccessorService : KNSServiceLocator.getSequenceAccessorService();
    }

    public void setSequenceAccessorService(SequenceAccessorService sequenceAccessorService) {
        this.sequenceAccessorService = sequenceAccessorService;
    }
    
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService != null ? businessObjectService : KNSServiceLocator.getBusinessObjectService();
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    protected LookupResultsService getLookupResultsService() {
        return lookupResultsService != null ? lookupResultsService : KNSServiceLocator.getLookupResultsService();
    }

    public void setLookupResultsService(LookupResultsService lookupResultsService) {
        this.lookupResultsService = lookupResultsService;
    }

    /**
     * @return false always, subclasses should override to do something smarter
     * @see org.kuali.core.lookup.LookupableHelperService#isSearchUsingOnlyPrimaryKeyValues()
     */
    public boolean isSearchUsingOnlyPrimaryKeyValues() {
        // by default, this implementation returns false, as lookups may not necessarily support this
        return false;
    }

    /**
     * Returns "N/A"
     *
     * @return "N/A"
     * @see org.kuali.core.lookup.LookupableHelperService#getPrimaryKeyFieldLabels()
     */
    public String getPrimaryKeyFieldLabels() {
        return KNSConstants.NOT_AVAILABLE_STRING;
    }

    /**
     * @see org.kuali.core.lookup.LookupableHelperService#isResultReturnable(org.kuali.core.bo.BusinessObject)
     */
    public boolean isResultReturnable(BusinessObject object) {
        return true;
    }
}
