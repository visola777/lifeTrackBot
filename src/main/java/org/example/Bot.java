package org.example;


import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Bot extends TelegramLongPollingBot {

    // 🔹 Shu yerga o‘z token va username’ingni yoz
    private static final String BOT_TOKEN = "8347639042:AAFXcwdcWL3pTnCVBUNdWf2XBS9IBN_DXlM";
    private static final String BOT_USERNAME = "lifetrack_java_bot";

    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            String text = update.getMessage().getText();

            SendMessage message = new SendMessage(chatId, "Senga javob: " + text);
            try {
                wait(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
}

