package eng1.c1group12.entities;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.CenterQuad;
import eng1.c1group12.Main;
import eng1.c1group12.ui.InventoryElement;
import eng1.c1group12.world.item.Item;

public class ItemEntity extends Entity {

    private final Item item;
    private final InventoryElement inventoryUI;

    public ItemEntity(AssetManager assetManager, String name, Item item, InventoryElement inventoryUI) {
        // tell the Entity class to load the placeholder so it doesn't crash then replace with our own texture
        super(assetManager, name, "item");
        this.item = item;
        this.texture = item.getTexture();
        this.inventoryUI = inventoryUI;
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
        // check collision with the player
        Player player = Main.worldManager.getPlayer();
        Vector3f playerPos = player.getNode().getLocalTranslation();
        Vector3f itemPos = getNode().getLocalTranslation();
        float playerWidth = player.width;
        float playerHeight = player.height;
        boolean is_touching =
                playerPos.x < itemPos.x + this.width &&
                playerPos.x + playerWidth > itemPos.x &&
                playerPos.y < itemPos.y + this.height &&
                playerPos.y + playerHeight > itemPos.y;
        if (is_touching) {
            this.item.onPickup();
            player.getInventory().add(this.item);
            Main.worldManager.removeEntity(this.name);
            inventoryUI.refreshInventoryUI();
        }
    }
}
