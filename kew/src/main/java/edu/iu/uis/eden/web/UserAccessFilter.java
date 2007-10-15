package edu.iu.uis.eden.web;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.util.Utilities;
import edu.iu.uis.eden.web.session.Authentication;
import edu.iu.uis.eden.web.session.UserSession;

public class UserAccessFilter implements Filter {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(UserAccessFilter.class);

	private String currentDeniedRolesConst = null;
	private String currentAllowedRolesConst = null;
	private Set<String> deniedRoles = new HashSet<String>();
	private Set<String> allowedRoles = new HashSet<String>();

	public void init(FilterConfig config) throws ServletException {
	}

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
	    LOG.debug("Begin UserAccessFilter...");
		UserSession userSession = UserSession.getAuthenticatedUser();
		AuthorizationResult result = KEWServiceLocator.getWebAuthorizationService().isAuthorized(userSession, (HttpServletRequest)request);
		boolean isDeniedAccess = !result.isAuthorized();
		if (!isDeniedAccess) {
			boolean denied = false;
			boolean allowed = false;
			try {
				updateRoles();
				if (!deniedRoles.isEmpty() || !allowedRoles.isEmpty()) {
					for (Iterator iterator = userSession.getAuthentications().iterator(); iterator.hasNext();) {
						Authentication auth = (Authentication) iterator.next();
						String role = auth.getAuthority();
						if (deniedRoles.contains(role)) {
							denied = true;
						} else if (allowedRoles.contains(role)) {
							allowed = true;
						}
					}
				}
			} catch (Exception e) {
				throw new ServletException("Problem determining if user '" + userSession.getNetworkId() +"' has access to the system.  An error was encountered.", e);
			}
			isDeniedAccess = denied && !allowed;
		}
        if (isDeniedAccess) {
        	request.getRequestDispatcher("/WEB-INF/jsp/NotAuthorized.jsp").forward(request, response);
        } else {
            LOG.debug("...end UserAccessFilter.");
        	chain.doFilter(request, response);
        }
	}

	/**
	 * This method not thread-safe but we'll err on the side of multiple updates to these values for the sake of speed
	 */
	protected void updateRoles() {
		String deniedRolesConst = Utilities.getApplicationConstant("Config.Immutables.DeniedRoles");
		String allowedRolesConst = Utilities.getApplicationConstant("Config.Immutables.AllowedRoles");
		if (!ObjectUtils.equals(currentDeniedRolesConst, deniedRolesConst) && !StringUtils.isBlank(deniedRolesConst)) {
			Set<String> deniedRoles = new HashSet<String>();
			deniedRoles.addAll(Arrays.asList(deniedRolesConst.split(",")));
			currentDeniedRolesConst = deniedRolesConst;
			this.deniedRoles = deniedRoles;
		}
		if (!ObjectUtils.equals(currentAllowedRolesConst, allowedRolesConst) && !StringUtils.isBlank(allowedRolesConst)) {
			Set<String> allowedRoles = new HashSet<String>();
			allowedRoles.addAll(Arrays.asList(allowedRolesConst.split(",")));
			currentAllowedRolesConst = allowedRolesConst;
			this.allowedRoles = allowedRoles;
		}
	}


}
