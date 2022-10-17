package exe;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.SwingWorker;

import com.mongodb.*;
import data.Trade;
import io.CSVWriter;
import util.RoundFloat;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.set;

/**
 * The main back-end class. Uses standard SwingWorker functionality:
 * 	- doInBackground() - performs the heavy calculations in the background
 * 	- process() - sends results to the GUI
 * 	- done() - once all processes are completed or cancelled, writes the resulting trades to a CSV file
 */

public class MongoDBTradeSimulator {

	LinkedList<Trade> trades = new LinkedList<Trade>();
	DataLoaderWorker myWorker = new DataLoaderWorker();
	
	public void runApp() throws InterruptedException 
	{
		myWorker.execute();
	}

	class DataLoaderWorker extends SwingWorker<Void,String>
	{
		protected Void doInBackground() throws Exception
        {
			int m = (Integer) BuilderTest.mSpinner.getValue();
			int n = (Integer) BuilderTest.nSpinner.getValue();
			int notionalValue = (Integer) BuilderTest.notValueSpinner.getValue();
			
			Double mAvg = Double.valueOf(0);
			Double nAvg = Double.valueOf(0);
			
			double qty = 0;
			double cumulativePnL = 0;
			int tradeId = 0;
			
			String dataLine = "";
			String tradeSide = "";
			float[] mArray = new float[m];
			float[] nArray = new float[n];
			
			boolean pendingSale = true;
			boolean collectionExists = false;
	
			mArray = populateWithZeros(mArray);
			nArray = populateWithZeros(nArray);
			
			Trade newTrade = new Trade();

			try (MongoClient mongoClient = MongoClients.create("mongodb+srv://USERNAME:PASSWORD@CLUSTER_URI"))
			{
				MongoDatabase database = mongoClient.getDatabase("prices");

				database.getCollection("ticks").drop();
				database.getCollection("averages").drop();
				database.getCollection("views").drop();

				MongoCollection<Document> collection = database.getCollection("views");

				try
				{
					collection.insertOne(new Document()
							.append("sourceDb","prices")
							.append("sourceCollection","ticks")
							.append("pipeline","[   { \"$setWindowFields\": { \"sortBy\": { \"timestamp\":1 }, \"output\": { \"smaM\": { \"$avg\": \"$price\",\"window\": { \"documents\": [-"+m+",0]}}, \"smaN\": { \"$avg\": \"$price\",\"window\": { \"documents\": [-"+n+",0]}}}}},{ \"$merge\" : { \"into\": { \"db\": \"prices\", \"coll\": \"averages\" },\"on\": \"_id\", \"whenMatched\": \"keepExisting\", \"whenNotMatched\": \"insert\" } }]"));

					System.out.println("Success! Inserted: ");
				}
				catch (MongoException me)
				{
					System.err.println("Something not right: " + me);
				}
			}


			while (true)
			{
				Document price;

				try (MongoClient mongoClient2 = MongoClients.create("mongodb+srv://USERNAME:PASSWORD@CLUSTER_URI"))
				{
					BuilderTest.tradesTextArea.append("Connecting to database\n");
					MongoDatabase database2 = mongoClient2.getDatabase("prices");

					while (!collectionExists)
					{
						collectionExists = database2.listCollectionNames().into(new ArrayList()).contains("averages");
						BuilderTest.tradesTextArea.append("No incoming data\n");
						Thread.sleep(5000);
					}
					BuilderTest.tradesTextArea.append("Getting collection\n");
					MongoCollection<Document> collection2 = database2.getCollection("averages");
					BuilderTest.tradesTextArea.append("Getting first doc\n");
					price = collection2.findOneAndUpdate(not(exists("Processed")),set("Processed",true));
					BuilderTest.tradesTextArea.append("Getting doc: "+price.toJson()+"\n");
				}

				mAvg = price.getDouble("smaM");
				nAvg = price.getDouble("smaN");
				double unitPrice = price.getDouble("price");
				long timestamp = price.getLong("time_in_milliseconds");

				dataLine = timestamp+","+unitPrice+","+mAvg+","+nAvg+",";
				double pnl = 0;
				BuilderTest.tradesTextArea.append("Calculations start\n");
				if (mAvg>nAvg && pendingSale)
				{	
					tradeSide = "BUY";
					pendingSale = false;
					qty = RoundFloat.roundFloat(notionalValue / unitPrice,4);
					newTrade = new Trade(tradeId,timestamp,tradeSide,qty,unitPrice,pnl,cumulativePnL);
					dataLine = dataLine+tradeId+","+tradeSide+","+qty+","+pnl+","+cumulativePnL;
					trades.add(newTrade);
				}
				else if (nAvg>mAvg && !pendingSale)
				{
					tradeSide = "SELL";
					pendingSale = true;
					pnl = RoundFloat.roundFloat((qty*(unitPrice))-notionalValue,4);
					cumulativePnL = RoundFloat.roundFloat(cumulativePnL+pnl,4);
					newTrade = new Trade(tradeId,timestamp,tradeSide,qty,unitPrice,pnl,cumulativePnL);
					dataLine = dataLine+tradeId+","+tradeSide+","+qty+","+pnl+","+cumulativePnL;
					trades.add(newTrade);
					tradeId++;			
				}
				else
				{
					dataLine = dataLine+"NOTRADE";
				}
				publish(dataLine);

				BuilderTest.tradesTextArea.append("Calculations end - sleeping\n");
				Thread.sleep(60000);
			}
        }
		
		protected void process(List<String> inputLines)
		{
			for (String data : inputLines) 
			{
				if (!data.isBlank())
				{
					String[] dataItem = data.split(",");

					BuilderTest.prices.add(Long.parseLong(dataItem[0]), Float.parseFloat(dataItem[1]));
					BuilderTest.mAvgs.add(Long.parseLong(dataItem[0]), Float.parseFloat(dataItem[2]));
					BuilderTest.nAvgs.add(Long.parseLong(dataItem[0]), Float.parseFloat(dataItem[3]));	
					
					if (!dataItem[4].equals("NOTRADE"))
					{
						LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(dataItem[0])), ZoneId.systemDefault());
						BuilderTest.tradesTextArea.append("ID: "+dataItem[4]+" Time: "+localDateTime+" Trade: "+dataItem[5]+" "+dataItem[6]+"@"+dataItem[1]+" PnL: "+dataItem[7]+" Cumulative: "+dataItem[8]+"\n");
					}
				}
			}
		}
		

		protected void done() 
        {
			CSVWriter myWriter = new CSVWriter(trades);
			Thread csvWriter = new Thread(myWriter);
			csvWriter.start();
        }
	};
	
	/*
	 * Just a short method that populates an array with zeros, used for the first few prices
	 */
	
	public static float[] populateWithZeros(float[] myArray)
	{
		float[] newArray = myArray;
		
		for (int i=0;i<myArray.length;i++)
		{
			newArray[i]=0;
		}
		return newArray;
	}
}
