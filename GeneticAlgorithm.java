import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

final public class GeneticAlgorithm extends Algorithm
{
	final private static int DEFAULT_POPULATION_SIZE = 20;
	final private static long DEFAULT_ITERATIONS = 1000000;
	final private static double DEFAULT_CROSSOVER_RATE = 0.75;
	final private static double DEFAULT_MUTATION_RATE = 0.02;
	private String OUTPUT_FILENAME;
	
	public GeneticAlgorithm(ExpressionEvaluator evaluator, String outputFilename)
	{
		System.out.println("\nGENETIC ALGORITHM");
		try
		{
			if (evaluator.numberOfLiterals() == 0)
			{
				throw new GeneticAlgorithmException(1);
			}
		}
		catch (GeneticAlgorithmException e)
		{
			e.printStackTrace();
			return;
		}
		OUTPUT_FILENAME = outputFilename;
		run(evaluator);
		return;
	}
	
	@Override
	protected void run(ExpressionEvaluator evaluator)
	{
		int populationSize = (int)consoleLong(1L, 1000L, new String("Genetic algorithm: Population size? "));
		if (populationSize == 0L)
		{
			System.out.println("Using value of GeneticAlgorithm.DEFAULT_POPULATION_SIZE (" + DEFAULT_POPULATION_SIZE + ")");
			populationSize = DEFAULT_POPULATION_SIZE;
		}
		boolean[][] currentGeneration = new boolean[populationSize][evaluator.numberOfLiterals()];
		randomizePopulation(currentGeneration);
		long stoppingCriteria = consoleLong(1L, 1000000000L, new String("Genetic algorithm: Number of iterations to allow? "));
		if (stoppingCriteria == 0L)
		{
			System.out.println("Using value of GeneticAlgorithm.DEFAULT_ITERATIONS (" + DEFAULT_ITERATIONS + ")");
			stoppingCriteria = DEFAULT_ITERATIONS;
		}
		double crossoverRate = consoleDouble(0.0, 1.0, new String("Genetic Algorithm: Crossover rate? "));
		if (crossoverRate < 0.0)
		{
			System.out.println("Using value of GeneticAlgorithm.DEFAULT_CROSSOVER_RATE (" + DEFAULT_CROSSOVER_RATE + ")");
			crossoverRate = DEFAULT_CROSSOVER_RATE;
		}
		double mutationRate = consoleDouble(0.0, 1.0, new String("Genetic Algorithm: Mutation rate? "));
		if (mutationRate < 0.0)
		{
			System.out.println("Using value of GeneticAlgorithm.DEFAULT_MUTATION_RATE (" + DEFAULT_MUTATION_RATE + ")");
			mutationRate = DEFAULT_MUTATION_RATE;
		}
		long iteration;
		for (iteration = 1L; iteration <= stoppingCriteria; ++iteration)
		{
			if (rouletteWheelSelection(currentGeneration, evaluator) != 0)
			{
				/*
				 * While doing roulette wheel selection, a solution was found.
				 * Exit the algorithm early.
				 * Otherwise, continue with crossover and mutation.
				 */
				System.out.println("The genetic algorithm succeeded in finding a solution after " + iteration + " iterations(s).");
				outputToFile(evaluator.getLiteralListReference(), OUTPUT_FILENAME);
				return;
			}
			singlePointCrossover(currentGeneration, crossoverRate);
			mutation(currentGeneration, crossoverRate);
		}
		System.out.println("GeneticAlgorithm.run(ExpressionEvaluator): A solution was not found.");
		outputToFile(evaluator.getLiteralListReference(), OUTPUT_FILENAME);
		return;
	}
	
	private void randomizePopulation(boolean[][] population)
	{
		/*
		 * Use ThreadLocalRandom to assign uniformly distributed random boolean values.
		 * Each row represents a chromosome.
		 * Every column of a chromosome represents a gene.
		 */
		for (int chromosome = 0; chromosome < population.length; ++chromosome)
		{
			for (int gene = 0; gene < population[0].length; ++gene)
			{
				population[chromosome][gene] = ThreadLocalRandom.current().nextBoolean();
			}
		}
	}
	
