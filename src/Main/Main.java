package Main;

public class Main {
	private static void run() {
		init();
		loop();
		clean();
		//commmit10
	}
	
	private static void init() {
		Window.init();
	}
	
	private static void loop() {
		while (!Window.shouldClose()) { 
			Window.loop_before();
			Window.loop_after();			
		}
	}
	
	private static void clean() {
		Window.clean();
	}
	
	public static void main(String [] args) {
		run();
	}
}
