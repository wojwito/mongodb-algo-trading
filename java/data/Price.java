package data;

import java.time.LocalDateTime;

/**
 *  This object stores a price read from the input CSV
 */

public class Price {
	
	private LocalDateTime timestamp;
	private long timeInMs;
	private float price;
	private float volume;

	public Price(LocalDateTime timestamp, long timeInMs, float price, float volume) 
	{ 
		this.timestamp = timestamp; 
		this.timeInMs = timeInMs; 
		this.price = price; 
		this.volume = volume;
	} 
	public Price()
	{
		
	}
	public LocalDateTime getTimestamp() 
	{ 
		return timestamp; 
	} 
	public long getTimeInMs() 
	{ 
		return timeInMs; 
	} 
	public float getPrice() 
	{ 
		return price; 
	}
	public float getVolume() 
	{ 
		return volume; 
	}
}