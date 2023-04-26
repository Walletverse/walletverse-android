window.__jMessageCallbacks = {};

function randomString(length) {
    var chars = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
    var result = '';
    for (var i = length; i > 0; --i) result += chars[Math.floor(Math.random() * chars.length)];
    return result;
}

window.jhost=function () {
    if (arguments.length < 1) {
        return;
    }
    var method = arguments[0];
    var params = arguments[1];
    var callback = arguments[2];
    var id = (new Date().getTime().toString()) + randomString(6);
    __jMessageCallbacks[id] = callback;

    try {
        if (window.__JSHOST) {
            var qeru = {id, method, params};
            __JSHOST.postMessage(JSON.stringify(qeru));
        } else if (window.webkit && window.webkit.messageHandlers
            && window.webkit.messageHandlers.__JSHOST) {
            var data = [method, id, params];
            window.webkit.messageHandlers.__JSHOST.postMessage(data);
        } else {
            jMessage(id, 'Bridge Not Found ' + method);
            console.log(id, 'Bridge Not Found ' + method)
        }
    } catch (e) {
        jMessage(id, 'Bridge Error');
        console.log(id, 'Bridge Error',e)
    }
}
window.__jMessage=function (){
    console.log("__jMessage",arguments);
    if (arguments.length < 1) {
        return;
    }

    if (window.__jMessageCallbacks) {
        var id = arguments[0].toString();
        var args = [];
        if (arguments.length >= 2) {
            var err = arguments[1];
            if (err == '') {
                args.push('0');
            } else {
                args.push(err);
            }
            for (var i = 2; i < arguments.length; i++) {
                var item = arguments[i];
                args.push(item);
            }
        }
        if (window.__jMessageCallbacks[id]) {
            window.__jMessageCallbacks[id].apply(this, args);
            delete window.__jMessageCallbacks[id]
        }
    }
}
window.jMessage=function (){
    window.__jMessage.apply(this, arguments)
}
