package userApplication;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


import javax.imageio.ImageIO;

import ithakimodem.Modem;

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
		System.out.println(gpsPacket);
		
		String b = "", m = "";					
		for(int n = 18; n < 22; n++)
			m+=gpsPacket.charAt(n);
		for(int n = 23; n < 27; n++)
			b+=gpsPacket.charAt(n);
		int c = Integer.parseInt(b);
		int s = Integer.parseInt(m);
		c = (int)(c * 0.006);					//Gets North Latitude
		String cs = Integer.toString(s);
		System.out.println(cs);
		String cc = Integer.toString(c);
		System.out.println(cc);
		String productN = cs + cc;
		System.out.println(productN);
			
		String b2 = "", m2 = "";
		for(int n = 30; n < 35; n++)
			m2+=gpsPacket.charAt(n);
		for(int n = 36; n < 40; n++)			//Gets East Latitude
			b2+=gpsPacket.charAt(n);
		int c2 = Integer.parseInt(b2);
		int s2 = Integer.parseInt(m2);
		c2 = (int)(c2 * 0.006);
			
		String cs2 = Integer.toString(s2);
		System.out.println(s2);
		String cc2 = Integer.toString(c2);
		System.out.println(c2);
		String productE = cs2 + cc2;		
		System.out.println(productE);			//Combines the two
		
		tPackets.add("T=" + productE + productN);
		System.out.println("T=" + productE + productN);
	}
	
	//Class that gives us an image of a map with pins, the number of which is determined
	//by the number of T traces the class has produced 
	public void getImageGPS() {
		String finalT = "";
		for(int i = 0; i < tPackets.size(); i++)
			finalT += tPackets.get(i);

		System.out.println(finalT);
		
		
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
