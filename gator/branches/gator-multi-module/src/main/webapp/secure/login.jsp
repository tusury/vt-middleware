<%@ include file="/WEB-INF/jsp/includes/top.jsp" %>

<h1>Login</h1>

<form method="post" action="<c:url value="/secure/login_validate" />">
  <c:if test="${param.error == 403}">
    <div id="error">Authentication failed.</div>
  </c:if>
  
  <fieldset>
  <legend>Authentication</legend>
  <div class="field">
    <div><label for="j_username">Username</label></div>
    <div><input id="j_username" name="j_username" type="text" /></div>
  </div>
  <div class="field">
    <div><label for="j_password">Password</label></div>
    <div><input id="j_password" name="j_password" type="password" /></div>
  </div>
  <div class="field">
    <div><input type="submit" name="action" value="Login" /></div>
  </div>
  </fieldset>

</form>

<%@ include file="/WEB-INF/jsp/includes/bottom.jsp" %>
