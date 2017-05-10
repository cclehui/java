
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.Cookie;
import org.jboss.netty.handler.codec.http.CookieDecoder;
import org.jboss.netty.handler.codec.http.CookieEncoder;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpChunkTrailer;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.jboss.netty.handler.codec.http.multipart.*;
import org.jboss.netty.util.CharsetUtil;
import org.w3c.dom.Attr;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.*;
import static org.jboss.netty.handler.codec.http.HttpHeaders.*;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.*;
import static org.jboss.netty.handler.codec.http.HttpVersion.*;

public class HttpServerHandler extends SimpleChannelUpstreamHandler {

    protected Logger logger = Logger.getLogger(HttpServerHandler.class);

    private HttpRequest request;
    private boolean readingChunks;
    /** Buffer that stores the response content */
    private final StringBuilder buf = new StringBuilder();

    protected ConcurrentHashMap<String, Object> getData = new ConcurrentHashMap<String, Object>();
    protected ConcurrentHashMap<String, Object> postData = new ConcurrentHashMap<String, Object>();
    protected ConcurrentHashMap<String, Object> serverData = new ConcurrentHashMap<String, Object>();

    protected ArrayList<FileUpload> fileUploadList = new ArrayList<FileUpload>();

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        if (!readingChunks) {
            HttpRequest request = this.request = (HttpRequest) e.getMessage();

            if (is100ContinueExpected(request)) {
                send100Continue(e);
            }

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
                sendServerException(e);
                return;
            }

            logger.info("server data:\t" + serverData);
            logger.info("get data:\t" + getData);
            logger.info("post data:\t" + postData);

            logger.info("file uploads:\t" + fileUploadList);

            if (request.isChunked()) {
                readingChunks = true;
            } else {
                ChannelBuffer content = request.getContent();
                if (content.readable()) {
                    buf.append("CONTENT: " + content.toString(CharsetUtil.UTF_8) + "\r\n");
                }

                //返回结果
                writeResponse(e);
            }
        } else {
            HttpChunk chunk = (HttpChunk) e.getMessage();
            if (chunk.isLast()) {
                readingChunks = false;
                buf.append("END OF CONTENT\r\n");

                HttpChunkTrailer trailer = (HttpChunkTrailer) chunk;
                if (!trailer.trailingHeaders().names().isEmpty()) {
                    buf.append("\r\n");
                    for (String name: trailer.trailingHeaders().names()) {
                        for (String value: trailer.trailingHeaders().getAll(name)) {
                            buf.append("TRAILING HEADER: " + name + " = " + value + "\r\n");
                        }
                    }
                    buf.append("\r\n");
                }

                writeResponse(e);
            } else {
                buf.append("CHUNK: " + chunk.getContent().toString(CharsetUtil.UTF_8) + "\r\n");
            }
        }
    }

    //解析http header 协议等相关信息
    public  void parseServerData(HttpRequest request) {
        serverData.put("VERSION", request.getProtocolVersion());
        serverData.put("HOSTNAME", getHost(request, "unknown"));
        serverData.put("METHOD", request.getMethod());
        serverData.put("REQUEST_URI", request.getUri());

        for (Map.Entry<String, String> h: request.headers()) {
            buf.append("HEADER: " + h.getKey() + " = " + h.getValue() + "\r\n");
            serverData.put(h.getKey(), h.getValue());
        }

        return;
    }

    //解析get数据
    public void parseGetData(HttpRequest request) {
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.getUri());
        Map<String, List<String>> params = queryStringDecoder.getParameters();
        if (!params.isEmpty()) {
            for (Entry<String, List<String>> p: params.entrySet()) {
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
    public void parsePostData(HttpRequest request) throws Exception {

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
    public void sendServerException(MessageEvent e) {
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, NOT_IMPLEMENTED);
        e.getChannel().write(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        e.getCause().printStackTrace();
        e.getChannel().close();
    }

    private static void send100Continue(MessageEvent e) {
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, CONTINUE);
        e.getChannel().write(response);
    }
    private void writeResponse(MessageEvent e) {
        // Decide whether to close the connection or not.
        boolean keepAlive = isKeepAlive(request);

        // Build the response object.
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
        response.setContent(ChannelBuffers.copiedBuffer(buf.toString(), CharsetUtil.UTF_8));
        response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");

        if (keepAlive) {
            // Add 'Content-Length' header only for a keep-alive connection.
            response.headers().set(CONTENT_LENGTH, response.getContent().readableBytes());
            // Add keep alive header as per:
            // - http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
            response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        }

        // Encode the cookie.
        String cookieString = request.headers().get(COOKIE);
        if (cookieString != null) {
            CookieDecoder cookieDecoder = new CookieDecoder();
            Set<Cookie> cookies = cookieDecoder.decode(cookieString);
            if (!cookies.isEmpty()) {
                // Reset the cookies if necessary.
                CookieEncoder cookieEncoder = new CookieEncoder(true);
                for (Cookie cookie : cookies) {
                    cookieEncoder.addCookie(cookie);
                    response.headers().add(SET_COOKIE, cookieEncoder.encode());
                }
            }
        } else {
            // Browser sent no cookie.  Add some.
            CookieEncoder cookieEncoder = new CookieEncoder(true);
            cookieEncoder.addCookie("key1", "value1");
            response.headers().add(SET_COOKIE, cookieEncoder.encode());
            cookieEncoder.addCookie("key2", "value2");
            response.headers().add(SET_COOKIE, cookieEncoder.encode());
        }

        // Write the response.
        ChannelFuture future = e.getChannel().write(response);

        // Close the non-keep-alive connection after the write operation is done.
        if (!keepAlive) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

}
