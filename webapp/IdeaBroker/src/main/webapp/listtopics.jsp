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
    <link href="bootstrap-3.0.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="css/login.css" rel="stylesheet">
    <link href="css/clouds.css" rel="stylesheet">
    <link href="css/simple-sidebar.css" rel="stylesheet">
    <title></title>
</head>
<body>
<div style="position:relative">
    <div id="clouds" style="position:relative">
        <div class="cloud x1"></div>
        <!-- Time for multiple clouds to dance around -->
        <div class="cloud x2"></div>
        <div class="cloud x3"></div>
        <div class="cloud x4"></div>
        <div class="cloud x5"></div>
</div>
        <div style="position:absolute; top:0; left:0">
<div id="wrapper">

    <!-- Sidebar -->
    <div id="sidebar-wrapper">
        <ul class="sidebar-nav">
            <li class="sidebar-brand"><a href="#">Start Bootstrap</a></li>
            <li><a href="#">Dashboard</a></li>
            <li><a href="#">Shortcuts</a></li>
            <li><a href="#">Overview</a></li>
            <li><a href="#">Events</a></li>
            <li><a href="#">About</a></li>
            <li><a href="#">Services</a></li>
            <li><a href="#">Contact</a></li>
        </ul>
    </div>

    <!-- Page content -->
    <div id="page-content-wrapper">
        <div class="content-header">
            <h1>
                <a id="menu-toggle" href="#" class="btn btn-default"><i class="icon-reorder"></i></a>
                Simple Sidebar
            </h1>
        </div>
        <!-- Keep all page content within the page-content inset div! -->
        <div class="page-content inset">
            <div class="row">
                <div class="col-md-12">
                    <s:iterator var="i" step="1" value="topics">
                        <s:url action="viewtopic" var="urlTag">
                            <s:param name="tid" value="top.id" />
                        </s:url>

                        <s:a href="%{urlTag}">Topico <s:property value="id" /></s:a><br />
                        <s:property value="id" /><br />
                        <s:property value="title" /><br />
                    </s:iterator>
                </div>
            </div>
        </div>
    </div>

</div>
            </div>
    </div>
<script src="jquery.js"></script>
<script src="bootstrap-3.0.2/dist/js/bootstrap.min.js"></script>
</body>
</html>
