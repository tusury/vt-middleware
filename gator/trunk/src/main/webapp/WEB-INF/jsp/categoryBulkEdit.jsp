<%@ include file="includes/top.jsp" %>

<div class="crumbs">
  <span>
    <a href="<c:url value="/auth/list.html" />">Project Listing</a>
  </span>
  <span>&raquo;</span>
  <span>
    <a href="<c:url value="/auth/project/${project.name}/edit.html" />">Edit <em>${project.name}</em></a>
  </span>
  <span>&raquo;</span>
  <span>Bulk Edit Categories</span>
</div>

<h1>Bulk Edit Categories</h1>

<form:form method="post" commandName="formData">
  <form:errors id="error" path="*" element="div" />
  
  <fieldset>
  <legend>Bulk Edit Form</legend>
  <div class="field">
    <div>
      <label for="name">Categories to Change</label>
    </div>
    <div style="margin:5px">
      <span><a href="javascript:select('categoryIds', true)">Select All</a><span>
      <span>|</span>
      <span><a href="javascript:select('categoryIds', false)">Select None</a><span>
    </div>
    <div class="checkboxes">
      <form:checkboxes id="categoryIds" path="categoryIds"
       items="${availableCategories}" itemValue="id" itemLabel="name"
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
  	</div>
  	<div style="margin:5px">
      <span><a href="javascript:select('appenderIds', true)">Select All</a><span>
      <span>|</span>
      <span><a href="javascript:select('appenderIds', false)">Select None</a><span>
    </div>
    <div class="checkboxes">
	    <form:checkboxes id="appenderIds" path="appenderIds"
	     items="${availableAppenders}" itemValue="id" itemLabel="name"
	     element="div" />
	  </div>
  </div>
  <div class="field">
		<div><input type="submit" name="action" value="Apply" /></div>
  </div>
  </fieldset>

</form:form>

<%@ include file="includes/bottom.jsp" %>
