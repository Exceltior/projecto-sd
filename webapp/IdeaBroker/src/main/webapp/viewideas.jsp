<!-- STRUTS -->
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="c"
           uri="http://java.sun.com/jsp/jstl/core" %>
<!-- END STRUTS -->

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <link rel="stylesheet" type="text/css" href="css/sidebar.css">
    <title></title>
</head>
<body>
<div id="header_container">
    <div id="header">
        <h1 style="color: blue; text-align: center;">
            <span style="font-family:lucida sans unicode,lucida grande,sans-serif;"><strong>IdeaBroker</strong></span></h1>
        <hr />
    </div>
</div>

<div id="nav">
    <ul>
        <li><a href="http://example.com/page2">A Link</a></li>
        <li><a href="http://example.com/page3">Another Link</a></li>
    </ul>
</div>
<div id="content_header">
    <p> Here's some stuff </p>



    <p>
        &nbsp;</p>
    <table align="center" border="1" cellpadding="1" cellspacing="1" style="width: 500px; background: url('<s:url
            value="images/bg_gradient_tiny.png"/>');">
        <tbody>
        <tr>
            <td>
                <h2 style="text-align: center;">
                    <em>User Ideas</em></h2>
            </td>
        </tr>
        </tbody>
    </table>
    <div style="text-align: center;">
        <span  style="text-align: center;">

            <s:iterator var="i" step="1" value="ideasList">
                <s:url action="viewidea" var="urlTag">
                    <s:param name="iid" value="top.id" />
                </s:url>
                <s:a href="%{urlTag}">Ideia <s:property value="iid" /></s:a><br />

                <s:property value="id" /><br />
                <s:property value="title" /><br />

                <s:if test="userIdeas == true">

                    <s:url action="goSetSharePrice" var="urlTag">
                        <s:param name="iid" value="top.id" />
                    </s:url>
                    <s:a href="%{urlTag}">Set Idea's Shares Selling Prices <s:property value="iid" /></s:a><br />
                </s:if>
            </s:iterator>

        </span>
    </div>
</div>
</body>
</html>
