package application;

import javafx.scene.paint.Color;

import java.io.Serializable;
import java.lang.Math;
import java.rmi.RemoteException;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.layout.VBox;


public class Main extends Application{
	
	public static final int TILE_SIZE = 64;
	public static final int WIDTH = 17;
    public static final int HEIGHT = 11;    
    
    private Group tileGroup = new Group();
    public Group pieceGroup = new Group();
    public Tile[][] mainBoard = new Tile[WIDTH][HEIGHT];
    public Player player = new Player();
    private Label player1 = new Label("Jogador 1");
    private Label player2 = new Label("Jogador 2");
    private TextArea chat = new TextArea();
    private Text endGameText = new Text();
    private ImageView winImage1 = new ImageView(getClass().getResource("win.png").toExternalForm());
    private ImageView winImage2 = new ImageView(getClass().getResource("win.png").toExternalForm());
    private Button withdrawalButton = new Button("Desistir");
    private Button resetButton = new Button("Resetar");
    
    private NetworkConnection connection;
    
    
    // Criar página de login
    private Parent createLogin() {
    	Pane root = new Pane();
    	
    	BackgroundFill backgroundFill = new BackgroundFill(Color.valueOf("#E27D60"), new CornerRadii(10), new Insets(10));

    	Background background = new Background(backgroundFill);
    	
    	root.setBackground(background);
    	
    	root.setPrefSize(WIDTH * 0.52 * TILE_SIZE, HEIGHT * 0.63 * TILE_SIZE);
    	
    	Label gameName = new Label("RESTA UM");
    	gameName.setFont(new Font("Monaco",36));
    	gameName.setLayoutX(200);
    	gameName.setLayoutY(40);
    	
    	Label title = new Label("Insira o nome do seu jogador, o ip do servidor a se conectar, \n o nome do servidor e a porta. Então clique no botão abaixo \n para iniciar o jogo.");
    	title.setFont(new Font("Arial",18));
    	title.setLayoutX(45);
    	title.setLayoutY(120);
    	title.setTextAlignment(TextAlignment.CENTER);
    	
    	Label name = new Label("Nome Jogador :");
    	name.setFont(new Font("Arial",13));
    	name.setLayoutX(95);
    	name.setLayoutY(225);
    	
    	TextField nameInput = new TextField();
    	nameInput.setLayoutX(195);
    	nameInput.setLayoutY(220);
    	nameInput.setMinWidth(220);
    	
    	Label serverIp = new Label("Ip do Servidor  :");
    	serverIp.setFont(new Font("Arial",13));
    	serverIp.setLayoutX(95);
    	serverIp.setLayoutY(265);
    	
    	TextField serverIpInput = new TextField("192.168.0.14");
    	serverIpInput.setLayoutX(195);
    	serverIpInput.setLayoutY(260);
    	serverIpInput.setMinWidth(220);
    	
    	Label serverName = new Label("Nome do Servidor :");
    	serverName.setFont(new Font("Arial",13));
    	serverName.setLayoutX(75);
    	serverName.setLayoutY(305);
    	
    	TextField serverNameInput = new TextField("Server");
    	serverNameInput.setLayoutX(195);
    	serverNameInput.setLayoutY(300);
    	serverNameInput.setMinWidth(220);
    	
    	Label serverPort = new Label("Porta do Servidor :");
    	serverPort.setFont(new Font("Arial",13));
    	serverPort.setLayoutX(75);
    	serverPort.setLayoutY(345);
    	
    	TextField serverPortInput = new TextField("6000");
    	serverPortInput.setLayoutX(195);
    	serverPortInput.setLayoutY(340);
    	serverPortInput.setMinWidth(220);
    	
    	Button loginButton = new Button("Iniciar Jogo");
    	loginButton.setLayoutX(210);
    	loginButton.setLayoutY(390);
    	loginButton.setMinWidth(150);
    	loginButton.setOnAction(event -> {
    		// Após o login, se inicia a conexão com o servidor
    		this.connection = new NetworkConnection(this);
    		this.connection.setServerUrl(serverIpInput.getText(), serverPortInput.getText(), serverNameInput.getText());
    		this.connection.start();
    		this.player.setName(nameInput.getText());
    		
    		try {
    			// Esperar a conexão terminar de ser iniciada antes de chamar alguma função do servidor
				Thread.sleep(500);
			} catch (InterruptedException e) {
				chat.appendText("A conexão não pode ser estabelecida.\n");
			}
        	try {
        		// Registrar o jogador no servidor
				this.player = this.connection.serverConnection.registerPlayer(this.player);
			} catch (Exception e) {
				chat.appendText("A conexão não pode ser estabelecida.\n");
			}
        	
        	
    		if(player.getNumber() == 1) {
    			this.player1.setText(this.player.getName());
    		}
    		else {
    			this.player2.setText(this.player.getName());
    		}
        	Stage window = (Stage)loginButton.getScene().getWindow();
        	Scene scene = new Scene(createContent());
        	scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        	window.setScene(scene);
        	window.setResizable(false);
        });
    	
    	root.getChildren().addAll(gameName, title, name, nameInput, serverIp, serverIpInput, serverName, serverNameInput,
    			serverPort, serverPortInput, loginButton);
    	
    	return root;
    }
    
