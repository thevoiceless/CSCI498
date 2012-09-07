package csci498.thevoiceless.lunchlist;

import java.util.Date;

public class Restaurant
{
	public enum Type
	{
		SIT_DOWN,
		TAKE_OUT,
		DELIVERY
	}
	
	// Not sure what to do about this
	private Date visited;
	private String name = "";
	private String address = "";
	private Type type;
	
	@Override
	public String toString()
	{
		return this.getName();
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public void setName(String newName)
	{
		this.name = newName;
	}
	
	public String getAddress()
	{
		return this.address;
	}
	
	public void setAddress(String newAddress)
	{
		this.address = newAddress;
	}
	
	public Type getType()
	{
		return this.type;
	}
	
	public void setType(Type newType)
	{
		this.type = newType;
	}
	
	public Date getDateVisited()
	{
		return visited;
	}
	
	public void setDateVisited(int month, int day, int year)
	{
		this.visited = new Date(year, month, day);
	}
}
