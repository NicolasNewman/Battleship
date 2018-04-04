package classes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class ShipManager {
	
	private Ship[] ships = new Ship[10];
	private HashMap<int[], State> globalState = new HashMap<int[], State>();
	
	public ShipManager() {
		
		Ship carrier = new Ship(5);
		
		Ship battleshipOne = new Ship(4);
		Ship battleshipTwo = new Ship(4);
		
		Ship cruiserOne = new Ship(3);
		Ship cruiserTwo = new Ship(3);
		Ship cruiserThree = new Ship(3);
		
		Ship destroyerOne = new Ship(2);
		Ship destroyerTwo = new Ship(2);
		Ship destroyerThree = new Ship(2);
		Ship destroyerFour = new Ship(2);
		
		ships = new Ship[]{carrier, battleshipOne, battleshipTwo, cruiserOne, cruiserTwo, cruiserThree, destroyerOne, destroyerTwo, destroyerThree, destroyerFour};
		for(Ship s : ships) {
			boolean notInUse = false;
			int targetLength = s.getLength();
			System.out.println("Outer loop");
			while(!notInUse) {
				int valid = 0;
				int[][] cords = CordGenerator.generateCords(targetLength);
				for(int[] c : cords) {
					if(globalState.size() == 0) {
						valid = targetLength;
						break;
					} else if(!globalState.containsKey(c)) {
						valid += 1;
					}
				}
				if(valid == targetLength) {
					for(int[] c : cords) {
						globalState.put(c, State.SHIP);
						s.addPositionState(c, State.SHIP);
					}
					notInUse = true;
				}
			}
		}
	}
	
	public Ship[] getShips() {
		return ships;
	}
}
