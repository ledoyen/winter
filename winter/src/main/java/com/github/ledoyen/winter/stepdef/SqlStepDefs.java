package com.github.ledoyen.winter.stepdef;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Provider;

import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.ledoyen.automocker.extension.sql.Connections;
import com.github.ledoyen.automocker.extension.sql.DataSources;
import com.github.ledoyen.automocker.extension.sql.DatasourceLocator;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import cucumber.api.DataTable;
import cucumber.api.java.After;
import cucumber.api.java.en.Given;

public class SqlStepDefs {

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
							// TODO fix this, not working
							List<String> copy = Lists.newArrayList(names);
							Collections.sort(copy, Comparator.reverseOrder());
							names.forEach(name -> Connections.truncate(c, name));
						}));
		tablesToClean.clear();
	}
}
