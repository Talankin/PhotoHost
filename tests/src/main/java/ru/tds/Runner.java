/**
 *     10.12.2015
 *     Dmitry Talankin
 *     For I P R
 */

package ru.tds;

import cucumber.api.CucumberOptions;
import org.junit.runner.RunWith;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = {"tests/src/main/java/ru.tds.features"},
        glue = {"ru.tds.steps"},
        plugin = {"pretty",
                "json:target/cucumber.json",
                "junit:target/cucumber.xml"}
)
public class Runner {
}
