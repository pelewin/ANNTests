package org.pelevin.ann.training;

import org.pelevin.ann.GameRules;

public class Game2World extends GameRules {


	public Game2World(int width, int height, int fillFactor) {
		super(width, height, fillFactor);
		generateWalls();
	}

}
