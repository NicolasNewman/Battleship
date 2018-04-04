package classes;

import java.util.Random;

public class CordGenerator {
	
	//private static Random rand;
	
	public CordGenerator() {

	}
	
	public static int[][] generateCords(int length) {
		Random rand = new Random();
		int[][] cords = new int[length][length];
		int startX = rand.nextInt((9 - 0) + 1) + 0;
		int startY = rand.nextInt((9 - 0) + 1) + 0;
		//cords[0] = new int[] {startX, startY};
		if(startX + length < 9) {
			cords[0] = new int[] {startX, startY};
			int j = 1;
			for(int i = startX+1; i < length+startX; i++) {
				cords[j] = new int[] {i, startY};
				j++;
			}
			return cords;
		} else if(startX - length > 0) {
			cords[0] = new int[] {startX, startY};
			int j = 1;
			for(int i = startX-1; i > startX-length; i--) {
				cords[j] = new int[] {i, startY};
				j++;
			}
			return cords;
		} else if(startY + length < 9) {
			cords[0] = new int[] {startX, startY};
			int j = 1;
			for(int i = startY+1; i < length+startY; i++) {
				cords[j] = new int[] {startX, i};
				j++;
			}
			return cords;
		} else if(startY - length > 0) {
			cords[0] = new int[] {startX, startY};
			int j = 1;
			for(int i = startY-1; i > startY-length; i--) {
				cords[j] = new int[] {startX, i};
				j++;
			}
			return cords;
		}
		return null;
	}

}
