package base;

import base.input.Preload;

public class SSLauncher {
	
	public static void main(String[] args) {
		Preload.check();
		Game game = new Game("Swift Shinobi", 1000, 650, 120);
		game.run();
	}

}
