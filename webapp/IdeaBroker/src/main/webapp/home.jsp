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
<jsp:useBean id="client" scope="session" class="actions.model.Client" />
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
Welcome, user <s:property value="%{#session.client.uid}"/>!


<a href="<s:url action="GoCreateIdea" />" >
    Create Idea
</a><br />
<a href="<s:url action="viewuserideas" />" >
    View User Ideas
</a><br />
<a href="<s:url action="viewuserwatchlist" />" >
    View User Watchlist
</a><br />
<a href="<s:url action="listtopics" />" >
    Check Topics
</a><br />
<a href="<s:url action="searchtopic" />" >
    Search Topic
</a><br />
<a href="<s:url action="gosearchidea" />" >
    Search Idea
</a><br />


<!-- Display main menu -->

<!-- FIXME: METER WALL COM IDEIAS? -->
</body>
</html>
