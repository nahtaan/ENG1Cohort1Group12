package eng1.c1group12.entities;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import eng1.c1group12.Main;

import java.util.Random;

public class Goose extends Entity {
    private final float speed = new Random().nextFloat(0.3f, 0.9f);

    public Goose(AssetManager assetManager, String name) {
        //todo: fix goose textures
        super(assetManager, name, "");
    }

    @Override
    public void tick(float tpf) {
        // find the player's direction and move towards them
        Vector3f playerPos = Main.worldManager.getPlayer().getNode().getLocalTranslation();
        Vector3f goosePos = getNode().getLocalTranslation();
        if(goosePos.x == playerPos.x && goosePos.y == playerPos.y) return;
        double angle = Math.atan2((playerPos.y - goosePos.y), (playerPos.x - goosePos.x));
        float x = (float) Math.cos(angle) * speed;
        float y = (float) Math.sin(angle) * speed;

        move(x * tpf, y * tpf, 0);
    }
}
