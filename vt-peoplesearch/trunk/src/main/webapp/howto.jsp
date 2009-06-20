<%@ page import="java.util.StringTokenizer" %>
<%
    StringBuffer myAddress = request.getRequestURL();
    // remove this jsp from the URL
    myAddress.setLength(myAddress.length() - request.getServletPath().length());
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
  <link rel="stylesheet" href="css/vt.css" type="text/css" media="screen" />
  <title>People Search</title>
</head>
<body>
<div id="page-wrapper">
<div id="page-header">
  <a href="index.jsp"><img src="images/logo.png" alt="People Search Logo" border="0"/></a>
</div>
<div id="content">
<p>
This service takes a query and tries to find someone in the directory who you might be looking for.<br/>
The result is returned as <a href="http://www.oasis-open.org/specs/index.php#dsmlv2">DSML</a>.<br/>
</p>
<hr/>
<p>
Use this form to try out some queries:<br/>
<form method="GET" action="<%= myAddress %>/PeopleSearch">
Search and return results as DSML
<input type="hidden" name="content-type" value="text"/>
<input type="text" name="query" size="30"/>
<input type="submit"/>
</form>
<br/>
</p>
<hr/>
<p>
A valid query looks like this:
<pre><%= myAddress %>/PeopleSearch?query=dfisher</pre>
or
<pre><%= myAddress %>/PeopleSearch?query=Daniel+Fisher</pre>
If you only want to receive certain attributes you should do this:
<pre><%= myAddress %>/PeopleSearch?query=dfisher&attrs=givenname&attrs=surname</pre>
The default content-type returned is text/xml, if you want text/plain you should do this:
<pre><%= myAddress %>/PeopleSearch?query=dfisher&content-type=text</pre>
The default result set is in DSML version 1 format.<br/>
If you want DSML version 2 you should do this:
<pre><%= myAddress %>/PeopleSearch?query=dfisher&dsml-version=2</pre>
</p>
<hr/>
<p>
Here is some sample java code for accessing this resource:
<pre>
import java.util.ArrayList;
import java.net.URL;
import java.net.URLEncoder;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

class PeopleSearch {
  public static void main(String[] args) {

    try {

      String query = null;
      ArrayList attrsArrayList = new ArrayList();

      for (int i = 0; i < args.length; i++) {
        if (args[i].equals("-h")) {
          throw new ArrayIndexOutOfBoundsException();
        } else if (args[i].equals("-q")) {
          query = args[++i];
        } else if (args[i].equals("-a")) {
          attrsArrayList.add(args[++i]);
        } else {
          attrsArrayList.add(args[i]);
        }
      }

      String[] attrs = (String[]) attrsArrayList.toArray(new String[0]);

      StringBuffer url = new StringBuffer("<%= myAddress %>/PeopleSearch");
      if (query != null) {
        url.append("?query=").append(URLEncoder.encode(query));
        if (attrs != null) {
          for (int i = 0; i < attrs.length; i++) {
            url.append("&attrs=").append(URLEncoder.encode(attrs[i]));
          }
        }
      }

      Document search = new SAXReader().read(new URL(url.toString()));
      XMLWriter writer = new XMLWriter(System.out,
                                       OutputFormat.createPrettyPrint());
      writer.write(doc);

    } catch (ArrayIndexOutOfBoundsException e) {
      System.out.println("Usage: DsmlSearch <options>");
      System.out.println("where &lt;options&gt; includes:");
      System.out.println("  -q query");
      System.out.println("  -a &lt;attributes&gt;");
      System.out.println("where &lt;attributes&gt; is:");
      System.out.println("  a space delimited list of return attributes");
      System.exit(1);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
</pre>
</p>
<hr/>
<p>
In addition to the fuzzy logic provide by the PeopleSearch servlet, there
is also a servlet which provides direct access to the LDAP.
More information is available <a href="howto_advanced.jsp">here</a>.
</p>
</div>
<div id="page-footer">
<p>&copy; 2008 Virginia Tech</p>
</div>
</div>
</body>
</html>
