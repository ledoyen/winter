package com.github.ledoyen.winter.examples.stocklogger;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

@Service
public class CurrencyConversionService {

	public BigDecimal convertCurrency(BigDecimal value, Currency from, Currency to) {
		return value.multiply(new BigDecimal("2"));
	}
}
