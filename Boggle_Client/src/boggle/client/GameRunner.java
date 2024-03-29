package boggle.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import boggle.client.tools.AnswerStack;
import boggle.client.tools.Frame;
import boggle.client.tools.UpdateGrid;
import javafx.application.Platform;
import javafx.scene.control.TextArea;
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
				System.out.println("command: "+command);
				String[] info = command.split("/");
				if(info.length == 0) continue;
				switch(info[0]) {
				case "BIENVENUE":

					String[] scores = info[2].split("[*]");
					writeTextArea(bw.getSystem(),"---------- SCORE ----------\nNombre de tirages : "+scores[0]+"\n");
					for(int i = 1; i < scores.length; i+=2) {
						writeTextArea(bw.getSystem(),"Utilisateur : "+scores[i]+"\t Points : "+scores[i+1]+"\n");
					}

					if(info[1].length()==0) break;
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
					Platform.runLater(() -> {
						bw.getCombinaison().setDisable(false);
						bw.getWord().setDisable(false);
					});


					break;
				case "CONNECTE":
					writeTextArea(bw.getChatcontent(),"[Système] "+info[1]+" vient de se connecter.\n");
					writeTextArea(bw.getSystem(),info[1]+" vient de se connecter.\n");
					break;
				case "DECONNEXION":
					writeTextArea(bw.getChatcontent(),"[Système] "+info[1]+" vient de se déconnecter.\n");
					writeTextArea(bw.getSystem(),info[1]+" vient de se déconnecter.\n");
					break;
				case "SESSION":
					writeTextArea(bw.getSystem(),"---------- DEBUT DE SESSION ----------\n");
					writeTextArea(bw.getSystem(),"La partie va commencer dans environ 10 secondes.\n");
					break;
				case "VAINQUEUR":

					Platform.runLater(()->{
						bw.getGrid().getChildren().clear();
						BoggleWindow.init_grid(bw.getGrid());
					});

					String []scoresfin = info[1].split("[*]");
					writeTextArea(bw.getSystem(),"---------- VAINQUEUR ----------\n");
					writeTextArea(bw.getSystem(),"Nombre total de tours : "+scoresfin[0]+"\n");
					for(int i = 1; i < scoresfin.length; i+=2) {
						writeTextArea(bw.getSystem(),"Utilisateur : "+scoresfin[i]+"\t Points : "+scoresfin[i+1]+"\n");
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
					Platform.runLater(() -> {
						bw.getCombinaison().setDisable(false);
						bw.getWord().setDisable(false);
					});
					writeTextArea(bw.getSystem(),"---------- TOUR SUIVANT ----------\n");
					break;
				case "MVALIDE":
					writeTextArea(bw.getSystem(),"Le mot "+info[1]+" est valide\n");
					break;
				case "MINVALIDE":
					writeTextArea(bw.getSystem(),"Le mot est invalide\nRAISON : "+info[1]+"\n");
					break;
				case "RFIN":
					writeTextArea(bw.getSystem(),"---------- FIN DU TOUR ----------\n");
					Platform.runLater(() -> {
						bw.getCombinaison().clear();
						bw.getCombinaison().setDisable(true);
						bw.getWord().clear();
						bw.getWord().setDisable(true);
					});
					
					break;
				case "BILANMOTS":
					String[] bilanscores = info[2].split("[*]");
					writeTextArea(bw.getSystem(),"---------- BILAN DU TOUR ----------\n");
					writeTextArea(bw.getSystem(),"Nombre de tours : "+bilanscores[0]+"\n");
					String[] bilanmots = info[1].split("[*]");
					writeTextArea(bw.getSystem(),"Mots proposés et validé :\n");
					for(String mots: bilanmots) {
						writeTextArea(bw.getSystem(),"\t-"+mots+"\n");
					}

					writeTextArea(bw.getSystem(),"---------- SCORE ----------\n");
					for(int i = 1; i < bilanscores.length; i+=2) {
						writeTextArea(bw.getSystem(),"Utilisateur : "+bilanscores[i]+"\t Points : "+bilanscores[i+1]+"\n");
					}

					break;
				case "RECEPTION":
					if(info.length == 1) break;
					writeTextArea(bw.getChatcontent(),"[Message public] : "+info[1]+"\n");
					break;
				case "PRECEPTION":
					if(info.length == 1) break;
					writeTextArea(bw.getChatcontent(),"("+info[2]+" -> "+bw.getUsername()+") : "+info[1]+"\n");
					break;
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void writeTextArea(TextArea ta, String content) {
		Platform.runLater(() -> ta.appendText(content));
	}


}
