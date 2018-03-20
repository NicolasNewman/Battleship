package classes;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

public class Battleship {
	
	
	private Parent root;
	private Stage stage;
	private Label statusBar;
	private GridPane grid = new GridPane();
	private Button[][] location = new Button[10][10];
	private Ship[] ships = new Ship[15];
	
	public Battleship() {
		createWindow();
		startGame();
	}
	
	private void createWindow() {
			//root = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/field.fxml"));
			stage = new Stage();
			stage.setTitle("Battleship");
			
			Scene scene = new Scene(grid);
			
			for(int i = 0; i < location.length; i++) {
				for(int j = 0; j < location[0].length; j++) {
					location[i][j] = new Button(State.EMPTY.toString());
					location[i][j].setPrefSize(50, 50);
					grid.add(location[i][j], i, j);
				}
			}
			statusBar = new Label();
			statusBar.setPrefSize(100, 50);
			grid.add(statusBar, 10, 0);

			
			
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
	
	public void setState(int i, int j, State state) {
		location[i][j].setText(state.toString());
		updateGrid();
	}
	
	public void setStatusText(String text) {
		statusBar.setText(text);
	}
	
	// Multi thread
	public void startGame() {
		Game game = new Game(this, "game");
		game.start();
	}
} 
