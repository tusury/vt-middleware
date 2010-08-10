<%@ include file="includes/top.jsp" %>
<%@ include file="includes/user.jsp" %>

<div id="crumbs">
  <ul>
    <li class="first"><a class="navlink" href="<c:url value="/secure/project/list.html" />">Project Listing</a></li>
    <li><a class="navlink" href="<c:url value="/secure/project/${bulkData.projectName}/edit.html" />">Edit <em>${bulkData.projectName}</em></a></li>
    <li>Bulk Edit Appenders</li>
  </ul>
</div>
<div class="clear"></div>

<div id="title">Bulk Edit Appenders</div>

<form:form method="post" commandName="bulkData">
  <fieldset>
  <legend>Bulk Edit Form</legend>

  <spring:hasBindErrors name="bulkData">
    <div id="validation-summary"><spring:message code="error.validationSummary" /></div>
  </spring:hasBindErrors>

  <div class="field">
    <div>
      <label for="appenderIds">Appenders to Change</label>
    </div>
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
    <div><label for="appenderClassName">Fully Qualified Appender Class Name</label></div>
    <div class="note">Leave blank to omit appender class changes.</div>
    <form:errors cssClass="field-error" path="appenderClassName" element="div" />
    <div><form:input id="appenderClassName" path="appenderClassName"
      size="100" /></div>
  </div>
  <div class="field">
    <div><label for="layoutClassName">Fully Qualified Layout Class Name</label></div>
    <div class="checkboxes">
      <form:checkbox id="applyBlankLayoutClass" path="applyBlankLayoutClass"
        label="Apply blank value (otherwise blank leaves layout class unchanged)" />
    </div>
    <div><form:input id="layoutClassName" path="layoutClassName"
      size="100" /></div>
  </div>
  <div class="field">
    <div><label for="errorHandlerClassName">Fully Qualified Error Handler Class Name</label></div>
    <div class="checkboxes">
      <form:checkbox id="applyBlankErrorHandlerClass" path="applyBlankErrorHandlerClass"
        label="Apply blank value (otherwise blank leaves error handler class unchanged)" />
    </div>
    <div><form:input id="errorHandlerClassName" path="errorHandlerClassName"
      size="100" /></div>
  </div>
  <div class="field">
    <div>
			<label for="appenderParams">Appender Parameters</label>
			<span class="note">Format is name=value, one per line.</span>
    </div>
    <div class="checkboxes">
      <form:checkbox id="clearAppenderParams" path="clearAppenderParams"
        label="Delete existing appender parameters" />
    </div>
    <div><form:textarea id="appenderParams" path="appenderParams"
      rows="8" cols="75" /></div>
  </div>
  <div class="field">
    <div>
			<label for="layoutParams">Layout Parameters</label>
			<span class="note">Format is name=value, one per line.</span>
    </div>
    <div class="checkboxes">
      <form:checkbox id="clearLayoutParams" path="clearLayoutParams"
        label="Delete existing layout parameters" />
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