    // Criar tela do jogo
    private Parent createContent() {
    	Pane root = new Pane();
    	
    	// Criar a interface do tabuleiro
    	this.endGameText.setFont(new Font("Roboto",30));        
    	this.endGameText.setWrappingWidth(1482);
    	this.endGameText.setY(70);      
    	this.endGameText.setTextAlignment(TextAlignment.CENTER);
              
    	root.setPrefSize(WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);
    	root.getChildren().addAll(this.tileGroup,this.pieceGroup, this.endGameText);
        createBoard(this.tileGroup, this.pieceGroup,  this.mainBoard);
        
        // Regra: o primeiro jogador a se registrar é o primeiro a jogar
        if(player.getNumber() == 2) {
        	setPiecesMovement(false);
        }
        
        
        // Criar a interface dos jogadores
        ImageView userImage1 = new ImageView(getClass().getResource("user.png").toExternalForm());
        ImageView userImage2 = new ImageView(getClass().getResource("user.png").toExternalForm());
        this.player1.setFont(new Font("Arial",30));
        this.player1.setLayoutX(80);
        this.player1.setLayoutY(64);
        this.player1.setGraphic(userImage1);
        this.player1.setGraphicTextGap(20);
        
        this.player2.setFont(new Font("Arial",30));
        this.player2.setLayoutX(80);
        this.player2.setLayoutY(150);
        this.player2.setGraphicTextGap(20);
        this.player2.setGraphic(userImage2);
        
        this.winImage1.setLayoutX(10);
        this.winImage1.setLayoutY(50);
        
        this.winImage2.setLayoutX(10);
        this.winImage2.setLayoutY(135);;
        
        this.winImage1.setVisible(false);
        this.winImage2.setVisible(false);
        
        root.getChildren().addAll(player1,player2, winImage1, winImage2);
        
        // Criar o botão de resetar o jogo
        this.resetButton.setLayoutX(50);
        this.resetButton.setLayoutY(TILE_SIZE * 5);
        this.resetButton.setMinWidth(TILE_SIZE * 2);
        this.resetButton.setOnAction(event -> {
        	resetGame();
            try {
            	// Avisar ao servidor que o jogador em questão resetou o tabuleiro
    			connection.serverConnection.confirmReset(player);
    		} catch (Exception e) {
    			chat.appendText("Não foi possível resetar o tabuleiro.\n");
    		}
        });
        
        // Criar o botão de desistir do jogo
        this.withdrawalButton.setLayoutX(210);
        this.withdrawalButton.setLayoutY(TILE_SIZE * 5);
        this.withdrawalButton.setMinWidth(TILE_SIZE * 2);
        this.withdrawalButton.setOnAction(event -> {
        	try {
        		// Avisar ao servidor que o jogador em questão desistiu do jogo
				connection.serverConnection.endGame(GameResult.PLAYERGAVEUP, player);
			} catch (Exception e) {
				chat.appendText("Não foi possível realizar a ação de desistir.\n");
			}
        	onGameEnded(GameResult.PLAYERGAVEUP);
        });
        
        
        // Criar a área do chat		
        this.chat.setPrefHeight(TILE_SIZE * 5);
        this.chat.setPrefWidth(TILE_SIZE * 6);
        this.chat.setEditable(false);
        TextField input = new TextField();
        input.setOnAction(event -> {
        	String message = this.player.getNumber() == 1 ? this.player1.getText() + ": " : this.player2.getText() + ": ";
        	message += input.getText();
        	input.clear();
        	
        	this.chat.appendText(message + "\n");
        	
        	try {
        		// Avisar ao servidor que o jogador em questão escreveu uma mensagem
				connection.serverConnection.writeMessage(player, message + "\n");
			} catch (Exception e) {
				chat.appendText("A mensagem não pode ser enviada.\n");
			}
        });
     		
        VBox chatBox = new VBox(0, this.chat, input);
        chatBox.setLayoutY(360);
     
        root.getChildren().addAll(chatBox,withdrawalButton,resetButton);
        
    	return root;
    }
	
