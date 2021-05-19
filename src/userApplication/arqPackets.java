package userApplication;
import java.util.ArrayList;
import ithakimodem.Modem;
import java.io.FileWriter;

//Class that executes the ARQ protocol
public class arqPackets {
	String requestCodeQ;
	String requestCodeR;
	Modem modem;
	
	public arqPackets() {
		requestCodeQ = "";
		requestCodeR = "";
		modem = new Modem();
	}
	
	public arqPackets(String requestCodeQ, String requestCodeR, Modem modem) {
		this.requestCodeQ = requestCodeQ;
		this.requestCodeR = requestCodeR;
		this.modem = modem;
	}
	
	
	//Method that returns true or false based on whether the right or wrong package was sent
	public boolean getPacket(String requestCode) {
		modem.write((requestCode + "\r").getBytes());

		String response = "";
		String testPacket = "";
		for(;;) {
			int k;
			try {
				k = modem.read();
				if(k==-1) break;
				testPacket+=(char)k;
				if(testPacket.contains("<")) {				//Saving data from "<" till "PSTOP" 
					response+=(char)k;
					if(response.indexOf("PSTOP") != -1) {
						testPacket = "";
						break;
					};
				}
			}catch(Exception e) {
				System.out.println(e);
			}
		}
		
		int xor = 0;							//Execute XOR for each char of the package sent
		String fcbString = "";					//and compare it to the FCB sent with the package
		int fcb;
		for(int i = 1; i < 17; i++)
			xor = (xor^response.charAt(i));
		for(int i = 19; i < 22; i++)
			fcbString+=response.charAt(i);

		fcb = Integer.parseInt(fcbString);
		if(xor != fcb)
			return false;
		else
			return true;
	}
	
	//This method gives us two text files.
	//The first containing the time between each ACK request code and the Second containing the number of ACK and NACK codes
	public void getTimeDelay() {
		long currentTime = System.currentTimeMillis();
		long end = currentTime + (4 * 60 * 1000);
		ArrayList<Long> timeDelay = new ArrayList<Long>();
		boolean send = getPacket(requestCodeQ);
		int timesRcalled = 0;
		int timesQcalled = 0;
		
		//Loop that runs for 4 minutes
		do {
			if(send == false) {
				send = getPacket(requestCodeR);
				timesRcalled++;
			}else {
				send = getPacket(requestCodeQ);
				timeDelay.add((System.currentTimeMillis() - currentTime));
				timesQcalled++;
			}
			
			currentTime = System.currentTimeMillis();
		}while(currentTime < end);
		
		ArrayList<String> timeDelayString = new ArrayList<String>();
		for(int i = 0; i < timeDelay.size(); i++) 
			timeDelayString.add(timeDelay.get(i).toString());
		
		try {														
			FileWriter writer = new FileWriter("arqOutput1.txt");
			for(String str: timeDelayString) {
				writer.write(str + System.lineSeparator());
			}
			writer.close();
			}catch(Exception e) {
				System.out.println(e);
		}
		
		ArrayList<String> timesCalled = new ArrayList<String>();
		timesCalled.add(Integer.toString(timesRcalled));
		timesCalled.add(Integer.toString(timesQcalled));
		try {
			FileWriter writer = new FileWriter("arqOutput2.txt");
			for(String str: timesCalled) {
				writer.write(str + System.lineSeparator());
			}
			writer.close();
			}catch(Exception e) {
				System.out.println(e);
			}
	}
}
