import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CleanDataSet {

	ArrayList<String> cleanDataSet;

	public ArrayList<String> Cleaning(ArrayList<String> trainSet) {

		HashMap<String, HashMap<String, Integer>> hashMap = new HashMap<String, HashMap<String, Integer>>();
		ArrayList<String> cleanDataSet = new ArrayList<String>();

		Pattern pattern = Pattern.compile(
				"<ERR targ=[?]?(\\w* *\'?-?[,]*\\w*) *\'?-?[,]*\\w* *\'?-?[,]*> (\\w* *\'?-?[,]*\\w*) *\'?-?[,]*\\w* *\'?-?[,]* </ERR>");
		for (String sentence : trainSet) {
			Matcher matcher = pattern.matcher(sentence);
			while (matcher.find()) {

				String match = matcher.group();

				String[] words = match.split("<ERR targ=");

				String[] correctOne = words[1].split("> ");

				String[] misspelledOne = correctOne[1].split("<");
				String[] misspelledOne1 = misspelledOne[0].split(" ");
				misspelledOne1[0] = "#" + misspelledOne1[0] + "#";
				misspelledOne1[0].toLowerCase(Locale.ENGLISH);
				Main.misspelledOnes.add(misspelledOne1[0]);

				HashMap<String, Integer> tempMap = new HashMap<String, Integer>();
				if (hashMap.containsKey(correctOne[0])) {
					tempMap = hashMap.get(correctOne[0]);
					if (tempMap.containsKey(misspelledOne[0])) {
						int a = tempMap.get(misspelledOne[0]);
						tempMap.put(misspelledOne[0], a + 1);
						hashMap.put(correctOne[0], tempMap);

					} else {
						tempMap.put(misspelledOne[0], 1);
						hashMap.put(correctOne[0], tempMap);
					}
				} else {
					tempMap.put(misspelledOne[0], 1);
					hashMap.put(correctOne[0], tempMap);
				}
				sentence = (sentence.replace(match, correctOne[0])); // replace the correct forms..

			}
			sentence = (sentence.replaceAll("^[\\W]|\\.|\\,", ""));
			if (sentence.length() == 0) // for empty lines..
				continue;
			cleanDataSet.add(sentence);

		}
		this.cleanDataSet = cleanDataSet;
		return cleanDataSet;
	}

}
