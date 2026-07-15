package model.entities.zombie.loader;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.entities.zombie.Zombie;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ZombieLoader {
    private static final String FILE_PATH = "database/zombies.json";
    private static final Gson gson = new Gson();

    public static List<Zombie> loadZombies() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (FileReader reader = new FileReader(file)) {
            List<Zombie> zombies = gson.fromJson(reader, new TypeToken<List<Zombie>>(){}.getType());
            if (zombies != null) {
                return zombies;
            }
            return new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}