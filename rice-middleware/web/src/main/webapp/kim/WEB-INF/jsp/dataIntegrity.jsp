<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp" %>

<kul:page headerTitle="Data Integrity" lookup="true"
          transactionalDocument="false" showDocumentInfo="false"
          htmlFormAction="dataIntegrity" docTitle="Data Integrity">
    <script>
        function check() {
            document.forms[0].elements['methodToCall'].value = 'check';
            document.forms[0].submit();
        }
        function repair() {
            document.forms[0].elements['methodToCall'].value = 'repair';
            document.forms[0].submit();
        }
    </script>
    <html-el:form action="dataIntegrity">
        <html-el:hidden property="methodToCall" value=""/>
        <div>
            <input type="button" value="Run Data Check" onclick="check()"/>
        </div>
        <c:if test="${! empty checkMessages}">
            <div>
                <ul>
                    <c:forEach var="message" items="${checkMessages}">
                        <li><c:out value="${message}" escapeXml="true"/></li>
                    </c:forEach>
                </ul>
            </div>
        </c:if>
        <div>
            <input type="button" value="Run Data Repair" onclick="repair()"/>
        </div>
        <c:if test="${! empty repairMessages}">
            <div>
                <ul>
                    <c:forEach var="message" items="${repairMessages}">
                        <li><c:out value="${message}" escapeXml="true"/></li>
                    </c:forEach>
                </ul>
            </div>
        </c:if>
    </html-el:form>

</kul:page>
