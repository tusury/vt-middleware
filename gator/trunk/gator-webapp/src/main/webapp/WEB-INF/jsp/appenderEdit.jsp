<%@ include file="includes/top.jsp" %>

<c:choose>
  <c:when test="${wrapper.appender.id == 0}">
    <c:set var="action" value="Add Appender" />
  </c:when>
  <c:otherwise>
    <c:set var="action" value="Edit Appender" />
  </c:otherwise>
</c:choose>

<div class="crumbs">
  <span>
    <a href="<c:url value="/secure/list.html" />">Project Listing</a>
  </span>
  <span>&raquo;</span>
  <span>
    <a href="<c:url value="/secure/project/${project.name}/edit.html" />">Edit <em>${project.name}</em></a>
  </span>
  <span>&raquo;</span>
  <span>${action}</span>
</div>

<h1>${action}</h1>

<form:form method="post" commandName="wrapper">
  <form:errors id="error" path="*" element="div" />
  
  <fieldset>
  <legend>Appender Configuration</legend>
  <div class="field">
    <div><label for="name">Appender Name</label></div>
    <div><form:input id="name" path="appender.name" size="50" /></div>
  </div>
  <div class="field">
    <div><label for="appenderClassName">Fully Qualified Appender Class Name</label></div>
    <div><form:input id="appenderClassName" path="appender.appenderClassName"
      size="100" /></div>
  </div>
  <div class="field">
    <div><label for="layoutClassName">Fully Qualified Layout Class Name</label></div>
    <div><form:input id="layoutClassName" path="appender.layoutClassName"
      size="100" /></div>
  </div>
  <div class="field">
    <div><label for="errorHandlerClassName">Fully Qualified Error Handler Class Name</label></div>
    <div><form:input id="errorHandlerClassName" path="appender.errorHandlerClassName"
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
		<div><input type="submit" name="action" value="Update" /></div>
  </div>
  </fieldset>

</form:form>

<%@ include file="includes/bottom.jsp" %>
