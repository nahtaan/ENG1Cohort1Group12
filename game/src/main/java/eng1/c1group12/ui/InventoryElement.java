package eng1.c1group12.ui;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;
import eng1.c1group12.Main;
import eng1.c1group12.world.item.Item;

import java.util.List;

public class InventoryElement extends UIElement {

    private final AssetManager assetManager;

    public InventoryElement(String name, AssetManager assetManager) {
        super(name);
        this.assetManager = assetManager;

        buildInventory(Main.worldManager.getPlayer().getInventory());
        getNode().setLocalTranslation(0, Main.uiManager.screenHeight-150, 0);
    }

    private void buildInventory(List<Item> items) {
        getNode().detachAllChildren();
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            int item_size = 80;
            Quad quad = new Quad(item_size, item_size);
            Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat.setTexture("ColorMap", item.getTexture());
            mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);

            Geometry geom = new Geometry(item.getName(), quad);
            geom.setMaterial(mat);
            geom.setLocalTranslation(i * item_size, 0, 0);

            getNode().attachChild(geom);
        }

    }


    @Override
    public void tick(float tpf) {

    }
}
