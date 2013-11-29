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
Welcome, user <s:property value="%{#session.client.uid}" />!



<form method="post" action="createIdea">
    <p>
        <s:textfield name="title" label="Type the idea's title"/>
    </p>

    <p>
        <s:textfield name="body" label="Type the idea's body"/>
    </p>
    <p>
        <s:textfield name="topicsList" label="Type the idea's topics. Separate with \";\" "/>
    </p>
    <p>
        <s:textfield name="moneyInvested" label="Insert the amount of DEI Coins you want to invest"/>
    </p>
    <p>
        <s:file name="filePath" label="Select the file you want to attach" accept="text/*" />
    </p>
    <p>
        <button type="submit">Submit</button>
    </p>
</form>

<!-- Display main menu -->

<!-- FIXME: METER WALL COM IDEIAS? -->
</body>
</html>
