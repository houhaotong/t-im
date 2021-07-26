package com.hht.tim.component;

import com.hht.tim.constants.ImConst;
import com.hht.tim.entity.Msg;
import com.hht.tim.utils.MsgUtil;
import org.springframework.stereotype.Component;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;
import org.tio.websocket.common.WsPacket;
import org.tio.websocket.common.WsResponse;
import org.tio.websocket.common.WsSessionContext;
import org.tio.websocket.server.WsServerAioListener;

/**
 * @author hht
 * @date 2021/7/20
 */
@Component
public class CustomWsAioListener extends WsServerAioListener {

    @Override
    public void onBeforeClose(ChannelContext channelContext, Throwable throwable, String remark, boolean isRemove) throws Exception {
        super.onBeforeClose(channelContext, throwable, remark, isRemove);

        WsSessionContext sessionContext=(WsSessionContext) channelContext.get();

        if(sessionContext!=null && sessionContext.isHandshaked()){
            int count = Tio.getAll(channelContext.tioConfig).getObj().size()-1;
            String msg= MsgUtil.buildMsg("admin",channelContext.userid+"离开了群聊，目前共有【"+count+"】人");
            WsResponse wsResponse=WsResponse.fromText(msg, WsPacket.CHARSET_NAME);
            Tio.sendToGroup(channelContext.tioConfig, ImConst.GROUP_ID,wsResponse);
        }
    }

}
