<%@ include file="includes/top.jsp" %>

<div class="crumbs">
  <span>
    <a href="<c:url value="/secure/list.html" />">Project Listing</a>
  </span>
  <span>&raquo;</span>
  <span>Watch Logs</span>
</div>

<h1>Watch Logs for <em>${project.name}</em></h1>

<form:form method="post" commandName="watchConfig">
  <form:errors id="error" path="*" element="div" />
  
  <fieldset>
  <legend>Watch Configuration</legend>
  <div class="field">
    <div>
      <label for="pattern">Layout Conversion Pattern</label>
    </div>
    <div><form:input id="pattern" path="layoutConversionPattern" size="100" /></div>
  </div>
  <div class="field">
    <div><label for="categories">Enabled Categories</label></div>
    <div class="note">Display logging events in following categories.</div>
    <div style="margin:5px">
      <span><a href="javascript:select('categoryIds', true)">Select All</a><span>
      <span>|</span>
      <span><a href="javascript:select('categoryIds', false)">Select None</a><span>
    </div>
    <div class="checkboxes">
      <form:checkboxes id="categoryIds" path="categoryIds"
       items="${project.categories}" itemValue="id" itemLabel="name"
       element="div" />
    </div>
  </div>
  <div class="field">
    <div><input type="submit" name="submit" value="Watch Now" /></div>
  </div>
  </fieldset>

</form:form>

<%@ include file="includes/bottom.jsp" %>