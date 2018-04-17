package boggle.client.tools;

import javafx.scene.image.ImageView;

public class Frame {
	private int col;
	private int row;
	private char letter;
	
	public Frame(int col, int row, char letter) {
		this.col = col;
		this.row = row;
		this.letter = letter;
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
