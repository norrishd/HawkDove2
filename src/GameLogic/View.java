package GameLogic;

import Agents.Agent;
import Agents.DoveAgent;
import Agents.HawkAgent;
import Tiles.Tile;
import Tiles.TilePattern;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;

/**
 * View class to handle display logic
 */
public class View extends Application {

    // GameLogic.WorldSettings variables
    private final Group root = new Group();
    private final Group tiles = new Group();
    private final Group food = new Group();
    private final Group highlightedFood = new Group();
    private final Group agents = new Group();

    /**
     * Draw the game tiles
     */
    public void drawWorldTiles(Tile[][] worldTiles, int tileSize, int offset) {

        for (int y = 0; y < worldTiles.length; y++) {
            for (int x = 0; x < worldTiles[0].length; x++) {
                Rectangle newRec = new Rectangle(x * tileSize + offset, y * tileSize + offset, tileSize, tileSize);
                if (!worldTiles[x][y].walkable()) {
                    newRec.setFill(Color.DARKGRAY);
                } else {
                    newRec.setFill(Color.DARKGREEN);
                }
                tiles.getChildren().add(newRec);
            }
        }
    }

    // Draw all food items on the board
    public void drawFood(Tile[][] worldTiles, int tileSize, int offset) {

        food.getChildren().clear();

        for (int y = 0; y < worldTiles.length; y++) {
            for (int x = 0; x < worldTiles[0].length; x++) {
                // if food on this tile, draw a circle on top
                if (worldTiles[x][y].hasFood()) {

                    food.getChildren().add(new Circle(x * tileSize + offset + tileSize / 2,
                            y * tileSize + offset + tileSize / 2, tileSize / 6, Color.DARKRED));
                }
            }
        }
    }

    public void highlightGoalFood(ArrayList<Agent> currentAgents, int tileSize, int offset) {

        highlightedFood.getChildren().clear();

        for (Agent agent : currentAgents) {
            if (agent.goal != null) {
                Position goalFood = agent.goal.tile.position;
                highlightedFood.getChildren().add(new Circle(goalFood.x * tileSize + offset + tileSize / 2,
                        goalFood.y * tileSize + offset + tileSize / 2, tileSize / 6, Color.GOLD));
            }
        }
    }

    public void drawAgents(ArrayList<Agent> currentAgents, int tileSize, int offset) {

        agents.getChildren().clear();

        for (Agent agent : currentAgents) {
            if (agent instanceof DoveAgent)
                agents.getChildren().add(new Circle(agent.position.x * tileSize + offset + tileSize / 2,
                        agent.position.y * tileSize + offset +
                                tileSize / 2, tileSize / 4, Color.LIGHTBLUE));
            else if (agent instanceof HawkAgent)
                agents.getChildren().add(new Circle(agent.position.x * tileSize + offset + tileSize / 2,
                        agent.position.y * tileSize + offset +
                                tileSize / 2, tileSize / 4, Color.PALEVIOLETRED));
        }
    }

    private void makecontrols(GridWorld gridWorld, Controller controller) {
        // Control panel background rectangle
        Rectangle newRec = new Rectangle(gridWorld.WINDOW_WIDTH - gridWorld.PANEL_WIDTH - gridWorld.OFFSET,
                gridWorld.OFFSET, gridWorld.PANEL_WIDTH, gridWorld.WINDOW_HEIGHT - 2 * gridWorld.OFFSET);
        newRec.setFill(Color.CHARTREUSE);
        root.getChildren().add(newRec);

        Button nextTurnButton = new Button("Next turn");
        nextTurnButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                controller.nextTurn();
                }
        });

        Button newGameButton = new Button("New game");
        newGameButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                controller.newGame();
            }
        });

        HBox hb = new HBox();
        hb.getChildren().addAll(nextTurnButton, newGameButton);
        hb.setSpacing(20);
        hb.setLayoutX(gridWorld.WINDOW_WIDTH - gridWorld.PANEL_WIDTH - gridWorld.OFFSET);
        hb.setLayoutY(gridWorld.OFFSET);
        root.getChildren().add(hb);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {

        // Create a GridWorld (Model), generate tiles
        GridWorld gridWorld = new GridWorld();
        Controller controller = new Controller();
        controller.addModel(gridWorld);
        controller.addView(this);
        makecontrols(gridWorld, controller);
        controller.newGame();

        Scene scene = new Scene(root, gridWorld.WINDOW_WIDTH, gridWorld.WINDOW_HEIGHT);
        primaryStage.setTitle("HawkDove: A Game Theory Battleground");

        root.getChildren().add(tiles);
        root.getChildren().add(food);
        root.getChildren().add(highlightedFood);
        root.getChildren().add(agents);

        primaryStage.setScene(scene);

        /*// Timeline that makes all the action happen!
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(100), handler -> {
                }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();*/

        primaryStage.show();
    }
}