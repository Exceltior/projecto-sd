<%@ page language="java" contentType="text/html; charset=utf8"
         pageEncoding="utf8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<div class="bs-header">
    <h1  style="margin-left: -10px;">Idea Broker</h1>
    <p> As suas ideias. O nosso mercado. </p>
</div>
<nav class="navbar navbar-default navbar-static-top" role="navigation">
    <ul class="nav nav-pills nav-justified"  style="font-size: 18pt;">
        <li><a href="#"><span class="glyphicon glyphicon-user"></span>&nbsp;
            <s:property value="#session.client.username"/></a></li>
        <li><a href="#" id="coins"><span class="glyphicon glyphicon-euro"></span>&nbsp;<span
                id="currmoney"><s:property
                value="#session.client.coins"/></span> DEICoins</a></li>
        <li><a href="#" id="numNotifications"><span class="glyphicon
                     glyphicon-envelope"></span>&nbsp;<s:property value="#session.client.numNotifications"/> Novas
            Mensagens</a></li>
    </ul>
</nav>