<%@ include file="/WEB-INF/jsp/includes/top.jsp" %>

<p>Redirecting to
<a href="<c:url value="/auth/list.html" />">application start page</a>...</p>

<c:redirect url="/auth/list.html" />

<%@ include file="/WEB-INF/jsp/includes/bottom.jsp" %>
