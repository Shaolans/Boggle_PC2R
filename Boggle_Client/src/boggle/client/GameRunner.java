package boggle.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class GameRunner extends Thread {
	private BoggleWindow bw;
	private BufferedReader in;
	public GameRunner(BoggleWindow bw) {
		this.bw = bw;
		in = bw.getIn();
		try {
			in = new BufferedReader(new InputStreamReader(bw.getSocket().getInputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		try {
			while(!isInterrupted()) {
				String command;
				command = in.readLine();
				String[] info = command.split("/");
				System.out.println(info[0]);
				switch(info[0]) {
				case "BIENVENUE":
					break;
				case "CONNECTE":
					bw.getChatcontent().setText(bw.getChatcontent().getText()+info[1]+"VIENT DE SE CONNECTER A LA PARTIE\n");
					break;
				case "DECONNEXION":
					bw.getChatcontent().setText(bw.getChatcontent().getText()+info[1]+"VIENT DE SE DECONNECTER DE LA PARTIE\n");
					break;
				case "SESSION":
					break;
				case "VAINQUEUR":
					break;
				case "TOUR":
					break;
				case "MVALIDE":
					break;
				case "MINVALIDE":
					break;
				case "RFIN":
					break;
				case "BILANMOTS":
					break;
				case "RECEPTION":
					bw.getChatcontent().setText(bw.getChatcontent().getText()+"[Message public]: "+info[1]+"\n");
					break;
				case "PRECEPTION":
					bw.getChatcontent().setText(bw.getChatcontent().getText()+"("+info[1]+" -> "+bw.getUsername()+"): "+info[2]+"\n");
					break;
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
