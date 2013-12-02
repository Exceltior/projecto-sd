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
    <link href="css/banner.css" rel="stylesheet">
    <link href="css/bootstrap-dialog.css" rel="stylesheet">
    <!--<link href="css/3dbtn.css" rel="stylesheet">-->
    <title></title>
    <script type="text/javascript">
        function addToWatchlist(id){
            btn = "addtowatchlistbtn"+id;
            tags = "ideatags"+id;
            var formData = {iid:id}; //Array
            $.getJSON('addtowatchlist.action', formData,function(data) {

                if ( data.success ) {
                    $('#'+btn).remove();
                    $('#'+tags).html($('#'+tags).html()+'<span class="label label-success" id="watchlistlabel'+id+'"><span class="glyphicon glyphicon-eye-open"></span>Na Watchlist <a href="#" type="button" onclick="removeFromWatchlist('+id+');"><span class="glyphicon glyphicon-remove"></span></a> </span>');
                } else {
                    alert("Server Internal Error...RMI is probably down!");
                }
                return true;
            });
        }

        function removeFromWatchlist(id) {
            btns = "buttons"+id;
            btn = "addtowatchlistbtn"+id;
            tags = "ideatags"+id;
            label = "watchlistlabel"+id;
            var formData = {iid:id}; //Array
            $.getJSON('removefromwatchlist.action', formData,function(data) {

                if ( data.success ) {
                    $('#'+label).remove();
                    $('#'+btns).html($('#'+btns).html()+'<a href="#" type="button" class="btn btn-success btn-sm" id="'+btn+'" onclick="addToWatchlist('+id+');"> <span class="glyphicon glyphicon-eye-open"></span> Adicionar à Watchlist </a>');
                } else {
                    alert("Server Internal Error...RMI is probably down!");
                }
                return true;
            });
        }

        function isValidPositiveNum(input) {
            return !isNaN(input) && parseFloat(input) > 0;
        }

        function isValidPositiveInt(input) {
            return !isNaN(input) && parseInt(input) > 0;
        }

        function sharePriceChanged(id) {
            btn="btnsellingprice"+id;
            textarea="sellingprice"+id;
            text = $('#'+textarea);
            button = $('#'+btn);
            //$('#'+btn).addClass('btn-success').removeClass('btn-info');

            if ( isValidPositiveNum(text.val()) ) {
                text.removeAttr('title');
                text.removeAttr('title');
                button.addClass('btn-info').removeClass('btn-success').removeClass('btn-error');
                button.html('<span class="glyphicon glyphicon-share-alt"></span>');
                button.attr('title', 'Confirmar Alteração de Preço');
                button.off('click').on('click',{id:id},setSharePrice);
                text.css('background-color','skyblue');
                text.css('color','black');
            } else {
                text.attr('title', 'Preço inválido!');
                button.attr('title', 'Preço inválido!');
                button.addClass('btn-danger').removeClass('btn-success').removeClass('btn-info');
                button.html('<span class="glyphicon glyphicon-remove"></span>');
                text.css('background-color','#d2322d');
                text.css('color','white');
            }
        }

        function setSharePrice(event) {
            id = event.data.id;
            btn="btnsellingprice"+id;
            textarea="sellingprice"+id;
            button = $('#'+btn);
            text = $('#'+textarea);

            var formData = {iid:id,price:text.val()}; //Array
            $.getJSON('setshareprice.action', formData,function(data) {
                if ( data.success ) {
                    button.addClass('btn-success').removeClass('btn-info').removeClass('btn-error');;
                    button.html('<span class="glyphicon glyphicon-ok-sign"></span>');
                    button.removeAttr('title');
                    text.removeAttr('title');
                    button.off('click');
                    text.css('background-color','white');
                    text.css('color','black');
                } else {
                    alert("Server Internal Error...RMI is probably down!");
                }
                return true;
            });
        }

        function getUserMoney() {
            return parseFloat($('#currmoney').text());
        }

        function getMaxSharesForIdea(id) {
            return 100000;
        }

        function getNumSharesForIdea(id) {
            var numshareslabel = $('#numshares'+id);
            if ( numshareslabel.length != 0)
                return numshareslabel.text();
            else
                return 0;
        }

        gClosedialog = null;

        function onMaxWillingToBuyChanged() {
            var m = $('#maxpershareinput');
            var modalsubmitbutton = $('#modalsubmitbutton');
            if (m.val() == '' || m.val() == 0.0 || !isValidPositiveNum(m.val())) {
                modalsubmitbutton.prop('disabled', true);
                return;
            }

            var maxwillingtobuy = parseFloat(m.val());
            var currentmoney = getUserMoney();
            //console.log('max: '+maxwillingtobuy+', money: '+currentmoney);



            if ( maxwillingtobuy > currentmoney ) {
                //Can't sell
                m.val(currentmoney);
                modalsubmitbutton.prop('disabled', true);
            } else {
                //Can sell
                modalsubmitbutton.prop('disabled', false);
            }
        }

        function onNumSharesWantChanged(id) {
            var maxSharesAvail=getMaxSharesForIdea(id);
            var m = $('#numshareswant');
            var modalsubmitbutton = $('#modalsubmitbutton');
            if (m.val() == '' || m.val() == 0 || !isValidPositiveInt(m.val())) {
                modalsubmitbutton.prop('disabled', true);
                return;
            }

            var currentSharesWant = parseInt(m.val());


            if ( maxSharesAvail < currentSharesWant ) {
                //Can't sell
                m.val(maxSharesAvail);
                modalsubmitbutton.prop('disabled', true);
            } else {
                //Can sell
                modalsubmitbutton.prop('disabled', false);
            }
        }

        function doBuyShares(id) {
            var maxPerShare   = $('#maxpershareinput').val();
            var numSharesWant = $('#numshareswant').val();
            var wantToQueue   = $('#addtoqueue').prop('checked');

            var formData = {iid:id,
                            maxPricePerShare:maxPerShare,
                            targetNumShares:numSharesWant,
                            addToQueueOnFailure:wantToQueue};
            /*
            $.getJSON('buyshares.action', formData,function(data) {
                if ( data.success ) {
                    if ( data.result == 'OK' ) {
                        // Went fine
                    } else if ( data.result == 'QUEUED.NOMOREMONEY' ) {
                        // Request got queued because we ran out of money
                    } else if ( data.result == 'QUEUED.NOMORESHARES' ) {
                        // Request got queued because there are no more shares
                    }
                } else {
                    alert("Server Internal Error...RMI is probably down!");
                }
                return true;
            });*/
        }

        function buyShares(id) {
            var currentnumshares = getNumSharesForIdea(id);
            if ( currentnumshares == 0) currentnumshares = 1;
            var currentmoney = getUserMoney();


            var numsharesarea =
                    '<div class="input-append"><span class="glyphicon glyphicon-chevron-right"></span>&nbsp; Número de shares desejadas:&nbsp;<input name="text" id="numshareswant" value="'+currentnumshares+'" style="width:125px;" onkeyup="onNumSharesWantChanged(id);" /></div>';
            var maxpersharearea =
                    '<div class="input-append"><span class="glyphicon glyphicon-chevron-right"></span>&nbsp; Máximo por share:&nbsp;<input name="text" id="maxpershareinput" value="'+currentmoney+'" style="width:125px;" onkeyup="onMaxWillingToBuyChanged();" /> DEICoins</div>';

            var modalcheckbox =
                    '<div class="input-append"><span class="glyphicon glyphicon-chevron-right"></span>&nbsp;<input type="checkbox" id="addtoqueue" value="true" > Colocar pedido na fila se não for possível satisfazer</input></div>';

            var message = function(dialogRef){
                var $message =
                        $("<div style='font-size:16pt'>Começou com <span style='color: #6fc65d'>"+currentnumshares+"</span> shares</div>");
                $message.append(numsharesarea).append($('<div>&nbsp;</div>')).append($(maxpersharearea)).append($(modalcheckbox));

                return $message;
            }
            var dialog = new BootstrapDialog({
                size: BootstrapDialog.SIZE_LARGE,
                message: message,
                closable:true
            });
            gClosedialog = dialog;

            var button =
                    '<button class="btn btn-primary btn-lg" id="modalsubmitbutton" onclick="doBuyShares(id);"><span class="glyphicon glyphicon-cloud"></span><span style="margin:-9px; color: #dbd02b" class="glyphicon glyphicon-euro"></span> &nbsp; &nbsp;Comprar Shares</button>';
            var closebutton
                    = '<button class="btn btn-default btn-lg" onclick="gClosedialog.close();">Cancelar</button>';


            dialog.realize();

            dialog.getModalHeader().html('<div' +
                    ' class="bootstrap-dialog-title">Comprar Shares</div><div class="bootstrap-dialog-close-button" style="display: block;"><button class="close"  onclick="gClosedialog.close();"><span class="glyphicon glyphicon-remove"></span></button></div>');

            dialog.getModalFooter().html(button+closebutton);
            dialog.open();

            /**
            * TODO: Handle the click on the button with AJAX (new function) and possibly open up a new dialog
             * We also want to update these idea's fields explicitly...that is we want o add the number of shares label
             * (or update it) and the price at which to sell them label. We should probably always add them, just make
             * them invisible...
             */
        }
    </script>

