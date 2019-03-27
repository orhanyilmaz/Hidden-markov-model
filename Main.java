import java.util.ArrayList;

public class Main {

	static ArrayList<String> misspelledOnes = new ArrayList<String>();

	public static void main(String[] args) {

		ArrayList<String> cleanDataSet;

		ArrayList<String> trainSet = new ArrayList<String>();
		Read object = new Read();
		trainSet = object.readInput(args[0]);
		CleanDataSet dataSet = new CleanDataSet();

		cleanDataSet = dataSet.Cleaning(trainSet);

		HiddenMarkovModel hMM = new HiddenMarkovModel(cleanDataSet);
		 hMM.train();
		 hMM.EmissionProbability(cleanDataSet);
		// hMM.transitionProbability)(word1, word2);
		// hMM.initialProbability(word1, cleanDataSet);

	}

}
