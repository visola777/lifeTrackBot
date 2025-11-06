import java.util.ArrayList;
import java.util.List;

public class UserData {
    private List<Task> tasks;
    private List<String> journal;

    public UserData() {
        this.tasks = new ArrayList<>();
        this.journal = new ArrayList<>();
    }

    public void addTask(String taskText) {
        tasks.add(new Task(taskText));
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void markTaskDone(int index) {
        if (index >= 0 && index < tasks.size()) {
            tasks.get(index).setDone(true);
        }
    }

    public void addJournalEntry(String entry) {
        journal.add(entry);
    }

    public List<String> getJournal() {
        return journal;
    }
}