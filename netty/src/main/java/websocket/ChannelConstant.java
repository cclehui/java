package websocket;

import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.util.AttributeKey;

/**
 * Created by Administrator on 2017/5/14.
 */
public class ChannelConstant {


    public static AttributeKey<WebSocketServerHandshaker> handshakerAttributeKey = AttributeKey.valueOf("handshaker");

}
