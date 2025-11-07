package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class MyBot extends TelegramLongPollingBot {
    private final MyBotService myBotService;
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MyBot.class.getName());

    public MyBot(MyBotService myBotService) {
        this.myBotService = myBotService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Long chatId = update.getMessage().getChatId();
            String username = update.getMessage().getFrom().getUserName();
            if (username == null) username = "NoUsername";
            String message = update.getMessage().getText();

            info(chatId, username, message);

            try {
                // 1. STATE TEKSHIRISH — ENG BIRINCHI!
                String state = myBotService.getUserState(chatId);
                if ("WAITING_TASK".equals(state)) {
                    execute(myBotService.addTask(chatId, message));
                    return;
                } else if ("WAITING_NOTE".equals(state)) {
                    execute(myBotService.addNote(chatId, message));
                    return;
                } else if ("WAITING_DONE_TASK".equals(state)) {
                    execute(myBotService.markTaskDone(chatId, message));
                    return;
                }

                // 2. COMMANDLAR — faqat state bo‘lmasa
                if (message.equals("/start")) {
                    execute(myBotService.startWelcome(chatId));
                    execute(myBotService.startMenu(chatId));
                } else if (message.equals("/addtask")) {
                    execute(myBotService.askTaskText(chatId));
                } else if (message.equals("/tasks")) {
                    execute(myBotService.showTasks(chatId));
                } else if (message.equals("/donetask")) {
                    execute(myBotService.askDoneTask(chatId));
                } else if (message.equals("/addnote")) {
                    execute(myBotService.askNoteText(chatId));
                } else if (message.equals("/journal")) {
                    execute(myBotService.showJournal(chatId));
                } else if (message.equals("/stat")) {
                    execute(myBotService.showStats(chatId));
                } else {
                    execute(myBotService.unknownCommand(chatId));
                }
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }

        // Inline callback
        else if (update.hasCallbackQuery()) {
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            String data = update.getCallbackQuery().getData();
            try {
                if (data.startsWith("DONE_")) {
                    int taskId = Integer.parseInt(data.substring(5));
                    execute(myBotService.confirmTaskDone(chatId, taskId));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }


    @Override
    public String getBotUsername() {
        return "***";
    }

    @Override
    public String getBotToken() {
        return "***";
    }


    private void info(Long chatId, String username, String message) {
        logger.info("User: @" + username + " (ID: " + chatId + ") → " + message);
    }

}
