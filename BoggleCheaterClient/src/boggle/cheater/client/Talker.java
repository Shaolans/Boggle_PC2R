package boggle.cheater.client;

public class Talker extends Thread {
	private BoggleCheater bc;
	
	public Talker(BoggleCheater bc) {
		this.bc = bc;
	}
	
	@Override
	public void run() {
		try {
			while(true) {
				
				Thread.sleep(((long)Math.random()*10000));
				if(Math.random() > 0.5) {
					String msg = "ENVOI/"+bc.getTalkLines().get((int)(Math.random()*bc.getTalkLines().size()));
					System.out.println("TO SERVER: "+msg);
					bc.send(msg+"/\r\n");
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
