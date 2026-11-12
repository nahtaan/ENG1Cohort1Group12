package eng1.c1group12.ui;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import eng1.c1group12.Main;

public class TimerElement extends UIElement {

    private final BitmapText text;
    private final float duration;
    private float time_spent = 0;
    private boolean has_completed = false;
    private final Runnable end_task;

    public TimerElement(String name, AssetManager assetManager, float seconds, Runnable end_task) {
        super(name);
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
        time_spent += tpf;
        text.setText(format_time(duration - time_spent));
        if(duration - time_spent <= 0 && !has_completed) {
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
