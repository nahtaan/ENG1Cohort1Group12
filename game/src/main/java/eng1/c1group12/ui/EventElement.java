package eng1.c1group12.ui;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;

public class EventElement extends UIElement {
    private final BitmapText itemCounterText;
    private final BitmapText eventCounterText;
    private final BitmapText messageText;
    private float messageTime = 0;
    private int eventCount = 0;
    private int itemCount = 0;

    public EventElement(String name, AssetManager assetManager,
                        int screenWidth, int screenHeight) {
        super(name);
        BitmapFont font = assetManager.loadFont("Interface/Fonts/Default.fnt");

        // Display the number of items used
        itemCounterText = new BitmapText(font);
        itemCounterText.setSize(20);
        itemCounterText.setText("Items Used: 0");
        itemCounterText.setLocalTranslation(screenWidth - 200, screenHeight - 35, 0);
        getNode().attachChild(itemCounterText);

        // Display the number of events triggered
        eventCounterText = new BitmapText(font);
        eventCounterText.setSize(20);
        eventCounterText.setText("Events: 0");
        eventCounterText.setLocalTranslation(screenWidth - 200, screenHeight - 75, 0);
        getNode().attachChild(eventCounterText);

        // Display a message when an event happens
        messageText = new BitmapText(font);
        messageText.setSize(20);
        messageText.setLocalTranslation(screenWidth - 430, screenHeight - 115, 0);
        getNode().attachChild(messageText);
    }

    public void useItem() {
        itemCount += 1;
        itemCounterText.setText("Items Used: " + itemCount);
    }

    public void showEvent(String message) {
        messageText.setText(message);
        messageTime = 4f;
        eventCount += 1;
        eventCounterText.setText("Events: " + eventCount);
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

