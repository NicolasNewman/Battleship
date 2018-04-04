package classes;

public class Game implements Runnable {
	private Thread t;
	private Battleship field;
	private String name;
	
	public Game(Battleship field, String name) {
		this.field = field;
		this.name = name;
	}
	
	public void run() {
		Phase phase = Phase.CREATE;
		field.setStatusText("Click two horizontal or verticle spots to place a 5x5 ship");
		/*while(phase.equals(Phase.CREATE)) {
			
		}*/
	}
	
	public void start() {
		if(t == null) {
			t = new Thread(this, name);
			t.start();
		}
	}
}
