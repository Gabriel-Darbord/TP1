
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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class HardSudoku {

	static int n;
	static int s;
	private static long timeout = 3600000; // one hour
	private static String filepath;
	private static boolean solveAll;

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
		n = 9;
		// Check arguments and options
		for (Option opt : line.getOptions()) {
			checkOption(line, opt.getLongOpt());
		}

		s = (int) Math.sqrt(n);
		
		new HardSudoku().solve();
	}

	// récupération d'une instance depuis un fichier
	public int[][] readFromFile() {
		int[][] res = null;
		File data = new File(filepath);
		BufferedReader reader;

		try {
			reader = new BufferedReader(new FileReader(data));
			String line = reader.readLine();
	
			n = Integer.parseInt(line);
			res = new int[n][n];
	
			for (int i = 0; i < n; i++) {
				line = reader.readLine();
				String[] nums = line.split(" ");
	
				for (int j = 0; j < n; j++) {
					res[i][j] = Integer.parseInt(nums[j]);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return res;
	}

	public void solve() {

		buildModel();
		model.getSolver().showStatistics();

		do {
			if (!model.getSolver().solve())
				break;

			StringBuilder st = new StringBuilder(String.format("Sudoku -- %s\n", n, " X ", n));
			st.append("\t");
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					st.append(rows[i][j]).append("\t\t\t");
				}
				st.append("\n\t");

			}
			System.out.println(st.toString());
		} while(solveAll);
	}
	
	// instance de la question 7
	@Deprecated
	protected int[][] getInstance() {
		return new int[][] {
				{8,0,0, 0,0,0, 0,0,0},
				{0,0,3, 6,0,0, 0,0,0},
				{0,7,0, 0,9,0, 2,0,0},
				
				{0,5,0, 0,0,7, 0,0,0},
				{0,0,0, 0,4,5, 7,0,0},
				{0,0,0, 1,0,0, 0,3,0},
				
				{0,0,1, 0,0,0, 0,6,8},
				{0,0,8, 5,0,0, 0,1,0},
				{0,9,0, 0,0,0, 4,0,0},
		};
	}

	public void buildModel() {
		model = new Model();

		int[][] instance = null;
		if (filepath != null) {
			instance = readFromFile();
		}

		rows = new IntVar[n][n];
		cols = new IntVar[n][n];
		shapes = new IntVar[n][n];

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (instance != null && instance[i][j] > 0) {
					rows[i][j] = model.intVar("c_" + i + "_" + j, instance[i][j]);
				} else {
					rows[i][j] = model.intVar("c_" + i + "_" + j, 1, n, false);
				}
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
		// TODO: add constraints here

		// --------------------------------------

	}

	// Check all parameters values
	public static void checkOption(CommandLine line, String option) {

		switch (option) {
		case "instance":
			n = Integer.parseInt(line.getOptionValue(option));
			break;
		case "timeout":
			timeout = Long.parseLong(line.getOptionValue(option));
			break;
		case "file":
			filepath = line.getOptionValue(option);
			break;
		case "all":
			solveAll = Boolean.parseBoolean(line.getOptionValue(option));
			break;
		default:
			System.err.println("Bad parameter: " + option);
			System.exit(2);
		}

	}

	// Add options here
	private static Options configParameters() {

		final Option helpFileOption = Option.builder("h").longOpt("help").desc("Display help message").build();

		final Option instOption = Option.builder("i").longOpt("instance").hasArg(true).argName("sudoku instance")
				.desc("sudoku instance size").required(false).build();

		final Option limitOption = Option.builder("t").longOpt("timeout").hasArg(true).argName("timeout in ms")
				.desc("Set the timeout limit to the specified time").required(false).build();
		
		final Option fileOption = Option.builder("f").longOpt("file").hasArg(true).argName("instance file")
				.desc("File containing an instance of sudoku").required(false).build();
		
		final Option allOption = Option.builder("a").longOpt("all").hasArg(true).argName("all solutions")
				.desc("Search all solutions of this sudoku instance").required(false).build();

		// Create the options list
		final Options options = new Options();
		options.addOption(instOption);
		options.addOption(limitOption);
		options.addOption(helpFileOption);
		options.addOption(fileOption);
		options.addOption(allOption);

		return options;
	}

	public void configureSearch() {
		model.getSolver().setSearch(minDomLBSearch(append(rows)));

	}

}
