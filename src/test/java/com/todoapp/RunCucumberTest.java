package com.todoapp;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        plugin = {"pretty"},
        glue = {"com.todoapp.steps"}
)
public class RunCucumberTest {
}
