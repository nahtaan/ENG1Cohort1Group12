package eng1.c1group12.world;

import com.google.gson.Gson;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import eng1.c1group12.Main;
import eng1.c1group12.entities.Entity;
import eng1.c1group12.entities.Goose;
import eng1.c1group12.entities.HiddenEvent;
import eng1.c1group12.entities.Player;
import eng1.c1group12.ui.EventElement;
import eng1.c1group12.ui.InventoryElement;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorldManager {

    private float width;
    private float height;
    private final Node root;
    private final AssetManager assetManager;
    private final Camera camera;
    private Level currentLevel;
    private Player player;
    private List<Entity> currentEntites;
    private final Map<Level, List<Entity>> levelEntities = new HashMap<>();

    public WorldManager(Node rootNode, AssetManager assetManager, Camera camera, Level initialLevel, InventoryElement inventoryUI, EventElement eventUI) {
        this.root = rootNode;
        this.assetManager = assetManager;
        this.camera = camera;
        setupCamera();
        if (!loadLevel(initialLevel)) throw new RuntimeException("Unable to load world.");
        loadPlayer(inventoryUI, eventUI);
        loadEvents(eventUI);
    }

    public List<Goose> getNearestHungryGeese(int count) {
        List<Goose> nearest = new ArrayList<>();
        Vector3f pPos = player.getNode().getLocalTranslation();

        for (Entity e : currentEntites) {
            if (e instanceof Goose g && g.isHungry()) {
                Vector3f gPos = g.getNode().getLocalTranslation();

                float dx = pPos.x - gPos.x;
                float dy = pPos.y - gPos.y;
                float distance = dx * dx + dy * dy;

                if (nearest.size() < count) {
                    nearest.add(g);
                } else {
                    int furthestIndex = 0;
                    float furthestDistance = -1;

                    for (int i = 0; i < nearest.size(); i++) {
                        Vector3f pos = nearest.get(i).getNode().getLocalTranslation();

                        float fx = pPos.x - pos.x;
                        float fy = pPos.y - pos.y;
                        float d = fx * fx + fy * fy;

                        if (d > furthestDistance) {
                            furthestDistance = d;
                            furthestIndex = i;
                        }
                    }

                    if (distance < furthestDistance) {
                        nearest.set(furthestIndex, g);
                    }
                }
            }
        }

        return nearest;
    }

    private void setupCamera() {
        this.camera.setParallelProjection(true);
        this.camera.setLocation(new Vector3f(0, 0, 10));
        this.camera.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
        // aspect ratio should always be 16:9 to match a regular monitor
        final float scale_factor = 5;
        this.camera.setFrustum(1, 100, -16f / scale_factor, 16f / scale_factor, 9f / scale_factor, -9f / scale_factor);
    }

    public boolean loadLevel(Level newLevel) {
        if(currentLevel != null && this.currentEntites != null) {
            levelEntities.put(this.currentLevel, this.currentEntites);
        }
        root.detachAllChildren();

        this.currentLevel = newLevel;
        this.currentEntites = levelEntities.getOrDefault(currentLevel, new ArrayList<>());

        WorldData worldData = loadWorldData(newLevel);
        if (worldData == null) return false;

        this.width = ((float)worldData.tile_res() * worldData.x()) / worldData.pixels_per_unit();
        this.height = ((float)worldData.tile_res() * worldData.y()) / worldData.pixels_per_unit();

        Node background = new Node();

        for(int x = 0; x < worldData.x(); x++) {
            for(int y = 0; y < worldData.y(); y++) {
                Texture tex = assetManager.loadTexture("Textures/Levels/" + newLevel.id + "/" + x + "-" + y + ".png");
                tex.setMagFilter(Texture.MagFilter.Nearest);

                // disallow images that are the wrong resolution from what is expected
                if (tex.getImage().getWidth() != worldData.tile_res() || tex.getImage().getHeight() != worldData.tile_res()) {
                    return false;
                }
                Quad quad = new Quad(
                        (float) worldData.tile_res() / worldData.pixels_per_unit(),
                        (float) worldData.tile_res() / worldData.pixels_per_unit()
                );
                Geometry geom = new Geometry(newLevel.id + x + "-" + y, quad);

                Material mat = new Material(
                        assetManager, "Common/MatDefs/Misc/Unshaded.j3md"
                );
                mat.getAdditionalRenderState().setDepthTest(true);
                mat.getAdditionalRenderState().setDepthWrite(false);
                mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
                mat.setTexture("ColorMap", tex);
                geom.setMaterial(mat);
                geom.setQueueBucket(RenderQueue.Bucket.Transparent);
                background.attachChild(geom);
                geom.setLocalTranslation((x * (float) worldData.tile_res()) / worldData.pixels_per_unit(), (y * (float) worldData.tile_res()) / worldData.pixels_per_unit(),  0);
            }
        }

        background.setLocalTranslation(0, 0, -1);
        root.attachChild(background);
        return true;
    }

    private void loadEvents(EventElement eventUI) {
        if(currentLevel == Level.OUTSIDE) {
            // Create hidden events near the player's starting position
            Vector3f startPos = player.getNode().getLocalTranslation();
            addEntity(new HiddenEvent(
                    assetManager,
                    "Helpful Student",
                    eventUI,
                    startPos.x + 1.5f,
                    startPos.y,
                    0.6f
            ) {
                @Override
                public void onTrigger() {
                    removeEntity("goose-1");
                }

                @Override
                public String getMessage() {
                    return "A student distracted a goose!";
                }
            });
            addEntity(new HiddenEvent(
                    assetManager,
                    "Goose Gathering",
                    eventUI,
                    startPos.x,
                    startPos.y - 1.5f,
                    0.6f
            ) {
                @Override
                public void onTrigger() {
                    Vector3f playerPos = getPlayer().getNode().getLocalTranslation();
                    Goose goose = new Goose(assetManager, "goose-event-1");
                    goose.getNode().setLocalTranslation(
                            playerPos.x + 2f,
                            playerPos.y + 1f,
                            0
                    );
                    addEntity(goose);
                }

                @Override
                public String getMessage() {
                    return "Another goose has appeared!";
                }
            });
        }
    }

    private WorldData loadWorldData(Level level) {
        // noinspection ConstantConditions
        try (Reader reader = new InputStreamReader(Main.class.getResourceAsStream("/Textures/Levels/" + level.id + "/map.json"))) {
            return new Gson().fromJson(reader, WorldData.class);
        }catch (IOException e) {
            e.printStackTrace(System.err);
            return null;
        }
    }

    private void loadPlayer(InventoryElement inventoryUI, EventElement eventUI) {
        this.player = new Player(assetManager, camera, inventoryUI, eventUI);
        addEntity(this.player);
    }

    public void addEntity(Entity entity) {
        post_tick_addition.add(entity);
    }

    public void removeEntity(String name) {
        for (Entity e: currentEntites) {
            if(e.getName().equals(name)) {
                post_tick_removal.add(e);
                return;
            }
        }
    }

    public Player getPlayer() {
        return player;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    // Avoids concurrentmodification exception
    private final List<Entity> post_tick_removal = new ArrayList<>();
    private final List<Entity> post_tick_addition = new ArrayList<>();

    public void tick(float tpf) {
        for (Entity entity: currentEntites) {
            entity.tick(tpf);
        }
        for(Entity entity: post_tick_removal) {
            root.detachChild(entity.getNode());
            currentEntites.remove(entity);
        }
        post_tick_removal.clear();
        for(Entity entity: post_tick_addition) {
            this.root.attachChild(entity.getNode());
            currentEntites.add(entity);
        }
        post_tick_addition.clear();
    }

    public boolean isEntityAtPos(float x, float y, Class<? extends Entity> type) {
        for (Entity entity : currentEntites) {
            if (type.isInstance(entity)) {
                Vector3f targetPos = entity.getNode().getLocalTranslation();
                float dx = x - targetPos.x;
                float dy = y - targetPos.y;
                if (dx * dx + dy * dy < 0.6f * 0.6f) {
                    return true;
                }
            }
        }
        return false;
    }


    public enum Level {
        TEST("test"),
        OUTSIDE("outside");

        public final String id;

        Level(String id) {
            this.id = id;
        }
    }

}
