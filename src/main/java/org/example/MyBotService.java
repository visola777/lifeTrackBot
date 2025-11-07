package org.example;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MyBotService {
    private final Map<Long, UserData> userDataMap = new ConcurrentHashMap<>();
    private final Map<Long, String> userState = new ConcurrentHashMap<>();

    private UserData getUser(Long chatId) {
        return userDataMap.computeIfAbsent(chatId, id -> new UserData());
    }

    public String getUserState(Long chatId) {
        return userState.getOrDefault(chatId, "");
    }

    public SendMessage startWelcome(Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Salom! LifeTrackBot ga xush kelibsiz!\n\nHayotingizni tartibga soling: vazifalar, kundalik, statistika.\n\nBuyruqlar:");
        return sendMessage;
    }

    public SendMessage startMenu(Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("/addtask — Yangi vazifa\n/tasks — Vazifalar ro‘yxati\n/donetask — Vazifani bajarildi deb belgilash\n/addnote — Kundalik yozuv\n/journal — Barcha yozuvlar\n/stat — Statistika");
        return sendMessage;
    }

    public SendMessage askTaskText(Long chatId) {
        userState.put(chatId, "WAITING_TASK");
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Yangi vazifa matnini yuboring:");
        return sendMessage;
    }

    public SendMessage addTask(Long chatId, String text) {
        userState.remove(chatId);
        if (text == null || text.trim().isEmpty()) {
            userState.put(chatId, "WAITING_TASK");
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText("Iltimos, vazifa matnini yuboring (bo‘sh emas).");
            return sendMessage;
        }
        getUser(chatId).addTask(text);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Vazifa qo‘shildi: *" + escapeMarkdown(text.trim()) + "*");
        sendMessage.setParseMode("Markdown");
        return sendMessage;
    }
    //a
    private String escapeMarkdown(String text) {
        return text.replace("_", "\\_")
                .replace("*", "\\*")
                .replace("[", "\\[")
                .replace("`", "\\`");
    }

    private String truncate(String text, int max) {
        return text.length() > max ? text.substring(0, max) + "..." : text;
    }

    public SendMessage showTasks(Long chatId) {
        UserData data = getUser(chatId);
        List<Task> tasks = data.getTasks();
        if (tasks.isEmpty()) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText("Vazifalar yo‘q. /addtask bilan qo‘shing.");
            return sendMessage;
        }

        StringBuilder sb = new StringBuilder("*Vazifalar:*\n\n");
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (Task task : tasks) {
            String status = task.isDone() ? "[Bajarildi]" : "[Bajarilmagan]";
            sb.append(status).append(" *").append(task.getId()).append(".* ")
                    .append(escapeMarkdown(task.getText())).append("\n");

            if (!task.isDone()) {
                InlineKeyboardButton btn = new InlineKeyboardButton();
                btn.setText("Bajarildi #" + task.getId() + " – " + truncate(task.getText(), 20));
                btn.setCallbackData("DONE_" + task.getId());
                List<InlineKeyboardButton> row = new ArrayList<>();
                row.add(btn);
                rows.add(row);
            }
        }

        markup.setKeyboard(rows);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(sb.toString());
        sendMessage.setReplyMarkup(markup);
        sendMessage.setParseMode("Markdown"); // Markdown format
        return sendMessage;
    }

    public SendMessage confirmTaskDone(Long chatId, int taskId) {
        UserData data = getUser(chatId);
        Task task = data.getTasks().stream()
                .filter(t -> t.getId() == taskId)
                .findFirst()
                .orElse(null);

        boolean done = data.markTaskDone(taskId);
        String text;
        if (done && task != null) {
            text = String.format("*Vazifa bajarildi!*\n#%d – %s", taskId, escapeMarkdown(task.getText()));
        } else {
            text = "Vazifa topilmadi yoki allaqachon bajarilgan.";
        }

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        sendMessage.setParseMode("Markdown");
        return sendMessage;
    }

    public SendMessage askDoneTask(Long chatId) {
        userState.put(chatId, "WAITING_DONE_TASK");
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Bajarilgan vazifa raqamini yuboring (masalan: 1):");
        return sendMessage;
    }

    public SendMessage markTaskDone(Long chatId, String input) {
        userState.remove(chatId);
        try {
            int id = Integer.parseInt(input.trim());
            boolean done = getUser(chatId).markTaskDone(id);
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText(done ? "Vazifa #" + id + " bajarildi!" : "Vazifa topilmadi.");
            return sendMessage;
        } catch (Exception e) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText("Raqam kiriting (masalan: 1)");
            return sendMessage;
        }
    }

    public SendMessage askNoteText(Long chatId) {
        userState.put(chatId, "WAITING_NOTE");
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Kundalik yozuvni kiriting:");
        return sendMessage;
    }

    public SendMessage addNote(Long chatId, String text) {
        userState.remove(chatId);
        getUser(chatId).addNote(text);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Yozuv saqlandi: " + text);
        return sendMessage;
    }

    public SendMessage showJournal(Long chatId) {
        List<Note> notes = getUser(chatId).getNotes();
        if (notes.isEmpty()) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText("Kundalik yozuvlar yo‘q.");
            return sendMessage;
        }

        StringBuilder sb = new StringBuilder("Kundalik yozuvlar:\n\n");
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        for (Note note : notes) {
            sb.append(sdf.format(note.getDate()))
                    .append("\n")
                    .append(note.getText())
                    .append("\n\n");
        }

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(sb.toString());
        return sendMessage;
    }

    public SendMessage showStats(Long chatId) {
        StatsManager stats = new StatsManager(getUser(chatId));
        String text = String.format(
                "Bugun:\n" +
                        "• Vazifa: %d\n" +
                        "• Yozuv: %d\n\n" +
                        "Hafta:\n" +
                        "• Vazifa: %d\n" +
                        "• Yozuv: %d\n\n" +
                        "Oy:\n" +
                        "• Vazifa: %d\n" +
                        "• Yozuv: %d\n" +
                        "• Progress: %.1f%%\n",
                stats.todayTasks, stats.todayNotes,
                stats.weekTasks, stats.weekNotes,
                stats.monthTasks, stats.monthNotes,
                stats.monthProgress
        );

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        return sendMessage;
    }

    public SendMessage unknownCommand(Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Noma‘lum buyruq. /start ni bosing.");
        return sendMessage;
    }
}
