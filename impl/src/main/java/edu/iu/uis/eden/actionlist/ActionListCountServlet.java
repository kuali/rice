package edu.iu.uis.eden.actionlist;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.user.AuthenticationUserId;
import edu.iu.uis.eden.user.EmplId;
import edu.iu.uis.eden.user.UserId;
import edu.iu.uis.eden.user.UuId;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.user.WorkflowUserId;

public class ActionListCountServlet extends HttpServlet {

	private static final long serialVersionUID = 260649920715567145L;

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ActionListCountServlet.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/plain");
	    PrintWriter out = response.getWriter();
	    int count = getCount(request);
	    out.println(Integer.toString(count));
	    out.close();
	}

	private int getCount(HttpServletRequest request) {
		try {
			String id = request.getParameter("id");
			if (id == null || id.equals("")) {
				return 0;
			}
			String idType = request.getParameter("idType");
			if (idType == null || idType.equals("")) {
				idType = "a";
			}
			UserId userId = null;
			if ("emplId".equalsIgnoreCase(idType) || "e".equalsIgnoreCase(idType)) {
				userId = new EmplId(id);
			} else if ("workflowId".equalsIgnoreCase(idType) || "w".equalsIgnoreCase(idType)) {
				userId = new WorkflowUserId(id);
			} else if ("uuId".equalsIgnoreCase(idType) || "u".equalsIgnoreCase(idType)) {
				userId = new UuId(id);
		    } else if ("authenticationId".equalsIgnoreCase(idType) || "a".equalsIgnoreCase(idType)) {
		    	userId = new AuthenticationUserId(id);
		    }
			if (userId == null) {
				return 0;
			}
			WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(userId);
			return KEWServiceLocator.getActionListService().getCount(user);
		} catch (Throwable t) {
			LOG.error("Fatal error when querying for Action List Count", t);
			return 0;
		}
	}



}
