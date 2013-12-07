<%@ page language="java" contentType="text/html; charset=utf8"
                                       pageEncoding="utf8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<ul class="sidebar-nav">
    <li class="sidebar-brand"><a href="#">Team TransformadaZ</a></li>
    <li style="margin-top:100px;"><a href="#" onclick="createIdea();"><span class="glyphicon glyphicon-edit">&nbsp;Nova
                Ideia</span><span>&nbsp;</span></a></li>
    <li><a href="#" onclick="searchIdea()"><span class="glyphicon glyphicon-search"  style="z-index:0"></span><span
            class="glyphicon glyphicon-cloud" style="margin-left:-5px; z-index:1">&nbsp;Pesquisar
                Ideias</span><span>&nbsp;</span></a> </li>
    <li><a href="listideas.action?mode=userideas"><span class="glyphicon glyphicon-cloud">&nbsp;As Minhas
                Ideias</span><span>&nbsp;</span></a></li>
    <li><a href="listtopics.action"><span
            class="glyphicon glyphicon-list">&nbsp;Ver
                Tópicos</span><span>&nbsp;</span></a></li>
    <li><a href="listideas.action?mode=watchlist"><span class="glyphicon glyphicon-eye-open">
                Watchlist</span><span>&nbsp;</span></a></li>
    <li><a href="viewhalloffame.action"><span class="glyphicon glyphicon-fire">&nbsp;Hall
                of Fame</span><span>&nbsp;</span></a></li>
    <li><a href="transactionhistory.action"><span class="glyphicon glyphicon-list-alt">
            Transacções</span><span>&nbsp;</span></a></li>
</ul>