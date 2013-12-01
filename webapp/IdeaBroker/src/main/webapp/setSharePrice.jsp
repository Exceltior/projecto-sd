<%--
  Created by IntelliJ IDEA.
  User: jorl17
  Date: 24/11/13
  Time: 02:39
  To change this template use File | Settings | File Templates.
--%>
<!-- STRUTS -->
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!-- END STRUTS -->
<html>
<head>
    <title></title>
</head>
<body>
<h1 style="color: blue; text-align: center;">
    <span style="font-family:lucida sans unicode,lucida grande,sans-serif;"><strong>IdeaBroker</strong></span></h1>
<hr />
<p>
    &nbsp;</p>
<p style="text-align: center;">
    AMAZING HOMEPAGE.</p>
Set Share Price!!!


<form method="post" action="setSharePrice">

    <p><s:textfield name="price" label="Type the current shares' selling price"/></p>
    <p><button type="submit">Submit</button></p>
</form>

</body>
</html>
