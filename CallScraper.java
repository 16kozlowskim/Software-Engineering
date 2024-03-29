import java.io.*;

// A demonstration of how to call the scraper from Java. Try changing the String cmd.

public class CallScraper {

	// using https://alvinalexander.com/java/edu/pj/pj010016

	public static void main(String args[]) { 
	
		String cmd = "python Scrape.py currentprice ABF";
		String s = null;

		try {
			Process p = Runtime.getRuntime().exec(cmd);
			
			BufferedReader stdInput = new BufferedReader(new 
			InputStreamReader(p.getInputStream()));

			BufferedReader stdError = new BufferedReader(new 
			InputStreamReader(p.getErrorStream()));

			// read the output from the command
			System.out.println("Here is the standard output of the command:\n");
			while ((s = stdInput.readLine()) != null) {
				System.out.println(s);
			}
			
			// read any errors from the attempted command
			System.out.println("Here is the standard error of the command (if any):\n");
			while ((s = stdError.readLine()) != null) {
				System.out.println(s);
			}
			
			System.exit(0);
		}
		catch (IOException e) {
			System.out.println("exception happened - here's what I know: ");
			e.printStackTrace();
			System.exit(-1);
		}
	
	}  
	
}