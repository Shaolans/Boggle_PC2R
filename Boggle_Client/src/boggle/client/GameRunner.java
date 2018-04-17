package boggle.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import boggle.client.tools.AnswerStack;
import boggle.client.tools.Frame;
import boggle.client.tools.UpdateGrid;
import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class GameRunner extends Thread {
	private BoggleWindow bw;
	private BufferedReader in;
	private Frame[][] frames;
	private AnswerStack as;
	
	public GameRunner(BoggleWindow bw) {
		this.bw = bw;
		in = bw.getIn();
		try {
			in = new BufferedReader(new InputStreamReader(bw.getSocket().getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		frames = bw.getFrames();
		as = bw.getAs();
	}
	
	@Override
	public void run() {
		try {
			ImageView img;
			ImageView img_s;
			while(!isInterrupted()) {
				String command;
				command = in.readLine();
				String[] info = command.split("/");
				switch(info[0]) {
				case "BIENVENUE":
					GridPane gpb = bw.getGrid();
					for(int i = 0; i < 16; i++) {
						img = new ImageView("file:letters_img/"+info[1].charAt(i)+".jpg");
						img.setFitWidth(75);
						img.setFitHeight(75);
						img.setId(((i%4)+1)+" "+((i/4)+1)+" n");
						img_s = new ImageView("file:letters_img/"+info[1].charAt(i)+"_s.jpg");
						img_s.setFitWidth(75);
						img_s.setFitHeight(75);
						img_s.setId(((i%4)+1)+" "+((i/4)+1)+" s");
						frames[i%4][i/4] = new Frame(i%4, i/4, info[1].charAt(i), img, img_s);
					}
					Platform.runLater(new UpdateGrid(gpb, frames, as));
					bw.getCombinaison().setDisable(false);
					bw.getWord().setDisable(false);
					
					String[] scores = info[2].split("[*]");
					bw.getSystem().setText(bw.getSystem().getText()+"SCORE:\nNOMBRE DE TIRAGE: "+info[1]+"\n");
					for(int i = 1; i < scores.length; i+=2) {
						bw.getSystem().setText(bw.getSystem().getText()+"Utilisateur: "+scores[i]+"\t Points: "+scores[i+1]+"\n");
					}
					
					break;
				case "CONNECTE":
					bw.getChatcontent().setText(bw.getChatcontent().getText()+"[Système] "+info[1]+" vient de se connecter.\n");
					bw.getSystem().setText(bw.getSystem().getText()+info[1]+" vient de se connecter.\n");
					break;
				case "DECONNEXION":
					bw.getChatcontent().setText(bw.getChatcontent().getText()+"[Système] "+info[1]+" vient de se déconnecter.\n");
					bw.getSystem().setText(bw.getSystem().getText()+info[1]+" vient de se déconnecter.\n");
					break;
				case "SESSION":
					bw.getSystem().setText(bw.getSystem().getText()+"Début de session\n");
					break;
				case "VAINQUEUR":
					String []scoresfin = info[1].split("[*]");
					bw.getSystem().setText(bw.getSystem().getText()+"Nombre total de tour: "+scoresfin[0]+"\n");
					for(int i = 1; i < scoresfin.length; i+=2) {
						bw.getSystem().setText(bw.getSystem().getText()+"Utilisateur: "+scoresfin[i]+"\t Points: "+scoresfin[i+1]+"\n");
					}
					break;
				case "TOUR":
					GridPane gp = bw.getGrid();
					for(int i = 0; i < 16; i++) {
						img = new ImageView("file:letters_img/"+info[1].charAt(i)+".jpg");
						img.setFitWidth(75);
						img.setFitHeight(75);
						img.setId(((i%4)+1)+" "+((i/4)+1)+" n");
						img_s = new ImageView("file:letters_img/"+info[1].charAt(i)+"_s.jpg");
						img_s.setFitWidth(75);
						img_s.setFitHeight(75);
						img_s.setId(((i%4)+1)+" "+((i/4)+1)+" s");
						frames[i%4][i/4] = new Frame(i%4, i/4, info[1].charAt(i), img, img_s);
					}
					Platform.runLater(new UpdateGrid(gp, frames, as));
					bw.getCombinaison().setDisable(false);
					bw.getWord().setDisable(false);
					bw.getSystem().setText(bw.getSystem().getText()+"Tour suivant\n");
					break;
				case "MVALIDE":
					bw.getSystem().setText(bw.getSystem().getText()+"Le mot "+info[1]+" est valide\n");
					break;
				case "MINVALIDE":
					bw.getSystem().setText(bw.getSystem().getText()+"Le mot est invalide\nRAISON: "+info[1]+"\n");
					break;
				case "RFIN":
					bw.getSystem().setText(bw.getSystem().getText()+"Fin du tour\n");
					bw.getCombinaison().clear();
					bw.getCombinaison().setDisable(true);
					bw.getWord().clear();
					bw.getWord().setDisable(true);
					break;
				case "BILANMOTS":
					String[] bilanscores = info[2].split("[*]");
					bw.getSystem().setText(bw.getSystem().getText()+"Bilan du tour\n");
					bw.getSystem().setText(bw.getSystem().getText()+"Nombre de tours: "+bilanscores[0]+"\n");
					String[] bilanmots = info[1].split("[*]");
					for(int i = 0; i < bilanmots.length; i+=2) {
						String player = bilanmots[i];
						String[] propositions = bilanmots[i+1].split("[&]");
						bw.getSystem().setText(bw.getSystem().getText()+"Mots proposés et validé par "+player+": \n");
						for(String mots: propositions) {
							bw.getSystem().setText(bw.getSystem().getText()+"\t-"+mots+"\n");
						}
					}
					
					for(int i = 1; i < bilanscores.length; i+=2) {
						bw.getSystem().setText(bw.getSystem().getText()+"Utilisateur: "+bilanscores[i]+"\t Points: "+bilanscores[i+1]+"\n");
					}
					
					break;
				case "RECEPTION":
					bw.getChatcontent().setText(bw.getChatcontent().getText()+"[Message public]: "+info[1]+"\n");
					break;
				case "PRECEPTION":
					bw.getChatcontent().setText(bw.getChatcontent().getText()+"("+info[2]+" -> "+bw.getUsername()+"): "+info[1]+"\n");
					break;
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