</head>
<body>
<div id="wrapper">

    <!-- Sidebar -->
    <div id="sidebar-wrapper">
        <ul class="sidebar-nav">
            <li class="sidebar-brand"><a href="#">Team TransformadaZ</a></li>
            <li style="margin-top:100px;"><a
                    href="#"><span class="glyphicon glyphicon-home">&nbsp;Home</span><span style="color:black">
                _</span></a></li>
            <li style="background-color: black"><a href="listtopics.action"><span
                    class="glyphicon glyphicon-list">&nbsp;Ver
                Tópicos</span><span style="color:black">_</span></a></li>
            <li><a
                    href="#"><span class="glyphicon glyphicon-eye-open">&nbsp;Watchlist</span><span
                    style="color:black">
                _</span></a></li>
            <li><a href="#"><span class="glyphicon glyphicon-cloud">&nbsp;As Minhas Ideias</span><span style="color:black">_</span></a></li>
            <li><a href="#"><span class="glyphicon glyphicon-bell">&nbsp;Mensagens</span><span style="color:black">_</span></a></li>
            <li><a href="#"><span class="glyphicon glyphicon-fire">&nbsp;Hall of Fame</span><span style="color:black">
                _</span></a></li>
            <li><a href="#"><span class="glyphicon glyphicon-search">&nbsp;Pesquisar Tópicos</span><span
                    style="color:black">
                _</span></a></li>
            <li><a href="#"><span class="glyphicon glyphicon-search"  style="z-index:0"></span><span
                    class="glyphicon glyphicon-cloud" style="margin-left:-5px; z-index:1">&nbsp;Pesquisar
                Ideias</span><span
                    style="color:black">
                _</span></a> </li>

            <s:if test="client.adminStatus">
                <li><a href="#"><span class="glyphicon glyphicon-wrench">&nbsp;Painel de Administrador</span><span
                        style="color:black">
                _</span></a></li>
            </s:if>
        </ul>
    </div>

    <!-- Page content -->
    <div id="page-content-wrapper">
        <div class="content-header">
            <div class="bs-header">
                <h1  style="margin-left: -10px;">Idea Broker</h1>
                <p> Your ideas. Our market. </p>
            </div>
            <nav class="navbar navbar-default navbar-static-top" role="navigation">
                <ul class="nav nav-pills nav-justified"  style="font-size: 18pt;">
                    <li><a href="#"><span class="glyphicon glyphicon-user"></span>&nbsp;
                        <s:property value="%{#session.client.username}"/></a></li>
                    <li><a href="#" id="coins"><span class="glyphicon glyphicon-euro"></span>&nbsp;<span
                            id="currmoney"><s:property
                            value="%{#session.client.coins}"/></span> DEICoins</a></li>
                    <li><a href="#" id="numNotifications"><span class="glyphicon
                     glyphicon-envelope"></span>&nbsp;<s:property value="%{#session.client.numNotifictions}"/> Novas
                        Mensagens</a></li>
                </ul>
            </nav>
        </div>
        <!-- Keep all page content within the page-content inset div! -->
        <div class="page-content inset" style=" margin: -20px">
            <div style="position:relative">
                <div id="clouds" style="position:relative; margin: -20px; margin-top: -40px; z-index: -20;">
                    <div class="cloud x1"></div>
                    <div class="cloud x2"></div>
                    <div class="cloud x3"></div>
                    <div class="cloud x4"></div>
                    <div class="cloud x5"></div>
                    <div class="cloud x2"></div>
                    <div class="cloud x1"></div>
                </div>
                <div style="position:absolute; top:0; left:0; width:100%">
                    <div class="row" style="margin:10px; margin-top:30px">

                        <div class="col-md-8 col-md-offset-2">
                            <h2 style="text-align:center">#<s:property value="topicName" /></h2>
                            <div class="list-group text-center" >
                                <s:iterator var="i" step="1" value="ideas">
                                    <div class="list-group-item">
                                        <%-- href="viewidea.action?iid=<s:property value="iid" />" --%>
                                        <h4
                                                class="list-group-item-heading"><s:property value="title" /><div
                                                "></span></h4>
                                        <p class="list-group-item-text">
                                            <div style="height: 25px">
                                            <div style="float:right; white-space:nowrap;" id="ideatags<s:property
                                             value="id" />">
                                                <!-- Labels here -->
                                                <s:if test="top.percentOwned > 0.0">
                                                    <span class="label label-info" id="ownidea<s:property
                                                     value="id" />"><span
                                                            class="glyphicon glyphicon-ok"></span><span
                                                            id="numshares<s:property
                                                     value="id" />"><s:property
                                                            value="numSharesOwned" /></span> shares (<s:property
                                                            value="percentOwned" />%)
                                                    </span>
                                                </s:if>
                                                <!-- Watchlist Label -->
                                                <s:if test="top.inWatchList">

                                                    <span class="label label-success" id="watchlistlabel<s:property
                                                     value="id" />"><span class="glyphicon glyphicon-eye-open"></span>Na Watchlist
                                                        <a href="#" type="button"
                                                           onclick="removeFromWatchlist(<s:property
                                                                value="id" />);"><span
                                                                class="glyphicon glyphicon-remove"></span></a> </span>
                                                </s:if>

                                            </div>
                                            <div style="float:left; white-space:nowrap;" id="idealeftarea<s:property
                                             value="id" />">
                                                <!-- Left buttons -->
                                                <s:if test="top.percentOwned > 0.0">
                                                    <!-- Set share price -->
                                                    <div class="input-append">
                                                        <span
                                                                class="glyphicon glyphicon-euro"></span>
                                                        Vender a
                                                        <input name="text" id="sellingprice<s:property value="id" />"
                                                               value="<s:property value="sellingPrice" />"
                                                               style="width:50px;"
                                                               onkeyup="sharePriceChanged(<s:property
                                                                       value="id" />);"/>
                                                         DEICoins/share <button
                                                            id="btnsellingprice<s:property value="id" />"
                                                            class="btn btn-success btn-sm"
                                                            ><span
                                                                class="glyphicon glyphicon-ok-sign"></span></button>
                                                    </div>

                                                </s:if>
                                            </div>
                                        </div>
                                            <div style="height: 45px">
                                                <div style="float:right" id="buttons<s:property
                                                     value="id" />">
                                                    <!-- Buttons here -->

                                                    <!-- Delete idea -->
                                                    <s:if test="top.percentOwned == 100.0">
                                                        <a href="deleteidea.action?iid=<s:property value="id" />" type="button"
                                                           class="btn btn-danger btn-sm">
                                                            <span class="glyphicon glyphicon-remove"></span> Apagar Ideia
                                                        </a>
                                                    </s:if>

                                                    <!-- Delete idea -->
                                                    <s:if test="top.percentOwned < 100.0">
                                                        <!--data-toggle="modal" href="#myModal"-->
                                                        <a
                                                           type="button"
                                                           class="btn btn-primary btn-sm"
                                                           id="buyshares<s:property value="id" />"
                                                           onclick="buyShares(<s:property value="id" />);">
                                                            <span class="glyphicon glyphicon-cloud"></span><span
                                                                style="margin:-9px; color: #dbd02b"
                                                                class="glyphicon glyphicon-euro"></span>
                                                            &nbsp; &nbsp;Comprar Shares
                                                        </a>
                                                    </s:if>

                                                    <s:if
                                                            test="!top.inWatchList">
                                                        <a href="#" type="button"
                                                           class="btn btn-success btn-sm"
                                                           id="addtowatchlistbtn<s:property value="id" />"
                                                           onclick="addToWatchlist(<s:property value="id" />);">
                                                            <span class="glyphicon glyphicon-eye-open"></span> Adicionar
                                                            à Watchlist
                                                        </a>
                                                    </s:if>

                                                </div>
                                        </div>
                                            <%--Watchlist: <s:property value="inWatchList" />
                                            Owned: <s:property value="numSharesOwned" />
                                            Percent Owned: <s:property value="percentOwned" />--%>
                                                <s:property value="body" /> </p>
                                    </div>
                                </s:iterator>
                            </div><%--
                            <s:iterator var="i" step="1" value="ideas">
                                <s:url action="viewidea" var="urlTag">
                                    <s:param name="iid" value="top.id" />
                                </s:url>
                                <s:a href="%{urlTag}">Ideia <s:property value="iid" /></s:a><br />

                                <s:property value="id" /><br />
                                <s:property value="title" /><br />
                            </s:iterator>--%>

                        </div>
                    </div>
                </div>
            </div>

        </div>
    </div>
</div>
<script src="jquery.js"></script>
<script src="bootstrap-3.0.2/dist/js/bootstrap.min.js"></script>
<script src="bootstrap-dialog.js"></script>
</body>
</html>

