package userApplication;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

//Class that enables the application to stop working after a fixed period of time passes
public class inputTimer {
	String input = "";
	
	TimerTask task = new TimerTask(){
		public void run() {
			if(input.isEmpty()) {
				System.out.println("Timout period reached. \nNow Exiting.");
				System.exit(0);
			}
		}
	};
	
	
	
	public String getInput() throws Exception{
		Timer timer = new Timer();
		timer.schedule(task, 30*1000);
		
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		input = in.readLine();
	
		return input;
	}
}
