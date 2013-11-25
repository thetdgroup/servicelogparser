package com.thetdgroup.components;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * LogParser.java - a class that parses FuzeIn services log files, has parsing
 * time frame logic, and supports paging and filters.
 * 
 * @author jmendez
 * 
 */

public class LogParser
{
	private String directory;

	private String logName;

	private Set<String> eventNameFilter;

	private Set<String> eventTypeFilter;

	private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

	private String splitDelimiter;

	private StringSplitter splitter;

	private BufferedReader br;

	private Calendar calendar;

	public LogParser()
	{
	directory = "";
	logName = "";
	eventNameFilter = new HashSet<String>();
	eventTypeFilter = new HashSet<String>();
	splitDelimiter = ParserConstants.LOG_EVENT_DELIMETER;
	
	splitter = new StringSplitter();
	splitter.setDelimiter(splitDelimiter);
	}

	public void setFileLocation(String location)
	{
	directory = location;
	}

	public void setLogName(String inName)
	{
	logName = inName;
	}

	public void addEventNameFilter(String filter)
	{
	eventNameFilter.add(filter);
	}

	public void clearEventsFilter()
	{
	if (eventNameFilter.size() > 0)
	{
		eventNameFilter.clear();
	}
	}

	public void addEventTypeFilter(String filter)
	{
	eventTypeFilter.add(filter);
	}

	public void clearEventTypesFilter()
	{
	if (eventTypeFilter.size() > 0)
	{
		eventTypeFilter.clear();
	}
	}

