package test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

public class ParseRussian {
	
	static HashMap<Character, Integer> alphabet = new HashMap<Character, Integer>(); 
	
	
	public static void readAlphabet(String filename) {
		String line;
		InputStream fis;
		try {
			fis = new FileInputStream(filename);
			InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
			BufferedReader br = new BufferedReader(isr);
			int i = 0; 
			while ((line = br.readLine()) != null) {
				char rusChar = line.charAt(0);
				alphabet.put(rusChar, i);
				i ++; 
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static int getIntValue (char letter){
		return alphabet.get(letter);
	}

	public static char getCharValue (int num){
		return getKeyByValue(alphabet, num);
	}
	
	public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
	    for (Entry<T, E> entry : map.entrySet()) {
	        if (Objects.equals(value, entry.getValue())) {
	            return entry.getKey();
	        }
	    }
	    return null;
	}
}
