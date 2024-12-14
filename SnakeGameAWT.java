import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class SnakeGameAWT extends Frame implements Runnable, KeyListener {
    private static final int BOARD_WIDTH = 800;
    private static final int BOARD_HEIGHT = 600;
    private static final int UNIT_SIZE = 25;

    private final int[] x = new int[BOARD_WIDTH * BOARD_HEIGHT / UNIT_SIZE];
    private final int[] y = new int[BOARD_WIDTH * BOARD_HEIGHT / UNIT_SIZE];
    private int snakeLength;
    private int foodX, foodY;
    private int score;

    private char direction;
    private boolean running;
    private boolean gameOver;

    private Thread gameThread;

    public SnakeGameAWT() {
        // Set up the frame
        this.setTitle("Snake Game - AWT");
        this.setSize(BOARD_WIDTH, BOARD_HEIGHT);
        this.setResizable(false);
        this.setVisible(true);
        this.addKeyListener(this);

        // Start the game
        startGame();

        // Close the application on window close
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    public void startGame() {
        // Initialize snake's position and properties
        snakeLength = 3;
        direction = 'R'; // Initial direction
        score = 0;
        running = true;
        gameOver = false;

        for (int i = 0; i < snakeLength; i++) {
            x[i] = 50 - i * UNIT_SIZE;
            y[i] = 50;
        }

        placeFood();

        // Start the game thread
        if (gameThread == null || !gameThread.isAlive()) {
            gameThread = new Thread(this);
            gameThread.start();
        }
    }

    public void placeFood() {
        Random random = new Random();
        foodX = random.nextInt(BOARD_WIDTH / UNIT_SIZE) * UNIT_SIZE;
        foodY = random.nextInt(BOARD_HEIGHT / UNIT_SIZE) * UNIT_SIZE;
    }

    public void checkFoodCollision() {
        if (x[0] == foodX && y[0] == foodY) {
            snakeLength++;
            score += 10;
            placeFood();
        }
    }

    public void checkWallCollision() {
        if (x[0] < 0 || x[0] >= BOARD_WIDTH || y[0] < 0 || y[0] >= BOARD_HEIGHT) {
            running = false;
            gameOver = true;
        }
    }

    public void checkBodyCollision() {
        for (int i = 1; i < snakeLength; i++) {
            if (x[0] == x[i] && y[0] == y[i]) {
                running = false;
                gameOver = true;
            }
        }
    }

    public void move() {
        for (int i = snakeLength; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        switch (direction) {
            case 'R' -> x[0] += UNIT_SIZE;
            case 'L' -> x[0] -= UNIT_SIZE;
            case 'U' -> y[0] -= UNIT_SIZE;
            case 'D' -> y[0] += UNIT_SIZE;
        }
    }

    @Override
    public void run() {
        while (true) {
            if (running) {
                move();
                checkFoodCollision();
                checkWallCollision();
                checkBodyCollision();
                repaint();
            }

            try {
                Thread.sleep(100); // Game speed
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        if (running) {
            // Draw food
            g.setColor(Color.RED);
            g.fillOval(foodX, foodY, UNIT_SIZE, UNIT_SIZE);

            // Draw snake
            for (int i = 0; i < snakeLength; i++) {
                if (i == 0) {
                    g.setColor(Color.GREEN); // Snake's head
                } else {
                    g.setColor(Color.BLACK); // Snake's body
                }
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }

            // Draw score
            g.setColor(Color.BLUE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Score: " + score, 10, 50);
        } else if (gameOver) {
            // Game Over screen
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.drawString("Game Over", BOARD_WIDTH / 3, BOARD_HEIGHT / 3);

            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Press Enter to Restart", BOARD_WIDTH / 3, BOARD_HEIGHT / 2);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (running) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT -> {
                    if (direction != 'R') direction = 'L';
                }
                case KeyEvent.VK_RIGHT -> {
                    if (direction != 'L') direction = 'R';
                }
                case KeyEvent.VK_UP -> {
                    if (direction != 'D') direction = 'U';
                }
                case KeyEvent.VK_DOWN -> {
                    if (direction != 'U') direction = 'D';
                }
            }
        } else if (gameOver && e.getKeyCode() == KeyEvent.VK_ENTER) {
            // Restart the game
            startGame();
            repaint();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        new SnakeGameAWT();
    }
}
