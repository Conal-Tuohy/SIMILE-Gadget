function getTarget(e) {
    if (!e) var e = window.event;
    if (e.target) {
       targ = e.target;
    } else if (e.srcElement) {
       targ = e.srcElement;
    }
    return targ;
}

function hide(e) {
    var start = new Date();
    var starttime = start.getSeconds() * 1000 + start.getMilliseconds();
    var targ = getTarget(e);
    var items = document.getElementsByName("item");
    var regexp = new RegExp(targ.value, "ig");
    for (i = 0; i < items.length; i++) {
        var item = items.item(i);
        if (targ.value.length > 0) {
            if (item.firstChild.nodeValue.match(regexp)) {
                if (item.parentNode.parentNode.style.display != "block") {
                    item.parentNode.parentNode.style.display = "block";
                }
            } else {
                if (item.parentNode.parentNode.style.display != "none") {
                    item.parentNode.parentNode.style.display = "none";
                }
            }
        } else {
            item.parentNode.parentNode.style.display = "block";
        }
    }
    var end = new Date();
    var endtime = end.getSeconds() * 1000 + end.getMilliseconds();
    window.status = "Update time: " + (endtime - starttime) + " ms";
}

function toggle(id) {
    var element = document.getElementById(id);
    with (element.style) {
        if ( display == "none" ){
            display = ""
        } else{
            display = "none"
        }
    }
    var text = document.getElementById(id + "-switch").firstChild;
    if (text.nodeValue == "[show]") {
        text.nodeValue = "[hide]";
    } else {
        text.nodeValue = "[show]";
    }
}
