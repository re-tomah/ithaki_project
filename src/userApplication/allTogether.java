/*package userApplication;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;

import ithakimodem.Modem;

public class virtualModem {
	public static void main(String[] param){ 
		(new virtualModem()).demo();
	}
	
	public void demo(){
		
		//Modem Object Created
		int k;
		Modem modem;
		modem=new Modem(); modem.setSpeed(80000); modem.setTimeout(5000);
		modem.open("ithaki");
		
		String keyInput = "";
		
		for (;;) {
			try {
				k=modem.read();
				if (k==-1) break; System.out.print((char)k);
			} catch (Exception x) {
				break;
			} 
		}
		int i = 0;
		System.out.println("Ithaki server is waiting for your commands. "
				+ "\nThis application timeouts after 30seconds of being idle");
		
		//Loop that continues to run while user enter commands
		do {
			if(i != 0) {
				System.out.println("Ready for your next request.");
			}
			i++;
			
			try {
				inputTimer initial = new inputTimer();
				keyInput = initial.getInput();
				
				System.out.println(keyInput);
				//Depending on what the user presses Application responds accordingly
				if(keyInput.isEmpty()){
					System.out.println("No input");
				}else if(keyInput.charAt(0) == 'E'){
					echoPackets echoPacket = new echoPackets(keyInput, modem);
					echoPacket.getEchoPackets();	
					
				}else if((keyInput.charAt(0) == 'M') || (keyInput.charAt(0) == 'G')) {
					System.out.println("Press 'PTZ' or 'FIX' to either get Fixed image or Pan-TiltZoom image.");
					inputTimer secondary = new inputTimer();
					String secondKeyInput = secondary.getInput();
					if(secondKeyInput.equals("PTZ")) {
						secondKeyInput = keyInput + "CAM=PTZ";
						System.out.println("Press 'L', 'R', 'U', 'D' if you want your camera to move left, right, up or down. "
								+ "\nYou can choose 'M' or 'C' if you want to save cameras angle for future shots or bring it back to its former angle");
						inputTimer third = new inputTimer();
						String thirdKeyInput = third.getInput();
						if((!thirdKeyInput.equals("L")) || (!thirdKeyInput.equals("R")) || (!thirdKeyInput.equals("U"))
								|| (!thirdKeyInput.equals("D")) || (!thirdKeyInput.equals("M")) || (!thirdKeyInput.equals("C"))){
							imagePacketsM imagePacketM = new imagePacketsM((secondKeyInput + "DIR=" + thirdKeyInput), modem);
							imagePacketM.getImagePacketsM();

						}else if(thirdKeyInput.equals("")){
							imagePacketsM imagePacketM = new imagePacketsM(secondKeyInput, modem);
							imagePacketM.getImagePacketsM();
						}else {
							System.out.println("Something probably went wrong. Please try again");
						}

					}else if((secondKeyInput.equals("")) || (secondKeyInput.equals("FIX"))) {
						imagePacketsM imagePacketM = new imagePacketsM(keyInput, modem);
						imagePacketM.getImagePacketsM();

					}else {
						System.out.println("Something probably went wrong. Please try again");
					}

				}else if(keyInput.charAt(0) == 'P') {
					ArrayList<String> tempArray = new ArrayList<String>();
					gpsPackets gpsPacket = new gpsPackets(keyInput, modem, tempArray);
					gpsPacket.getTPackets("R=1011001");
					gpsPacket.getTPackets("R=1010101");
					gpsPacket.getTPackets("R=1022001");
					gpsPacket.getTPackets("R=1012001");
					gpsPacket.getImageGPS();
				}else if(keyInput.equals("ARQ")) {
					System.out.println("Please enter Q code and R code");
					inputTimer secondary = new inputTimer();
					String secondKeyInput = secondary.getInput();
					inputTimer third = new inputTimer();
					String thirdKeyInput = third.getInput();
					if((secondKeyInput.charAt(0) == 'R') && (thirdKeyInput.charAt(0) == 'Q')) {
						String temp = secondKeyInput;
						secondKeyInput = thirdKeyInput;
						thirdKeyInput = temp;
					}
					arqPackets test = new arqPackets(secondKeyInput, thirdKeyInput, modem);
					test.getTimeDelay();
				}else if((keyInput.charAt(0) == 'Q') || (keyInput.charAt(0) == 'R')) {
					if(keyInput.charAt(0) == 'Q') {
						System.out.println("Please enter NACK result code now");
						inputTimer secondary = new inputTimer();
						String secondKeyInput = secondary.getInput();
						arqPackets test = new arqPackets(keyInput, secondKeyInput, modem);
						test.getTimeDelay();
					}else if(keyInput.charAt(0) == 'R') {
						System.out.println("Please enter ACK result code now");
						inputTimer secondary = new inputTimer();
						String secondKeyInput = secondary.getInput();
						arqPackets test = new arqPackets(keyInput, secondKeyInput, modem);
						
						test.getTimeDelay();
					}
				}
			}catch(Exception e) {
				System.out.println(e);
			}
			if(keyInput.equals(""))
				System.out.println("No user Input. Now Exiting");
		}while(!keyInput.equals(""));
		
		
		modem.close(); 
	}
	
	//
	//
	//
	
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
}

//
//
//

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
			timeDelay.add((System.currentTimeMillis()- currentTime));	//Before it starts over with the loop, 
																		//	the time that elapsed from sending the request
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

//
//
//

//Class that executes image request(with and without errors)
public class imagePacketsM {
	String requestCode;
	Modem modem;
	static int timesCalled;
	
	public imagePacketsM() {
		requestCode = "";
		modem = new Modem();
	}
	public imagePacketsM(String requestCode, Modem modem) {
		this.requestCode = requestCode;
		this.modem = modem;
	}
	
	public void getImagePacketsM(){
		int k;
		timesCalled++;
		ArrayList<Byte> preImage = new ArrayList<Byte>();
		modem.write((requestCode + "\r").getBytes());
		byte[] termination = {(byte)0xFF, (byte)0xD9};			//Termination sequence for jpeg files
		int buffer_termination_index = 0;
		
		for(;;) {
			try {
				k = modem.read();
				if(k == -1)
					break;
				
				preImage.add((byte)k);
				if((byte)k == termination[buffer_termination_index]) {			//Stops receiving packets from modem after 
					buffer_termination_index++;									//termination sequence is reached
					if(buffer_termination_index >= termination.length) break;
				}else {
					buffer_termination_index = 0;
				}
			}catch(Exception e){
				break;
			}
		}
		
		byte[] pixels = new byte[preImage.size()];
		for(int i = 0; i < (preImage.size()); i++)
			pixels[i] = preImage.get(i);
		
		
		try {																//Produces an image for the user
			ByteArrayInputStream bis = new ByteArrayInputStream(pixels);
			BufferedImage image;
			image = ImageIO.read(bis);
			ImageIO.write(image, "jpeg", new File("outputImage" + timesCalled + ".jpeg"));
		} catch (IOException e) {
			System.out.println(e);
		}
	}
}

//
//
//

//Class that executes GPS request and gives us a picture
public class gpsPackets {
	String requestCode;
	Modem modem;
	ArrayList<String> tPackets;

	public gpsPackets(){
		requestCode = "";
		modem = new Modem();
		tPackets = new ArrayList<String>();
	}
	
	public gpsPackets(String requestCode, Modem modem, ArrayList<String> tPackets) {
		this.requestCode = requestCode;
		this.modem = modem;
		this.tPackets = tPackets;
	}
	
	//Method that calculates the value T given a trace
	public void getTPackets(String trace) {

		modem.write((requestCode + trace + "\r").getBytes());
		String testPacket = "";
		String gpsPacket = "";
		
		for(;;) {
			int k;
			try {
				k = modem.read();
				if(k == -1) break;
				testPacket += (char)k;
				if(testPacket.contains("$")) {				//Saves the chars that we take between "$" and "0000*"
					gpsPacket+=(char)k;
					if(gpsPacket.contains("0000*")) {
						testPacket = "";
					}
				}
			}catch(Exception e){
				break;
			}
		}
		
		String b = "", m = "";					
		for(int n = 18; n < 22; n++)
			m+=gpsPacket.charAt(n);
		for(int n = 23; n < 27; n++)
			b+=gpsPacket.charAt(n);
		int c = Integer.parseInt(b);
		int s = Integer.parseInt(m);
		c = (int)(c * 0.006);					//Gets North Latitude
		String cs = Integer.toString(s);
		String cc = Integer.toString(c);
		String productN = cs + cc;
			
		String b2 = "", m2 = "";
		for(int n = 30; n < 35; n++)
			m2+=gpsPacket.charAt(n);
		for(int n = 36; n < 40; n++)			//Gets East Latitude
			b2+=gpsPacket.charAt(n);
		int c2 = Integer.parseInt(b2);
		int s2 = Integer.parseInt(m2);
		c2 = (int)(c2 * 0.006);
			
		String cs2 = Integer.toString(s2);
		String cc2 = Integer.toString(c2);
		String productE = cs2 + cc2;			//Combines the two
		
		tPackets.add("T=" + productE + productN);
	}
	
	//Method that gives us an image of a map with pins, the number of which is determined
	//by the number of T traces the class has produced 
	public void getImageGPS() {
		String finalT = "";
		for(int i = 0; i < tPackets.size(); i++)
			finalT += tPackets.get(i);
		
		int t;
		ArrayList<Byte> preImage = new ArrayList<Byte>();
		modem.write((requestCode + finalT + "\r").getBytes());
		byte[] termination = {(byte)0xFF, (byte)0xD9};
		int buffer_termination_index = 0;	
		
		for(;;) {
			try {
				t = modem.read();
				if(t == -1)
					break;
				
				preImage.add((byte)t);
				if((byte)t == termination[buffer_termination_index]) {				//Stops saving bytes after 
					buffer_termination_index++;										//termination sequence occurs
					if(buffer_termination_index >= termination.length) break;
				}else {
					buffer_termination_index = 0;
				}
			}catch(Exception e){
				break;
			}
		}
		byte[] pixels = new byte[preImage.size()];
		for(int i = 0; i < (preImage.size()); i++)
			pixels[i] = preImage.get(i);
		
		
		
		try {																		//Produce an image
			ByteArrayInputStream bis = new ByteArrayInputStream(pixels);
			BufferedImage image;
			image = ImageIO.read(bis);
			ImageIO.write(image, "jpeg", new File("outputImageGPS" + ".jpeg"));
		} catch (IOException e) {
			System.out.println(e);
		}
	}
		
}

//
//
//

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
*/