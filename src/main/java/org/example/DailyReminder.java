package org.example;

import java.time.LocalTime;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class DailyReminder {

    private final LifeTrackBot bot;
    private final FileStorage fileStorage;
    private final Timer timer;

    // Eslatma vaqti (09:00)
    private static final int REMINDER_HOUR = 9;
    private static final int REMINDER_MINUTE = 0;

    public DailyReminder(LifeTrackBot bot, FileStorage fileStorage) {
        this.bot = bot;
        this.fileStorage = fileStorage;
        this.timer = new Timer(true);
    }

    public void start() {
        // Har 1 soatda bir tekshirish
        long oneHour = 60 * 60 * 1000;

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkAndSendReminders();
            }
        }, 0, oneHour);

        System.out.println("⏰ Kundalik eslatmalar tizimi ishga tushdi");
    }

    private void checkAndSendReminders() {
        LocalTime now = LocalTime.now();

        // Faqat 09:00 da eslatma yuborish
        if (now.getHour() == REMINDER_HOUR && now.getMinute() < 60) {
            sendRemindersToAllUsers();
        }
    }

    private void sendRemindersToAllUsers() {
        Map<Long, UserData> allUsers = fileStorage.loadAllUsers();

        if (allUsers == null || allUsers.isEmpty()) {
            return;
        }

        System.out.println("📢 Kundalik eslatmalar yuborilmoqda...");

        for (Map.Entry<Long, UserData> entry : allUsers.entrySet()) {
            Long chatId = entry.getKey();
            UserData userData = entry.getValue();

            String reminderText = createReminderMessage(userData);
            bot.sendReminder(chatId, reminderText);

            try {
                Thread.sleep(100); // Spam oldini olish uchun
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("✅ Eslatmalar yuborildi: " + allUsers.size() + " ta foydalanuvchi");
    }

    private String createReminderMessage(UserData userData) {
        int uncompletedTasks = userData.getTotalTasks() - userData.getCompletedTasks();

        StringBuilder message = new StringBuilder();
        message.append("🌅 Xayrli tong!\n\n");

        if (uncompletedTasks > 0) {
            message.append("📋 Sizda ").append(uncompletedTasks)
                    .append(" ta bajarilmagan vazifa bor.\n\n");
            message.append("Bugun ularni bajarishni boshlaysizmi? 💪\n");
            message.append("Vazifalaringizni ko'rish uchun: /tasks");
        } else {
            message.append("🎉 Barcha vazifalar bajarilgan!\n\n");
            message.append("Bugun yangi vazifalar qo'shishingiz mumkin: /addtask");
        }

        return message.toString();
    }

    public void stop() {
        timer.cancel();
        System.out.println("⏰ Eslatmalar tizimi to'xtatildi");
    }
}