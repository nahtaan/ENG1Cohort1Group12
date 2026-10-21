package eng1.c1group12;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.CenterQuad;
import com.jme3.texture.Texture;
import eng1.c1group12.entities.Entity;
import eng1.c1group12.entities.Player;

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

        Texture texture = assetManager.loadTexture("Textures/Levels/"+currentLevel.id+".png");
        texture.setMagFilter(Texture.MagFilter.Nearest);

        this.width = texture.getImage().getWidth() / Main.PIXELS_PER_UNIT;
        this.height = texture.getImage().getHeight() / Main.PIXELS_PER_UNIT;

        CenterQuad quad = new CenterQuad(
                width,
                height
        );

        Geometry background = new Geometry("Background", quad);

        Material material = new Material(
                assetManager, "Common/MatDefs/Misc/Unshaded.j3md"
        );
        material.setTexture("ColorMap", texture);
        background.setMaterial(material);
        this.root.attachChild(background);
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
