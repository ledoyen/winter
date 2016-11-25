Feature: Batch execution

Scenario: nominal
	Given logs are recorded
	When the job TickerPriceConversion is launched
	Then execution is a success
	And logger com.github.ledoyen.winter.examples.batch.LogItemWriter have been used 4 times
