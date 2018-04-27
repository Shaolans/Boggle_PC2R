package boggle.cheater.client;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BoggleCheater {

	public static void main(String[] args) throws NumberFormatException, UnknownHostException, IOException {
		if(args.length < 4 || !args[0].equals("-serveur") || !args[2].equals("-port")) {
			System.out.println("Veuillez lancer le BoggleCheater comme ceci: java BoggleCheater -serveur hostname -port numport");
			return;
		}
		
		BoggleCheater bc = new BoggleCheater(args[1], Integer.parseInt(args[3]));
		Talker t = new Talker(bc);
		bc.run();
		t.run();
			

	}
	
	Set<String> dictionnary;
	private Socket socket;
	private PrintWriter pw;
	private BufferedReader bf;
	private String name;
	private static String names[] = {"Ling-Chun","Amel","Bernard","Jean","PasSuspect","NotCheater","Player"};
	private List<String> talklines;
	
	public BoggleCheater(String server, int port) throws UnknownHostException, IOException {
		dictionnary = loadDictionnary("dictionnary/glaff-dictionnary-formatted.txt");
		talklines = loadlines("talklines/talklines.txt");
		socket = new Socket(server, port);
		pw = new PrintWriter(socket.getOutputStream());
		bf = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		int numname = (int)(Math.random()*names.length);
		name = names[numname];
		send("CONNEXION/"+name+"/\r\n");
	}
	
	public List<String> loadlines(String path){
		try {
			BufferedReader bfr = new BufferedReader(new FileReader(path));
			String word = bfr.readLine();
			List<String> lines = new ArrayList<>();
			while(word != null) {
				lines.add(word);
				word = bfr.readLine();
			}
			bfr.close();
			return lines;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<String> getTalkLines(){
		return talklines;
	}
	
	public synchronized void send(String msg) {
		pw.write(msg);
		pw.flush();
	}
	
	public void run() throws IOException {
		while(true) {
			String command = bf.readLine();
			String []info = command.split("/");
			if(info.length == 0) continue;
			switch(info[0]) {
			case "TOUR":
				Map<String, String> solutions = computeWords(info[1], dictionnary);
				for(Map.Entry<String, String> entry: solutions.entrySet()) {
					String msg = "TROUVE/"+entry.getKey()+"/"+entry.getValue();
					System.out.println("TO SERVER: "+msg);
					send(msg+"/\r\n");
				}
				break;
			default:
				String msg = "ENVOI/"+talklines.get((int)(Math.random()*talklines.size()));
				System.out.println("TO SERVER: "+msg);
				send(msg+"/\r\n");
			}
		}
	}
	
	public static Map<String, String> computeWords(String grid, Set<String> dictionnary){
		char[][] boggle = new char[4][4];
		for(int i = 0; i < 4; i++) {
			for(int j = 0; j < 4; j++) {
				boggle[i][j] = grid.charAt(i*4+j);
			}
		}
		
		boolean visit[][] = new boolean[4][4];
		Map<String, String> words = new HashMap<>();
		for(int i = 0; i < 4; i++) {
			for(int j = 0; j < 4; j++) {
				computeSolution(i, j, "", "", words, dictionnary, boggle, cloneVisit(visit));
				System.out.println("COMPUTE "+i+" "+j+" DONE");
			}
		}
		
		
		return words;
	}
	
	public static Set<String> loadDictionnary(String path){
		try {
			BufferedReader bf = new BufferedReader(new FileReader(path));
			String words = bf.readLine();
			Set<String> dictionnary = new HashSet<String>();
			while(words != null) {
				dictionnary.add(words.toUpperCase());
				words = bf.readLine();
			}
			bf.close();
			return dictionnary;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void computeSolution(int row, int col, String currentwords, String currentpath, Map<String, String> words, Set<String> dictionnary, char[][] boggle, boolean[][] visit) {
		if(currentwords.length() >= 3 && dictionnary.contains(currentwords)) words.put(currentwords, currentpath);
		visit[row][col] = true;
		
		if(row+1 < boggle.length && !visit[row+1][col]) {
			computeSolution(row+1, col, currentwords+boggle[row+1][col], currentpath+findPosition(row+1, col), words, dictionnary, boggle, cloneVisit(visit));
		}
		
		if(row-1 >= 0 && !visit[row-1][col]) {
			computeSolution(row-1, col, currentwords+boggle[row-1][col], currentpath+findPosition(row-1, col), words, dictionnary, boggle, cloneVisit(visit));
		}
		
		if(col+1 < boggle[0].length && !visit[row][col+1]) {
			computeSolution(row, col+1, currentwords+boggle[row][col+1], currentpath+findPosition(row, col+1), words, dictionnary, boggle, cloneVisit(visit));
		}
		
		if(col-1 >= 0 && !visit[row][col-1]) {
			computeSolution(row, col-1, currentwords+boggle[row][col-1], currentpath+findPosition(row, col-1), words, dictionnary, boggle, cloneVisit(visit));
		}
		
		if(row+1 < boggle.length && col+1 < boggle[0].length && !visit[row+1][col+1]) {
			computeSolution(row+1, col+1, currentwords+boggle[row+1][col+1], currentpath+findPosition(row+1, col+1), words, dictionnary, boggle, cloneVisit(visit));
		}
		
		if(row-1 >= 0 && col-1 >= 0 && !visit[row-1][col-1]) {
			computeSolution(row-1, col-1, currentwords+boggle[row-1][col-1], currentpath+findPosition(row-1, col-1), words, dictionnary, boggle, cloneVisit(visit));
		}
		
		if(row+1 < boggle.length && col-1 >= 0 && !visit[row+1][col-1]) {
			computeSolution(row+1, col-1, currentwords+boggle[row+1][col-1], currentpath+findPosition(row+1, col-1), words, dictionnary, boggle, cloneVisit(visit));
		}
		
		if(row-1 >= 0 && col+1 < boggle[0].length && !visit[row-1][col+1]) {
			computeSolution(row-1, col+1, currentwords+boggle[row-1][col+1], currentpath+findPosition(row-1, col+1), words, dictionnary, boggle, cloneVisit(visit));
		}
		
	}
	
	public static boolean[][] cloneVisit(boolean[][] visit){
		boolean[][] clone = new boolean[visit.length][visit[0].length];
		for(int i = 0; i < visit.length; i++) {
			for(int j = 0; j < visit[0].length; j++) {
				clone[i][j] = visit[i][j];
			}
		}
		
		return clone;
	}
	
	public static String findPosition(int row, int col) {
		String position = "";
		switch(row) {
		case 0:
			position = "A";
			break;
		case 1:
			position = "B";
			break;
		case 2:
			position = "C";
			break;
		case 3:
			position = "D";
			break;
		}
		
		position += (col+1);
		return position;
	}
	
	public static void normalizeGlaff() {
		try {
			BufferedReader bf = new BufferedReader(new FileReader("dictionnary/glaff-1.2.2.txt"));
			FileWriter f = new FileWriter("dictionnary/glaff-dictionnary-formatted.txt");
			String words = bf.readLine();
			while(words != null) {
				String[] split = words.split("[|]");
				split[0] = removeAccents(split[0]);
				f.write(split[0]+"\n");
				words = bf.readLine();
			}
			bf.close();
			f.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String removeAccents(String text) { 
	 return text == null ? null 
	 : Normalizer.normalize(text, Form.NFD) 
	 .replaceAll("\\p{InCombiningDiacriticalMarks}+", ""); 
	}
}
