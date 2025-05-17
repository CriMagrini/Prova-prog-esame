package org.example.flappybird;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameController {
    @FXML private Pane gamePane;
    @FXML private ImageView bird;

    private double velocity = 0;
    private final double gravity = 0.4;
    private final double jumpStrength = -8;
    private final List<Rectangle> pipes = new ArrayList<>();
    private int score = 0;

    public void initialize() {
        spawnPipe();
        AnimationTimer timer = new AnimationTimer() {
            long lastPipe = 0;
            @Override
            public void handle(long now) {
                updateBird();
                updatePipes();
                checkCollisions();
                if (now - lastPipe > 2_000_000_000) {
                    spawnPipe();
                    lastPipe = now;
                }
            }
        };
        timer.start();
        gamePane.setOnKeyPressed(this::handleKeyPressed);
        gamePane.requestFocus();
    }

    private void handleKeyPressed(KeyEvent e) {
        switch (e.getCode()) {
            case SPACE -> velocity = jumpStrength;
        }
    }

    private void updateBird() {
        velocity += gravity;
        bird.setLayoutY(bird.getLayoutY() + velocity);
    }

    private void spawnPipe() {
        double height = 100 + Math.random() * 200;
        Rectangle topPipe = new Rectangle(60, height);
        Rectangle bottomPipe = new Rectangle(60, 400 - height - 150);
        topPipe.setLayoutX(600);
        topPipe.setLayoutY(0);
        bottomPipe.setLayoutX(600);
        bottomPipe.setLayoutY(height + 150);
        pipes.add(topPipe);
        pipes.add(bottomPipe);
        gamePane.getChildren().addAll(topPipe, bottomPipe);
    }

    private void updatePipes() {
        Iterator<Rectangle> it = pipes.iterator();
        while (it.hasNext()) {
            Rectangle pipe = it.next();
            pipe.setLayoutX(pipe.getLayoutX() - 5);
            if (pipe.getLayoutX() + pipe.getWidth() < 0) {
                gamePane.getChildren().remove(pipe);
                it.remove();
                score++;
            }
        }
    }

    private void checkCollisions() {
        for (Rectangle pipe : pipes) {
            if (bird.getBoundsInParent().intersects(pipe.getBoundsInParent())) {
                resetGame();
                return;
            }
        }

        if (bird.getLayoutY() < 0 || bird.getLayoutY() > 400) {
            resetGame();
        }
    }

    private void resetGame() {
        bird.setLayoutY(200);
        velocity = 0;
        for (Rectangle pipe : pipes) {
            gamePane.getChildren().remove(pipe);
        }
        pipes.clear();
        score = 0;
    }
}

