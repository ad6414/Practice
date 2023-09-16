package com.sky.websocket;

import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@ServerEndpoint("/ws/{cid}")
public class WebSocketServer {
    private static Map<String,Session> sessionMap = new HashMap<>();

    @OnOpen
    public void onLink(@PathParam("cid") String cid,Session session){
        System.out.println("连接建立:" + cid);
        sessionMap.put(cid,session);
    };
    @OnClose
    public void onClose(@PathParam("cid") String cid) {
        System.out.println("连接断开:" + cid);
        sessionMap.remove(cid);
    }
    public void sendToAllClient(String message) {
        Collection<Session> sessions = sessionMap.values();
        for (Session session : sessions) {
            try {
                //服务器向客户端发送消息
                session.getBasicRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
