import java.util.ArrayList;

final public class ExpressionEvaluator
{
	public ExpressionEvaluator(ArrayList<Boolean> orderOfNots, ArrayList<Integer> orderOfLiteralIds)
	{
		try
		{
			/*
			 * References are not null.
			 * References are of equal size.
			 * References are ArrayLists of lengths multiple of 3.
			 * References are nonempty ArrayLists.
			 */
			if (orderOfNots == null)
			{
				throw new ExpressionEvaluatorException(1);
			}
			else if (orderOfLiteralIds == null)
			{
				throw new ExpressionEvaluatorException(2);
			}
			else if (orderOfNots.size() != orderOfLiteralIds.size())
			{
				throw new ExpressionEvaluatorException(3);
			}
			else if (orderOfNots.size() % 3 != 0)
			{
				throw new ExpressionEvaluatorException(4);
			}
			else if (orderOfNots.size() == 0)
			{
				throw new ExpressionEvaluatorException(5);
			}
			
			/*
			 * Define each clause with each piece of information that appears in the expression.
			 * The result is an ArrayList<Clause>.
			 */
			clauseList = new ArrayList<Clause>();
			for (int i = 0, c = 0; i < orderOfLiteralIds.size(); i += 3, ++c)
			{
				clauseList.add(new Clause());
				clauseList.get(c).setNots(orderOfNots.get(i),
						orderOfNots.get(i + 1),
						orderOfNots.get(i + 2));
				clauseList.get(c).setIds(orderOfLiteralIds.get(i),
						orderOfLiteralIds.get(i + 1),
						orderOfLiteralIds.get(i + 2));
			}
			/*
			 * For every unique literal that appears in the expression,
			 * add it to the variable list.
			 */
			literalList = new ArrayList<Literal>();
			int id;
			boolean found;
			for (int i = 0; i < orderOfLiteralIds.size(); ++i)
			{
				id = orderOfLiteralIds.get(i);
				found = false;
				for (int j = 0; j < literalList.size(); ++j)
				{
					if (id == literalList.get(j).getId())
					{
						found = true;
						break;
					}
				}
				if (!found)
				{
					literalList.add(new Literal(id));
				}
			}
			/*
			 * Link the clauseList to the literalList by
			 * setting the literals in the clauseList to refer to
			 * objects contained in the literalList.
			 */
			for (int i = 0; i < clauseList.size(); ++i)
			{
				clauseList.get(i).setLiteralReference(literalList);
			}
			return;
		}
		catch (ExpressionEvaluatorException e)
		{
			e.printStackTrace();
			return;
		}
	}
	
	public void setLiteralListFalse()
	{
		for (int i = 0; i < literalList.size(); ++i)
		{
			literalList.get(i).setValue(false);
		}
	}
	
	public int numberOfLiterals()
	{
		if (literalList == null)
			return 0;
		return literalList.size();
	}
	
	public int numberOfClauses()
	{
		if (clauseList == null)
			return 0;
		return clauseList.size();
	}
	
	public ArrayList<Literal> getLiteralListReference()
	{
		return literalList;
	}
	
	public int trueClauseCount()
	{
		int count = 0;
		for (int i = 0; i < clauseList.size(); ++i)
		{
			if (clauseList.get(i).isTrue())
				count++;
		}
		return count;
	}
	
	public boolean lazyEvaluation()
	{
		if (clauseList.size() == 0)
		{
			return false;
		}
		for (int i = 0; i < clauseList.size(); ++i)
		{
			if (!clauseList.get(i).isTrue())
			{
				return false;
			}
		}
		return true;
	}
	
	private ArrayList<Clause> clauseList;
	private ArrayList<Literal> literalList;
}

@SuppressWarnings("serial")
final class ExpressionEvaluatorException extends Exception
{
	public ExpressionEvaluatorException(int errno)
	{
		switch (errno)
		{
		case 1:
			System.out.println("Constructor failed: parameter 1 null");
			break;
		case 2:
			System.out.println("Constructor failed: parameter 2 null");
			break;
		case 3:
			System.out.println("Constructor failed: list sizes unequal");
			break;
		case 4:
			System.out.println("Constructor failed: list sizes not multiple of 3");
			break;
		case 5:
			System.out.println("Constructor failed: lists are empty");
		}
	}
}