package eng1.c1group12.world;

import com.jme3.math.Vector3f;

public class HiddenEvent {
    private final String name;
    private final float x;
    private final float y;
    private final float radius;
    private boolean triggered = false;

    public HiddenEvent(String name, float x, float y, float radius) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    public boolean checkTrigger(Vector3f playerPos) {
        if (triggered) {
            return false;
        }

        float dx = playerPos.x - x;
        float dy = playerPos.y - y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance <= radius) {
            triggered = true;
            return true;
        }
        return false;
    }

    public String getName() {
        return name;
    }
}

