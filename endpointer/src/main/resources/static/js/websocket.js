// 设置 STOMP 客户端
var stompClient = null;
// 设置 WebSocket 进入端点
var SOCKET_ENDPOINT = "/streamData";
// 设置订阅消息的请求前缀
var SUBSCRIBE_PREFIX = "/topic"
// 设置订阅消息的请求地址
var SUBSCRIBE = "";
// 设置服务器端点，访问服务器中哪个接口
var SEND_ENDPOINT = "/app/client";
//设置总数
var rows = 0;

/* 进行连接 */
function connect() {
    // 设置 SOCKET
    var socket = new SockJS(SOCKET_ENDPOINT);
    // 配置 STOMP 客户端
    stompClient = Stomp.over(socket);
    // STOMP 客户端连接
    stompClient.connect({}, function (frame) {
        alert("连接成功");
        subscribeSocket();
    });
}

/* 订阅信息 */
function subscribeSocket() {
    // 执行订阅消息
    $("#subscribe").val("/stream");
    SUBSCRIBE = SUBSCRIBE_PREFIX + $("#subscribe").val();
    alert("自动订阅地址：" + SUBSCRIBE);
    stompClient.subscribe(SUBSCRIBE, function (responseBody) {
        //转成json
        var receiveMessage = JSON.parse(responseBody.body);
        //转成可以的Json字符串
        var josnStr = JSON.stringify(receiveMessage, null, 2);
        if (rows >= 20) {
            rows = 0;
            $("#information").html("");
        }
        $("#information").append("<tr><td><pre style='overflow-x: auto'>" + josnStr + "</pre></td></tr>");
        rows = rows + 1;
    });
}

/* 断开连接 */
function disconnect() {
    stompClient.disconnect(function () {
        alert("断开连接");
    });
}

/* 发送消息并指定目标地址（这里设置的目标地址为自身订阅消息的地址，当然也可以设置为其它地址） */
function sendMessageNoParameter() {
    // 设置发送的内容
    var sendContent = $("#content").val();
    // 设置待发送的消息内容
    var message = '{"destination": "' + SUBSCRIBE + '", "content": "' + sendContent + '"}';
    // 发送消息
    stompClient.send(SEND_ENDPOINT, {}, message);
}