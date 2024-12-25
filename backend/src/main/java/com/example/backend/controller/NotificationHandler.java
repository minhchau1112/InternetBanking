package com.example.backend.controller;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.ArrayList;
import java.util.List;

@Component
public class NotificationHandler extends TextWebSocketHandler {
    private static final List<WebSocketSession> sessions = new ArrayList<>();

    public NotificationHandler() {

    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        System.out.println("Attributes before setup: " + session.getAttributes());
        String query = session.getUri().getQuery();
        System.out.println("Query: " + query);

        if (query != null) {
            String[] params = query.split("&");
            for (String param : params) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2 && "account".equals(keyValue[0])) {
                    session.getAttributes().put("account", keyValue[1]);
                    break;
                }
            }
        }
        System.out.println("Attributes after setup: " + session.getAttributes());
        sessions.add(session);
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
    }

    public void sendNotification(String message, String recipientAccount) {
        System.out.println("sendNotification");

        for (WebSocketSession session : sessions) {
            System.out.println("attribute: " + session.getAttributes());
            try {
                System.out.println("attribute count: " + session.getAttributes().get("account"));
                System.out.println("recipientAccount: " + recipientAccount);
                if (session.getAttributes().get("account").equals(recipientAccount)) {
                    session.sendMessage(new TextMessage(message));
                    System.out.println("SEND MESSAGE SUCCESS");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

//import org.springframework.stereotype.Component;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.socket.*;
//import org.springframework.web.socket.handler.TextWebSocketHandler;
//
//import java.util.concurrent.CopyOnWriteArrayList;
//
////@CrossOrigin(origins = "http://localhost:5173")
//@Component
//public class NotificationHandler extends TextWebSocketHandler {
//
//    private final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
//
//    @Override
//    public void afterConnectionEstablished(WebSocketSession session) {
//        sessions.add(session);
//        System.out.println("New WebSocket connection: " + session.getId());
//    }
//
//    @Override
//    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
//        // Xử lý tin nhắn từ client (nếu cần)
//        System.out.println("Received message: " + message.getPayload());
//    }
//
//    @Override
//    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
//        sessions.remove(session);
//        System.out.println("WebSocket closed: " + session.getId());
//    }
//
//    public void sendNotification(String message) {
//        sessions.forEach(session -> {
//            try {
//                session.sendMessage(new TextMessage(message));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//    }
//}

