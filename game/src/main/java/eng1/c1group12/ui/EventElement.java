package eng1.c1group12.ui;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;

public class EventElement extends UIElement {
    private final BitmapText counterText;
    private final BitmapText messageText;
    private float messageTime = 0;

    public EventElement(String name, AssetManager assetManager,
                        int screenWidth, int screenHeight) {
        super(name);
        BitmapFont font = assetManager.loadFont("Interface/Fonts/Default.fnt");

        // Display the number of events triggered
        counterText = new BitmapText(font);
        counterText.setSize(20);
        counterText.setText("Events: 0/2");
        counterText.setLocalTranslation(screenWidth - 200, screenHeight - 35, 0);
        getNode().attachChild(counterText);

        // Display a message when an event happens
        messageText = new BitmapText(font);
        messageText.setSize(20);
        messageText.setLocalTranslation(screenWidth - 430, screenHeight - 75, 0);
        getNode().attachChild(messageText);
    }

    public void showEvent(String message, int count) {
        counterText.setText("Events: " + count + "/2");
        messageText.setText(message);
        messageTime = 4f;
    }

    @Override
    public void tick(float tpf) {
        if (messageTime > 0) {
            messageTime -= tpf;

            if (messageTime <= 0) {
                messageText.setText("");
            }
        }
    }
}

