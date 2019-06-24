import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

final public class RandomAlgorithm extends Algorithm
{
	final private static long DEFAULT_ITERATIONS = 1000000L;
	final private static double ENTROPY = 0.5;
	private String OUTPUT_FILENAME;
	
	public RandomAlgorithm(ExpressionEvaluator evaluator, String outputFilename)
	{
		System.out.println("\nRANDOM ALGORITHM");
		try
		{
			/*
			 * Ensure there is at least one literal in the ExpressionEvaluator.
			 * The amount of distinct literals is not an issue.
			 */
			if (evaluator.numberOfLiterals() == 0)
			{
				throw new RandomAlgorithmException(1);
			}
		}
		catch (RandomAlgorithmException e)
		{
			e.printStackTrace();
			return;
		}
		OUTPUT_FILENAME = outputFilename;
		run(evaluator);
	}
	
	@Override
	protected void run(ExpressionEvaluator evaluator)
	{
		evaluator.setLiteralListFalse();
		long stoppingCriteria = consoleLong(1L, 1000000000L, new String("Random algorithm: Number of iterations to allow? "));
		if (stoppingCriteria == 0L)
		{
			System.out.println("Using value of RandomAlgorithm.DEFAULT_ITERATIONS (" + DEFAULT_ITERATIONS + ")");
			stoppingCriteria = DEFAULT_ITERATIONS;
		}
		System.out.println("Performing up to " + stoppingCriteria + " iterations...");
		final int goalFitness = evaluator.numberOfClauses();
		ArrayList<Literal> bestSolution = new ArrayList<Literal>();
		final int numberOfLiterals = evaluator.numberOfLiterals();
		final ArrayList<Literal> literalList = evaluator.getLiteralListReference();
		int bestFitness = 0;
		int currentFitness = 0;
		int deltaE;
		int randomIndex;
		long iteration;
		for (iteration = 1L; iteration <= stoppingCriteria; ++iteration)
		{
			randomIndex = ThreadLocalRandom.current().nextInt(numberOfLiterals);
			literalList.get(randomIndex).setValue(! literalList.get(randomIndex).getValue());
			deltaE = fitnessEvaluationFunction(evaluator) - currentFitness;
			if (deltaE > 0)
			{
				/*
				 * Accept the change to the bitstring.
				 * Accept positive deltaE always.
				 */
				bestFitness += deltaE;
				currentFitness += deltaE;
				saveBestSolution(evaluator, bestSolution);
				if (bestFitness == goalFitness)
				{
					/*
					 * Solution is found.
					 * Exit algorithm early.
					 */
					System.out.println("The random algorithm succeeded in finding a solution after " + iteration + " iteration(s).");
					outputToFile(bestSolution, OUTPUT_FILENAME);
					return;
				}
				continue;
			}
			else if (ThreadLocalRandom.current().nextDouble() < Math.exp(deltaE / ENTROPY))
			{
				/*
				 * At a certain probability, even though the fitness is worse,
				 * accept the change to the bitstring.
				 * The more negative deltaE is,
				 * the lower the probability is of the worse bitstring being kept.
				 * We do not accept the new bitstring as a better bitstring.
				 * The change is not reverted but bestSolution is not changed.
				 */
				currentFitness += deltaE;
				continue;
			}
			else
			{
				/*
				 * Completely reject the new bitstring by reversing the bit flip.
				 * Keep the original configuration.
				 */
				literalList.get(randomIndex).setValue(! literalList.get(randomIndex).getValue());
				continue;
			}
		}
		System.out.println("The random algorithm failed to find a solution with best fitness of " + bestFitness + ".");
		outputToFile(bestSolution, OUTPUT_FILENAME);
		return;
	}
}

@SuppressWarnings("serial")
final class RandomAlgorithmException extends Exception
{
	public RandomAlgorithmException(int errno)
	{
		switch (errno)
		{
		case 1:
			System.out.println("RandomAlgorithm.run(ExpressionEvaluator,String): evaluator has no literals");
			break;
		}
	}
}