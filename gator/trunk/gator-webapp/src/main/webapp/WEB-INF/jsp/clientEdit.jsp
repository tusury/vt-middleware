<%@ include file="includes/top.jsp" %>

<c:choose>
  <c:when test="${client.new}">
    <c:set var="action" value="Add Client" />
  </c:when>
  <c:otherwise>
    <c:set var="action" value="Edit Client" />
  </c:otherwise>
</c:choose>

<div class="crumbs">
  <span>
    <a href="<c:url value="/secure/project/list.html" />">Project Listing</a>
  </span>
  <span>&raquo;</span>
  <span>
    <a href="<c:url value="/secure/project/${client.project.name}/edit.html" />">
    Edit <em>${client.project.name}</em></a>
  </span>
  <span>&raquo;</span>
  <span>${action}</span>
</div>

<h1>${action}</h1>

<form:form method="post" commandName="client">
  <form:errors id="error" path="*" element="div" />
  
  <fieldset>
  <legend>Client Configuration</legend>
  <div class="field">
    <div><label for="name">Client Host Name/IP Address</label></div>
    <div><form:input id="name" path="name" size="50" /></div>
  </div>
  <div class="field">
		<div><input type="submit" name="action" value="Update" /></div>
  </div>
  </fieldset>

</form:form>

<%@ include file="includes/bottom.jsp" %>
