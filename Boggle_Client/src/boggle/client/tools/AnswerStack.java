package boggle.client.tools;


import java.util.ArrayList;
import java.util.List;

import boggle.client.BoggleWindow;
import javafx.scene.control.TextField;

public class AnswerStack {
	private BoggleWindow bw;
	private List<Frame> stack;
	
	public AnswerStack(BoggleWindow bw) {
		this.bw = bw;
		stack = new ArrayList<>();
	}
	
	public boolean select(Frame f) {
		TextField comb = bw.getCombinaison();
		TextField word = bw.getWord();
		word.setText(word.getText()+f.getLetter());
		int row = f.getRow()+1;
		int col = f.getCol()+1;
		
		switch(row) {
		case 1:
			comb.setText(comb.getText()+"A"+col);
			break;
		case 2:
			comb.setText(comb.getText()+"B"+col);
			break;
		case 3:
			comb.setText(comb.getText()+"C"+col);
			break;
		case 4:
			comb.setText(comb.getText()+"D"+col);
			break;
		}
		
		stack.add(0, f);
		return true;
		
	}
	
	public BoggleWindow getBw() {
		return bw;
	}

	public void setBw(BoggleWindow bw) {
		this.bw = bw;
	}

	public List<Frame> getStack() {
		return stack;
	}

	public void setStack(List<Frame> stack) {
		this.stack = stack;
	}

	public boolean deselect(Frame f) {
		if(stack.isEmpty() || stack.get(0) != f) return false;
		TextField comb = bw.getCombinaison();
		TextField word = bw.getWord();
		word.setText(word.getText().substring(0, word.getText().length()-1));
		comb.setText(comb.getText().substring(0, comb.getText().length()-2));
		stack.remove(0);
		return true;
	}
	
	public void clear() {
		stack.clear();
	}
}
