<%@ include file="includes/top.jsp" %>
<%@ include file="includes/user.jsp" %>

<div id="crumbs">
  <ul>
    <li class="first"><a class="navlink" href="<c:url value="/secure/project/list.html" />">Project Listing</a></li>
    <li>Create Project</li>
  </ul>
</div>
<div class="clear"></div>

<div id="title">Create Project</div>

<form:form method="post" commandName="project">
<fieldset>
  <legend>Project Properties</legend>

	<spring:hasBindErrors name="project">
	  <div id="validation-summary">Form data validation failed.</div>
	</spring:hasBindErrors>

  <div class="field">
    <div><label for="name">Project Name</label></div>
    <form:errors cssClass="field-error" path="name" element="div" />
    <div><form:input id="name" path="name" size="50" maxlength="50" /></div>
  </div>
  <div class="field">
    <div><label for="clientLogDir">Client Log Directory</label></div>
    <div class="note">Prepended to all file appender paths
      for the client configuration only.</div>
    <div><form:input id="clientLogDir" path="clientLogDir" size="75"
      maxlength="100" /></div>
  </div>
  <div class="field">
    <div><label for="loggingEngine">Logging Engine</label></div>
    <div class="note">Logging engine used to process logging events sent
      by clients.</div>
    <div><form:select id="loggingEngine" path="loggingEngine"
      items="${loggingEngineMap}"/></div>
  </div>
  <div class="field">
		<input class="button" type="submit" name="action" value="Create" />
  </div>
</fieldset>
</form:form>

<%@ include file="includes/bottom.jsp" %>
