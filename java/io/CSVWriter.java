package io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedList;

import data.Trade;
import exe.BuilderTest;

/**
 * This method writes all generated trades to a CSV file
 */

public class CSVWriter implements Runnable
{
	private LinkedList<Trade> trades;
	String filename = "trades.csv";
	
	public CSVWriter(LinkedList<Trade> trades)
	{
		this.trades = trades;
	}
	
	public void run() 
	{
		try (PrintWriter writer = new PrintWriter(new File(filename))) 
		{
			Thread.sleep(100);
			BuilderTest.tradesTextArea.append("Writing trada data...");
					
			StringBuilder sb = new StringBuilder();
			sb.append("TradeId,Timestamp,Buy/Sell,Qty,Price,PnL,Cumulative PnL");
			sb.append('\n');
		    
	    	int j = 0;
		    while (trades.size() > j) 
			{
		    	LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(trades.get(j).getTradeTime()), ZoneId.systemDefault());
		    	sb.append(trades.get(j).getTradeId()+","+localDateTime+","+trades.get(j).getTradeType()+","+trades.get(j).getTradeQty()+","+trades.get(j).getTradePrice()+","+trades.get(j).getTradePnL()+","+trades.get(j).getCumulativePnL());
		    	sb.append('\n');
		    	j++;
			}
			writer.write(sb.toString());
			
			BuilderTest.tradesTextArea.append("Finished writing "+trades.size()+" trades.\n");
		} 
		catch (FileNotFoundException e) 
		{
			System.out.println(e.getMessage());
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}	
	}
}
