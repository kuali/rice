package edu.sampleu.demo.kitchensink;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * Test service exposed through REST and connected to a {@link org.kuali.rice.krad.uif.element.DataTable}
 * component in the UIF
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Path("/")
public class DataTableRestServiceTestImpl {

    /**
     * Returns a string of data in JSON format for populating the table
     *
     * @return String json data string
     */
    @GET
    @Path("/TableData")
    public String getTableData() {
        StringBuilder sb = new StringBuilder();

        sb.append("{ \"aaData\": [\n");

        for (int i = 0; i < 800; i++) {
            sb.append("[\"CHEM " + i + "\",");
            // add a hidden script to verify that it runs successfully
            if (i % 10 == 0) {
                String spanId = "nm_row_" + i;
                sb.append("\"<span id='" + spanId + "'>INTRODUCTION TO GENERAL CHEMISTRY</span>");
                sb.append("<input type='hidden' name='script' value=\\\"jQuery('#"
                        + spanId + "').attr('style', 'color:green');\\\"/>\"");
                sb.append(",");
            } else {
                sb.append("\"INTRODUCTION TO GENERAL CHEMISTRY\",");
            }
            sb.append("\"3\",");
            sb.append("\"AU\",");
            sb.append("\"NW\",");
            sb.append("\"Neal\",");
            sb.append("\"MWF\",");
            sb.append("\"300.00\",");
            sb.append("\"3\",");
            sb.append("\"100\"]");

            if (i < 799) {
                sb.append(",\n");
            }
        }

        sb.append("\n] }");

        return sb.toString();
    }
}
