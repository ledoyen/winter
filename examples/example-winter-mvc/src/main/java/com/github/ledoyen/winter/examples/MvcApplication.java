package com.github.ledoyen.winter.examples;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Sample web application.<br/>
 * Run {@link #main(String[])} to launch.
 */
@SpringBootApplication
@RestController
public class MvcApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(MvcApplication.class);

	private final Map<Integer, Customer> database = new LinkedHashMap<>();
	private final AtomicInteger sequenceGenerator = new AtomicInteger();

	public static void main(String[] args) {
		SpringApplication.run(MvcApplication.class);
	}

	public MvcApplication() {
		LOGGER.info("Initiating web server");
	}

	@RequestMapping("/create_user")
	String createUser(@RequestParam("firstName") String firstName, @RequestParam("lastName") String lastName) {
		int id = sequenceGenerator.incrementAndGet();
		database.put(id, new Customer(firstName, lastName));
		return "User saved : id=" + id;
	}

	@RequestMapping("/list_users")
	List<String> listUsers() {
		return database.entrySet().stream().map(e -> e.getValue().getFirstName() + " " + e.getValue().getLastName()).collect(Collectors.toList());
	}
}
