package application;

import java.io.Serializable;

// Dados necessários para que um jogador possa registrar uma jogada realizada por outro jogador
public class MoveResultData implements Serializable{
	
	public int oldX;
	
	public int oldY;
	
	public int newX;
	
	public int newY;
	
	public int removedX;
	
	public int removedY;
	
	public MoveResultData(int oldX, int oldY, int newX,  int newY, int removedX, int removedY) {
		this.oldX = oldX;
		this.oldY = oldY;
		this.newX = newX;
		this.newY = newY;
		this.removedX = removedX;
		this.removedY = removedY;
		
	}
	
	
}
