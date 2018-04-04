package classes;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Battleship {
	
	
	private Parent root;
	private Stage stage;
	private Label statusBar;
	private GridPane grid = new GridPane();
	private Button[][] location = new Button[10][10];
	private ShipManager shipPlayer;
	private Ship[] playerShips;
	
	public Battleship() {
		createWindow();
	}
	
	private void createWindow() {
			//root = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/field.fxml"));
			stage = new Stage();
			stage.setTitle("Battleship");
			
			Scene scene = new Scene(grid);
			
			for(int i = 0; i < location.length; i++) {
				for(int j = 0; j < location[0].length; j++) {
					location[i][j] = new Button(State.EMPTY.toString());
					location[i][j].setUserData(new int[] {i, j});
					location[i][j].setOnAction(new EventHandler<ActionEvent>( ) {
						@Override
						public void handle(ActionEvent e) {
							Object btn = e.getSource();
							if(btn instanceof Button) {
								int[] data = (int[]) ((Button) btn).getUserData();
								buttonPressed(data[0], data[1]);
							}
						}
					});
					location[i][j].setPrefSize(50, 50);
					grid.add(location[i][j], i, j);
				}
			}
			statusBar = new Label();
			statusBar.setPrefSize(100, 50);
			grid.add(statusBar, 10, 0);

			shipPlayer = new ShipManager();
			playerShips = shipPlayer.getShips();
			for(Ship s : playerShips) {
				int[][] cords = s.getUsedCords();
				for(int i = 0; i < cords.length; i++) {
					setState(cords[i][0], cords[i][1], State.SHIP);
				}
			}
			
			stage.setScene(scene);
			stage.show();
			
	}
	
	private void updateGrid() {
		grid.getChildren().clear();
		for(int i = 0; i < location.length; i++) {
			for(int j = 0; j < location[0].length; j++) {
				grid.add(location[i][j], i, j);
			}
		}
		grid.add(statusBar, 10, 0);
	}
	
	public void buttonPressed(int i, int j) {
		//eventComplete = true;
		//TODO: detect hit
		if(location[i][j].getText() == "[S]") {
			System.out.println("HIT");
			location[i][j].setText(State.HIT.toString());
		}
	}
	
	public void setState(int i, int j, State state) {
		location[i][j].setText(state.toString());
		updateGrid();
	}
	
	public void setStatusText(String text) {
		statusBar.setText(text);
	}
} 
