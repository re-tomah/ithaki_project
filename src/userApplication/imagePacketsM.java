package userApplication;
import ithakimodem.Modem;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

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
