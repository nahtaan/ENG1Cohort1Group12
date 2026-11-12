package eng1.c1group12.entities;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.CenterQuad;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import eng1.c1group12.Main;

import java.util.Random;

public class Goose extends Entity {
    private final float speed = new Random().nextFloat(0.3f, 0.9f);
    private int hunger = 100;
    private final Node hungerNode = new Node();
    private final AssetManager assetManager;
    private float scared_time = 0f;

    public Goose(AssetManager assetManager, String name) {
        super(assetManager, name, "goose");
        this.assetManager = assetManager;
        animationHandler.showCell(1, 1);
        // attach hunger bar node so we don't have to keep re-attaching
        hungerNode.setLocalTranslation(-16f / this.animationHandler.getInfo().pixels_per_unit(),16f / this.animationHandler.getInfo().pixels_per_unit(),0.1f);
        drawHungerBar();
    }

    public boolean isHungry() {
        return hunger > 0;
    }

    public void feed(int amount) {
        hunger -= amount;
        if(hunger <= 0) {
            hunger = 0;
            if (getNode().hasChild(hungerNode)) {
                getNode().detachChild(hungerNode);
            }

            // set to sleeping texture
            Texture texture = assetManager.loadTexture("Textures/Entities/goose/sleeping.png");
            texture.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
            texture.setMagFilter(Texture.MagFilter.Nearest);
            Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat.setTexture("ColorMap", texture);
            mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
            Geometry geom = new Geometry("sleeping", new CenterQuad(0.5f, 0.5f));
            geom.setMaterial(mat);
            geom.setQueueBucket(RenderQueue.Bucket.Transparent);
            getNode().detachAllChildren();
            getNode().attachChild(geom);
        }
    }

    public void scare(float duration) {
        this.scared_time = duration;
    }


    private void drawHungerBar() {
        hungerNode.detachAllChildren();
        // calculate values for the hunger bar
        float totalWidth = (float) this.animationHandler.getInfo().res() / this.animationHandler.getInfo().pixels_per_unit();
        float redWidth = totalWidth * (hunger / 100f);
        float blackWidth = totalWidth - redWidth;

        // create the left side of the hunger bar
        Quad red = new Quad(redWidth, 1f / this.animationHandler.getInfo().pixels_per_unit());
        Geometry hungerBarRed = new Geometry("hunger-bar-red", red);

        Material matRed = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matRed.setColor("Color", ColorRGBA.Red);
        hungerBarRed.setMaterial(matRed);

        // create the right side of the hunger bar
        Quad black = new Quad(blackWidth, 1f / this.animationHandler.getInfo().pixels_per_unit());
        Geometry hungerBarBlack = new Geometry("hunger-bar-black", black);

        Material matBlack = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matBlack.setColor("Color", ColorRGBA.Black);
        hungerBarBlack.setMaterial(matBlack);
        hungerBarBlack.setLocalTranslation(redWidth, 0, 0);

        hungerNode.attachChild(hungerBarRed);
        hungerNode.attachChild(hungerBarBlack);
    }

    @Override
    public void tick(float tpf) {
        // when the goose is no longer hungry, it goes to sleep.
        if(hunger <= 0) {
            return;
        }

        // Find the player's position
        Vector3f playerPos = Main.worldManager.getPlayer().getNode().getLocalTranslation();
        Vector3f goosePos = getNode().getLocalTranslation();

        float dx = playerPos.x - goosePos.x;
        float dy = playerPos.y - goosePos.y;

        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if(distance < 1.5f) {
            getNode().attachChild(hungerNode);
        } else if (getNode().hasChild(hungerNode)) {
            getNode().detachChild(hungerNode);
        }

        // Stop when close enough to the player
        float stopDistance = 0.6f;
        if (distance <= stopDistance) {
            return;
        }

        // Move towards the player without overshooting
        float movement = Math.min(speed * tpf, distance - stopDistance);

        // run away when scared
        if(scared_time > 0) {
            scared_time -= tpf;
            movement = -movement;
        }

        float x = (dx / distance) * movement;
        float y = (dy / distance) * movement;

        move(x, y, 0);
        // update animation
        animationHandler.update(tpf, x, y);
    }
}
