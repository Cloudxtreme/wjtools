import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.*;

// args parsing library
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;


/**
 * jtail application (replacement for GNU tail)
 * @author webnull
 *
 */

class jtailApp {
	/**
	 * Initialize app, parse arguments etc.
	 * @param argv
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @author Damian Kęska
	 */
	
	public static void main(String[] argv) throws FileNotFoundException, IOException, InterruptedException
	{
		ArgumentParser parser = ArgumentParsers.newArgumentParser("jtail");
		parser.defaultHelp(true);
		
		// avaliable arguments
		parser.addArgument("-s", "--string").help("Only if given string found");
		parser.addArgument("file").nargs("*");
		
		// here we will store values
		Map<String, String> values = new HashMap<String, String>();
	    String file = new String();
		ArrayList<String> files = new ArrayList<String>(16);
		
		Namespace res;
		try {
			// save arguments in memory
            res = parser.parseArgs(argv);
            values.put("string", (String) res.get("string"));
            files = (ArrayList) res.get("file");
            
            if (!files.isEmpty())
            {
            	file = files.get(0);
            } else
            	printHelp();

        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }
		
		File f = new File(file);
		
		// check if file exists
		if (f.exists())
		{
			mainLoop(file, values.get("string"));
		} else {
			System.out.println("Error opening file, check if file exists");
			System.exit(1);
		}
	}
	
	public static void printHelp()
	{
		System.out.println("Please specify an input file");
		System.exit(0);
	}
	
	/**
	 * Main loop, monitors file for changes and prints file contents when specified string found
	 * @param file
	 * @param matchString
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	
	public static void mainLoop(String file, String matchString) throws FileNotFoundException, IOException, InterruptedException
	{
		// initialize some variables
		Long lastmod = (long) 0;
		File f = new File(file);
		String contents = "";
		
		//System.out.println("Looking for "+matchString);
		//Thread.sleep(1000);
		
		while (true)
		{
			f = new File(file);
			
			// always check if file exists
			if (!f.exists())
			{
				Thread.sleep(100);
				continue;
			}
			
			try {
				// load file contents
				contents = file_get_contents(file);
				
				// print contents only if file was modified
				if (lastmod != f.lastModified())
				{
					if (matchString == "" || matchString == null)
					{
						clearScreen();
						System.out.println(contents);
						
					} else {
						// if we specified a filter
						if (contents.contains(matchString))
						{
							clearScreen();
							System.out.println(contents);
						}
					}
				}
			} catch (IOException e) {
				// nothing
			}

			
			lastmod = f.lastModified();
			Thread.sleep(100);
		}
	}
	
	public static void clearScreen()
	{
		final String ANSI_CLS = "\u001b[2J";
        final String ANSI_HOME = "\u001b[H";
        System.out.print(ANSI_CLS + ANSI_HOME);
        System.out.flush();
	}
	
	/**
	 * Get file contents
	 * @param String fileName
	 * @return String contents
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @author Damian Kęska
	 */
	
	public static String file_get_contents(String fileName) throws FileNotFoundException, IOException
	{
		FileReader input = new FileReader(fileName);
		StringBuilder content = new StringBuilder();
		
		char[] buffer = new char[4096];
		int position = 0;
		
		while (true)
		{
			position = input.read(buffer);
			
			if (position <= 0)
			{
				break;
			}
			
			content.append(buffer, 0, position);
		}
		
		input.close();
		
		return content.toString();
		
		
	}
	
	public static void var_dump(Object o)
	{
		Field[] fields = o.getClass().getDeclaredFields();
		for (int i=0; i<fields.length; i++) {
		    try {
		        System.out.println(fields[i].getName() + " - " + fields[i].get(o));
		    } catch (java.lang.IllegalAccessException e) {
		        System.out.println(e); 
		    }
		}
	}
}