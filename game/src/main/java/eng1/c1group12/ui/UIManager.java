package eng1.c1group12.ui;

import com.jme3.asset.AssetManager;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;

import java.util.ArrayList;
import java.util.List;

public class UIManager {

    private final Node root;
    private final AssetManager assetManager;
    private final List<UIElement> elements = new ArrayList<>();
    public final int screenWidth;
    public final int screenHeight;

    public UIManager(Node guiNode, AssetManager assetManager, Camera camera) {
        this.root = guiNode;
        this.assetManager = assetManager;
        this.screenHeight = camera.getHeight();
        this.screenWidth = camera.getWidth();
    }

    public void addElement(UIElement element) {
        elements.add(element);
        root.attachChild(element.getNode());
    }

    public void removeElement(String name) {
        for (UIElement element: elements) {
            if (element.getName().equals(name)) {
                post_tick_removal.add(element);
                break;
            }
        }
    }

    // we have to remove elements after the tick loop so that if an element removes itself during a tick, we don't get a
    // concurrentmodification exception
    private final List<UIElement> post_tick_removal = new ArrayList<>();

    public void tick(float tpf) {
        for(UIElement element: elements) {
            element.tick(tpf);
        }
        elements.removeAll(post_tick_removal);
        for(UIElement element: post_tick_removal) {
            root.detachChild(element.getNode());
        }
        post_tick_removal.clear();
    }

}
