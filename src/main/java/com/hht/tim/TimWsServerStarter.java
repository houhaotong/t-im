package com.hht.tim;

import com.hht.tim.component.CustomWsAioListener;
import com.hht.tim.component.CustomWsMsgHandler;
import com.hht.tim.constants.ImConst;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.tio.server.ServerTioConfig;
import org.tio.websocket.server.WsServerStarter;

import java.io.IOException;

/**
 * @author hht
 * @date 2021/7/20
 */
@Component
public class TimWsServerStarter implements CommandLineRunner {

    private WsServerStarter wsServerStarter;

    private ServerTioConfig serverTioConfig;

    private CustomWsMsgHandler handler;

    public TimWsServerStarter(CustomWsMsgHandler handler) throws IOException {
        wsServerStarter=new WsServerStarter(ImConst.SERVER_PORT,handler);
        serverTioConfig=wsServerStarter.getServerTioConfig();
        //设置超时时间
        serverTioConfig.setHeartbeatTimeout(ImConst.TIMEOUT);
        //设置监听器
        serverTioConfig.setServerAioListener(new CustomWsAioListener());

        serverTioConfig.setName(ImConst.PROTOCOL_NAME);

        this.handler=handler;
    }

    @Override
    public void run(String... args) throws Exception {
        TimWsServerStarter timWsServerStarter = new TimWsServerStarter(handler);
        timWsServerStarter.wsServerStarter.start();
    }

    public WsServerStarter getWsServerStarter() {
        return wsServerStarter;
    }

    public ServerTioConfig getServerTioConfig() {
        return serverTioConfig;
    }
}
