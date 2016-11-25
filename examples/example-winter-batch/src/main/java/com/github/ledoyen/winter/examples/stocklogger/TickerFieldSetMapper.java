package com.github.ledoyen.winter.examples.stocklogger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class TickerFieldSetMapper implements FieldSetMapper<TickerData> {

	private static final Logger LOGGER = LoggerFactory.getLogger(TickerFieldSetMapper.class);

	public TickerData mapFieldSet(FieldSet fieldSet) throws BindException {
		TickerData data = new TickerData();
		data.setSymbol(fieldSet.readString(0));
		data.setName(fieldSet.readString(1));
		data.setLastTradeDate(fieldSet.readDate(2, "mm/DD/yyyy"));
		try {
			data.setOpen(fieldSet.readBigDecimal(3));
		} catch (NumberFormatException e) {
			LOGGER.debug("Cannot parse open value");
		}
		data.setLastTrade(fieldSet.readBigDecimal(4));
		data.setChangePct(fieldSet.readString(5));
		return data;
	}
}
