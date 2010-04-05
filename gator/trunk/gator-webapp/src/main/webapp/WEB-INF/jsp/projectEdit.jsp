<%@ include file="includes/top.jsp" %>

<c:choose>
  <c:when test="${project.id == 0}">
    <c:set var="action" value="Create Project" />
  </c:when>
  <c:otherwise>
    <c:set var="action" value="Edit Project" />
  </c:otherwise>
</c:choose>

<div class="crumbs">
  <span>
    <a href="<c:url value="/secure/project/list.html" />">Project Listing</a>
  </span>
  <span>&raquo;</span>
  <span>${action} <em>${project.name}</em></span>
</div>

<h1>${action}</h1>

<c:if test="${project.id > 0}">
  <div>Last Modified: <strong>${project.modifiedDate.time}</strong></div>
</c:if>

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
		<div><input type="submit" name="action" value="Update" /></div>
  </div>
  </fieldset>

  <c:if test="${project.id > 0}">
	<fieldset>
	  <legend>Categories</legend>
	  <div class="button_group">
	    <span class="button">
		    <a href="<c:url
		      value="/secure/project/${project.name}/category/add.html" />">Add Category</a>
	    </span>
	    <span class="button">
	      <a href="<c:url
	        value="/secure/project/${project.name}/category/bulk_edit.html" />">Bulk Change</a>
      </span>
	  </div>
	  <c:choose>
	    <c:when test="${not empty project.categories}">
	      <table summary="Project category listing">
	        <tr>
	          <th>Name</th>
	          <th>Level</th>
	          <th>Additivity</th>
		        <th>Appenders</th>
	          <th>Edit</th>
	          <th>Delete</th>
	        </tr>
	        <c:forEach items="${project.categories}" var="category">
	          <tr>
	            <td class="button_row">${category.name}</td>
	            <td class="button_row" align="center">${category.level}</td>
	            <td class="button_row" align="center">${category.additivity}</td>
	            <td class="button_row">
	            	<c:forEach items="${category.appenders}"
	            	 var="appender"
		             varStatus="stat">
		              <c:if test="${stat.count > 1}">, </c:if>${appender.name}
		            </c:forEach>
	            </td>
	            <td class="button_cell">
	            <span class="button">
	            <a href="<c:url
	              value="/secure/project/${project.name}/category/${category.id}/edit.html" />">Edit</a>
	            </span>
	            </td>
	            <td class="button_cell">
	            <span class="button">
	            <a href="<c:url
	              value="/secure/project/${project.name}/category/${category.id}/delete.html" />">Delete</a>
	            </span>
	            </td>
	          </tr>
	        </c:forEach>
	      </table>    
	    </c:when>
	    <c:otherwise>
	      <p>No categories defined.</p>
	    </c:otherwise>
	  </c:choose>
	</fieldset>

  <fieldset>
  <legend>Appenders</legend>
  <div class="button_group">
    <span class="button">
    <a href="<c:url
      value="/secure/project/${project.name}/appender/add.html" />">Add Appender</a>
    </span>
    <c:if test="${not empty project.appenders}">
      <span class="button">
      <a href="<c:url
        value="/secure/project/${project.name}/appender/copy.html" />">Copy Appender</a>
      </span>
    </c:if>
  </div>
  <c:choose>
    <c:when test="${not empty project.appenders}">
	    <table summary="Project appender listing">
	      <tr>
	        <th>Name</th>
	        <th>Class</th>
	        <th>Edit</th>
	        <th>Delete</th>
	      </tr>
	      <c:forEach items="${project.appenders}" var="appender">
	        <tr>
	          <td class="button_row">${appender.name}</td>
	          <td class="button_row">${appender.appenderClassName}</td>
	          <td class="button_cell">
	          <span class="button">
	          <a href="<c:url
	            value="/secure/project/${project.name}/appender/${appender.id}/edit.html" />">Edit</a>
	          </span>
	          </td>
	          <td class="button_cell">
	          <span class="button">
            <a href="<c:url
              value="/secure/project/${project.name}/appender/${appender.id}/delete.html" />">Delete</a>
            </span>
            </td>
	        </tr>
	      </c:forEach>
	    </table>    
		</c:when>
		<c:otherwise>
		  <p>No appenders defined.</p>
		</c:otherwise>
	</c:choose>
  </fieldset>

  <fieldset>
  <legend>Allowed Clients</legend>
  <div class="button_group">
    <span class="button">
    <a href="<c:url
      value="/secure/project/${project.name}/client/add.html" />">Add Client</a>
    </span>
  </div>
  <c:choose>
    <c:when test="${not empty project.clients}">
	    <table summary="Project allowed client listing">
	      <tr>
	        <th>Name/Address</th>
	        <th>Edit</th>
	        <th>Delete</th>
	      </tr>
	      <c:forEach items="${project.clients}" var="client">
	        <tr>
	          <td class="button_row">${client.name}</td>
            <td class="button_cell">
	          <span class="button">
            <a href="<c:url
              value="/secure/project/${project.name}/client/${client.id}/edit.html" />">Edit</a>
            </span>
            </td>
            <td class="button_cell">
	          <span class="button">
            <a href="<c:url
              value="/secure/project/${project.name}/client/${client.id}/delete.html" />">Delete</a>
            </span>
            </td>
	        </tr>
	      </c:forEach>
	    </table>    
		</c:when>
		<c:otherwise>
		  <p>No allowed clients defined.</p>
		</c:otherwise>
	</c:choose>
  </fieldset>

  <fieldset>
  <legend>Security Permissions</legend>
  <div class="button_group">
    <span class="button">
    <a href="<c:url
      value="/secure/project/${project.name}/perm/add.html" />">Add Permissons</a>
    </span>
  </div>
  <c:choose>
    <c:when test="${not empty project.permissions}">
      <table summary="Security permissions table">
        <tr>
          <th>Security ID</th>
          <th>Permission</th>
	        <th>Edit</th>
	        <th>Delete</th>
        </tr>
        <c:forEach items="${project.permissions}" var="perm">
          <tr>
            <td class="button_row">${perm.name}</td>
            <td class="button_row">${perm.permissions}</td>
            <td class="button_cell">
            <span class="button">
            <a href="<c:url
              value="/secure/project/${project.name}/perm/${perm.id}/edit.html" />">Edit</a>
            </span>
            </td>
            <td class="button_cell">
            <span class="button">
            <a href="<c:url
              value="/secure/project/${project.name}/perm/${perm.id}/delete.html" />">Delete</a>
            </span>
            </td>
          </tr>
        </c:forEach>
      </table>    
    </c:when>
    <c:otherwise>
      <p>No security permissions defined.</p>
    </c:otherwise>
  </c:choose>
  </fieldset>

  </c:if>
</form:form>

<%@ include file="includes/bottom.jsp" %>
