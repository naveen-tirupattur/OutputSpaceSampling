/**
 * Class to read a list from file and return a sorted vector
 */
package textmining.outputspace1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.TreeSet;


/**
 * @author Naveen Tirupattur
 *
 */
public class ReadList {
	
	@SuppressWarnings("unchecked")
	public TreeSet<String> returnSet(String fileName)
	{
		//Initialize a new vector
		TreeSet<String> v = new TreeSet<String>();
		try 
		{
			//Read the file and add elements to set			
			BufferedReader reader = new BufferedReader(new FileReader(".\\Data\\"+fileName));
			
			//Write sorted vector to file 
			BufferedWriter writer = new BufferedWriter(new FileWriter(".\\Data\\Sorted"+fileName));
			String line="";
			while((line = reader.readLine()) != null)
			{
				//Add elements to set				
				v.add(line);				
			}		
			
			//Iterate over set
			Iterator it = v.iterator();
			while(it.hasNext())
			{
				writer.write((String)it.next());
				writer.newLine();				
			}
			writer.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return v;		
	}

}
