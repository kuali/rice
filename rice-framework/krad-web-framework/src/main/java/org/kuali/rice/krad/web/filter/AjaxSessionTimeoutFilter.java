package org.kuali.rice.krad.web.filter;

import org.kuali.rice.krad.util.KRADConstants;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Looks for a session timeout on an Ajax request and returns back an error
 * code back to the caller
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class AjaxSessionTimeoutFilter implements Filter {

    private int sessionTimeoutErrorCode = 403;

    public void init(FilterConfig filterConfig) throws ServletException {
        String timeoutErrorCode = filterConfig.getInitParameter("sessionTimeoutErrorCode");

        if (timeoutErrorCode != null) {
            sessionTimeoutErrorCode = Integer.parseInt(timeoutErrorCode);
        }
    }

    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain filerChain) throws IOException, ServletException {
        HttpSession currentSession = ((HttpServletRequest) request).getSession(false);

        if ((currentSession == null) || (currentSession.getAttribute(KRADConstants.USER_SESSION_KEY) == null)) {
            String ajaxHeader = ((HttpServletRequest) request).getHeader("x-requested-with");
            if ("XMLHttpRequest".equals(ajaxHeader)) {
                HttpServletResponse resp = (HttpServletResponse) response;
                resp.setStatus(sessionTimeoutErrorCode);
            }
        }

        filerChain.doFilter(request, response);
    }

    public void destroy() {

    }
}
