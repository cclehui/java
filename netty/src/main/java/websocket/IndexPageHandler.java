package websocket;

import com.sun.deploy.net.HttpRequest;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;

import java.io.*;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpHeaders.Values;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.*;
import static io.netty.util.AttributeKey.newInstance;

/**
 * Created by Administrator on 2017/5/14.
 */
public class IndexPageHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    public static final String WEBSOCKET_PATH = "/websocket";

    private static final String NEWLINE = "\r\n";

    protected void channelRead0(final ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {

        if (req.getMethod() != HttpMethod.GET) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN));
            return;
        }

        // Send the demo page and favicon.ico
        if ("/".equals(req.getUri())) {

            //ByteBuf contentByteBuf = getIndexContent(getWebSocketLocation(req));
            ByteBuf contentByteBuf = getIndexContentFromFile("websocketIndex.html");

            DefaultFullHttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, OK, contentByteBuf);
            res.headers().set(CONTENT_TYPE, "text/html; charset=UTF-8");

            if (HttpHeaders.isKeepAlive(req)) {
                res.headers().set(CONNECTION, Values.KEEP_ALIVE);//keep alive
            }
            res.headers().set(CONTENT_LENGTH, contentByteBuf.readableBytes());
            sendHttpResponse(ctx, req, res);

            return;
        }

        if ("/favicon.ico".equals(req.getUri())) {
            DefaultFullHttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND);
            sendHttpResponse(ctx, req, res);
            return;
        }

        // websocke Handshake 握手

        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(getWebSocketLocation(req), null, false);
        WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(req);
        if(handshaker == null) {
            wsFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            ChannelFuture handshakeFuture = handshaker.handshake(ctx.channel(), req);
            handshakeFuture.addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (!future.isSuccess()) {
                        ctx.fireExceptionCaught(future.cause());
                    } else {
                        ctx.fireUserEventTriggered(WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE);
                    }

                }
            });
        }

        Attribute<WebSocketServerHandshaker> handshakerAttribute =  ctx.channel().attr(ChannelConstant.handshakerAttributeKey);
        handshakerAttribute.setIfAbsent(handshaker);
    }

    private static void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse res) {
        // Generate an error page if response status code is not OK (200).
        if (res.getStatus() != HttpResponseStatus.OK) {
//            res.setContent(ChannelBuffers.copiedBuffer(res.getStatus().toString(), CharsetUtil.UTF_8));
//            setContentLength(res, res.getContent().readableBytes());
        }

        // Send the response and close the connection if necessary.
        ChannelFuture f = ctx.writeAndFlush(res);
        if (!HttpHeaders.isKeepAlive(req) || res.getStatus() != HttpResponseStatus.OK) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    private static String getWebSocketLocation(FullHttpRequest req) {
        String location =  req.headers().get(HOST) + WEBSOCKET_PATH;
        if (WebSocketServer.SSL) {
            return "wss://" + location;
        } else {
            return "ws://" + location;
        }
    }

    protected static ByteBuf getIndexContentFromFile(String fileName) throws IOException {

        String filePath = (WebSocketServer.class.getClass().getResource("/").getPath() + fileName);

        File file = new File(filePath);

        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

        StringBuffer result = new StringBuffer();
        String curLine = null;

        while ((curLine = bufferedReader.readLine()) != null) {
             result.append(curLine);
        }

        return Unpooled.wrappedBuffer(result.toString().getBytes());
    }

    protected static ByteBuf getIndexContent(String webSocketLocation) {

        return Unpooled.copiedBuffer("<html><head><title>Web Socket Test</title></head>" + NEWLINE +
                "<body>" + NEWLINE +
                "<script type=\"text/javascript\">" + NEWLINE +
                "var socket;" + NEWLINE +
                "if (!window.WebSocket) {" + NEWLINE +
                "  window.WebSocket = window.MozWebSocket;" + NEWLINE +
                '}' + NEWLINE +
                "if (window.WebSocket) {" + NEWLINE +
                "  socket = new WebSocket(\"" + webSocketLocation + "\");" + NEWLINE +
                "  socket.onmessage = function(event) {" + NEWLINE +
                "    var ta = document.getElementById('responseText');" + NEWLINE +
                "    ta.value = ta.value + '\\n' + event.data" + NEWLINE +
                "  };" + NEWLINE +
                "  socket.onopen = function(event) {" + NEWLINE +
                "    var ta = document.getElementById('responseText');" + NEWLINE +
                "    ta.value = \"Web Socket opened!\";" + NEWLINE +
                "  };" + NEWLINE +
                "  socket.onclose = function(event) {" + NEWLINE +
                "    var ta = document.getElementById('responseText');" + NEWLINE +
                "    ta.value = ta.value + " + NEWLINE + "\"Web Socket closed\"; " + NEWLINE +
                "  };" + NEWLINE +
                "} else {" + NEWLINE +
                "  alert(\"Your browser does not support Web Socket.\");" + NEWLINE +
                '}' + NEWLINE +
                NEWLINE +
                "function send(message) {" + NEWLINE +
                "  if (!window.WebSocket) { return; }" + NEWLINE +
                "  if (socket.readyState == WebSocket.OPEN) {" + NEWLINE +
                "    socket.send(message);" + NEWLINE +
                "  } else {" + NEWLINE +
                "    alert(\"The socket is not open.\");" + NEWLINE +
                "  }" + NEWLINE +
                '}' + NEWLINE +
                "</script>" + NEWLINE +
                "<form onsubmit=\"return false;\">" + NEWLINE +
                "<input type=\"text\" name=\"message\" value=\"Hello, World!\"/>" +
                "<input type=\"button\" value=\"Send Web Socket Data\"" + NEWLINE +
                "       onclick=\"send(this.form.message.value)\" />" + NEWLINE +
                "<h3>Output</h3>" + NEWLINE +
                "<textarea id=\"responseText\" style=\"width:500px;height:300px;\"></textarea>" + NEWLINE +
                "</form>" + NEWLINE +
                "</body>" + NEWLINE +
                "</html>" + NEWLINE, CharsetUtil.UTF_8);
    }
}
