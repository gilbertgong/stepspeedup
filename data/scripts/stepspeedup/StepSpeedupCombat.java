package data.scripts.stepspeedup;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import java.util.List;

public class StepSpeedupCombat extends BaseEveryFrameCombatPlugin {

    private static final String STAT_ID = "stepspeedup_combat";
    private static final String SOUND_ID = "ui_noise_static_message";

    // Static so the speed can carry over between battles when resetEachBattle is off
    private static int speedIndex = StepSpeedupConfig.getNormalSpeedIndex();

    private CombatEngineAPI engine;

    @Override
    public void init(CombatEngineAPI engine) {
        this.engine = engine;
        if (StepSpeedupConfig.resetEachBattle) {
            speedIndex = StepSpeedupConfig.getNormalSpeedIndex();
        }
        speedIndex = StepSpeedupConfig.clampSpeedIndex(speedIndex);
    }

    @Override
    public void advance(float amount, List<InputEventAPI> events) {
        if (engine == null || engine.getCombatUI() == null) {
            return;
        }

        // Skip the title screen background battle
        ShipAPI player = engine.getPlayerShip();
        if ((player == null || !engine.isEntityInPlay(player))
                && !engine.isInCampaign()
                && !engine.isInCampaignSim()
                && !engine.isUIShowingHUD()) {
            return;
        }

        int newSpeedIndex = StepSpeedupConfig.processInput(events, speedIndex);
        boolean changed = newSpeedIndex != speedIndex;
        speedIndex = newSpeedIndex;

        float speed = StepSpeedupConfig.speeds[speedIndex];
        if (speed != 1f) {
            engine.getTimeMult().modifyMult(STAT_ID, speed, "Step Speedup");
            if (player != null && engine.isEntityInPlay(player)) {
                engine.maintainStatusForPlayerShip(STAT_ID,
                        Global.getSettings().getSpriteName("ui", "icon_tactical_coordinated_maneuvers"),
                        "Step Speedup", "game speed " + StepSpeedupConfig.formatSpeed(speed), false);
            }
        } else {
            engine.getTimeMult().unmodify(STAT_ID);
        }

        if (changed && StepSpeedupConfig.showMessages) {
            engine.getCombatUI().addMessage(0, new Object[] {StepSpeedupConfig.textColor(), "Game speed ",
                    StepSpeedupConfig.highlightColor(), StepSpeedupConfig.formatSpeed(speed)});
            Global.getSoundPlayer().playUISound(SOUND_ID, 1f, 0.5f);
        }
    }
}
