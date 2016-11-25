package com.github.ledoyen.winter.examples.stocklogger;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

@Service
public class CurrencyConversionService {

	public BigDecimal convertCurrency(BigDecimal value, Currency from, Currency to) {
		if (value == null) {
			return BigDecimal.ZERO;
		}
		return value.multiply(new BigDecimal("2"));
	}
}
