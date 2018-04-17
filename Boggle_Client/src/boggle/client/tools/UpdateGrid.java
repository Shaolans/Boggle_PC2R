package boggle.client.tools;

import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class UpdateGrid implements Runnable {
	private GridPane gp;
	private Frame[][] frames;
	
	public UpdateGrid(GridPane gp, Frame[][] frames) {
		this.frames = frames;
		this.gp = gp;
		
	}
	@Override
	public void run() {
		for(int i = 0; i < 4; i++) {
			for(int j = 0; j < 4; j++) {
				ImageView img = new ImageView("file:letters_img/"+frames[i][j].getLetter()+".jpg");
				img.setFitWidth(75);
				img.setFitHeight(75);
				gp.add(img, frames[i][j].getCol()+1, frames[i][j].getRow()+1);
			}
		}
	}

}
