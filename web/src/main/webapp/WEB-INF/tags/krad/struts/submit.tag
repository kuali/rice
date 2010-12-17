<%@ include file="/WEB-INF/jsp/krad/tldHeader.jsp"%>

<%@ attribute name="property" required="true" description="" %>
<%@ attribute name="value" required="true" description="" %>
<%@ attribute name="title" required="false" description="" %>
<%@ attribute name="alt" required="false" description="" %>
<%@ attribute name="disabled" required="false" description="" %>


<c:choose>
	<c:when test="${disabled != 'true'}">
		<c:set var="disabledDisplay" value="disabled" />
	</c:when>
	<c:otherwise>
		<c:set var="disabledDisplay" value="" />
	</c:otherwise>
</c:choose>

<input type="submit" name="${property}" value="${value} title="${title}" alt="${alt}" ${disabledDisplay} />