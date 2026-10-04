package data.scripts.stepspeedup;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.listeners.CampaignInputListener;
import com.fs.starfarer.api.input.InputEventAPI;
import java.awt.Color;
import java.util.List;

public class StepSpeedupCampaign implements CampaignInputListener {

    private static final String SETTING = "campaignSpeedupMult";
    private static final String SOUND_ID = "ui_noise_static_message";

    private static int speedIndex = StepSpeedupConfig.getDefaultSpeedupIndex();
    private static boolean speedListShown = false;

    public static void resetSpeed() {
        speedIndex = StepSpeedupConfig.getDefaultSpeedupIndex();
        Global.getSettings().setFloat(SETTING, StepSpeedupConfig.speeds[speedIndex]);
    }

    @Override
    public void processCampaignInputPreFleetControl(List<InputEventAPI> events) {
        int newSpeedIndex = StepSpeedupConfig.processInput(events, speedIndex);
        if (newSpeedIndex == speedIndex) {
            return;
        }
        speedIndex = newSpeedIndex;

        // Show the speed list once, on the first recognized key press
        if (!speedListShown) {
            speedListShown = true;
            Global.getSector().getCampaignUI().addMessage(
                    "Step Speedup running with speeds: " + StepSpeedupConfig.getSpeedList(),
                    StepSpeedupConfig.textColor());
        }

        float speed = StepSpeedupConfig.speeds[speedIndex];
        Global.getSettings().setFloat(SETTING, speed);
        String isFF = Global.getSector().getCampaignUI().isFastForward() ? "enabled" : "disabled";

        if (StepSpeedupConfig.showMessages) {
            String label = StepSpeedupConfig.formatSpeed(speed);
            Global.getSector().getCampaignUI().addMessage("Campaign Fast-forward speed " + label + " (" + isFF + ")",
                    StepSpeedupConfig.textColor(), label, "", StepSpeedupConfig.highlightColor(), Color.BLACK);
            Global.getSoundPlayer().playUISound(SOUND_ID, 1f, 0.5f);
        }
    }

    @Override
    public void processCampaignInputPreCore(List<InputEventAPI> events) {
    }

    @Override
    public void processCampaignInputPostCore(List<InputEventAPI> events) {
    }

    @Override
    public int getListenerInputPriority() {
        return 1;
    }
}
