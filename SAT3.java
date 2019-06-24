final public class SAT3
{
	static String FILENAME_BRUTE;
	static String FILENAME_RANDOM;
	static String FILENAME_GENETIC;
	static ExpressionEvaluator evaluator;
	
	public static void main(String[] args)
	{
		if (checkInputParameters(args) != 0)
			return;
		
		if (analyze(args[0]) != 0)
			return;
		
		BruteForce();
		Random();
		Genetic();
		
		return;
	}
	
	private static int checkInputParameters(String[] args)
	{
		switch (args.length)
		{
		case 0:
			System.out.println("Provide exactly one or four file paths.");
			return 1;
		case 1:
			FILENAME_BRUTE = new String("brute.txt");
			FILENAME_RANDOM = new String("random.txt");
			FILENAME_GENETIC = new String("genetic.txt");
			System.out.println("Using the default filepaths for output.");
			return 0;
		case 4:
			System.out.println("Using the user-defined filepaths for output.");
			FILENAME_BRUTE = args[1];
			FILENAME_RANDOM = args[2];
			FILENAME_GENETIC = args[3];
			return 0;
		default:
			System.out.println("This program only allows exactly one or four parameters as filenames for output.");
			return 2;
		}
	}
	
	private static int analyze(String fname)
	{
		int errno;
		Analyzer analyzer = new Analyzer(fname);
		errno = analyzer.lexicalAnalysis();
		if (errno != 0)
		{
			System.out.print("File or lexical error: ");
			System.out.println(errno);
			return 1;
		}
		errno = analyzer.syntaxAnalysis();
		if (errno != 0)
		{
			System.out.print("Invalid expression error: ");
			System.out.println(errno);
			return 2;
		}
		System.out.println(analyzer.getExpression());
		evaluator = new ExpressionEvaluator(analyzer.getOrderOfNotsReference(), analyzer.getOrderOfLiteralIdsReference());
		return 0;
	}
	
	private static void BruteForce()
	{
		new BruteForceAlgorithm(evaluator, FILENAME_BRUTE);
		return;
	}
	
	private static void Random()
	{
		new RandomAlgorithm(evaluator, FILENAME_RANDOM);
		return;
	}
	
	private static void Genetic()
	{
		new GeneticAlgorithm(evaluator, FILENAME_GENETIC);
		return;
	}
}