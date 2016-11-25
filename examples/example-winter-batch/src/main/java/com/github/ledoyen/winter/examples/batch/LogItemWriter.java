package com.github.ledoyen.winter.examples.batch;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

public class LogItemWriter implements ItemWriter<Object> {

	private static final Logger LOG = LoggerFactory.getLogger(LogItemWriter.class);

	public void write(List<? extends Object> items) throws Exception {
		for (Object item : items) {
			LOG.info(item.toString());
		}
	}
}
