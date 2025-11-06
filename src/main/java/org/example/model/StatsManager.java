import java.util.List;

public class StatsManager {

    public static String getStats(UserData userData) {
        List<Task> tasks = userData.getTasks();
        List<String> journal = userData.getJournal();

        long now = System.currentTimeMillis();
        long dayMs = 24 * 60 * 60 * 1000;
        long weekMs = 7 * dayMs;
        long monthMs = 30 * dayMs;

        int todayDone = 0;
        int weekDone = 0;
        int monthDone = 0;
        int totalDone = 0;

        for (Task task : tasks) {
            if (task.isDone()) {
                totalDone++;
                long age = now - task.getCreatedAt();
                if (age <= dayMs) todayDone++;
                if (age <= weekMs) weekDone++;
                if (age <= monthMs) monthDone++;
            }
        }

        int totalTasks = tasks.size();
        int progress = totalTasks > 0 ? (totalDone * 100 / totalTasks) : 0;

        return "📊 Statistika:\n\n" +
                "📅 Bugun: " + todayDone + " ta vazifa bajarildi\n" +
                "📆 Haftalik: " + weekDone + " ta vazifa bajarildi\n" +
                "📈 Oylik: " + monthDone + " ta vazifa bajarildi\n" +
                "📝 Jami yozuvlar: " + journal.size() + " ta\n" +
                "✅ Umumiy progress: " + progress + "%";
    }
}