package boggle.client.tools;

import javafx.scene.layout.GridPane;

public class UpdateGrid implements Runnable {
	private GridPane gp;
	private Frame[][] frames;
	private AnswerStack as;
	public UpdateGrid(GridPane gp, Frame[][] frames, AnswerStack as) {
		this.frames = frames;
		this.gp = gp;
		this.as = as;
		
	}
	@Override
	public void run() {
		for(int i = 0; i < 4; i++) {
			for(int j = 0; j < 4; j++) {
				gp.add(frames[i][j].getImg(), frames[i][j].getCol()+1, frames[i][j].getRow()+1);
			}
		}
		
		for(Frame[] fs: frames) {
			for(Frame f: fs) {
				f.getImg().setOnMouseClicked(e->{
					if(!f.isSelected() && as.select(f)) {
						f.setSelected(true);
						String pos[] = f.getImg_selected().getId().split("\\s+");
						gp.getChildren().remove(f.getImg());
						gp.add(f.getImg_selected(), Integer.parseInt(pos[0]), Integer.parseInt(pos[1]));
					}
				});
				
				f.getImg_selected().setOnMouseClicked(e->{
					if(f.isSelected() && as.deselect(f)) {
						f.setSelected(false);
						String pos[] = f.getImg().getId().split("\\s+");
						gp.getChildren().remove(f.getImg_selected());
						gp.add(f.getImg(), Integer.parseInt(pos[0]), Integer.parseInt(pos[1]));
					}
				});
			}
		}
	}

}
