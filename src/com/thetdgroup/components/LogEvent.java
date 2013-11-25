package com.thetdgroup.components;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.thetdgroup.util.*;

public class LogEvent
{

	// log4j components
	private String date;
	private String time;
	private String origin;
	private String eventType;
	private String loggerName;

	// internal components
	private String eventName;
	private String eventData;

	public LogEvent()
	{
	date = "";
	time = "";
	origin = "";
	eventType = "";
	loggerName = "";
	eventName = "";
	eventData = "";
	}

	public LogEvent(List<String> data)
	{
		if (data.size() == 7)
		{
			date = data.get(0).trim();
			time = data.get(1).trim();
			origin = data.get(2).trim();
			eventType = data.get(3).trim();
			loggerName = data.get(4).trim();
			eventName = data.get(5).trim();
			eventData = data.get(6).trim();
		}
	}

	public boolean isValid()
	{
		return (!StringUtil.isEmpty(date)) && (!StringUtil.isEmpty(time))
									&& (!StringUtil.isEmpty(origin)) && (!StringUtil.isEmpty(eventType))
									&& (!StringUtil.isEmpty(loggerName)) && (!StringUtil.isEmpty(eventName))
									&& (!StringUtil.isEmpty(eventData));
	}

	public void setDate(String inDate)
	{
	date = inDate;
	}

	public String getDate()
	{
	return this.date;
	}

	public void setTime(String inTime)
	{
	time = inTime;
	}

	public String getTime()
	{
	return this.time;
	}

	public void setOrigin(String inProto)
	{
	origin = inProto;
	}

	public String getOrigin()
	{
	return this.origin;
	}

	public void setEventType(String inType)
	{
	eventType = inType;
	}

	public String getEventType()
	{
	return this.eventType;
	}

	public void setLoggerName(String inName)
	{
	loggerName = inName;
	}

	public String getLoggerName()
	{
	return this.loggerName;
	}

	// eventName
	public void setEventName(String inName)
	{
	eventName = inName;
	}

	public String getEventName()
	{
	return this.eventName;
	}

	// eventData
	public void setEventData(String inData)
	{
	eventData = inData;
	}

	public String getEventData()
	{
	return this.eventData;
	}

	public JSONObject toJSON()
	{
	JSONObject jsonEvent = new JSONObject();

	try
	{
		// Not all of the LogEvent attributes are jsonized
		// for the gui. I kept them around just for future cases.

		jsonEvent.put(ParserConstants.EVENT_DATE, this.date);
		jsonEvent.put(ParserConstants.EVENT_TIME, this.time);
		jsonEvent.put(ParserConstants.EVENT_LOG_TYPE, this.eventType);
		jsonEvent.put(ParserConstants.EVENT_NAME, this.eventName);
		jsonEvent.put(ParserConstants.EVENT_DATA, this.eventData);
		jsonEvent.put(ParserConstants.EVENT_LOGGER_NAME, this.loggerName);
		jsonEvent.put(ParserConstants.EVENT_ORIGIN, this.origin);
	} 
	catch (JSONException e)
	{
		e.printStackTrace();
	}

	return jsonEvent;
	}
}
