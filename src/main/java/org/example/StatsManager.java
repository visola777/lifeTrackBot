package org.example;

import java.util.Calendar;
import java.util.Date;

public class StatsManager {
    private final UserData data;
    private final Calendar cal = Calendar.getInstance();
    private final Date now = new Date();

    public final int todayTasks, todayNotes, weekTasks, weekNotes, monthTasks, monthNotes;
    public final double monthProgress;

    public StatsManager(UserData data) {
        this.data = data;

        cal.setTime(now);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date todayStart = cal.getTime();

        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        Date weekStart = cal.getTime();

        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date monthStart = cal.getTime();

        todayTasks = countDoneTasksSince(todayStart);
        todayNotes = countNotesSince(todayStart);
        weekTasks = countDoneTasksSince(weekStart);
        weekNotes = countNotesSince(weekStart);
        monthTasks = countDoneTasksSince(monthStart);
        monthNotes = countNotesSince(monthStart);

        int total = data.getAllTasks().size();
        monthProgress = total > 0 ? (monthTasks * 100.0 / total) : 0;
    }

    private int countDoneTasksSince(Date start) {
        return (int) data.getDoneTasks().stream()
                .filter(t -> t.getCreated().after(start))
                .count();
    }

    private int countNotesSince(Date start) {
        return (int) data.getNotes().stream()
                .filter(n -> n.getDate().after(start))
                .count();
    }
}
