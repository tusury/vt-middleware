<%@ include file="/WEB-INF/jsp/includes/top.jsp" %>

<p>Redirecting to
<a href="<c:url value="/secure/list.html" />">application start page</a>...</p>

<c:redirect url="/secure/list.html" />

<%@ include file="/WEB-INF/jsp/includes/bottom.jsp" %>
