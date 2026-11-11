package eng1.c1group12;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.system.AppSettings;
import eng1.c1group12.ui.InventoryElement;
import eng1.c1group12.ui.TimerElement;
import eng1.c1group12.ui.UIManager;
import eng1.c1group12.world.WorldManager;

import java.awt.*;

public class Main extends SimpleApplication {

    public static WorldManager worldManager;
    public static UIManager uiManager;

    public static void main(String[] args) {
        Main app = new Main();

        AppSettings settings = new AppSettings(true);
        settings.setTitle("Titled Goose Game");

        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        DisplayMode mode = device.getDisplayMode();
        settings.setResolution(mode.getWidth(), mode.getHeight());
        settings.setFullscreen(device.isFullScreenSupported());
        settings.setVSync(true);
        settings.setBitsPerPixel(mode.getBitDepth());
        settings.setFrequency(mode.getRefreshRate());
        settings.setFrameRate(mode.getRefreshRate());

        app.setSettings(settings);

        app.start();
    }

    @Override
    public void simpleInitApp() {
        flyCam.setEnabled(false);
        registerInputs();
        setDisplayFps(false);
        setDisplayStatView(false);

        worldManager = new WorldManager(rootNode, assetManager, cam, WorldManager.Level.OUTSIDE);

        worldManager.getPlayer().attachInputListener(inputManager);
        setupUI();
    }

    public void endGame() {
        stop(false);
    }

    private void setupUI() {
        uiManager = new UIManager(guiNode, assetManager, cam);

        // create the game timer
        TimerElement clock = new TimerElement("Timer", assetManager, timer, 60 * 5, ()->{
            // runs once the timer reaches 0
            uiManager.removeElement("Timer");
            endGame();
        });
        uiManager.addElement(clock);

        // create the inventory UI
        InventoryElement inventory = new InventoryElement("Inventory", assetManager);
        uiManager.addElement(inventory);
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
        worldManager.tick(tpf);
        uiManager.tick(tpf);
    }

}