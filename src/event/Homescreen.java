package event;

import classes.BattleshipAI;
import classes.BattleshipHuman;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class Homescreen {
	
	@FXML Button playAI;
	@FXML Button playHuman;
	
	public void playAI() {
		BattleshipAI game = new BattleshipAI();
		//game.setState(1, 1, State.HIT);
	}
	
	public void playHuman() {
		try {
			BattleshipHuman game = new BattleshipHuman();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
