package jna;

// GSR communicates with GSR 0.1 board


import java.awt.*;
import java.awt.event.*;

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

public class HardReset  {
	MyHID myHID = new MyHID();
	
	public HardReset() throws InterruptedException
	{		

		//rom.Connect();
		
		boolean res = myHID.Connect(0x04d8, 0x003f);
		System.out.println("gethandle "+res);
		
		int i;
		
		byte[] buff = new byte[64];
		
		buff[1] = (byte) 0x41; // HARD RESET
		
		myHID.IntSendOutputReport(buff,65);		
				
		myHID.CloseHIDDevice();
	}
	
	

	public static void main(String[] args) throws InterruptedException 
	{
		new HardReset();
	}
}

