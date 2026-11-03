package eng1.c1group12.entities;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.CenterQuad;
import com.jme3.texture.Texture;
import eng1.c1group12.Main;

/**
 * Abstract class for all types of entities to extend. Manages creating geometry and a Node for the entity.
 */
public abstract class Entity {

    private final String name;
    private final Texture texture;
    private final Geometry geom;
    private final Node node;
    public final float width;
    public final float height;

    public Entity(AssetManager assetManager, String name, Texture texture) {
        this.name = name;
        this.texture = texture;
        this.width = texture.getImage().getWidth() / Main.PIXELS_PER_UNIT;
        this.height = texture.getImage().getHeight() / Main.PIXELS_PER_UNIT;
        this.geom = new Geometry(name, new CenterQuad(width, height));
        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.setTexture("ColorMap", texture);
        material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        // Very important, makes all entities render transparency properly!
        geom.setQueueBucket(RenderQueue.Bucket.Transparent);
        geom.setMaterial(material);
        this.node = new Node(name);
        node.attachChild(geom);
    }

    /**
     * Called when the entity logic needs to be updated.
     */
    public abstract void tick(float tpf);

    public String getName() {
        return name;
    }

    public void move(float x, float y, float z) {
        getNode().move(x, y, z);
    }


    public Node getNode() {
        return node;
    }
}
