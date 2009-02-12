<%@ include file="includes/top.jsp" %>

<c:choose>
  <c:when test="${wrapper.category.id == 0}">
    <c:set var="action" value="Add Category" />
  </c:when>
  <c:otherwise>
    <c:set var="action" value="Edit Category" />
  </c:otherwise>
</c:choose>

<div class="crumbs">
  <span>
    <a href="<c:url value="/auth/list.html" />">Project Listing</a>
  </span>
  <span>&raquo;</span>
  <span>
    <a href="<c:url value="/auth/project/${project.name}/edit.html" />">Edit <em>${project.name}</em></a>
  </span>
  <span>&raquo;</span>
  <span>${action}</span>
</div>

<h1>${action}</h1>

<form:form method="post" commandName="wrapper">
  <form:errors id="error" path="*" element="div" />
  
  <fieldset>
  <legend>Category Configuration</legend>
  <div class="field">
    <div>
      <label for="name">Category Name</label>
      <span class="note">Special name "root" indicates root category.</span>
    </div>
    <div><form:input id="name" path="category.name" size="50" /></div>
  </div>
  <div class="field">
    <div><label for="level">Log Level</label></div>
    <div><form:select id="level" path="category.level" items="${logLevels}" /></div>
  </div>
  <div class="field">
    <div><label for="appenderIds">Category Appenders</label>
			<span class="note">Send logging events to these appenders.</div>
    <div class="checkboxes">
	    <form:checkboxes id="appenderIds" path="appenderIds"
	     items="${availableAppenders}" itemValue="id" itemLabel="name"
	     element="div" />
	  </div>
  </div>
  <div class="field">
		<div><input type="submit" name="action" value="Update" /></div>
  </div>
  </fieldset>

</form:form>

<%@ include file="includes/bottom.jsp" %>
