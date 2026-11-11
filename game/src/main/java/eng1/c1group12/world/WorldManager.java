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
import eng1.c1group12.entities.Player;
import eng1.c1group12.entities.item.Item;

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
    private List<Item> currentItems;
    private final Map<Level, List<Entity>> levelEntities = new HashMap<>();
    private final Map<Level, List<Item>> levelItems = new HashMap<>();

    public WorldManager(Node rootNode, AssetManager assetManager, Camera camera, Level initialLevel) {
        this.root = rootNode;
        this.assetManager = assetManager;
        this.camera = camera;
        setupCamera();
        if (!loadLevel(initialLevel)) throw new RuntimeException("Unable to load world.");
        loadPlayer();
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
        if(currentLevel != null && this.currentEntites != null && this.currentItems != null) {
            levelEntities.put(this.currentLevel, this.currentEntites);
            levelItems.put(this.currentLevel, this.currentItems);
        }
        root.detachAllChildren();

        this.currentLevel = newLevel;
        this.currentEntites = levelEntities.getOrDefault(currentLevel, new ArrayList<>());
        this.currentItems = levelItems.getOrDefault(currentLevel, new ArrayList<>());

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

    private WorldData loadWorldData(Level level) {
        try (Reader reader = new InputStreamReader(Main.class.getResourceAsStream("/Textures/Levels/" + level.id + "/map.json"))) {
            return new Gson().fromJson(reader, WorldData.class);
        }catch (IOException e) {
            e.printStackTrace(System.err);
            return null;
        }
    }

    private void loadPlayer() {
        this.player = new Player(assetManager, camera);
        addEntity(this.player);
    }

    public void addEntity(Entity entity) {
        currentEntites.add(entity);
        this.root.attachChild(entity.getNode());
        if(entity instanceof Item i) {
            currentItems.add(i);
        }
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

    public void tick(float tpf) {
        for (Entity entity: currentEntites) {
            entity.tick(tpf);
        }
        for(Entity entity: post_tick_removal) {
            root.detachChild(entity.getNode());
            if(entity instanceof Item i) {
                currentItems.remove(i);
            }
        }
        post_tick_removal.clear();
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
