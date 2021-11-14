package dinner.learn;

import dinner.model.Decision;
import dinner.model.Example;
import dinner.model.Attribute;

import java.util.*;

public class DecisionTree {

    public DecisionTree(List<Example> examples, List<Attribute> attributes) {
        createDecisionTree(examples, attributes);
    }

    public static void createDecisionTree(List<Example> examples, List<Attribute> attributes) {
        Attribute attribute = getMostImportantAttribute(examples, attributes);
        System.out.println("Node: " + attribute.toString());
        Map<Object, List<Example>> nodes = categorizeExamplesByAttributeValue(examples, attribute);
        List<Attribute> newAttributes = new ArrayList<Attribute>();
        newAttributes.addAll(attributes);
        newAttributes.remove(attribute);
        for (Object branch : nodes.keySet()) {
            List<Example> subset = nodes.get(branch);
            System.out.println();
            createDecisionTreeHelper(subset, newAttributes, attribute.toString(), branch.toString());
        }
    }

    private static void createDecisionTreeHelper(List<Example> examples, List<Attribute> attributes, String parent, String incomingBranchName) {
        System.out.println("Parent: " + parent);
        System.out.println("Branch: " + incomingBranchName);
        Decision decisionOne = examples.get(0).getDecision();
        boolean isDecided = true;
        for (int i = 1; i < examples.size(); i++) {
            if (examples.get(i).getDecision() != decisionOne) {
                isDecided = false;
                break;
            }
        }
        if (isDecided == false) {
            createDecisionTree(examples, attributes);
        } else {
            System.out.println("Decision (" + examples.size() + "): " + decisionOne.toString());
        }


    }

    public static Attribute getMostImportantAttribute(List<Example> examples, List<Attribute> attributes) {
        Map<Object, Map<Decision, Integer>> decisionMap;
        Attribute toReturn = null;
        double currentMax = 0.0;
        for (Attribute attribute : attributes) {
            decisionMap = generateDecisionMap(examples, attribute);
            if (getNumOutcomesDecided(decisionMap) > currentMax) {
                currentMax = getNumOutcomesDecided(decisionMap);
                toReturn = attribute;
            }
        }
        if (toReturn == null) {
            return attributes.get(0);
        } else {
            return toReturn;
        }
    }

    private static Map<Object, List<Example>> categorizeExamplesByAttributeValue(List<Example> examples, Attribute attribute) {
        Map<Object,List<Example>> attributeExampleMap = new TreeMap<>();
        Object attributeValue;
        List<Example> valueExamples;
        for (Example example : examples) {
            attributeValue = example.getAttributeValue(attribute);
            if (attributeExampleMap.containsKey(attributeValue)) {
                valueExamples = attributeExampleMap.get(attributeValue);
                valueExamples.add(example);
            } else {
                valueExamples = new ArrayList<>();
                valueExamples.add(example);
                attributeExampleMap.put(attributeValue, valueExamples);
            }
        }
        return attributeExampleMap;
    }

    private static Map<Object, Map<Decision, Integer>> generateDecisionMap(List<Example> examples, Attribute attribute) {
        Map<Object, Map<Decision, Integer>> decisionMap = new HashMap<>();
        Map<Decision, Integer> decisionCountMap;
        Object attributeValue;
        Decision decision;

        for(Example example: examples) {
            attributeValue = example.getAttributeValue(attribute);
            decision = example.getDecision();
            if(decisionMap.containsKey(attributeValue)) {
                decisionCountMap = decisionMap.get(attributeValue);
                if(decisionCountMap.containsKey(decision)) {
                    decisionCountMap.put(decision, decisionCountMap.get(decision)+1);
                } else {
                    decisionCountMap.put(decision, 1);
                }
            } else {
                decisionCountMap = new HashMap<>();
                decisionCountMap.put(decision, 1);
                decisionMap.put(attributeValue, decisionCountMap);
            }
        }
        return decisionMap;
    }
    
    private static double getNumOutcomesDecided(Map<Object, Map<Decision, Integer>> decisionMap) {
        int numDecided = 0;
        for (Object attrValue : decisionMap.keySet()) {
            if (decisionMap.get(attrValue).keySet().size() == 1) {
                numDecided++;
            }
        }
        return numDecided;
    }
}