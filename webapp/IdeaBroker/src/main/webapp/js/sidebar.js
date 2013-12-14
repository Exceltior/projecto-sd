
gClosedialog = null;

function doSearch() {
    var s = $('#searchcontent').val();
    console.log('Searching; '+s);
    doPost('listideas.action',{q:s,mode:'searchidea'});
}

function searchIdea() {

    var searchbox =
        '<div class="input-group" ><span class="input-group-addon">Título: </span><input type="text" class="form-control" placeholder="A Mariana é Linda" id="searchcontent" onshow="$(\'#searchcontent\').focus();"/></div>';

    var message = function(dialogRef){
        var $message =
            $("<div style='font-size:16pt; text-align:center;'>Insira os dados da pesquisa</div>");
        $message.append($(searchbox));

        return $message;
    }
    var dialog = new BootstrapDialog({
        size: BootstrapDialog.SIZE_LARGE,
        message: message,
        closable:true
    });
    gClosedialog = dialog;

    var button =
        '<button class="btn btn-primary btn-lg" id="modalsubmitbutton" onclick="doSearch()"><span style="color: #dbd02b" class="glyphicon glyphicon-search"></span> &nbsp; &nbsp;Pesquisar!</button>';
    var closebutton
        = '<button class="btn btn-default btn-lg" onclick="gClosedialog.close();">Cancelar</button>';


    dialog.realize();

    dialog.getModalHeader().html('<div' +
        ' class="bootstrap-dialog-title">Pesquisar Ideia</div><div class="bootstrap-dialog-close-button" style="display: block;"><button class="close"  onclick="gClosedialog.close();"><span class="glyphicon glyphicon-remove"></span></button></div>');

    dialog.getModalFooter().html(button+closebutton);
    dialog.open();

}

function onIdeaformChange() {

    var investmentbox = $('#moneyInvested');
    var modalsubmitbutton = $('#modalsubmitbutton');
    console.log("val: "+investmentbox.val());
    if ( investmentbox.val().length == 0 || !isValidPositiveNum(investmentbox.val()) ) {
        modalsubmitbutton.prop('disabled',true);
        return;
    }

    var value = parseFloat(investmentbox.val());
    var money = getUserMoney();

    if ( value > money ) {
        investmentbox.val(money);
        modalsubmitbutton.prop('disabled',false);
    } else if ( value <= 0 ) {
        modalsubmitbutton.prop('disabled',true);
    }
    else
        modalsubmitbutton.prop('disabled',false);
}

function goToUserIdeas() {
    doPost('listideas.action',{mode:'userideas'});
}

function onSubmitIdeaSuccess() {
    gClosedialog.close();
    var message, header, type;
    message =
        $('<div style="text-align:center;"><div style="font-size:40pt;"><span style="color: #6fc65d" class="glyphicon glyphicon-ok-circle"></span></div>&nbsp;A ideia criada com sucesso!</div>');
    header =
        '<div class="bootstrap-dialog-title">Operação concluída com sucesso!</div><div class="bootstrap-dialog-close-button" style="display: block;"><button class="close"  onclick="gClosedialog.close();"><span class="glyphicon glyphicon-remove"></span></button></div>';
    type = BootstrapDialog.TYPE_SUCCESS;
    var dialog = new BootstrapDialog({
        size: BootstrapDialog.SIZE_LARGE,
        type: type,
        message: message,
        closable:true
    });
    gClosedialog = dialog;
    dialog.realize();
    dialog.getModalHeader().html(header);
    dialog.getModalFooter().html('<button class="btn btn-success btn-lg" onclick="goToUserIdeas();">Ir para a Lista de Ideias</button><button class="btn btn-default btn-lg" onclick="gClosedialog.close();">Continuar</button>');
    dialog.open();
}

gIdeaStateBox = null;
function onSubmitIdeaFailure() {

    var message, header, type;
    message =
        $('<div style="text-align:center;"><div style="font-size:40pt;"><span style="color: #d2322d" class="glyphicon glyphicon-remove-circle"></span></div>&nbsp;Já existe uma ideia com esse título!</div>');
    header =
        '<div class="bootstrap-dialog-title">A ideia não foi criada!</div><div class="bootstrap-dialog-close-button" style="display: block;"><button class="close"  onclick="gIdeaStateBox.close();"><span class="glyphicon glyphicon-remove"></span></button></div>';
    type = BootstrapDialog.TYPE_DANGER;
    var dialog = new BootstrapDialog({
        size: BootstrapDialog.SIZE_LARGE,
        type: type,
        message: message,
        closable:true
    });
    gIdeaStateBox = dialog;
    dialog.realize();
    dialog.getModalHeader().html(header);
    dialog.getModalFooter().html('<button class="btn btn-danger btn-lg" onclick="gIdeaStateBox.close();gClosedialog.close();">Voltar para a feed</button><button class="btn btn-default btn-lg" onclick="gIdeaStateBox.close();">Voltar</button>');
    dialog.open();
}

