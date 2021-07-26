package com.hht.tim.controller;

import com.hht.tim.TimWsServerStarter;
import com.hht.tim.constants.ImConst;
import com.hht.tim.utils.MsgUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tio.core.Tio;
import org.tio.server.ServerTioConfig;
import org.tio.websocket.common.WsPacket;
import org.tio.websocket.common.WsResponse;

import javax.annotation.Resource;

/**
 * @author hht
 * @date 2021/7/20
 */
@RestController
@RequestMapping("/api/message")
public class MessageController {

    @Resource
    TimWsServerStarter serverStarter;

    @PostMapping("/send")
    public void sendMessage(String id,String msg){
        ServerTioConfig tioConfig = serverStarter.getServerTioConfig();

        String message= MsgUtil.buildMsg(id,msg);
        WsResponse wsResponse=WsResponse.fromText(message, WsPacket.CHARSET_NAME);
        Tio.sendToGroup(tioConfig, ImConst.GROUP_ID,wsResponse);
    }
}
