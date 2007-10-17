/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package edu.iu.uis.eden.lookupable;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.displaytag.tags.TableTagParameters;
import org.displaytag.util.ParamEncoder;
import org.kuali.rice.resourceloader.GlobalResourceLoader;

import edu.iu.uis.eden.WorkflowServiceErrorException;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.export.ExportDataSet;
import edu.iu.uis.eden.export.ExportFormat;
import edu.iu.uis.eden.export.Exportable;
import edu.iu.uis.eden.plugin.attributes.WorkflowLookupable;
import edu.iu.uis.eden.util.Utilities;
import edu.iu.uis.eden.web.WorkflowAction;

/**
 * Struts Action for interacting with lookupables.
 *
 * @see WorkflowLookupable
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class LookupAction extends WorkflowAction {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LookupAction.class);

    /**
     * storeForm - When crossing quickFinders (moving from one lookupable to another), this method is called to store the data on the previous quickFinder in the UserSession object.
     *
     * @param mapping -
     *            action mapping.
     * @param form -
     *            LookupForm that is stored in user session and contains the values entered on the jsp form.
     * @param request -
     *            request.
     * @param response -
     *            response.
     * @return - where to go next.
     * @throws Exception
     */
    public ActionForward performLookup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LookupForm lookupForm = (LookupForm) form;
        WorkflowLookupable workflowLookupable = getLookupable(request.getParameter("lookupableImplServiceName"));//(WorkflowLookupable) SpringServiceLocator.getExtensionService().getLookupable(request.getParameter("lookupableImplServiceName"));

        Map fieldValues = new HashMap();
        for (Iterator iter = workflowLookupable.getRows().iterator(); iter.hasNext();) {
            Row row = (Row) iter.next();

            for (Iterator iterator = row.getFields().iterator(); iterator.hasNext();) {
                Field field = (Field) iterator.next();
                if (field.isHasLookupable() && request.getParameter("lookupableImplServiceName").equals(field.getQuickFinderClassNameImpl())) {
                    fieldValues.put(field.getPropertyName(), "");
                } else {
                    fieldValues.put(field.getPropertyName(), request.getParameter(field.getPropertyName()));
                }
            }
        }
        lookupForm.setFields(fieldValues);

        workflowLookupable.checkForAdditionalFields(fieldValues, request);
        StringBuffer url = new StringBuffer("Lookup.do?methodToCall=quickFinder&lookupableImplServiceName=");
        if (request.getParameter("quickFinderLookupable") != null && !request.getParameter("quickFinderLookupable").equals("")) {
            url.append(request.getParameter("quickFinderLookupable"));
        } else {
            url.append(request.getParameter("lookupableImplServiceName"));
        }
        url.append("&docFormKey=").append(getUserSession(request).addObject(lookupForm));
        url.append("&returnLocation=").append(workflowLookupable.getReturnLocation());

        if (Utilities.isEmpty(request.getParameter("customFieldConversions"))) {
            StringBuffer conversionFields = new StringBuffer();
            for (Iterator iter = workflowLookupable.getRows().iterator(); iter.hasNext();) {
                Row row = (Row) iter.next();

                for (Iterator iterator = row.getFields().iterator(); iterator.hasNext();) {
                    Field field = (Field) iterator.next();
                    if(!Utilities.isEmpty(field.getDefaultLookupableName())){
                        conversionFields.append(field.getDefaultLookupableName()).append(":").append(field.getPropertyName()).append(",");
                    }
                }
            }
            if(!Utilities.isEmpty(conversionFields.toString())){
                url.append("&conversionFields=").append(conversionFields.substring(0, conversionFields.lastIndexOf(",")));
            }
        }
        else {
            String conversionFields = request.getParameter("customFieldConversions");
            url.append("&conversionFields=").append(conversionFields);
        }

        return new ActionForward(url.toString(), true);
    }

    /**
     * quickFinder - sets the data on the lookupForm from the previous quickFinder to the next quickFinder.
     *
     * @param mapping -
     *            action mapping.
     * @param form -
     *            LookupForm for the next quickFinder.
     * @param request -
     *            request.
     * @param response -
     *            response.
     * @return - where to go next.
     * @throws Exception
     */
    public ActionForward quickFinder(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LookupForm lookupForm = (LookupForm) form;
        if (request.getParameter("lookupableImplServiceName") != null) {
            lookupForm.setLookupableImplServiceName(request.getParameter("lookupableImplServiceName"));
        }

        if (request.getParameter("docFormKey") != null) {
            lookupForm.setFormKey((String) request.getParameter("docFormKey"));
        }
        if (request.getParameter("returnLocation") != null) {
            lookupForm.setBackLocation(request.getParameter("returnLocation"));
        }
        if (request.getParameter("conversionFields") != null) {
            lookupForm.setConversionFields(request.getParameter("conversionFields"));
        }
        return mapping.findForward("basic");
    }

    /**
     * search - sets the values of the data entered on the form on the jsp into a map and then searches for the results.
     *
     * @param mapping -
     *            action mapping.
     * @param form -
     *            LookupForm
     * @param request -
     *            request.
     * @param response -
     *            response.
     * @return - where to go next.
     * @throws Exception
     */
    public ActionForward search(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LookupForm lookupForm = (LookupForm) form;
        boolean ascending = true;
        String sortOrderParameter = new ParamEncoder("result").encodeParameterName(TableTagParameters.PARAMETER_ORDER);
        String sortOrder = request.getParameter(sortOrderParameter);
        if (sortOrder == null) {
        	sortOrder = "1";
        }
        if (sortOrder.equals("2") ){
        	ascending = false;
        }
        String sortNameParameter = new ParamEncoder("result").encodeParameterName(TableTagParameters.PARAMETER_SORT);
        WorkflowLookupable workflowLookupable = getLookupable(request.getParameter("lookupableImplServiceName"));//(WorkflowLookupable) SpringServiceLocator.getExtensionService().getLookupable(request.getParameter("lookupableImplServiceName"));
        Map fieldValues = constructFieldValues(workflowLookupable, lookupForm, request);
        Column sortColumn = getSortColumn(workflowLookupable, request.getParameter(sortNameParameter));
        List displayList = workflowLookupable.getSearchResults(fieldValues, lookupForm.getFieldConversions());
        workflowLookupable.changeIdToName(fieldValues);
        sortDisplayList(sortColumn, displayList, ascending);
        request.setAttribute("workflowLookupable", workflowLookupable);
        request.setAttribute("reqSearchResults", displayList);
        if (request.getParameter("listKey") != null) {
            getUserSession(request).removeObject(request.getParameter("listKey"));
        }
        request.setAttribute("listKey", getUserSession(request).addObject(displayList));
        return mapping.findForward("basic");
    }

    private Column getSortColumn(WorkflowLookupable lookupable, String sortName) {
    	List columns = lookupable.getColumns();
    	if (StringUtils.isEmpty(sortName)) {
    		return (Column)columns.get(0);
    	}
    	for (Iterator iterator = columns.iterator(); iterator.hasNext();) {
			Column column = (Column) iterator.next();
			if (column.getSortName().equals(sortName)) {
				return column;
			}
		}
    	throw new WorkflowRuntimeException("Could not sort based on the given sort name of " + sortName);
    }

    private void sortDisplayList(Column sortColumn, List displayList, boolean ascending) {
    	Collections.sort(displayList, new ColumnComparator(sortColumn, ascending));
    }

    private Map constructFieldValues(WorkflowLookupable workflowLookupable, LookupForm lookupForm, HttpServletRequest request) throws Exception {
        Map fieldValues = new HashMap();
        for (Iterator iter = workflowLookupable.getRows().iterator(); iter.hasNext();) {
            Row row = (Row) iter.next();
            for (Iterator iterator = row.getFields().iterator(); iterator.hasNext();) {
                Field field = (Field) iterator.next();
                if (field.getFieldType() != field.getQUICKFINDER()) {
                    fieldValues.put(field.getPropertyName(), request.getParameter(field.getPropertyName()));
                }
                if (request.getParameter(field.getPropertyName()) != null) {
                    field.setPropertyValue(request.getParameter(field.getPropertyName()));
                }
            }
        }
        if (workflowLookupable.checkForAdditionalFields(fieldValues, request)) {
            for (Iterator iter = workflowLookupable.getRows().iterator(); iter.hasNext();) {
                Row row = (Row) iter.next();
                for (Iterator iterator = row.getFields().iterator(); iterator.hasNext();) {
                    Field field = (Field) iterator.next();
                    if (field.getFieldType() != field.getQUICKFINDER()) {
                        fieldValues.put(field.getPropertyName(), request.getParameter(field.getPropertyName()));
                    }
                    if (request.getParameter(field.getPropertyName()) != null) {
                        field.setPropertyValue(request.getParameter(field.getPropertyName()));
                    }
                }
            }
        }
        fieldValues.put("docFormKey", lookupForm.getFormKey());
        fieldValues.put("backLocation", lookupForm.getBackLocation());
        return fieldValues;
    }

    public ActionForward export(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String listKey = (String)request.getParameter("listKey");
        List results = (List)getUserSession(request).retrieveObject(listKey);
        WorkflowLookupable workflowLookupable = getLookupable(request.getParameter("lookupableImplServiceName"));//(WorkflowLookupable) SpringServiceLocator.getExtensionService().getLookupable(request.getParameter("lookupableImplServiceName"));
        if (!(workflowLookupable instanceof Exportable)) {
            throw new WorkflowServiceErrorException("This lookup does not support export.");
        }
        Exportable exportable = (Exportable)workflowLookupable;
        // for now just hardcode for XML
        ExportDataSet dataSet = exportable.export(ExportFormat.XML, results);
        return exportDataSet(request, dataSet);
    }

    /**
     * refresh - is called when one quickFinder returns to the previous one. Sets all the values and performs the new search.
     *
     * @param mapping -
     *            action mapping.
     * @param form -
     *            LookupForm
     * @param request -
     *            request.
     * @param response -
     *            response.
     * @return - where to go next.
     * @throws Exception
     */
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LookupForm lookupForm = (LookupForm) form;
        WorkflowLookupable workflowLookupable = getLookupable(lookupForm.getLookupableImplServiceName());//(WorkflowLookupable) SpringServiceLocator.getExtensionService().getLookupable(lookupForm.getLookupableImplServiceName());

        Map fieldValues = new HashMap();
        Map values = lookupForm.getFields();

