import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Read {

	public ArrayList<String> readInput(String path) {

		String line;
		try {
			ArrayList<String> results = new ArrayList<String>();
			BufferedReader readr = new BufferedReader(new FileReader(path));
			line = readr.readLine();

			while (line != null) {
				results.add(line);
				line = readr.readLine();
			}
			readr.close();

			return results;

		} catch (IOException iox) {
			return null;
		}
	}

}
