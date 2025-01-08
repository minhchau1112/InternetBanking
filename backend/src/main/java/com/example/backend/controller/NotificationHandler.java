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