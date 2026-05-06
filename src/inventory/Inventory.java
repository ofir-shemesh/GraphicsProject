package inventory;


public class Inventory {
	public static final int MAX_COINS = 20;
	private static int coins;
	

	public static void addCoins(int addition) {
		coins = Math.clamp(coins+addition, 0, MAX_COINS);
	}
	
	public static int getCoins() {
		return coins;
	}
}
