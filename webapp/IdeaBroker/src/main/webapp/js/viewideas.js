
function getWatchListLabelStr(id) {
    return '#watchlistlabel'+id;
}

function getAddToWatchListButtonStr(id) {
    return '#addtowatchlistbtn'+id;
}

function getSetSharePriceBtnStr(id) {
    return '#btnsellingprice'+id;
}

function getSetSharePriceTextEditStr(id) {
    return '#sellingprice'+id;
}

function getNumSharesIdeaStr(id) {
    return '#numshares'+id;
}

function getPercentSharesIdeaStr(id) {
    return "#percentshares"+id;
}

function getShareNumLabelStr(id) {
    return "#ownidea"+id;
}

function getSharePriceEditBoxStr(id) {
    return '#setsharepriceeditbox'+id;
}

function getRemoveIdeaBtnStr(id) {
    return '#removeidea'+id;
}

function getIdeaStr(id) {
    return '#idea'+id;
}

function getBuySharesBtnStr(id) {
    return "#buyshares"+id;
}

function getMarketValueStr(id) {
    return '#marketvalue'+id;
}

function updateMarketValue(id, val) {
    if ( haveIdeaOnWebpage(id) ) {
        console.log("Printing new value: "+val);
        $(getMarketValueStr(id)).text(val);
    }
}

function haveIdeaOnWebpage(id) {
    return $(getIdeaStr(id)).length != 0;
}

function showShareNumLabel(id) {
    $(getShareNumLabelStr(id)).show();
}

function hideShareNumLabel(id) {
    $(getShareNumLabelStr(id)).hide();
}


function showSetSellingPriceBox(id) {
    $(getSharePriceEditBoxStr(id)).show();
}

function hideSetSellingPriceBox(id) {
    $(getSharePriceEditBoxStr(id)).hide();
}

function showRemoveIdeaBtn(id) {
    $(getRemoveIdeaBtnStr(id)).show();
}

function showBuySharesBtn(id) {
    $(getBuySharesBtnStr(id)).show();
}

function hideBuySharesBtn(id) {
    $(getBuySharesBtnStr(id)).hide();
}

function hideRemoveIdeaBtn(id) {
    $(getRemoveIdeaBtnStr(id)).hide();
}

function takeover(id) {
    var formData = {iid:id};
    postJSON('takeover.action', formData, function(data) {
        console.log(data);
        if ( data.success ) {
            $(getIdeaStr(id)).hide();
        } else {
            alert("Server Internal Error...RMI is probably down!");
        }
    })
}

function addToWatchlist(id){
    var formData = {iid:id}; //Array
    postJSON('addtowatchlist.action', formData,function(data) {

        if ( data.success ) {
            $(getWatchListLabelStr(id)).show();
            $(getAddToWatchListButtonStr(id)).hide();
        } else {
            alert("Server Internal Error...RMI is probably down!");
        }
        return true;
    });
}

function removeFromWatchlist(id) {
    var formData = {iid:id}; //Array
    postJSON('removefromwatchlist.action', formData,function(data) {

        if ( data.success ) {
            $(getAddToWatchListButtonStr(id)).show();
            $(getWatchListLabelStr(id)).hide();
        } else {
            alert("Server Internal Error...RMI is probably down!");
        }
        return true;
    });
}

