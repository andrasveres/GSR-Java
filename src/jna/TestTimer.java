package jna;

// GSR communicates with GSR 0.1 board


import java.awt.*;
import java.awt.event.*;
import java.nio.ByteBuffer;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.text.*;


import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class TestTimer  {
	MyHID myHID = new MyHID();

	double ymax = 2000;
	double dx = 60;
	
	JFreeChart chart;
	
	protected void finalize()
	{
		System.out.println("Close HID");
		myHID.CloseHIDDevice();
	}

	void CheckTime() throws InterruptedException {
		byte[] buff = new byte[64];

		double t_start = System.currentTimeMillis();

		long msec_start=0;
		
		do {
			
			buff[1] = (byte) 0x50; // MSEC
						
			myHID.IntSendOutputReport(buff, 65);	
						
			ByteBuffer bb = ByteBuffer.allocate(65);		
			myHID.IntReadInputReport(bb, 65);
						
			double t = System.currentTimeMillis();
			Thread.sleep(1000);
			
			int b0 = (0xFF & bb.array()[1]);
			int b1 = (0xFF & bb.array()[2]);
			int b2 = (0xFF & bb.array()[3]);
			int b3 = (0xFF & bb.array()[4]);
			int b4 = (0xFF & bb.array()[5]);
			
			long msec = b1 + b2*256 + b3*65536 + b4*65536*256;

			if(msec_start==0) {
				msec_start = msec;
				continue;
			}
			
			double dmsec = msec - msec_start;
			double dt = t - t_start;
			
			
			
			System.out.println("MSEC "+dmsec+" "+dt+" Error(sec) after 1 hour:"+(dt-dmsec)/dt*3600.0/1000.0);
			
		} while(true);
	}
	
	TestTimer() throws InterruptedException
	{		
				
		// ANDRAS
		if (!myHID.Connect(0x04d8, 0x003f)) {
			System.out.println("NOT FOUND HID");
			// System.exit(0);
		}else System.out.println("FOUND HID");

		
		CheckTime();
		
	}
	
	public static void main(String[] args) throws InterruptedException 
	{
		new TestTimer();
	}
}

