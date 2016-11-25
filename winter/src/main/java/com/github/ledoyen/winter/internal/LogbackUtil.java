package com.github.ledoyen.winter.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.AppenderBase;

public class LogbackUtil {

	private static final List<ILoggingEvent> events = new ArrayList<>();

	public static void record() {
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		lc.getLoggerList()
				.forEach(l -> {
					Iterator<Appender<ILoggingEvent>> iterator = l.iteratorForAppenders();
					if (iterator.hasNext()) {
						Appender<ILoggingEvent> first = iterator.next();
						l.detachAppender(first);
						Appender<ILoggingEvent> filteredAppender = new AppenderBase<ILoggingEvent>() {
							@Override
							protected void append(ILoggingEvent event) {
								events.add(event);
								first.doAppend(event);
							}
						};
						filteredAppender.start();
						l.addAppender(filteredAppender);
					}
				});
	}

	public static void clear() {
		events.clear();
	}

	public static List<ILoggingEvent> getEvents() {
		return events;
	}
}