function doSubmitIdea() {
    $("form#createideadata").submit(function(){

        var formData = new FormData($(this)[0]);
        console.log(formData);
        $.ajax({
            url: 'submitidea.action',
            type: 'POST',
            data: formData,
            beforeSend: function (xhr){
                xhr.setRequestHeader("Accept","text/json");
            },
            async: false,
            success: function (data) {
                console.log(data);
                if ( data.success ) {
                    onSubmitIdeaSuccess();
                } else {
                    onSubmitIdeaFailure();
                }

            },
            cache: false,
            contentType: false,
            processData: false
        });

        return false;
    });

    $('#submitIt').click();
    return;/*
    title = $('#ideatitle').val();
    body = $('#ideabody').val();
    topics = $('#ideatopics').val();
    moneyinvested = $('#moneyInvested').val();
    console.log('t: '+title);
    console.log('bo: '+body);
    console.log('to: '+topics);
    console.log('money: '+moneyinvested);
    postJSON('submitidea.action',{title:title,body:body,topicsList:topics,moneyInvested:moneyinvested},function(data){
            if ( data.success ) {
                onSubmitIdeaSuccess();
            } else {
                onSubmitIdeaFailure();
            }
        }
    );*/
}

function btnchange() {
    console.log("woohoo!");
        var input = $('#file'),
            label = input.val().replace(/C:\\fakepath\\/i, '');;
    console.log(input);
    console.log(label);
    $('#ficheirodeisugaalmas').val(label);
}
function createIdea() {

    var html =
        '<form id="createideadata" method="post" enctype="multipart/form-data" action="submitidea.action">' +
            '<div class="input-group" style="padding-bottom:10px;">' +
            '<span class="input-group-addon" >Título: </span>' +
            '<input onkeyup="onIdeaformChange()" type="text" class="form-control" placeholder="Exemplo: DEI Suga Almas" name="title" id="title"/>' +
            '</div>' +
            '<div class="input-group"   style="padding-bottom:10px;">' +
            '<span class="input-group-addon">Conteúdo: </span>' +
            '<textarea onkeyup="onIdeaformChange()" style="resize:vertical;"' +
            'rows="3"' +
            'type="textarea"' +
            'class="form-control"' +
            'placeholder="Exemplo: 90% do meu tempo útil é passado no DEI" id="body" name="body"/>' +
            '</div>' +
            '<div class="input-group"  style="padding-bottom:10px;">' +
            '<span class="input-group-addon">Tópicos: </span>' +
            '<input onkeyup="onIdeaformChange()" type="text" class="form-control" placeholder="Exemplo #Topico1 #Topico2 #TopicoTrês" name="topicsList" id="topicsList"/>' +
            '</div>' +
            '<div class="input-group">' +
            '<span class="input-group-addon">Investimento Inicial: </span>' +
            '<input type="text" class="form-control" placeholder="Exemplo: 19930507.1605" name="moneyInvested" id="moneyInvested" ' +
            '' +
            'onkeyup="onIdeaformChange()"/>' +
            '</div>' +
            '<br /><div class="input-group">' +
            '<span class="input-group-btn">' +
            '<span class="btn btn-primary btn-file" onchange="btnchange()">Adicionar ficheiro...' +
            '<input id="file" name="file" type="file" multiple="" >' +
            '</span>' +
            '</span>' +
            '<input type="text" id="ficheirodeisugaalmas" class="form-control" readonly="">' +
            '</div>' +
            '<button type="submit" id="submitIt" class="btn btn-success btn-sm" style="display: none;">' +
            '</form>';

    var message = function(dialogRef){
        var $message =
            $("<div style='font-size:16pt; text-align:center;'>Insira os dados da ideia</div>");
        $message.append($(html));

        return $message;
    }
    var dialog = new BootstrapDialog({
        size: BootstrapDialog.SIZE_LARGE,
        message: message,
        closable:true
    });
    gClosedialog = dialog;

    var button =
        '<button class="btn btn-primary btn-lg" id="modalsubmitbutton" onclick="doSubmitIdea()" disabled><span style="color: #dbd02b" class="glyphicon glyphicon-edit"></span> &nbsp; &nbsp;Submeter Ideia</button>';
    var closebutton
        = '<button class="btn btn-default btn-lg" onclick="gClosedialog.close();">Cancelar</button>';


    dialog.realize();

    dialog.getModalHeader().html('<div' +
        ' class="bootstrap-dialog-title">Criar Ideia</div><div class="bootstrap-dialog-close-button" style="display: block;"><button class="close"  onclick="gClosedialog.close();"><span class="glyphicon glyphicon-remove"></span></button></div>');

    dialog.getModalFooter().html(button+closebutton);
    dialog.open();
    $('#modalsubmitbutton').prop('disabled',true);
}

