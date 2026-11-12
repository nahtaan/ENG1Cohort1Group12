package eng1.c1group12.world.item;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import eng1.c1group12.Main;
import eng1.c1group12.entities.Goose;

public abstract class BaguetteItem extends Item {

    /**
     * The maximum distance that this item can be used on a goose from.
     */
    private final float range = 1f;

    public BaguetteItem(AssetManager assetManager) {
        super("baguette", assetManager);
    }

    @Override
    public boolean attemptUseItem() {
        // feed the nearest goose
        Goose nearest = Main.worldManager.getNearestHungryGeese(1).get(0);
        Vector3f pPos = Main.worldManager.getPlayer().getNode().getLocalTranslation();
        Vector3f gPos = nearest.getNode().getLocalTranslation();
        float dx = pPos.x - gPos.x;
        float dy = pPos.y - gPos.y;
        float distance = dx * dx + dy * dy;

        // do nothing when out of range
        if(distance > range) {
            return false;
        }

        // full feed the nearest goose if in range
        nearest.feed(100);

        return true;
    }
}
