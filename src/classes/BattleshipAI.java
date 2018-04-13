package classes;

import java.util.ArrayList;
import java.util.Random;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class BattleshipAI {
	
	
	private Parent root;
	private Stage stage;
	private GridPane grid = new GridPane();
	private ImageView[][] fieldImageTarget = new ImageView[10][10];
	private ImageView[][] fieldImageLocation = new ImageView[10][10];
	//private State[][] fieldStateTarget = new State[10][10];
	private State[][] fieldStateLocation = new State[10][10];
	
	private State[][] aiState = new State[10][10];
	
	private AI ai = new AI(fieldStateLocation, fieldImageLocation);
	
	private int aiHit = 0;
	private int playerHit = 0;
	
	public BattleshipAI() {
		createWindow();
	}
	
	private void createWindow() {
			//root = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/field.fxml"));
			stage = new Stage();
			stage.setTitle("Battleship");
			Scene scene = new Scene(grid);
			
			generateTargetField();
			generateLocationField();
			createShips(true);
			createShips(false);
			
			grid.setHgap(5);
			grid.setVgap(5);
			stage.setScene(scene);
			stage.show();
			
	}
	
	private void generateTargetField() {
		for(int i = 0; i < fieldImageTarget.length; i++) {
			for(int j = 0; j < fieldImageTarget[0].length; j++) {
				fieldImageTarget[i][j] = new ImageView("res/tile_empty.jpg");
				fieldImageTarget[i][j].setUserData(new int[] {i, j});
				
				//fieldStateTarget[i][j] = State.EMPTY;
				//fieldState[i][j].setUserData(new int[] {i, j});
				
				fieldImageTarget[i][j].setOnMousePressed((event) -> {
					Object img = event.getSource();
					if(img instanceof ImageView) {
						int[] cords = (int[])((ImageView) img).getUserData();
						if(aiState[cords[0]][cords[1]].equals(State.EMPTY)) {
							((ImageView) img).setImage(new Image("res/tile_clicked.jpg"));
						}
					}
				});
				
				fieldImageTarget[i][j].setOnMouseReleased((event) -> {
					Object img = event.getSource();
					if(img instanceof ImageView) {
						int[] cords = (int[])((ImageView) img).getUserData();
						
						if(aiState[cords[0]][cords[1]].equals(State.EMPTY) || aiState[cords[0]][cords[1]].equals(State.SHIP)) {
							aiMove();
						}
						
						if(aiState[cords[0]][cords[1]].equals(State.SHIP)) {
							aiState[cords[0]][cords[1]] = State.SHIPHIT;
							((ImageView) img).setImage(new Image("res/tile_ship_hit.jpg"));
							playerHit++;
						} else if(aiState[cords[0]][cords[1]].equals(State.EMPTY)) {
							aiState[cords[0]][cords[1]] = State.EMPTYHIT;
							((ImageView) img).setImage(new Image("res/tile_empty_hit.jpg"));
						}
						
						if(playerHit == 17) {
							System.out.println("Player won");
							for(int x = 0; x < fieldImageLocation.length; x++) {
								for(int y = 0; y < fieldImageLocation[0].length; y++) {
									fieldImageLocation[x][y].setImage(new Image("res/tile_hover.jpg"));
									fieldStateLocation[x][y] = State.DEFEAT;
								}
							}
						} else if(!ai.isAlive()) {
							System.out.println("Player lost");
							for(int x = 0; x < fieldImageTarget.length; x++) {
								for(int y = 0; y < fieldImageTarget[0].length; y++) {
									fieldImageTarget[x][y].setImage(new Image("res/tile_hover.jpg"));
									aiState[x][y] = State.DEFEAT;
								}
							}
						}
						
					}
				});
				
				fieldImageTarget[i][j].setOnMouseEntered((event) -> {
					Object img = event.getSource();
					if(img instanceof ImageView) {
						int[] cords = (int[])((ImageView) img).getUserData();
						if (aiState[cords[0]][cords[1]].equals(State.EMPTY) || aiState[cords[0]][cords[1]].equals(State.SHIP)) {
							((ImageView) img).setImage(new Image("res/tile_hover.jpg"));
						}
					}
				});
				
				fieldImageTarget[i][j].setOnMouseExited((event) -> {
					Object img = event.getSource();
					if(img instanceof ImageView) {
						int[] cords = (int[])((ImageView) img).getUserData();
						if (aiState[cords[0]][cords[1]].equals(State.EMPTY) || aiState[cords[0]][cords[1]].equals(State.SHIP)) {
							((ImageView) img).setImage(new Image("res/tile_empty.jpg"));
						} /*else if (fieldStateTarget[cords[0]][cords[1]].equals(State.EMPTYHIT)) {
							((ImageView) img).setImage(new Image("res/tile_empty_hit.jpg"));
						}*/
					}
				});
				
				grid.add(fieldImageTarget[i][j], i, j);
			}
		}
	}
	
	private void generateLocationField() {
		for(int i = 0; i < fieldImageLocation.length; i++) {
			for(int j = 0; j < fieldImageLocation[0].length; j++) {
				fieldImageLocation[i][j] = new ImageView("res/tile_empty.jpg");
				fieldImageLocation[i][j].setUserData(new int[] {i, j});
				
				fieldStateLocation[i][j] = State.EMPTY;
			
				grid.add(fieldImageLocation[i][j], i+15, j);
			}
		}
	}
	
	private void createShips(boolean forPlayer) {
		//TODO: avoid brute forcing location
		if(!forPlayer) {
			for(int i = 0; i < aiState.length; i++) {
				for(int j = 0; j < aiState.length; j++) {
					aiState[i][j] = State.EMPTY;
				}
			}
		}
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
				if(forPlayer) {
					fieldImageLocation[c[0]][c[1]].setImage(new Image("res/tile_ship_alive.jpg"));
					fieldStateLocation[c[0]][c[1]] = State.SHIP;
				} else {
					aiState[c[0]][c[1]] = State.SHIP;
				}
			}
		}
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
	
	private void aiMove() {
		ai.aiMove();
	}
	
} 
