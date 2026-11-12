package eng1.c1group12.world.item;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import eng1.c1group12.Main;
import eng1.c1group12.entities.Goose;

import java.util.List;

public class WaterBlasterItem extends Item{
    /**
     * The maximum distance that this item can be used on a goose from.
     */
    private final float range = 3f;
    /**
     * The number of seconds that the goose gets scared away for.
     */
    private final float scare_duration = 10f;

    public WaterBlasterItem(AssetManager assetManager) {
        super("water_blaster", assetManager);
    }

    @Override
    public boolean attemptUseItem() {
        // scares away the nearest 5 geese for 10 seconds
        List<Goose> targets = Main.worldManager.getNearestHungryGeese(5);
        Vector3f pPos = Main.worldManager.getPlayer().getNode().getLocalTranslation();
        for(Goose target: targets) {
            Vector3f gPos = target.getNode().getLocalTranslation();
            float dx = pPos.x - gPos.x;
            float dy = pPos.y - gPos.y;
            float distance = dx * dx + dy * dy;
            if(distance <= range) {
                target.scare(scare_duration);
            }
        }
        return true;
    }

    @Override
    public void onPickup() {
        // nothing special
    }
}
