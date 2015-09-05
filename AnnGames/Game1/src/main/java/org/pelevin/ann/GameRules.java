package org.pelevin.ann;

import java.util.Random;

import static org.pelevin.ann.Constants.WALL;

/**
 * Created by dmitry on 19.07.15.
 */
public class GameRules {

	protected int fillFactor;
	public int width;
	public int height;
	public int[][] walls;

	public GameRules(int width, int height, int fillFactor) {
		this.width = width;
		this.fillFactor = fillFactor;
		this.height = height;
	}

	public void generateWalls() {
		Random rnd = new Random();

		walls = new int[this.width][this.height];

		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++) {

				if (x == 0 && y == 0) continue;

				if (rnd.nextInt(100) <= fillFactor) {
					walls[x][y] = WALL;
				}
			}

	}

	public double calculateScore(AbstractLife life) {
		if (life == null) return 0;
		double curX = life.getX() + 1;
		double curY = life.getY() + 1;
		double totalRoute = Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2));
		double remaindRoute = Math.sqrt(Math.pow(width - curX, 2) + Math.pow(height - curY, 2));
		return (1 - remaindRoute / totalRoute) * 100;
	}

	public boolean checkMove(int xx, int yy) {
		return xx >= 0
				&& yy >= 0
				&& xx < width
				&& yy < height
				&& walls[xx][yy] != WALL;
	}

	public void getVision(int[][] vision, int x, int y) {
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				int curX = i + x - 2;
				int curY = j + y - 2;

				if (curX < 0 || curY < 0
						|| curX >= width
						|| curY >= width) {
					vision[i][j] = WALL;
				} else {
					vision[i][j] = walls[curX][curY];
				}
			}
		}
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
