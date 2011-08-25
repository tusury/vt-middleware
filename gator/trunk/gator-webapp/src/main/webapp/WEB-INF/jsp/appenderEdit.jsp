<%@ include file="includes/top.jsp" %>
<%@ include file="includes/user.jsp" %>

<c:choose>
  <c:when test="${appender['new']}">
    <c:set var="action" value="Add Appender" />
  </c:when>
  <c:otherwise>
    <c:set var="action" value="Edit Appender" />
  </c:otherwise>
</c:choose>

<div id="crumbs">
  <ul>
    <li class="first"><a class="navlink" href="<c:url value="/secure/project/list.html" />">Project Listing</a></li>
    <li><a class="navlink" href="<c:url value="/secure/project/${appender.project.name}/edit.html" />">Edit <em>${appender.project.name}</em></a></li>
    <li>${action}</li>
  </ul>
</div>
<div class="clear"></div>

<div id="title">${action}</div>

<form:form method="post" commandName="appender">
  <fieldset>
  <legend>Appender Configuration</legend>

  <spring:hasBindErrors name="appender">
    <div id="validation-summary"><spring:message code="error.validationSummary" /></div>
  </spring:hasBindErrors>

  <div class="field">
    <div><label for="name">Appender Name</label></div>
    <form:errors cssClass="field-error" path="name" element="div" />
    <div><form:input id="name" path="name" size="50" /></div>
  </div>
  <div class="field">
    <div><label for="appenderClassName">Fully Qualified Appender Class Name</label></div>
    <form:errors cssClass="field-error" path="appenderClassName" element="div" />
    <div><form:input id="appenderClassName" path="appenderClassName"
      size="100" /></div>
  </div>
  <div class="field">
    <div><label for="layoutClassName">Fully Qualified Layout Class Name</label></div>
    <div><form:input id="layoutClassName" path="layoutClassName"
      size="100" /></div>
  </div>
  <div class="field">
    <div><label for="errorHandlerClassName">Fully Qualified Error Handler Class Name</label></div>
    <div><form:input id="errorHandlerClassName" path="errorHandlerClassName"
      size="100" /></div>
  </div>
  <div class="field">
    <div>
			<label for="appenderParams">Appender Parameters</label>
			<span class="note">Format is name=value, one per line.</span>
    </div>
    <div><form:textarea id="appenderParams" path="appenderParams"
      rows="8" cols="75" /></div>
  </div>
  <div class="field">
    <div>
			<label for="layoutParams">Layout Parameters</label>
			<span class="note">Format is name=value, one per line.</span>
    </div>
    <div><form:textarea id="layoutParams" path="layoutParams"
      rows="8" cols="75" /></div>
  </div>
  <div class="field">
		<input class="button" type="submit" name="action" value="Update" />
  </div>
  </fieldset>

</form:form>

<%@ include file="includes/bottom.jsp" %>
