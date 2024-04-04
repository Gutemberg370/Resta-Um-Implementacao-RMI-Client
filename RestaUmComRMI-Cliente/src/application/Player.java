package application;

import java.io.Serializable;

// Classe que representa um jogador
public class Player implements Serializable{
	
	private String name;
	
	private int number;
	
	private boolean isTurn;
	
	private boolean winner;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getNumber() {
		return number;
	}
	
	public void setNumber(int number) {
		this.number = number;
	}
	
	public boolean getIsTurn() {
		return isTurn;
	}
	
	public void setIsTurn(boolean isTurn) {
		this.isTurn = isTurn;
	}
	
	public boolean getWinner() {
		return winner;
	}
	
	public void setWinner(boolean winner) {
		this.winner = winner;
	}
	
	public Player(String name, boolean isTurn, int number) {
		this.name = name;
		this.isTurn = isTurn;
		this.winner = false;
		this.number = number;
	}
	
	public Player() {
		this.winner = false;
	}

}
