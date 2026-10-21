package eng1.c1group12;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import eng1.c1group12.entities.Player;

public class WorldManager {

    private final Node root;
    private final AssetManager assetManager;
    private final Camera camera;
    private Level currentLevel;
    private Player player;

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
        final float scale_factor = 15;
        this.camera.setFrustum(1, 100, -16f / scale_factor, 16f / scale_factor, 9f / scale_factor, -9f / scale_factor);
    }

    public void loadLevel(Level newLevel) {
        this.currentLevel = newLevel;

        Texture texture = assetManager.loadTexture("Textures/Levels/"+currentLevel.id+".png");
        texture.setMagFilter(Texture.MagFilter.Nearest);

        int width = texture.getImage().getWidth();
        int height = texture.getImage().getHeight();

        float pixelsPerUnit = 512f;

        Quad quad = new Quad(
                width / pixelsPerUnit,
                height / pixelsPerUnit
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
        this.root.attachChild(this.player.getNode());
    }

    public Player getPlayer() {
        return player;
    }





    public enum Level {
        OUTSIDE("outside");

        public final String id;

        Level(String id) {
            this.id = id;
        }
    }

}
