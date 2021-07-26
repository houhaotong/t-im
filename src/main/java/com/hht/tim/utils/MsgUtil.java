package com.hht.tim.utils;

import com.alibaba.fastjson.JSON;
import com.hht.tim.entity.Msg;

/**
 * @author hht
 * @date 2021/7/19
 */
public class MsgUtil {

    public static String buildMsg(String userid,String msg){
        Msg message = new Msg(userid, msg);
        return JSON.toJSONString(message);
    }
}
