public class Literal
{
	protected Literal(int i)
	{
		id = i;
		value = false;
	}
	
	protected Literal(Literal old)
	{
		id = old.id;
		value = old.value;
	}
	
	protected int getId()
	{
		return id;
	}

	protected boolean getValue()
	{
		return value;
	}
	
	protected void setValue(boolean b)
	{
		value = b;
	}
	
	private int id;
	private boolean value;
}
