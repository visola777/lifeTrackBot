package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class LifeTrackBot extends TelegramLongPollingBot {

    private BotService botService;

    public LifeTrackBot(BotService botService) {
        this.botService = botService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            String username = update.getMessage().getFrom().getUserName();

            Info(chatId, username, message);

            try {
                execute(botService.handleMessage(chatId, message));
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public String getBotUsername() {
        return "LifeTrackBot";
    }

    @Override
    public String getBotToken() {
        return "8347639042:AAFXcwdcWL3pTnCVBUNdWf2XBS9IBN_DXlM";
    }

    public void Info(Long chatId, String username, String message) {
        System.out.println(chatId + " " + username + " " + message);
    }
}