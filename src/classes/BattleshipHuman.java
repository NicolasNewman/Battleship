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
	private Parent root;
	private Stage stage;
	private GridPane grid = new GridPane();
	private boolean debugEnabled = true;
	
	private boolean yourTurn;
	private ImageView[][] fieldImageTarget = new ImageView[10][10];
	private ImageView[][] fieldImageLocation = new ImageView[10][10];
	private State[][] fieldStateLocation = new State[10][10];
	private State[][] fieldStateTarget = new State[10][10];
	
	private boolean isServer = false;
	private boolean isConnected = false;
	private int port = 0;
	private String ip;
	private NetworkConnection connection;
	
	private Server createServer() {
		return new Server(port, data -> {
			Platform.runLater(() -> {
				DEBUGPRINT("Received: " + data);
				if(data.getClass().isArray()) {
					if(data.getClass().getComponentType().equals(int.class)) {
						int[] cords = (int[]) data;
						DEBUGPRINT("Received cords (" + cords[0] + "," + cords[1] + ")");
						processCords(cords[0], cords[1]);
						// Effect location
						if(fieldStateLocation[cords[0]][cords[1]].equals(State.EMPTY)) {
							fieldStateLocation[cords[0]][cords[1]] = State.EMPTYHIT;
							fieldImageLocation[cords[0]][cords[1]].setImage(new Image(ImagePath.TILE_EMPTY_HIT.toText()));
						} else if (fieldStateLocation[cords[0]][cords[1]].equals(State.SHIP)) {
							fieldStateLocation[cords[0]][cords[1]] = State.SHIPHIT;
							fieldImageLocation[cords[0]][cords[1]].setImage(new Image(ImagePath.TILE_SHIP_HIT.toText()));
						}
					} else if(data.getClass().getComponentType().equals(State[].class)) {
						State[][] enemyLocations = (State[][]) data;
						System.out.println("Updating with opponents state");
						for(int i = 0; i < enemyLocations.length; i++) {
							for(int j = 0; j < enemyLocations[0].length; j++) {
								fieldStateTarget[i][j] = enemyLocations[i][j];
							}
						}
					}
				} else {
					switch (data.toString()) {
						case "ENDTURN":
							yourTurn = true;
							stage.setTitle("Your turn");
							DEBUGPRINT("My turn");
							break;
						case "CONNECTIONESTABLISHED":
							isConnected = true;
							for(int i = 0; i < fieldStateLocation.length; i++) {
								for(int j = 0; j < fieldStateLocation[0].length; j++) {
									System.out.println("Running loop");
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
								System.out.println("SENDING STATES");
								connection.send(fieldStateLocation);
							} catch (Exception e) {
								e.printStackTrace();
							}
							break;
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
					//System.out.println(data.getClass().getComponentType());
					if(data.getClass().getComponentType().equals(int.class)) {
						int[] cords = (int[]) data;
						DEBUGPRINT("Received cords (" + cords[0] + "," + cords[1] + ")");
						processCords(cords[0], cords[1]);
						// Effect location
						if(fieldStateLocation[cords[0]][cords[1]].equals(State.EMPTY)) {
							fieldStateLocation[cords[0]][cords[1]] = State.EMPTYHIT;
							fieldImageLocation[cords[0]][cords[1]].setImage(new Image(ImagePath.TILE_EMPTY_HIT.toText()));
						} else if (fieldStateLocation[cords[0]][cords[1]].equals(State.SHIP)) {
							fieldStateLocation[cords[0]][cords[1]] = State.SHIPHIT;
							fieldImageLocation[cords[0]][cords[1]].setImage(new Image(ImagePath.TILE_SHIP_HIT.toText()));
						}
					} else if(data.getClass().getComponentType().equals(State.class)) {
						State[][] enemyLocations = (State[][]) data;
						for(int i = 0; i < enemyLocations.length; i++) {
							for(int j = 0; j < enemyLocations[0].length; j++) {
								fieldStateTarget[i][j] = enemyLocations[i][j];
							}
						}
					}
				} else {
					switch (data.toString()) {
						case "ENDTURN":
							yourTurn = true;
							stage.setTitle("Your turn");
							DEBUGPRINT("My turn");
							break;
						case "CONNECTIONESTABLISHED":
							isConnected = true;
							for(int i = 0; i < fieldStateLocation.length; i++) {
								for(int j = 0; j < fieldStateLocation[0].length; j++) {
									System.out.println("Running loop");
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
								System.out.println("SENDING STATES");
								connection.send(fieldStateLocation);
							} catch (Exception e) {
								e.printStackTrace();
							}
							break;
					}
				}
			});
		});
	}
	
	private int playerHit = 0;
	
	public BattleshipHuman() throws Exception {
		createWindow();
	}
	
	private void createWindow() throws Exception {
			stage = new Stage();
			stage.setTitle("Battleship");
			Scene scene = new Scene(grid);
			
			stage.setOnCloseRequest((event) -> {
				try {
					connection.closeConnection();
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			
			List<String> modeChoices = new ArrayList<>();
			modeChoices.add("Client");
			modeChoices.add("Server");
			
			ChoiceDialog<String> choiceDialog = new ChoiceDialog<>("Client", modeChoices);
			choiceDialog.setTitle("Run as...");
			choiceDialog.setHeaderText("Would you like to run as the client or server?");
			choiceDialog.setContentText("Choice:");
			
			Optional<String> choiceResult = choiceDialog.showAndWait();
			choiceResult.ifPresent(letter -> {
				if(choiceResult.get().equals("Client")) {
					isServer = false;
				} else if(choiceResult.get().equals("Server")) {
					isServer = true;
				}
			});
			yourTurn = isServer ? true : false;
			if(yourTurn) {
				stage.setTitle("Your turn");
			} else {
				stage.setTitle("Opponents turn");
			}
			
			TextInputDialog portDialog = new TextInputDialog("1234");
			portDialog.setTitle("Port");
			portDialog.setHeaderText("Please enter the port to use");
			portDialog.setContentText("Port:");
			
			Optional<String> portResult = portDialog.showAndWait();
			portResult.ifPresent(letter -> {
				port = Integer.parseInt(letter);
			});
			
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
			
			connection = isServer ? createServer() : createClient();
			
			generateTargetField();
			generateLocationField();
			createShips();
			
			connection.startConnection();
			grid.setHgap(5);
			grid.setVgap(5);
			stage.setScene(scene);
			stage.show();
			
	}
	
	private void generateTargetField() {
		for(int i = 0; i < fieldImageTarget.length; i++) {
			for(int j = 0; j < fieldImageTarget[0].length; j++) {
				//fieldImageTarget[i][j] = new ImageView("res/tile_empty.jpg");
				fieldImageTarget[i][j] = new ImageView(ImagePath.TILE_CLICKED.toText());
				fieldImageTarget[i][j].setUserData(new int[] {i, j});
				fieldStateTarget[i][j] = State.EMPTY;
				
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
				
				fieldImageTarget[i][j].setOnMouseReleased((event) -> {
					Object img = event.getSource();
					if(yourTurn && isConnected) {
						if(img instanceof ImageView) {
							int[] cords = (int[])((ImageView) img).getUserData();
							try {
								DEBUGPRINT("Sent: (" + cords[0] + "," + cords[1] + ")");
								connection.send(new int[] {cords[0], cords[1]});
								connection.send("ENDTURN");
								stage.setTitle("Opponents turn");
								yourTurn = false;
							} catch (Exception e) {
								e.printStackTrace();
							}
							
							if(fieldStateTarget[cords[0]][cords[1]].equals(State.EMPTY) || fieldStateTarget[cords[0]][cords[1]].equals(State.SHIP)) {
								//aiMove();
							}
							
							if(fieldStateTarget[cords[0]][cords[1]].equals(State.SHIP)) {
								fieldStateTarget[cords[0]][cords[1]] = State.SHIPHIT;
								DEBUGPRINT("Setting tile to ship hit");
								((ImageView) img).setImage(new Image("res/tile_ship_hit.jpg"));
								playerHit++;
							} else if(fieldStateTarget[cords[0]][cords[1]].equals(State.EMPTY)) {
								fieldStateTarget[cords[0]][cords[1]] = State.EMPTYHIT;
								DEBUGPRINT("Setting tile to empty hit");
								((ImageView) img).setImage(new Image("res/tile_empty_hit.jpg"));
							}
						}
					}
				});
				
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
	
	private void generateLocationField() {
		for(int i = 0; i < fieldImageLocation.length; i++) {
			for(int j = 0; j < fieldImageLocation[0].length; j++) {
				//fieldImageLocation[i][j] = new ImageView("res/tile_empty.jpg");
				fieldImageLocation[i][j] = new ImageView(ImagePath.TILE_CLICKED.toText());
				fieldImageLocation[i][j].setUserData(new int[] {i, j});
				
				fieldStateLocation[i][j] = State.EMPTY;
			
				grid.add(fieldImageLocation[i][j], i+15, j);
			}
		}
	}
	
	private void createShips() {
		//TODO: avoid brute forcing location
		ArrayList<int[]> usedCords = new ArrayList<>();
		int[] lengths = {5, 4, 3, 3, 2};
		for(int len : lengths) {
			boolean validCords = false;
			int[][] cords = null;
			while(!validCords) {
				cords = CordGenerator.generateCords(len);
				int validCount = 0;
				for(int[] c : cords) {
					if(!contains(usedCords, c)) {
						validCount++;
					}
				}
				if(validCount == len) {
					break;
				}
			}
			
			for(int[] c : cords) {
				usedCords.add(c);
				//fieldImageLocation[c[0]][c[1]].setImage(new Image("res/tile_ship_alive.jpg"));
				fieldStateLocation[c[0]][c[1]] = State.SHIP;
			}
		}
	}
	
	private void processCords(int x, int y) {
		
	}
	
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