    // Criar a interface do tabuleiro e o background da tela de jogo inteira
    public void createBoard(Group tileGroup, Group pieceGroup,  Tile[][] board) {
		
    	/////// INTERFACE DE USUÁRIO ///////////
    	
    	// Criar os ladrilhos para a interface do usuário
		for(int y = 0; y < HEIGHT; y++) {
			for(int x = 0; x < 6; x++ ) {
				UserTile tile = new UserTile(x , y);
				tileGroup.getChildren().add(tile);
			}
		}
    	
    	
    	/////// INTERFACE DO TABULEIRO ///////////
    	
    	
		//Primeiras duas linhas
		for(int y = 0; y < 2; y++) {
			for(int x = 6; x < Main.WIDTH; x++ ) {
				Tile tile = new Tile(false, x , y);
				board[x][y] = tile;
				tileGroup.getChildren().add(tile);
			}
		}
		
		//Terceira e quarta linhas
		for(int y = 2; y < 4; y++) {
			for(int x = 6; x < Main.WIDTH; x++ ) {
				Tile tile = new Tile( x > 9 && x < 13 ? true : false, x , y);
				board[x][y] = tile;
				tileGroup.getChildren().add(tile);
				
				if(x > 9 && x < 13) {
					Piece piece = makePiece(x , y);
					board[x][y].setPiece(piece);
					pieceGroup.getChildren().add(piece);
				}
			}
		}
		
		//Da quinta até a sétima linhas
		for(int y = 4; y < 7; y++) {
			for(int x = 6; x < Main.WIDTH; x++ ) {
				Tile tile = new Tile( x > 7 && x < 15 ? true : false, x , y);
				board[x][y] = tile;
				tileGroup.getChildren().add(tile);
				
				if(x > 7 && x < 15 && !(x == 11 && y == 5)) {
					Piece piece = makePiece(x , y);
					board[x][y].setPiece(piece);
					pieceGroup.getChildren().add(piece);
				}
				
			}
		
		}
		
		//Oitava e nona linhas
		for(int y = 7; y < 9; y++) {
			for(int x = 6; x < Main.WIDTH; x++ ) {
				Tile tile = new Tile( x > 9 && x < 13 ? true : false, x , y);
				board[x][y] = tile;
				tileGroup.getChildren().add(tile);
				
				if(x > 9 && x < 13) {
					Piece piece = makePiece(x , y);
					board[x][y].setPiece(piece);
					pieceGroup.getChildren().add(piece);
				}
			}
		}
		
		//Duas últimas linhas
		for(int y = 9; y < 11; y++) {
			for(int x = 6; x < Main.WIDTH; x++ ) {
				Tile tile = new Tile(false, x , y);
				board[x][y] = tile;
				tileGroup.getChildren().add(tile);
			}
		}
		
	}
    
