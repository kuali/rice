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
package edu.iu.uis.eden.messaging.web;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.comparators.ComparableComparator;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.kuali.bus.services.KSBServiceLocator;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.messaging.AsynchronousCall;
import edu.iu.uis.eden.messaging.MessageQueueService;
import edu.iu.uis.eden.messaging.PersistedMessage;
import edu.iu.uis.eden.web.WorkflowAction;


/**
 * Struts action for interacting with the queue of messages.
 *
 * @author rkirkend
 */
public class RouteQueueAction extends WorkflowAction {

	public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		return mapping.findForward("report");
	}

    public ActionForward saveAndRequeue(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RouteQueueForm routeQueueForm = (RouteQueueForm) form;
        Long routeQueueId = routeQueueForm.getRouteQueueFromForm().getRouteQueueId();
        if ((routeQueueId == null) || (routeQueueId.longValue() <= 0)) {
            throw new IllegalArgumentException("Invalid routeQueueId passed in.  SaveAndRequeue command cannot be processed.");
        }
        
        //  save (and implicitly re-queue) the message
        PersistedMessage message = routeQueueForm.getRouteQueueFromForm();
        KEWServiceLocator.getRouteQueueService().save(message);
        
        ActionMessages messages = new ActionMessages();
        messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("routequeue.RouteQueueService.queued"));
        saveMessages(request, messages);
        
        routeQueueForm.setRouteQueueId(routeQueueId);
        routeQueueForm.setRouteQueueFromDatabase(null);
        routeQueueForm.setRouteQueueFromForm(null);
        routeQueueForm.setShowEdit("yes");
        routeQueueForm.setMethodToCall("");
        establishRequiredState(request, form);
        routeQueueForm.setRouteQueueFromForm(routeQueueForm.getRouteQueueFromDatabase());
        routeQueueForm.setNewQueueDate(routeQueueForm.getExistingQueueDate());
        routeQueueForm.getRouteQueueFromForm().setMethodCall(unwrapPayload(routeQueueForm.getRouteQueueFromForm()));
        return mapping.findForward("basic");
    }
    
    /**
     * Performs a quick ReQueue of the indicated persisted message.  
     * 
     * The net effect of this requeue is to set the Status to QUEUED, 
     * set the Date to now, and to reset the RetryCount to zero.  The payload 
     * is not modified.
     * 
     * @param message The populated message to be requeued.
     */
    protected void quickRequeueMessage(PersistedMessage message) {
        message.setQueueStatus(EdenConstants.ROUTE_QUEUE_QUEUED);
        message.setQueueDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
        message.setRetryCount(new Integer(0));
        getRouteQueueService().save(message);
    }
    
	public ActionForward quickRequeueMessage(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RouteQueueForm routeQueueForm = (RouteQueueForm) form;
        if (StringUtils.isBlank(request.getParameter("routeQueueId"))) {
            throw new IllegalArgumentException("No routeQueueId passed in with the Request.");
        }
        
        PersistedMessage message = routeQueueForm.getRouteQueueFromDatabase();
        quickRequeueMessage(message);
        
        ActionMessages messages = new ActionMessages();
		messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("routequeue.RouteQueueService.requeued"));
		saveMessages(request, messages);
        
        routeQueueForm.setRouteQueueFromDatabase(null);
        routeQueueForm.setRouteQueueFromForm(null);
        routeQueueForm.setRouteQueueId(null);
        routeQueueForm.setMethodToCall("");
        
        //  re-run the state method to load the full set of rows
        establishRequiredState(request, form);
		return mapping.findForward("report");
	}

	public ActionForward edit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		RouteQueueForm routeQueueForm = (RouteQueueForm) form;
		routeQueueForm.setShowEdit("yes");
		routeQueueForm.setRouteQueueFromForm(routeQueueForm.getRouteQueueFromDatabase());
		routeQueueForm.setNewQueueDate(routeQueueForm.getExistingQueueDate());
        routeQueueForm.getRouteQueueFromForm().setMethodCall(unwrapPayload(routeQueueForm.getRouteQueueFromForm()));
		return mapping.findForward("basic");
	}

    public ActionForward viewPayload(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RouteQueueForm routeQueueForm = (RouteQueueForm) form;
        routeQueueForm.setShowEdit("no");
        routeQueueForm.setRouteQueueFromForm(routeQueueForm.getRouteQueueFromDatabase());
        routeQueueForm.setNewQueueDate(routeQueueForm.getExistingQueueDate());
        AsynchronousCall messagePayload = unwrapPayload(routeQueueForm.getRouteQueueFromDatabase());
        routeQueueForm.getRouteQueueFromForm().setMethodCall(messagePayload);
        return mapping.findForward("payload");
    }
    
	public ActionForward reset(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		RouteQueueForm routeQueueForm = (RouteQueueForm) form;
		if (routeQueueForm.getShowEdit().equals("yes")) {
			routeQueueForm.setRouteQueueFromForm(routeQueueForm.getRouteQueueFromDatabase());
		}
		return mapping.findForward("basic");
	}

	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		RouteQueueForm routeQueueForm = (RouteQueueForm) form;
		routeQueueForm.getRouteQueueFromForm().setQueuePriority(null);
		routeQueueForm.getRouteQueueFromForm().setQueueStatus(null);
		routeQueueForm.getRouteQueueFromForm().setQueueDate(null);
        routeQueueForm.getRouteQueueFromForm().setExpirationDate(null);
		routeQueueForm.getRouteQueueFromForm().setRetryCount(null);
		routeQueueForm.getRouteQueueFromForm().setIpNumber(null);
		routeQueueForm.getRouteQueueFromForm().setServiceName(null);
        routeQueueForm.getRouteQueueFromForm().setMessageEntity(null);
        routeQueueForm.getRouteQueueFromForm().setMethodName(null);
		routeQueueForm.getRouteQueueFromForm().setPayload(null);
        routeQueueForm.getRouteQueueFromForm().setMethodCall(null);
		routeQueueForm.setExistingQueueDate(null);
		routeQueueForm.setNewQueueDate(null);
		return mapping.findForward("basic");
	}

	public ActionForward delete(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		RouteQueueForm routeQueueForm = (RouteQueueForm) form;
		routeQueueForm.setRouteQueueFromForm(routeQueueForm.getRouteQueueFromDatabase());
		routeQueueForm.setRouteQueueFromDatabase(null);
		getRouteQueueService().delete(routeQueueForm.getRouteQueueFromForm());
		ActionMessages messages = new ActionMessages();
		messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("routequeue.RouteQueueService.deleted", routeQueueForm.getRouteQueueFromForm().getRouteQueueId().toString()));
		saveMessages(request, messages);
        establishRequiredState(request, form);
		return mapping.findForward("report");
	}

    /**
     * Sets up the expected state by retrieving the selected RouteQueue by RouteQueueId, and placing it in 
     * the ExistingRouteQueue member.
     * 
     * Called by the super's Execute method on every request.  
     */
	public ActionMessages establishRequiredState(HttpServletRequest request, ActionForm form) throws Exception {
		RouteQueueForm routeQueueForm = (RouteQueueForm) form;
		if (routeQueueForm.getRouteQueueId() != null) {
			PersistedMessage rq = getRouteQueueService().findByRouteQueueId(routeQueueForm.getRouteQueueId());
			if (rq != null) {
				// routeQueueForm.setExistingQueueDate(EdenConstants.getDefaultDateFormat().format(rq.getQueueDate()));
				routeQueueForm.setExistingQueueDate(EdenConstants.getDefaultDateFormat().format(new Date()));
				routeQueueForm.setRouteQueueFromDatabase(rq);
			} else {
				ActionMessages messages = new ActionMessages();
				messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("routequeue.RouteQueueService.queuedDocumentNotFound", routeQueueForm.getRouteQueueId().toString()));
				return messages;
			}
			routeQueueForm.setRouteQueueId(null);
		} else if (!"clear".equalsIgnoreCase(request.getParameter("methodToCall")) && !"saveAndRequeueDocument".equalsIgnoreCase(request.getParameter("methodToCall"))) {
			List<PersistedMessage> queueEntries = findRouteQueues(request, routeQueueForm);
			if (queueEntries.size() > 0) {
				Collections.sort(queueEntries, new Comparator() {
					private Comparator comp = new ComparableComparator();

					public int compare(Object object1, Object object2) {
						if (object1 == null && object2 == null) {
							return 0;
						} else if (object1 == null) {
							return 1;
						} else if (object2 == null) {
							return -1;
						}
						Long id1 = ((PersistedMessage) object1).getRouteQueueId();
						Long id2 = ((PersistedMessage) object2).getRouteQueueId();

						try {
							return this.comp.compare(id1, id2);
						} catch (Exception e) {
							return 0;
						}
					}
				});
			}
			routeQueueForm.setRouteQueueRows(queueEntries);
		}
		return null;
	}

  protected List<PersistedMessage> findRouteQueues(HttpServletRequest request, RouteQueueForm routeQueueForm) {
    List<PersistedMessage> routeQueues = new ArrayList<PersistedMessage>();
    
    //  no filter applied
    if (StringUtils.isBlank(routeQueueForm.getFilterApplied())) {
      routeQueues.addAll( getRouteQueueService().findAll(routeQueueForm.getMaxRows()) );
    }
    
    //  one or more filters applied
    else {
      Map<String,String> criteriaValues = new HashMap<String,String>();
      String key = null;
      String value = null;
      String trimmedKey = null;
      for (Iterator iter = request.getParameterMap().keySet().iterator(); iter.hasNext();) {
        key = (String) iter.next();
        if (key.endsWith(EdenConstants.ROUTE_QUEUE_FILTER_SUFFIX)) {
          value = request.getParameter(key);
          if (StringUtils.isNotBlank(value)) {
            trimmedKey = key.substring(0, key.indexOf(EdenConstants.ROUTE_QUEUE_FILTER_SUFFIX));
            criteriaValues.put(trimmedKey, value);
          }
        }
      }
      routeQueues.addAll( getRouteQueueService().findByValues(criteriaValues));
    }
    return routeQueues;
  }
  
	private MessageQueueService getRouteQueueService() {
		return (MessageQueueService) KEWServiceLocator.getService(KEWServiceLocator.ROUTE_QUEUE_SRV);
	}

    /**
     * Extracts the payload from a PersistedMessage, attempts to convert it to the expected 
     * AsynchronousCall type, and returns it.  
     * 
     * Throws an IllegalArgumentException if the decoded payload isnt of the expected type.
     * 
     * @param message The populated PersistedMessage object to extract the payload from.
     * @return Returns the payload if one is present and it can be deserialized, otherwise returns null.
     */
    protected AsynchronousCall unwrapPayload(PersistedMessage message) {
        String encodedPayload = message.getPayload();
        if (StringUtils.isBlank(encodedPayload)) {
            return null;
        }
        Object decodedPayload = null;
        if (encodedPayload != null) {
            decodedPayload = KSBServiceLocator.getMessageHelper().deserializeObject(encodedPayload);
        }
        //  fail fast if its not the expected type of AsynchronousCall
        if ((decodedPayload != null) && !(decodedPayload instanceof AsynchronousCall)) {
            throw new IllegalArgumentException("PersistedMessage payload was not of the expected class. " + 
                    "Expected was [" + AsynchronousCall.class.getName() + "], actual was: [" + decodedPayload.getClass().getName() + "]");
        }
        return (AsynchronousCall) decodedPayload;
    }
    
}
