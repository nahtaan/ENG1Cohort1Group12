package eng1.c1group12.entities;

import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.input.controls.AnalogListener;
import com.jme3.renderer.Camera;

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
    public void tick() {

    }
}
