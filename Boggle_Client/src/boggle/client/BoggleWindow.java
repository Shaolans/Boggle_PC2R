package boggle.client;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import boggle.client.tools.ConnectInfo;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class BoggleWindow {
	private Socket socket;
	private BufferedReader in;
	private DataOutputStream out;
	private String username;
	private boolean sayAll;
	private GridPane grid;
	private Map<String, ImageView> img_database;
	private static final char letters[] = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
	private GameRunner gr;
	private TextArea chatcontent;
	
	public BoggleWindow(Stage stage) {
		username = "";
		in = null;
		out = null;
		socket = null;
		sayAll = true;
		
		stage.getIcons().add(new Image("file:icons/boggle_icon.jpg"));
		img_database = new HashMap<>();
		
		for(char l: letters) {
			ImageView img = new ImageView("file:letters_img/"+l+".jpg");
			img.setFitWidth(75);
			img.setFitHeight(75);
			img_database.put(l+"", img);
		}
		
		stage.setTitle("Boggle Game");
		VBox vbox = new VBox();
		HBox hbox = new HBox();
		
		MenuBar mb = new MenuBar();
		Menu menu = new Menu("Action");

		
		MenuItem connect = new MenuItem("Rejoindre un serveur");
		Dialog<ConnectInfo> infoserver = initConnectServer();
		connect.setOnAction(e->{
			Optional<ConnectInfo> result = infoserver.showAndWait();
			result.ifPresent(info->{
				try {
					socket = new Socket(info.getServer(), info.getPort());
					in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					out = new DataOutputStream(socket.getOutputStream());
					if(gr != null) gr.interrupt();
					gr = new GameRunner(this);
					gr.start();
				} catch (UnknownHostException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				username = info.getUser();
			});
		});
		
		MenuItem disconnect = new MenuItem("Déconnexion");
		disconnect.setOnAction(e->{
			try {
				
				out.writeChars("SORT/"+username+"/\n");
				if(gr != null) gr.interrupt();
				username = "";
				in.close();
				out.close();
				socket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
		});
		

		MenuItem quit = new MenuItem("Quitter");
		quit.setOnAction(e->{
			stage.close();
		});
		
		menu.getItems().addAll(connect, disconnect, quit);
		mb.getMenus().add(menu);
		
		
		VBox chat = new VBox();
		chatcontent = new TextArea();
		chatcontent.setPrefSize(400, 600);
		chatcontent.setEditable(false);
		
		
		HBox chattextbox = new HBox();
		

		TextField receiver = new TextField();
		receiver.setPromptText("Destinataire");
		receiver.setPrefWidth(80);
		receiver.setDisable(true);
		
		TextField chattext = new TextField();
		chattext.setPromptText("Envoyer un message");
		chattext.setPrefWidth(400);
		chattext.setPrefHeight(50);
		chattext.setOnKeyPressed(e->{
			if(e.getCode()==KeyCode.ENTER) {
				try {
					if(sayAll) {
						chatcontent.setText(chatcontent.getText()+username+": "+chattext.getText()+"\n");
						out.writeChars("ENVOI/"+chattext.getText()+"\n");	
					}else {
						chatcontent.setText(chatcontent.getText()+"("+username+" -> "+receiver.getText()+"): "+chattext.getText()+"\n");
						out.writeChars("PENVOI/"+receiver.getText()+"/"+chattext.getText()+"\n");
						
					}
					chattext.clear();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		ChoiceBox<String> choicemsgmode = new ChoiceBox(FXCollections.observableArrayList("Tous","Privé"));
		choicemsgmode.setValue("Tous");
		choicemsgmode.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Object>() {
			@Override
			public void changed(@SuppressWarnings("rawtypes") ObservableValue ov, Object oldSelected, Object newSelected) {
				if((int)ov.getValue()==0) {
					sayAll = true;
					receiver.setDisable(true);
				}else {
					sayAll = false;
					receiver.setDisable(false);
				}
			}
		});
		
		VBox optionchat = new VBox();
		optionchat.getChildren().addAll(choicemsgmode, receiver);
		
		chattextbox.getChildren().addAll(optionchat, chattext);
		
		chat.getChildren().addAll(chatcontent, chattextbox);

		grid = new GridPane();
		init_grid(grid);
		
		VBox game = new VBox();
		final TextField combinaison = new TextField();
		TextField word = new TextField();
		word.setPromptText("Envoyer une réponse");
		word.setOnKeyPressed(e->{
			if(e.getCode()==KeyCode.ENTER) {
				if(!word.getText().equals("") && !combinaison.getText().equals("")) {
					try {
						out.writeChars("TROUVE/"+word.getText()+"/"+combinaison.getText()+"\n");
						word.clear();
						combinaison.clear();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				
			}
		});
		
		
		combinaison.setPromptText("Envoyer la combinaison");
		combinaison.setOnKeyPressed(e->{
			if(e.getCode()==KeyCode.ENTER) {
				if(!word.getText().equals("") && !combinaison.getText().equals("")) {
					try {
						out.writeChars("TROUVE/"+word.getText()+"/"+combinaison.getText()+"\n");
						word.clear();
						combinaison.clear();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				
			}
		});
		
		
		
		game.getChildren().addAll(grid, word, combinaison);
		hbox.getChildren().addAll(chat, game);
		vbox.getChildren().addAll(mb, hbox);
		stage.setScene(new Scene(vbox));
		stage.show();
	}
	
	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public BufferedReader getIn() {
		return in;
	}

	public void setIn(BufferedReader in) {
		this.in = in;
	}

	public DataOutputStream getOut() {
		return out;
	}

	public void setOut(DataOutputStream out) {
		this.out = out;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public boolean isSayAll() {
		return sayAll;
	}

	public void setSayAll(boolean sayAll) {
		this.sayAll = sayAll;
	}

	public GridPane getGrid() {
		return grid;
	}

	public void setGrid(GridPane grid) {
		this.grid = grid;
	}

	public Map<String, ImageView> getImg_database() {
		return img_database;
	}

	public void setImg_database(Map<String, ImageView> img_database) {
		this.img_database = img_database;
	}

	public GameRunner getGr() {
		return gr;
	}

	public void setGr(GameRunner gr) {
		this.gr = gr;
	}

	public TextArea getChatcontent() {
		return chatcontent;
	}

	public void setChatcontent(TextArea chatcontent) {
		this.chatcontent = chatcontent;
	}

	public static char[] getLetters() {
		return letters;
	}

	//Creation du popup de connexion
	public static Dialog<ConnectInfo> initConnectServer(){
		Dialog<ConnectInfo> dialog = new Dialog<>();
		dialog.setTitle("Connexion Serveur");
		dialog.setHeaderText("Veuillez spécifier tout les champs.");
		
		ImageView icon = new ImageView("file:icons/server_icon.png");
		icon.setFitWidth(100);
		icon.setFitHeight(100);
		dialog.setGraphic(icon);
		
		Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image("file:icons/server_icon.png"));
		
		ButtonType loginButtonType = new ButtonType("Connexion", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);
		
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));
		
		
		TextField server = new TextField();
		server.setPromptText("Adresse serveur");
		TextField port = new TextField();
		port.setPromptText("Port");
		TextField username = new TextField();
		username.setPromptText("Utilisateur");
		

		grid.add(new Label("Adresse serveur:"), 0, 0);
		grid.add(server, 1, 0);
		grid.add(new Label("Port:"), 0, 1);
		grid.add(port, 1, 1);
		grid.add(new Label("Utilisateur:"), 0, 2);
		grid.add(username, 1, 2);
		
		dialog.getDialogPane().setContent(grid);
		
		dialog.setResultConverter(dialogButton -> {
		    if (dialogButton == loginButtonType) {
		        return new ConnectInfo(server.getText(), Integer.parseInt(port.getText()), username.getText());
		    }
		    return null;
		});
		
		return dialog;
		
	}
	
	public static void init_grid(GridPane grid) {
		ImageView img;
		
		img = new ImageView("file:grid_init\\A.jpg");
		img.setFitWidth(75);
		img.setFitHeight(75);
		grid.add(img, 0, 1);
		
		img = new ImageView("file:grid_init\\B.jpg");
		img.setFitWidth(75);
		img.setFitHeight(75);
		grid.add(img, 0, 2);
		
		img = new ImageView("file:grid_init\\C.jpg");
		img.setFitWidth(75);
		img.setFitHeight(75);
		grid.add(img, 0, 3);
		
		img = new ImageView("file:grid_init\\D.jpg");
		img.setFitWidth(75);
		img.setFitHeight(75);
		grid.add(img, 0, 4);
		
		img = new ImageView("file:grid_init\\1.png");
		img.setFitWidth(75);
		img.setFitHeight(55);
		grid.add(img, 1, 0);
		
		img = new ImageView("file:grid_init\\2.png");
		img.setFitWidth(75);
		img.setFitHeight(55);
		grid.add(img, 2, 0);
		
		img = new ImageView("file:grid_init\\3.png");
		img.setFitWidth(75);
		img.setFitHeight(55);
		grid.add(img, 3, 0);
		
		img = new ImageView("file:grid_init\\4.png");
		img.setFitWidth(75);
		img.setFitHeight(55);
		grid.add(img, 4, 0);
		
	}
	
}
