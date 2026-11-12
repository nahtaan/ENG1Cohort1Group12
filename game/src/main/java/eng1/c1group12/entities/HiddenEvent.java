package eng1.c1group12.entities;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import eng1.c1group12.Main;
import eng1.c1group12.ui.EventElement;

public abstract class HiddenEvent extends Entity {
    private final float radius;
    private boolean triggered = false;
    private final EventElement eventUI;

    public HiddenEvent(AssetManager assetManager, String name, EventElement eventUI, float x, float y, float radius) {
        super(assetManager, name, "hidden_event");
        this.eventUI = eventUI;
        this.radius = radius;
        getNode().setLocalTranslation(x, y, 0);
    }

    private boolean isInRadius(Vector3f playerPos) {
        if (triggered) {
            return false;
        }
        Vector3f eventPos = getNode().getLocalTranslation();

        float dx = playerPos.x - eventPos.x;
        float dy = playerPos.y - eventPos.y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance <= radius) {
            triggered = true;
            return true;
        }
        return false;
    }

    /**
     * Called when this event is triggered by the player.
     */
    public abstract void onTrigger();

    /**
     * @return The message to display the player when this event is triggered.
     */
    public abstract String getMessage();

    @Override
    public void tick(float tpf) {
        Vector3f playerPos = Main.worldManager.getPlayer().getNode().getLocalTranslation();
        if (isInRadius(playerPos)) {
            eventUI.showEvent(getMessage());
            this.onTrigger();
            Main.worldManager.removeEntity(getName());
        }
    }
}

