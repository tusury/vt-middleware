<%@ include file="includes/top.jsp" %>
<%@ include file="includes/user.jsp" %>

<c:choose>
  <c:when test="${category.new}">
    <c:set var="action" value="Add Category" />
  </c:when>
  <c:otherwise>
    <c:set var="action" value="Edit Category" />
  </c:otherwise>
</c:choose>

<div id="crumbs">
  <ul>
    <li class="first"><a class="navlink" href="<c:url value="/secure/project/list.html" />">Project Listing</a></li>
    <li><a class="navlink" href="<c:url value="/secure/project/${category.project.name}/edit.html" />">Edit <em>${category.project.name}</em></a></li>
    <li>${action}</li>
  </ul>
</div>
<div class="clear"></div>

<div id="title">${action}</div>

<form:form method="post" commandName="category">
  <fieldset>
  <legend>Category Configuration</legend>

  <spring:hasBindErrors name="category">
    <div id="validation-summary"><spring:message code="error.validationSummary" /></div>
  </spring:hasBindErrors>

  <div class="field">
    <div><label for="name">Category Name</label></div>
    <div class="note">Special name "root" indicates root category.</div>
    <form:errors cssClass="field-error" path="name" element="div" />
    <div><form:input id="name" path="name" size="75" /></div>
  </div>
  <div class="field">
    <div><label for="level">Log Level</label></div>
    <div><form:select id="level" path="level" items="${logLevels}" /></div>
  </div>
  <div class="field">
    <div class="label">Additivity</div>
    <div class="checkboxes">
      <form:checkbox id="level" path="additivity"
        label="Enable additivity for this category" />
    </div>
  </div>
  <div class="field">
    <div><label for="appenderIds">Category Appenders</label></div>
		<div class="note">Send logging events to these appenders.</div>
    <div class="navlink_group" style="margin:9px">
      <a class="navlink"
        href="javascript:selectMultiple(['appenders','allowSocketAppender'], true)">Select All</a>
      <span>|</span>
      <a class="navlink"
        href="javascript:selectMultiple(['appenders','allowSocketAppender'], false)">Select None</a>
    </div>
    <form:errors cssClass="field-error" path="appenders" element="div" />
    <div class="checkboxes">
      <form:checkbox id="allowSocketAppender" path="allowSocketAppender"
        label="SOCKET (gator will receive a logging event for each message)" />
    </div>
    <div class="checkboxes">
	    <form:checkboxes id="appenderIds" path="appenders"
	     items="${projectAppenders}" itemValue="id" itemLabel="name"
	     element="div" />
	  </div>
  </div>
  <div class="field">
		<input class="button" type="submit" name="action" value="Update" />
  </div>
  </fieldset>

</form:form>

<%@ include file="includes/bottom.jsp" %>
