package jna;

import java.nio.ByteBuffer;

public class GSRDevice {
	MyHID hid;
	public EEPROM rom;
	
	public int Connect() {
		
		hid = new MyHID();
		
		boolean res = hid.Connect(0x04d8, 0x003f);
		System.out.println("gethandle "+res+" isopened "+hid.isOpened());
		if(!res) return -1;
		
		int c = CheckConnected();
		if(c==1) return -2;
		
		SetConnected();
		
		//c = CheckConnected();
		//if(c==0) System.exit(0);
		
		rom = new EEPROM(hid);
		
		return 0;
	}
	
	int CheckConnected() {
		System.out.println("CheckConnected");

		
		byte[] buff = new byte[64];
					
		buff[1] = (byte) 0x36;

		int n=hid.IntSendOutputReport(buff, 65);
						
		ByteBuffer bb = ByteBuffer.allocate(65);		
		hid.IntReadInputReport(bb, 65);
		
		System.out.println(""+bb.array()[1]);
		if(bb.array()[1]!=0x36) System.exit(0);
			
		int connected = bb.array()[2];
		
		System.out.println(""+connected);
		
		return connected;
	}
	
	void SetConnected() {
		System.out.println("SetConnected");
	
		byte[] buff = new byte[64];
					
		buff[1] = (byte) 0x34;

		int n=hid.IntSendOutputReport(buff, 65);
						
		ByteBuffer bb = ByteBuffer.allocate(65);		
		hid.IntReadInputReport(bb, 65);
			
		return ;				
		
	}    
    
	void ResetConnected() {
		System.out.println("ResetConnected");

		byte[] buff = new byte[64];
					
		buff[1] = (byte) 0x35;

		int n=hid.IntSendOutputReport(buff, 65);
						
		ByteBuffer bb = ByteBuffer.allocate(65);		
		hid.IntReadInputReport(bb, 65);
			
		return ;				
		
	}    
	
	
	public String ReadVersion() {
		System.out.println("ReadVersion");

		byte[] buff = new byte[64];
		
		buff[1] = (byte) 0x38;


		int n=rom.hid.IntSendOutputReport(buff, 65);
				
		ByteBuffer bb = ByteBuffer.allocate(65);
		rom.hid.IntReadInputReport(bb, 65);
		
		String version = "";
		
		for(int i=1; i<64; i++) {
           if(bb.array()[i]==0) break;
		   version+= (char) bb.array()[i];
		}
				
		return version;		
	}
	
	public double ReadGSR() {
		byte[] buff = new byte[64];
		ByteBuffer bb = ByteBuffer.allocate(65);

		buff[1] = (byte) 0x37; // GSR
		hid.IntSendOutputReport(buff, 65);
		hid.IntReadInputReport(bb, 65);
		int b0 = (0xFF & bb.get(1));
				
		int b1 = (0xFF & bb.get(2));
		int b2 = (0xFF & bb.get(3));
		int b3 = (0xFF & bb.get(4));
		int b4 = (0xFF & bb.get(5));
		double gsr = (b2*256 + b1)/4.0;
		
		return gsr;
	}
	
	public class pulse {
		public int bpm;
		public int avg_pulse;
		public int avg_pulse_fast;
		public int pp;
	}
	
	public pulse ReadPulse() {
		byte[] buff = new byte[64];
		ByteBuffer bb = ByteBuffer.allocate(65);
		int b0;		
		
		do{
			
			buff[1] = (byte) 0x42; // PULSE
			
			hid.IntSendOutputReport(buff, 65);
			hid.IntReadInputReport(bb, 65);
						
						
			b0 = (0xFF & bb.get(1));	
			if(b0!=0x42) System.out.println("ReadPulse error");		 
		} while (b0 != 0x42);
		
		int b1 = (0xFF & bb.get(2));
		int b2 = (0xFF & bb.get(3));

		int b3 = (0xFF & bb.get(4));
		int b4 = (0xFF & bb.get(5));
        
		int b5 = (0xFF & bb.get(6));
		int b6 = (0xFF & bb.get(7));

		int b7 = (0xFF & bb.get(8));
		int b8 = (0xFF & bb.get(9));

		pulse p = new pulse();
		
		p.bpm = b2*256 + b1;
		p.avg_pulse = b4*256 + b3;            			
		p.avg_pulse_fast = b6*256 + b5;
		p.pp = b8*256 + b7;
		
		return p;
	}

	public void Disconnect() {
	    ResetConnected();
	    
	    System.out.println("Close hid");
	    hid.CloseHIDDevice();
	}
}
