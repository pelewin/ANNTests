package org.pelevin.ann.visual;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import com.pelevin.gameengine.Sprite;

public class WallSprite extends Sprite {

    public WallSprite(double x, double y, double WIDTH) {
        Rectangle rect = new Rectangle(x, y, WIDTH, WIDTH);


        rect.setFill(Color.BLACK);
        
        // set javafx node to a circle
        node = rect;
            
    }

    /**
     * Returns a node casted as a JavaFX Circle shape. 
     * @return Circle shape representing JavaFX node for convenience.
     */
    public Rectangle getAsRectangle() {
        return (Rectangle) node;
    }

    @Override
    public void update() {

    }
}
