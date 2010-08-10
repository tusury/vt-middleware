<%@ include file="includes/top.jsp" %>
<%@ include file="includes/user.jsp" %>

<div id="crumbs">
  <ul>
    <li class="first"><a class="navlink" href="<c:url value="/secure/project/list.html" />">Project Listing</a></li>
    <li><a class="navlink" href="<c:url value="/secure/project/${bulkData.project.name}/edit.html" />">Edit <em>${bulkData.project.name}</em></a></li>
    <li>Bulk Edit Categories</li>
  </ul>
</div>
<div class="clear"></div>

<div id="title">Bulk Edit Categories</div>

<form:form method="post" commandName="bulkData">
  <fieldset>
  <legend>Bulk Edit Form</legend>

  <spring:hasBindErrors name="bulkData">
    <div id="validation-summary"><spring:message code="error.validationSummary" /></div>
  </spring:hasBindErrors>

  <div class="field">
    <div>
      <label for="categoryIds">Categories to Change</label>
    </div>
    <div style="margin:5px">
      <span><a href="javascript:select('categoryIds', true)">Select All</a><span>
      <span>|</span>
      <span><a href="javascript:select('categoryIds', false)">Select None</a><span>
    </div>
    <div class="checkboxes">
      <form:checkboxes id="categoryIds" path="categoryIds"
       items="${projectCategories}" itemValue="id" itemLabel="name"
       element="div" />
    </div>
  </div>
  <div class="field">
    <div><label for="level">Log Level</label></div>
    <div><form:select id="level" path="level" items="${logLevels}" /></div>
  </div>
  <div class="field">
    <div><form:checkbox id="clearExistingAppenders" path="clearExistingAppenders"
      label="Clear Existing Appenders"/></div>
  </div>
  <div class="field">
    <div><label for="appenderIds">Category Appenders</label></div>
		<div class="note">Send logging events to these appenders.</div>
  	<div style="margin:5px">
      <span><a href="javascript:select('appenderIds', true)">Select All</a><span>
      <span>|</span>
      <span><a href="javascript:select('appenderIds', false)">Select None</a><span>
    </div>
    <div class="checkboxes">
	    <form:checkboxes id="appenderIds" path="appenderIds"
	     items="${projectAppenders}" itemValue="id" itemLabel="name"
	     element="div" />
	  </div>
  </div>
  <div class="field">
		<input class="button" type="submit" name="action" value="Apply" />
  </div>
  </fieldset>

</form:form>

<%@ include file="includes/bottom.jsp" %>
