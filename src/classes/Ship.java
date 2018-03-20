package classes;

import java.util.HashMap;

public class Ship {
	
	private int length;
	private HashMap<int[], State> positionState;
	
	public Ship(int length, int[][] pos) {
		positionState = new HashMap<int[], State>();
		this.length = length;
		
		for(int i = 0; i < pos.length; i++) {
			positionState.put(pos[i], State.SHIP);
		}
	}
	
	public State getState(int[] cords) {
		return positionState.get(cords);
	}
	

}
