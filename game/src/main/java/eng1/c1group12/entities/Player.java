package eng1.c1group12.entities;

import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import eng1.c1group12.Main;
import eng1.c1group12.ui.InventoryElement;
import eng1.c1group12.world.item.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Player extends Entity {
    private float moveX;
    private float moveY;
    private final float speed = 1f;
    private final Camera camera;
    private final List<Item> inventory = new ArrayList<>();
    private final InventoryElement inventoryUI;

    public Player(AssetManager assetManager, Camera cam, InventoryElement inventoryUI) {
        super(assetManager,"Player", "player");
        this.camera = cam;
        this.inventoryUI = inventoryUI;
        // set player start position
        getNode().setLocalTranslation(24.7f, 10.3f, 0.1f);
        animationHandler.showCell(1,1);
    }

    public void attachInputListener(InputManager inputManager) {
        inputManager.addListener((AnalogListener) (name, value, tpf) -> {
            switch (name) {
                case "Left" -> this.moveX -= 1;
                case "Right" -> this.moveX += 1;
                case "Up" -> this.moveY += 1;
                case "Down" -> this.moveY -= 1;
            }
        }, "Left", "Right", "Up", "Down");
        inputManager.addListener((ActionListener) (name, is_pressed, tpf) -> {
            switch(name) {
                case "item-1" -> attemptUseItem(ItemUseSlot.ONE, is_pressed);
                case "item-2" -> attemptUseItem(ItemUseSlot.TWO, is_pressed);
                case "item-3" -> attemptUseItem(ItemUseSlot.THREE, is_pressed);
                case "item-4" -> attemptUseItem(ItemUseSlot.FOUR, is_pressed);
            }
        }, "item-1", "item-2", "item-3", "item-4");
    }

    private enum ItemUseSlot {
        ONE(0),
        TWO(1),
        THREE(2),
        FOUR(3);

        final int index;
        ItemUseSlot(int index) {
            this.index = index;
        }
    }

    private final HashMap<ItemUseSlot, Float> timeLastUsed = new HashMap<>();

    private void attemptUseItem(ItemUseSlot slot, boolean is_pressed) {
        // ignore release events
        if(!is_pressed) return;
        float now = Main.timer.getTimeInSeconds();
        float old = timeLastUsed.getOrDefault(slot, 0f);
        // do nothing if under 1 second since the last use of this slot to avoid double clicks
        if(now - old < 1f) {
            return;
        }
        timeLastUsed.put(slot, now);
        try {
            Item item = inventory.get(slot.index);
            item.useItem();
            inventory.remove(slot.index);
            inventoryUI.refreshInventoryUI();
        }catch (IndexOutOfBoundsException ignored) {}
    }

    public List<Item> getInventory() {
        return inventory;
    }

    @Override
    public void move(float x, float y, float z) {
        super.move(x, y, z);
        camera.setLocation(getNode().getLocalTranslation().add(0, 0, 10));
    }

    @Override
    public void tick(float tpf) {
        animationHandler.update(tpf, moveX, moveY);
        float changeX = moveX * speed * tpf;
        float changeY = moveY * speed * tpf;
        // Check horizontal movement
        if (!Main.worldManager.isGooseBlocking(
                getNode().getLocalTranslation().x + changeX,
                getNode().getLocalTranslation().y)) {
            move(changeX, 0, 0);
        }
        // Check vertical movement
        if (!Main.worldManager.isGooseBlocking(
                getNode().getLocalTranslation().x,
                getNode().getLocalTranslation().y + changeY)) {
            move(0, changeY, 0);
        }
        moveX = 0;
        moveY = 0;


        // world border collision check
        float worldWidth = Main.worldManager.getWidth();
        float worldHeight = Main.worldManager.getHeight();

        Vector3f pos = getNode().getLocalTranslation();
        // left side
        if(pos.x < width/2) {
            move(Math.abs(pos.x-width/2), 0, 0);
        }
        // right side
        if(pos.x > worldWidth-(width/2)) {
            move(-Math.abs(pos.x - (worldWidth-width/2)), 0,0);
        }
        // top
        if(pos.y > worldHeight-(height/2)) {
            move(0, -Math.abs(pos.y - (worldHeight-height/2)), 0);
        }
        // bottom
        if(pos.y < height/2) {
            move(0, Math.abs(pos.y-height/2), 0);
        }
    }
}
