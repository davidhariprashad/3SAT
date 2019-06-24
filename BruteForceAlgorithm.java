import java.util.ArrayList;

final public class BruteForceAlgorithm extends Algorithm
{
	private String OUTPUT_FILENAME;
	
	public BruteForceAlgorithm(ExpressionEvaluator evaluator, String outputFilename)
	{
		System.out.println("\nBRUTE FORCE ALGORITHM");
		try
		{
			/*
			 * Throw an exception if necessary or give warnings on long runtime expectations.
			 * Every unique literal introduces twice the number of iterations to brute force.
			 * 20 unique literals will force about a billion iterations.
			 */
			if (evaluator.numberOfLiterals() == 0)
			{
				throw new BruteForceAlgorithmException(1);
			}
			if (evaluator.numberOfLiterals() > 62)
			{
				throw new BruteForceAlgorithmException(2);
			}
			if (evaluator.numberOfLiterals() > 20)
			{
				System.out.println("Warning: Brute force algorithm will take extremely long.");
			}
		}
		catch (BruteForceAlgorithmException e)
		{
			/*
			 * If an exception is thrown,
			 * safely return without executing the remainder of the method.
			 */
			e.printStackTrace();
			return;
		}
		OUTPUT_FILENAME = outputFilename;
		run(evaluator);
	}
	
	@Override
	protected void run(ExpressionEvaluator evaluator) {
		evaluator.setLiteralListFalse();
		final long stoppingCriteria = (1L << evaluator.numberOfLiterals());
		final int goalFitness = evaluator.numberOfClauses();
		ArrayList<Literal> bestSolution = new ArrayList<Literal>();
		int bestFitness = 0;
		int fit;
		System.out.println("Performing up to " + stoppingCriteria + " iterations...");
		long iteration;
		for (iteration = 1L; iteration <= stoppingCriteria; ++iteration)
		{
			fit = fitnessEvaluationFunction(evaluator);
			if (fit > bestFitness)
			{
				bestFitness = fit;
				saveBestSolution(evaluator, bestSolution);
			}
			if (fit == goalFitness)
			{
				System.out.println("The brute force algorithm found a solution after " + iteration + " iteration(s).");
				outputToFile(bestSolution, OUTPUT_FILENAME);
				return;
			}
			iterateBitstring(evaluator);
		}
		System.out.println("The brute force algorithm was unable to find a solution.");
		outputToFile(bestSolution, OUTPUT_FILENAME);
		return;
	}
	
	private static void iterateBitstring(ExpressionEvaluator evaluator)
	{
		/*
		 * bitstring is an ordered Literal object sequence containing
		 * the unique literals which appear in the input expression.
		 * If interpreted as an unsigned integer, increments by 1U.
		 */
		ArrayList<Literal> bitstring = evaluator.getLiteralListReference();
		int i = 0;
		while ((i < bitstring.size()) && (bitstring.get(i).getValue()))
		{
			bitstring.get(i).setValue(false);
			++i;
		}
		if (i < bitstring.size())
		{
			bitstring.get(i).setValue(true);
		}
	}
}

@SuppressWarnings("serial")
final class BruteForceAlgorithmException extends Exception
{
	/*
	 * Print some information to the console if an exception is thrown.
	 */
	public BruteForceAlgorithmException(int errno)
	{
		switch (errno)
		{
		case 1:
			System.out.println("BruteForceAlgorithm.run(ExpressionEvaluator,String): evaluator has no literals.");
			break;
		case 2:
			System.out.println("BruteForceAlgorithm.run(ExpressionEvaluator,String): evaluator exceeded 62 unique literals");
			break;
		}
	}
}