package com.thetdgroup.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringSplitter {

	private String delimiter;
	
	public StringSplitter(){

	}
	
	public void setDelimiter(String delimiter){
		this.delimiter = delimiter;
	}
	
	public List<String> split(String line)
	{
		List<String> results = new ArrayList<String>();
		
		int end = line.indexOf(delimiter);
		int lineLength = line.length();
		if(end > -1){
			int start = 0;
			
			while(end > -1){
				results.add(line.substring(start, end));
				start = end +1;
				end = line.indexOf(delimiter, start);
			}
			
			if((end == -1) && (start < lineLength)){
				results.add(line.substring(start, lineLength));				
			}
		}
			
		return results;
	}
	
	public List<String> split_1(String line)
	{
	 String[] stringArray = line.split("[|]");
			
		return Arrays.asList(stringArray);  
	}
	
}
