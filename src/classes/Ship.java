package classes;

import java.util.HashMap;

public class Ship {
	
	private int length;
	private HashMap<int[], State> positionState = new HashMap<int[], State>();
	private int[][] cordsUsed;
	private int i;
	
	public Ship(int length) {
		this.length = length;
		cordsUsed = new int[length][2];
	}
	
	public State getState(int[] cords) {
		return positionState.get(cords);
	}
	
	public void addPositionState(int[] cord, State state) {
		positionState.put(cord, state);
		cordsUsed[i] = cord;
		i++;
	}

	public int getLength() {
		return length;
	}
	
	public int[][] getUsedCords() {
		return cordsUsed;
	}
}
