<!--<jsp:useBean id="banana" scope="request" type="java.util.ArrayList"/>-->
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
    <link rel="stylesheet" type="text/css" href="css/layout.css">
    <title></title>
</head>
<body>

<!-- Header -->
<div class="top">
    <!--<div id="top-container-outer">
        <div id="top-container-inner">
            <div id="top-logo-div">-->

                <img src="<s:url value="/images/logo.jpg"/>"  alt="Idea Broker V we're so fucked" height="80px"
                     style="position:absolute; top:5px; left:6.5%;"/>

            <!-- </div>
        </div>
        <div id="top-main">-->
            <h1 style="color: blue; text-align: center; margin: 0px;">
                <span style="font-family:lucida sans unicode,lucida grande,sans-serif;">
                    <strong>IdeaBroker</strong>
                </span>
            </h1>
            <h2 style="color: lightblue; margin: 0px; text-align: center;">
                <span style="font-family:lucida sans unicode,lucida grande,sans-serif;">
                    <strong>IdeaBroker</strong>
                </span>
            </h2>
        <!--</div>
    </div>-->
    <hr  style="width:90%" />
</div>

<!--<div class="left_blank"></div>-->
<!-- Sidebar -->
<div id="container-outer">
    <div id="container-inner">
        <div id="sidebar">
            <div id="headercontent">
                <ul>
                    <li>
                        <a href="http://example.com/page2">A Link</a>
                    </li>
                    <li>
                        <a href="http://example.com/page3">Another Link</a>
                    </li>
                </ul>
                </div>
        </div>
        <div id="content">
            <div id="content-real">
                <!--<nav>
                    <a href="#">About</a>
                    <a href="#">Reports</a>
                    <a href="#">Documents</a>
                    <a href="#">Checklists</a>
                    <a href="#">License Tools</a>
                    <a href="#">Presentations</a>
                    <a href="#">Software Releases</a>
                </nav>-->
                <TABLE BORDER="1" CELLPADDING="0" CELLSPACING="2" WIDTH="100%" style="background-color: #92bcff" >
                    <TR>
                        <TD WIDTH="33%" style="vertical-align:middle;height:50px"><h4>Hakuna</h4></TD>
                        <TD WIDTH="33%" style="vertical-align:middle;height:50px"><img src="<s:url value="/images/notification.png"/>"
                                             alt="X MERDA FIXME" height="20px" align="left" /><h3
                                style="color:black; margin: 0px; white-space: nowrap">250 DEI Coins</h3></TD>
                        <TD WIDTH="33%" style="vertical-align:middle;height:50px">
                            <img src="<s:url value="/images/notification.png"/>"
                                             alt="X NOTIFICATIONS FIXME" height="20px" align="left"
                                             style="border-right:hidden;"/>
                            <h3 style="color:palevioletred; margin: 0px; white-space: nowrap">5 Novas
                                Mensagens</h3></TD>
                    </TR>
                </TABLE>
                <br />
            <!-- Page body -->
            <div class="idea">
                <div
                        style="display: block; background-color: cornflowerblue; border-radius: 30px 30px 0 0;
                        height:40px; border-bottom: solid 1px; black">
                    <div>
                    <div style="padding-left: 30px; padding-top:10px; float: left">Ideia 1</div>
                    <div style="padding-right: 30px;padding-top:10px;float: right">Market Value: 50
                        DEICoins/share</div>
                    </div>
                </div>
                    <div
                            style="clear: both; display: block; background-color: #b8cded; border: solid 1px
                            black; height:50px" >
                            <div style="padding-left: 30px; padding-top:10px; float: left">
                                <a class="idea-button" href="#">Add to Watchlist</a>
                                <a class="idea-button" href="#">Buy Shares</a>
                            </div>
                        <div style="padding-right: 30px; padding-top:10px; float: right">
                            <a class="idea-shareinfo" href="#">% Owned: 100 000 shares</a>
                        </div>
                    </div>


                <div class="idea-body"><p>
                    IDEA BODYIDEA BODYIDEA BODYIDEA BODYIDEA BODYIDEA BODYIDEA BODY IDEA BODYIDEA BODY IDEA BODYIDEA
                    BODYIDEA BODYIDEA BODYIDEA BODYIDEA BODYIDEA BODYIDEA BODYIDEA BODYIDEA BODYIDEA BODY IDEA BODY IDEA BODY
                </p></div>
                <div>
                    tags tags tags tags tags
                </div>
            </div>

                <br />



            <p>
                &nbsp;</p>
            <table align="center" border="1" cellpadding="1" cellspacing="1" style="width: 500px; background: url('<s:url
                    value="images/bg_gradient_tiny.png"/>');">
                <tbody>
                <tr>
                    <td>
                        <h2 style="text-align: center;">
                            <em>LOGIN</em></h2>
                    </td>
                </tr>
                </tbody>
            </table>
            <div style="text-align: center;">
                            <span  style="text-align: center;">


                                HELLO WORLD!!!<br />
                                <s:iterator var="i" step="1" value="banana">
                                    <s:url action="teste2" var="urlTag">
                                        <s:param name="umaVariavel2" value="top.a" />
                                    </s:url>

                                    <s:a href="%{urlTag}">clica-me</s:a><br />
                                    <s:property value="a" /><br />
                                    <s:property value="b" /><br />
                                </s:iterator>
                                <s:property value="%{#request.aha}"/><br />


                            </span>
            </div>
        </div>
        </div>
    </div>
</div>
</body>
</html>
