<%@ include file="includes/top.jsp" %>
<%@ include file="includes/user.jsp" %>

<c:choose>
  <c:when test="${project.id == 0}">
    <c:set var="action" value="Create Project" />
  </c:when>
  <c:otherwise>
    <c:set var="action" value="Edit Project" />
  </c:otherwise>
</c:choose>

<div id="crumbs">
  <ul>
    <li class="first"><a class="navlink" href="<c:url value="/secure/project/list.html" />">Project Listing</a></li>
    <li>Edit Project</li>
  </ul>
</div>
<div class="clear"></div>

<div id="title">Edit Project</div>

<div>Last Modified: <strong>${project.modifiedDate.time}</strong></div>

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
			<input class="button" type="submit" name="action" value="Update" />
	  </div>
  </fieldset>
 
  <div class="infobox">
    <p>Jump to configuration section:
      <span class="navlink_group">
	      <a class="navlink" href="#appender">Appenders</a>
	      <a class="navlink" href="#client">Clients</a>
	      <a class="navlink" href="#perm">Permissions</a>
	    </span>
    </p>
  </div>

  <h2><a name="category">Categories</a></h2>
	<fieldset>
	  <legend>Edit Categories</legend>
	  <div class="button_group">
	    <a class="button" href="<c:url
	      value="/secure/project/${project.name}/category/add.html" />">Add Category</a>
      <a class="button" href="<c:url
        value="/secure/project/${project.name}/category/bulk_edit.html" />">Bulk Change</a>
	  </div>
	  <c:choose>
	    <c:when test="${not empty project.categories}">
	      <table summary="Project category listing" class="smaller">
	        <tr>
	          <th>Name</th>
	          <th>Level</th>
	          <th>Additivity</th>
		        <th>Appenders</th>
	          <th>Delete</th>
	        </tr>
	        <c:forEach items="${project.categories}" var="category">
	          <tr class="button_row">
	            <td>
		            <a class="navlink" href="<c:url
                  value="/secure/project/${project.name}/category/${category.id}/edit.html" />">${category.name}</a>
              </td>
              <td>${category.level}</td>
	            <td>${category.additivity}</td>
	            <td>
	              <c:if test="${category.allowSocketAppender}">SOCKET<c:if test="${not empty category.appenders}">,</c:if></c:if>
	            	<c:forEach items="${category.appenders}"
	            	  var="appender"
		              varStatus="stat"><c:if test="${stat.count > 1}">,</c:if>
		              <a class="navlink" href="<c:url
	                  value="/secure/project/${project.name}/appender/${appender.id}/edit.html" />">${appender.name}</a></c:forEach>
	            </td>
	            <td class="button_cell">
		            <a class="button" href="<c:url
		              value="/secure/project/${project.name}/category/${category.id}/delete.html" />">Delete</a>
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

  <h2><a name="appender">Appenders</a></h2>
  <fieldset>
	  <legend>Edit Appenders</legend>
	  <div class="button_group">
	    <a class="button" href="<c:url
	      value="/secure/project/${project.name}/appender/add.html" />">Add Appender</a>
	    <a class="button" href="<c:url
	      value="/secure/project/${project.name}/appender/bulk_edit.html" />">Bulk Change</a>
	    <c:if test="${not empty project.appenders}">
	      <a class="button" href="<c:url
	        value="/secure/project/${project.name}/appender/copy.html" />">Copy Appender</a>
	    </c:if>
	  </div>
	  <c:choose>
	    <c:when test="${not empty project.appenders}">
		    <table summary="Project appender listing" class="smaller">
		      <tr>
		        <th>Name</th>
		        <th>Class</th>
		        <th>Delete</th>
		      </tr>
		      <c:forEach items="${project.appenders}" var="appender">
		        <tr class="button_row">
		          <td>
	              <a class="navlink" href="<c:url
		              value="/secure/project/${project.name}/appender/${appender.id}/edit.html" />">${appender.name}</a>
		          </td>
		          <td>${appender.appenderClassName}</td>
		          <td class="button_cell">
		            <a class="button" href="<c:url
		              value="/secure/project/${project.name}/appender/${appender.id}/delete.html" />">Delete</a>
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

  <h2><a name="client">Clients</a></h2>
  <fieldset>
	  <legend>Allowed Clients</legend>
	  <div class="button_group">
	    <a class="button" href="<c:url
	      value="/secure/project/${project.name}/client/add.html" />">Add Client</a>
	  </div>
	  <c:choose>
	    <c:when test="${not empty project.clients}">
		    <table summary="Project allowed client listing">
		      <tr>
		        <th>Name/Address</th>
		        <th>Delete</th>
		      </tr>
		      <c:forEach items="${project.clients}" var="client">
		        <tr class="button_row">
		          <td>
		             <a class="navlink" href="<c:url
	                value="/secure/project/${project.name}/client/${client.id}/edit.html" />">${client.name}</a>
	            </td>
	            <td class="button_cell">
		            <a class="button" href="<c:url
		              value="/secure/project/${project.name}/client/${client.id}/delete.html" />">Delete</a>
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

  <h2><a name="perm">Security Permissions</a></h2>
  <fieldset>
	  <legend>Edit Permissions</legend>
	  <div class="button_group">
	    <a class="button" href="<c:url
	      value="/secure/project/${project.name}/perm/add.html" />">Add Permissons</a>
	  </div>
	  <c:choose>
	    <c:when test="${not empty project.permissions}">
	      <table summary="Security permissions table">
	        <tr>
	          <th>User/Role Name</th>
	          <th>Permission</th>
		        <th>Delete</th>
	        </tr>
	        <c:forEach items="${project.permissions}" var="perm">
	          <tr class="button_row">
		          <td>
		             <a class="navlink" href="<c:url
	                value="/secure/project/${project.name}/perm/${perm.id}/edit.html" />">${perm.name}</a>
	            </td>
	            <td>${perm.permissions}</td>
	            <td class="button_cell">
		            <a class="button" href="<c:url
		              value="/secure/project/${project.name}/perm/${perm.id}/delete.html" />">Delete</a>
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

</form:form>

<%@ include file="includes/bottom.jsp" %>
