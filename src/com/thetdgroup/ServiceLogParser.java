package com.thetdgroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.thetdgroup.components.LogEvent;
import com.thetdgroup.components.LogParser;
import com.thetdgroup.components.PaginatedParsingPackage;
import com.thetdgroup.util.StringUtil;

/**
 * ServiceLogParser.java -  a JSON wrapper class around the LogParser.java class.
 * 
 * @author jmendez
 *
 */

public final class ServiceLogParser{
	
	private LogParser parser;
	private String serviceLogName;
	private String serviceLogDirectory;
	private List<String> eventNameFilter;
	private List<String> eventTypeFilter;
	
	public ServiceLogParser(){
		parser = new LogParser();
		serviceLogName = "";
		serviceLogDirectory = "";
		eventNameFilter = new ArrayList<String>();
		eventTypeFilter = new ArrayList<String>();
	}
	
	public void init(){
		if(!StringUtil.isEmpty(serviceLogName) && !StringUtil.isEmpty(serviceLogDirectory)){
			parser.setFileLocation(serviceLogDirectory);
			parser.setLogName(serviceLogName);
			parser.clearEventsFilter();
			for(String filter : eventNameFilter){
				parser.addEventNameFilter(filter);
			}
			
			for(String filter : eventTypeFilter){
				parser.addEventTypeFilter(filter);
			}
		}
	}
	
	public void setServiceLogName(String inName){
		serviceLogName = inName;
	}
	
	public String getServiceLogName(){
		return this.serviceLogName;
	}
	
	public void setServiceLogDirectory(String inDirectory){
		serviceLogDirectory = inDirectory;
	}
	
	public String getServiceLogDirectory(){
		return this.serviceLogDirectory;
	}
	
	public void setEventNameFilter(List<String> eventFilters){
		eventNameFilter = eventFilters;
	}
	
	public void addEventNameFilter(String filter){
		eventNameFilter.add(filter);
	}
	
	public void clearEventNameFilter(){
		eventNameFilter.clear();
		parser.clearEventsFilter();
	}
	
	public void setEventTypeFilter(List<String> eventFilters){
		eventTypeFilter = eventFilters;
	}
	
	public void addEventTypeFilter(String filter){
		eventTypeFilter.add(filter);
	}
	
	public void clearEventTypeFilter(){
		eventTypeFilter.clear();
		parser.clearEventTypesFilter();
	}
	
	public JSONArray getPastEvents(int days, int pageSize, int pageStart, String eventType){
		JSONArray eventResults = new JSONArray();
		if(days <= 0){
			for(LogEvent currLog : parser.parsePastDatesPaged(days, pageSize, pageStart)){
				eventResults.put(currLog.toJSON());
			}
		}
		return eventResults;
	}
	
	public List<LogEvent> getTimeFrameEvents(Calendar from, Calendar to){
		return parser.parseTimeFrame(from, to);
	}
	
	public JSONObject getTimeFrameEventsPaged(Calendar from, Calendar to, int pageSize, int currentPage){
		JSONObject returnPackage = new JSONObject();
		JSONArray eventResults = new JSONArray();
		
		PaginatedParsingPackage eventsPackage = parser.parseTimeFramePaged(from, to, pageSize, currentPage);
		for(LogEvent currLog : eventsPackage.getEvents()){
			eventResults.put(currLog.toJSON());
		}
		
		try {
			returnPackage.put("events", eventResults);
			returnPackage.put("parsing_count", eventsPackage.getTotalEventsParsed());
			returnPackage.put("total_pages", eventsPackage.getTotalPages());
			returnPackage.put("start_rows", eventsPackage.getStartRows());
			returnPackage.put("end_rows", eventsPackage.getEndRows());
			returnPackage.put("total_rows", eventsPackage.getTotalRows());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return returnPackage;
	}

	public JSONObject getSingleDayEvents(Calendar requestedDate, int pageSize, int currentPage){
		JSONObject returnPackage = new JSONObject();
		JSONArray eventResults = new JSONArray();
		
		PaginatedParsingPackage eventsPackage = parser.parseSingleDatePaged(requestedDate, pageSize, currentPage);
		for(LogEvent currLog : eventsPackage.getEvents()){
			eventResults.put(currLog.toJSON());
		}
		
		try {
			returnPackage.put("events", eventResults);
			returnPackage.put("parsing_count", eventsPackage.getTotalEventsParsed());
			returnPackage.put("total_pages", eventsPackage.getTotalPages());
			returnPackage.put("start_rows", eventsPackage.getStartRows());
			returnPackage.put("end_rows", eventsPackage.getEndRows());
			returnPackage.put("total_rows", eventsPackage.getTotalRows());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return returnPackage;
	}
}
