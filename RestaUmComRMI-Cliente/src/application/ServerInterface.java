package application;

import java.rmi.Remote;
import java.rmi.RemoteException;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

// Interface do servidor
public interface ServerInterface extends Remote{
	
	public TypesOfChanges checkIfSomethingChanged(Player player) throws RemoteException;
		
	public Player registerPlayer(Player player) throws RemoteException;

	public Player getOponnent(Player player) throws RemoteException;
	
	public void writeMessage(Player player, String message) throws RemoteException;
	
	public String updateChat() throws RemoteException;
	
	public void movePiece(MoveResultData moveResultData, Player player) throws RemoteException;
	
	public MoveResultData updateBoard() throws RemoteException;
	
	public void confirmReset(Player player) throws RemoteException;
	
	public void resetGame() throws RemoteException;
	
	public GameResult checkIfgameEnded(Tile[][] mainBoard) throws RemoteException;
	
	public void endGame(GameResult gameResult, Player player) throws RemoteException;
	
	public GameResult updateGameResult(Player player) throws RemoteException;
	
	public Player getWinner() throws RemoteException;
	
	public Player getLoser() throws RemoteException;
	
}

