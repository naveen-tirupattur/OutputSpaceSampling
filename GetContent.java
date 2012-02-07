/**
 * Class to read all documents and create a map
 */
package textmining.outputspace1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
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

import sun.reflect.generics.tree.Tree;

/**
 * @author Naveen Tirupattur
 *
 */
public class GetContent {

	/** 
	 * @param termsSet
	 * @param documentsSet
	 * @return Map<term,Object>
	 */
	public TreeMap<String,TreeSet<Object>> readDocuments(TreeSet<String> termsSet, TreeSet<String> documentsSet, String pathName)
	{
		TreeMap<String,TreeSet<Object>> termsMap = new TreeMap<String,TreeSet<Object>>();

		//For each term
		Iterator termIterator = termsSet.iterator();
		while(termIterator.hasNext())
		{
			//Get the term			
			String term = (String) termIterator.next();		

			System.out.println("Term: "+term);

			//Create a  to store documents actually having this term
			TreeSet<Object> tempDocumentSet = new TreeSet<Object>();

			long startTime = System.currentTimeMillis();

			//Vector to store document ids returned by pubmed for this term
			TreeSet<String> docIds = getDocumentIds(term);

			//System.out.println("Time taken for getting docIds set: "+(System.currentTimeMillis() - startTime));

			startTime = System.currentTimeMillis();
			
			//Retain only document ids in main vector
			docIds.retainAll(documentsSet);			
			//System.out.println("Time taken for retaining all docIds: "+(System.currentTimeMillis() - startTime));

			System.out.println("Number of documents retained: "+ docIds.size());

			//For each document
			Iterator docIterator = docIds.iterator();

			startTime = System.currentTimeMillis();			
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

					//Check if term exists in the document
					Pattern p = Pattern.compile(term);
					Matcher m = p.matcher(word.toString());

					//If yes, add the document to set
					if(m.find())
						tempDocumentSet.add(documentId.toString());

				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			//Check if tempDocumentSet is frequent
			//if(tempDocumentVector.size() > 100)
			termsMap.put(term, tempDocumentSet);
			System.out.println("Time taken for adding a term and all documens: "+(System.currentTimeMillis() - startTime));
		}

		writeTermsMapToFile(termsMap,".\\Data\\TermsMap.txt");
		return termsMap;
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
	public void writeTermsMapToFile(TreeMap<String, TreeSet<Object>> termsMap, String fileName)
	{	

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
			BufferedWriter termHitsWriter = new BufferedWriter(new FileWriter(".\\Data\\TermHits.txt"));

			//Write the map to a file
			Iterator it = termsMap.entrySet().iterator();			
			while(it.hasNext())
			{				
				writer.write(it.next().toString());
				writer.newLine();
			}
			writer.close();

			// Write the hits for each term
			it = termsMap.keySet().iterator();			
			while(it.hasNext())
			{
				String termName = it.next().toString();
				TreeSet<Object> t = termsMap.get(termName);				
				termHitsWriter.write(termName+"  "+t.size());
				termHitsWriter.newLine();
			}
			termHitsWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Reads the termsmap file and creates a map
	 * @param fileName
	 * @return map of terms and documents set
	 */
	public TreeMap<String,TreeSet<Object>> readTermsMapFromFile(String fileName)
	{
		TreeMap<String,TreeSet<Object>> termsMap = new TreeMap<String, TreeSet<Object>>();

		try
		{
			//Read from terms map file
			BufferedReader reader = new BufferedReader(new FileReader(fileName));

			String line = "";
			StringBuffer subString = new StringBuffer();
			StringBuffer documentIdSubString = new StringBuffer();

			while((line = reader.readLine()) != null)
			{
				subString.setLength(0);
				TreeSet<Object> documentMap = new TreeSet<Object>();

				//Get the index of first = to get term name
				int index = line.indexOf("=");

				//Tokenize the remaining string to get document id and term frequency in each document
				StringTokenizer tokenizer = new StringTokenizer(subString.append(line.subSequence(line.indexOf("[")+1, line.indexOf("]"))).toString().replaceAll(",",""));				
				while(tokenizer.hasMoreTokens())
				{					
					documentIdSubString.setLength(0);
					documentIdSubString.append(tokenizer.nextToken());				

					//put it in a document map
					documentMap.add(documentIdSubString.toString());
				}
				//put the term and documentmap into termmap
				termsMap.put(line.substring(0,index), documentMap);

				//System.out.println("Substring "+subString.append(line.subSequence(line.indexOf("{")+1, line.indexOf("}"))));
			}
			reader.close();

		}catch(Exception e)
		{
			e.printStackTrace();
		}		
		return termsMap;
	}

	/**
	 * method to create terms map from dataset which is in horizontal format
	 * @param data set file name, number of transactions, number of terms
	 */
	public TreeMap<String,TreeSet<Object>> createTermsMapFromDataSet(String fileName)
	{
		TreeMap<String,TreeSet<Object>> termsMap = new TreeMap<String, TreeSet<Object>>();

		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			String line="";			
			int i = 1;
			/*while(i < nRows)
			{*/				
				while((line = reader.readLine()) != null)
				{	
					i++;
					//System.out.println("Line: "+i);
					StringTokenizer tokenizer = new StringTokenizer(line);
					//System.out.println("Number of tokens: "+tokenizer.countTokens());
					while(tokenizer.hasMoreTokens())
					{
						String token = tokenizer.nextToken();
						//System.out.println("Token: "+Integer.parseInt(token.toString()));
						//if(Integer.parseInt(token) < nColumns)
						//{						 

						TreeSet<Object> tempSet = new TreeSet<Object>();
						if(termsMap.containsKey(token))
						{
							//System.out.println("Contains");
							tempSet = termsMap.get(token);
							tempSet.add(Integer.toString(i));	

						}else
						{								 
												
							//System.out.println("Not Contains");
							tempSet.add(Integer.toString(i));
							termsMap.put(token, tempSet);
						}
						termsMap.put(token, tempSet);							
						//}						
				}
				}				
			//}
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		writeTermsMapToFile(termsMap, ".\\Data\\TermsMap.txt");
		return termsMap;
	}
}
