package org.example;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BotService {
    private Map<Long, UserData> users;
    private Map<Long, String> userStates;

    public BotService() {
        this.users = new HashMap<>();
        this.userStates = new HashMap<>();
    }

    public UserData getUserData(Long chatId) {
        if (!users.containsKey(chatId)) {
            users.put(chatId, new UserData());
        }
        return users.get(chatId);
    }

    public void setUserState(Long chatId, String state) {
        userStates.put(chatId, state);
    }

    public String getUserState(Long chatId) {
        return userStates.get(chatId);
    }

    public SendMessage startMenu(Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("🎯 LifeTrackBot ga xush kelibsiz!\n\n" +
                "Men sizning kundalik hayotingizni tartibga solishga yordam beraman.\n\n" +
                "Buyruqlar ro'yxati:");

        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        List<KeyboardRow> rowList = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("➕ Vazifa qo'shish"));
        row1.add(new KeyboardButton("📋 Vazifalar"));

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("✅ Vazifa bajarildi"));
        row2.add(new KeyboardButton("📝 Yozuv qo'shish"));

        KeyboardRow row3 = new KeyboardRow();
        row3.add(new KeyboardButton("📖 Kundalik"));
        row3.add(new KeyboardButton("📊 Statistika"));

        rowList.add(row1);
        rowList.add(row2);
        rowList.add(row3);

        keyboard.setKeyboard(rowList);
        keyboard.setResizeKeyboard(true);
        sendMessage.setReplyMarkup(keyboard);

        return sendMessage;
    }

    public SendMessage handleMessage(Long chatId, String text) {
        SendMessage response = new SendMessage();
        response.setChatId(chatId);

        String state = getUserState(chatId);

        if (state != null && state.equals("ADDING_TASK")) {
            getUserData(chatId).addTask(text);
            response.setText("✅ Vazifa muvaffaqiyatli qo'shildi!");
            setUserState(chatId, null);
            return response;
        }

        if (state != null && state.equals("ADDING_NOTE")) {
            getUserData(chatId).addJournalEntry(text);
            response.setText("✅ Kundalik yozuv saqlandi!");
            setUserState(chatId, null);
            return response;
        }

        if (state != null && state.equals("MARKING_DONE")) {
            try {
                int index = Integer.parseInt(text) - 1;
                getUserData(chatId).markTaskDone(index);
                response.setText("✅ Vazifa bajarilgan deb belgilandi!");
            } catch (NumberFormatException e) {
                response.setText("❌ Iltimos, raqam kiriting!");
            }
            setUserState(chatId, null);
            return response;
        }

        switch (text) {
            case "/start":
                return startMenu(chatId);

            case "➕ Vazifa qo'shish":
            case "/addtask":
                setUserState(chatId, "ADDING_TASK");
                response.setText("📝 Yangi vazifani yozing:");
                break;

            case "📋 Vazifalar":
            case "/tasks":
                List<Task> tasks = getUserData(chatId).getTasks();
                if (tasks.isEmpty()) {
                    response.setText("📋 Sizda hali vazifa yo'q!");
                } else {
                    StringBuilder sb = new StringBuilder("📋 Vazifalar ro'yxati:\n\n");
                    for (int i = 0; i < tasks.size(); i++) {
                        sb.append((i + 1)).append(". ").append(tasks.get(i).toString()).append("\n");
                    }
                    response.setText(sb.toString());
                }
                break;

            case "✅ Vazifa bajarildi":
            case "/donetask":
                List<Task> allTasks = getUserData(chatId).getTasks();
                if (allTasks.isEmpty()) {
                    response.setText("📋 Sizda hali vazifa yo'q!");
                } else {
                    StringBuilder sb = new StringBuilder("Bajarilgan vazifa raqamini kiriting:\n\n");
                    for (int i = 0; i < allTasks.size(); i++) {
                        if (!allTasks.get(i).isDone()) {
                            sb.append((i + 1)).append(". ").append(allTasks.get(i).getText()).append("\n");
                        }
                    }
                    response.setText(sb.toString());
                    setUserState(chatId, "MARKING_DONE");
                }
                break;

            case "📝 Yozuv qo'shish":
            case "/addnote":
                setUserState(chatId, "ADDING_NOTE");
                response.setText("📖 Kundalik yozuvingizni yozing:");
                break;

            case "📖 Kundalik":
            case "/journal":
                List<String> journal = getUserData(chatId).getJournal();
                if (journal.isEmpty()) {
                    response.setText("📖 Sizda hali yozuv yo'q!");
                } else {
                    StringBuilder sb = new StringBuilder("📖 Kundalik yozuvlar:\n\n");
                    for (int i = 0; i < journal.size(); i++) {
                        sb.append((i + 1)).append(". ").append(journal.get(i)).append("\n\n");
                    }
                    response.setText(sb.toString());
                }
                break;

            case "📊 Statistika":
            case "/stat":
                response.setText(StatsManager.getStats(getUserData(chatId)));
                break;

            default:
                response.setText("❌ Noma'lum buyruq. /start ni bosing.");
        }

        return response;
    }
}