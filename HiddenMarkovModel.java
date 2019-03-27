import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HiddenMarkovModel {
	ArrayList<String> cleanDataSet;
	HashMap<String, Double> bigramCounts;
	HashMap<String, Double> unigramCounts;
	HashSet<String> wordSet;
	public final String START = "";
	HashMap<String, Double> firstWordCounts;

	public HiddenMarkovModel(ArrayList<String> cleanDataSet) {
		this.cleanDataSet = cleanDataSet;
		this.bigramCounts = new HashMap<String, Double>();
		this.unigramCounts = new HashMap<String, Double>();
		this.wordSet = new HashSet<String>();
		this.firstWordCounts = new HashMap<String, Double>();

	}

	public void train() {
		String regexp = "('?\\w+|\\p{Punct})";
		Pattern pattern = Pattern.compile(regexp);

		for (String sentence : cleanDataSet) {
			int count = 0;
			Matcher matcher = pattern.matcher(sentence);
			String previousWord = START;
			while (matcher.find()) {

				String[] firstWord = sentence.split(" ");
				firstWord[0] = firstWord[0].toLowerCase(Locale.ENGLISH);

				String match = matcher.group();
				match = match.toLowerCase(Locale.ENGLISH);

				double firstWordCount = 0;
				if (count == 0) {
					if (firstWordCounts.containsKey(match)) {
						firstWordCount = firstWordCounts.get(match);

					}
					firstWordCounts.put(match, firstWordCount + 1);

				}

				double unigramCount = 0;
				if (unigramCounts.containsKey(match)) {
					unigramCount = unigramCounts.get(match);
				}
				unigramCounts.put(match, unigramCount + 1);

				wordSet.add(match);

				String a = previousWord + " " + match;

				double bigramCount = 0;
				if (bigramCounts.containsKey(a)) {
					bigramCount = bigramCounts.get(a);
				}
				bigramCounts.put(a, bigramCount + 1);

				previousWord = match;
				count++;
			}
		}

	}

	public double transitionProbability(String word1, String word2) {
		String a = word1 + " " + word2;
		if (bigramCounts.containsKey(a)) {
			return bigramCounts.get(a) / unigramCounts.get(word1);
		} else {
			return 0.0;
		}
	}

	public double initialProbability(String word1, ArrayList<String> cleanDataSet) {
		if (firstWordCounts.containsKey(word1)) {
			return firstWordCounts.get(word1) / cleanDataSet.size();
		} else {
			return 0.0;
		}
	}

	public int minDistance(String word1, String word2) {
		int len1 = word1.length();
		int len2 = word2.length();

		int[][] editDistance = new int[len1 + 1][len2 + 1];

		for (int i = 0; i <= len1; i++) {
			editDistance[i][0] = i;
		}

		for (int j = 0; j <= len2; j++) {
			editDistance[0][j] = j;
		}

		for (int i = 0; i < len1; i++) {
			char c1 = word1.charAt(i);
			for (int j = 0; j < len2; j++) {
				char c2 = word2.charAt(j);

				if (c1 == c2) {
					editDistance[i + 1][j + 1] = editDistance[i][j];
				} else {
					int sub = editDistance[i][j] + 1;
					int ins = editDistance[i][j + 1] + 1;
					int del = editDistance[i + 1][j] + 1;

					int min;
					if (sub > ins)
						min = ins;
					else
						min = sub;

					if (del < min)
						min = del;

					editDistance[i + 1][j + 1] = min;
				}
			}
		}

		return editDistance[len1][len2];
	}

	public void EmissionProbability(ArrayList<String> cleanDataSet) {

		HashMap<String, Integer> possibleCorrect = new HashMap<String, Integer>();
		HashMap<String, Integer> SubPairs = new HashMap<String, Integer>();
		HashMap<String, Integer> DelPairs = new HashMap<String, Integer>();
		HashMap<String, Integer> InsPairs = new HashMap<String, Integer>();
		HashMap<String, HashMap<String, Integer>> possibleCorrectsOfMisspelleds = new HashMap<String, HashMap<String, Integer>>();
		for (String clean : cleanDataSet) {
			String[] word = clean.split(" ");
			for (int i = 0; i < word.length; i++) {
				word[i] = word[i].toLowerCase(Locale.ENGLISH);
				word[i] = "#" + word[i] + "#";
				if (possibleCorrect.containsKey(word[i])) {
					int a = possibleCorrect.get(word[i]);
					possibleCorrect.put(word[i], a + 1);

				} else {
					possibleCorrect.put(word[i], 1);
				}
			}

		}

		for (String misspelled : Main.misspelledOnes) {
			HashMap<String, Integer> tempMap = new HashMap<String, Integer>();
			for (String t : possibleCorrect.keySet()) {

				int distance = minDistance(misspelled, t);

				if (distance == 1) {

					if (possibleCorrectsOfMisspelleds.containsKey(misspelled)) {
						tempMap = possibleCorrectsOfMisspelleds.get(misspelled);
						int a = possibleCorrect.get(t);
						tempMap.put(t, a);
						possibleCorrectsOfMisspelleds.put(misspelled, tempMap);

					} else {
						tempMap.put(t, 1);
						possibleCorrectsOfMisspelleds.put(misspelled, tempMap);
					}

				}
			}
		}
		String pair = null;
		for (String miss : possibleCorrectsOfMisspelleds.keySet()) {

			for (String corr : possibleCorrectsOfMisspelleds.get(miss).keySet()) {
				if (miss.length() == corr.length()) { // subs
					for (int i = 0; i < miss.length(); i++) {
						if (miss.charAt(i) != corr.charAt(i)) {
							pair = miss.charAt(i) + "," + corr.charAt(i);
							if (SubPairs.containsKey(pair)) {
								int count = SubPairs.get(pair);
								SubPairs.put(pair, count + 1);
							} else {
								SubPairs.put(pair, 1);
							}

						}
					}

				}
				if (miss.length() < corr.length()) { // del

					String[] missChar = miss.split("");
					String[] corrChar = corr.split("");
					int a = 0;
					for (int i = 0; i < miss.length(); i++) {
						for (int j = i; j < corr.length(); j++) {
							if (missChar[i].equals(corrChar[j])) {
								break;
							} else {
								pair = miss.charAt(i) + "," + corr.charAt(j);
								if (DelPairs.containsKey(pair)) {
									int count = DelPairs.get(pair);
									DelPairs.put(pair, count + 1);
								} else {
									DelPairs.put(pair, 1);
								}
								a = 1;
								break;
							}
						}
						if (a == 1)
							break;
					}
				}
				if (miss.length() > corr.length()) { // ins

					String[] missChar = miss.split("");
					String[] corrChar = corr.split("");
					int a = 0;
					for (int i = 0; i < miss.length(); i++) {
						for (int j = i; j < corr.length(); j++) {
							if (missChar[i].equals(corrChar[j])) {
								break;
							} else {
								pair = miss.charAt(i) + "," + corr.charAt(j);
								if (InsPairs.containsKey(pair)) {
									int count = InsPairs.get(pair);
									InsPairs.put(pair, count + 1);
								} else {
									InsPairs.put(pair, 1);
								}

								a = 1;
								break;
							}
						}
						if (a == 1)
							break;
					}
				}
			}
		}
	}
}
