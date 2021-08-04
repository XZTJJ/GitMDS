/*************** 公共的方法 *******************/
//总数
var rows = 0;
const streamName = "stream";

//拼接方法
function append(objStr) {
    if (rows >= 20) {
        rows = 0;
        $("#information").html("");
    }
    $("#information").append("<tr><td><pre style='overflow-x: auto'>" + objStr + "</pre></td></tr>");
    rows = rows + 1;
}

//发送消息
function sendMsg() {
    var orMsg = $("#content").val();
    $("#content").val("");
    if (!orMsg) {
        console.log("消息不能为空")
        return;
    }

    var obj = JSON.stringify({[streamName]: orMsg});

    $.ajax({
        url: "http://127.0.0.1:9013/insert/datatest",
        type: "post",
        data: obj,
        contentType: "application/json",
        dataType: "json",
        success: function (data) {
            console.log(data);
        },
        error: function (error) {
            console.log(error);
        }
    })
}
//窗口关闭动作
window.onbeforeunload = function() {
    wsClose();
    sseClose();
}

/*************** 公共的方法 *******************/


/*******************   websocket  *******************/
var websocket;

//websocket的连接
function wsCon() {
    if ('WebSocket' in window) {
        websocket = new WebSocket("ws://127.0.0.1:9013/subscribe/" + streamName);
        websocket.onclose = function () {
            alert("websocket连接关闭了")
        };
        websocket.onerror = function () {
            alert("websocket链接发生错误")
        };
        websocket.onmessage = function (event) {
            var msg = JSON.parse(event.data);
            msg.pushType = "websocket";
            //转成可以的Json字符串
            var josnStr = JSON.stringify(msg, null, 2);
            append(josnStr);
        }
        websocket.onopen = function () {
            console.log("websocket链接已经建立");
        };
        //显示订阅地址
        $("#subscribe-websocket").val(streamName);
    } else {
        alert("当前浏览器不支持 websocket 协议");
    }
}

//关系消息时触发
function wsClose() {
    if (websocket)
        websocket.close();
    $("#subscribe-websocket").val("");
}

/*******************   websocket  *******************/


/*******************   sse  *******************/
var sse;

//sse的链接方法
function sseCon() {
    if ("EventSource" in window) {
        sse = new EventSource("http://127.0.0.1:9013/subscribe/" + streamName);
        sse.onerror = function () {
            alert("sse链接发生错误")
        };
        sse.onmessage = function (event) {
            var msg = JSON.parse(event.data);
            msg.pushType = "sse";
            //转成可以的Json字符串
            var josnStr = JSON.stringify(msg, null, 2);
            append(josnStr);
        }
        sse.onopen = function () {
            console.log("sse链接已经建立");
        };
        //显示订阅地址
        $("#subscribe-sse").val(streamName);
    } else {
        alert("当前浏览器不支持sse协议");
    }
}

//sse的断开方法
function sseClose() {
    if (sse)
        sse.close()
    $("#subscribe-sse").val("");
}

/*******************   sse  *******************/


