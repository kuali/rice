package edu.iu.uis.eden.messaging.web;


import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.actions.DispatchAction;

/**
 * An abstract super class for all Struts Actions in KEW.  Adds some custom
 * dispatch behavior by extending the Struts DispatchAction.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public abstract class KSBAction extends DispatchAction {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KSBAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			ActionMessages messages = null;
			messages = establishRequiredState(request, form);
			if (messages != null && !messages.isEmpty()) {
				// XXX: HACK: FIXME:
				// obviously this implies that we can't return both ActionErrors
				// and ActionMessages... :(
				// probably establishRequiredState should be refactored to have
				// a generic 'should-we-continue'
				// boolean return, so that control flow can be more explicitly
				// specified by the subclass
				if (messages instanceof ActionErrors) {
					saveErrors(request, (ActionErrors) messages);
				} else {
					saveMessages(request, messages);
				}
				return mapping.findForward("requiredStateError");
			}
			LOG.info(request.getQueryString());
			ActionForward returnForward = null;

			if (request.getParameterMap() != null) {
				for (Iterator iter = request.getParameterMap().entrySet().iterator(); iter.hasNext();) {
					String parameterName = (String) ((Map.Entry) iter.next()).getKey();
					if (parameterName.startsWith("methodToCall.") && parameterName.endsWith(".x")) {
						String methodToCall = parameterName.substring(parameterName.indexOf("methodToCall.") + 13, parameterName.lastIndexOf(".x"));
						if (methodToCall != null && methodToCall.length() > 0) {
							returnForward = this.dispatchMethod(mapping, form, request, response, methodToCall);
						}
					}
				}
			}
			if (returnForward == null) {
				if (request.getParameter("methodToCall") != null && !"".equals(request.getParameter("methodToCall")) && !"execute".equals(request.getParameter("methodToCall"))) {
					LOG.info("dispatch to methodToCall " + request.getParameter("methodToCall") + " called");
					returnForward = super.execute(mapping, form, request, response);
				} else {
					LOG.info("dispatch to default start methodToCall");
					returnForward = start(mapping, form, request, response);
				}
			}

			messages = establishFinalState(request, form);
			if (messages != null && !messages.isEmpty()) {
				saveMessages(request, messages);
				return mapping.findForward("finalStateError");
			}
			return returnForward;
		} catch (Exception e) {
			LOG.error("Error processing action " + mapping.getPath(), e);
			throw new RuntimeException(e);
		}
	}

	public abstract ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception;

	public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		return start(mapping, form, request, response);
	}

	public abstract ActionMessages establishRequiredState(HttpServletRequest request, ActionForm form) throws Exception;

	public ActionMessages establishFinalState(HttpServletRequest request, ActionForm form) throws Exception {
		return null;
	}

	public ActionForward noOp(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		return mapping.findForward("basic");
	}

}