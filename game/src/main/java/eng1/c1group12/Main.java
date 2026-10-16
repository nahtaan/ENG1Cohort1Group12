package eng1.c1group12;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import eng1.c1group12.entities.Player;

public class Main extends SimpleApplication {
    private Player player;

    public static void main(String[] args) {
        Main app = new Main();

        AppSettings settings = new AppSettings(true);
        settings.setTitle("Titled Goose Game");
        settings.setFullscreen(true);
        app.setSettings(settings);

        app.start();
    }

    @Override
    public void simpleInitApp() {
        flyCam.setEnabled(false);

        Camera camera = cam;
        camera.setParallelProjection(true);
        camera.setLocation(new Vector3f(0, 0, 10));
        camera.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);

        player = new Player(assetManager, camera);
        rootNode.attachChild(player.getGeom());

        Box background = new Box(0.5f, 0.5f, 0.5f);
        Geometry g = new Geometry("Background", background);
        g.move(0, 0, -0.1f);
        rootNode.attachChild(g);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Red);
        g.setMaterial(mat);


        inputManager.addMapping(
                "Left", new KeyTrigger(KeyInput.KEY_A)
        );
        inputManager.addMapping(
                "Right", new KeyTrigger(KeyInput.KEY_D)
        );
        inputManager.addMapping(
                "Up", new KeyTrigger(KeyInput.KEY_W)
        );
        inputManager.addMapping(
                "Down", new KeyTrigger(KeyInput.KEY_S)
        );
        inputManager.addListener((AnalogListener) (name, value, tpf) -> {
            switch (name) {
                case "Left" -> player.moveX = -1;
                case "Right" -> player.moveX = 1;
                case "Up" -> player.moveY = 1;
                case "Down" -> player.moveY = -1;
            }
        }, "Left", "Right", "Up", "Down");
    }

    @Override
    public void simpleUpdate(float tpf) {
        player.move(tpf);
    }

}