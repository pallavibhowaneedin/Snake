import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.LinkedList;
import java.util.Random;

public class SnakeApp extends Application {

    private static final int TILE_SIZE = 20;
    private static final int WIDTH = 15;
    private static final int HEIGHT = 10;

    private Pane snakePane;
    private LinkedList<Rectangle> snake;
    private Rectangle redPixel;
    private int directionX = 1; // Initial direction: right
    private int directionY = 0;
    private boolean gameOver = false;
    private Timeline timeline;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        snakePane = new Pane();
        root.setCenter(snakePane);

        redPixel = createRedPixel();
        snake = createSnake();

        Scene scene = new Scene(root, WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);
        scene.setOnKeyPressed(e -> handleKeyPress(e.getCode()));

        root.setBottom(new Label("Score: 0"));
        BorderPane.setAlignment(root.getBottom(), Pos.CENTER);

        primaryStage.setScene(scene);
        primaryStage.setTitle("SnakeFX");
        primaryStage.show();

        startGameLoop();
    }

    private LinkedList<Rectangle> createSnake() {
        LinkedList<Rectangle> initialSnake = new LinkedList<>();
        for (int i = 0; i < 3; i++) {
            Rectangle segment = new Rectangle(TILE_SIZE, TILE_SIZE, Color.BLACK);
            segment.setTranslateX((WIDTH / 2 - i) * TILE_SIZE);
            segment.setTranslateY(HEIGHT / 2 * TILE_SIZE);
            snakePane.getChildren().add(segment);
            initialSnake.add(segment);
        }
        return initialSnake;
    }

    private Rectangle createRedPixel() {
        Rectangle pixel = new Rectangle(TILE_SIZE, TILE_SIZE, Color.RED);
        placeRedPixel();
        return pixel;
    }

    private void placeRedPixel() {
        Random random = new Random();
        int x, y;
        do {
            x = random.nextInt(WIDTH) * TILE_SIZE;
            y = random.nextInt(HEIGHT) * TILE_SIZE;
        } while (collidesWithSnake(x, y));

        redPixel = new Rectangle(TILE_SIZE, TILE_SIZE, Color.RED);
        redPixel.setTranslateX(x);
        redPixel.setTranslateY(y);
        snakePane.getChildren().add(redPixel);
    }

    private boolean collidesWithSnake(double x, double y) {
        for (Rectangle segment : snake) {
            if (segment.getTranslateX() == x && segment.getTranslateY() == y) {
                return true;
            }
        }
        return false;
    }

    private void handleKeyPress(KeyCode code) {
        switch (code) {
            case UP:
                directionX = 0;
                directionY = -1;
                break;
            case DOWN:
                directionX = 0;
                directionY = 1;
                break;
            case LEFT:
                directionX = -1;
                directionY = 0;
                break;
            case RIGHT:
                directionX = 1;
                directionY = 0;
                break;
            // Add a case label for NONCONVERT if needed
            case NONCONVERT:
                // Handle NONCONVERT case
                break;
            default:
                // Handle the default case (if needed)
                break;
        }
    }
    

    private void startGameLoop() {
        timeline = new Timeline(new KeyFrame(Duration.millis(200), e -> moveSnake()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void moveSnake() {
        if (!gameOver) {
            double headX = snake.getFirst().getTranslateX() + directionX * TILE_SIZE;
            double headY = snake.getFirst().getTranslateY() + directionY * TILE_SIZE;

            if (headX < 0 || headX >= WIDTH * TILE_SIZE || headY < 0 || headY >= HEIGHT * TILE_SIZE || collidesWithSnake(headX, headY)) {
                handleGameOver();
                return;
            }

            if (headX == redPixel.getTranslateX() && headY == redPixel.getTranslateY()) {
                increaseScore();
                placeRedPixel();
                growSnake();
            }

            moveSnakeBody();
            snake.getFirst().setTranslateX(headX);
            snake.getFirst().setTranslateY(headY);
        }
    }

    private void moveSnakeBody() {
        for (int i = snake.size() - 1; i > 0; i--) {
            double prevX = snake.get(i - 1).getTranslateX();
            double prevY = snake.get(i - 1).getTranslateY();
            snake.get(i).setTranslateX(prevX);
            snake.get(i).setTranslateY(prevY);
        }
    }

    private void growSnake() {
        Rectangle newSegment = new Rectangle(TILE_SIZE, TILE_SIZE, Color.BLACK);
        snake.addLast(newSegment);
        snakePane.getChildren().add(newSegment);
    }

    private void increaseScore() {
        int currentScore = Integer.parseInt(((Label) ((BorderPane) snakePane.getParent()).getBottom()).getText().substring(7));
        currentScore++;
        ((Label) ((BorderPane) snakePane.getParent()).getBottom()).setText("Score: " + currentScore);
    }

    private void handleGameOver() {
        gameOver = true;
        timeline.stop();
        System.out.println("Game Over. Your score: " + Integer.parseInt(((Label) ((BorderPane) snakePane.getParent()).getBottom()).getText().substring(7)));
    }
}
