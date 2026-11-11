package eng1.c1group12.world.item;

import com.google.gson.Gson;
import com.jme3.asset.AssetManager;
import com.jme3.texture.Texture;
import eng1.c1group12.Main;
import eng1.c1group12.entities.Entity;

import java.io.InputStreamReader;

public abstract class Item extends Entity {

    protected final String name;
    protected final Texture texture;
    protected final ItemTextureData data;

    public Item(String name, AssetManager assetManager) {
        super(assetManager, name, name);
        this.name = name;
        this.texture = assetManager.loadTexture("Textures/Items/"+name+"/item.png");
        texture.setMagFilter(Texture.MagFilter.Nearest);
        texture.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
        // noinspection ConstantConditions
        this.data = new Gson().fromJson(new InputStreamReader(Main.class.getResourceAsStream("/Textures/Items/"+name+"/item.json")), ItemTextureData.class);
    }

    public String getName() {
        return this.name;
    }
    public Texture getTexture() { return this.texture; }
    public ItemTextureData getTextureData() {
        return this.data;
    }

    /**
     * Called when the item is attempted to be used by the player.
     */
    public abstract void useItem();

    /**
     * Called when the item enters the player's inventory.
     */
    public abstract void onPickup();

    public record ItemTextureData(
            int res,
            int pixels_per_unit
    ){}
}
