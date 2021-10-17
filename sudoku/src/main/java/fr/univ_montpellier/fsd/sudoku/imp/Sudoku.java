package fr.univ_montpellier.fsd.sudoku.imp;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class Sudoku {

	int n;
	int s;
	int[][] grid;

	/*
	 * =============================================================================
	 * Create an instance of the problem sudoku (nxn)
	 * =============================================================================
	 */

	public Sudoku(int n) {
		this.n = n;
		this.s = (int) Math.sqrt(n);
		this.grid = new int[n][n];
	}

	/*
	 * =============================================================================
	 * check if this.grid is a correct sudoku solution.
	 * =============================================================================
	 */

	private boolean solutionChecker() {
		for (int i = 0; i < n; i++) {
			if (!checkRow(i) || !checkColumn(i)) {
				return false;
			}
		}

		for (int i = 0; i < s; i++) {
			for (int j = 0; j < s; j++) {
				if (!checkSquare(i, j)) {
					return false;
				}
			}
		}

		return true;
	}

	private boolean checkRow(int k) {
		HashSet<Integer> distinct = new HashSet<>(n);

		for (int j = 0; j < n; j++) {
			if (grid[k][j] == 0) {
				// noop
			} else if (distinct.contains(grid[k][j])) {
				return false;
			} else if (j < n - 1) {
				distinct.add(grid[k][j]);
			}
		}

		return true;
	}

	private boolean checkColumn(int k) {
		HashSet<Integer> distinct = new HashSet<>(n);

		for (int i = 0; i < n; i++) {
			if (grid[i][k] == 0) {
				// noop
			} else if (distinct.contains(grid[i][k])) {
				return false;
			} else if (i < n - 1) {
				distinct.add(grid[i][k]);
			}
		}

		return true;
	}

	private boolean checkSquare(int k, int l) {
		HashSet<Integer> distinct = new HashSet<>(n);

		for (int i = 0; i < s; i++) {
			for (int j = 0; j < s; j++) {
				int p = k * s + i;
				int q = l * s + j;
				if (grid[p][q] == 0) {
					// noop
				} else if (distinct.contains(grid[p][q])) {
					return false;
				} else if (i + j < n - 1) {
					distinct.add(grid[p][q]);
				}
			}
		}

		return true;
	}

	/************************************
	 * check if the value val is already used in column col
	 * 
	 * @param val
	 * @param col
	 * @return true/false
	 *************************************/

	public boolean alreadyInCol(int val, int col) {
		for (int i = 0; i < n; i++) {
			if (grid[i][col] == val) {
				return true;
			}
		}
		return false;
	}

	/************************************
	 * check if the value val is already used in row row
	 * 
	 * @param val
	 * @param row
	 * @return true/false
	 *************************************/

	public boolean alreadyInRow(int val, int row) {
		for (int j = 0; j < n; j++) {
			if (grid[row][j] == val) {
				return true;
			}
		}
		return false;
	}

	/*************************************
	 * check if the value val is already used in the square containing the cell
	 * grid[row][col]
	 * 
	 * @param val
	 * @param row
	 * @param col
	 * @return true/false
	 *************************************/

	public boolean alreadyInSquare(int val, int row, int col) {
		for (int k = 0; k < s; k++) {
			for (int l = 0; l < s; l++) {
				int i = row / s * s + k;
				int j = col / s * s + l;
				if (grid[i][j] == val) {
					return true;
				}
			}
		}
		return false;
	}

	/*************************************
	 * check if the value val is:
	 * 1- already used in column col
	 * 2- already used in row row
	 * 3- already used in the square containing the cell grid[row][col]
	 * 
	 * @param val
	 * @param row
	 * @param col
	 * @return true/false
	 *************************************/
	public boolean isPossible(int valeur, int row, int col) {
		return !alreadyInCol(valeur, col)
				&& !alreadyInRow(valeur, row)
				&& !alreadyInSquare(valeur, row, col);
	}

	/*
	 * =============================================================================
	 * Generate a random grid solution
	 * =============================================================================
	 */

	private void generateSolution() {
		// méthode basée sur le décalage d'une ligne
		ArrayList<Integer> nRand = new ArrayList<>(n);
		for (int k = 1; k <= n; k++) {
			nRand.add(k);
		}
		Collections.shuffle(nRand);

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				int ind = (j + i * s + i / s) % n;
				grid[i][j] = nRand.get(ind);
			}
		}
	}

	/*
	 * =============================================================================
	 * Find a solution to the sudoku problem
	 * =============================================================================
	 */

	public void findSolution() {
		// vérifie que l'état courant est correct
		if (solutionChecker() && findSolutionBT(0, 0)) {
			System.out.println(toString() + '\n');
		} else {
			System.out.println("Pas de solution.");
		}
	}

	private boolean findSolutionBT(int i, int j) {
		// condition d'arrêt
		if (i == n) {
			return true;
		}

		// prochains indices
		int nextI, nextJ;
		if (j == n - 1) {
			nextI = i + 1;
			nextJ = 0;
		} else {
			nextI = i;
			nextJ = j + 1;
		}

		// si un numéro est déjà placé, on sait qu'il est correct
		if (grid[i][j] != 0) {
			return findSolutionBT(nextI, nextJ);
		}

		// test de chaque numéro
		for (int k = 1; k <= n; k++) {
			// appel récursif si le numéro est correct
			if (isPossible(k, i, j)) {
				grid[i][j] = k;

				if (findSolutionBT(nextI, nextJ)) {
					return true;
				}
			}
		}

		// remise à zéro
		grid[i][j] = 0;

		return false;
	}

	/*
	 * =============================================================================
	 */

	@Override
	public String toString() {
		StringBuilder res = new StringBuilder();
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				res.append(grid[i][j]);
				if (j < n - 1) {
					res.append(" ");
				}
			}
			if (i < n - 1) {
				res.append("\n");
			}
		}
		return res.toString();
	}

	public static void main(String args[]) {
		int n = 4;

		Sudoku s = new Sudoku(n);
		s.generateSolution();
		System.out.println("Solution générée aléatoirement :\n" + s);

		System.out.println("\nEnsemble des solutions :");
		s = new Sudoku(n);
		s.findSolution();
	}
}
