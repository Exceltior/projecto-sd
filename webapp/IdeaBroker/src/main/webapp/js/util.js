
function isValidPositiveNum(input) {
    return !isNaN(input) && parseFloat(input) > 0;
}

function isValidPositiveInt(input) {
    return !isNaN(input) && parseInt(input) > 0;
}
function postJSON(page, data, func) {
    $.post(page, data, func, 'json');
}
function doPost(url,data){
    $.ajax({
        type: "POST",
        url: url,
        data: data,
        dataType: "html",
        success: function(data, textStatus) {
            console.log(data);
            console.log(textStatus);
//            window.location.replace(url);
            document.open();
            document.write(data);
            document.close();
        }
    });

}