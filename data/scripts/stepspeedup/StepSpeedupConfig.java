package data.scripts.stepspeedup;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.input.InputEventAPI;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;

public class StepSpeedupConfig {

    public static final String MOD_ID = "stepspeedup";
    private static final String CONFIG_FILE = "data/config/stepspeedup.json";

    public static float[] speeds = {1f, 2f, 4f, 6f, 8f, 16f};
    public static Set<Integer> speedUpKeys = new HashSet();
    public static Set<Integer> slowDownKeys = new HashSet();
    public static boolean allowKeyRepeat = false;
    public static boolean resetEachBattle = true;
    public static boolean enableCampaign = true;
    public static boolean showMessages = true;

    public static void load() throws Exception {
        JSONObject json = Global.getSettings().loadJSON(CONFIG_FILE, MOD_ID);

        speeds = readSpeeds(json.optJSONArray("speeds"));
        speedUpKeys = readKeys(json.getJSONArray("speedUpKeys"));
        slowDownKeys = readKeys(json.getJSONArray("slowDownKeys"));
        allowKeyRepeat = json.optBoolean("allowKeyRepeat", false);
        resetEachBattle = json.optBoolean("resetEachBattle", true);
        enableCampaign = json.optBoolean("enableCampaign", true);
        showMessages = json.optBoolean("showMessages", true);
    }

    /**
     * Returns the configured speeds plus x1 and x2 if they are missing, sorted.
     */
    private static float[] readSpeeds(JSONArray array) throws Exception {
        // 1) Add all the speeds from the json
        List list = new ArrayList();
        if (array != null) {
            for (int i = 0; i < array.length(); i++) {
                float speed = (float) array.getDouble(i);
                if (speed <= 0f) {
                    throw new IllegalArgumentException("\"speeds\" entries must be greater than 0");
                }
                list.add(Float.valueOf(speed));
            }
        }

        // 2) Append x1 and x2 if they are missing
        if (!list.contains(Float.valueOf(1f))) {
            list.add(Float.valueOf(1f));
        }
        if (!list.contains(Float.valueOf(2f))) {
            list.add(Float.valueOf(2f));
        }

        // 3) Sort
        float[] result = new float[list.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = ((Float) list.get(i)).floatValue();
        }
        Arrays.sort(result);
        return result;
    }

    private static Set<Integer> readKeys(JSONArray array) throws Exception {
        Set<Integer> keys = new HashSet();
        for (int i = 0; i < array.length(); i++) {
            keys.add(Integer.valueOf(array.getInt(i)));
        }
        return keys;
    }

    /**
     * Consumes speed up/down key presses and returns the new speed index.
     */
    public static int processInput(List<InputEventAPI> events, int speedIndex) {
        // Index loop with a cast: Janino ignores generic type arguments
        for (int i = 0; i < events.size(); i++) {
            InputEventAPI event = (InputEventAPI) events.get(i);
            if (event.isConsumed() || !event.isKeyDownEvent()) {
                continue;
            }
            // Leave Ctrl/Alt combos to the game and other mods
            if (event.isCtrlDown() || event.isAltDown()) {
                continue;
            }

            Integer key = Integer.valueOf(event.getEventValue());
            int step = speedUpKeys.contains(key) ? 1 : slowDownKeys.contains(key) ? -1 : 0;
            if (step == 0) {
                continue;
            }

            // Read event data before consuming: consumed events throw on access
            boolean ignoreRepeat = event.isRepeat() && !allowKeyRepeat;
            event.consume();
            if (ignoreRepeat) {
                continue;
            }
            speedIndex = clampSpeedIndex(speedIndex + step);
        }
        return speedIndex;
    }

    /**
     * Returns the index of x1, which readSpeeds() guarantees is in the list.
     */
    public static int getNormalSpeedIndex() {
        for (int i = 0; i < speeds.length; i++) {
            if (speeds[i] == 1f) {
                return i;
            }
        }
        return 0;
    }

    /**
     * Returns the index of x2, which readSpeeds() guarantees is in the list.
     */
    public static int getDefaultSpeedupIndex() {
        for (int i = 0; i < speeds.length; i++) {
            if (speeds[i] == 2f) {
                return i;
            }
        }
        return 0;
    }

    public static int clampSpeedIndex(int speedIndex) {
        return Math.max(0, Math.min(speeds.length - 1, speedIndex));
    }

    // Same colors as Misc.getTextColor() / Misc.getHighlightColor()
    public static Color textColor() {
        return Global.getSettings().getColor("standardTextColor");
    }

    public static Color highlightColor() {
        return Global.getSettings().getColor("buttonShortcut");
    }

    /**
     * Returns the speeds as a readable list, e.g. "x1, x2, x4".
     */
    public static String getSpeedList() {
        String speedList = "";
        for (int i = 0; i < speeds.length; i++) {
            if (i > 0) {
                speedList += ", ";
            }
            speedList += formatSpeed(speeds[i]);
        }
        return speedList;
    }

    public static String formatSpeed(float speed) {
        // Whole numbers print without a decimal ("x4"), anything else to one decimal ("x1.5")
        if (Math.abs(speed - Math.round(speed)) < 0.01f) {
            return "x" + Math.round(speed);
        }
        return "x" + (Math.round(speed * 10f) / 10f);
    }
}