	private int rouletteWheelSelection(boolean[][] currentGeneration, ExpressionEvaluator evaluator)
	{
		boolean[][] nextGeneration = new boolean[currentGeneration.length][currentGeneration[0].length];
		ArrayList<Literal> literalList = evaluator.getLiteralListReference();
		int[] fitnessArray = new int[currentGeneration.length];
		int fitnessSum = 0;
		int fit;
		for (int chromosome = 0; chromosome < currentGeneration.length; ++chromosome)
		{
			/*
			 * Copy each gene (bit) into the evaluator.
			 */
			for (int gene = 0; gene < currentGeneration[0].length; ++gene)
			{
				literalList.get(gene).setValue(currentGeneration[chromosome][gene]);
			}
			/*
			 * Store the fitness evaluation function into the fitnessArray
			 * and also accumulate into fitnessSum.
			 */
			fitnessSum += (fitnessArray[chromosome] = (fit = fitnessEvaluationFunction(evaluator)));
			if (fit == evaluator.numberOfClauses())
			{
				/*
				 * A solution was found.
				 * Return early.
				 */
				return 1;
			}
			/*
			 * Generate a random integer in the range (0, fitnessSum].
			 * This will be used to select a chromosome to be copied into the next generation.
			 */
			int selector = ThreadLocalRandom.current().nextInt(0, fitnessSum);
			int selectedChromosomeIndex = -1;
			do {selector -= fitnessArray[++selectedChromosomeIndex];} while (selector > 0);
			/*
			 * Copy the selected chromosome into the next population.
			 */
			for (int gene = 0; gene < currentGeneration[0].length; ++gene)
			{
				nextGeneration[chromosome][gene] = currentGeneration[selectedChromosomeIndex][gene];
			}
		}
		/*
		 * Copy the new generation into the current generation.
		 */
		for (int chromosome = 0; chromosome < currentGeneration.length; ++chromosome)
		{
			for (int gene = 0; gene < currentGeneration[0].length; ++gene)
			{
				currentGeneration[chromosome][gene] = nextGeneration[chromosome][gene];
			}
		}
		return 0;
	}
	
	private void singlePointCrossover(boolean[][] population, double crossoverRate)
	{
		boolean temp;
		for (int chromosome = 0; chromosome < population.length; ++chromosome)
		{
			if (ThreadLocalRandom.current().nextDouble() < crossoverRate)
			{
				/*
				 * Choose a chromosome to crossover with.
				 */
				int mate = ThreadLocalRandom.current().nextInt(0, population.length);
				/*
				 * Choose a point which is an index of the chromosome to be crossed over.
				 */
				int point = ThreadLocalRandom.current().nextInt(0, population[0].length);
				/*
				 * From the point onward, swap genes (bits).
				 */
				for (int gene = point; gene < population[0].length; ++gene)
				{
					temp = population[chromosome][gene];
					population[chromosome][gene] = population[mate][gene];
					population[mate][gene] = temp;
				}
			}
		}
	}
	
	private void mutation(boolean[][] population, double mutationRate)
	{
		int gene;
		for (int chromosome = 0; chromosome < population.length; ++chromosome)
		{
			if (ThreadLocalRandom.current().nextDouble() < mutationRate)
			{
				/*
				 * Choose a gene (bit) in the current chromosome to mutate.
				 */
				gene = ThreadLocalRandom.current().nextInt(0, population[0].length);
				population[chromosome][gene] = ! population[chromosome][gene];
			}
		}
	}
}

@SuppressWarnings("serial")
final class GeneticAlgorithmException extends Exception
{
	public GeneticAlgorithmException(int errno)
	{
		switch (errno)
		{
		case 1:
			System.out.println("GeneticAlgorithm.run(ExpressionEvaluator,String): evaluator has no literals");
			break;
		}
	}
}