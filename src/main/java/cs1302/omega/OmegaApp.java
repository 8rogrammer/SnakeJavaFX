package cs1302.omega;

import cs1302.game.DemoGame;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.*;
import javafx.animation.AnimationTimer;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

/**
 * Snake Game. Play as a snake and try to eat the food to get a high score.
 */
public class OmegaApp extends Application {

    static boolean gameOver = false;
    static Dir direction = Dir.left;
    static Random rand = new Random();
    static int speed = 5;
    static int width = 20;
    static int height = 20;
    static int xFood = 0;
    static int yFood = 0;
    static long endTime = 0;
    static int cornerSize = 25;
    static List<Corner> snake = new ArrayList<>();
    static int i = 0;

    /**
     * The directions that a player can move.
     */
    public enum Dir {
        left, right, up, down, space
    }

    /**
     * represents the blocks on the screen.
     */
    public static class Corner {
        int x;
        int y;

        /**
         * constuctor for x and y coordinates on the grid.
         *
         * @param x
         * @param y
         */
        public Corner(int x, int y) {
            this.y = y;
            this.x = x;
        }
    }


    /**
     * Constructs an {@code OmegaApp} object. This default (i.e., no argument)
     * constructor is executed in Step 2 of the JavaFX Application Life-Cycle.
     */
    public OmegaApp() {
        //    new OmegaApp();

    }

    /** {@inheritDoc} */
    @Override
    public void start(Stage stage) {
        try {
            startGame(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    } // start

    /**
     * creates the game and passes the user's inputs.
     */
    public void startGame(Stage stage) {
        food();
        VBox vbox = new VBox();
        Canvas canvas = new Canvas(width * cornerSize, height * cornerSize);
        GraphicsContext graphicscontext = canvas.getGraphicsContext2D();
        vbox.getChildren().add(canvas);
        new AnimationTimer() {
            long endTime = 0;
            public void handle(long time) {
                if (endTime == 0) {
                    endTime = time;
                    timer(graphicscontext);
                }
                if (time - endTime > 100000000 / speed) {
                    endTime = time;
                    timer(graphicscontext);
                }
            }
        }.start();

        Scene scene = new Scene(vbox, width * cornerSize, height * cornerSize);
        scene.addEventFilter(KeyEvent.KEY_PRESSED, key -> {
            System.out.println("Key pressed: " + key.getCode());
            if (key.getCode() == KeyCode.UP) {
                direction = Dir.up;
            }
            else if (key.getCode() == KeyCode.LEFT) {
                direction = Dir.left;
            }
            else if (key.getCode() == KeyCode.DOWN) {
                direction = Dir.down;
            }
            else  if (key.getCode() == KeyCode.RIGHT) {
                direction = Dir.right;
            }
            else if (key.getCode() == KeyCode.SPACE) {
                restartGame(graphicscontext);
            }
        });
        for (int i = 0; i < 3; i++) {
            snake.add(new Corner(width / 2, height / 2));
        }
        // setup stage
        stage.setTitle("OmegaApp!");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> Platform.exit());
        stage.sizeToScene();
        stage.show();
    }

    public static void restartGame(GraphicsContext graphicscontext) {
        System.out.println("reset was done");
        graphicscontext.clearRect(0, 0, width * cornerSize, height * cornerSize);
        gameOver = false;
        speed = 6;
        List<Corner> snakes = new ArrayList<>();
        snake = snakes;
        snake.get(0).x = 10;
        snake.get(0).y = 10;
        for (int i = 0; i < 3; i++) {
            snake.add(new Corner(width / 2, height / 2));
        }

    }

    /**
     * serves as the main game, preforms various actions to allow for content of game to work.
     *
     * @param graphicscontext
     */
    public static void timer(GraphicsContext graphicscontext) {
        System.out.println("timer is being called @ " + new Date());
        if (gameOver) {
            graphicscontext.setFill(Color.BLUE);
            graphicscontext.setFont(new Font("", 50));
            graphicscontext.fillText("Game Over", 100, 250);
            graphicscontext.setFont(new Font("", 25));
            graphicscontext.fillText("Hit space to start a new game", 100, 300);
            return;
        }
        for (int i = snake.size() - 1; i >= 1; i--) {
            snake.get(i).x = snake.get(i - 1).x;
            snake.get(i).y = snake.get(i - 1).y;
        }

        switch (direction) {
        case up:
            snake.get(0).y--;
            if (snake.get(0).y < 0) {
                gameOver = true;
            }
            break;
        case down:
            snake.get(0).y++;
            if (snake.get(0).y > height) {
                gameOver = true;
            }
            break;
        case left:
            snake.get(0).x--;
            if (snake.get(0).x < 0) {
                gameOver = true;
            }
            break;
        case right:
            snake.get(0).x++;
            if (snake.get(0).x > width) {
                gameOver = true;
            }
            break;
        }
        eatFood();
        dead();
        createBoard(graphicscontext);
        snake(graphicscontext);
    }


    /**
     * creates the board.
     *
     * @param graphicscontext
     */
    public static void createBoard(GraphicsContext graphicscontext) {
        graphicscontext.setFill(Color.LIGHTBLUE);
        graphicscontext.fillRect(0, 0, width * cornerSize, height * cornerSize);
        graphicscontext.setFill(Color.BLACK);
        graphicscontext.setFont(new Font("", 25));
        graphicscontext.fillText("Score: " + (speed - 6), 15, 45);
        graphicscontext.setFill(Color.RED);
        int xBoard = xFood * cornerSize;
        int yBoard = yFood * cornerSize;
        graphicscontext.fillOval(xBoard, yBoard, cornerSize, cornerSize);
    }

    /**
     * Allows for the snake to eat.
     */
    public static void eatFood() {
        if (xFood == snake.get(0).x && yFood == snake.get(0).y) {
            snake.add(new Corner(-1, -1));
            food();
        }
    }

    /**
     * Gameover method that ends the game if condition is met.
     *
     * @param graphicscontext
     */
    public static void gameOver(GraphicsContext graphicscontext) {
        if (gameOver) {
            graphicscontext.setFill(Color.BLUE);
            graphicscontext.setFont(new Font("", 50));
            graphicscontext.fillText("Game Over", 100, 250);
            graphicscontext.fillText("Hit space to start a new game", 150, 250);
            return;
        }
    }


    /**
     * If the snake runs into itself the snake dies.
     */
    public static void dead() {
        for (int i = 1; i < snake.size(); i++) {
            if (snake.get(0).x == snake.get(i).x && snake.get(0).y == snake.get(i).y) {
                gameOver = true;
            }
        }
    }

    /**
     * Allows for the snake to move.
     *
     * @param graphicscontext
     */
    public static void snake(GraphicsContext graphicscontext) {
        for (Corner c : snake) {
            graphicscontext.setFill(Color.GREEN);
            int xSnake = c.x * cornerSize;
            int ySnake = c.y * cornerSize;
            graphicscontext.fillRect(xSnake, ySnake, cornerSize - 2, cornerSize - 1);
        }
    }

    /**
     * Randomly places food as the snake eats it.
     */
    public static void food() {
    start : while (true) {
            xFood = rand.nextInt(width);
            yFood = rand.nextInt(height);
            for (Corner c : snake) {
                if (c.x == xFood && c.y == yFood) {
                    continue start;
                }
            }
            speed++;
            break;
        }
    }

    /**
     * Main method that launches the program.
     *
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }

} // OmegaApp
