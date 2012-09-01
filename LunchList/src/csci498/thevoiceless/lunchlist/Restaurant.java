package csci498.thevoiceless.lunchlist;

public class Restaurant
{
	public enum Type
	{
		SIT_DOWN,
		TAKE_OUT,
		DELIVERY
	}
	private String name = "";
	private String address = "";
	private Type type;
	
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
}
