package ly.unnecessary.frontend.controller;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.SpawnData;

import java.util.stream.IntStream;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;
import static ly.unnecessary.frontend.GameApplication.*;

public class LevelController {
    public static void setLevel(int levelId) {
        if (levelId != 10) {
            if (levelId >= level.size() || levelId < 0)
                return;
        }

        getGameWorld().getEntitiesCopy().forEach(e -> e.removeFromWorld());



        String currentLevel = "";
        set("gameStatus", 0);
        set("freeze", false);
        set("level", levelId);
        set("score", 0);

        if (levelId == 10) {
            currentLevel = generateRandomLevel();
        } else
            currentLevel = level.get(levelId).toString();

        spawn("background", 0, 0);
        player = spawn("player", playerSpawnPoint);
        ball = spawn("ball", ballSpawnPoint);

        // Ui
        spawn("uiSpawnLevelInfo");

        if (currentLevel.equals("boss")) {
            spawn("boss", 100, 100);
            return;
        }

        int i = 0, x = 0, y = levelMargin;
        for (int row = 0; row < levelRows; row++) {
            for (int col = 0; col < 1920 / brickWidth; col++) {
                if (currentLevel.charAt(i) == '1') // 1 life
                    spawn("brick", new SpawnData(x, y).put("type", 1));
                else if (currentLevel.charAt(i) == '2') // 2 life
                    spawn("brick", new SpawnData(x, y).put("type", 2));
                else if (currentLevel.charAt(i) == '3') // 3 life
                    spawn("brick", new SpawnData(x, y).put("type", 3));
                else if (currentLevel.charAt(i) == 'X') // not destroyable
                    spawn("levelBrick", x, y);
                i++;
                x += brickWidth;
            }
            x = 0;
            y += brickHeight;
        }
    }


    public static String generateRandomLevel() {
        String result = "";
        for (int i = 0; i < 285; i++) {
            result += FXGLMath.randomBoolean() ? "1" : "0";
        }
        return result;
    }
}
