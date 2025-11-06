package org.example;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class FileStorage {

    private static final String DATA_DIRECTORY = "bot_data";
    private static final String USERS_FILE = "users.dat";

    public FileStorage() {
        createDataDirectory();
    }

    // Data papkasini yaratish
    private void createDataDirectory() {
        try {
            Path path = Paths.get(DATA_DIRECTORY);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                System.out.println("📁 Data papkasi yaratildi: " + DATA_DIRECTORY);
            }
        } catch (IOException e) {
            System.err.println("❌ Papka yaratishda xatolik: " + e.getMessage());
        }
    }

    // Foydalanuvchini saqlash
    public void saveUser(Long chatId, UserData userData) {
        Map<Long, UserData> allUsers = loadAllUsers();
        allUsers.put(chatId, userData);
        saveAllUsers(allUsers);
    }

    // Barcha foydalanuvchilarni saqlash
    private void saveAllUsers(Map<Long, UserData> users) {
        String filePath = DATA_DIRECTORY + File.separator + USERS_FILE;

        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(filePath))) {
            oos.writeObject(users);
            System.out.println("💾 Ma'lumotlar saqlandi: " + users.size() + " ta foydalanuvchi");
        } catch (IOException e) {
            System.err.println("❌ Saqlashda xatolik: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Barcha foydalanuvchilarni yuklash
    public Map<Long, UserData> loadAllUsers() {
        String filePath = DATA_DIRECTORY + File.separator + USERS_FILE;
        File file = new File(filePath);

        if (!file.exists()) {
            return new HashMap<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(filePath))) {
            @SuppressWarnings("unchecked")
            Map<Long, UserData> users = (Map<Long, UserData>) ois.readObject();
            return users;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("❌ Ma'lumotlarni yuklashda xatolik: " + e.getMessage());
            return new HashMap<>();
        }
    }

    // Bitta foydalanuvchini yuklash
    public UserData loadUser(Long chatId) {
        Map<Long, UserData> allUsers = loadAllUsers();
        return allUsers.get(chatId);
    }

    // Foydalanuvchini o'chirish
    public void deleteUser(Long chatId) {
        Map<Long, UserData> allUsers = loadAllUsers();
        allUsers.remove(chatId);
        saveAllUsers(allUsers);
        System.out.println("🗑️ Foydalanuvchi o'chirildi: " + chatId);
    }

    // Foydalanuvchi mavjudligini tekshirish
    public boolean userExists(Long chatId) {
        Map<Long, UserData> allUsers = loadAllUsers();
        return allUsers.containsKey(chatId);
    }

    // Barcha ma'lumotlarni backup qilish
    public void createBackup() {
        String backupFileName = "backup_" + System.currentTimeMillis() + ".dat";
        String backupPath = DATA_DIRECTORY + File.separator + backupFileName;
        String originalPath = DATA_DIRECTORY + File.separator + USERS_FILE;

        try {
            Files.copy(Paths.get(originalPath), Paths.get(backupPath));
            System.out.println("💾 Backup yaratildi: " + backupFileName);
        } catch (IOException e) {
            System.err.println("❌ Backup yaratishda xatolik: " + e.getMessage());
        }
    }

    // Ma'lumotlar hajmini olish
    public int getUserCount() {
        Map<Long, UserData> allUsers = loadAllUsers();
        return allUsers.size();
    }
}