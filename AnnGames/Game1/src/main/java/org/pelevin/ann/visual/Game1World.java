package org.pelevin.ann.visual;

import javafx.animation.Animation;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.pelevin.ann.GameRules;
import com.pelevin.gameengine.GameWorld;
import com.pelevin.gameengine.Sprite;

import java.util.List;

import static org.pelevin.ann.Constants.LIFE;
import static org.pelevin.ann.Constants.WALL;

/**
 * This is a simple game world simulating a bunch of spheres looking
 * like atomic particles colliding with each other. When the game loop begins
 * the user will notice random spheres (atomic particles) floating and
 * colliding. The user is able to press a button to generate more
 * atomic particles. Also, the user can freeze the game.
 *
 * @author cdea
 */
public class Game1World extends GameWorld {
	/**
	 * Read only field to show the number of sprite objects are on the field
	 */
	private int NUMBER_OF_SPIECES = 1;
	private final static Label NUM_SPRITES_FIELD = new Label();

	private int SCALE = 10;

	private int epoch = 0;
	private int iteration = 0;

	GameRules gameRules = new GameRules(50, 50, 20);

	TextArea textArea;

	public Game1World(int fps, String title) {
		super(fps, title);
	}

	/**
	 * Initialize the game world by adding sprite objects.
	 */
	@Override
	public void initialize(final Stage primaryStage) {

		// Sets the window title
		primaryStage.setTitle(getWindowTitle());

		//VBox root = new VBox();

		BorderPane root = new BorderPane();
		VBox topBox = new VBox();
		VBox rightBox = new VBox();
		Group worldGroup = new Group();

		root.setTop(topBox);
		root.setRight(rightBox);

		root.setCenter(worldGroup);

		Scene scene = new Scene(root, 800, 600, Color.WHITE);
		primaryStage.setScene(scene);

		final Menu menu1 = new Menu("Actions");
		final MenuItem pauseMenu = new MenuItem("Pause");
		final MenuItem resetMenu = new MenuItem("Reset");
		menu1.getItems().addAll(pauseMenu, resetMenu);

		pauseMenu.setOnAction(this::handlePause);
		pauseMenu.setAccelerator(new KeyCodeCombination(KeyCode.SPACE, KeyCombination.CONTROL_ANY));

		resetMenu.setOnAction(this::handleReset);

		MenuBar menuBar = new MenuBar();
		menuBar.getMenus().addAll(menu1);

		// lay down the controls
		topBox.getChildren().add(menuBar);

		// Create the scene
		setSceneNodes(worldGroup);
		setGameSurface(scene);

		textArea = new TextArea();
		textArea.setPrefHeight(scene.getHeight());
		textArea.setPrefWidth(150);
		textArea.setFont(Font.font("Monospaced", 10));
		textArea.setEditable(false);
		rightBox.getChildren().add(textArea);

		// Create objects
		generateWalls();
		startEpoch();
	}

	/**
	 * Make some more space spheres (Atomic particles)
	 */
	public void generateWalls() {
		gameRules.generateWalls();
		for (int x = 0; x < gameRules.width; x++)
			for (int y = 0; y < gameRules.height; y++) {

				if (gameRules.walls[x][y] == WALL) {
					WallSprite b = new WallSprite(x * SCALE, y * SCALE, SCALE);
					Rectangle rect = b.getAsRectangle();
					rect.setVisible(true);
					rect.setId(b.toString());

					// add to actors in play (sprite objects)
					getSpriteManager().addSprites(WALL, b);

					// add sprite's
					getSceneNodes().getChildren().add(0, b.node);
				}
			}

	}

	private void generateLife() {
		//neural
		for (int i = 0; i < NUMBER_OF_SPIECES; i++) {

			LifeSprite neuralLifeSprite = new LifeSprite(this, 0, 0, SCALE, true);
			Rectangle rect = neuralLifeSprite.getAsRectangle();
			rect.setVisible(true);
			rect.setId(neuralLifeSprite.toString());

			// add to actors in play (sprite objects)
			getSpriteManager().addSprites(LIFE, neuralLifeSprite);

			// add sprite's
			getSceneNodes().getChildren().add(0, neuralLifeSprite.node);
		}

		//static
		LifeSprite staticLifeSprite = new LifeSprite(this, 0, 0, SCALE, false);
		Rectangle rect = staticLifeSprite.getAsRectangle();
		rect.setVisible(true);
		rect.setId(staticLifeSprite.toString());

		// add to actors in play (sprite objects)
		getSpriteManager().addSprites(LIFE, staticLifeSprite);

		// add sprite's
		getSceneNodes().getChildren().add(0, staticLifeSprite.node);

	}


	/**
	 * Each sprite will update it's velocity and bounce off wall borders.
	 */
	@Override
	protected void handleUpdate(Integer type, List<Sprite> sprites) {
		sprites.forEach(Sprite::update);
	}

	@Override
	protected void updateSprites() {
		super.updateSprites();
		iteration++;
		if (iteration == gameRules.width + gameRules.height) {
			handleReset(null);
		}
	}

	public void startEpoch() {

		getSpriteManager().addSpritesToBeRemovedByType(LIFE);

		List<Sprite> lifeCollection = getSpriteManager().getSpritesByType(LIFE);

		if (lifeCollection.size() > 1) {
			printLog("Current score: \n" +
					"    neural:" + gameRules.calculateScore((LifeSprite) lifeCollection.get(0)) + "\n" +
					"    static:" + gameRules.calculateScore((LifeSprite) lifeCollection.get(1)));
		}

		printLog("Current epoch: " + epoch);

		iteration = 0;
		epoch++;
		generateLife();
	}

	/**
	 * How to handle the collision of two sprite objects. Stops the particle
	 * by zeroing out the velocity if a collision occurred.
	 */
	@Override
	protected boolean handleCollision(Sprite spriteA, Sprite spriteB) {
		return false;
	}

	/**
	 * Remove dead things.
	 */
	@Override
	protected void cleanupSprites() {
		// removes from the scene and backend store
		super.cleanupSprites();

		// let user know how many sprites are showing.
		NUM_SPRITES_FIELD.setText(String.valueOf(getSpriteManager().getAllSprites().size()));

	}

	public void printLog(String str) {
		textArea.appendText(str);
		textArea.appendText("\n");
	}

	private void handlePause(ActionEvent actionEvent) {
		if (getGameLoop().statusProperty().getValue().equals(Animation.Status.PAUSED))
			getGameLoop().play();
		else
			getGameLoop().pause();
	}

	private void handleReset(ActionEvent actionEvent) {
		getSpriteManager().addSpritesToBeRemovedByType(WALL);

		epoch = 0;
		generateWalls();
		startEpoch();
	}

}
