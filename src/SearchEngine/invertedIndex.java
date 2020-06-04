package SearchEngine;

import java.io.*;
import java.util.*;

import org.jsoup.Jsoup;

public class invertedIndex {

	Map<Integer, String> source;
	HashMap<String, List<Integer>> index;
	
	PorterStemmer ps = new PorterStemmer();
	
	List<String> stopwords = Arrays.asList("a", "able", "about",
			"across", "after", "all", "almost", "also", "am", "among", "an",
			"and", "any", "are", "as", "at", "be", "because", "been", "but",
			"by", "can", "cannot", "could", "dear", "did", "do", "does",
			"either", "else", "ever", "every", "for", "from", "get", "got",
			"had", "has", "have", "he", "her", "hers", "him", "his", "how",
			"however", "i", "if", "in", "into", "is", "it", "its", "just",
			"least", "let", "likely", "may", "me", "might", "most",
			"must", "my", "neither", "no", "nor", "not", "of", "off", "often",
			"on", "only", "or", "other", "our", "own", "rather", "said", "say",
			"says", "she", "should", "since", "so", "some", "than", "that",
			"the", "their", "them", "then", "there", "these", "they", "this",
			"tis", "to", "too", "twas", "us", "wants", "was", "we", "were",
			"what", "when", "where", "which", "while", "who", "whom", "why",
			"will", "with", "would", "yet", "you", "your");
	
	invertedIndex(){
		source = new HashMap<Integer, String>();
		index = new HashMap<String, List<Integer>>();
		
	}
	
public void indexHtmlFiles(String[] files) {
		
		int file_no = 1;
		
		for(String file: files) {
			
			try {
				File html = new File(file);
				org.jsoup.nodes.Document doc= Jsoup.parse(html, "UTF-8");
				source.put(file_no, file);
				String text = doc.body().text();
				
				String[] words = text.split("\\s");
				int pos = 0;
				for(String word: words) {
					word = word.toLowerCase();
					word = ps.stem(word);
					
					if(stopwords.contains(word)) {
						continue;
					}
					
					
					List<Integer> doc_id = index.get(word);
					if(doc_id == null) {
						doc_id = new LinkedList<Integer>();
						index.put(word, doc_id);
					}
					doc_id.add(file_no);
				}
				
			} catch (IOException e) {
				System.out.println("File" + file + "not found.");
			}
			
			file_no++;
		}
	}
	
	
	public void indexFiles(String[] files) {
		
		int file_no = 1;
		
		for(String file: files) {
			
			try {
				BufferedReader read_file = new BufferedReader(new FileReader(file));
				
				source.put(file_no, file);
				String line;
				
				while((line = read_file.readLine()) != null) {
					
					String[] words = line.split("\\s");
					for(String word: words) {
						word = word.toLowerCase();
						word = ps.stem(word);
						
						if(stopwords.contains(word)) {
							continue;
						}
						
						
						List<Integer> doc_id = index.get(word);
						if(doc_id == null) {
							doc_id = new LinkedList<Integer>();
							index.put(word, doc_id);
						}
						doc_id.add(file_no);
					}
				}
				
			} catch (IOException e) {
				System.out.println("File" + file + "not found.");
			}
			
			file_no++;
		}
	}
	
	
	public Set<String> searchQuery(String query) {
		
		String[] words = query.split("\\s");
		//HashSet<Integer> result = new HashSet<Integer>(index.get(words[0].toLowerCase()));
		Set<String> result = new HashSet<String>();;
		
		for(String word: words) {
			
			word = word.toLowerCase();
			word = ps.stem(word);
			List<Integer> doc_id = index.get(word);
			
			System.out.println(word);
			if(doc_id != null) {
				for(Integer i: doc_id) {
					result.add(source.get(i));
				}
			} else {
				System.out.print("No document found!!");
			}
			
		}
		

		for(String s: result) {
			System.out.print("  " + s);
		}
		System.out.println();
		
		return result;
	}
	
	public HashMap<String, Integer> searchQueryImp(String query) {
		
		String[] words = query.split("\\s");
		//HashSet<Integer> result = new HashSet<Integer>(index.get(words[0].toLowerCase()));
		HashMap<String, Integer> result = new HashMap<String, Integer>();;
		
		for(String word: words) {
			
			word = word.toLowerCase();
			word = ps.stem(word);
			List<Integer> doc_id = index.get(word);
			
			System.out.println(word);
			if(doc_id != null) {
				for(Integer i: doc_id) {
					if(result.containsKey(source.get(i))) {
						result.replace(source.get(i), result.get(source.get(i)) + 1);
					}else {
						result.put(source.get(i), 1);
					}
					
				}
			} else {
				System.out.print("No document found!!");
			}
			
		}
		
		
		return result;
	}
}
