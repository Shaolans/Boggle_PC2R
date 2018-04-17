package boggle.client.tools;

import javafx.scene.image.ImageView;

public class Frame {
	private int col;
	private int row;
	private char letter;
	private boolean selected;
	private ImageView img;
	private ImageView img_selected;
	
	public Frame(int col, int row, char letter, ImageView img, ImageView img_selected) {
		this.col = col;
		this.row = row;
		this.letter = letter;
		this.img = img;
		this.img_selected = img_selected;
		selected = false;
	}
	
	public boolean isSelected() {
		return selected;
	}


	public void setSelected(boolean selected) {
		this.selected = selected;
	}


	public ImageView getImg() {
		return img;
	}


	public void setImg(ImageView img) {
		this.img = img;
	}


	public ImageView getImg_selected() {
		return img_selected;
	}


	public void setImg_selected(ImageView img_selected) {
		this.img_selected = img_selected;
	}



	
	
	public char getLetter() {
		return letter;
	}
	
	public void setLetter(char letter) {
		this.letter = letter;
	}
	
	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

}
