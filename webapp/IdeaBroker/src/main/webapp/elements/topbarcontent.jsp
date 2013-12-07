<%@ page language="java" contentType="text/html; charset=utf8"
         pageEncoding="utf8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<div class="bs-header">
    <h1 style="margin-left: -10px;">Idea Broker</h1>

    <p> As suas ideias. O nosso mercado. </p>
</div>
<nav class="navbar navbar-default navbar-static-top" role="navigation">
    <ul class="nav nav-pills nav-justified" style="font-size: 18pt;">
        <li><a href="#"><span class="glyphicon glyphicon-user"></span>&nbsp;
            <s:property value="#session.client.username"/></a></li>
        <li><a href="#" id="coins"><span class="glyphicon glyphicon-euro"></span>&nbsp;<span
                id="currmoney"><s:property
                value="#session.client.coins"/></span> DEICoins</a></li>


        <s:if test="#session.client.facebookAccount">
        <li><a href="#">
                    <span class="glyphicon
                     glyphicon-thumbs-up">

                    <s:if test="#session.client.facebookName == null">
                        <script>doWaitForFacebookLogin();</script>
                        <div id="auth-status" style="text-align: center"><div id="auth-loggedout"><div
                                class="fb-login-button" autologoutlink="true"
                                scope="email,user_checkins,publish_actions,publish_stream,read_stream">
                            Login with Facebook</div></div>
                            <div id="auth-loggedin" style="display: none"></div>
                    </s:if>
                    <s:else>
                        <s:property value="#session.client.facebookName"/>
                    </s:else>
                </s:if>
                <s:else>
        <li><a href="#" onclick="doFacebookAssociate();">&nbsp;


                    <span class="glyphicon
                     glyphicon-thumbs-down"> Associar Facebook

                </s:else>
        </a></li>

    </ul>
</nav>