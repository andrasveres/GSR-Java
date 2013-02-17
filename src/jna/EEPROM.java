package jna;

import java.nio.Buffer;
import java.nio.ByteBuffer;


public class EEPROM {
	
	public MyHID hid;
	
	public EEPROM(MyHID h) {
		
		hid = h;
		
		if(hid.isOpened() == false) {
			System.out.println("Hid is not open");
			System.exit(0);
		}
				
		System.out.println("FeatureReportLenght "+hid.GetFeatureReportLength());
		System.out.println("InputReportLength "+hid.GetInputReportLength());
		System.out.println("OutputReportLength "+hid.GetOutputReportLength());
	
		
		//byte err = c.IntSendOutputReport(buff, (short)65);
		
	}
		
	public int[] ReadEEPROMBlock(int addr) {
		byte[] buff = new byte[64];
		
		buff[1] = (byte) 0x40;
		
		buff[2] = (byte) (addr & 0x000000ff);
		buff[3] = (byte) ((addr & 0x0000ff00)>>>8);
		buff[4] = (byte) ((addr & 0x00ff0000)>>>16);
		buff[5] = (byte) ((addr & 0xff000000)>>>24);		
		
		while (hid.IntSendOutputReport(buff, (short)65)!=0) {
			System.out.println("40 Send failed");
			System.exit(0);
		}
		//System.out.println("Send ok");
		
		ByteBuffer bb = ByteBuffer.allocate(65); 		
		int n = hid.IntReadInputReport(bb, 65);
		
		//System.out.println("n="+n);
				
		int[] b = new int[64];
		
		for(int i=0; i<64; i++) b[i] = (int) bb.array()[1+i] & 0xFF;
		
		//System.out.println("Header "+i);
		
		return b;		
	}
	
	public int ReadEEPROMByte(int addr) {
		byte[] buff = new byte[64];
		
		buff[1] = (byte) 0x39;
		
		buff[2] = (byte) (addr & 0x000000ff);
		buff[3] = (byte) ((addr & 0x0000ff00)>>>8);
		buff[4] = (byte) ((addr & 0x00ff0000)>>>16);
		buff[5] = (byte) ((addr & 0xff000000)>>>24);		
		
		while (hid.IntSendOutputReport(buff, (short)65)!=0) {
			System.out.println("40 Send failed");
			System.exit(0);
		}
		
		ByteBuffer bb = ByteBuffer.allocate(65); 		
		int n = hid.IntReadInputReport(bb, 65);
		
		int b = (0xFF & bb.array()[2]);
				
		return b;		
	}
	
	public static void main(String[] args) throws InterruptedException 
	{
		MyHID hid = new MyHID();	
		boolean res = hid.Connect(0x04d8, 0x003f);

		EEPROM e = new EEPROM(hid);

		
		System.out.println("Read");
		int[] b = e.ReadEEPROMBlock(0);
		System.out.println("OK");
		
		for(int i=0; i<b.length; i++) {
			System.out.print(" "+b[i]);
		}
		
		hid.CloseHIDDevice();

	}
}
