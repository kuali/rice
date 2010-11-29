<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
    <title>${kualiInquirable.title}</title>
</head>
<body>
    ${command.businessObjectClassName}
    
    <c:set var="FieldSections" value="${command.sections}" />
    <table border="1">
    <c:forEach items="${FieldSections}" var="section">
    	<tr><th colspan="2">${section.sectionTitle}</th></tr>
    	<c:forEach items="${section.rows}" var="row">
    		<tr>
    		<c:forEach items="${row.fields}" var="field">
    			<th>${field.propertyName}</th>
    			<td>${field.propertyValue}</td>
    		</c:forEach>
    		</tr>
    	</c:forEach>
    </c:forEach>
    </table>
</body>
</html>