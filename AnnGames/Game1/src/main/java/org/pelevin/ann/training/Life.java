package org.pelevin.ann.training;

import org.encog.neural.neat.NEATNetwork;
import org.encog.neural.networks.BasicNetwork;


public class Life extends org.pelevin.ann.AbstractLife {

	public Life(Game2World world, NEATNetwork network) {
		this.network = network;
		this.vY = 1;
		this.y = 0;
		this.world = world;
		this.x = 0;
		this.vX = 1;
	}

	public void update() {

		world.getVision(vision, x, y);

		//printVision();
		makeDecision();

		int xx = x + vX;
		int yy = y + vY;

		successfullStep = world.checkMove(xx, yy);
		if (successfullStep) {
			x = xx;
			y = yy;
		}
	}

}
