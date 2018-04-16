package boggle.client;

import javafx.application.Application;
import javafx.stage.Stage;

public class BoggleMain extends Application {
	@Override
	public void start(Stage stage) throws Exception {
		BoggleWindow bw = new BoggleWindow(stage);
	}
	
    public static void main(String[] args) { 
    	launch(args); 
    }
    
    

}
