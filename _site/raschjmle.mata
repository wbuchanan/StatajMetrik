// Start Mata
mata:

// Clear existing objects from Mata
mata clear

// Function to build matrix of item parameter estimates and fit statistics
void itemstats() {

	// Real number matrix to store the values
	real matrix temp
	
	// Rowvector used to store the variable names
	string rowvector varnames 
	
	// Used to initialize the variable used in the loop below
	numeric i
	
	// Set the varnames variable based on the varlist used in the raschjmle program
	varnames = tokens(st_local("varlist"))
	
	// Create a temporary matrix with rows = # of variables and 6 columns
	temp = J(cols(varnames), 6, .)
	
	// Loop over the variables
	for (i = 1; i <= cols(varnames); i++) {
		
		// Set the difficulty parameter estimates for the jth item
		temp[i, 1] = st_numscalar(varnames[1, i] + "_diff")
		
		// Set the difficulty standard errors for the jth item
		temp[i, 2] = st_numscalar(varnames[1, i] + "_diffse")
		
		// Set the outfit statistics estimates for the jth item
		temp[i, 3] = st_numscalar(varnames[1, i] + "_outfit")
		
		// Set the infit statistics estimates for the jth item
		temp[i, 4] = st_numscalar(varnames[1, i] + "_infit")
		
		// Set the standardized outfit statistics estimates for the jth item
		temp[i, 5] = st_numscalar(varnames[1, i] + "_stdoutfit")
		
		// Set the standardized infit statistics estimates for the jth item
		temp[i, 6] = st_numscalar(varnames[1, i] + "_stdinfit")
	
	} // End of Loop over items
	
	// Return the matrix as the Stata matrix named itemstats
	st_matrix("itemstats", temp)

} // End of function declaration

// Function to retrieve the matrix of person/item residuals
void residuals(real scalar obs) {

	// Real number matrix to store the values
	real matrix temp
	
	// Rowvector used to store the variable names
	string rowvector varnames 
		
	// String matrices used to convert numeric indices to strings
	string matrix ti, tj
	
	// String scalars used to extract the string matrix values above
	string scalar obid, varid
	
	// Used to initialize the variable used in the loop below
	real i, j
	
	// Set the varnames variable based on the varlist used in the raschjmle program
	varnames = tokens(st_local("varlist"))
	
	// Create a temporary matrix with rows = # of variables and 6 columns
	temp = J(obs, cols(varnames), .)
	
	// Loop over observations
	for (i = 1; i <= obs; i++) {
	
		// Loop over the variables
		for (j = 1; j <= cols(varnames); j++) {
		
			// Converts the index i to a string marix
			ti = strofreal(i)
			
			// Converts the index j to a string matrix
			tj = strofreal(j)
			
			// Converts the value in ti to a string scalar obid
			obid = ti[1, 1]
			
			// Converts the value in tj to a string scalar varid
			varid = tj[1, 1]
		
			// Stores the scalars returned from the java function in the matrix
			temp[i, j] = st_numscalar("res_" + obid + "_" + varid)

		} // End of Loop over variables
		
	} // End of Loop over observations	
	
	// Return the matrix as a Stata matrix named residuals
	st_matrix("residuals", temp)
		
} // End of Function declaration

// Function to retrieve the matrix of person parameter estimates
void personparams(real scalar obs) {

	// Real number matrix to store the values
	real matrix temp
	
	// String matrices used to convert numeric indices to strings
	string matrix ti
	
	// String scalars used to extract the string matrix values above
	string scalar obid
	
	// Used to initialize the variable used in the loop below
	real i
		
	// Create a temporary matrix with rows = # of variables and 6 columns
	temp = J(obs, 6, .)
	
	// Loop over observations
	for (i = 1; i <= obs; i++) {
			
		// Converts the index i to a string marix
		ti = strofreal(i)
		
		// Converts the value in ti to a string scalar obid
		obid = ti[1, 1]
		
		// Stores the scalars returned from the java function in the matrix
		temp[i, 1] = st_numscalar("ptheta_" + obid)

		// Stores the conditional standard error of measurement
		temp[i, 2] = st_numscalar("pcsem_" + obid)
		
		// Stores the Person-level outfit
		temp[i, 3] = st_numscalar("poutfit_" + obid)

		// Stores the person-level infit
		temp[i, 4] = st_numscalar("pinfit_" + obid)
		
		// Stores the Person-level standardized outfit
		temp[i, 5] = st_numscalar("psoutfit_" + obid)
		
		// Stores the Person-level standardized infit
		temp[i, 6] = st_numscalar("psinfit_" + obid)
	
	} // End of Loop over observations	
	
	// Return the matrix as a Stata matrix named residuals
	st_matrix("personparams", temp)
		
} // End of Function declaration

// Save the functions
// mata mosave itemstats(), dir(`"`c(sysdir_personal)'"') replace
// mata mosave residuals(), dir(`"`c(sysdir_personal)'"') replace
// mata mosave personparams(), dir(`"`c(sysdir_personal)'"') replace

// Creates a mata library
mata mlib create libraschjmle, dir(`"`c(sysdir_personal)'"') replace
// mata mlib create libraschjmle, dir(`"`c(pwd)'"') replace

// Adds mata functions to the library
mata mlib add libraschjmle itemstats() residuals() personparams(), dir(`"`c(sysdir_personal)'"')
// mata mlib add libraschjmle itemstats() residuals() personparams(), dir(`"`c(pwd)'"')

// End of declaration of Mata functions
end

