<%@ include file="includes/top.jsp" %>

<c:choose>
  <c:when test="${appender.new}">
    <c:set var="action" value="Add Appender" />
  </c:when>
  <c:otherwise>
    <c:set var="action" value="Edit Appender" />
  </c:otherwise>
</c:choose>

<div class="crumbs">
  <span>
    <a href="<c:url value="/secure/project/list.html" />">Project Listing</a>
  </span>
  <span>&raquo;</span>
  <span>
    <a href="<c:url value="/secure/project/${appender.project.name}/edit.html" />">Edit <em>${appender.project.name}</em></a>
  </span>
  <span>&raquo;</span>
  <span>${action}</span>
</div>

<h1>${action}</h1>

<form:form method="post" commandName="appender">
  <form:errors id="error" path="*" element="div" />
  
  <fieldset>
  <legend>Appender Configuration</legend>
  <div class="field">
    <div><label for="name">Appender Name</label></div>
    <div><form:input id="name" path="name" size="50" /></div>
  </div>
  <div class="field">
    <div><label for="appenderClassName">Fully Qualified Appender Class Name</label></div>
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
    <div><form:textarea id="appenderParams" path="appenderParamArray"
      rows="8" cols="75" /></div>
  </div>
  <div class="field">
    <div>
			<label for="layoutParams">Layout Parameters</label>
			<span class="note">Format is name=value, one per line.</span>
    </div>
    <div><form:textarea id="layoutParams" path="layoutParamArray"
      rows="8" cols="75" /></div>
  </div>
  <div class="field">
		<div><input type="submit" name="action" value="Update" /></div>
  </div>
  </fieldset>

</form:form>

<%@ include file="includes/bottom.jsp" %>
