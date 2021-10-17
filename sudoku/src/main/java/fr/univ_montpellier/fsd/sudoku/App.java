package fr.univ_montpellier.fsd.sudoku;

import java.time.Duration;
import java.time.Instant;

import fr.univ_montpellier.fsd.sudoku.ppc.*;

//import fr.univ_montpellier.fsd.sudoku.imp.Sudoku;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	final int iter = 100;
    	long[] tempsMoyen = new long[7];
    	
    	Instant debut, fin;
    	Duration duree;
    	fr.univ_montpellier.fsd.sudoku.imp.Sudoku impSudoku;
    	Sudoku ppcSudoku;
    	
    	for (int k = 0; k < iter; k++) {
	    	for (int i = 0; i <= 2; i++) {
	    		int n = (i + 2) * (i + 2);
	
		    	impSudoku = new fr.univ_montpellier.fsd.sudoku.imp.Sudoku(n);
		    	debut = Instant.now();
		    	impSudoku.findSolution();
		    	fin = Instant.now();
		    	duree = Duration.between(debut, fin);
		    	tempsMoyen[i * 2] += duree.toNanos();
		
		    	Sudoku.instance = n;
		    	ppcSudoku = new Sudoku();
		    	debut = Instant.now();
		    	ppcSudoku.solve();
		    	fin = Instant.now();
		    	duree = Duration.between(debut, fin);
		    	tempsMoyen[i * 2 + 1] += duree.toNanos();
	    	}
	
	    	GTSudoku.instance = 9;
	    	GTSudoku gtSudoku = new GTSudoku();
	    	debut = Instant.now();
	    	gtSudoku.solve();
	    	fin = Instant.now();
	    	duree = Duration.between(debut, fin);
	    	tempsMoyen[6] += duree.toNanos();
    	}
    	
    	for (int i = 0; i < 7; i++) {
	    	tempsMoyen[i] /= iter;
    	}
    	
    	for (int i = 0; i <= 2; i++) {
    		int n = (i + 2) * (i + 2);
	    	System.out.println("imp.Sudoku - " + n + " : " + tempsMoyen[i*2] + "ns\n"
	    			+ "====================================================");
	    	System.out.println("ppc.Sudoku - " + n + " : " + tempsMoyen[i*2+1] + "ns\n"
	    			+ "====================================================");
    	}
    	
    	System.out.println("ppc.GTSudoku : " + tempsMoyen[6] + "ns");
    }
    
    /* une instance de ce petit benchmark :

imp.Sudoku - 4 : 40670ns
====================================================
ppc.Sudoku - 4 : 2709310ns
====================================================
imp.Sudoku - 9 : 876930ns
====================================================
ppc.Sudoku - 9 : 4116490ns
====================================================
imp.Sudoku - 16 : 1508430ns
====================================================
ppc.Sudoku - 16 : 42125240ns
====================================================
ppc.GTSudoku : 2079700ns

	 * on peut en conclure qu'implémenter le problème soi-même peut être plus
	 * performant que d'utiliser une API, cependant on perd le confort d'avoir
	 * des contraintes faciles à mettre en place
     */
    
}
