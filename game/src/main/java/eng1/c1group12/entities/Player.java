package eng1.c1group12.entities;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

public class Player {
    public float moveX;
    public float moveY;
    private final float speed = 0.05f;
    private final Geometry geom;
    private final Camera camera;

    public Player(AssetManager assetManager, Camera cam) {
        this.camera = cam;
        Box b = new Box(0.1f, 0.1f, 0.5f);
        geom = new Geometry("Player", b);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        geom.setMaterial(mat);
    }

    public void move(float tpf) {
        geom.move(moveX * speed * tpf, moveY * speed * tpf, 0);
        camera.setLocation(geom.getLocalTranslation().add(0,0,10));
        moveX = 0;
        moveY = 0;
    }

    public Geometry getGeom() {
        return this.geom;
    }


}
