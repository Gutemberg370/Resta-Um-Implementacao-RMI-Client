package application;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;

import static application.Main.TILE_SIZE;

import java.io.Serializable;

// Classe que representa uma peça do tabuleiro
public class Piece extends StackPane implements Serializable{
	
	private double mouseX, mouseY;
	private double oldX, oldY;
	private boolean canMove = true;
	
	public double getOldX() {
		return oldX;
	}
	
	public void setOldX(double x) {
		oldX = x;
	}
	
	
	public double getOldY() {
		return oldY;
	}
	
	public void setOldY(double y) {
		oldY = y;
	}
	
	
	public boolean getCanMove() {
		return canMove;
	}
	
	public void setCanMove(boolean ifCanMove) {
		canMove = ifCanMove;
	}
	
	public Piece(int x, int y) {
		
		move(x * TILE_SIZE, y * TILE_SIZE);
		
		Ellipse bg = new Ellipse(TILE_SIZE * 0.3125, TILE_SIZE * 0.26);
		bg.setFill(Color.BLACK);
		
		bg.setStroke(Color.BLACK);
		bg.setStrokeWidth(TILE_SIZE * 0.03);
		
		bg.setTranslateX((TILE_SIZE - TILE_SIZE * 0.3125 * 2) / 2);
		bg.setTranslateY((TILE_SIZE - TILE_SIZE * 0.3125 * 2) / 2 + TILE_SIZE * 0.07);
		
		
		Ellipse ellipse = new Ellipse(TILE_SIZE * 0.3125, TILE_SIZE * 0.26);
		ellipse.setFill(Color.GREEN);
		
		ellipse.setStroke(Color.BLACK);
		ellipse.setStrokeWidth(TILE_SIZE * 0.03);
		
		ellipse.setTranslateX((TILE_SIZE - TILE_SIZE * 0.3125 * 2) / 2);
		ellipse.setTranslateY((TILE_SIZE - TILE_SIZE * 0.3125 * 2) / 2);
		
		getChildren().addAll(bg, ellipse);
		
		setOnMousePressed(e -> {
			mouseX = e.getSceneX();
			mouseY = e.getSceneY();
		});
		
		setOnMouseDragged(e -> {
			if(canMove) {
			   relocate(e.getSceneX() - mouseX + oldX, e.getSceneY() - mouseY + oldY);
			}
		});
	}
	
	// Mover a peça para uma nova posição
	public void move(int x, int y) {
		oldX = x;
		oldY = y;
		
		relocate(oldX, oldY);
		
	}
	
	// Abortar movimento errado
	public void abortMove() {
		relocate(oldX,oldY);
	}

}