//        StringBuffer url = new StringBuffer("Lookup.do?methodToCall=viewResults&lookupableImplServiceName=");
//        url.append(lookupForm.getLookupableImplServiceName()).append("&backLocation=").append(lookupForm.getBackLocation());
//        url.append("&docFormKey=").append(request.getParameter("docFormKey"));
//        url.append("&formKey=").append(lookupForm.getFormKey()).append("&conversionFields=").append(lookupForm.getConversionFields());

        for (Iterator iter = workflowLookupable.getRows().iterator(); iter.hasNext();) {
            Row row = (Row) iter.next();

            for (Iterator iterator = row.getFields().iterator(); iterator.hasNext();) {
                Field field = (Field) iterator.next();

                if (field.getPropertyName() != null && !field.getPropertyName().equals("")) {
                    if (request.getParameter(field.getPropertyName()) != null) {
                        field.setPropertyValue(request.getParameter(field.getPropertyName()));
                    } else {
                        field.setPropertyValue((String) values.get(field.getPropertyName()));
                    }
                }
                fieldValues.put(field.getPropertyName(), field.getPropertyValue());
            }
        }
        fieldValues.put("docFormKey", lookupForm.getFormKey());
        fieldValues.put("backLocation", lookupForm.getBackLocation());

        if (workflowLookupable.checkForAdditionalFields(fieldValues, request)) {
            for (Iterator iter = workflowLookupable.getRows().iterator(); iter.hasNext();) {
                Row row = (Row) iter.next();
                for (Iterator iterator = row.getFields().iterator(); iterator.hasNext();) {
                    Field field = (Field) iterator.next();
                    if (field.getPropertyName() != null && !field.getPropertyName().equals("")) {
                        if (request.getParameter(field.getPropertyName()) != null) {
                            field.setPropertyValue(request.getParameter(field.getPropertyName()));
                            fieldValues.put(field.getPropertyName(), request.getParameter(field.getPropertyName()));
                        } else {
                            if (values.get(field.getPropertyName()) != null) {
                                field.setPropertyValue((String) values.get(field.getPropertyName()));
                            }
                        }

                    }
                }
            }
        }