	// ~ Helper methods
	// ----------------------------------------------------------------------------
	private List<LogEvent> parseFile(Calendar date)
	{
	String formattedDate = formatter.format(date.getTime());
	boolean today = formattedDate.equals(getTodaysDate());
	StringBuilder fileToParse = new StringBuilder(directory)
			.append(logName)
			.append(
					today ? ParserConstants.LOG_EXTENSION : ParserConstants.LOG_EXTENSION_DOT)
			.append(today ? "" : formattedDate);
	List<LogEvent> events = new ArrayList<LogEvent>();
	File existingFile = new File(fileToParse.toString());
	
	if (existingFile.exists())
	{
		try
		{
			String lineData;
			LogEvent currEvent;
			br = new BufferedReader(new FileReader(existingFile));

			while (((lineData = br.readLine()) != null))
			{
				List<String> splitResults = splitter.split_1(lineData);
					
				if ((eventNameFilter.size() == 0) && eventTypeFilter.size() == 0)
				{
					currEvent = new LogEvent(splitResults);
					
					if (currEvent.isValid())
					{
						events.add(currEvent);
					}
				} 
				else
				{
					if(splitResults.size() > 2 && eventTypeFilter.contains(splitResults.get(3).trim()))
					{
						currEvent = new LogEvent(splitResults);
						if (currEvent.isValid())
						{
							events.add(currEvent);
						}
					}
					
					if(splitResults.size() > 4 && eventNameFilter.contains(splitResults.get(5).trim()))
					{
						currEvent = new LogEvent(splitResults);
						if (currEvent.isValid())
						{
							events.add(currEvent);
						}
					}
				}
			}
		} 
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	return events;
	}

	private String getTodaysDate()
	{
	calendar = Calendar.getInstance();
	return formatter.format(calendar.getTime());
	}

	// ~ Parser logic methods
	// ----------------------------------------------------------------------

	// ~ Parse Past Dates with paging
	public List<LogEvent> parsePastDatesPaged(int pastDays, int pageSize,
			int currentPage)
	{
	Calendar runningDate = Calendar.getInstance();
	List<LogEvent> runningEventList;
	List<LogEvent> parsedEvents = new ArrayList<LogEvent>();

	int counter = 0;
	while (counter >= pastDays)
	{
		runningEventList = parseFile(runningDate);
		for (LogEvent currentEvent : runningEventList)
		{
			parsedEvents.add(currentEvent);
		}
		runningDate.add(Calendar.DATE, -1);
		counter--;
	}

	Paginator paginator = new Paginator();
	paginator.setPageSize(pageSize);
	paginator.setRows(parsedEvents.size());
	paginator.setScrollerControlCurrentPage(currentPage);

	return parsedEvents.subList(paginator.getStart() - 1, paginator.getEnd());
	}

	// ~ Parse Past Dates with no paging
	public List<LogEvent> parsePastDates(int pastDays)
	{
	Calendar runningDate = Calendar.getInstance();
	List<LogEvent> runningEventList;
	List<LogEvent> parsedEvents = new ArrayList<LogEvent>();

	int counter = 0;
	while (counter >= pastDays)
	{
		runningEventList = parseFile(runningDate);
		for (LogEvent currentEvent : runningEventList)
		{
			parsedEvents.add(currentEvent);
		}
		runningDate.add(Calendar.DATE, -1);
		counter--;
	}

	return parsedEvents;
	}

	// ~ Parse Time Frame with paging
	public PaginatedParsingPackage parseTimeFramePaged(Calendar startDate,
			Calendar endDate, int pageSize, int currentPage)
	{

	List<LogEvent> runningEventList;
	List<LogEvent> parsedEvents = new ArrayList<LogEvent>();

	if (endDate.after(startDate))
	{
		while (endDate.after(startDate) || endDate.equals(startDate))
		{
			runningEventList = parseFile(startDate);
			for (LogEvent currentEvent : runningEventList)
			{
				parsedEvents.add(currentEvent);
			}
			startDate.add(Calendar.DATE, 1);
		}
	} else if (startDate.after(endDate))
	{
		while (startDate.after(endDate) || startDate.equals(endDate))
		{
			runningEventList = parseFile(startDate);
			for (LogEvent currentEvent : runningEventList)
			{
				parsedEvents.add(currentEvent);
			}

			startDate.add(Calendar.DATE, -1);
		}
	} else if (startDate.equals(endDate))
	{
		parsedEvents = parseFile(startDate);
	}

	Paginator paginator = new Paginator();
	paginator.setPageSize(pageSize);
	paginator.setRows(parsedEvents.size());
	paginator.setScrollerControlCurrentPage(currentPage);
	int remainder = parsedEvents.size() % pageSize;
	int whole = parsedEvents.size() / pageSize;
	int totalPages = remainder == 0 ? whole : whole + 1;

	PaginatedParsingPackage parsedLogActivity = new PaginatedParsingPackage();
	parsedLogActivity.setTotalEventsParsed(parsedEvents.size());
	parsedLogActivity.addEvents(parsedEvents.subList(paginator.getStart() - 1,
			paginator.getEnd()));
	parsedLogActivity.setTotalPages(totalPages);
	parsedLogActivity.setStartRows(paginator.getStart());
	parsedLogActivity.setEndRows(paginator.getEnd());
	parsedLogActivity.setTotalRows(paginator.getRows());

	return parsedLogActivity;
	}

	// ~ Parse Time Frame with no paging
	public List<LogEvent> parseTimeFrame(Calendar startDate, Calendar endDate)
	{

	List<LogEvent> runningEventList;
	List<LogEvent> parsedEvents = new ArrayList<LogEvent>();

	if (endDate.after(startDate))
	{
		while (endDate.after(startDate) || endDate.equals(startDate))
		{
			runningEventList = parseFile(startDate);
			for (LogEvent currentEvent : runningEventList)
			{
				parsedEvents.add(currentEvent);
			}
			startDate.add(Calendar.DATE, 1);
		}
	} else if (startDate.after(endDate))
	{
		while (startDate.after(endDate) || startDate.equals(endDate))
		{
			runningEventList = parseFile(startDate);
			for (LogEvent currentEvent : runningEventList)
			{
				parsedEvents.add(currentEvent);
			}

			startDate.add(Calendar.DATE, -1);
		}
	} else if (startDate.equals(endDate))
	{
		parsedEvents = parseFile(startDate);
	}

	return parsedEvents;
	}

	// ~ Parse Single Date with paging
	public PaginatedParsingPackage parseSingleDatePaged(Calendar date,
			int pageSize, int currentPage)
	{
	PaginatedParsingPackage parsedLogActivity = new PaginatedParsingPackage();

	if (date.before(Calendar.getInstance()) || date.equals(Calendar.getInstance()))
	{

		List<LogEvent> parsedEvents = parseFile(date);
		Paginator paginator = new Paginator();
		paginator.setPageSize(pageSize);
		paginator.setRows(parsedEvents.size());
		paginator.setScrollerControlCurrentPage(currentPage);
		int remainder = parsedEvents.size() % pageSize;
		int whole = parsedEvents.size() / pageSize;
		int totalPages = remainder == 0 ? whole : whole + 1;

		parsedLogActivity.setTotalEventsParsed(parsedEvents.size());
		parsedLogActivity.addEvents(parsedEvents.subList(paginator.getStart() - 1,
				paginator.getEnd()));
		parsedLogActivity.setTotalPages(totalPages);
		parsedLogActivity.setStartRows(paginator.getStart());
		parsedLogActivity.setEndRows(paginator.getEnd());
		parsedLogActivity.setTotalRows(paginator.getRows());
	}

	return parsedLogActivity;
	}

	// ~ Parse Single Date with no paging
	public List<LogEvent> parseSingleDate(Calendar date)
	{
	List<LogEvent> parsedEvents = new ArrayList<LogEvent>();

	if (date.before(Calendar.getInstance()) || date.equals(Calendar.getInstance()))
	{

		parsedEvents = parseFile(date);
	}

	return parsedEvents;
	}
}
