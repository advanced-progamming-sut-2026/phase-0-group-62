package util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import model.User;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private static final String FOLDER_PATH = "database";
    private static final String FILE_PATH = FOLDER_PATH + "/users.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();


    public static void saveUsers(List<User> users) {
        // ۱. ساختنِ پوشه اگر وجود ندارد
        File folder = new File(FOLDER_PATH);
        if (!folder.exists()) {
            folder.mkdir();
        }

        // ۲. نوشتنِ فایل
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            gson.toJson(users, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isUsernameExists(String username) {
        List<User> users = loadUsers();
        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }


    public static List<User> loadUsers() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (FileReader reader = new FileReader(FILE_PATH)) {
            // خواندن لیست از فایل
            List<User> users = gson.fromJson(reader, new TypeToken<List<User>>(){}.getType());


            return (users != null) ? users : new ArrayList<>();

        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}