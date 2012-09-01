package csci498.thevoiceless.lunchlist;

public class Restaurant
{
	private String name = "";
	private String address = "";
	private String type = "";
	
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
	
	public String getType()
	{
		return this.type;
	}
	
	public void setType(String newType)
	{
		this.type = newType;
	}
}
