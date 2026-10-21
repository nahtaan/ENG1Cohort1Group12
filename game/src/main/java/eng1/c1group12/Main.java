package eng1.c1group12;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.system.AppSettings;

public class Main extends SimpleApplication {
    public static final float PIXELS_PER_UNIT = 256f;

    public static WorldManager worldManager;

    public static void main(String[] args) {
        Main app = new Main();

        AppSettings settings = new AppSettings(true);
        settings.setTitle("Titled Goose Game");
//        settings.setFullscreen(true);
        settings.setResolution(1920, 1080);
        app.setSettings(settings);

        app.start();
    }

    @Override
    public void simpleInitApp() {
        flyCam.setEnabled(false);
        registerInputs();

        worldManager = new WorldManager(rootNode, assetManager, cam, WorldManager.Level.OUTSIDE);

        worldManager.getPlayer().attachInputListener(inputManager);
    }

    private void registerInputs() {
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
    }


    @Override
    public void simpleUpdate(float tpf) {
        worldManager.getPlayer().move(tpf);
    }

}