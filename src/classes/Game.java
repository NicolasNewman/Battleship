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
		while(!phase.equals(Phase.END)) {
			if(phase.equals(Phase.CREATE)) {
				//System.out.println("Hi");
			}
		}
	}
	
	public void start() {
		if(t == null) {
			t = new Thread(this, name);
			t.start();
		}
	}
}
