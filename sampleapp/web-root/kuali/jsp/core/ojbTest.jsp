<%--
 Copyright 2006 The Kuali Foundation.
 
 Licensed under the Educational Community License, Version 1.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl1.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="tldHeader.jsp"%>
<%
    out.println("test");
    org.kuali.core.dao.DocumentDao dao = (org.kuali.core.dao.DocumentDao) org.kuali.core.util.SpringServiceLocator.getBeanFactory().getBean("documentDao");
    out.println("test2");

    java.lang.String version = request.getParameter("FDOC_NBR");
    java.lang.String delete = request.getParameter("DELETE");

    out.println("Parameters");
    out.println("-------------------------");
    out.println("FDOC_NBR: " + version);
    out.println("DELETE (Y/N): " + delete);
    out.println("-------------------------");

    if ("Y".equals(delete)) {
        out.println("running SQL delete commands");
        org.kuali.module.gl.dao.UnitTestSqlDao unitTestSqlDao = (org.kuali.module.gl.dao.UnitTestSqlDao) org.kuali.core.util.SpringServiceLocator.getBeanFactory().getBean("glUnitTestSqlDao");
        unitTestSqlDao.sqlCommand("DELETE FROM FP_MAINTENANCE_DOCUMENT_T WHERE FDOC_NBR =  " + version);
        unitTestSqlDao.sqlCommand("DELETE FROM FP_DOC_HEADER_T WHERE FDOC_NBR =  " + version);
    }
    else {
        out.println("not running any explicit SQL");
    }

    org.kuali.core.document.MaintenanceDocument doc;

    doc = (org.kuali.core.document.MaintenanceDocument) dao.findByDocumentHeaderId(org.kuali.core.document.MaintenanceDocumentBase.class, version);

    if (doc == null) {
        out.println("No document retrieved, creating it.");
        doc = new org.kuali.core.document.MaintenanceDocumentBase();
        org.kuali.core.document.DocumentHeader header = new org.kuali.core.document.DocumentHeader();
        header.setDocumentNumber(version);
        doc.setDocumentNumber(version);
        doc.setDocumentHeader(header);
    }
    else {
        out.println("No document retrieved, creating it.");
    }


    java.lang.StringBuffer longString = new java.lang.StringBuffer();

    for (int i = 0; i < 500; i++) {
        longString.append("0123456789");
    }

    doc.setXmlDocumentContents(longString.toString());


    dao.save(doc);

    out.println("test3");

%>
