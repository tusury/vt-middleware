<%@ include file="includes/top.jsp" %>

<div class="crumbs">
  <span>
    <a href="<c:url value="/secure/list.html" />">Project Listing</a>
  </span>
  <span>&raquo;</span>
  <span>Delete Project</span>
</div>

<h1>Delete Project</h1>

<form:form method="post" commandName="spec">
  <form:errors id="error" path="*" element="div" />
  
  <fieldset>
    <legend>Delete Project</legend>
    <div class="field">
      <div class="checkboxes">
        <form:checkbox id="delete" path="confirmationFlag"
          value="confirmationFlag"
          label="Confirm deletion of project <strong>${spec.name}</strong>" />
      </div>
    </div>
    <div class="field">
      <input type="submit" name="action" value="Delete" />
    </div>
  </fieldset>
</form:form>

<%@ include file="includes/bottom.jsp" %>
