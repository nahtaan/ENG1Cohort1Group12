package eng1.c1group12.ui;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.system.Timer;
import eng1.c1group12.Main;

public class TimerElement extends UIElement {

    private final BitmapText text;
    private final Timer timer;
    private final float duration;
    private boolean has_completed = false;
    private final Runnable end_task;

    public TimerElement(String name, AssetManager assetManager, Timer timer, float seconds, Runnable end_task) {
        super(name);
        this.timer = timer;
        this.duration = seconds;
        this.end_task = end_task;

        BitmapFont font = assetManager.loadFont("Interface/Fonts/Default.fnt");

        text = new BitmapText(font);

        text.setSize(48);
        text.setColor(ColorRGBA.White);

        text.setLocalTranslation(0, Main.uiManager.screenHeight, 0);
        getNode().attachChild(text);
    }

    @Override
    public void tick(float tpf) {
        text.setText(format_time(duration - timer.getTimeInSeconds()));
        if(duration - timer.getTimeInSeconds() <= 0 && !has_completed) {
            has_completed = true;
            end_task.run();
        }
    }

    private String format_time(float time) {
        StringBuilder sb = new StringBuilder();
        int minutes = Math.floorDiv((int) time, 60);
        sb.append(minutes).append(':');
        int seconds = (int) time - (minutes*60);
        if(seconds<10) {
            sb.append("0");
        }
        sb.append(seconds);
        return sb.toString();
    }

}
