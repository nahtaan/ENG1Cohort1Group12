package eng1.c1group12.entities;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import eng1.c1group12.Main;

import java.util.Random;

public class Goose extends Entity {
    private final float speed = new Random().nextFloat(0.3f, 0.9f);

    public Goose(AssetManager assetManager, String name) {
        super(assetManager, name, "goose");
        animationHandler.showCell(1, 1);
    }

    @Override
    public void tick(float tpf) {
        // Find the player's position
        Vector3f playerPos = Main.worldManager.getPlayer().getNode().getLocalTranslation();
        Vector3f goosePos = getNode().getLocalTranslation();

        float dx = playerPos.x - goosePos.x;
        float dy = playerPos.y - goosePos.y;

        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        // Stop when close enough to the player
        float stopDistance = 0.6f;
        if (distance <= stopDistance) {
            return;
        }

        // Move towards the player without overshooting
        float movement = Math.min(speed * tpf, distance - stopDistance);
        float x = (dx / distance) * movement;
        float y = (dy / distance) * movement;

        move(x, y, 0);
        // update animation
        animationHandler.update(tpf, x, y);
    }
}
