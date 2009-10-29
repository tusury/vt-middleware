<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
  <link rel="stylesheet" href="css/vt.css" type="text/css" media="screen" />
  <title>People Search</title>
  <script language="javascript" type="text/javascript">
  function doFocus(formNum, elementNum) {
    if (document.forms.length > 0) {
      self.focus();
      document.forms[formNum].elements[elementNum].focus();
    }
  }
  </script>
</head>
<%
    String user = request.getParameter("user");
    if (user == null) {
      user = "";
    }
    String url = request.getParameter("url");
    if (url == null) {
      url = "";
    }
%>
<body onload="doFocus(0,0);">
<div id="page-wrapper">
<div id="page-header">
  <a href="index.jsp"><img src="images/logo.png" alt="People Search Logo" border="0"/></a>
</div>
<div id="content">
<form method="post" action="Login">
<table width="325" border="0" cellspacing="0" cellpadding="4" align="center">
<tr>
  <td colspan="2" align="center"><strong>People Search Login</strong><br/></td>
</tr>
<% String error = request.getParameter("error"); %>
<% if (error != null) { %>
<tr>
  <td colspan="2" align="center"><font color="#FF0000"><%= error %></font><br/></td>
</tr>
<% } %>
<tr>
  <td width="75" align="right">User:<br/></td>
  <td width="250"><input type="text" name="user" value="<%= user %>" style="width:200px;" tabindex="1"/></td>
</tr>
<tr>
  <td width="75" align="right">Password:<br/></td>
  <td width="250"><input type="password" name="credential" style="width:200px;" tabindex="2"/></td>
</tr>
<tr>
  <td width="75" align="right">&nbsp;<br/></td>
  <td width="250"><input type="submit" name="login" value="Login" style="width:100px;" tabindex="3"/><br/></td>
</tr>
</table>
<input type="hidden" name="url" value="<%= url %>"/>
</form>
</div>
<div id="page-footer">
<p>&copy; 2008 Virginia Tech</p>
</div>
</div>
</body>
</html>
