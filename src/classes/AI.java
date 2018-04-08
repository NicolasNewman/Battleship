package classes;

import java.util.Random;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class AI {
	
	private boolean debugMode = true;
	
	private State[][] playerState;
	private ImageView[][] playerImage;
	
	private boolean foundShip;
	
	private boolean initialShip = false;
	private boolean hitLowerBounds = false;
	private boolean hitUpperBounds = false;
	private int lowerCheck = 1;
	private int upperCheck = 1;
	private int tilesAlive = 17;
	
	private Orientation orientation = Orientation.UNKNOWN;
	
	private int[] shipCords = new int[2];
	private int[] initialCords = new int[2];
	
	public AI(State[][] playerState, ImageView[][] playerImage) {
		this.playerImage = playerImage;
		this.playerState = playerState;
	}
	
	public boolean isAlive() {
		return tilesAlive != 0;
	}
	
	public void aiMove() {
		boolean played = false;
		Random rand = new Random();
		// While a move has not been made
		while(!played) {
			int x = 0;
			int y = 0;
			// If a ship has been found, execute this. Else, make a random move
			if(initialShip) {
				x = initialCords[0];
				y = initialCords[1];
				// If we do not yet know the orientation of the ship, find it by testing each
				// square around the ship
				if(orientation.equals(Orientation.UNKNOWN)) {
					boolean[] valid = testForShip(x, y, 1, 0);
					if(!valid[0] && !valid[1]) {
						valid = testForShip(x, y, -1, 0);
					}
					if(!valid[0] && !valid[1]) {
						valid = testForShip(x, y, 0, 1);
					}
					
					if(!valid[0] && !valid[1]) {
						valid = testForShip(x, y, 0, -1);
					}
					if(valid[1] == true) {
						played = true;
					}
					break;
				// If the ship is horizontally aligned, execute this
				} else if (orientation.equals(Orientation.HORIZONTAL)) {
					// IF the first tile started at a border, we know where it ends
					if(initialCords[0] == 9) {
						hitUpperBounds = true;
						DEBUGPRINT("Tile started at border. Upper bounds found (not from fucn)");
					}
					if(initialCords[0] == 0) {
						hitLowerBounds = true;
						DEBUGPRINT("Tile started at border. Lower bounds found (not from func)");
					}
					// If the lower region of the ship has not been found, run this
					if(!hitLowerBounds) {
						DEBUGPRINT("Testing lower bounds");
						while(true) {
							// valid[0] = if the bounds have been found. 
							// valid[1] = if a move has been used (incase the bounds if found through a empty hit)
							boolean[] valid = hitEmpty(x, y, -1*lowerCheck, 0);
							// Increment the factor to check the next square
							lowerCheck++;
							if(valid[0] == true) {
								DEBUGPRINT("Found lower bounds");
								hitLowerBounds = true;
								//break; //TODO: needed
							}
							if(valid[1] == true) {
								played = true;
								break;
							} else {
								break; //TODO: lost move
							}
						}
					// If the upper region of the ship has not been found, run this
					} else if(!hitUpperBounds) {
						DEBUGPRINT("Testing upper bounds");
						while(true) {
							boolean[] valid = hitEmpty(x, y, 1*upperCheck, 0);
							upperCheck++;
							if(valid[0] == true) {
								DEBUGPRINT("Found upper bounds");
								hitUpperBounds = true;
								//break; //TODO: needed
							}
							if(valid[1] == true) {
								played = true;
								break;
							} else {
								break;
							}
						}
					}
					// If the lower and upper bounds have been found, the ship is dead.
					// Then, reset variables used for tracking ship
					if(hitLowerBounds && hitUpperBounds) {
						//TODO: End condition
						initialShip = false;
						hitUpperBounds = false;
						hitLowerBounds = false;
						lowerCheck = 1;
						upperCheck = 1;
						orientation = Orientation.UNKNOWN;
						DEBUGPRINT("SHIP SUNK");
					}
				} else if (orientation.equals(Orientation.VERTICAL)) {
					// If the lower region of the ship has not been found, run this
					if(initialCords[1] == 9) {
						hitUpperBounds = true;
						DEBUGPRINT("Tile started at border. Upper bounds found (not from func)");
					}
					if(initialCords[1] == 0) {
						hitLowerBounds = true;
						DEBUGPRINT("Tile started at border. Lower bounds found (not from func)");
					}
					if(!hitLowerBounds) {
						DEBUGPRINT("Testing lower bounds");
						while(true) {
							// valid[0] = if the bounds have been found. 
							// valid[1] = if a move has been used (incase the bounds if found through a empty hit)
							boolean[] valid = hitEmpty(x, y, 0, -1*lowerCheck);
							// Increment the factor to check the next square
							lowerCheck++;
							if(valid[0] == true) {
								DEBUGPRINT("Found lower bounds");
								hitLowerBounds = true;
								//break; //TODO: needed
							}
							if(valid[1] == true) {
								played = true;
								break;
							} else {
								break; //TODO: lost move
							}
						}
					// If the upper region of the ship has not been found, run this
					} else if(!hitUpperBounds) {
						DEBUGPRINT("Testing upper bounds");
						while(true) {
							boolean[] valid = hitEmpty(x, y, 0, 1*upperCheck);
							upperCheck++;
							if(valid[0] == true) {
								DEBUGPRINT("Found upper bounds");
								hitUpperBounds = true;
								//break; //TODO: needed
							}
							if(valid[1] == true) {
								played = true;
								break;
							} else {
								break;
							}
						}
					}
					// If the lower and upper bounds have been found, the ship is dead.
					// Then, reset variables used for tracking ship
					if(hitLowerBounds && hitUpperBounds) {
						//TODO: End condition
						initialShip = false;
						hitUpperBounds = false;
						hitLowerBounds = false;
						lowerCheck = 1;
						upperCheck = 1;
						orientation = Orientation.UNKNOWN;
						DEBUGPRINT("SHIP SUNK");
					}
				}
			// If a ship has not yet been found, randomly hit until it is found
			} else {
				x = rand.nextInt((9 - 0) + 1) + 0;
				y = rand.nextInt((9 - 0) + 1) + 0;
				
				// If the tile is empty, set it to emptyhit
				if(playerState[x][y].equals(State.EMPTY)) {
					playerState[x][y] = State.EMPTYHIT;
					playerImage[x][y].setImage(new Image("res/tile_empty_hit.jpg"));
					played = true;
					break;
				// If the tile is a ship, hit that tile and have the next moves uncover the ship
				} else if(playerState[x][y].equals(State.SHIP)) {
					playerState[x][y] = State.SHIPHIT;
					tilesAlive--;
					playerImage[x][y].setImage(new Image("res/tile_ship_hit.jpg"));
					played = true;
					
					if(!initialShip) {
						initialShip = true;
						initialCords = new int[] {x, y};
					}
					
					// Sets cords to the inital tile that was found
					shipCords = new int[] {x, y};
					break;
				}
			}
		}
	}
	
	/**
	 * Once a ship is found, test the surrounding tiles to determine if it is vertical or horizontal
	 * @param x cord
	 * @param y cord
	 * @param xF x factor
	 * @param yF y factor
	 * @return [0]: ship found [1]: move used
	 */
	private boolean[] testForShip(int x, int y, int xF, int yF) {
		// Checks to make sure cords doesn't exit the board (index out of bounds)
		if(x+xF > 9 || y+yF > 9 || x+xF < 0 || y+yF < 0) {
			return new boolean[] {false, false};
		}
		
		// If the tile is empty, a move was used and a ship was not found
		if(playerState[x+xF][y+yF].equals(State.EMPTY)) {
			playerState[x+xF][y+yF] = State.EMPTYHIT;
			playerImage[x+xF][y+yF].setImage(new Image("res/tile_empty_hit.jpg"));
			return new boolean[] {false, true};
		// If the tile was a ship, a move was used and the ship was found
		} else if(playerState[x+xF][y+yF].equals(State.SHIP)) {
			playerState[x+xF][y+yF] = State.SHIPHIT;
			playerImage[x+xF][y+yF].setImage(new Image("res/tile_ship_hit.jpg"));
			tilesAlive--;
			shipCords = new int[] {x+1, y};
			
			// If the x factor is not zero, then the ship was discovered on a horizontal move
			// Otherwise, a vertical move
			if(xF != 0) {
				DEBUGPRINT("Ship is horizontal");
				orientation = Orientation.HORIZONTAL;
			} else {
				DEBUGPRINT("Ship is vertical");
				orientation = Orientation.VERTICAL;
			}
			return new boolean[] {true, true};
		}
		DEBUGPRINT("Error: 6");
		return new boolean[] {false, false};
	}
	
	/**
	 * 
	 * @param x cord
	 * @param y cord
	 * @param xF x factor
	 * @param yF y factor
	 * @return [0]: ship end [1] move used
	 */
	private boolean[] hitEmpty(int x, int y, int xF, int yF) {
		//TODO: IF IT STARTS AT BORDER, THIS CAUSES LOOP
		// Make sure the cords don't exit the board
		if(x+xF > 9 || y+yF > 9 || x+xF < 0 || y+yF < 0) {
			return new boolean[] {false, false};
		}
		// If the tile is empty, we know the ship ends at that bounds
		if(playerState[x+xF][y+yF].equals(State.EMPTY)) {
			playerState[x+xF][y+yF] = State.EMPTYHIT;
			playerImage[x+xF][y+yF].setImage(new Image("res/tile_empty_hit.jpg"));
			return new boolean[] {true, true};
		// If the tile is already hit, we know the ship ends but a move isn't used
		} else if(playerState[x+xF][y+yF].equals(State.EMPTYHIT)) {
			return new boolean[] {true, false};
		// If the tile is a ship, it still hasn't ended but a move is used
		} else if(playerState[x+xF][y+yF].equals(State.SHIP)) {
			playerState[x+xF][y+yF] = State.SHIPHIT;
			tilesAlive--;
			playerImage[x+xF][y+yF].setImage(new Image("res/tile_ship_hit.jpg"));
			shipCords = new int[] {x+xF, y+yF};

			// If we hit a ship at a boarder, we know its ended
			if(orientation.equals(Orientation.HORIZONTAL)) {
				System.out.println(x + " : " + xF + "(" + y + ":" + yF + ")");
				if(x+xF == 9 || x+xF == 0) {
					return new boolean[] {true, true};
				}
			} else if (orientation.equals(Orientation.VERTICAL)) {
				System.out.println(y + ":" + yF + " (" + x + ":" + xF + ")");
				if(y+yF == 9 || y+yF == 0) {
					return new boolean[] {true, true};
				}
			}
			return new boolean[] {false, true};
		}
		DEBUGPRINT("Error: 5");
		return new boolean[] {false, false};
	}
	
	private void DEBUGPRINT(String message) {
		if(debugMode) {
			System.out.println(message);
		}
	}

}
