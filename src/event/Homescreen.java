package event;

import java.io.IOException;

import classes.Battleship;
import classes.State;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class Homescreen {
	
	@FXML Button playAI;
	@FXML Button playHuman;
	
	public void playAI() {
		Battleship game = new Battleship();
		game.setState(1, 1, State.HIT);
	}
	
	public void playHuman() {
		System.out.println("Human");
	}
}
