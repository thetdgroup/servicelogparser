package com.thetdgroup.components;

import java.util.ArrayList;
import java.util.List;


public class PaginatedParsingPackage {

	private List<LogEvent> events;
	private int totalEventsParsed;
	private int totalPages;
	private int startRows;
	private int endRows;
	private int totalRows;
	
	public PaginatedParsingPackage(){
		events = new ArrayList<LogEvent>();
		totalEventsParsed = 0;
	}

	public void setTotalRows(int rows){
		totalRows = rows;
	}
	
	public int getTotalRows(){
		return this.totalRows;
	}
	
	public void setStartRows(int rows){
		startRows = rows;
	}
	
	public int getStartRows(){
		return this.startRows;
	}
	
	public void setEndRows(int rows){
		endRows = rows;
	}
	
	public int getEndRows(){
		return this.endRows;
	}
	
	public void addEvent(LogEvent event){
		events.add(event);
	}
	
	public void addEvents(List<LogEvent> events){
		for(LogEvent currentEvent: events){
			this.events.add(currentEvent);
		}
	}
	
	public List<LogEvent> getEvents(){
		return this.events;
	}
	
	public void incrementEventsParsed(int count){
		totalEventsParsed = totalEventsParsed + count;
	}
	
	public void setTotalEventsParsed(int count){
		totalEventsParsed = count;
	}
	
	public void setTotalPages(int pages){
		totalPages = pages;
	}
	
	public int getTotalPages(){
		return this.totalPages;
	}
	
	public int getTotalEventsParsed(){
		return this.totalEventsParsed;
	}
}
