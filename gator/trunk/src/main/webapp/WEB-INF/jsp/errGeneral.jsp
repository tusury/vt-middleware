<%@ include file="/WEB-INF/jsp/includes/top.jsp" %>

<div class="crumbs">
  <span>
    <a href="<c:url value="/auth/list.html" />">Project Listing</a>
  </span>
  <span>&raquo;</span>
  <span>Application Error</span>
</div>

<h1>Error</h1>

<p>Current time: <%= new java.util.Date() %></p> 
<p id="error">Threw ${exception.class.name}:<br/>
<c:out escapeXml="true" value="${exception.localizedMessage}" />
</p>
<p>Please retry the operation that caused this error.</p>

<%@ include file="/WEB-INF/jsp/includes/bottom.jsp" %>