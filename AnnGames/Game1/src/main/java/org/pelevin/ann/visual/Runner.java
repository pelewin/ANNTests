package org.pelevin.ann.visual;

import javafx.application.Application;
import javafx.stage.Stage;
import com.pelevin.gameengine.GameWorld;

/**
 * Created by dmitry on 30.06.15.
 */
public class Runner extends Application {

	GameWorld gameWorld = new Game1World(20, "ANN Game1");

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		// setup title, scene, stats, controls, and actors.
		gameWorld.initialize(primaryStage);

		// kick off the game loop
		gameWorld.beginGameLoop();

		// display window
		primaryStage.show();
	}


}
