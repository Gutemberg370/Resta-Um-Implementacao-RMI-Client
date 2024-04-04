package application;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import javafx.application.Platform;

// Classe que representa a conexão do jogador com o servidor
public class NetworkConnection extends Thread{
	
	public ServerInterface serverConnection;
	private Main main;
	private String serverUrl;
	
	public NetworkConnection(Main main) {
		this.main = main;
	}
	
	// Definir a url do servidor
	public void setServerUrl(String ip, String port, String name) {
		String url = String.format("rmi://%s:%s/%s", ip, port, name);
		this.serverUrl = url;
	}

	@Override
	public void run() {
		
		try {
			
			serverConnection = (ServerInterface) Naming.lookup(serverUrl);
			
			// Será constantemente checado no servidor se o adversário realizou alguma ação
			while(true) {
				
				TypesOfChanges result = serverConnection.checkIfSomethingChanged(this.main.player);
				
				switch (result) {
				
				// Sem alterações
    			case NOCHANGES:
    				break;
    				
    			// O oponente foi registrado
    			case PLAYERREGISTERED:
    				Player opponent = serverConnection.getOponnent(this.main.player);
    		        Runnable registerPlayerTask = () -> {
    		            Platform.runLater(() -> {
    		            	this.main.handleNewPlayer(opponent);	
    		            });
    		        };
    		        Thread registerPlayerThread = new Thread(registerPlayerTask);
    		        registerPlayerThread.setDaemon(true);
    		        registerPlayerThread.start();
    				break;
    				
    			// O oponente enviou uma mensagem
				case MESSAGESENT:
    		        Runnable chatMessageTask = () -> {
    		            Platform.runLater(() -> {
    		            	try {
								this.main.setChatText(serverConnection.updateChat());
							} catch (RemoteException e) {
								e.printStackTrace();
							}	
    		            });
    		        };
    		        Thread chatMessageThread = new Thread(chatMessageTask);
    		        chatMessageThread.setDaemon(true);
    		        chatMessageThread.start();
					break;
					
				// O oponente realizou um movimento
				case PLAYERMOVED:
    		        Runnable moveMadeTask = () -> {
    		            Platform.runLater(() -> {
    		            	try {
    		            		this.main.updateBoard(serverConnection.updateBoard());
							} catch (RemoteException e) {
								e.printStackTrace();
							}	
    		            });
    		        };
    		        Thread moveMadeThread = new Thread(moveMadeTask);
    		        moveMadeThread.setDaemon(true);
    		        moveMadeThread.start();
					break;
					
				// O oponente resetou o tabuleiro
				case GAMERESETED:
    		        Runnable gameResetedTask = () -> {
    		            Platform.runLater(() -> {
    		            	try {
								serverConnection.resetGame();
								this.main.resetGame();
							} catch (RemoteException e) {
								e.printStackTrace();
							}	
    		            });
    		        };
    		        Thread gameResetedThread = new Thread(gameResetedTask);
    		        gameResetedThread.setDaemon(true);
    		        gameResetedThread.start();
					break;
					
				// O oponente finalizou o jogo
				case GAMEENDED:
    		        Runnable gameEndedTask = () -> {
    		            Platform.runLater(() -> {
    		            	try {
								this.main.onGameEnded(serverConnection.updateGameResult(this.main.player));
							} catch (RemoteException e) {
								e.printStackTrace();
							}	
    		            });
    		        };
    		        Thread gameEndedThread = new Thread(gameEndedTask);
    		        gameEndedThread.setDaemon(true);
    		        gameEndedThread.start();
					break;
					
				default:
					break;
				}
				
				try{Thread.sleep(250);}catch(InterruptedException e){System.out.println(e);}
			}
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			e.printStackTrace();
		}	
	}
	
}