    // Verificar se é possível mover a peça para (newX,newY)
    private MoveResult tryMove(Piece piece, int newX, int newY) {
    	
    	//Se eu tentar mover a peça para fora do jogo
    	if( newX < 6 || newX > 16 || newY < 0 || newY > 10) {
    		return new MoveResult(MoveType.NONE);
    	}
    	
    	//Se eu tentar mover a peça para um lugar fora do tabuleiro ou para um lugar com peça 
    	if(!(mainBoard[newX][newY].getIsPartOfBoard()) || mainBoard[newX][newY].hasPiece()) {
    		return new MoveResult(MoveType.NONE);
    	}
    	
    	int x0 = toBoard(piece.getOldX());
    	int y0 = toBoard(piece.getOldY());
    	
    	//Se eu tentar mover para muito longe
    	if((Math.abs(newX - x0)  +  Math.abs(newY - y0)) != 2) {
    		return new MoveResult(MoveType.NONE);
    	}

    	
    	// Se eu tentar mover para a direita
    	if(newX - x0 == 2 && mainBoard[newX-1][newY].hasPiece()) {
    		return new MoveResult(MoveType.RIGHT, mainBoard[newX-1][newY].getPiece());
    	}
    	
    	
    	// Se eu tentar mover para a esquerda
    	if(newX - x0 == -2 && mainBoard[newX+1][newY].hasPiece()) {
    		return new MoveResult(MoveType.LEFT, mainBoard[newX+1][newY].getPiece());
    	}
    	
    	// Se eu tentar mover para baixo
    	if(newY - y0 == 2 && mainBoard[newX][newY-1].hasPiece()) {
    		return new MoveResult(MoveType.DOWN, mainBoard[newX][newY-1].getPiece());
    	}
    	
    	// Se eu tentar mover para cima
    	if(newY - y0 == -2 && mainBoard[newX][newY+1].hasPiece()) {
    		return new MoveResult(MoveType.UP, mainBoard[newX][newY+1].getPiece());
    	}
    	
    	return new MoveResult(MoveType.NONE);
    }
    
    // Converter o valor de pixel para posição na matriz do tabuleiro
    private int toBoard(double pixel) {
    	return (int)(pixel + TILE_SIZE / 2) / TILE_SIZE;
    }
    
