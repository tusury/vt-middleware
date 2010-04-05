<%@ include file="includes/top.jsp" %>

<div class="crumbs">
  <span>
    <a href="<c:url value="/secure/project/list.html" />">Project Listing</a>
  </span>
  <span>&raquo;</span>
  <span>Copy Project</span>
</div>

<h1>Copy Project</h1>

<form:form method="post" commandName="spec">
  <form:errors id="error" path="*" element="div" />
  
  <fieldset>
  <legend>Create New Project from Existing</legend>
  <div class="field">
    <div><label for="sourceId">Project to Copy</label></div>
    <div><form:select id="sourceId" path="sourceId"
      items="${projects}" itemValue="id" itemLabel="name"/></div>
  </div>
  <div class="field">
    <div><label for="name">New Project Name</label></div>
    <div><form:input id="name" path="name" size="30" /></div>
  </div>
  <div class="field">
    <div><input type="submit" name="action" value="Copy" /></div>
  </div>
  </fieldset>

</form:form>

<%@ include file="includes/bottom.jsp" %>
