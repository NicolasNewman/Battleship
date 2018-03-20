package classes;

public enum State {
	EMPTY, SHIP, HIT, HITSHIP, CREATESHIP;
	
	public String toString() {
		switch(this) {
			case EMPTY: return "[ ]";
			case SHIP: return "[S]";
			case HIT: return "[X]";
			case HITSHIP: return "[X]";
			case CREATESHIP: return "[O]";
		}
		return null;
	}
}
