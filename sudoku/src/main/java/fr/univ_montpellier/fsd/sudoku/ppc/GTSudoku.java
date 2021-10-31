
package fr.univ_montpellier.fsd.sudoku.ppc;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;
import static org.chocosolver.solver.search.strategy.Search.minDomLBSearch;
import static org.chocosolver.util.tools.ArrayUtils.append;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class GTSudoku {

	static int n;
	static int s;
	public static int instance;
	private static long timeout = 3600000; // one hour
	private static String filepath;

	IntVar[][] rows, cols, shapes;

	Model model;

	public static void main(String[] args) throws ParseException {

		final Options options = configParameters();
		final CommandLineParser parser = new DefaultParser();
		final CommandLine line = parser.parse(options, args);

		boolean helpMode = line.hasOption("help"); // args.length == 0
		if (helpMode) {
			final HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("sudoku", options, true);
			System.exit(0);
		}
		instance = 9;
		filepath = "data/GTSudoku.txt";
		// Check arguments and options
		for (Option opt : line.getOptions()) {
			checkOption(line, opt.getLongOpt());
		}

		new GTSudoku().solve();
	}

	public GTSudoku() {		
		n = instance;
		s = (int) Math.sqrt(n);
	}

	public void solve() {

		buildModel();
		model.getSolver().showStatistics();
		model.getSolver().solve();

		StringBuilder st = new StringBuilder(String.format("Sudoku -- %s\n", instance, " X ", instance));
		st.append("\t");
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				st.append(rows[i][j]).append("\t\t\t");
			}
			st.append("\n\t");
		}

		System.out.println(st.toString());
	}

	public void buildModel() {
		model = new Model();

		rows = new IntVar[n][n];
		cols = new IntVar[n][n];
		shapes = new IntVar[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				rows[i][j] = model.intVar("c_" + i + "_" + j, 1, n, false);
				cols[j][i] = rows[i][j];
			}
		}

		for (int i = 0; i < s; i++) {
			for (int j = 0; j < s; j++) {
				for (int k = 0; k < s; k++) {
					for (int l = 0; l < s; l++) {
						shapes[j + k * s][i + (l * s)] = rows[l + k * s][i + j * s];
					}
				}
			}
		}

		for (int i = 0; i < n; i++) {
			System.out.println(i);
			model.allDifferent(rows[i], "AC").post();
			model.allDifferent(cols[i], "AC").post();
			model.allDifferent(shapes[i], "AC").post();
		}

		// --------------------------------------
		readGTConstraintsFromFile();
		// --------------------------------------
	}
	
	private void readGTConstraintsFromFile() {
		File data = new File(filepath);
		BufferedReader reader;
		
		try {
			reader = new BufferedReader(new FileReader(data));
			String ops = reader.readLine(); // n
			ops = reader.readLine();
			
			int ind = 0;
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					if (j % s != s - 1) {
						model.arithm(rows[i][j], "" + ops.charAt(ind++), rows[i][j + 1]).post();
					}
				}
			}
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					if (j % s != s - 1) {
						model.arithm(cols[i][j], "" + ops.charAt(ind++), cols[i][j + 1]).post();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Deprecated
	private void buildGTConstraints() {
		// contraintes générées à partir de l'instance suivante :
		// <><<><<<<<<<><>>><>><><<<>>>>><<<><>><>><<>>>>>>><><>><>><<><><><><><<<>><<><>><><>><<><<<><<>><>><<><>>><><
		model.arithm(rows[0][0], "<", rows[0][1]).post();
		model.arithm(rows[0][1], ">", rows[0][2]).post();
		model.arithm(rows[0][3], "<", rows[0][4]).post();
		model.arithm(rows[0][4], "<", rows[0][5]).post();
		model.arithm(rows[0][6], ">", rows[0][7]).post();
		model.arithm(rows[0][7], "<", rows[0][8]).post();
		model.arithm(rows[1][0], "<", rows[1][1]).post();
		model.arithm(rows[1][1], "<", rows[1][2]).post();
		model.arithm(rows[1][3], "<", rows[1][4]).post();
		model.arithm(rows[1][4], "<", rows[1][5]).post();
		model.arithm(rows[1][6], "<", rows[1][7]).post();
		model.arithm(rows[1][7], "<", rows[1][8]).post();
		model.arithm(rows[2][0], ">", rows[2][1]).post();
		model.arithm(rows[2][1], "<", rows[2][2]).post();
		model.arithm(rows[2][3], ">", rows[2][4]).post();
		model.arithm(rows[2][4], ">", rows[2][5]).post();
		model.arithm(rows[2][6], ">", rows[2][7]).post();
		model.arithm(rows[2][7], "<", rows[2][8]).post();
		model.arithm(rows[3][0], ">", rows[3][1]).post();
		model.arithm(rows[3][1], ">", rows[3][2]).post();
		model.arithm(rows[3][3], "<", rows[3][4]).post();
		model.arithm(rows[3][4], ">", rows[3][5]).post();
		model.arithm(rows[3][6], "<", rows[3][7]).post();
		model.arithm(rows[3][7], "<", rows[3][8]).post();
		model.arithm(rows[4][0], "<", rows[4][1]).post();
		model.arithm(rows[4][1], ">", rows[4][2]).post();
		model.arithm(rows[4][3], ">", rows[4][4]).post();
		model.arithm(rows[4][4], ">", rows[4][5]).post();
		model.arithm(rows[4][6], ">", rows[4][7]).post();
		model.arithm(rows[4][7], ">", rows[4][8]).post();
		model.arithm(rows[5][0], "<", rows[5][1]).post();
		model.arithm(rows[5][1], "<", rows[5][2]).post();
		model.arithm(rows[5][3], "<", rows[5][4]).post();
		model.arithm(rows[5][4], ">", rows[5][5]).post();
		model.arithm(rows[5][6], "<", rows[5][7]).post();
		model.arithm(rows[5][7], ">", rows[5][8]).post();
		model.arithm(rows[6][0], ">", rows[6][1]).post();
		model.arithm(rows[6][1], "<", rows[6][2]).post();
		model.arithm(rows[6][3], ">", rows[6][4]).post();
		model.arithm(rows[6][4], ">", rows[6][5]).post();
		model.arithm(rows[6][6], "<", rows[6][7]).post();
		model.arithm(rows[6][7], "<", rows[6][8]).post();
		model.arithm(rows[7][0], ">", rows[7][1]).post();
		model.arithm(rows[7][1], ">", rows[7][2]).post();
		model.arithm(rows[7][3], ">", rows[7][4]).post();
		model.arithm(rows[7][4], ">", rows[7][5]).post();
		model.arithm(rows[7][6], ">", rows[7][7]).post();
		model.arithm(rows[7][7], ">", rows[7][8]).post();
		model.arithm(rows[8][0], ">", rows[8][1]).post();
		model.arithm(rows[8][1], "<", rows[8][2]).post();
		model.arithm(rows[8][3], ">", rows[8][4]).post();
		model.arithm(rows[8][4], "<", rows[8][5]).post();
		model.arithm(rows[8][6], ">", rows[8][7]).post();
		model.arithm(rows[8][7], ">", rows[8][8]).post();

		model.arithm(cols[0][0], "<", cols[0][1]).post();
		model.arithm(cols[0][1], ">", cols[0][2]).post();
		model.arithm(cols[0][3], ">", cols[0][4]).post();
		model.arithm(cols[0][4], "<", cols[0][5]).post();
		model.arithm(cols[0][6], "<", cols[0][7]).post();
		model.arithm(cols[0][7], ">", cols[0][8]).post();
		model.arithm(cols[1][0], "<", cols[1][1]).post();
		model.arithm(cols[1][1], ">", cols[1][2]).post();
		model.arithm(cols[1][3], "<", cols[1][4]).post();
		model.arithm(cols[1][4], ">", cols[1][5]).post();
		model.arithm(cols[1][6], "<", cols[1][7]).post();
		model.arithm(cols[1][7], ">", cols[1][8]).post();
		model.arithm(cols[2][0], "<", cols[2][1]).post();
		model.arithm(cols[2][1], ">", cols[2][2]).post();
		model.arithm(cols[2][3], "<", cols[2][4]).post();
		model.arithm(cols[2][4], "<", cols[2][5]).post();
		model.arithm(cols[2][6], "<", cols[2][7]).post();
		model.arithm(cols[2][7], ">", cols[2][8]).post();
		model.arithm(cols[3][0], ">", cols[3][1]).post();
		model.arithm(cols[3][1], "<", cols[3][2]).post();
		model.arithm(cols[3][3], "<", cols[3][4]).post();
		model.arithm(cols[3][4], ">", cols[3][5]).post();
		model.arithm(cols[3][6], "<", cols[3][7]).post();
		model.arithm(cols[3][7], ">", cols[3][8]).post();
		model.arithm(cols[4][0], ">", cols[4][1]).post();
		model.arithm(cols[4][1], "<", cols[4][2]).post();
		model.arithm(cols[4][3], ">", cols[4][4]).post();
		model.arithm(cols[4][4], "<", cols[4][5]).post();
		model.arithm(cols[4][6], ">", cols[4][7]).post();
		model.arithm(cols[4][7], ">", cols[4][8]).post();
		model.arithm(cols[5][0], "<", cols[5][1]).post();
		model.arithm(cols[5][1], "<", cols[5][2]).post();
		model.arithm(cols[5][3], ">", cols[5][4]).post();
		model.arithm(cols[5][4], "<", cols[5][5]).post();
		model.arithm(cols[5][6], "<", cols[5][7]).post();
		model.arithm(cols[5][7], "<", cols[5][8]).post();
		model.arithm(cols[6][0], ">", cols[6][1]).post();
		model.arithm(cols[6][1], "<", cols[6][2]).post();
		model.arithm(cols[6][3], "<", cols[6][4]).post();
		model.arithm(cols[6][4], ">", cols[6][5]).post();
		model.arithm(cols[6][6], ">", cols[6][7]).post();
		model.arithm(cols[6][7], "<", cols[6][8]).post();
		model.arithm(cols[7][0], ">", cols[7][1]).post();
		model.arithm(cols[7][1], ">", cols[7][2]).post();
		model.arithm(cols[7][3], "<", cols[7][4]).post();
		model.arithm(cols[7][4], "<", cols[7][5]).post();
		model.arithm(cols[7][6], ">", cols[7][7]).post();
		model.arithm(cols[7][7], "<", cols[7][8]).post();
		model.arithm(cols[8][0], ">", cols[8][1]).post();
		model.arithm(cols[8][1], ">", cols[8][2]).post();
		model.arithm(cols[8][3], ">", cols[8][4]).post();
		model.arithm(cols[8][4], "<", cols[8][5]).post();
		model.arithm(cols[8][6], ">", cols[8][7]).post();
		model.arithm(cols[8][7], "<", cols[8][8]).post();
	}

	// Check all parameters values
	public static void checkOption(CommandLine line, String option) {

		switch (option) {
		case "inst":
			instance = Integer.parseInt(line.getOptionValue(option));
			break;
		case "timeout":
			timeout = Long.parseLong(line.getOptionValue(option));
			break;
		default: {
			System.err.println("Bad parameter: " + option);
			System.exit(2);
		}

		}

	}

	// Add options here
	private static Options configParameters() {

		final Option helpFileOption = Option.builder("h").longOpt("help").desc("Display help message").build();

		final Option instOption = Option.builder("i").longOpt("instance").hasArg(true).argName("sudoku instance")
				.desc("sudoku instance size").required(false).build();

		final Option limitOption = Option.builder("t").longOpt("timeout").hasArg(true).argName("timeout in ms")
				.desc("Set the timeout limit to the specified time").required(false).build();

		// Create the options list
		final Options options = new Options();
		options.addOption(instOption);
		options.addOption(limitOption);
		options.addOption(helpFileOption);

		return options;
	}

	public void configureSearch() {
		model.getSolver().setSearch(minDomLBSearch(append(rows)));

	}

}
