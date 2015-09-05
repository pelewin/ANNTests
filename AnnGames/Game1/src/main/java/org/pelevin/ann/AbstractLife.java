package org.pelevin.ann;

import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.neural.neat.NEATNetwork;
import com.pelevin.gameengine.Sprite;

import static org.pelevin.ann.Constants.WALL;

/**
 * Created by dmitry on 19.07.15.
 */
public abstract class AbstractLife extends Sprite {

	protected GameRules world;
	protected int x;
	protected int y;
	protected boolean successfullStep;
	protected int[][] vision = new int[5][5];
	protected NEATNetwork network;
	MLData output;

	private MLData vision2data() {
		MLData result = new BasicMLData(25);

		for (int y = 0; y < 5; y++)
			for (int x = 0; x < 5; x++) {
				if (vision[x][y] == WALL)
					result.setData(y * 5 + x, 1);
				else
					result.setData(y * 5 + x, 0);
			}

		if (output != null) {
			//result.setData(25, output.getData(0));
			//result.setData(26, output.getData(0));
			//result.setData(27, (successfullStep) ? 1 : 0);
		}
		return result;
	}

	protected void makeDecision() {

		MLData input = vision2data();

		output = network.compute(input);

		if (output.getData(0) < 0.4) vX = -1;
		else if (output.getData(0) > 0.4) vX = 1;
		else vX = 0;

		if (output.getData(1) < 0.4) vY = -1;
		else if (output.getData(1) > 0.4) vY = 1;
		else vY = 0;

	}

	public void printVision() {

		/*world.printLog("Current vision");
		for (int y = 0; y < 5; y++) {
			StringBuilder line = new StringBuilder();
			for (int x = 0; x < 5; x++) {
				if (y == 2 && x == 2)
					line.append('0');
				else if (vision[x][y] == WALL)
					line.append('1');
				else
					line.append('*');
			}
			world.printLog(line.toString());
		}*/
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getvX() {
		return vX;
	}

	public int getvY() {
		return vY;
	}
}