    // Criar/Mover peça de acordo com o input do usuário
    private Piece makePiece(int x, int y) {
    	Piece piece = new Piece(x ,y);
    	
    	piece.setOnMouseReleased(e -> {
    		int newX = toBoard(piece.getLayoutX());
    		int newY = toBoard(piece.getLayoutY());
    		
    		MoveResult result = tryMove(piece, newX, newY);
    		
    		int x0 = toBoard(piece.getOldX());
    		int y0 = toBoard(piece.getOldY());
    		
    		switch (result.getType()) {
    		
    			case NONE:
    				piece.abortMove();
    				break;
    				
    			case LEFT:
    				piece.move(newX * TILE_SIZE, newY * TILE_SIZE);
    				this.mainBoard[newX][newY].setPiece(piece);
    				this.mainBoard[x0][y0].setPiece(null);
    				this.mainBoard[newX+1][newY].setPiece(null);
    				this.pieceGroup.getChildren().remove(result.getPiece());
    				this.player.setIsTurn(false);
    				setPiecesMovement(this.player.getIsTurn());
    				MoveResultData moveLeftResultData = new MoveResultData(x0, y0, newX, newY,newX+1,newY);
					try {
						// Avisar ao servidor que o jogador em questão realizou um movimento
						this.connection.serverConnection.movePiece(moveLeftResultData,this.player);
					} catch (Exception e1) {
						chat.appendText("O movimento para a esquerda não foi registrado.\n");
					}
					try {
						// Verificar se o jogo terminou
						GameResult gameResult = this.connection.serverConnection.checkIfgameEnded(this.mainBoard);
						if(!gameResult.name().equalsIgnoreCase("STILLAVALIABLEMOVES")) {
							if(gameResult.name().equalsIgnoreCase("NOPIECESLEFT")) {
								this.player.setWinner(true);
							}
							else {
								this.player.setWinner(false);
							}
							// Finalizar o jogo
							this.connection.serverConnection.endGame(gameResult, this.player);
							onGameEnded(gameResult);
						}
					} catch (Exception e1) {
						chat.appendText("Não foi possível enviar o resultado do jogo. \n");
					}
	    			break;
	    				
    			case RIGHT:
    				piece.move(newX * TILE_SIZE, newY * TILE_SIZE);
    				this.mainBoard[newX][newY].setPiece(piece);
    				this.mainBoard[x0][y0].setPiece(null);
    				this.mainBoard[newX-1][newY].setPiece(null);
    				this.pieceGroup.getChildren().remove(result.getPiece());
    				this.player.setIsTurn(false);
    				setPiecesMovement(this.player.getIsTurn());
    				MoveResultData moveRightResultData = new MoveResultData(x0, y0, newX, newY,newX-1,newY);
					try {
						// Avisar ao servidor que o jogador em questão realizou um movimento
						this.connection.serverConnection.movePiece(moveRightResultData,this.player);
					} catch (Exception e1) {
						chat.appendText("O movimento para a direita não foi registrado.\n");
					}
					try {
						// Verificar se o jogo terminou
						GameResult gameResult = this.connection.serverConnection.checkIfgameEnded(this.mainBoard);
						if(!gameResult.name().equalsIgnoreCase("STILLAVALIABLEMOVES")) {
							if(gameResult.name().equalsIgnoreCase("NOPIECESLEFT")) {
								this.player.setWinner(true);
							}
							else {
								this.player.setWinner(false);
							}	
							// Finalizar o jogo
							this.connection.serverConnection.endGame(gameResult, this.player);
							onGameEnded(gameResult);
						}
					} catch (Exception e1) {
						chat.appendText("Não foi possível enviar o resultado do jogo. \n");
					}
    				break;
    				
    			case DOWN:
    				piece.move(newX * TILE_SIZE, newY * TILE_SIZE);
    				this.mainBoard[newX][newY].setPiece(piece);
    				this.mainBoard[x0][y0].setPiece(null);
    				this.mainBoard[newX][newY-1].setPiece(null);
    				this.pieceGroup.getChildren().remove(result.getPiece());
    				this.player.setIsTurn(false);
    				setPiecesMovement(this.player.getIsTurn());
    				MoveResultData moveDownResultData = new MoveResultData(x0, y0, newX, newY,newX,newY-1);
					try {
						// Avisar ao servidor que o jogador em questão realizou um movimento
						this.connection.serverConnection.movePiece(moveDownResultData,this.player);
					} catch (Exception e1) {
						chat.appendText("O movimento para baixo não foi registrado. \n");
					}
					try {
						// Verificar se o jogo terminou
						GameResult gameResult = this.connection.serverConnection.checkIfgameEnded(this.mainBoard);
						if(!gameResult.name().equalsIgnoreCase("STILLAVALIABLEMOVES")) {
							if(gameResult.name().equalsIgnoreCase("NOPIECESLEFT")) {
								this.player.setWinner(true);
							}
							else {
								this.player.setWinner(false);
							}	
							// Finalizar o jogo
							this.connection.serverConnection.endGame(gameResult, this.player);
							onGameEnded(gameResult);
						}
					} catch (Exception e1) {
						chat.appendText("Não foi possível enviar o resultado do jogo. \n");
					}
    				break;
    				
    			case UP:
    				piece.move(newX * TILE_SIZE, newY * TILE_SIZE);
    				this.mainBoard[newX][newY].setPiece(piece);
    				this.mainBoard[x0][y0].setPiece(null);
    				this.mainBoard[newX][newY+1].setPiece(null);
    				this.pieceGroup.getChildren().remove(result.getPiece());
    				this.player.setIsTurn(false);
    				setPiecesMovement(this.player.getIsTurn());
    				MoveResultData moveUpResultData = new MoveResultData(x0, y0, newX, newY,newX,newY+1);
					try {
						// Avisar ao servidor que o jogador em questão realizou um movimento
						this.connection.serverConnection.movePiece(moveUpResultData,this.player);
					} catch (Exception e1) {
						chat.appendText("O movimento para cima não foi registrado. \n");
					}
					try {
						// Verificar se o jogo terminou
						GameResult gameResult = this.connection.serverConnection.checkIfgameEnded(this.mainBoard);
						if(!gameResult.name().equalsIgnoreCase("STILLAVALIABLEMOVES")) {
							if(gameResult.name().equalsIgnoreCase("NOPIECESLEFT")) {
								this.player.setWinner(true);
							}
							else {
								this.player.setWinner(false);
							}	
							// Finalizar o jogo
							this.connection.serverConnection.endGame(gameResult, this.player);
							onGameEnded(gameResult);
						}
					} catch (Exception e1) {
						chat.appendText("Não foi possível enviar o resultado do jogo. \n");
					}
    				break;
    		}
    	});
    	    	
    	return piece;
    }
    
