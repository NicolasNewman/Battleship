package classes;

public enum ImagePath {
	TILE_CLICKED, TILE_EMPTY_HIT, TILE_EMPTY, TILE_HOVER, TILE_SHIP, TILE_SHIP_HIT;
	
	public String toText() {
		switch (this) {
			case TILE_CLICKED:
				return "res/tile_clicked.jpg";
			case TILE_EMPTY_HIT:
				return "res/tile_empty_hit.jpg";
			case TILE_EMPTY:
				return "res/tile_empty.jpg";
			case TILE_HOVER:
				return "res/tile_hover.jpg";
			case TILE_SHIP:
				return "res/tile_ship_alive.jpg";
			case TILE_SHIP_HIT:
				return "res/tile_ship_hit.jpg";
			default:
				return null;
		}
	}
}
