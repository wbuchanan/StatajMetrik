package org.paces.Stata;

import com.stata.sfi.Macro;

/**
 * @author Billy Buchanan
 * @version 0.0.0
 * <h2>Class used as access point to IRT related classes and methods</h2>
 * <p>This class contains no member variables and is used only to dispatch
 * methods related to fitting IRT models to data.</p>
 */
public class IRT {

	/***
	 * Method to fit the JMLE estimator to the data in memory in Stata
	 * @param args Passed from javacall function in Stata
	 * @return An integer success code if method executes
	 */
	public static int rasch(String[] args) {

		// Fit the model to the data
		RaschJMLE raschmodel = new RaschJMLE(args);

		// Get the print option from the Stata ado wrapper
		String printopt = Macro.getLocalSafe("print");

		// Switch used to define which statistics to print out for the end user
		switch (printopt) {

			// If all is passed to the print option of raschjmle
			case "all":

				// Call method that prints all available statistics to the
				// console
				raschmodel.printStats();

				// Break out of the switch statement after execution
				break;

			// If the value person was passed as an argument to the print
			// parameter
			case "person":

				// Call method that prints person parameter/stats to the console
				raschmodel.printPersons();

				// Break out of the switch statement after execution
				break;

			// If the value qaqc was passed as an argument to the print
			// parameter
			case "qaqc":

				// Call method that prints scale quality info to the console
				raschmodel.printScaleQuality();

				// Break out of the switch statement after execution
				break;

			// If the value lookup was passed as an argument to the print
			// parameter
			case "lookup":

				// Call method that prints raw to theta conversion table to
				// the console
				raschmodel.printScoreTable();

				// Break out of the switch statement after execution
				break;

			// For all other argument values
			default:

				// Call method that prints everything but the person
				// parameters/statistics to the console
				raschmodel.printStandard();

				// Break out of the switch statement after execution
				break;

		} // End of Switch statement

		// Standard value to return on success
		return 0;

	} // End of method declaration

} // End of Class definition



