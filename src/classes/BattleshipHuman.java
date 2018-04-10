package classes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import network.Client;
import network.NetworkConnection;
import network.Server;

public class BattleshipHuman {
	//private Parent root;
	private Stage stage;
	private GridPane grid = new GridPane();
	private boolean debugEnabled = true;
	
	private boolean yourTurn;
	private ImageView[][] fieldImageTarget = new ImageView[10][10];
	private ImageView[][] fieldImageLocation = new ImageView[10][10];
	private State[][] fieldStateLocation = new State[10][10];
	private State[][] fieldStateTarget = new State[10][10];
	private int tileHit = 0;
	private int otherTileHit = 0;
	
	private boolean isServer = false;
	private boolean isConnected = false;
	private int port = 0;
	private String ip;
	private NetworkConnection connection;
	
	// data is a consumer that runs an event each time it is called. It is on another thread so it won't interfere
	// with the main thread
	private Server createServer() {
		return new Server(port, data -> {
			Platform.runLater(() -> {
				DEBUGPRINT("Received: " + data);
				// If the data is an array, it is either cords or the other players states
				if(data.getClass().isArray()) {
					if(data.getClass().getComponentType().equals(int.class)) {
						// Process the cords the other user attacked
						int[] cords = (int[]) data;
						DEBUGPRINT("Received cords (" + cords[0] + "," + cords[1] + ")");
						if(fieldStateLocation[cords[0]][cords[1]].equals(State.EMPTY)) {
							fieldStateLocation[cords[0]][cords[1]] = State.EMPTYHIT;
							fieldImageLocation[cords[0]][cords[1]].setImage(new Image(ImagePath.TILE_EMPTY_HIT.toText()));
						} else if (fieldStateLocation[cords[0]][cords[1]].equals(State.SHIP)) {
							fieldStateLocation[cords[0]][cords[1]] = State.SHIPHIT;
							fieldImageLocation[cords[0]][cords[1]].setImage(new Image(ImagePath.TILE_SHIP_HIT.toText()));
						}
					// Receives states from the enemy so the client knows if they hit their ship
					} else if(data.getClass().getComponentType().equals(State[].class)) {
						State[][] enemyLocations = (State[][]) data;
						for(int i = 0; i < enemyLocations.length; i++) {
							for(int j = 0; j < enemyLocations[0].length; j++) {
								fieldStateTarget[i][j] = enemyLocations[i][j];
							}
						}
					}
				} else {
					// If the enemy hit your tile, keep count
					if(data.toString().contains("CLIENTHITTILE")) {
						String data2 = data.toString().replaceAll("CLIENTHITTILE", "");
						otherTileHit = Integer.parseInt(data2);
					} else {
						switch (data.toString()) {
							// Sets the tile so the user knows who turn it is
							case "ENDTURN":
								yourTurn = true;
								stage.setTitle((isServer ? "Server: " : "Client: ") + "Your turn");
								DEBUGPRINT("My turn");
								break;
							// Once connection is established between two users, ititalize the field and allow the
							// game to start
							case "CONNECTIONESTABLISHED":
								isConnected = true;
								for(int i = 0; i < fieldStateLocation.length; i++) {
									for(int j = 0; j < fieldStateLocation[0].length; j++) {
										if(fieldStateLocation[i][j].equals(State.EMPTY)) {
											fieldImageLocation[i][j].setImage(new Image(ImagePath.TILE_EMPTY.toText()));
										} else if(fieldStateLocation[i][j].equals(State.SHIP)) {
											fieldImageLocation[i][j].setImage(new Image(ImagePath.TILE_SHIP.toText()));
										}
										
										if(fieldStateTarget[i][j].equals(State.EMPTY)) {
											fieldImageTarget[i][j].setImage(new Image(ImagePath.TILE_EMPTY.toText()));
										} else if(fieldStateTarget[i][j].equals(State.SHIP)) {
											fieldImageTarget[i][j].setImage(new Image(ImagePath.TILE_SHIP.toText()));
										}
									}
								}
								// Sends the states to the other user
								try {
									connection.send(fieldStateLocation);
								} catch (Exception e) {
									e.printStackTrace();
								}
								break;
							// If the server won (which is you), you win
							case "SERVERWON":
								if(!stage.getTitle().equals("Server: You won")) {
									try {
										connection.send("SERVERWON");
										stage.setTitle("Server: You won");
										yourTurn = false;
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
								break;
							// If the server lost (which is you), you win
							// Since the game is over, show the locations of ships you missed
							case "CLIENTWON":
								if(!stage.getTitle().equals("Server: You lost")) {
									try {
										connection.send("CLIENTWON");
										stage.setTitle("Server: You lost");
										yourTurn = false;
										for(int i = 0; i < fieldImageTarget.length; i++) {
											for(int j = 0; j < fieldImageTarget[0].length; j++) {
												if(fieldStateTarget[i][j].equals(State.SHIP)) {
													fieldImageTarget[i][j].setImage(new Image(ImagePath.TILE_SHIP.toText()));
												}
											}
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
								break;
						}
					}
				}
			});
		});
	}
	
	private Client createClient() {
		return new Client(ip, port, data -> {
			Platform.runLater(() -> {
				DEBUGPRINT("Received: " + data);
				if(data.getClass().isArray()) {
					if(data.getClass().getComponentType().equals(int.class)) {
						int[] cords = (int[]) data;
						DEBUGPRINT("Received cords (" + cords[0] + "," + cords[1] + ")");
						//processCords(cords[0], cords[1]);
						if(fieldStateLocation[cords[0]][cords[1]].equals(State.EMPTY)) {
							fieldStateLocation[cords[0]][cords[1]] = State.EMPTYHIT;
							fieldImageLocation[cords[0]][cords[1]].setImage(new Image(ImagePath.TILE_EMPTY_HIT.toText()));
						} else if (fieldStateLocation[cords[0]][cords[1]].equals(State.SHIP)) {
							fieldStateLocation[cords[0]][cords[1]] = State.SHIPHIT;
							fieldImageLocation[cords[0]][cords[1]].setImage(new Image(ImagePath.TILE_SHIP_HIT.toText()));
						}
					} else if(data.getClass().getComponentType().equals(State[].class)) {
						State[][] enemyLocations = (State[][]) data;
						for(int i = 0; i < enemyLocations.length; i++) {
							for(int j = 0; j < enemyLocations[0].length; j++) {
								fieldStateTarget[i][j] = enemyLocations[i][j];
							}
						}
					}
				} else {
					if(data.toString().contains("CLIENTHITTILE")) {
						String data2 = data.toString().replaceAll("CLIENTHITTILE", "");
						otherTileHit = Integer.parseInt(data2);
					} else {
						switch (data.toString()) {
							case "ENDTURN":
								yourTurn = true;
								stage.setTitle((isServer ? "Server: " : "Client: ") + "Your turn");
								DEBUGPRINT("My turn");
								break;
							case "CONNECTIONESTABLISHED":
								isConnected = true;
								for(int i = 0; i < fieldStateLocation.length; i++) {
									for(int j = 0; j < fieldStateLocation[0].length; j++) {
										if(fieldStateLocation[i][j].equals(State.EMPTY)) {
											fieldImageLocation[i][j].setImage(new Image(ImagePath.TILE_EMPTY.toText()));
										} else if(fieldStateLocation[i][j].equals(State.SHIP)) {
											fieldImageLocation[i][j].setImage(new Image(ImagePath.TILE_SHIP.toText()));
										}
										
										if(fieldStateTarget[i][j].equals(State.EMPTY)) {
											fieldImageTarget[i][j].setImage(new Image(ImagePath.TILE_EMPTY.toText()));
										} else if(fieldStateTarget[i][j].equals(State.SHIP)) {
											fieldImageTarget[i][j].setImage(new Image(ImagePath.TILE_SHIP.toText()));
										}
									}
								}
								try {
									connection.send(fieldStateLocation);
								} catch (Exception e) {
									e.printStackTrace();
								}
								break;
							case "SERVERWON":
								if(!stage.getTitle().equals("Client: You lost")) {
									try {
										connection.send("SERVERWON");
										stage.setTitle("Client: You lost");
										yourTurn = false;
										for(int i = 0; i < fieldImageTarget.length; i++) {
											for(int j = 0; j < fieldImageTarget[0].length; j++) {
												if(fieldStateTarget[i][j].equals(State.SHIP)) {
													fieldImageTarget[i][j].setImage(new Image(ImagePath.TILE_SHIP.toText()));
												}
											}
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
								break;
							case "CLIENTWON":
								if(!stage.getTitle().equals("Client: You won")) {
									try {
										connection.send("CLIENTWON");
										stage.setTitle("Client: You won");
										yourTurn = false;
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
								break;
						}
					}
				}
			});
		});
	}
	
	public BattleshipHuman() throws Exception {
		createWindow();
	}
	
	private void createWindow() throws Exception {
			stage = new Stage();
			stage.setTitle("Battleship");
			Scene scene = new Scene(grid);
			
			// Close the connection at the end to avoid blocking ports
			stage.setOnCloseRequest((event) -> {
				try {
					connection.closeConnection();
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			
			// Figure out weather to run as a client or server
			List<String> modeChoices = new ArrayList<>();
			modeChoices.add("Client");
			modeChoices.add("Server");
			
			ChoiceDialog<String> choiceDialog = new ChoiceDialog<>("Client", modeChoices);
			choiceDialog.setTitle("Run as...");
			choiceDialog.setHeaderText("Would you like to run as the client or server?");
			choiceDialog.setContentText("Choice:");
			
			// Process result
			Optional<String> choiceResult = choiceDialog.showAndWait();
			choiceResult.ifPresent(letter -> {
				if(choiceResult.get().equals("Client")) {
					isServer = false;
				} else if(choiceResult.get().equals("Server")) {
					isServer = true;
				}
			});
			
			// Server always goes first. Set the variable that dictates that
			yourTurn = isServer ? true : false;
			if(yourTurn) {
				stage.setTitle((isServer ? "Server: " : "Client: ") + "Your turn");
			} else {
				stage.setTitle((isServer ? "Server: " : "Client: ") + "Opponents turn");
			}
			
			// Ask user for the port
			TextInputDialog portDialog = new TextInputDialog("1234");
			portDialog.setTitle("Port");
			portDialog.setHeaderText("Please enter the port to use");
			portDialog.setContentText("Port:");
			
			// Get result and set variable
			Optional<String> portResult = portDialog.showAndWait();
			portResult.ifPresent(letter -> {
				port = Integer.parseInt(letter);
			});
			
			// If you are the client, ask for the ip address
			if(!isServer) {
				TextInputDialog ipDialog = new TextInputDialog("127.0.0.1");
				ipDialog.setTitle("IP");
				ipDialog.setHeaderText("Please enter the IP to use");
				ipDialog.setContentText("IP:");
				
				Optional<String> ipResult = ipDialog.showAndWait();
				ipResult.ifPresent(letter -> {
					ip = letter;
				});
			}
			
			System.out.println("IP: " + ip);
			System.out.println("Port: " + port);
			System.out.println("isServer: " + isServer);
			
			// If you are running as a server, create the server. Otherwise, create the client
			connection = isServer ? createServer() : createClient();
			
			// Sets up the game
			generateTargetField();
			generateLocationField();
			createShips();
			
			// Start the connection thread once the field is set up
			connection.startConnection();
			
			grid.setHgap(5);
			grid.setVgap(5);
			stage.setScene(scene);
			stage.show();
			
	}
	
	/**
	 * Sets up the field that the user will be attacking. This mostly involves creating the button listeners
	 */
	private void generateTargetField() {
		// Loop through the board and set every tile to empty and create the button listeners
		for(int i = 0; i < fieldImageTarget.length; i++) {
			for(int j = 0; j < fieldImageTarget[0].length; j++) {
				fieldImageTarget[i][j] = new ImageView(ImagePath.TILE_CLICKED.toText());
				fieldImageTarget[i][j].setUserData(new int[] {i, j});
				fieldStateTarget[i][j] = State.EMPTY;
				
				// When a button is clicked, set the image to have the click effect IF it is empty
				fieldImageTarget[i][j].setOnMousePressed((event) -> {
					Object img = event.getSource();
					if(yourTurn && isConnected) {
						if(img instanceof ImageView) {
							int[] cords = (int[])((ImageView) img).getUserData();
							if(fieldStateTarget[cords[0]][cords[1]].equals(State.EMPTY)) {
								((ImageView) img).setImage(new Image("res/tile_clicked.jpg"));
							}
						}
					}
				});
				
				// When the mouse is let go of, process the cords and decide if that tile is empty or has a ship
				fieldImageTarget[i][j].setOnMouseReleased((event) -> {
					Object img = event.getSource();
					if(yourTurn && isConnected) {
						if(img instanceof ImageView) {
							// Grab the cords associated with the tile that was clicked
							int[] cords = (int[])((ImageView) img).getUserData();
							
							// If the tile is empty or contains a ship, a move was used. Tell that to the server
							if(fieldStateTarget[cords[0]][cords[1]].equals(State.EMPTY) || fieldStateTarget[cords[0]][cords[1]].equals(State.SHIP)) {
								try {
									DEBUGPRINT("Sent: (" + cords[0] + "," + cords[1] + ")");
									connection.send(new int[] {cords[0], cords[1]});
									connection.send("ENDTURN");
									stage.setTitle((isServer ? "Server: " : "Client: ") + "Opponents turn");
									yourTurn = false;
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
							
							// If the tile is a ship, set the tile and state to be hit and increase the victory int 
							// Also tell the other client that a tile was hit so they can keep track
							if(fieldStateTarget[cords[0]][cords[1]].equals(State.SHIP)) {
								fieldStateTarget[cords[0]][cords[1]] = State.SHIPHIT;
								DEBUGPRINT("Setting tile to ship hit");
								((ImageView) img).setImage(new Image("res/tile_ship_hit.jpg"));
								tileHit++;
								try {
									connection.send("CLIENTHITTILE" + tileHit);
								} catch (Exception e) {
									e.printStackTrace();
								}
							// If the tile is empty, set it to be hit
							} else if(fieldStateTarget[cords[0]][cords[1]].equals(State.EMPTY)) {
								fieldStateTarget[cords[0]][cords[1]] = State.EMPTYHIT;
								DEBUGPRINT("Setting tile to empty hit");
								((ImageView) img).setImage(new Image("res/tile_empty_hit.jpg"));
							}
							
							// If you are the server, tileHit is your count and otherTileHit is the clients count.
							// If either is 17, that person won. Same logic vice versa
							try {
								if(isServer) {
									DEBUGPRINT("Server=" + tileHit + " Client="+otherTileHit);
									if(tileHit == 17) {
										DEBUGPRINT("Server won");
										connection.send("SERVERWON");
									} else if(otherTileHit == 17) {
										DEBUGPRINT("Client won");
										connection.send("CLIENTWON");
									}
								} else {
									DEBUGPRINT("Client=" + tileHit + " Server="+otherTileHit);
									if(tileHit == 17) {
										DEBUGPRINT("Client won");
										connection.send("CLIENTWON");
									} else if(otherTileHit == 17) {
										DEBUGPRINT("Server won");
										connection.send("SERVERWON");
									}
								}
							} catch(Exception e) {
								e.printStackTrace();
							}
						}
						
					}
				});
				
				// When the mouse hovers over a tile, display a visual effect IF it is empty
				// (Still need to check if it it's state is a ship because the player doesn't know that yet)
				fieldImageTarget[i][j].setOnMouseEntered((event) -> {
					Object img = event.getSource();
					if(yourTurn && isConnected) {
						if(img instanceof ImageView) {
							int[] cords = (int[])((ImageView) img).getUserData();
							if (fieldStateTarget[cords[0]][cords[1]].equals(State.EMPTY) || fieldStateTarget[cords[0]][cords[1]].equals(State.SHIP)) {
								((ImageView) img).setImage(new Image("res/tile_hover.jpg"));
							}
						}
					}
				});
				
				// When the mouse leaves a tile, set it to normal IF it is empty
				fieldImageTarget[i][j].setOnMouseExited((event) -> {
					Object img = event.getSource();
					if(yourTurn && isConnected) {
						if(img instanceof ImageView) {
							int[] cords = (int[])((ImageView) img).getUserData();
							if (fieldStateTarget[cords[0]][cords[1]].equals(State.EMPTY) || fieldStateTarget[cords[0]][cords[1]].equals(State.SHIP)) {
								((ImageView) img).setImage(new Image("res/tile_empty.jpg"));
							}
						}
					}
				});
				
				grid.add(fieldImageTarget[i][j], i, j);
			}
		}
	}
	
	/**
	 * Creates the field that shows the players ship locations
	 */
	private void generateLocationField() {
		for(int i = 0; i < fieldImageLocation.length; i++) {
			for(int j = 0; j < fieldImageLocation[0].length; j++) {
				fieldImageLocation[i][j] = new ImageView(ImagePath.TILE_CLICKED.toText());
				fieldImageLocation[i][j].setUserData(new int[] {i, j});
				
				fieldStateLocation[i][j] = State.EMPTY;
			
				grid.add(fieldImageLocation[i][j], i+15, j);
			}
		}
	}
	
	/**
	 * Creates ships for the player
	 */
	private void createShips() {
		//TODO: avoid brute forcing location
		// Contains cords that are already used
		ArrayList<int[]> usedCords = new ArrayList<>();
		int[] lengths = {5, 4, 3, 3, 2};
		for(int len : lengths) {
			boolean validCords = false;
			int[][] cords = null;
			while(!validCords) {
				// Class used to generate cords
				cords = CordGenerator.generateCords(len);
				int validCount = 0;
				// If a cord is not used, increase the valid count. If that equals the ship length, break the loop.
				for(int[] c : cords) {
					if(!contains(usedCords, c)) {
						validCount++;
					}
				}
				if(validCount == len) {
					break;
				}
			}
			// Adds each cords to the list containing used ones
			for(int[] c : cords) {
				usedCords.add(c);
				fieldStateLocation[c[0]][c[1]] = State.SHIP;
			}
		}
	}
	
	/**
	 * Custom contains function that is compatible with an ArrayList and Int array
	 * @param usedCords list of used cords
	 * @param cords list of cords we want to use
	 * @return
	 */
	private boolean contains(ArrayList<int[]> usedCords, int[] cords) {
		boolean isUsed = false;
		for(int[] i : usedCords) {
			if(i[0] == cords[0] && i[1] == cords[1]) {
				isUsed = true;
			}
		}
		return isUsed;
	}
	
	public void DEBUGPRINT(String text) {
		if(debugEnabled) {
			if(isServer) {
				System.out.println("SERVER: " + text);
			} else {
				System.out.println("CLIENT: " + text);
			}
		}
	}
}
