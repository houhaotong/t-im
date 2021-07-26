package com.hht.tim.component;

import com.hht.tim.constants.ImConst;
import com.hht.tim.utils.MsgUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResponse;
import org.tio.utils.lock.SetWithLock;
import org.tio.websocket.common.WsPacket;
import org.tio.websocket.common.WsRequest;
import org.tio.websocket.common.WsResponse;
import org.tio.websocket.common.WsSessionContext;
import org.tio.websocket.server.handler.IWsMsgHandler;

import java.util.Objects;

/**
 * @author hht
 * @date 2021/7/19
 */
@Slf4j
@Component
public class CustomWsMsgHandler implements IWsMsgHandler {

    /**
     * 握手走这里，可以获取cookie，request等参数
     * @param httpRequest
     * @param httpResponse
     * @param channelContext
     * @return
     * @throws Exception
     */
    @Override
    public HttpResponse handshake(HttpRequest httpRequest, HttpResponse httpResponse, ChannelContext channelContext) throws Exception {

        String clientIp = httpRequest.getClientIp();
        String name = httpRequest.getParam("name");
        //绑定用户
        Tio.bindUser(channelContext,name);

        log.info("收到来自{}的握手包\n",clientIp);
        return httpResponse;
    }

    /**
     * 握手之后的操作,可以绑定群发
     * @param httpRequest
     * @param httpResponse
     * @param channelContext
     * @throws Exception
     */
    @Override
    public void onAfterHandshaked(HttpRequest httpRequest, HttpResponse httpResponse, ChannelContext channelContext) throws Exception {
        //绑定群组
        Tio.bindGroup(channelContext, ImConst.GROUP_ID);
        //建立连接后，群发进群信息
        int count = Tio.getAll(channelContext.tioConfig).getObj().size();
        String msg="{name:'admin',message:'"+channelContext.userid+"进来了，当前共有"+count+"人'}";
        //tio-websocket中，服务器对客户端发送的信息都是WsResponse
        WsResponse wsResponse=WsResponse.fromText(msg, WsPacket.CHARSET_NAME);
        //群发消息
        Tio.sendToGroup(channelContext.tioConfig,ImConst.GROUP_ID,wsResponse);
    }

    @Override
    public Object onBytes(WsRequest wsRequest, byte[] bytes, ChannelContext channelContext) throws Exception {
        return null;
    }

    @Override
    public Object onClose(WsRequest wsRequest, byte[] bytes, ChannelContext channelContext) throws Exception {
        Tio.remove(channelContext,"close");
        return null;
    }

    /**
     * 收到了文字消息，走这里
     * @param wsRequest
     * @param text
     * @param channelContext
     * @return
     * @throws Exception
     */
    @Override
    public Object onText(WsRequest wsRequest, String text, ChannelContext channelContext) throws Exception {

        WsSessionContext wsSessionContext = (WsSessionContext) channelContext.get();

        //心跳包过滤
        if(Objects.equals(ImConst.HEARTBEAT,text)){
            return null;
        }
        String msg= MsgUtil.buildMsg(channelContext.userid,text);
        //用tio-websocket，服务器发送到客户端的Packet都是WsResponse
        WsResponse wsResponse=WsResponse.fromText(msg,WsPacket.CHARSET_NAME);
        Tio.sendToGroup(channelContext.tioConfig,ImConst.GROUP_ID,wsResponse);
        //返回值是要返回给客户端的值，一般为null
        return null;
    }
}
