var HTTP = {

    get : function(url, onError, onDone) {
        var ro = http_getRequestObject();
        ro.open('GET', url, true);
        ro.onreadystatechange = function() {
            http_onReadyStateChange(ro, onError, onDone);
        };
        ro.send(null);
    }
    
}
    
var http_onReadyStateChange = function (ro, onError, onDone) {
    switch (ro.readyState) {

        // Request not yet made
        case 1:
        break;

        // Contact established with server but nothing downloaded yet
        case 2:
            try {
                // Check for HTTP status 200
                if (ro.status != 200) {
                    if (onError) {
                        onError(ro.status, ro.statusText);
                        ro.abort();
                    }
                }
            } catch (e) {
                alert("established: " + e);
            }
        break;

        // Called multiple times while downloading in progress
        case 3:
        break;

        // Download complete
        case 4:
            try {
                if (onDone && ro.status == 200) {
                    //alert(ro.responseText);   
                    onDone(ro.responseText);
                }
            } catch (e) {
                alert("complete: " + e);
            }
        break;
    }
};

var http_getRequestObject = function() {
    var ro;
    var browser = navigator.appName;
    if (browser == "Microsoft Internet Explorer") {
        ro = new ActiveXObject("Microsoft.XMLHTTP");
    } else {
        ro = new XMLHttpRequest();
    }
    return ro;
}

