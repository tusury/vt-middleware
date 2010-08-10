<%@ include file="includes/top.jsp" %>
<%@ include file="includes/user.jsp" %>

<div id="crumbs">
  <ul>
    <li class="first"><a class="navlink" href="<c:url value="/secure/project/list.html" />">Project Listing</a></li>
    <li><a class="navlink" href="<c:url value="/secure/project/${spec.project.name}/edit.html" />">Edit <em>${spec.project.name}</em></a></li>
    <li>Delete ${spec.typeName}</li>
  </ul>
</div>
<div class="clear"></div>

<div id="title">Delete ${spec.typeName}</div>

<form:form method="post" commandName="spec">
  
  <fieldset>
    <legend>Delete Confirmation</legend>
	
	  <spring:hasBindErrors name="spec">
	    <div id="validation-summary"><spring:message code="error.validationSummary" /></div>
	  </spring:hasBindErrors>

 	  <form:errors cssClass="field-error" path="*" element="div" />
    <div class="field">
      <div class="checkboxes">
        <form:checkbox id="delete" path="confirmationFlag"
          value="confirmationFlag"
          label="Confirm deletion of <strong>${spec.configToBeDeleted.name}</strong>" />
      </div>
    </div>
    <div class="field">
      <input class="button" type="submit" name="action" value="Delete" />
    </div>
  </fieldset>
</form:form>

<%@ include file="includes/bottom.jsp" %>