package com.flyingpig.chat.websocket.message.resp;

public class WebSocketRespCode {

    /*
        流程
     */

    public static final int RETURN_UNREAD = 201;
    public static final int SEND_SECCESS = 202;
    public static final int RECEIVED_MESSAGE = 203;


    /*
        异常
     */

    public static final int SERVER_ERROR = 500;
    public static final int PARAMETER_ERROR = 400;
}
