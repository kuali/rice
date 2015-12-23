package org.kuali.rice.web.health;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.StringWriter;

/**
 * Implements an endpoint for providing health information for a Kuali Rice server.
 */
public class HealthServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(HealthServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HealthStatus status = checkHealth();

        String includeDetail = req.getParameter("detail");
        if ("true".equals(includeDetail)) {
            if (status.isOk()) {
                resp.setStatus(200);
            } else {
                resp.setStatus(503);
            }
            resp.setContentType("application/json");
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(resp.getOutputStream(), status);

            StringWriter writer = new StringWriter();
            mapper.writeValue(writer, status);
            System.out.println("WRITE VALUE: " + writer.toString());
        } else {
            if (status.isOk()) {
                resp.setStatus(204);
            } else {
                resp.setStatus(503);
            }
        }
        resp.getOutputStream().flush();
    }

    private HealthStatus checkHealth() {
        HealthStatus status = new HealthStatus(HealthStatus.OK);
        checkDatabase(status);
        return status;
    }

    private void checkDatabase(HealthStatus status) {
        boolean connected = testDataSource(KEWServiceLocator.getDataSource());
        HealthMetric metric = new HealthMetric("DB", "Connected", connected);
        status.getMetrics().add(metric);
        if (!connected) {
            status.setStatusCode(HealthStatus.FAILED);
        }
    }

    /**
     * Just execute a random query against the KEW database which should never return any results but should still
     * execute successfully. This makes it so we don't have to try and figure out how to drum up the validation query
     * for our data source in this method.
     */
    private boolean testDataSource(DataSource dataSource) {
        JdbcTemplate template = new JdbcTemplate(dataSource);
        try {
            template.execute("select DOC_HDR_ID from KREW_DOC_HDR_T where DOC_HDR_ID='invalid_id'");
        } catch (Exception e) {
            LOG.warn("Failed health check", e);
            return false;
        }
        return true;
    }



}
