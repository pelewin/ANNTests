package org.pelevin.ann.visual;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.encog.neural.neat.NEATNetwork;
import org.encog.neural.networks.BasicNetwork;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.obj.SerializeObject;
import org.pelevin.ann.AbstractLife;

import java.io.File;
import java.io.IOException;
import java.util.Random;


public class LifeSprite extends AbstractLife {

	public static final String FILENAME = "data/lifeNetwork.dp";
	private int scale;

	public LifeSprite(Game1World world, int x, int y, int scale, boolean isNeural) {

		this.world = world.gameRules;
		this.x = x;
		this.y = y;
		this.scale = scale;

		Rectangle rect = new Rectangle(x * scale, y * scale, scale, scale);

		if (isNeural) {
			rect.setFill(Color.GREEN);
			//this.network = (NEATNetwork) EncogDirectoryPersistence.loadObject(new File(FILENAME));
			try {
				this.network = (NEATNetwork) SerializeObject.load(new File(FILENAME));
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} else
			rect.setFill(Color.RED);

		this.vX = 1;
		this.vY = 1;

		// set javafx node to a circle
		this.node = rect;

	}

	public Rectangle getAsRectangle() {
		return (Rectangle) node;
	}

	@Override
	public void update() {

		world.getVision(vision, x, y);

		//printVision();

		if (network == null)
			staticDecision();
		else
			makeDecision();

		int xx = x + vX;
		int yy = y + vY;

		if (world.checkMove(xx, yy)) {
			x = xx;
			y = yy;
			node.setTranslateX(xx * scale);
			node.setTranslateY(yy * scale);
		}
	}

	private void staticDecision() {
		Random rnd = new Random();
		if (!world.checkMove(x + vX, y + vY)) {
			//vX = vX * (rnd.nextInt(2) - 1);
			//vY = vY * (rnd.nextInt(2) - 1);
			if (vision[3][3] == 0) {vX = 1; vY = 1;}
			else if (vision[2][3] == 0) {vX = 0; vY = 1;}
			else if (vision[3][2] == 0) {vX = 1; vY = 0;}
			else if (vision[1][3] == 0) {vX = -1; vY = 1;}
			else if (vision[3][1] == 0) {vX = 1; vY = -1;}
			else if (vision[1][2] == 0) {vX = -1; vY = 0;}
			else if (vision[2][1] == 0) {vX = 0; vY = -1;}
			else if (vision[1][1] == 0) {vX = -1; vY = -1;}

		}
	}


}
