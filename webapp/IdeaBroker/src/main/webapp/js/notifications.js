/***
 * INCLUDE NOTTY FIRST!
 ****/

function makeNotification(title, body) {
    noty(
        {   text:
            '<div><h4 style="text-align:center; color:#ffcd41; text-shadow: 2px 2px 0px black;"><b>'+title+'<hr style="margin-top:10px;"/></b></h4></div><div>'+body+'</div>',
            layout: 'topRight',
            timeout: 5000,
            type: 'information'
        }
    );
}