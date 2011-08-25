<%@ include file="includes/top.jsp" %>
<%@ include file="includes/user.jsp" %>

<c:choose>
  <c:when test="${perm['new']}">
    <c:set var="action" value="Add Permissions" />
  </c:when>
  <c:otherwise>
    <c:set var="action" value="Edit Permissions" />
  </c:otherwise>
</c:choose>

<div id="crumbs">
  <ul>
    <li class="first"><a class="navlink" href="<c:url value="/secure/project/list.html" />">Project Listing</a></li>
    <li><a class="navlink" href="<c:url value="/secure/project/${perm.project.name}/edit.html" />">Edit <em>${perm.project.name}</em></a></li>
    <li>${action}</li>
  </ul>
</div>
<div class="clear"></div>

<div id="title">${action}</div>

<form:form method="post" commandName="perm">
  <fieldset>
  <legend>Security Permissions</legend>

  <spring:hasBindErrors name="perm">
    <div id="validation-summary"><spring:message code="error.validationSummary" /></div>
  </spring:hasBindErrors>

  <div class="field">
    <div><label for="name">Security Identifier</label></div>
    <form:errors cssClass="field-error" path="name" element="div" />
    <div class="note">Examples: bob, ROLE_IT-STAFF</div>
    <div><form:input id="name" path="name" size="50" /></div>
  </div>
  <div class="field">
    <div><label for="permissions">Permissions</label></div>
    <div class="note">
	    <ul>
	      <li>r - Read permission</li>
	      <li>w - Write/Edit permission</li>
	      <li>d - Delete permission</li>
	    </ul>
	    <div>Examples: rwd, r, rw</div>
	    <div>Note that permissions only apply to whole projects.</div>
    </div>
    <form:errors cssClass="field-error" path="permissions" element="div" />
    <div><form:input id="permissions" path="permissions" size="10" /></div>
  </div>
  <div class="field">
		<input class="button" type="submit" name="action" value="Update" />
  </div>
  </fieldset>

</form:form>

<%@ include file="includes/bottom.jsp" %>
