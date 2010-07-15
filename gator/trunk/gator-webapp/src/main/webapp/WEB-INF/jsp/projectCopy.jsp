<%@ include file="includes/top.jsp" %>
<%@ include file="includes/user.jsp" %>

<div id="crumbs">
  <ul>
    <li class="first"><a class="navlink" href="<c:url value="/secure/project/list.html" />">Project Listing</a></li>
    <li>Copy Project</li>
  </ul>
</div>
<div class="clear"></div>

<div id="title">Copy Project</div>

<form:form method="post" commandName="spec">
  <fieldset>
  <legend>Create New Project from Existing</legend>

  <spring:hasBindErrors name="spec">
    <div id="validation-summary"><spring:message code="error.validationSummary" /></div>
  </spring:hasBindErrors>

  <div class="field">
    <div><label for="sourceId">Project to Copy</label></div>
    <div><form:select id="sourceId" path="sourceId"
      items="${projects}" itemValue="id" itemLabel="name"/></div>
  </div>
  <div class="field">
    <div><label for="name">New Project Name</label></div>
    <form:errors cssClass="field-error" path="name" element="div" />
    <div><form:input id="name" path="name" size="30" /></div>
  </div>
  <div class="field">
    <input class="button" type="submit" name="action" value="Copy" />
  </div>
  </fieldset>

</form:form>

<%@ include file="includes/bottom.jsp" %>
