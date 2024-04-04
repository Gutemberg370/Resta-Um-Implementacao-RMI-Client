package application;

// Classe que representa uma ação (ação geral) feita por um jogador
public class Action {

	public boolean wasMade;
	public Player player;
	
	public Action(boolean wasMade, Player player) {
		this.wasMade = wasMade;
		this.player = player;
		
	}
	
}
