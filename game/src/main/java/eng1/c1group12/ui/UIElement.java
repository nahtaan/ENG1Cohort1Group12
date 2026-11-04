package eng1.c1group12.ui;

import com.jme3.scene.Node;

public abstract class UIElement {

    private final String name;
    private final Node node;

    public UIElement(String name) {
        this.name = name;
        this.node = new Node(name);
    }

    /**
     * Called on each frame update.
     */
    public abstract void tick(float tpf);

    public String getName() { return name; }
    public Node getNode() { return node; }
}
