package data.scripts.stepspeedup;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import org.apache.log4j.Level;

public class StepSpeedupModPlugin extends BaseModPlugin {

    @Override
    public void onApplicationLoad() {
        loadConfig();
    }

    @Override
    public void onGameLoad(boolean newGame) {
        if (StepSpeedupConfig.enableCampaign) {
            StepSpeedupCampaign.resetSpeed();
            Global.getSector().getListenerManager().addListener(new StepSpeedupCampaign(), true);
        }
    }

    @Override
    public void onDevModeF8Reload() {
        loadConfig();
    }

    private static void loadConfig() {
        try {
            StepSpeedupConfig.load();
        } catch (Exception e) {
            Global.getLogger(StepSpeedupModPlugin.class).log(Level.ERROR,
                    "Step Speedup config failed to load: " + e.getMessage());
        }

        Global.getLogger(StepSpeedupModPlugin.class).log(Level.INFO,
                "Step Speedup running with speeds: " + StepSpeedupConfig.getSpeedList());
    }
}
