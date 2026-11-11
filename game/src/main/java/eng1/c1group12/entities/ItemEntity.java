package eng1.c1group12.entities;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.CenterQuad;
import eng1.c1group12.world.item.Item;

public class ItemEntity extends Entity {
    public ItemEntity(AssetManager assetManager, String name, Item item) {
        // tell the Entity class to load the placeholder so it doesn't crash then replace with our own texture
        super(assetManager, name, "item");
        this.texture = item.getTexture();
        getNode().detachChild(this.geom);
        Item.ItemTextureData data = item.getTextureData();

        this.width = (float) data.res() / data.pixels_per_unit();
        this.height = (float) data.res() / data.pixels_per_unit();

        CenterQuad quad = new CenterQuad(this.width, this.height);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", texture);
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);

        this.geom = new Geometry(name, quad);
        geom.setMaterial(mat);
        geom.setQueueBucket(RenderQueue.Bucket.Transparent);

        getNode().attachChild(geom);
    }

    @Override
    public void tick(float tpf) {

    }
}
