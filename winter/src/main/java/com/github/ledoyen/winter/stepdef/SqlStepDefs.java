package com.github.ledoyen.winter.stepdef;

import java.util.stream.Collectors;

import javax.inject.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.ledoyen.automocker.api.jdbc.Connections;
import com.github.ledoyen.automocker.api.jdbc.DataSources;
import com.github.ledoyen.automocker.api.jdbc.DatasourceLocator;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import cucumber.api.DataTable;
import cucumber.api.java.After;
import cucumber.api.java.en.Given;

public class SqlStepDefs {

	private static final Logger LOGGER = LoggerFactory.getLogger(SqlStepDefs.class);

	@Autowired
	private Provider<DatasourceLocator> datasourceLocator;

	private final ListMultimap<String, String> tablesToClean = ArrayListMultimap.create();

	@Given("^an SQL table (\\S+) containing$")
	public void a_call_on_service_method_is_made(String tableName, DataTable data) throws Exception {
		DataSources.populateTable(datasourceLocator.get()
				.getDataSource(), tableName, data.topCells(), data.cells(1));
		tablesToClean.put(datasourceLocator.get()
				.getName(), tableName);
	}

	@After
	public void afterScenario() {
		tablesToClean.asMap()
				.forEach((dsName, names) -> DataSources.doInConnection(datasourceLocator.get()
						.getDataSource(dsName), c -> {
							Connections.execute(c, "SET REFERENTIAL_INTEGRITY FALSE");
							names.forEach(name -> Connections.truncate(c, name));
							Connections.execute(c, "SET REFERENTIAL_INTEGRITY TRUE");
							LOGGER.info("[" + dsName + "] Tables (" + names.stream()
									.collect(Collectors.joining(", ")) + ") truncated");
						}));
		tablesToClean.clear();
	}
}