function sharePriceChanged(id) {
    text = $(getSetSharePriceTextEditStr(id));
    button = $(getSetSharePriceBtnStr(id));

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
    text = $(getSetSharePriceTextEditStr(id));
    button = $(getSetSharePriceBtnStr(id));

    var formData = {iid:id,price:text.val()}; //Array
    postJSON('setshareprice.action', formData,function(data) {
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

function getMaxSharesForIdea(id) {
    return 100000;
}

function getNumSharesForIdea(id) {
    var numshareslabel = $(getNumSharesIdeaStr(id));
    if ( numshareslabel.length != 0)
        return numshareslabel.text();
    else
        return 0;
}


function setSellingPriceIdea(id, price) {
    $(getSetSharePriceTextEditStr(id)).val(price);
}

function getSellingPriceIdea(id, price) {
    if ( $(getSetSharePriceTextEditStr(id)).is(":visible") )
        return $(getSetSharePriceTextEditStr(id)).val();
    return -1;
}


////
// CALL THIS FUNCTION TO UPDATE THE SHARES FOR AN IDEA. IT UPDATES ALL THE FIELDS IT HAS TOO. ALSO DON'T FORGET
// THAT THIS SHOWS THE SET SELLING PRICE FIELD, SO YOU MIGHT WANT TO USE setSellingPriceIdea() TO UPDATE THAT!
//
function setNumSharesForidea(id, num) {
    var numshareslabel = $(getNumSharesIdeaStr(id));
    var percentshareslabel = $(getPercentSharesIdeaStr(id));
    var pct =  num / getMaxSharesForIdea() * 100.0;
    percentshareslabel.text(pct);
    numshareslabel.text(num);
    if ( num > 0 ) {
        showSetSellingPriceBox(id);
        showShareNumLabel(id);
        if ( pct == 100.0 ) {
            showRemoveIdeaBtn(id);
            hideBuySharesBtn(id);
        }
        else {
            hideRemoveIdeaBtn(id);
            showBuySharesBtn(id);
        }
    } else {
        hideSetSellingPriceBox(id);
        hideShareNumLabel(id);
        hideRemoveIdeaBtn(id);
    }
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
    } else {
        //Can sell
        modalsubmitbutton.prop('disabled', false);
    }
}

function onNumSharesWantChanged(id) {
    var maxSharesAvail=getMaxSharesForIdea(id)-parseInt($(getNumSharesIdeaStr(id)).text());
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
    } else {
        //Can sell
        modalsubmitbutton.prop('disabled', false);
    }
}

function removeIdea(id) {
    console.log("No removeIdea");
    var formData = {iid:id}; //Array
    postJSON('removeidea.action', formData,function(data) {
        console.log(data);
        if ( data.success ) {
            if ( data.result == "OK" ) {
                console.log("Hiding idea" + getIdeaStr(id));
                $(getIdeaStr(id)).hide();
            } else {
                showMessage('Erro', 'Não foi possível apgar a ideia porque não detém 100% das suas shares.',
                    BootstrapDialog.TYPE_DANGER);
            }
        } else {
            alert("Server Internal Error...RMI is probably down!");
        }
        return true;
    });
}

function doBuyShares(id) {
    var maxPerShare     = parseFloat($('#maxpershareinput').val());
    var numSharesWant   = parseInt($('#numshareswant').val());
    var wantToQueue     = $('#addtoqueue').prop('checked');
    var targetSellPrice = parseFloat($('#targetsellpriceinput').val());
    var formData = {iid:parseInt(id),
        maxPricePerShare:maxPerShare,
        buyNumShares:numSharesWant,
        addToQueueOnFailure:wantToQueue,
        targetSellPrice:targetSellPrice};

    var message, header, type;

    postJSON('buyshares.action', formData,function(data) {
        console.log("Buy shares action! "+data);
        if ( data.success ) {
            gClosedialog.close();

            //Update the money onscreen (it's okay if we don't buy anything because totalspent=0)
            //setUserMoney(getUserMoney()-data.totalSpent); FIXME Disabled because we get the notification
            //FIXME: WARNING, Most of buy shares is all screwed up. We probably will only get QUEUED.NOMOREMONEY OR NOBUY.NOMOREMONEY or NOBUY.NOMORESHARES

            if ( data.result == 'OK' ) {
                setNumSharesForidea(id,data.numSharesFinal);
                //Update the selling price onscreen
                setSellingPriceIdea(id,targetSellPrice);
                message =
                    $('<div style="text-align:center;"><div style="font-size:40pt;"><span style="color: #6fc65d" class="glyphicon glyphicon-ok-circle"></span></div>&nbsp;<span style="color: #6fc65d">'+data.numSharesBought+'</span> shares foram compradas pelo total de <span style="color: #6fc65d">'+data.totalSpent+'</span> DEICoins.<br /></div>');
                header =
                    '<div class="bootstrap-dialog-title">Operação concluída com sucesso!</div><div class="bootstrap-dialog-close-button" style="display: block;"><button class="close"  onclick="gClosedialog.close();"><span class="glyphicon glyphicon-remove"></span></button></div>';
                type = BootstrapDialog.TYPE_SUCCESS;
            } else if ( data.result == 'QUEUED.NOMOREMONEY' ) {
                setNumSharesForidea(id,data.numSharesFinal);
                // Request got queued because we ran out of money
                //Update the selling price onscreen
                setSellingPriceIdea(id,targetSellPrice);
                message =
                    $('<div style="text-align:center;"><div style="font-size:40pt;"><span style="color: #f0ad4e" class="glyphicon glyphicon-remove-circle"></span></div>&nbsp;<span style="color: #f0ad4e">'+data.numSharesBought+'</span> shares foram compradas pelo total de <span style="color: #f0ad4e">'+data.totalSpent+'</span> DEICoins.<br /> Não houve dinheiro para comprar mais (apesar de haver shares).<br /><span style="color: #1500d2">As restantes serão compradas assim que possível.</span></span></div>');
                header =
                    '<div class="bootstrap-dialog-title">Nem todas as shares foram compradas</div><div class="bootstrap-dialog-close-button" style="display: block;"><button class="close"  onclick="gClosedialog.close();"><span class="glyphicon glyphicon-remove"></span></button></div>';
                type = BootstrapDialog.TYPE_WARNING;
            } else if ( data.result == 'QUEUED.NOMORESHARES' ) {
                setNumSharesForidea(id,data.numSharesFinal);
                // Request got queued because there are no more shares (no more shares at all or at our desired price)
                //Update the selling price onscreen
                setSellingPriceIdea(id,targetSellPrice);
                message =
                    $('<div style="text-align:center;"><div style="font-size:40pt;"><span style="color: #f0ad4e" class="glyphicon glyphicon-remove-circle"></span></div>&nbsp;<span style="color: #f0ad4e">'+data.numSharesBought+'</span> shares foram compradas pelo total de <span style="color: #f0ad4e">'+data.totalSpent+'</span> DEICoins.<br /> O número de sahres pedido não está disponível.<br /><span style="color: #1500d2">As restantes serão compradas assim que possível.</span></span></div>');
                header =
                    '<div class="bootstrap-dialog-title">Nem todas as shares foram compradas (não havia shares suficientes)</div><div class="bootstrap-dialog-close-button" style="display: block;"><button class="close"  onclick="gClosedialog.close();"><span class="glyphicon glyphicon-remove"></span></button></div>';
                type = BootstrapDialog.TYPE_WARNING;
            } else if ( data.result == 'NOBUY.NOMOREMONEY' ) {
                // Not enough money to buy all the required shares, so nothing bought at all
                message =
                    $('<div style="text-align:center;"><div style="font-size:40pt;"><span style="color: #d2322d" class="glyphicon glyphicon-remove-circle"></span></div>&nbsp;<span style="color: #d2322d">Nenhuma compra foi feita.<br /> Não há dinheiro suficiente.<br /><span style="color: #1500d2">Por seu pedido, dada esta situação, a operação foi abortada.</span></span></div>');
                header =
                    '<div class="bootstrap-dialog-title">Operação cancelada</div><div class="bootstrap-dialog-close-button" style="display: block;"><button class="close"  onclick="gClosedialog.close();"><span class="glyphicon glyphicon-remove"></span></button></div>';
                type = BootstrapDialog.TYPE_DANGER;
            } else if ( data.result == 'NOBUY.NOMORESHARES' ) {
                // Not enough shares are available, so none bought at all
                message =
                    $('<div style="text-align:center;"><div style="font-size:40pt;"><span style="color: #d2322d" class="glyphicon glyphicon-remove-circle"></span></div>&nbsp;<span style="color: #d2322d">Nenhuma compra foi feita.<br /> Não há shares suficientes.<br /><span style="color: #1500d2">Por seu pedido, dada esta situação, a operação foi abortada.</span></span></div>');
                header =
                    '<div class="bootstrap-dialog-title">Operação cancelada</div><div class="bootstrap-dialog-close-button" style="display: block;"><button class="close"  onclick="gClosedialog.close();"><span class="glyphicon glyphicon-remove"></span></button></div>';
                type = BootstrapDialog.TYPE_DANGER;
            }

            var dialog = new BootstrapDialog({
                size: BootstrapDialog.SIZE_LARGE,
                type: type,
                message: message,
                closable:true
            });
            gClosedialog = dialog;
            dialog.realize();
            dialog.getModalHeader().html(header);
            dialog.getModalFooter().html('<button class="btn btn-default btn-lg" onclick="gClosedialog.close();">Terminado</button>');
            dialog.open();

        } else {
            alert("Server Internal Error...RMI is probably down!");
        }
        return true;
    });
}

function getMarketValue(id) {
    return parseFloat($(getMarketValueStr(id)).text());
}

function onTargetSellPriceChanged() {
    var m = $('#targetsellpriceinput');
    var modalsubmitbutton = $('#modalsubmitbutton');
    if (m.val() == '' || m.val() == 0 || !isValidPositiveNum(m.val())) {
        modalsubmitbutton.prop('disabled', true);
        return;
    }

    modalsubmitbutton.prop('disabled', false);
}


function buyShares(id) {
    var currentmoney = getUserMoney();

    var sellingPrice = getSellingPriceIdea(id);
    if (sellingPrice == -1)
        sellingPrice = getMarketValue(id);

    var numsharesarea =
        '<div class="input-append"><span class="glyphicon glyphicon-chevron-right"></span>&nbsp; Número de shares a comprar:&nbsp;<input name="text" id="numshareswant" value="'+1+'" style="width:125px;" onkeyup="onNumSharesWantChanged('+id+');" /></div>';
    var maxpersharearea =
        '<div class="input-append"><span class="glyphicon glyphicon-chevron-right"></span>&nbsp; Máximo por share:&nbsp;<input name="text" id="maxpershareinput" value="'+currentmoney+'" style="width:125px;" onkeyup="onMaxWillingToBuyChanged();" /> DEICoins</div>';

    var targetsellpricearea =
        '<div class="input-append"><span class="glyphicon glyphicon-chevron-right"></span>&nbsp; Preço de venda:&nbsp;<input name="text" id="targetsellpriceinput" value="'+sellingPrice+'" style="width:125px;" onkeyup="onTargetSellPriceChanged();" /> DEICoins/share</div>';

    var modalcheckbox =
        '<div class="input-append"><span class="glyphicon glyphicon-chevron-right"></span>&nbsp;<input type="checkbox" id="addtoqueue" value="true" > Colocar pedido na fila se não for possível satisfazer</input></div>';

    var message = function(dialogRef){
        var $message =
            $("<div style='font-size:16pt'>Começou com <span style='color: #6fc65d'>"+getNumSharesForIdea(id)+"</span> shares</div>");
        $message.append(numsharesarea).append($('<div>&nbsp;</div>')).append($(maxpersharearea)).append($('<div>&nbsp;</div>')).append($(targetsellpricearea)).append($(modalcheckbox));

        return $message;
    }
    var dialog = new BootstrapDialog({
        size: BootstrapDialog.SIZE_LARGE,
        message: message,
        closable:true
    });
    gClosedialog = dialog;

    var button =
        '<button class="btn btn-primary btn-lg" id="modalsubmitbutton" onclick="doBuyShares('+id+');"><span class="glyphicon glyphicon-cloud"></span><span style="margin:-9px; color: #dbd02b" class="glyphicon glyphicon-euro"></span> &nbsp; &nbsp;Comprar Shares</button>';
    var closebutton
        = '<button class="btn btn-default btn-lg" onclick="gClosedialog.close();">Cancelar</button>';


    dialog.realize();

    dialog.getModalHeader().html('<div' +
        ' class="bootstrap-dialog-title">Comprar Shares</div><div class="bootstrap-dialog-close-button" style="display: block;"><button class="close"  onclick="gClosedialog.close();"><span class="glyphicon glyphicon-remove"></span></button></div>');

    dialog.getModalFooter().html(button+closebutton);
    dialog.open();
}