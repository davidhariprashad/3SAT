import java.util.ArrayList;

final public class Clause
{
	/*
	 * Contains a section of the expression.
	 * Follows a strict format of certain production rules.
	 * Contains references to Literal objects.
	 * Purpose is to be evaluated while referencing Boolean objects.
	 */
	public Clause()
	{
		notArray = new Boolean[3];
		idArray = new Integer[3];
		literalArray = new Literal[3];
	}
	
	public void setNots(boolean b0, boolean b1, boolean b2)
	{
		/*
		 * True when the NOT operator is present.
		 * Otherwise, false.
		 */
		notArray[0] = b0;
		notArray[1] = b1;
		notArray[2] = b2;
		return;
	}
	
	public void setIds(int id0, int id1, int id2)
	{
		/*
		 * Sets labels of the three variables contained in the clause.
		 */
		idArray[0] = id0;
		idArray[1] = id1;
		idArray[2] = id2;
		return;
	}
	
	public void setLiteralReference(ArrayList<Literal> uniqueList)
	{
		/*
		 * idArray contains the label (int) of the variable to be assigned.
		 * For each id in the idArray,
		 * Assign the corresponding reference to the unique list of Literal objects.
		 * Even when literalArray elements change values, references are fixed.
		 * Evaluation of clauses can change with fixed references to Literal objects.
		 */
		for (int i = 0; i < idArray.length; ++i)
		{
			int id = idArray[i];
			for (int j = 0; j < uniqueList.size(); ++j)
			{
				if (id == uniqueList.get(j).getId())
				{
					literalArray[i] = uniqueList.get(j);
					break;
				}
			}
		}
		idArray = null;
		return;
	}
	
	public boolean isTrue()
	{
		/*
		 * notArray[i] signifies presence of NOT operator.
		 * true means "!" is there
		 * false means "!" is not there
		 * 
		 * literalArray[i] signifies value contained in the literal
		 * true means "true" is contained
		 * false means "false" is contained
		 * 
		 * When combined, XOR operator determines whether the subexpression is true.
		 * 
		 * !False = True
		 * 1 ^ 0
		 * 
		 * True = True
		 * 0 ^ 1
		 */
		return ((notArray[0] ^ literalArray[0].getValue())
				||(notArray[1] ^ literalArray[1].getValue())
				||(notArray[2] ^ literalArray[2].getValue()));
	}
	
	private Boolean[] notArray;
	private Integer[] idArray;
	private Literal[] literalArray;
}