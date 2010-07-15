<%@ include file="/WEB-INF/jsp/includes/top.jsp" %>
<%@ include file="includes/user.jsp" %>
<div class="clear"></div>

<h1>Error</h1>

<p>Current time: <%= new java.util.Date() %></p> 
<p id="error">Threw ${exception.class.name}:<br/>
<c:out escapeXml="true" value="${exception.localizedMessage}" />
</p>

<%@ include file="/WEB-INF/jsp/includes/bottom.jsp" %>