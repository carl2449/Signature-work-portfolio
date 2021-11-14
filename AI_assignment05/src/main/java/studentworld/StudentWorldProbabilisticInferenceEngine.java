package studentworld;

import java.util.ArrayList;
import java.util.List;


import studentworld.grid.StudentWorldBoard;

public class StudentWorldProbabilisticInferenceEngine {
	
	public static String getTargetCell(List<String> allFrontierCells, List<String> smellyCells) {
		//TODO: implement
		List<String> riskyFrontierCells = generateRiskyFrontierCells(allFrontierCells);
		String toReturn = "";
		double lowestProb = 2.0;
		for (String riskyCell : riskyFrontierCells) {
			double probability = calculateStudentProbabilityForCell(riskyCell, allFrontierCells, smellyCells);
			if (probability < lowestProb) {
				lowestProb = probability;
				toReturn = riskyCell;
			}
		}
		return toReturn;
	}
	
	public static double calculateStudentProbabilityForCell(String riskyFrontierCellToFix, List<String> allFrontierCells, List<String> smellyCells) {
		//TODO: implement
		String stringCopy = riskyFrontierCellToFix;
		double trueSum = calculateProbabilitySum(riskyFrontierCellToFix, true, allFrontierCells, smellyCells);
		riskyFrontierCellToFix = stringCopy;
		double falseSum = calculateProbabilitySum(riskyFrontierCellToFix, false, allFrontierCells, smellyCells);
		riskyFrontierCellToFix = stringCopy;
		return (0.2*trueSum)/((0.2*trueSum)+(0.8*falseSum));

	}
	
	public static double calculateProbabilitySum(String riskyFrontierCellToFix, boolean riskyFrontierCellHasStudent, List<String> allFrontierCells, List<String> smellyCells) {
		StudentWorldProbabilisticInferenceEngineConstraintSolver swpiecs = new StudentWorldProbabilisticInferenceEngineConstraintSolver();
		allFrontierCells = new ArrayList<>(allFrontierCells);
		String riskyCell;

		//update model to assume a student is or is not present in the given cell
		for(int i=0; i<allFrontierCells.size(); i++) {
			riskyCell = allFrontierCells.get(i);
			if(riskyCell.equals(riskyFrontierCellToFix)) {
				if(riskyFrontierCellHasStudent) {
					riskyCell = riskyCell.replace("= [0,1]", "= 1").replace("student", "fixed-student");
				} else {
					riskyCell = riskyCell.replace("= [0,1]", "= 0").replace("student", "fixed-student");
				}
				allFrontierCells.set(i, riskyCell);
				break;
			}
		}
		//return the probability sum given student presence or no student presence in the given cell
		return swpiecs.generateStateProbabilitySum(allFrontierCells, smellyCells);
	}
	
	private static List<String> generateRiskyFrontierCells(List<String> allFrontierCells) {
		List<String> riskyFrontierCells = new ArrayList<>();
		String riskyCell;
		for(int i=0; i<allFrontierCells.size(); i++) {
			riskyCell = allFrontierCells.get(i);
			if(riskyCell.startsWith("student") && riskyCell.endsWith("1")) {
				riskyCell = riskyCell.replace("student", "fixed-student");
				allFrontierCells.set(i, riskyCell);
			} else {
				riskyFrontierCells.add(riskyCell);
			}
		}
		return riskyFrontierCells;
	}
}