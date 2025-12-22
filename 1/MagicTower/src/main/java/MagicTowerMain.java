import java.io.File;

public class MagicTowerMain {
	static GameData gameData;
	static GameControl gameControl;
	static Menu menu;
	public static void main(String[] args) {

		gameData = new GameData();
		gameData.readMapFromFile("src/main/resources/Map.in");

		menu = new Menu(gameData);
		menu.loadMenu("src/main/resources/Menu.XML");
		gameControl = new GameControl(gameData, menu);
		gameData.printMap();
		gameControl.gameStart();
	}
}