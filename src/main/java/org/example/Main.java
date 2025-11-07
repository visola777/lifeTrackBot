package org.example;


import org.telegram.telegrambots.meta.TelegramBotsApi;

import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            MyBotService service = new MyBotService();
            botsApi.registerBot(new MyBot(service));
            System.out.println("LifeTrackBot is running successfully!");
        } catch (org.telegram.telegrambots.meta.exceptions.TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}