package boggle.client;

import javafx.application.Application;
import javafx.stage.Stage;

public class BoggleMain extends Application {
	private static String[] arguments;

	@Override
	public void start(Stage stage) throws Exception {
		BoggleWindow bw = new BoggleWindow(stage);
		if(arguments.length > 0 && arguments[0].equals("-serveur") && arguments[2].equals("-port")) {
			bw.commandLineConnect(arguments[1], Integer.parseInt(arguments[3]));
		}
	}

    public static void main(String[] args) {
    	arguments = args;
    	launch(args);
    }



}
