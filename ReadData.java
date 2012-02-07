/**
 * Class to read data into vertical representation and perform pre-processing.
 * In pre-processing all the level 1 patterns with support < minsup are pruned out
 */
package textmining.outputspace1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * @author Naveen Tirupattur
 *
 */
public class ReadData {

	/**	  
	 * @param Data set file name
	 * @param minimum support
	 * @return list of terms
	 */
	public HashSet<Term> readDataSet(String fileName, int minsup)
	{
		//Create a List to store frequent level 1 terms
		HashSet<Term> termsSet = new HashSet<Term>();
		CalculateSupport calculator = new CalculateSupport();

		try
		{
			//Read the dataset
			BufferedReader reader = new BufferedReader(new FileReader(fileName));

			String line = "";
			char c = 'A';
			//For each line in dataset
			while((line= reader.readLine()) != null)
			{
				//Create a term object
				Term t = new Term();

				TreeSet<String> patternSet = new TreeSet<String>();

				patternSet.add(String.valueOf(c++));

				//Set the pattern 
				t.setPattern(patternSet);

				//Create a Map to store VAT of pattern
				TreeMap<String, Integer> vat = new TreeMap<String, Integer>();

				int i=1;
				StringTokenizer tokenizer = new StringTokenizer(line);
				while(tokenizer.hasMoreTokens())
				{
					//put the document id and its frequency into vat
					vat.put("D"+i++, Integer.valueOf((tokenizer.nextToken())));
				}

				//Set the vat for term
				t.setVat(vat);

				//Perform pre=processing by pruning terms with support < minsup
				//Calculate support for each term and if it is greater than minsup add it to list

				t.setSupport(calculator.calculate(vat))	;			
				if(t.getSupport() >= minsup)
				{					
					//Add the term to terms list
					termsSet.add(t);
				}
			}			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return termsSet;
	}

	/**
	 * Method to get a set of items from file
	 * @param fileName
	 * @return  Set of Items
	 */
	public TreeSet<String> returnSet(String fileName)
	{
		//Initialize a new vector
		TreeSet<String> v = new TreeSet<String>();
		try 
		{
			//Read the file and add elements to set			
			BufferedReader reader = new BufferedReader(new FileReader(fileName));

			String line="";
			while((line = reader.readLine()) != null)
			{
				//Add elements to set				
				v.add(line);				
			}			

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return v;		
	}
	/** 
	 * @param termsSet
	 * @param documentsSet
	 * @return Map<term,Object>
	 */
	public HashSet<Term> readDocuments(TreeSet<String> patternsSet, TreeSet<String> documentsSet, String pathName, int minsup)
	{
		//Create a List to store frequent level 1 terms
		HashSet<Term> termsSet = new HashSet<Term>();
		
		//Create a List to store all terms
		HashSet<Term> allTermsSet = new HashSet<Term>();
		
		CalculateSupport calculator = new CalculateSupport();

		//For each term
		Iterator termIterator = patternsSet.iterator();
		while(termIterator.hasNext())
		{
			//Create a term object
			Term t = new Term();
			
			//Get the pattern			
			String pattern = (String) termIterator.next();		

			//Create a Set to store the pattern
			TreeSet<String> pSet = new TreeSet<String>();
			pSet.add(pattern);
			
			System.out.println("Pattern: "+pattern);

			//Create a map to store documents,frequency actually having this pattern
			TreeMap<String,Integer> vat = new TreeMap<String,Integer>();
			

			/*//Vector to store document ids returned by pubmed for this pattern
			TreeSet<String> docIds = getDocumentIds(pattern);			

			//Retain only document ids in main vector
			docIds.retainAll(documentsSet);	
			
			//System.out.println("Time taken for retaining all docIds: "+(System.currentTimeMillis() - startTime));

			System.out.println("Number of documents retained: "+ docIds.size());*/

			//For each document
			Iterator docIterator = documentsSet.iterator();

					
			while(docIterator.hasNext())
			{
				//Create a buffer for document id
				StringBuffer documentId = new StringBuffer();


				//Get the document id
				documentId.setLength(0);
				documentId.append(docIterator.next().toString());

				try
				{					
					//Create a file object
					File document = new File(pathName+documentId.toString().trim()+".txt");


					BufferedReader reader = new BufferedReader(new FileReader(document));

					//Buffer to store the words in file
					StringBuffer word = new StringBuffer();

					//Read the file
					String line = "";
					while((line = reader.readLine()) != null)
					{
						word.append(line);
					}
					reader.close();

					//Check if pattern exists in the document
					Pattern p = Pattern.compile(pattern);
					Matcher m = p.matcher(word.toString());
										
					int count = 0;
					
					//Count the frequency
					while(m.find()) {
						count++;
					}
					
					//Add the documentId and frequency to Map
					if(count > 0)						
					vat.put(documentId.toString(), count);				
					

				}catch(Exception e)
				{
					//System.out.println("Error: "+e.getMessage());
					//e.printStackTrace();
				}
			}
			//Set the pattern for Term
			t.setPattern(pSet);
			
			//Set the VAT for Term
			t.setVat(vat);
			
			//Perform pre=processing by pruning terms with support < minsup
			//Calculate support for each term and if it is greater than minsup add it to list
			t.setSupport(calculator.calculate(vat))	;			
			if(t.getSupport() >= minsup)
			{					
				//Add the term to terms list
				termsSet.add(t);
			}
			
			allTermsSet.add(t);			
		}

		writeTermsSetToFile(allTermsSet,".\\Data\\TermsSet.txt");
		return termsSet;
	}

	/**
	 * Gets the documentids for each term
	 * @param : Query String	  
	 * @return : Vector of document ids for each term
	 * 
	 */
	public TreeSet<String> getDocumentIds(String queryString)	
	{	
		//Vector of document ids
		TreeSet<String> docIds = new TreeSet<String>();

		queryString = queryString.replaceAll(" ","%20");

		//URL String
		String urlstring="http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&term="+queryString+"&retmax=1000000"+"&retmode=xml";		
		//System.out.println("URL String: "+ urlstring);

		//Create an instance of DocumentBuilderFactory
		DocumentBuilderFactory df = DocumentBuilderFactory.newInstance();


		//Create a DocumentBuilderObject
		DocumentBuilder builder;
		try {
			//Create a URL Object
			URL url = new URL(urlstring);

			builder = df.newDocumentBuilder();

			//Parse the document and create a DOM object
			Document document = builder.parse (url.openStream()) ;

			System.out.println("Total Documents: "+ document.getElementsByTagName("RetMax").item(0).getTextContent());

			//Get all the ID's
			NodeList idList = document.getElementsByTagName("Id");

			//Add all the new document IDs 
			for(int i =0; i<idList.getLength(); i++)
			{
				docIds.add(idList.item(i).getTextContent());			
			}	

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		return docIds;

	}

	/**	
	 * Write the termsMap to a file 
	 * @param termsMap
	 * @return void
	 */
	public void writeTermsSetToFile(Set<Term> termsSet, String fileName)
	{	

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));			

			//Write the map to a file
			Iterator it = termsSet.iterator();			
			while(it.hasNext())
			{	
				Term t = (Term)it.next();
				writer.write(t.getPattern()+" "+t.getSupport()+" "+t.getVat().toString());
				writer.newLine();
			}
			writer.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Reads the termsSet file and creates a Set
	 * @param fileName
	 * @return map of terms and documents set
	 */
	public HashSet<Term> readTermsSetFromFile(String fileName, int minsup)
	{
		//Create a List to store frequent level 1 terms
		HashSet<Term> termsSet = new HashSet<Term>();
		CalculateSupport calculator = new CalculateSupport();

		try
		{
			//Read from terms map file
			BufferedReader reader = new BufferedReader(new FileReader(fileName));

			String line = "";
			
			while((line = reader.readLine()) != null)
			{
				//Create a object of Term
				Term term = new Term();
				
				//Create a Set to store the pattern
				TreeSet<String> pSet = new TreeSet<String>();
				pSet.add(line.substring(line.indexOf("[")+1, line.indexOf("]")));
				
				//Set the pattern
				term.setPattern(pSet);
				
				TreeMap<String,Integer> vat = new TreeMap<String, Integer>();
				
				StringTokenizer tokenizer = new StringTokenizer(line.subSequence(line.indexOf("{")+1, line.indexOf("}")).toString(),",");				
				while(tokenizer.hasMoreTokens())
				{
					String token = tokenizer.nextToken();
					vat.put(token.substring(0,token.indexOf("=")).trim(), Integer.parseInt(token.substring(token.indexOf("=")+1).trim()));					
				}
				
				//Set the vat for pattern
				term.setVat(vat);
				
				/*if(calculator.calculate(vat) == Integer.parseInt(line.substring(line.indexOf("]")+2, line.indexOf("{") - 1)))
					System.out.println("My code is damn right");*/
				
				//Set the support for pattern
				term.setSupport(calculator.calculate(vat));
				
				//Add the term to set if it is frequent
				if(calculator.calculate(vat) >= minsup) termsSet.add(term);
								
			}
			reader.close();

		}catch(Exception e)
		{
			e.printStackTrace();
		}		
		return termsSet;
	}
	
	/**
	 * Method to replace term frequencies in VAT with TF-IDF scores
	 * @param Set of All terms
	 * @param Size of all documents
	 * @return Set of All terms with VAT's having TF-IDF scores
	 * 
	 */
	HashSet<Term> getTFIDF(HashSet<Term> alltermsSet, int documentsSize )
	{
		HashSet<Term> termsSet = new HashSet<Term>(alltermsSet);
		
		for(Term t: termsSet)
		{
			/*TreeMap<String,Integer> newPatternMap = new TreeMap<String, Integer>();			

			//I made a big mistake here of assigning a refrence to intersectionset instead of using constructor
			//Get the intersection of keys
			Set<String> interSectionSet = new TreeSet<String>(termAVAT.keySet());
			interSectionSet.retainAll(termBVAT.keySet());

			//For the keys in intersection, get the min(value)
			Iterator i = interSectionSet.iterator();
			while(i.hasNext())
			{
				String key = (String)i.next();
				//Add the intersection key,min(value) to map
				newPatternMap.put(key, Math.min(termAVAT.get(key), termBVAT.get(key)));
			}

			return newPatternMap;*/
			
			TreeMap<String,Integer> vat = t.getVat();
			
			double idf = Math.log((double)documentsSize/(double)vat.size())/Math.log(10);
			
			Iterator it = vat.keySet().iterator();
			while(it.hasNext())
			{				 
	            String key = (String)it.next();
	            int frequency = vat.get(key);	            
	            
	            int tfidf = (int)Math.round((double)idf * frequency);
	            vat.put(key, tfidf);
			}
			t.setVat(vat);			
		}
		
		writeTermsSetToFile(termsSet,".\\Data\\TFIDFTerms.txt");
		return termsSet;
	}
}
