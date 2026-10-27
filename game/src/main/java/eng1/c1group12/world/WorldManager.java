package eng1.c1group12.world;

import com.google.gson.Gson;
import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import eng1.c1group12.Main;
import eng1.c1group12.entities.Entity;
import eng1.c1group12.entities.Player;

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

    public WorldManager(Node rootNode, AssetManager assetManager, Camera camera, Level initialLevel) {
        this.root = rootNode;
        this.assetManager = assetManager;
        this.camera = camera;
        setupCamera();
        loadLevel(initialLevel);
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

    public void loadLevel(Level newLevel) {
        if(currentLevel != null && this.currentEntites != null) {
            levelEntities.put(this.currentLevel, this.currentEntites);
        }
        root.detachAllChildren();

        this.currentLevel = newLevel;
        this.currentEntites = levelEntities.getOrDefault(currentLevel, new ArrayList<>());

        WorldData worldData = loadWorldData(newLevel);

        System.out.println(worldData.toString());

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

    public void tick(float tpf) {
        for (Entity entity: currentEntites) {
            entity.tick(tpf);
        }
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