    // Habilitar/Desabilitar movimentação das peças do tabuleiro
    public void setPiecesMovement(boolean canMove) {
    	for(int y = 2; y < HEIGHT - 2; y++) {
    		for(int x = 8; x < WIDTH - 2; x++) {
    			if(mainBoard[x][y] != null && mainBoard[x][y].hasPiece()) {
    				this.mainBoard[x][y].getPiece().setCanMove(canMove);
    			}
    		}
    	}
    }
    
    // Resetar todo o jogo
    public void resetGame() {
    	this.tileGroup.getChildren().clear();
    	this.pieceGroup.getChildren().clear();
    	createBoard(this.tileGroup, this.pieceGroup, this.mainBoard);
    	setPiecesMovement(this.player.getIsTurn());
    	this.player.setWinner(false);
    	this.winImage1.setVisible(false);
    	this.winImage2.setVisible(false);
    	this.withdrawalButton.setDisable(false);
    	this.endGameText.setText("");
    }
    
    // Ações a serem realizadas no final do jogo
    public void onGameEnded(GameResult gameResult) {
    	
		try {
						
			Player winner = this.connection.serverConnection.getWinner();
			Player loser = this.connection.serverConnection.getLoser();
			
	    	if(gameResult.name().equalsIgnoreCase("NOPIECESLEFT")) {
	    		this.endGameText.setText("Fim do jogo! \n Não há mais peças disponíveis para mover.");
	    	}
	    	
	    	if(gameResult.name().equalsIgnoreCase("NOMOVESLEFT")) {
	    		this.endGameText.setText("Fim do jogo! \n Não há mais movimentos disponíveis.");
	    	}
	    	
	    	if(gameResult.name().equalsIgnoreCase("PLAYERGAVEUP")) {
	    		String endText = String.format("Fim do jogo! \n Jogador %s desistiu!", loser.getName());
	    		this.endGameText.setText(endText);
	    	}
	    	
	    	this.withdrawalButton.setDisable(true);
	    	setPiecesMovement(false);
			if(winner.getNumber() == 1) {
				this.winImage1.setVisible(true);
			}
			else {
				this.winImage2.setVisible(true);
			}
		} catch(Exception e) {
			chat.appendText("Houve um erro que impediu o jogo de ser finalizado. \n");
		}
    	
    }
    
    // Registrar o novo oponente
    public void handleNewPlayer(Player opponent) {
    	if(opponent.getNumber() == 1) {
    		this.player1.setText(opponent.getName());
    	}
    	else {
    		this.player2.setText(opponent.getName());
    	}
    }
    
    // Atualizar o chat
    public void setChatText(String message) {
    	this.chat.setText(message);
    }
    
    // Atualizar o tabuleiro com a jogada do oponente
    public void updateBoard(MoveResultData moveResultData) {
    	this.mainBoard[moveResultData.oldX][moveResultData.oldY].getPiece().move(moveResultData.newX * TILE_SIZE, moveResultData.newY * TILE_SIZE);
        this.mainBoard[moveResultData.newX][moveResultData.newY].setPiece(this.mainBoard[moveResultData.oldX][moveResultData.oldY].getPiece());
        this.mainBoard[moveResultData.oldX][moveResultData.oldY].setPiece(null);
        this.pieceGroup.getChildren().remove(this.mainBoard[moveResultData.removedX][moveResultData.removedY].getPiece());
        this.mainBoard[moveResultData.removedX][moveResultData.removedY].setPiece(null);
    	this.player.setIsTurn(true);
    	setPiecesMovement(true);
    }
    	
	@Override
	public void start(Stage primaryStage) {
		try {
			Scene loginScene = new Scene(createLogin());
			primaryStage.setTitle("Resta Um");;
			primaryStage.setScene(loginScene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {		
		launch(args);
	}
}
