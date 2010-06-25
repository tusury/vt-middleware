<%@ include file="includes/top.jsp" %>

<c:choose>
  <c:when test="${client.new}">
    <c:set var="action" value="Add Client" />
  </c:when>
  <c:otherwise>
    <c:set var="action" value="Edit Client" />
  </c:otherwise>
</c:choose>

<div id="crumbs">
  <ul>
    <li class="first"><a class="navlink" href="<c:url value="/secure/project/list.html" />">Project Listing</a></li>
    <li><a class="navlink" href="<c:url value="/secure/project/${client.project.name}/edit.html" />">Edit <em>${client.project.name}</em></a></li>
    <li>${action}</li>
  </ul>
</div>
<div class="clear"></div>

<div id="title">${action}</div>

<form:form method="post" commandName="client">
  <fieldset>
  <legend>Client Configuration</legend>

  <spring:hasBindErrors name="client">
    <div id="validation-summary"><spring:message code="error.validationSummary" /></div>
  </spring:hasBindErrors>

  <div class="field">
    <div><label for="name">Client Host Name/IP Address</label></div>
    <form:errors cssClass="field-error" path="name" element="div" />
    <div><form:input id="name" path="name" size="50" /></div>
  </div>
  <div class="field">
		<input class="button" type="submit" name="action" value="Update" />
  </div>
  </fieldset>

</form:form>

<%@ include file="includes/bottom.jsp" %>
