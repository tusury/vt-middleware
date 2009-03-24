<%@ include file="includes/top.jsp" %>

<div class="crumbs">
  <span>
    <a href="<c:url value="/secure/list.html" />">Project Listing</a>
  </span>
  <span>&raquo;</span>
  <span>
    <a href="<c:url value="/secure/project/${project.name}/edit.html" />">Edit <em>${project.name}</em></a>
  </span>
  <span>&raquo;</span>
  <span>Copy Appender</span>
</div>

<h1>Copy Appender</h1>

<form:form method="post" commandName="spec">
  <form:errors id="error" path="*" element="div" />
  
  <fieldset>
  <legend>Create New Appender from Existing</legend>
  <div class="field">Project <em>${project.name}</em></div>
  <div class="field">
    <div><label for="sourceAppenderId">Appender to Copy</label></div>
    <div><form:select id="sourceAppenderId" path="sourceAppenderId"
      items="${appenders}" itemValue="id" itemLabel="name"/></div>
  </div>
  <div class="field">
    <div><label for="newName">New Appender Name</label></div>
    <div><form:input id="newName" path="newName" size="30" /></div>
  </div>
  <div class="field">
    <div><input type="submit" name="action" value="Copy" /></div>
  </div>
  </fieldset>

</form:form>

<%@ include file="includes/bottom.jsp" %>
