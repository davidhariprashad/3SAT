import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

final public class Analyzer
{
	public Analyzer(String fpath)
	{
		filepath = fpath;
	}
	
	public int lexicalAnalysis()
	{
		/*
		 * Open the file.
		 * Store the expression in a meaningful set of tokens.
		 */
		int errno;
		if ((errno = open(filepath)) != 0)
			return errno;
		if ((errno = storeExpression()) != 0)
			return errno;
		return close();
	}
	
	public int syntaxAnalysis()
	{
		orderOfNots = new ArrayList<Boolean>();
		orderOfLiteralIds = new ArrayList<Integer>();
		return validate();
	}
	
	private int storeExpression()
	{
		/*
		 * Read the input file.
		 * Ignore whitespace.
		 * Continually append symbols if and only if legal.
		 * Otherwise, throw an exception and return nonzero.
		 */
		StringBuilder sb = new StringBuilder();
		try
		{
			if (reader == null)
			{
				expression = new String();
				return -2;
			}
			int i;
			char c;
			if ((i = reader.read()) == -1)
			{
				expression = new String();
				return -3;
			}
			c = (char)i;
			sb.append('(');
			do
			{
				c = (char)i;
				switch (c)
				{
				case '!':
				case '|':
				case 'x':
					sb.append(c);
					break;
				case ' ':
					break;
				case '\t':
					break;
				case '\r':
					break;
				case '\n':
					sb.append(')');
					sb.append('&');
					sb.append('(');
					break;
				default:
					if (Character.isDigit(c))
					{
						sb.append(c);
						break;
					}
					throw new InvalidExpressionException(c);
				}
			} while ((i = reader.read()) != -1);
			sb.append(')');
			expression =  sb.toString();
			return 0;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			expression = new String();
			return -3;
		}
		catch (InvalidExpressionException e)
		{
			e.printStackTrace();
			expression = new String();
			return -4;
		}
	}
	
	private int validate()
	{
		/*
		 * Ensure the expression string follows a particular set of production rules.
		 * If a production rule fails, an exception is thrown and nonzero is returned.
		 * Calls to helper functions CHAR, NOT, and LITERAL may throw exceptions.
		 */
		if (expression == null)
		{
			return 6;
		}
		if (expression.isEmpty())
		{
			return 7;
		}
		try
		{
			int len = expression.length();
			index = 0;
			while (index < len)
			{
				CHAR('(');
				NOT();
				LITERAL();
				CHAR('|');
				NOT();
				LITERAL();
				CHAR('|');
				NOT();
				LITERAL();
				CHAR(')');
				if (index != len)
				{
					CHAR('&');
				}
			}
			return 0;
		}
		catch (InvalidExpressionException e)
		{
			e.printStackTrace();
			System.out.print("Failed parsing at index: ");
			System.out.println(index);
			return 8;
		}
	}
	
	private void NOT() throws InvalidExpressionException
	{
		/*
		 * Checks current index for NOT operator.
		 * If NOT operator is confirmed, call CHAR to accept the symbol.
		 */
		if (expression.charAt(index) == '!')
		{
			orderOfNots.add(true);
			CHAR('!');
			return;
		}
		orderOfNots.add(false);
		return;
	}
	
	private void CHAR(char ch) throws InvalidExpressionException
	{
		/*
		 * Throws an exception if an unexpected character appears at the index.
		 * If the expected character is processed successfully,
		 * increment the index.
		 */
		if (expression.charAt(index) != ch)
		{
			throw new InvalidExpressionException(expression.charAt(index));
		}
		index++;
		return;
	}
	
	private void LITERAL() throws InvalidExpressionException
	{
		/*
		 * Accepts a string of characters beginning with
		 * x
		 * and is strictly followed by at least one digit.
		 */
		int start, end;
		char c;
		CHAR('x');
		start = index;
		c = expression.charAt(index);
		if (!Character.isDigit(c))
		{
			throw new InvalidExpressionException(c);
		}
		index++;
		while (Character.isDigit((c = expression.charAt(index))))
		{
			index++;
		}
		end = index;
		orderOfLiteralIds.add(Integer.parseInt(expression.substring(start, end)));
		return;
	}
	
	private int open(String fname)
	{
		try
		{
			reader = new BufferedReader(new FileReader(new File(fname)));
			return 0;
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			return -1;
		}
	}
	
	private int close()
	{
		try
		{
			if (reader != null)
			{
				reader.close();
				reader = null;
				return 0;
			}
			return -4;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return -5;
		}
	}
	
	public String getExpression()
	{
		return new String(expression);
	}
	
	public ArrayList<Boolean> getOrderOfNotsReference()
	{
		return orderOfNots;
	}
	
	public ArrayList<Integer> getOrderOfLiteralIdsReference()
	{
		return orderOfLiteralIds;
	}
	
	private BufferedReader reader;
	private String expression;
	private String filepath;
	private int index;
	private ArrayList<Boolean> orderOfNots;
	private ArrayList<Integer> orderOfLiteralIds;
}

@SuppressWarnings("serial")
final class InvalidExpressionException extends Exception
{
	/*
	 * Print some information to the console when an
	 * unexpected symbol is processed.
	 */
	public InvalidExpressionException(char c)
	{
		System.out.print("Unexpected character: ");
		System.out.println(c);
	}
}