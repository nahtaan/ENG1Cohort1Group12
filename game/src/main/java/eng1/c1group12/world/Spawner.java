package eng1.c1group12.world;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.system.Timer;
import eng1.c1group12.Main;
import eng1.c1group12.entities.Goose;
import eng1.c1group12.entities.ItemEntity;
import eng1.c1group12.ui.InventoryElement;
import eng1.c1group12.world.item.BaguetteItem;
import eng1.c1group12.world.item.Item;
import eng1.c1group12.world.item.WaterBlasterItem;

import java.util.*;

public class Spawner {

    private final Timer timer;
    private final AssetManager assetManager;
    private final InventoryElement inventoryUI;

    private final Map<ItemEntity, Float> baguette_entities = new HashMap<>();
    private float last_baguette_spawn = 0;

    private ItemEntity water_blaster_entity = null;
    private float last_water_blaster_spawn = 0;

    private float last_flock_spawn = -20; // start at -20 so a flock spawns as soon as the game starts.

    public Spawner(Timer timer, AssetManager assetManager, InventoryElement inventoryUI) {
        this.timer = timer;
        this.assetManager = assetManager;
        this.inventoryUI = inventoryUI;
    }

    private final float BAGUETTE_X_RANGE = 3;
    private final float BAGUETTE_Y_RANGE = 1.5f;
    private final int BAGUETTE_SPAWN_COOLDOWN = 5;
    private final int BAGUETTE_MAX = 5;

    private final int WATER_BLASTER_COOLDOWN = 15;

    private final float GOOSE_X_RANGE = 5;
    private final float GOOSE_Y_RANGE = 3f;
    private final int MAX_FLOCK_SIZE = 5;
    private final int FLOCK_SPAWN_COOLDOWN = 20;

    public void tick(float tpf) {
        // item spawning

        // 3 baguettes at one time on the map, after that no more spawn. Including the ones in the inventory.
        // Baguettes despawn after 30 seconds. Spawn cooldown of 10 seconds
        if(baguetteCount() < BAGUETTE_MAX && timer.getTimeInSeconds() - last_baguette_spawn > BAGUETTE_SPAWN_COOLDOWN) {
            // spawn new baguette item
            ItemEntity baguette = genBaguetteEntity();
            last_baguette_spawn = timer.getTimeInSeconds();
            baguette_entities.put(baguette, last_baguette_spawn);
            Main.worldManager.addEntity(baguette);
        }

        // 1 water blaster at most on the map, including the inventory. Spawns randomly anywhere on the map
        // spawns at most every 20 seconds
        if(waterBlasterCount() == 0 && timer.getTimeInSeconds() - last_water_blaster_spawn > WATER_BLASTER_COOLDOWN) {
            // spawn new water blaster item
            water_blaster_entity = genWaterBlasterEntity();
            Main.worldManager.addEntity(water_blaster_entity);
            last_water_blaster_spawn = timer.getTimeInSeconds();
        }

        // goose spawning

        // between 1 and 3 geese can spawn in at any point within range of the player every 20 seconds.
        // up to an unlimited amount of geese
        if(timer.getTimeInSeconds() - last_flock_spawn > FLOCK_SPAWN_COOLDOWN) {
            last_flock_spawn = timer.getTimeInSeconds();
            Random random = new Random();
            int flock_size = random.nextInt(1, MAX_FLOCK_SIZE);
            float x_origin = random.nextFloat(-GOOSE_X_RANGE, GOOSE_X_RANGE);
            float y_origin = random.nextFloat(-GOOSE_Y_RANGE, GOOSE_Y_RANGE);

            Vector3f pPos = Main.worldManager.getPlayer().getNode().getLocalTranslation();
            for (int i = 0; i < flock_size; i++) {
                String id = "goose-" + i + "-" + last_flock_spawn;
                Goose goose = new Goose(assetManager, id);
                goose.move(pPos.x, pPos.y, 0);
                goose.move(x_origin, y_origin, 0);
                goose.move(random.nextFloat(-0.5f, 0.5f), random.nextFloat(-0.5f, 0.5f), 0);
                Main.worldManager.addEntity(goose);
            }
        }
    }

    private ItemEntity genBaguetteEntity() {
        String id = "baguette-" + timer.getTimeInSeconds();
        // needs to use an array here because the ItemEntity references itself in its own initialisation
        ItemEntity[] baguette = new ItemEntity[1];
        baguette[0] = new ItemEntity(assetManager, id, new BaguetteItem(assetManager) {
            @Override
            public void onPickup() {
                baguette_entities.remove(baguette[0]);
            }
        }, inventoryUI);
        // randomise position around player
        Vector3f pPos = Main.worldManager.getPlayer().getNode().getLocalTranslation();
        Random random = new Random();
        baguette[0].move(pPos.x + random.nextFloat(-BAGUETTE_X_RANGE, BAGUETTE_X_RANGE),pPos.y + random.nextFloat(-BAGUETTE_Y_RANGE, BAGUETTE_Y_RANGE),0);
        return baguette[0];
    }

    private ItemEntity genWaterBlasterEntity() {
        String id = "water-blaster" + timer.getTimeInSeconds();
        ItemEntity[] blaster = new ItemEntity[1];
        blaster[0] = new ItemEntity(assetManager, id, new WaterBlasterItem(assetManager) {
            @Override
            public void onPickup() {
                water_blaster_entity = null;
            }
        }, inventoryUI);
        // Randomise position regardless of player position
        Random random = new Random();
        blaster[0].move(random.nextFloat(0, Main.worldManager.getWidth()), random.nextFloat(0, Main.worldManager.getHeight()),0);
        System.out.println(blaster[0].getNode().getLocalTranslation());
        return blaster[0];
    }

    private int baguetteCount() {
        int inv_count = 0;
        for(Item i: Main.worldManager.getPlayer().getInventory()) {
            if(i instanceof BaguetteItem) inv_count += 1;
        }
        return inv_count + baguette_entities.size();
    }

    private int waterBlasterCount() {
        int inv_count = 0;
        for(Item i: Main.worldManager.getPlayer().getInventory()) {
            if(i instanceof WaterBlasterItem) inv_count += 1;
        }
        return inv_count + (water_blaster_entity != null ? 1 : 0);
    }
}
