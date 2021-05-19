package userApplication;

import java.util.ArrayList;
import ithakimodem.Modem;
import java.io.FileWriter;

//Class that executes the echo request
public class echoPackets {
	String requestCode;
	Modem modem;
	
	
	public echoPackets() {
		requestCode = "";
		modem = new Modem();
	}
	
	public echoPackets(String requestCode, Modem modem) {
		this.requestCode = requestCode;
		this.modem = modem;
	}
	

	
	public void getEchoPackets(){
		ArrayList<Long> timeDelay = new ArrayList<Long>();
		int k;
		long currentTime, end;
		currentTime = System.currentTimeMillis();
		end = (System.currentTimeMillis()) + (4 * 60 * 1000);	
		do{																//This part runs for 4 minutes
			String echoPacket = "";										
			modem.write((requestCode + "\r").getBytes());
			
			for(;;) {
				try {
					k = modem.read();
					if(k == -1) {
						break;
					}
					echoPacket += (char)k;
					if(echoPacket.indexOf("PSTOP") != -1)
						break;
					
				}catch(Exception e){
					System.out.println(e);
					break;
				}
			}
			timeDelay.add((System.currentTimeMillis()- currentTime));	//Before it starts over the time that elapsed from sending the request
			currentTime = System.currentTimeMillis();					//to getting each packet is added to an arrayList
		}while(currentTime < end);
		
		ArrayList<String> timeDelayString = new ArrayList<String>();
		for(int i = 0; i < timeDelay.size(); i++) 
			timeDelayString.add(timeDelay.get(i).toString());
		
		try {
		FileWriter writer = new FileWriter("echoOutput.txt");			//The Contents of the arrayList mentioned above
		for(String str: timeDelayString) {								//are given to the user in a .txt file
			writer.write(str + System.lineSeparator());
		}
		writer.close();
		}catch(Exception e) {
			System.out.println(e);
		}
	}
}