//        Collection displayList = workflowLookupable.getSearchResults(fieldValues, lookupForm.getFieldConversions());
        workflowLookupable.changeIdToName(fieldValues);
//        for (Iterator iter = workflowLookupable.getRows().iterator(); iter.hasNext();) {
//            Row row = (Row) iter.next();
//
//            //for (Iterator iterator = row.getFields().iterator(); iterator.hasNext();) {
//            //    Field field = (Field) iterator.next();
////                url.append("&").append(field.getPropertyName()).append("=").append(field.getPropertyValue());
//            //}
//        }
        request.setAttribute("workflowLookupable", workflowLookupable);
//        request.setAttribute("reqSearchResults", displayList);
//        if (request.getParameter("listKey") != null) {
//            getUserSession(request).removeObject(request.getParameter("listKey"));
//        }
//        String listKey = getUserSession(request).addObject(displayList);
//        url.append("&listKey=").append(listKey);
//        request.setAttribute("listKey", listKey);
        return mapping.findForward("basic");
//        return new ActionForward(url.toString(), true);
    }

    /*
     * (non-Javadoc)
     *
     * @see edu.iu.uis.eden.web.WorkflowAction#establishRequiredState(javax.servlet.http.HttpServletRequest)
     */
    public ActionMessages establishRequiredState(HttpServletRequest request, ActionForm form) throws Exception {
        LookupForm lookupForm = (LookupForm) form;
        String lookupableName = request.getParameter("lookupableImplServiceName");
        if (lookupableName != null) {
            try {

                WorkflowLookupable workflowLookupable = getLookupable(lookupableName);//SpringServiceLocator.getExtensionService().getLookupable(lookupableName);
                if (workflowLookupable instanceof Exportable) {
                    lookupForm.setSupportedExportFormats(((Exportable)workflowLookupable).getSupportedExportFormats());
                }
                Map fieldValues = new HashMap();
                for (Iterator iter = workflowLookupable.getRows().iterator(); iter.hasNext();) {
                    Row row = (Row) iter.next();

                    for (Iterator iterator = row.getFields().iterator(); iterator.hasNext();) {
                        Field field = (Field) iterator.next();
                        if (request.getParameter(field.getPropertyName()) != null) {
                            field.setPropertyValue(request.getParameter(field.getPropertyName()));
                        }
                        fieldValues.put(field.getPropertyName(), field.getPropertyValue());
                    }
                }
                if (workflowLookupable.checkForAdditionalFields(fieldValues, request)) {
                    for (Iterator iter = workflowLookupable.getRows().iterator(); iter.hasNext();) {
                        Row row = (Row) iter.next();

                        for (Iterator iterator = row.getFields().iterator(); iterator.hasNext();) {
                            Field field = (Field) iterator.next();
                            if (request.getParameter(field.getPropertyName()) != null) {
                                field.setPropertyValue(request.getParameter(field.getPropertyName()));
                            }
                        }
                    }
                }
                Map fieldConversions = new HashMap();
                if (!Utilities.isEmpty(lookupForm.getConversionFields())) {
                    if (lookupForm.getConversionFields().indexOf(",") > 0) {
                        StringTokenizer token = new StringTokenizer(lookupForm.getConversionFields(), ",");
                        while (token.hasMoreTokens()) {
                            String element = token.nextToken();
                            fieldConversions.put(element.substring(0, element.indexOf(":")), element.substring(element.indexOf(":") + 1));
                        }
                    } else {
                        fieldConversions.put(lookupForm.getConversionFields().substring(0, lookupForm.getConversionFields().indexOf(":")), lookupForm.getConversionFields().substring(lookupForm.getConversionFields().indexOf(":") + 1));
                    }
                }
                lookupForm.setNoReturnParams(workflowLookupable.getNoReturnParams(fieldConversions));
                lookupForm.setFieldConversions(fieldConversions);

                request.setAttribute("workflowLookupable", workflowLookupable);
            } catch (Exception e) {
                LOG.error("error in establishRequiredState", e);
            }
        }
        return null;
    }

    /**
     * clearValues - clears the values of all the fields on the jsp.
     *
     * @param mapping -
     *            action mapping.
     * @param form -
     *            LookupForm
     * @param request -
     *            request.
     * @param response -
     *            response.
     * @return - where to go next.
     * @throws Exception
     */
    public ActionForward clearValues(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        LookupForm lookupForm = (LookupForm) form;
        try {
            WorkflowLookupable workflowLookupable = getLookupable(lookupForm.getLookupableImplServiceName());//(WorkflowLookupable) SpringServiceLocator.getExtensionService().getLookupable(lookupForm.getLookupableImplServiceName());
            for (Iterator iter = workflowLookupable.getRows().iterator(); iter.hasNext();) {
                Row row = (Row) iter.next();
                for (Iterator iterator = row.getFields().iterator(); iterator.hasNext();) {
                    Field field = (Field) iterator.next();
                    if (!field.getFieldType().equals(Field.RADIO)) {
                        field.setPropertyValue("");
                    }
                }
            }
            request.setAttribute("workflowLookupable", workflowLookupable);
        } catch (Exception e) {
            LOG.error("error clearing values", e);
        }
        return mapping.findForward("basic");
    }

    /**
     * viewResults - gets the results out from the search out of the session for sorting and paging on the jsp.
     *
     * @param mapping -
     *            action mapping.
     * @param form -
     *            LookupForm
     * @param request -
     *            request.
     * @param response -
     *            response.
     * @return - where to go next.
     * @throws Exception
     */
    public ActionForward viewResults(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.setAttribute("listKey", request.getParameter("listKey"));
        request.setAttribute("reqSearchResults", getUserSession(request).retrieveObject(request.getParameter("listKey")));
        return mapping.findForward("basic");
    }

    /*
     * (non-Javadoc)
     *
     * @see edu.iu.uis.eden.web.WorkflowAction#start(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LookupForm lookupForm = (LookupForm) form;
        if (request.getParameter("lookupableImplServiceName") != null) {
            lookupForm.setLookupableImplServiceName(request.getParameter("lookupableImplServiceName"));
        }

        if (request.getAttribute("docFormKey") != null) {
            lookupForm.setFormKey((String) request.getAttribute("docFormKey"));
        } else if (request.getParameter("docFormKey") != null) {
            lookupForm.setFormKey((String) request.getParameter("docFormKey"));
        }

        if (request.getParameter("returnLocation") != null) {
            lookupForm.setBackLocation(request.getParameter("returnLocation"));
        }
        if (request.getParameter("conversionFields") != null) {
            lookupForm.setConversionFields(request.getParameter("conversionFields"));
        }

        return mapping.findForward("basic");
    }

    private static WorkflowLookupable getLookupable(String serviceName) {
    	return (WorkflowLookupable)GlobalResourceLoader.getService(serviceName);
    }

    private class ColumnComparator implements Comparator<Object> {

    	private Column column;
    	private boolean ascending;

    	public ColumnComparator(Column column, boolean ascending) {
    		this.column = column;
    		this.ascending = ascending;
    	}

		public int compare(Object row1, Object row2) {
			try {
				Object property1 = BeanUtils.getProperty(row1, column.getSortName());
				Object property2 = BeanUtils.getProperty(row2, column.getSortName());
				String property1Value = (property1 == null ? "" : property1.toString());
				String property2Value = (property2 == null ? "" : property2.toString());
				int compare = 0;
				if (Column.INTEGER.equals(column.getType())) {
					Integer i1 = (StringUtils.isEmpty(property1Value) ? new Integer(0) : Integer.valueOf(property1Value));
					Integer i2 = (StringUtils.isEmpty(property2Value) ? new Integer(0) : Integer.valueOf(property2Value));
					compare = i1.compareTo(i2);
				} else {
					compare = property1Value.compareTo(property2Value);
				}
				if (!ascending) {
					compare *= -1;
				}
				return compare;
			} catch (Exception e) {
				throw new WorkflowRuntimeException(e);
			}
		}


    }
}