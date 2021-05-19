package userApplication;
import ithakimodem.Modem;
import java.util.ArrayList;

import userApplication.inputTimer;

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
		System.out.println("Ithaki server is waiting for your commands."
				+ " \nThis application timeouts after 30seconds of being idle");
		
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
}