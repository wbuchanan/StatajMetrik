// Drop the program from memory if currently loaded
cap prog drop raschjmle

// Define the program raschjmle
prog def raschjmle, rclass

	// Define version under which Stata should interpret the code
	version 13
	
	// Define syntax used for the program
	syntax varlist(min = 2 num) [if] [in] [,								 ///   
	noCENTitems GIter(integer 5000) PIter(integer 500) GConv(real 0.001)	 ///   
	PConv(real 0.001) ADJustment(real 0.3) Intercept(real 0.0) 				 ///   
	Scale(real 1.0) PRecision(integer 3) PRInt(string asis) ]

	// Get the number of observations
	marksample touse
	
	// Count if satisfying the condition
	qui: count if `touse'
	
	// Number of observations used by the program
	loc progobs = `r(N)'

	if !inlist(`"`print'"', "all", "person", "qaqc", "lookup") loc print `""""'

	if `"`centitems'"' != "" loc centitems "false"
	else loc centitems "true"
	
	// Call Java plugin to fit the model using the JMLE estimator
	javacall org.paces.Stata.IRT rasch `varlist' `if'`in',				    ///
	args(`giter' `piter' `gconv' `pconv' `adjustment' `intercept' `scale'   ///
	`precision' `centitems' `print')
	
	// Check calling version
	if `c(version)' < 14 {
	
		// Column names for item statistics return matrix
		loc istats Diff Diff_SE Outfit Infit Std_Outfit Std_Infit
		
		// Column names for person parameters return matrix
		loc pparams Theta CSEM Outfit Infit Std_Outfit Std_Infit
	
	}  // End IF Block for versions of Stata prior to 14
	
	// For version 14 and later
	else {
		
		// Column names for item statistics return matrix
		loc istats "Diff" "Diff S.E." "Outfit" "Infit" "Std Outfit" "Std Infit"
		
		// Column names for person parameters return matrix
		loc pparams "Theta" "CSEM" "Outfit" "Infit" "Std Outfit" "Std Infit"
	
	} // End ELSE Block for versions 14 and later
	
	// Assemble the item statistics from the scalars returned from Java
	mata: itemstats()
	
	// Assemble the person x item residuals from the scalars returned from Java
	mata: residuals(`progobs')
	
	// Assemble the person parameters from the scalars returned from Java
	mata: personparams(`progobs')
	
	// Label the columns of the item parameter matrix
	mat colnames itemstats = `istats'
	
	// Label the rows of the item parameters matrix
	mat rownames itemstats = `varlist'
	
	// Label the columns of the residual matrix
	mat colnames residuals = `varlist'
	
	// Label person parameters matrix columns
	mat colnames personparams = `pparams'
	
	// Return matrix of item statistics
	ret mat itemstats = itemstats
	
	// Return matrix of person x item residuals
	ret mat residuals = residuals
	
	// Return person parameters in matrix
	ret mat personparams = personparams
	
// End of program definition	
end




