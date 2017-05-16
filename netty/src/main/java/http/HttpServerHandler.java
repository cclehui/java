package http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import io.netty.handler.codec.http.multipart.*;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    protected Logger logger = Logger.getLogger(HttpServerHandler.class);

    private FullHttpRequest request;
    private boolean readingChunks;
    /** Buffer that stores the response content */
    private final StringBuilder buf = new StringBuilder();

    protected ConcurrentHashMap<String, Object> getData = new ConcurrentHashMap<String, Object>();
    protected ConcurrentHashMap<String, Object> postData = new ConcurrentHashMap<String, Object>();
    protected ConcurrentHashMap<String, Object> serverData = new ConcurrentHashMap<String, Object>();

    protected ArrayList<FileUpload> fileUploadList = new ArrayList<FileUpload>();

    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {

        if (HttpHeaders.is100ContinueExpected(request)) {
            send100Continue(ctx);
        }

        this.request = request;

        //解析http header相关内容
        parseServerData(request);

        buf.setLength(0);
        for (Map.Entry<String, Object>item : serverData.entrySet()) {
            buf.append(item.getKey() + "\t" + item.getValue().toString() + "\n");
        }
        buf.append("\r\n");

        // get 数据
        parseGetData(request);

        //post 数据
        try {
            parsePostData(request);
        } catch (Exception exception) {
            logger.info(exception.getMessage());
            sendServerException(ctx);
            return;
        }

        logger.info("server data:\t" + serverData);
        logger.info("get data:\t" + getData);
        logger.info("post data:\t" + postData);

        logger.info("file uploads:\t" + fileUploadList);

        ByteBuf content = request.content();
        if (content.readableBytes() > 0) {
            buf.append("CONTENT: " + content.toString(CharsetUtil.UTF_8) + "\r\n");
        }

        //返回结果
        writeResponse(ctx);
    }


    //解析http header 协议等相关信息
    public  void parseServerData(FullHttpRequest request) {
        serverData.put("VERSION", request.getProtocolVersion());
        serverData.put("HOSTNAME", request.headers().getHost(request));
        serverData.put("METHOD", request.getMethod());
        serverData.put("REQUEST_URI", request.getUri());

        for (Map.Entry<String, String> h: request.headers()) {
            buf.append("HEADER: " + h.getKey() + " = " + h.getValue() + "\r\n");
            serverData.put(h.getKey(), h.getValue());
        }

        return;
    }

    //解析get数据
    public void parseGetData(FullHttpRequest request) {
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.getUri());
        Map<String, List<String>> params = queryStringDecoder.parameters();
        if (!params.isEmpty()) {
            for (Map.Entry<String, List<String>> p: params.entrySet()) {
                String key = p.getKey();
                List<String> vals = p.getValue();
                for (String val : vals) {
                    getData.put(key, val);
                }
            }
        }

        return;
    }

    //解析Post数据
    public void parsePostData(FullHttpRequest request) throws Exception {

        DefaultHttpDataFactory httpDataFactory = new DefaultHttpDataFactory();
        HttpPostRequestDecoder postRequestDecoder = new HttpPostRequestDecoder(httpDataFactory, request, Charset.defaultCharset() );

        List<InterfaceHttpData> postDataList = postRequestDecoder.getBodyHttpDatas();

        for (InterfaceHttpData item :postDataList) {

            InterfaceHttpData.HttpDataType curDataType = item.getHttpDataType();

            if (curDataType == InterfaceHttpData.HttpDataType.Attribute) {
                //普通post数据
                Attribute attribute = (Attribute) item;
                logger.info("parse post data:\t" + attribute.getName() + "=>" + attribute.getValue());
                postData.put(attribute.getName(), attribute.getValue());

            } else if (curDataType == InterfaceHttpData.HttpDataType.FileUpload) {
                //文件上传
                fileUploadList.add((FileUpload)item);
            } else {
                throw new Exception("不支持的数据类型");
            }
        }

        return;
    }

    //返回 501
    public void sendServerException(ChannelHandlerContext ctx) {
        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_IMPLEMENTED);
        ctx.channel().write(response);
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.channel().close();
    }

    private static void send100Continue(ChannelHandlerContext ctx) {
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
        ctx.channel().write(response);
    }
    private void writeResponse(ChannelHandlerContext ctx) {
        // Decide whether to close the connection or not.
        boolean keepAlive = HttpHeaders.isKeepAlive(request);

        String responseData = buf.toString() + "\n" + getData.toString() + "\n" + postData.toString();

        // Build the response object.
        ByteBuf contentByteBuf = Unpooled.wrappedBuffer(responseData.getBytes());

        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, contentByteBuf);
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=UTF-8");

        if (keepAlive) {
            // Add 'Content-Length' header only for a keep-alive connection.
            response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, response.content().readableBytes());
            // Add keep alive header as per:
            // - http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
            response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        }

        // Encode the cookie.
        String cookieString = request.headers().get(HttpHeaders.Names.COOKIE);
        if (cookieString != null) {
            Set<Cookie> cookies = ServerCookieDecoder.LAX.decode(cookieString);
            if (!cookies.isEmpty()) {
                // Reset the cookies if necessary.
                for (Cookie cookie : cookies) {
                    response.headers().add(HttpHeaders.Names.SET_COOKIE, ServerCookieEncoder.LAX.encode(cookie));
                }
            }
        } else {
            // Browser sent no cookie.  Add some.
            response.headers().add(HttpHeaders.Names.SET_COOKIE, ServerCookieEncoder.LAX.encode("key1", "value1"));
            response.headers().add(HttpHeaders.Names.SET_COOKIE, ServerCookieEncoder.LAX.encode("key2", "value2"));
        }

        // Write the response.
        ChannelFuture future = ctx.channel().writeAndFlush(response);

        // Close the non-keep-alive connection after the write operation is done.
        if (!keepAlive) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

}
