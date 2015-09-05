/*
 * Encog(tm) Java Examples v3.2
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-examples
 *
 * Copyright 2008-2013 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.pelevin.ann.training;

import org.encog.ml.CalculateScore;
import org.encog.ml.MLMethod;
import org.encog.neural.neat.NEATNetwork;

public class LifeScore implements CalculateScore {

	private int numOfGames;
	private int fillFactor;

	public LifeScore(int numOfGames, int fillFactor) {
		this.numOfGames = numOfGames;
		this.fillFactor = fillFactor;
	}

	@Override
	public double calculateScore(MLMethod network) {

		Game2World gameWorld = new Game2World(50, 50, fillFactor);
		gameWorld.generateWalls();

		double singleStepScore = getSingleStepScore(gameWorld, (NEATNetwork) network);
		if (singleStepScore < 100) return singleStepScore;

		double fullGameScore = getFullGameScoreNTimes(gameWorld, (NEATNetwork) network);

		return singleStepScore + fullGameScore;
	}

	private double getFullGameScoreNTimes(Game2World gameWorld, NEATNetwork network) {

		double score = 0;

		for (int i = 0; i < numOfGames; i++) {

			gameWorld.generateWalls();
			Life life = new Life(gameWorld, network);

			for (int j = 0; j < gameWorld.getWidth() + gameWorld.getHeight(); j++) {
				life.update();
			}
			long curScore = Math.round(gameWorld.calculateScore(life));
			if (curScore == 0) return score;

			score = score + curScore;
		}

		return score;
	}


	private double getSingleStepScore(Game2World gameWorld, NEATNetwork network) {

		double score = 0;

		Life life = new Life(gameWorld, network);
		for (int i = 0; i < gameWorld.getWidth() + gameWorld.getHeight(); i++) {
			double curX = life.getX();
			double curY = life.getY();

			//step somewhere
			life.update();
			if (life.getX() != curX || life.getY() != curY)
				score++;

			//don't come back
			life.update();
			if (life.getX() != curX || life.getY() != curY)
				score++;
		}

		return score;
	}

	public boolean shouldMinimize() {
		return false;
	}


	@Override
	public boolean requireSingleThreaded() {
		return false;
	}
}
