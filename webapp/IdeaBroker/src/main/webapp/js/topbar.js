function getUserMoney() {
    return parseFloat($('#currmoney').text());
}

function setUserMoney(money) {
    $('#currmoney').text(money);
}