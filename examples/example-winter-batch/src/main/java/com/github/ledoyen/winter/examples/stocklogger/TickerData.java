package com.github.ledoyen.winter.examples.stocklogger;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TickerData implements Serializable {

	private static final long serialVersionUID = 6463492770982487812L;

	private String symbol;
	private String name;
	private Date lastTradeDate;
	private BigDecimal open;
	private BigDecimal lastTrade;
	private String changePct;
	private BigDecimal openGBP;
	private BigDecimal lastTradeGBP;
}
