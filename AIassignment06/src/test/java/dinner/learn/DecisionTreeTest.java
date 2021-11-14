package dinner.learn;

import dinner.model.Attribute;
import dinner.model.DayOfWeek;
import dinner.model.Decision;
import dinner.model.Example;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class DecisionTreeTest {

    private List<Attribute> attributes;
    private PrintStream ps;
    private ByteArrayOutputStream baos;

    @BeforeEach
    public void setUp() throws Exception {
        attributes = new ArrayList<>(Arrays.asList(Attribute.ALL_EXAMPLE_ATTRIBUTES));
        baos = new ByteArrayOutputStream();
        ps = new PrintStream(baos, true, StandardCharsets.UTF_8.name());
    }

    @Test
    public void testMostImportantAttribute() {
        try {
            List<Example> examplesOne = getExamplesOne();
            List<Example> examplesThree = getExamplesThree();
            List<Attribute> testAttributes = new ArrayList<>(attributes);

            Attribute mostImportantAttribute = DecisionTree.getMostImportantAttribute(examplesOne, testAttributes);
            Attribute expectedAttribute = Attribute.DAY;
            assertEquals(expectedAttribute, mostImportantAttribute, "getMostImportantAttribute method not returning the most important attribute");

            mostImportantAttribute = DecisionTree.getMostImportantAttribute(examplesThree, testAttributes);
            assertEquals(expectedAttribute, mostImportantAttribute, "getMostImportantAttribute method not returning the most important attribute");

            testAttributes.remove(Attribute.DAY);
            mostImportantAttribute = DecisionTree.getMostImportantAttribute(examplesOne, testAttributes);
            expectedAttribute = Attribute.ALREADY_ATE_PIZZA;
            assertEquals(expectedAttribute, mostImportantAttribute, "getMostImportantAttribute method not returning the most important attribute");

            mostImportantAttribute = DecisionTree.getMostImportantAttribute(examplesThree, testAttributes);
            expectedAttribute = Attribute.HAVE_LEFTOVERS;
            assertEquals(expectedAttribute, mostImportantAttribute, "getMostImportantAttribute method not returning the most important attribute");

        } catch(Exception e) {
            e.printStackTrace();
            fail("check the console for the exception stack trace");
        }
    }

    @Test
    public void testCategorizeExamplesByAttributeValue() {
        try {
            Method categorizeExamplesByAttributeValue = DecisionTree.class.getDeclaredMethod("categorizeExamplesByAttributeValue", List.class, Attribute.class);
            categorizeExamplesByAttributeValue.setAccessible(true);
            List<Example> examplesOne = getExamplesOne();
            List<Example> examplesThree = getExamplesThree();

            Map<Object, List<Example>> examplesByAttributeValue = (Map<Object, List<Example>>) categorizeExamplesByAttributeValue.invoke(null, examplesOne, Attribute.HAVE_LEFTOVERS);
            assertEquals(2, examplesByAttributeValue.size(), "categorizeExamplesByAttributeValue not returning map with 2 entries when passed examplesOne and HAVE_LEFTOVERS");
            assertEquals(6, examplesByAttributeValue.get(true).size(), "categorizeExamplesByAttributeValue not returning map containing entry: {true - [6 examples]} when passed examplesOne and HAVE_LEFTOVERS");
            assertEquals(9, examplesByAttributeValue.get(false).size(), "categorizeExamplesByAttributeValue not returning map containing entry: {false - [9 examples]} when passed examplesOne and HAVE_LEFTOVERS");

            examplesByAttributeValue = (Map<Object, List<Example>>) categorizeExamplesByAttributeValue.invoke(null, examplesOne, Attribute.DAY);
            assertEquals(7, examplesByAttributeValue.size(), "categorizeExamplesByAttributeValue not returning map with 7 entries when passed examplesOne and DAY");
            assertEquals(1, examplesByAttributeValue.get(DayOfWeek.SATURDAY).size(), "categorizeExamplesByAttributeValue not returning map containing entry: {SATURDAY - [1 example]} when passed examplesOne and DAY");
            assertEquals(1, examplesByAttributeValue.get(DayOfWeek.SUNDAY).size(), "categorizeExamplesByAttributeValue not returning map containing entry: {SUNDAY - [1 example]} when passed examplesOne and DAY");
            assertEquals(2, examplesByAttributeValue.get(DayOfWeek.MONDAY).size(), "categorizeExamplesByAttributeValue not returning map containing entry: {MONDAY - [2 examples]} when passed examplesOne and DAY");
            assertEquals(2, examplesByAttributeValue.get(DayOfWeek.TUESDAY).size(), "categorizeExamplesByAttributeValue not returning map containing entry: {TUESDAY - [2 examples]} when passed examplesOne and DAY");
            assertEquals(3, examplesByAttributeValue.get(DayOfWeek.WEDNESDAY).size(), "categorizeExamplesByAttributeValue not returning map containing entry: {WEDNESDAY - [3 examples]} when passed examplesOne and DAY");
            assertEquals(2, examplesByAttributeValue.get(DayOfWeek.THURSDAY).size(), "categorizeExamplesByAttributeValue not returning map containing entry: {THURSDAY - [2 examples]} when passed examplesOne and DAY");
            assertEquals(4, examplesByAttributeValue.get(DayOfWeek.FRIDAY).size(), "categorizeExamplesByAttributeValue not returning map containing entry: {FRIDAY - [4 examples]} when passed examplesOne and DAY");

            examplesByAttributeValue = (Map<Object, List<Example>>) categorizeExamplesByAttributeValue.invoke(null, examplesThree, Attribute.HAVE_LEFTOVERS);
            assertEquals(2, examplesByAttributeValue.size(), "categorizeExamplesByAttributeValue not returning map with 2 entries when passed examplesThree and HAVE_LEFTOVERS");
            assertEquals(19, examplesByAttributeValue.get(true).size(), "categorizeExamplesByAttributeValue not returning map containing entry: {true - [19 examples]} when passed examplesThree and HAVE_LEFTOVERS");
            assertEquals(24, examplesByAttributeValue.get(false).size(), "categorizeExamplesByAttributeValue not returning map containing entry: {false - [24 examples]} when passed examplesThree and HAVE_LEFTOVERS");

            examplesByAttributeValue = (Map<Object, List<Example>>) categorizeExamplesByAttributeValue.invoke(null, examplesThree, Attribute.DAY);
            assertEquals(7, examplesByAttributeValue.size(), "categorizeExamplesByAttributeValue not returning map with 7 entries when passed examplesThree and DAY");
            assertEquals(3, examplesByAttributeValue.get(DayOfWeek.SATURDAY).size(), "categorizeExamplesByAttributeValue not returning map containing entry: {SATURDAY - [3 examples]} when passed examplesThree and DAY");
            assertEquals(3, examplesByAttributeValue.get(DayOfWeek.SUNDAY).size(), "categorizeExamplesByAttributeValue not returning map containing entry: {SUNDAY - [3 examples]} when passed examplesThree and DAY");
            assertEquals(6, examplesByAttributeValue.get(DayOfWeek.MONDAY).size(), "categorizeExamplesByAttributeValue not returning map containing entry: {MONDAY - [6 examples]} when passed examplesThree and DAY");
            assertEquals(6, examplesByAttributeValue.get(DayOfWeek.TUESDAY).size(), "categorizeExamplesByAttributeValue not returning map containing entry: {TUESDAY - [6 examples]} when passed examplesThree and DAY");
            assertEquals(8, examplesByAttributeValue.get(DayOfWeek.WEDNESDAY).size(), "categorizeExamplesByAttributeValue not returning map containing entry: {WEDNESDAY - [8 examples]} when passed examplesThree and DAY");
            assertEquals(6, examplesByAttributeValue.get(DayOfWeek.THURSDAY).size(), "categorizeExamplesByAttributeValue not returning map containing entry: {THURSDAY - [6 examples]} when passed examplesThree and DAY");
            assertEquals(11, examplesByAttributeValue.get(DayOfWeek.FRIDAY).size(), "categorizeExamplesByAttributeValue not returning map containing entry: {FRIDAY - [11 examples]} when passed examplesThree and DAY");
        } catch(Exception e) {
            e.printStackTrace();
            fail("check the console for the exception stack trace");
        }
    }

    @Test
    public void testDecisionTreeCreationOne() {
        try {
            List<Example> examples = getExamplesOne();
            String expectedOutput = getExpectedExampleOutputOne();
            testExamplesHelper(examples, expectedOutput);
        } catch(Exception e) {
            e.printStackTrace();
            fail("check the console for the exception stack trace");
        }
    }

    @Test
    public void testDecisionTreeCreationTwo() {
        try {
            List<Example> examples = getExamplesTwo();
            String expectedOutput = getExpectedExampleOutputTwo();
            testExamplesHelper(examples, expectedOutput);
        } catch(Exception e) {
            e.printStackTrace();
            fail("check the console for the exception stack trace");
        }
    }

    @Test
    public void testDecisionTreeCreationThree() {
        try {
            List<Example> examples = getExamplesThree();
            String expectedOutput = getExpectedExampleOutputThree();
            testExamplesHelper(examples, expectedOutput);
        } catch(Exception e) {
            e.printStackTrace();
            fail("check the console for the exception stack trace");
        }
    }

    private void testExamplesHelper(List<Example> examples, String expectedOutput) throws UnsupportedEncodingException {
        PrintStream stdOut = System.out;
        System.setOut(ps);
        DecisionTree decisionTree = new DecisionTree(examples, attributes);
        System.setOut(stdOut);
        String output = baos.toString(StandardCharsets.UTF_8.name()).trim();

        String[] outputArr = output.split("\n");
        String[] expectedOutputArr = expectedOutput.split("\n");

        for (int i=0; i<outputArr.length && i<expectedOutputArr.length; i++) {
            assertEquals(expectedOutputArr[i], outputArr[i], "your output doesn't match the expected output");
        }
        assertEquals(expectedOutputArr.length, outputArr.length, "your output length is incorrect");
    }

    private List<Example> getExamplesOne() {
        List<Example> examples = new ArrayList<Example>();
        examples.add(new Example(DayOfWeek.SATURDAY, false, true, false, false, false, false, Decision.RESTAURANT));
        examples.add(new Example(DayOfWeek.SUNDAY, false, false, false, false, false, false, Decision.BIG_RECIPE));
        examples.add(new Example(DayOfWeek.FRIDAY, true, true, true, false, false, true, Decision.LEFTOVERS));
        examples.add(new Example(DayOfWeek.FRIDAY, false, false, false, true, false, false, Decision.PIZZA));
        examples.add(new Example(DayOfWeek.FRIDAY, false, true, true, true, false, true, Decision.PIZZA));
        examples.add(new Example(DayOfWeek.FRIDAY, false, false, false, true, true, false, Decision.SCRAPS));
        examples.add(new Example(DayOfWeek.THURSDAY, true, false, true, false, false, true, Decision.LEFTOVERS));
        examples.add(new Example(DayOfWeek.THURSDAY, false, true, false, false, false, true, Decision.FAST_FOOD));
        examples.add(new Example(DayOfWeek.WEDNESDAY, true, true, true, false, false, true, Decision.LEFTOVERS));
        examples.add(new Example(DayOfWeek.WEDNESDAY, false, false, true, true, false, false, Decision.PIZZA));
        examples.add(new Example(DayOfWeek.WEDNESDAY, false, false, false, false, false, true, Decision.SCRAPS));
        examples.add(new Example(DayOfWeek.TUESDAY, true, false, true, false, false, true, Decision.LEFTOVERS));
        examples.add(new Example(DayOfWeek.TUESDAY, false, false, false, false, false, false, Decision.SMALL_RECIPE));
        examples.add(new Example(DayOfWeek.MONDAY, true, false, false, false, false, true, Decision.LEFTOVERS));
        examples.add(new Example(DayOfWeek.MONDAY, true, false, true, false, false, false, Decision.SMALL_RECIPE));
        return examples;
    }

    private List<Example> getExamplesTwo() {
        List<Example> examples = getExamplesOne();
        examples.add(new Example(DayOfWeek.SATURDAY, true, true, true, true, false, false, Decision.RESTAURANT));
        examples.add(new Example(DayOfWeek.SUNDAY, false, false, false, true, true, false, Decision.BIG_RECIPE));
        examples.add(new Example(DayOfWeek.FRIDAY, false, false, true, false, false, true, Decision.SCRAPS));
        examples.add(new Example(DayOfWeek.FRIDAY, false, true, true, true, false, true, Decision.PIZZA));
        examples.add(new Example(DayOfWeek.FRIDAY, true, true, false, false, true, true, Decision.LEFTOVERS));
        examples.add(new Example(DayOfWeek.THURSDAY, true, false, true, false, true, true, Decision.LEFTOVERS));
        examples.add(new Example(DayOfWeek.THURSDAY, false, true, false, true, false, true, Decision.FAST_FOOD));
        examples.add(new Example(DayOfWeek.WEDNESDAY, true, false, false, true, false, true, Decision.LEFTOVERS));
        examples.add(new Example(DayOfWeek.WEDNESDAY, false, true, false, true, false, true, Decision.PIZZA));
        examples.add(new Example(DayOfWeek.WEDNESDAY, false, true, true, false, false, false, Decision.SCRAPS));
        examples.add(new Example(DayOfWeek.TUESDAY, true, true, false, true, false, false, Decision.LEFTOVERS));
        examples.add(new Example(DayOfWeek.TUESDAY, false, true, true, true, false, true, Decision.SMALL_RECIPE));
        examples.add(new Example(DayOfWeek.MONDAY, true, false, true, true, false, true, Decision.LEFTOVERS));
        examples.add(new Example(DayOfWeek.MONDAY, true, false, false, true, false, false, Decision.SMALL_RECIPE));
        return examples;
    }

    private List<Example> getExamplesThree() {
        List<Example> examples = getExamplesTwo();
        examples.add(new Example(DayOfWeek.SATURDAY, false, true, false, true, false, true, Decision.RESTAURANT));
        examples.add(new Example(DayOfWeek.SUNDAY, false, false, true, false, true, false, Decision.BIG_RECIPE));
        examples.add(new Example(DayOfWeek.FRIDAY, true, false, true, false, false, true, Decision.LEFTOVERS));
        examples.add(new Example(DayOfWeek.FRIDAY, false, true, false, true, false, true, Decision.PIZZA));
        examples.add(new Example(DayOfWeek.FRIDAY, false, true, true, false, false, true, Decision.SCRAPS));
        examples.add(new Example(DayOfWeek.FRIDAY, false, true, false, false, true, true, Decision.SCRAPS));
        examples.add(new Example(DayOfWeek.THURSDAY, true, true, false, true, false, true, Decision.LEFTOVERS));
        examples.add(new Example(DayOfWeek.THURSDAY, false, true, true, false, true, true, Decision.FAST_FOOD));
        examples.add(new Example(DayOfWeek.WEDNESDAY, true, false, false, true, false, true, Decision.LEFTOVERS));
        examples.add(new Example(DayOfWeek.WEDNESDAY, false, false, true, true, false, false, Decision.PIZZA));
        examples.add(new Example(DayOfWeek.TUESDAY, true, false, false, false, false, true, Decision.LEFTOVERS));
        examples.add(new Example(DayOfWeek.TUESDAY, false, false, true, true, false, true, Decision.SMALL_RECIPE));
        examples.add(new Example(DayOfWeek.MONDAY, true, false, true, true, false, true, Decision.LEFTOVERS));
        examples.add(new Example(DayOfWeek.MONDAY, true, false, false, true, false, false, Decision.SMALL_RECIPE));
        return examples;
    }

    private String getExpectedExampleOutputOne() {
        String newlineChar = System.lineSeparator();
        return "Node: DAY" + newlineChar +
                newlineChar +
                "Parent: DAY" + newlineChar +
                "Branch: SUNDAY" + newlineChar +
                "Decision (1): BIG_RECIPE" + newlineChar +
                "" + newlineChar +
                "Parent: DAY" + newlineChar +
                "Branch: MONDAY" + newlineChar +
                "Node: REALLY_HUNGRY" + newlineChar +
                "" + newlineChar +
                "Parent: REALLY_HUNGRY" + newlineChar +
                "Branch: false" + newlineChar +
                "Decision (1): LEFTOVERS" + newlineChar +
                "" + newlineChar +
                "Parent: REALLY_HUNGRY" + newlineChar +
                "Branch: true" + newlineChar +
                "Decision (1): SMALL_RECIPE" + newlineChar +
                "" + newlineChar +
                "Parent: DAY" + newlineChar +
                "Branch: TUESDAY" + newlineChar +
                "Node: HAVE_LEFTOVERS" + newlineChar +
                "" + newlineChar +
                "Parent: HAVE_LEFTOVERS" + newlineChar +
                "Branch: false" + newlineChar +
                "Decision (1): SMALL_RECIPE" + newlineChar +
                "" + newlineChar +
                "Parent: HAVE_LEFTOVERS" + newlineChar +
                "Branch: true" + newlineChar +
                "Decision (1): LEFTOVERS" + newlineChar +
                "" + newlineChar +
                "Parent: DAY" + newlineChar +
                "Branch: WEDNESDAY" + newlineChar +
                "Node: HAVE_LEFTOVERS" + newlineChar +
                "" + newlineChar +
                "Parent: HAVE_LEFTOVERS" + newlineChar +
                "Branch: false" + newlineChar +
                "Node: REALLY_HUNGRY" + newlineChar +
                "" + newlineChar +
                "Parent: REALLY_HUNGRY" + newlineChar +
                "Branch: false" + newlineChar +
                "Decision (1): SCRAPS" + newlineChar +
                "" + newlineChar +
                "Parent: REALLY_HUNGRY" + newlineChar +
                "Branch: true" + newlineChar +
                "Decision (1): PIZZA" + newlineChar +
                "" + newlineChar +
                "Parent: HAVE_LEFTOVERS" + newlineChar +
                "Branch: true" + newlineChar +
                "Decision (1): LEFTOVERS" + newlineChar +
                "" + newlineChar +
                "Parent: DAY" + newlineChar +
                "Branch: THURSDAY" + newlineChar +
                "Node: HAVE_LEFTOVERS" + newlineChar +
                "" + newlineChar +
                "Parent: HAVE_LEFTOVERS" + newlineChar +
                "Branch: false" + newlineChar +
                "Decision (1): FAST_FOOD" + newlineChar +
                "" + newlineChar +
                "Parent: HAVE_LEFTOVERS" + newlineChar +
                "Branch: true" + newlineChar +
                "Decision (1): LEFTOVERS" + newlineChar +
                "" + newlineChar +
                "Parent: DAY" + newlineChar +
                "Branch: FRIDAY" + newlineChar +
                "Node: HAVE_LEFTOVERS" + newlineChar +
                "" + newlineChar +
                "Parent: HAVE_LEFTOVERS" + newlineChar +
                "Branch: false" + newlineChar +
                "Node: ALREADY_ATE_PIZZA" + newlineChar +
                "" + newlineChar +
                "Parent: ALREADY_ATE_PIZZA" + newlineChar +
                "Branch: false" + newlineChar +
                "Decision (2): PIZZA" + newlineChar +
                "" + newlineChar +
                "Parent: ALREADY_ATE_PIZZA" + newlineChar +
                "Branch: true" + newlineChar +
                "Decision (1): SCRAPS" + newlineChar +
                "" + newlineChar +
                "Parent: HAVE_LEFTOVERS" + newlineChar +
                "Branch: true" + newlineChar +
                "Decision (1): LEFTOVERS" + newlineChar +
                "" + newlineChar +
                "Parent: DAY" + newlineChar +
                "Branch: SATURDAY" + newlineChar +
                "Decision (1): RESTAURANT";
    }

    private String getExpectedExampleOutputTwo() {
        String newlineChar = System.lineSeparator();
        return "Node: DAY" + newlineChar +
                "" + newlineChar +
                "Parent: DAY" + newlineChar +
                "Branch: SUNDAY" + newlineChar +
                "Decision (2): BIG_RECIPE" + newlineChar +
                "" + newlineChar +
                "Parent: DAY" + newlineChar +
                "Branch: MONDAY" + newlineChar +
                "Node: TIRED" + newlineChar +
                "" + newlineChar +
                "Parent: TIRED" + newlineChar +
                "Branch: false" + newlineChar +
                "Decision (2): SMALL_RECIPE" + newlineChar +
                "" + newlineChar +
                "Parent: TIRED" + newlineChar +
                "Branch: true" + newlineChar +
                "Decision (2): LEFTOVERS" + newlineChar +
                "" + newlineChar +
                "Parent: DAY" + newlineChar +
                "Branch: TUESDAY" + newlineChar +
                "Node: HAVE_LEFTOVERS" + newlineChar +
                "" + newlineChar +
                "Parent: HAVE_LEFTOVERS" + newlineChar +
                "Branch: false" + newlineChar +
                "Decision (2): SMALL_RECIPE" + newlineChar +
                "" + newlineChar +
                "Parent: HAVE_LEFTOVERS" + newlineChar +
                "Branch: true" + newlineChar +
                "Decision (2): LEFTOVERS" + newlineChar +
                "" + newlineChar +
                "Parent: DAY" + newlineChar +
                "Branch: WEDNESDAY" + newlineChar +
                "Node: HAVE_LEFTOVERS" + newlineChar +
                "" + newlineChar +
                "Parent: HAVE_LEFTOVERS" + newlineChar +
                "Branch: false" + newlineChar +
                "Node: HAVE_PIZZA" + newlineChar +
                "" + newlineChar +
                "Parent: HAVE_PIZZA" + newlineChar +
                "Branch: false" + newlineChar +
                "Decision (2): SCRAPS" + newlineChar +
                "" + newlineChar +
                "Parent: HAVE_PIZZA" + newlineChar +
                "Branch: true" + newlineChar +
                "Decision (2): PIZZA" + newlineChar +
                "" + newlineChar +
                "Parent: HAVE_LEFTOVERS" + newlineChar +
                "Branch: true" + newlineChar +
                "Decision (2): LEFTOVERS" + newlineChar +
                "" + newlineChar +
                "Parent: DAY" + newlineChar +
                "Branch: THURSDAY" + newlineChar +
                "Node: HAVE_LEFTOVERS" + newlineChar +
                "" + newlineChar +
                "Parent: HAVE_LEFTOVERS" + newlineChar +
                "Branch: false" + newlineChar +
                "Decision (2): FAST_FOOD" + newlineChar +
                "" + newlineChar +
                "Parent: HAVE_LEFTOVERS" + newlineChar +
                "Branch: true" + newlineChar +
                "Decision (2): LEFTOVERS" + newlineChar +
                "" + newlineChar +
                "Parent: DAY" + newlineChar +
                "Branch: FRIDAY" + newlineChar +
                "Node: HAVE_LEFTOVERS" + newlineChar +
                "" + newlineChar +
                "Parent: HAVE_LEFTOVERS" + newlineChar +
                "Branch: false" + newlineChar +
                "Node: SICK_OF_LEFTOVERS" + newlineChar +
                "" + newlineChar +
                "Parent: SICK_OF_LEFTOVERS" + newlineChar +
                "Branch: false" + newlineChar +
                "Node: REALLY_HUNGRY" + newlineChar +
                "" + newlineChar +
                "Parent: REALLY_HUNGRY" + newlineChar +
                "Branch: false" + newlineChar +
                "Node: ALREADY_ATE_PIZZA" + newlineChar +
                "" + newlineChar +
                "Parent: ALREADY_ATE_PIZZA" + newlineChar +
                "Branch: false" + newlineChar +
                "Decision (1): PIZZA" + newlineChar +
                "" + newlineChar +
                "Parent: ALREADY_ATE_PIZZA" + newlineChar +
                "Branch: true" + newlineChar +
                "Decision (1): SCRAPS" + newlineChar +
                "" + newlineChar +
                "Parent: REALLY_HUNGRY" + newlineChar +
                "Branch: true" + newlineChar +
                "Decision (1): SCRAPS" + newlineChar +
                "" + newlineChar +
                "Parent: SICK_OF_LEFTOVERS" + newlineChar +
                "Branch: true" + newlineChar +
                "Decision (2): PIZZA" + newlineChar +
                "" + newlineChar +
                "Parent: HAVE_LEFTOVERS" + newlineChar +
                "Branch: true" + newlineChar +
                "Decision (2): LEFTOVERS" + newlineChar +
                "" + newlineChar +
                "Parent: DAY" + newlineChar +
                "Branch: SATURDAY" + newlineChar +
                "Decision (2): RESTAURANT";
    }

    private String getExpectedExampleOutputThree() {
        String newlineChar = System.lineSeparator();
        return "Node: DAY" + newlineChar +
                "" + newlineChar +
                "Parent: DAY" + newlineChar +
                "Branch: SUNDAY" + newlineChar +
                "Decision (3): BIG_RECIPE" + newlineChar +
                "" + newlineChar +
                "Parent: DAY" + newlineChar +
                "Branch: MONDAY" + newlineChar +
                "Node: TIRED" + newlineChar +
                "" + newlineChar +
                "Parent: TIRED" + newlineChar +
                "Branch: false" + newlineChar +
                "Decision (3): SMALL_RECIPE" + newlineChar +
                "" + newlineChar +
                "Parent: TIRED" + newlineChar +
                "Branch: true" + newlineChar +
                "Decision (3): LEFTOVERS" + newlineChar +
                "" + newlineChar +
                "Parent: DAY" + newlineChar +
                "Branch: TUESDAY" + newlineChar +
                "Node: HAVE_LEFTOVERS" + newlineChar +
                "" + newlineChar +
                "Parent: HAVE_LEFTOVERS" + newlineChar +
                "Branch: false" + newlineChar +
                "Decision (3): SMALL_RECIPE" + newlineChar +
                "" + newlineChar +
                "Parent: HAVE_LEFTOVERS" + newlineChar +
                "Branch: true" + newlineChar +
                "Decision (3): LEFTOVERS" + newlineChar +
                "" + newlineChar +
                "Parent: DAY" + newlineChar +
                "Branch: WEDNESDAY" + newlineChar +
                "Node: HAVE_LEFTOVERS" + newlineChar +
                "" + newlineChar +
                "Parent: HAVE_LEFTOVERS" + newlineChar +
                "Branch: false" + newlineChar +
                "Node: HAVE_PIZZA" + newlineChar +
                "" + newlineChar +
                "Parent: HAVE_PIZZA" + newlineChar +
                "Branch: false" + newlineChar +
                "Decision (2): SCRAPS" + newlineChar +
                "" + newlineChar +
                "Parent: HAVE_PIZZA" + newlineChar +
                "Branch: true" + newlineChar +
                "Decision (3): PIZZA" + newlineChar +
                "" + newlineChar +
                "Parent: HAVE_LEFTOVERS" + newlineChar +
                "Branch: true" + newlineChar +
                "Decision (3): LEFTOVERS" + newlineChar +
                "" + newlineChar +
                "Parent: DAY" + newlineChar +
                "Branch: THURSDAY" + newlineChar +
                "Node: HAVE_LEFTOVERS" + newlineChar +
                "" + newlineChar +
                "Parent: HAVE_LEFTOVERS" + newlineChar +
                "Branch: false" + newlineChar +
                "Decision (3): FAST_FOOD" + newlineChar +
                "" + newlineChar +
                "Parent: HAVE_LEFTOVERS" + newlineChar +
                "Branch: true" + newlineChar +
                "Decision (3): LEFTOVERS" + newlineChar +
                "" + newlineChar +
                "Parent: DAY" + newlineChar +
                "Branch: FRIDAY" + newlineChar +
                "Node: HAVE_LEFTOVERS" + newlineChar +
                "" + newlineChar +
                "Parent: HAVE_LEFTOVERS" + newlineChar +
                "Branch: false" + newlineChar +
                "Node: HAVE_PIZZA" + newlineChar +
                "" + newlineChar +
                "Parent: HAVE_PIZZA" + newlineChar +
                "Branch: false" + newlineChar +
                "Decision (3): SCRAPS" + newlineChar +
                "" + newlineChar +
                "Parent: HAVE_PIZZA" + newlineChar +
                "Branch: true" + newlineChar +
                "Node: ALREADY_ATE_PIZZA" + newlineChar +
                "" + newlineChar +
                "Parent: ALREADY_ATE_PIZZA" + newlineChar +
                "Branch: false" + newlineChar +
                "Decision (4): PIZZA" + newlineChar +
                "" + newlineChar +
                "Parent: ALREADY_ATE_PIZZA" + newlineChar +
                "Branch: true" + newlineChar +
                "Decision (1): SCRAPS" + newlineChar +
                "" + newlineChar +
                "Parent: HAVE_LEFTOVERS" + newlineChar +
                "Branch: true" + newlineChar +
                "Decision (3): LEFTOVERS" + newlineChar +
                "" + newlineChar +
                "Parent: DAY" + newlineChar +
                "Branch: SATURDAY" + newlineChar +
                "Decision (3): RESTAURANT";
    }
}
