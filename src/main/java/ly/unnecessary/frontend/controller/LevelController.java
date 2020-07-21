package ly.unnecessary.frontend.controller;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;

import static com.almasb.fxgl.dsl.FXGL.getGameWorld;
import static com.almasb.fxgl.dsl.FXGL.set;
import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;
import static ly.unnecessary.frontend.GameApplication.*;

/**
 * Manages the level
 */
public class LevelController {

    /**
     * Set Level by Id from level.txt source in assets/text/level.txt
     *
     * @param levelId id of level (row in level.txt)
     */
    public static void setLevel(int levelId) {

        //validates and verify the levelId
        if (levelId != 100 && levelId != 99) {
            if (levelId >= level.size() || levelId < 0)
                return;
        }

        getGameWorld().getEntitiesCopy().forEach(Entity::removeFromWorld); //removes all entities from the game world (except the WALL)

        String currentLevel = "";
        set("gameStatus", 0);
        set("freeze", false);
        set("level", levelId);
        set("score", 0);

        if (levelId == 100) //random Level
            currentLevel = generateRandomLevel();
        else if (levelId == 99) //empty Level
            currentLevel = "000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
        else
            currentLevel = level.get(levelId).toString();

        spawn("background", 0, 0); //spawns background
        player = spawn("player", playerSpawnPoint); //spawns player
        ball = spawn("ball", ballSpawnPoint); //spawns ball

        // Deprecated for the Release
        //spawn("uiSpawnLevelInfo"); // Spawns UI text

        //Set the boss level
        if (currentLevel.equals("boss")) {
            spawn("boss", 100, 100);
            return;
        }

        //spawns brick based on level string
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

    /**
     * Generates a random level (levelId = 100)
     *
     * @return random level string
     */
    public static String generateRandomLevel() {
        String result = "";

        for (int i = 0; i < 285; i++) {
            result += FXGLMath.random(0, 4);
        }
        return result;
    }
}
