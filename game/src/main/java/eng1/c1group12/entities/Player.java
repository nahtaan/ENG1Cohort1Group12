package eng1.c1group12.entities;

import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.input.controls.AnalogListener;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import eng1.c1group12.Main;

public class Player extends Entity {
    private float moveX;
    private float moveY;
    private final float speed = 1f;
    private final Camera camera;

    public Player(AssetManager assetManager, Camera cam) {
        super(assetManager,"Player", assetManager.loadTexture("Textures/Entities/player.png"));
        this.camera = cam;
    }

    public void move(float tpf) {
        move(moveX * speed * tpf, moveY * speed * tpf, 0);
        moveX = 0;
        moveY = 0;
    }

    public void attachInputListener(InputManager inputManager) {
        inputManager.addListener((AnalogListener) (name, value, tpf) -> {
            switch (name) {
                case "Left" -> this.moveX = -1;
                case "Right" -> this.moveX = 1;
                case "Up" -> this.moveY = 1;
                case "Down" -> this.moveY = -1;
            }
        }, "Left", "Right", "Up", "Down");
    }

    @Override
    public void move(float x, float y, float z) {
        super.move(x, y, z);
        camera.setLocation(getNode().getLocalTranslation().add(0, 0, 10));
    }

    @Override
    public void tick(float tpf) {
        // world border collision check
        float worldWidth = Main.worldManager.getWidth();
        float worldHeight = Main.worldManager.getHeight();
        float maxLeft = worldWidth / -2;
        float maxRight = worldWidth / 2;
        float maxUp = worldHeight / 2;
        float maxDown = worldHeight / -2;

        Vector3f pos = getNode().getLocalTranslation();
        if(pos.x < maxLeft) {
            move(-(pos.x - maxLeft), 0, 0);
        }
        if(pos.x > maxRight) {
            move(-(pos.x - maxRight), 0,0);
        }
        if(pos.y > maxUp) {
            move(0, -(pos.y - maxUp), 0);
        }
        if(pos.y < maxDown) {
            move(0, -(pos.y - maxDown), 0);
        }

    }
}
