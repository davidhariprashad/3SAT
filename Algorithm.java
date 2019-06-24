import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public abstract class Algorithm
{
	protected Algorithm() {}
	
	protected abstract void run(ExpressionEvaluator evaluator);
	
	protected void saveBestSolution(ExpressionEvaluator evaluator, ArrayList<Literal> bestSolution)
	{
		/*
		 * Save the best solution as an ArrayList<Literal>.
		 * It will contain which variables had what value for the best solution.
		 * Uses constructor which copies values to create entirely new Literal object.
		 */
		bestSolution.clear();
		for (int i = 0; i < evaluator.getLiteralListReference().size(); ++i)
		{
			bestSolution.add(new Literal(evaluator.getLiteralListReference().get(i)));
		}
		return;
	}
	
	protected void outputToFile(ArrayList<Literal> bestSolution, String outputFilename)
	{
		System.out.println("Printing the best solution to file.");
		/*
		 * Print to the file path provided.
		 * For each literal,
		 * print the name of the literal (x42)
		 * and print what it is equivalent to (true/false)
		 * per line in the file.
		 */
		try
		{
			FileWriter fw = new FileWriter(new File(outputFilename));
			for (int i = 0; i < bestSolution.size(); ++i)
			{
				fw.write('x');
				fw.write(Integer.toString(bestSolution.get(i).getId()));
				fw.write('=');
				fw.write(Boolean.toString(bestSolution.get(i).getValue()));
				fw.write(System.lineSeparator());
			}
			fw.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	protected int fitnessEvaluationFunction(ExpressionEvaluator evaluator)
	{
		/*
		 * Define the fitness evaluation function to be the count of
		 * true clauses in the ExpressionEvaluator.
		 */
		return evaluator.trueClauseCount();
	}
	
	protected long consoleLong(long min, long max, String prompt)
	{
		/*
		 * Get a long from the console.
		 * If fail, return 0L.
		 */
		try
		{
			@SuppressWarnings("resource")
			Scanner scanner = new Scanner(System.in);
			String inputString;
			long inputLong;
			do
			{
				System.out.print(prompt);
				inputString = scanner.next();
				inputLong = Long.parseLong(inputString);
				if ((inputLong < min) || (inputLong > max))
				{
					System.out.println("Enter an integer at least " + min + " and at most " + max + ": ");
				}
			} while (inputLong < min || inputLong > max);
			return inputLong;
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace();
			System.out.println("Algorithm.consoleLong(long,long,String): returning 0L");
			return 0L;
		}
	}
	
	protected double consoleDouble(double min, double max, String prompt)
	{
		/*
		 * Get a double from the console.
		 * If fail, return -1.0
		 */
		try
		{
			@SuppressWarnings("resource")
			Scanner scanner = new Scanner(System.in);
			String inputString;
			double inputDouble;
			do
			{
				System.out.print(prompt);
				inputString = scanner.next();
				inputDouble = Double.parseDouble(inputString);
				if ((inputDouble < min) || (inputDouble > max))
				{
					System.out.println("Enter a floating point at least " + min + " and at most " + max + ": ");
				}
			} while ((inputDouble < min) || (inputDouble > max));
			return inputDouble;
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace();
			System.out.println("Algorithm.consoleDouble(long,long,String): returning -1.0");
			return -1.0;
		}
	}
}
