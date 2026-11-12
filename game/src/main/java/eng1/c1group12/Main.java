package eng1.c1group12;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.system.AppSettings;
import com.jme3.system.Timer;
import eng1.c1group12.ui.InventoryElement;
import eng1.c1group12.ui.TimerElement;
import eng1.c1group12.ui.UIManager;
import eng1.c1group12.world.WorldManager;
import com.jme3.math.Vector3f;
import eng1.c1group12.entities.Goose;
import eng1.c1group12.ui.EventElement;

import java.awt.*;

public class Main extends SimpleApplication {

    public static WorldManager worldManager;
    public static UIManager uiManager;
    public static Timer timer;

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
        timer = getTimer();
        flyCam.setEnabled(false);
        registerInputs();
        setDisplayFps(false);
        setDisplayStatView(false);

        // setup UI
        uiManager = new UIManager(guiNode, assetManager, cam);
        // create the inventory UI so it can be referenced elsewhere
        InventoryElement inventory = new InventoryElement("Inventory", assetManager);

        worldManager = new WorldManager(rootNode, assetManager, cam, WorldManager.Level.OUTSIDE, inventory);

        // Create the hidden event UI
        EventElement eventUI = new EventElement(
                "Events",
                assetManager,
                uiManager.screenWidth,
                uiManager.screenHeight
        );
        uiManager.addElement(eventUI);
        worldManager.setEventDisplay(eventUI);

        // Spawn three geese near the player
        Vector3f playerPos = worldManager.getPlayer().getNode().getLocalTranslation();
        for (int i = 0; i < 3; i++) {
            Goose goose = new Goose(assetManager, "goose-" + (i + 1));

            goose.getNode().setLocalTranslation(
                    playerPos.x + (i - 1) * 1.5f,
                    playerPos.y + 1.5f,
                    0
            );
            worldManager.addEntity(goose);
        }


        worldManager.getPlayer().attachInputListener(inputManager);

        // create the game timer
        TimerElement clock = new TimerElement("Timer", assetManager, timer, 60 * 5, ()->{
            // runs once the timer reaches 0
            uiManager.removeElement("Timer");
            endGame();
        });
        uiManager.addElement(clock);
        uiManager.addElement(inventory);
    }

    public void endGame() {
        stop(false);
    }

    /**
     * Registers input mappings with the input manager so they can be listened to later.
     */
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
        inputManager.addMapping("item-1", new KeyTrigger(KeyInput.KEY_1));
        inputManager.addMapping("item-2", new KeyTrigger(KeyInput.KEY_2));
        inputManager.addMapping("item-3", new KeyTrigger(KeyInput.KEY_3));
        inputManager.addMapping("item-4", new KeyTrigger(KeyInput.KEY_4));
    }


    @Override
    public void simpleUpdate(float tpf) {
        worldManager.tick(tpf);
        uiManager.tick(tpf);
    }

}