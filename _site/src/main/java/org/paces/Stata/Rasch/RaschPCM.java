package org.paces.Stata.Rasch;

/**
 * Created by billy on 2/7/16.
 */

import com.itemanalysis.psychometrics.data.VariableName;
import com.itemanalysis.psychometrics.irt.estimation.*;
import com.itemanalysis.psychometrics.irt.model.*;
import com.itemanalysis.psychometrics.measurement.DefaultItemScoring;
import com.itemanalysis.psychometrics.scaling.DefaultLinearTransformation;
import com.stata.sfi.*;
import org.paces.Stata.DataSets.*;
import org.paces.Stata.MetaData.*;
import org.paces.Stata.Utilities.MDArray;

import java.util.*;

/***
 * @author Billy Buchanan
 * @version 0.0.0
 * <h2>Class used to fit a Rasch Model to data using the Joint Maximum
 * Likelihood Estimator</h2>
 * <p>Some equations that may prove useful/helpful when
 * interpreting/understanding some of the parameters are detailed below.
 * These equations are adapted from page 100 of Wright, B. D., &amp; Masters, G
 * . N.
 * (1982).  <em>Rating scale analysis: Rasch measurement</em>. Chicago, IL:
 * MESA</p>
 *
 * <div>
 *  <table style="width:75%;border-spacing:15px;padding:10px;
 *  border-collapse:separate;text-align=center;">
 *      <caption style="font-size:16px;font-weight:300;">Estimation of
 *      Item/Person Parameters and Fit Statistics Under the Rasch
 *      Model</caption>
 *      <tr style="padding:20px;border-top: medium solid black;
 *      border-bottom: medium solid black;">
 *          <th>Parameter/Fit Statistic Name</th>
 *          <th>Equation Defining the Parameter</th>
 *          <th>Expectation</th>
 *          <th>Variance</th>
 *      </tr>
 *      <tr style="padding:20px;border-top: medium solid black;
 *      border-bottom: medium solid black;">
 *          <td>Observed Response</td>
 *          <td>\[\chi_{ij}\]</td>
 *          <td>\[\]</td>
 *          <td>\[\]</td>
 *      </tr>
 *      <tr style="padding:20px;border-top: medium solid black;
 *      border-bottom: medium solid black;">
 *          <td>Expected mean of \(x_{ij}\)</td>
 *          <td>\[\omega_{ij} = \sum_{k = 0}^{m_{i}}k\pi_{ijk}\]</td>
 *          <td>\[\]</td>
 *          <td>\[\]</td>
 *      </tr>
 *      <tr style="padding:20px;border-top: medium solid black;
 *      border-bottom: medium solid black;">
 *          <td rowspan="3">Variance of \(x_{ij}\)</td>
 *          <td>\[\psi_{ij} = \sum_{k = 0}^{m_{i}} exp\sum_{l = 0}^{k}
 *          (\beta_{i} - \delta_{ij})\]</td>
 *          <td>\[\]</td>
 *          <td>\[\]</td>
 *      </tr>
 *      <tr style="padding:20px;border-top: medium solid black;
 *      border-bottom: medium solid black;">
 *          <td>\[\pi_{ijk} = \frac{exp\sum_{l = 0}^{k}(\beta_{i} -
 *          \delta_{ij})}{\psi_{ij}}\]</td>
 *          <td>\[\]</td>
 *          <td>\[\]</td>
 *      </tr>
 *      <tr style="padding:20px;border-top: medium solid black;
 *      border-bottom: medium solid black;">
 *          <td>\[\sigma_{ij} = \sum_{k = 0}^{m_{i}}(k - \omega_{ij})
 *          ^{2}\pi_{ijk}\]
 *          </td>
 *          <td>\[\]</td>
 *          <td>\[\]</td>
 *      </tr>
 *      <tr style="padding:20px;border-top: medium solid black;
 *      border-bottom: medium solid black;">
 *          <td>Kurtosis of \(x_{ij}\)</td>
 *          <td>\[\gamma_{ij} = \sum_{k = 0}^{m_{i}}(k - \omega_{ij})
 *          ^{4}\pi_{ijk}\]
 *          </td>
 *          <td>\[\]</td>
 *          <td>\[\]</td>
 *      </tr>
 *      <tr style="padding:20px;border-top: medium solid black;
 *      border-bottom: medium solid black;">
 *      	<td>Score Residual</td>
 *      	<td>\[\xi_{ij} = \chi_{ij} - \omega_{ij}\]</td>
 *          <td>\[0\]</td>
 *          <td>\[\sigma_{ij}\]</td>
 *      </tr>
 *      <tr style="padding:20px;border-top: medium solid black;
 *      border-bottom: medium solid black;">
 *      	<td>Standardized Residuals</td>
 *      	<td>\[\zeta_{ij} = \frac{\xi_{ij}}{\sqrt{\sigma_{ij}}}\]</td>
 *      	<td>\[0\]</td>
 *      	<td>\[1\]</td>
 *      </tr>
 *      <tr style="padding:20px;border-top: medium solid black;
 *      border-bottom: medium solid black;">
 *      	<td>Score Residual Squared</td>
 *      	<td>\[\xi_{ij}^{2} = \sigma_{ij}\zeta_{ij}^{2}\]</td>
 *      	<td>\[\sigma_{ij}\]</td>
 *      	<td>\[\lambda_{ij} - \sigma_{ij}^{2}\]</td>
 *      </tr>
 *      <tr style="padding:20px;border-top: medium solid black;
 *      border-bottom: medium solid black;">
 *      	<td>Standardized Residual Squared</td>
 *      	<td>\[\zeta_{ij}^{2}\]</td>
 *      	<td>\[1\]</td>
 *      	<td>\[(\frac{\lambda_{ij}}{\sigma_{ij}^{2}}) - 1\]</td>
 *      </tr>
 *      <tr style="padding:20px;border-top: medium solid black;
 *      border-bottom: medium solid black;">
 *      	<td>Unweighted Mean Square Error (Outfit)</td>
 *      	<td>\[\varepsilon_{i} = \frac{\sum_{j = 1}^{J}\zeta_{ij}^{2}}{J}\]
 *      	</td>
 *      	<td>\[1\]</td>
 *      	<td>\[\sum_{j}^{J}(\frac{\gamma_{ij}}{\sigma_{ij}^{2}})
 *      	 / \frac{J^{2} - 1}{J}\]</td>
 *      </tr>
 *      <tr style="padding:20px;border-top: medium solid black;
 *      border-bottom: medium solid black;">
 *      	<td rowspan="2">Weighted Mean Squared Error (Infit)</td>
 *      	<td>\[\eta_{i} = \frac{\sum_{j =
 *      	1}^{J}\sigma_{ij}\zeta_{ij}^{2}}{\sum_{j}^{J}\sigma_{ij}}\]
 *      	</td>
 *      	<td rowspan="2">\[1\]</td>
 *      	<td rowspan="2">\[\omicron_{i}^{2} = \frac{\sum_{j}^{J}
 *      	(\gamma_{ij} -
 *      	\sigma_{ij}^{2})}{(\sum_{j}^{J}\sigma_{ij})^{2}}\]</td>
 *      </tr>
 *      <tr style="padding:20px;border-top: medium solid black;
 *      border-bottom: medium solid black;">
 *      	<td>\[   = \frac{\sum_{j}^{J}\xi_{ij}^{2}}{\sum_{j}^{J}\sigma_{ij}}\]
 *      	</td>
 *      </tr>
 *      <tr style="padding:20px;border-top: medium solid black;
 *      border-bottom: medium solid black;">
 *      	<td>Standardized Weighted Mean Squared Error (Std. Infit)</td>
 *      	<td>\[\tau_{i} = (\eta_{i}^{1/3} - 1)(\frac{3}{\omicron_{i}}) +
 *      	(\frac{\omicron_{i}}{3})\]
 *      	</td>
 *      	<td>\[0\]</td>
 *      	<td>\[1\]</td>
 *      </tr>
 *  </table>
 *
 * </div>
 */
public class RaschPCM {

	/***
	 * Meta Class Object
	 */
	private Meta metaob;

	private Variables stvars;

	/***
	 * An object that constructs a 2d Array of Item Responses
	 */
	private DataSetByteArrays thedataobject;

	/***
	 * An m x n matrix of item responses where m = persons and n = items
	 */
	public Byte[][] thedata;

	/***
	 * Estimated residual error from individual item responses
	 */
	public Double[][] residuals;

	/***
	 * Parameter used to adjust extreme scores (e.g., perfect
	 * failures/successes)
	 * This value defaults to 0.3 if no user-specified value is passed to
	 * the Stata program that calls this Java program.
	 */
	public Double adjustment;

	/***
	 * Parameter used for DefaultLinearTransformation
	 * Documentation from com.itemanalysis.psychometrics not clear.  Will
	 * default to a value of 1.0 if no user specified value is passed.
	 */
	public Double intercept;

	/***
	 * Parameter used for DefaultLinearTransformation
	 * Documentation from com.itemanalysis.psychometrics not clear.  Will
	 * default to a value of 1.0 if no user specified value is passed.
	 */
	public Double scale;

	/***
	 * Parameter used when defining the precision to use with the
	 * printScoreTable method.
	 * Sets a default of 3 if nothing passed by the user
	 */
	public Integer precision;

	/***
	 * The number of Maximum Likelihood Iterations to use for the model
	 * overall.  This value is set by the Stata local macro 'giter' and if
	 * null will default to a value of 5,000.
	 */
	public Integer globaliterations;

	/***
	 * The Global Maximum Likelihood convergence criteria.  Set by the Stata
	 * local macro 'gconv'.  If the local macro is null this parameter is set
	 * to a default of 0.0001.
	 */
	public Double globalconverge;

	/***
	 * The number of Maximum Likelihood Iterations to use for the estimation
	 * of person parameter theta.  This value is set by the Stata local macro
	 * 'piter' and if null will default to a value of 2,500.
	 */
	public Integer personiterations;

	/***
	 * The maximum likelihood convergence criteria for the estimation of
	 * person parameter theta.  Value is set by the Stata local macro 'pconv'
	 * and if null is set to a default of 0.0001.
	 */
	public Double personconverge;

	/***
	 * Sets a boolean in the estimation algorithm to center the item
	 * parameters.  The value is set by the Stata local macro 'centitems' and
	 * if null will set the boolean to false, if active it sets the boolean
	 * to true.
	 */
	public Boolean centeritems;

	/***
	 * A double array containing the estimated values of the trait across
	 * persons.  This can be thought of as an m x 1 vector of estimates where
	 * theta is assumed to be N(0, 1).
	 */
	public Double[] theta;

	/***
	 * A double array of estimated difficulty parameter values.  Since the
	 * Rasch model constrains the discrimination parameter to 1, the only
	 * item parameter that is freely estimated is the difficulty of the items
	 * .  This corresponds to the location where the probability of a correct
	 * response to the item j is 0.5.
	 */
	public Double[] diff;

	/***
	 * An array containing the conditional standard error of
	 * measurement values for the person parameter theta estimates
	 * following fitting the Rasch model with the JMLE estimator.
	 */
	public Double[] personStandardErrors;

	/***
	 * An array of standard errors of the difficulty parameter estimates from
	 * the JMLE values obtained from fitting the Rasch model to the data.
	 */
	public Double[] diffSE;

	/***
	 * An array of double valued estimates of item-level weighted mean
	 * squares errors - or InFit - statistics.
	 */
	public Double[] itemInfit;

	/***
	 * An array of double valued estimates of item-level unweighted mean
	 * squares errors - or OutFit - statistics.
	 */
	public Double[] itemOutfit;

	/***
	 * An array of double valued estimates of item-level standardized weighted
	 * mean squares errors - or Standardized InFit - statistics.
	 */
	public Double[] itemStdInfit;

	/***
	 * An array of double valued estimates of item-level standardized
	 * unweighted mean squares errors - or Standardized OutFit - statistics.
	 */
	public Double[] itemStdOutfit;

	/***
	 * An array of double valued estimates of person-level weighted mean
	 * squares errors - or InFit - statistics.
	 */
	public List<Double> personInfit;

	/***
	 * An array of double valued estimates of person-level unweighted mean
	 * squares errors - or OutFit - statistics.
	 */
	public List<Double> personOutfit;

	/***
	 * An array of double valued estimates of person-level standardized weighted
	 * mean squares errors - or Standardized InFit - statistics.
	 */
	public List<Double> personStdInfit;

	/***
	 * An array of double valued estimates of person-level standardized
	 * unweighted mean squares errors - or Standardized OutFit - statistics.
	 */
	public List<Double> personStdOutfit;

	/***
	 * List object to store person fit statistics
	 */
	public List<RaschFitStatistics> pfstats;

	/***
	 * An Array of Item Response Models from the com.itemanalysis
	 * .psychometrics package
	 */
	public ItemResponseModel[] irm;

	/***
	 * Joint Maximum Likelihood Estimator as implemented in the com
	 * .itemanalysis.psychometrics package (http://www.github
	 * .com/meyerjp3/psychometrics).
	 */
	public JointMaximumLikelihoodEstimation jmle;

	/***
	 * Constructor method for class
	 * @param args Arguments passed from javacall
	 */
	public RaschPCM(String[] args) {
		setGlobalConvergence();
		setGlobalIterations();
		setPersonConvergence();
		setPersonIterations();
		setCenterItems();
		setAdjustment();
		setScale();
		setPrecision();
		setIntercept();
		setMeta(args);
		setTheDataObject();
		setTheData();
		setIRM();
		setJmle();
		fitModel();
		setPersonFitStatistics();
		returnPersonParameters();
		setItemFitStatistics();
		setResiduals();
		returnItemParameters();
		returnResiduals();

	} // End of constructor method

	/***
	 * Method to set adjustment factor for extreme scores
	 * If no user-specified value is passed, the method sets this
	 * parameter to a default value of 0.3.  For additional information on
	 * adjusting for extreme scores, see http://www.winsteps
	 * .com/facetman/xtremescore.htm.
	 */
	public void setAdjustment() {

		// Check for value passed to adjustment argument.  If none passed,
		// use default value
		if ("".equals(Macro.getLocalSafe("adjustment"))) this.adjustment = 0.3;

			// If a value was supplied by the user, use that value
		else this.adjustment = Double.valueOf(Macro.getLocalSafe("adjustment"));

	} // End of method declaration to set Adjustment factor

	/***
	 * A setter method to set the Global Iterations variable/parameter
	 * If no value is passed the default value of 5,000 is used.
	 */
	public void setGlobalIterations() {

		// Check to see if the local macro has a value
		if ("".equals(Macro.getLocalSafe("giter"))) {

			// If not set defaul to a value of 16000
			this.globaliterations = 5000;

		} else {

			// If a value is populated, set the value of globaliterations
			// variable to that value
			this.globaliterations = Integer.valueOf(Macro.getLocalSafe("giter"));

		} // End IF/ELSE Block to check for values in the local macro giter

	} // End declaration of setter method for the global iterations parameter
	// for the JMLE model

	/***
	 * A setter method to set the Global Convergence criteria
	 * variable/parameter
	 * If no value is passed, the default of 0.001 is used.
	 */
	public void setGlobalConvergence() {

		// Check to see if the local macro has a value
		if ("".equals(Macro.getLocalSafe("gconv"))) {

			// If user did not specify a value set this as the default
			this.globalconverge = 0.001;

		} else {

			// If user specified a value use that
			this.globalconverge = Double.valueOf(Macro.getLocalSafe("gconv"));

		} // End IF/ELSE Block to check for values in the local macro gconv

	} // End declaration of setter method for the global convergence parameter
	// for the JMLE model


	/***
	 * A setter method to set the Person Convergence criteria
	 * variable/parameter
	 * If the user does not supply a value, the default 0.001 is used.
	 */
	public void setPersonConvergence() {

		// Check whether or not the user passed a person convergence
		// criterion value
		if ("".equals(Macro.getLocalSafe("pconv"))) {

			// If not set to 0.0001
			this.personconverge = 0.001;

		} else {

			// If the user provided a convergence criterion value set the
			// parameter to that value
			this.personconverge = Double.valueOf(Macro.getLocalSafe("pconv"));

		} // End IF/ELSE Block to check for values in the local macro pconv

	} // End declaration of setter method for the person-level convergence
	// parameter for the JMLE model

	/***
	 * A setter method to set the Person Iterations variable/parameter
	 * If no value is passed, the default of 5,000 is used.
	 */
	public void setPersonIterations() {

		// Did user pass a person max iteration value
		if ("".equals(Macro.getLocalSafe("piter"))) {

			// If no, set this parameter to 500
			this.personiterations = 500;

		} else {

			// If the user did pass a value, set this parameter to that value
			this.personiterations = Integer.valueOf(Macro.getLocalSafe("piter"));

		} // End IF/ELSE Block to check for values in the local macro piter

	} // End declaration of setter method for the person-level iterations
	// parameter for the JMLE model

	/***
	 * A Setter method for the estimation parameter to center the estimated
	 * item parameters
	 * When true, items are centered at \(\mu_{\alpha}\) to aid in the
	 * identification of the model; this is the standard practice for Rasch
	 * modeling.  If false, person parameters are centered around
	 * \(\mu_{\theta}\).
	 */
	public void setCenterItems() {

		// Check if centitems option is activated.  If not, set the
		// centeritems parameter to true
		// If it is not set, set the center items parameter to false
		this.centeritems = "".equals(Macro.getLocalSafe("centitems"));

	} // End declaration of setter method for the global convergence parameter
	// for the JMLE model

	/***
	 * Setter method for the intercept member variable
	 * Used to scale parameter estiamtes/score estimates.  Scaling is the
	 * equivalent of :
	 *
	 * \(Y_{s2} = \alpha + \betaY_{s1}\)
	 *
	 * Where \(Y_{s2}\) is the transformed value, \(Y_{s1}\) is the raw
	 * value, \(\alpha\) is the value passed to the intercept parameter (or 0
	 * .0), and \(\beta\) is the value passed to the scale parameter (or 1.0).
	 */
	public void setIntercept() {

		// If user does not specify a value use 1.0
		if ("".equals(Macro.getLocalSafe("intercept"))) this.intercept = 0.0;

			// If user specifies a value use that value
		else this.intercept = Double.valueOf(Macro.getLocalSafe("intercept"));

	} // End of Method declaration to set the intercept value used for the
	// DefaultLinearTransformation object

	/***
	 * Setter method for the scale member variable
	 * Used to scale parameter estiamtes/score estimates.  Scaling is the
	 * equivalent of :
	 *
	 * \(Y_{s2} = \alpha + \betaY_{s1}\)
	 *
	 * Where \(Y_{s2}\) is the transformed value, \(Y_{s1}\) is the raw
	 * value, \(\alpha\) is the value passed to the intercept parameter (or 0
	 * .0), and \(\beta\) is the value passed to the scale parameter (or 1.0).
	 */
	public void setScale() {

		// If user does not specify a value use 1.0
		if ("".equals(Macro.getLocalSafe("scale"))) this.scale = 1.0;

			// If user specifies a value use that value
		else this.scale = Double.valueOf(Macro.getLocalSafe("scale"));

	} // End of Method declaration to set the scale value used for the
	// DefaultLinearTransformation object

	/***
	 * Setter method for the precision member variable
	 * Used to set the precision parameter value for the method to
	 * printScoreTable
	 */
	public void setPrecision() {

		// If user does not specify a value use 9
		if ("".equals(Macro.getLocalSafe("precision"))) this.precision = 9;

			// If user specifies a value use that value
		else this.precision = Integer.valueOf(Macro.getLocalSafe("precision"));

	} // End of Method declaration to set the precision value for printing
	// the score table to the Stata console

	/***
	 * Getter method for the Global Iteration variable
	 * @return An integer with the maximum number of maximum likelihood
	 * iterations to use to achieve convergence.
	 */
	public int getGlobalIterations() {
		return this.globaliterations;
	}

	/***
	 * Getter method for the Global Convergence variable
	 * @return An real valued number with the convergence criterion for the
	 * maximum likelihood estimator overall.
	 */
	public double getGlobalConvergence() {
		return this.globalconverge;
	}

	/***
	 * Getter method for the Person Iteration variable
	 * @return An integer with the maximum number of maximum likelihood
	 * iterations to use to achieve convergence for the estimation of person
	 * parameters.
	 */
	public int getPersonIterations() {
		return this.personiterations;
	}


	/***
	 * Getter method for the Person Convergence variable
	 * @return An real valued number with the convergence criterion for the
	 * maximum likelihood estimator of person parameter theta.
	 */
	public double getPersonConvergence() {
		return this.personconverge;
	}

	/***
	 * Getter method used to set whether or not item parameters should be
	 * centered during the fitting of the model.
	 * @return A boolen value indicating if item parameters should be
	 * centered (true) or not (false).
	 */
	public boolean getCenteredItems() {
		return this.centeritems;
	}

	/***
	 * Setter method to create Meta object for class
	 * @param args Arguments passed from javacall/class constructor
	 */
	public void setMeta(String[] args) {

		this.metaob = new Meta(args);
		this.stvars = this.metaob.getStatavars();
	}

	/***
	 * Setter method for creating a data object containing the item responses
	 */
	public void setTheDataObject() {
		this.thedataobject = new DataSetByteArrays(getMetaOb());
	}

	/***
	 * Setter method for the matrix containing the item responses
	 */
	public void setTheData() {
		this.thedata = this.thedataobject.getData();
	}

	/***
	 * Getter method for the Meta class variable.
	 * @return A Meta class object used to construct matrix and meta data.
	 */
	public Meta getMetaOb() {
		return this.metaob;
	}

	/***
	 * Getter method for the m x n matrix of item responses.
	 * @return A 2d Array of Item responses stored as Byte type variables.
	 */
	public Byte[][] getTheData() {
		return this.thedataobject.getData();
	}

	/***
	 * Setter method for the Item Response Model array used by the
	 * JointMaximumLikelihoodEstimator class object from the com.itemanalysis
	 * .psychometrics package.
	 */
	public void setIRM(){

		// Creates a temporary array of item response model objects
		this.irm = new ItemResponseModel[this.metaob.varindex.size()];

		// Iterate over the items
		for (int i = 0; i < this.metaob.varindex.size(); i++) {

			// Specify a new Rasch model for the item
			this.irm[i] = new Irm3PL(0.0, 1.0);

			// Set the variable name based on the variables passed to javacall
			this.irm[i].setName(new VariableName(this.metaob.statavars.getVariableName(i)));

			// Set the item scoring parameter to the default
			this.irm[i].setItemScoring(new DefaultItemScoring(false));

		} // End Loop over the items

	} // End method declaration to construct the array of ItemResponseModel
	// objects

	/***
	 * Getter method for the array of Item Response Models.
	 * @return An Array of Item Response Model Objects.
	 */
	public ItemResponseModel[] getIRM() {
		return this.irm;
	}

	/***
	 * Setter method for the JointMaximumLikelihoodEstimator class variable
	 */
	public void setJmle(){
		this.jmle = new JointMaximumLikelihoodEstimation(MDArray.toPrimative(getTheData()), getIRM());
	}

	/***
	 * Method used to retrieve the value of the precision member variable
	 * @return Integer valued number for the reporting precision
	 */
	public int getPrecision() {
		return this.precision;
	}

	/***
	 * Method used to retrieve the adjustment member variable's value
	 * @return A real number containing the adjustment for extreme scores
	 * used in the estimation process
	 */
	public double getAdjustment() {
		return this.adjustment;
	}

	/***
	 * Method used to retrieve the intercept value for the linear
	 * scaling/transformation functions
	 * @return A real valued number containing the value of the intercept
	 * member variable
	 */
	public double getIntercept() {
		return this.intercept;
	}

	/***
	 * Method used to retrieve the scale - or slope - value for the linear
	 * scaling/transformation functions
	 * @return A real valued number containing the value of the scale
	 * member variable
	 */
	public double getScale() {
		return this.scale;
	}

	/***
	 * A method to fit the model to the data; This method also sets some of
	 * the variables of the class.
	 * This method fits the Rasch model to the data in memory.  The PROX
	 * procedure is used for initial item estimates.  This is equivalent to:
	 *
	 *
	 * \(d_i^o = x_i - x_.\)
	 *
	 * where \(x_i = ln( (1 - P_i) / P_i ) \) and \(x_. = \sum_{i}x_i/J\)
	 * .  Here, \(P_i = \sum_{i = 1}^{N}item_j/((m)(N))\) where m denotes
	 * the number of response categories, N denotes the number of subjects,
	 * the subscript j is used to index an individual item, and J is the
	 * number of items.
	 *
	 * Additionally, the item variance can be estimated as:
	 *
	 * \(\sigma^2 = (\sum_{i}x_i^2 - Jx_.^2)/(J - 1)\)
	 */
	public void fitModel() {

		// Creates a new DefaultLinearTransformation object using the
		// intercept and scale values passed by the user
		DefaultLinearTransformation linearTransformation = new
				DefaultLinearTransformation(getIntercept(), getScale());

		// Summarizes data with user-specified adjustment parameter
		this.jmle.summarizeData(getAdjustment());

		// Uses PROX procedure for initial item parameter estimates
		this.jmle.itemProx();

		// Estimates the model parameters given user specified convergence
		// criteria, maximum number of iterations, and boolean to identify
		// how to center the intermediate parameter values
		this.jmle.estimateParameters(getGlobalIterations(),
				getGlobalConvergence()
				, getPersonIterations(), getPersonConvergence(),
				getCenteredItems());

		// Apply the bias correction procedure
		this.jmle.biasCorrection();

		// this.jmle.linearTransformation(getIntercept(), getScale());

		// Print the MLE iteration history to the Stata Console
		SFIToolkit.display(this.jmle.printIterationHistory());

		// Estimate the item fit statstics
		this.jmle.computeItemFitStatistics();

		// Estimate the item fit statistics if polytomous items
		this.jmle.computeItemCategoryFitStatistics();

		// Prints conversion table from raw score to theta along with the
		// conditional standard errors of measurement.
		//SFIToolkit.displayln(this.jmle.printScoreTable(getGlobalIterations(),
		//		getGlobalConvergence(), getAdjustment(), linearTransformation,
		//		getPrecision()));

		// Estimate standard errors around the item parameters
		this.jmle.computeItemStandardErrors();

		// Estimate the conditional standard error of measurement
		this.jmle.computePersonStandardErrors();

		// Temporary variable to store estimates of theta for ith subject
		Double[] tmptheta = new Double[thedata.length];

		// Temporary variable to store the conditional standard error of
		// measurement for the ith subject
		Double[] tmpcsem = new Double[thedata.length];

		// Loop over subjects
		for (int i = 0; i < thedata.length; i++) {

			// Initialize a new RaschFitStatistics object
			RaschFitStatistics x = this.jmle.getPersonFitStatisticsAt(i);

			// Temp variable to match Stata indices
			int obid = i + 1;

			// Label for person-level outfit scalar
			String oflab = "poutfit_" + obid;

			// Label for person-level infit scalar
			String iflab = "pinfit_" + obid;

			// Label for person-level standardized outfit scalar
			String soflab = "psoutfit_" + obid;

			// Label for person-level standardized infit scalar
			String siflab = "psinfit_" + obid;

			// Label for theta scalar for person i
			String pthlab = "ptheta_" + obid;

			// Label for CSEM scalar for person i
			String psemlab = "pcsem_" + obid;

			// Return the person-level outfit scalar to Stata
			Scalar.setValue(oflab, x.getUnweightedMeanSquare());

			// Return the person-level infit scalar to Stata
			Scalar.setValue(iflab, x.getWeightedMeanSquare());

			// Return the person-level standardized outfit scalar to Stata
			Scalar.setValue(soflab, x.getStandardizedUnweightedMeanSquare());

			// Return the person-level standardized infit scalar to Stata
			Scalar.setValue(siflab, x.getStandardizedWeightedMeanSquare());

			// Return the person-level theta scalar to Stata
			Scalar.setValue(pthlab, jmle.getPersonEstimateAt(i));

			// Return the person-level csem scalar to Stata
			Scalar.setValue(psemlab, jmle.getPersonEstimateStdErrorAt(i));

			// Return the person-level outfit scalar to Stata
			tmptheta[i] = jmle.getPersonEstimateAt(i);

			// Return the person-level outfit scalar to Stata
			tmpcsem[i] = jmle.getPersonEstimateStdErrorAt(i);

		} // End loop over observations

		// Sets the theta variable of the object to the values in the temp
		// variable
		this.theta = tmptheta;

		// Sets the CSEM values for the personStandardErrors member variable
		this.personStandardErrors = tmpcsem;

	} // End of method declaration to fit the model to the data

	/***
	 * Method used to Print results to Stata Console
	 * This method prints general results from the estimation algorithm
	 * to the Stata console.  Frequency tables are suppressed to save space
	 * on the screen since these results are easily obtained by iterating
	 * over the items and calling the tabulate function in Stata.
	 */
	public void printStats() {

		// Prints item information
		printItems();

		// Prints information about person parameters
		printPersons();

		// Details of item and person parameter scales
		printScaleQuality();

		// Prints raw to theta conversion table
		printScoreTable();

	} // End of method declaration to print the results to the Stata console

	/***
	 * Default print method excludes the person parameter output to conserve
	 * a bit of screen real estate.
	 */
	public void printStandard() {

		// Prints item information
		printItems();

		// Details of item and person parameter scales
		printScaleQuality();

		// Prints raw to theta conversion table
		printScoreTable();

	}

	/***
	 * Prints data related to item statistics to the console
	 */
	public void printItems() {

		// Prints table with items, difficulties, diff SE, and item fit
		// statistics to Stata console
		SFIToolkit.displayln(String.valueOf(this.jmle.printBasicItemStats()));

		//
		SFIToolkit.displayln(String.valueOf(this.jmle.printCategoryStats()));

		// Unsure what this prints at the moment.
		SFIToolkit.displayln(String.valueOf(this.jmle.printRatingScaleTables()));

	} // End of method declaration

	/***
	 * Prints the raw to theta look up table to the Stata console
	 */
	public void printScoreTable() {

		// Prints raw to theta conversion score table
		SFIToolkit.display(this.jmle.printScoreTable(getGlobalIterations(),
				getGlobalConvergence(), getAdjustment(),
				new DefaultLinearTransformation(getIntercept(), getScale()),
				getPrecision()));

	} // End of Method declaration

	/***
	 * Prints person-level parameter statistics
	 */
	public void printPersons() {

		SFIToolkit.displayln(String.valueOf(this.jmle.printPersonStats()));

	} // End of Method declaration

	/***
	 * Prints statistics about the quality of the item and person scales (e.g
	 * ., variances, reliability estimates, etc...).
	 */
	public void printScaleQuality() {

		// Initialize new RaschScaleQualityOutput object containing both item
		// and person staistics
		RaschScaleQualityOutput scaleOutput = new RaschScaleQualityOutput(
				this.jmle.getItemSideScaleQuality(),
				this.jmle.getPersonSideScaleQuality());

		// Prints meta statistics (e.g., observed variance, SD, MSE, Root
		// MSE, adjusted Variance/SD, separation index, number of strata, and
		// reliability estimates for items and persons.
		SFIToolkit.displayln(String.valueOf(scaleOutput.printScaleQuality()));

	} // End of method declaration

	/***
	 * Method used to set all Person Fit Statistics
	 * This method is used to set person outfit, infit, standardized
	 * outfit, and standardized infit values after JMLE estimation of a
	 * Rasch IRT model.  To access these data from Stata, see the return
	 * methods below.
	 */
	public void setPersonFitStatistics() {

		// Create temporary object to store person outfit statistics
		List<Double> pout = new ArrayList<>();

		// Create temporary object to store person infit statistics
		List<Double> pin  = new ArrayList<>();

		// Create temporary object to store person standardized outfit
		// statistics
		List<Double> psout = new ArrayList<>();

		// Create temporary object to store person standardized infit statistics
		List<Double> psin = new ArrayList<>();

		// Loop over person indices
		for(int i = 0; i < thedata.length; i++) {

			RaschFitStatistics x = this.jmle.getPersonFitStatisticsAt(i);

			// Populate the pout temp variable with the unweighted mean
			// squared error (or Outfit) statistic
			pout.add(x.getUnweightedMeanSquare());

			// Populate the pin temp variable with the weighted mean
			// squared error (or Infit) statistic
			pin.add(x.getWeightedMeanSquare());

			// Populate the psout temp variable with the standardized
			// unweighted mean squared error (or Outfit) statistic
			psout.add(x.getStandardizedUnweightedMeanSquare());

			// Populate the psin temp variable with the standardized
			// weighted mean squared error (or Infit) statistic
			psin.add(x.getStandardizedWeightedMeanSquare());

		} // End of Loop over persons

		// Set person outfit variable to the value of the pout temp variable
		this.personOutfit = pout;

		// Set person infit variable to the value of the pin temp variable
		this.personInfit = pin;

		// Set person standardized outfit variable to the value of the psout
		// temp variable
		this.personStdOutfit = psout;

		// Set person standardized infit variable to the value of the psin
		// temp variable
		this.personStdInfit = psin;

	} // End of method used to populate people fit statistics variables

	/***
	 * Method to retrieve the estimates of \(\theta\) for the subjects in the
	 * data set
	 * @return An array of double objects containing the estimated \(\theta\)
	 * for each subject.
	 */
	public Double[] getTheta() {
		return this.theta;
	}

	/***
	 * Method to retrieve the estimates of \(\theta\) for the ith subject in the
	 * data set
	 * @param idx Index value identifying the ith subject
	 * @return \(\theta\) for the ith subject
	 */
	public Double getTheta(int idx) {
		return this.theta[idx];
	}

	/***
	 * Method to retrieve the estimates of \(\sigma^2\) for the subjects in the
	 * data set
	 * @return An array of double objects containing the estimated \(\sigma^2\)
	 * for each subject.
	 */
	public Double[] getCSEM() {
		return this.personStandardErrors;
	}

	/***
	 * Method to retrieve the estimates of \(\sigma^2\) for the ith subject in
	 * the data set
	 * @param idx Index value identifying the ith subject
	 * @return \(\sigma^2\) for the ith subject
	 */
	public Double getCSEM(int idx) {
		return this.personStandardErrors[idx];
	}

	/***
	 * Method to get the List of double objects with the person infit statistics
	 * @return A list of double type objects containing estimated person infit
	 */
	public List<Double> getPersonInfit() {
		return this.personInfit;
	}

	/***
	 * Method to get the double objects with the estimated infit statistic
	 * for the ith subject
	 * @param idx The index value to identify the ith subject
	 * @return A double type object containing estimated person-level infit
	 * statistic for the ith subject
	 */
	public Double getPersonInfit(int idx) {
		return this.personInfit.get(idx);
	}

	/***
	 * Method to get the List of double objects with the person outfit
	 * statistics
	 * @return A list of double type objects containing estimated person outfit
	 */
	public List<Double> getPersonOutfit() {
		return this.personOutfit;
	}

	/***
	 * Method to get the double objects with the estimated outfit statistic
	 * for the ith subject
	 * @param idx The index value to identify the ith subject
	 * @return A double type object containing estimated person-level outfit
	 * statistic for the ith subject
	 */
	public Double getPersonOutfit(int idx) {
		return this.personOutfit.get(idx);
	}

	/***
	 * Method to get the List of double objects with the standardized
	 * person-level infit statistics
	 * @return A list of double type objects containing estimated
	 * standardized person-level infit statistics
	 */
	public List<Double> getPersonStdInfit() {
		return this.personStdInfit;
	}

	/***
	 * Method to get the double objects with the estimated standardized
	 * infit statistic for the ith subject
	 * @param idx The index value to identify the ith subject
	 * @return A double type object containing estimated standardized
	 * person-level infit statistic for the ith subject
	 */
	public Double getPersonStdInfit(int idx) {
		return this.personStdInfit.get(idx);
	}

	/***
	 * Method to get the List of double objects with the standardized
	 * person-level infit statistics
	 * @return A list of double type objects containing estimated
	 * standardized person-level infit statistics
	 */
	public List<Double> getPersonStdOutfit() {
		return this.personStdOutfit;
	}

	/***
	 * Method to get the double objects with the estimated standardized
	 * outfit statistic for the ith subject
	 * @param idx Index of the ith individual
	 * @return A double type object containing estimated standardized
	 * person-level outfit statistic for the ith subject
	 */
	public Double getPersonStdOutfit(int idx) {
		return this.personStdOutfit.get(idx);
	}

	/***
	 * Method to return Person fit statistics to Stata
	 * This method adds six new variables to the dataset: theta, csem,
	 * outfit, infit, stdoutfit, and stdinfit.  These store person-level fit
	 * statistics estimated while fitting the model to the data.  Item-level
	 * fit statistics are returned using a different method (see below for
	 * additional information).
	 */
	public void returnPersonParameters() {

		// Add a variable to the data set to store the person outfit statistics
		Data.addVarDouble("outfit");

		// Add a variable to the data set to store the person infit statistics
		Data.addVarDouble("infit");

		// Add a variable to the data set to store the person std. outfit
		// statistics
		Data.addVarDouble("stdoutfit");

		// Add a variable to the data set to store the person std. infit
		// statistics
		Data.addVarDouble("stdinfit");

		// Add a variable to the data set to store the person theta statistics
		Data.addVarDouble("theta");

		// Add a variable to the data set to store the person CSEM statistics
		Data.addVarDouble("csem");

		// Get variable index for the outfit variable
		int outfit = Data.getVarIndex("outfit");

		// Get variable index for the infit variable
		int infit = Data.getVarIndex("infit");

		// Get variable index for the stdoutfit variable
		int stdoutfit = Data.getVarIndex("stdoutfit");

		// Get variable index for the stdinfit variable
		int stdinfit = Data.getVarIndex("stdinfit");

		// Get variable index for the theta variable
		int theta = Data.getVarIndex("theta");

		// Get variable index for the csem variable
		int csem = Data.getVarIndex("csem");

		// Add variable label to the outfit variable in Stata
		Data.setVarLabel(outfit, "Person-level Outfit (Unweighted MSE)");

		// Add variable label to the infit variable in Stata
		Data.setVarLabel(infit, "Person-level Infit (Information Weighted MSE)");

		// Add variable label to the stdoutfit variable in Stata
		Data.setVarLabel(stdoutfit, "Person-level Standardized Outfit");

		// Add variable label to the stdinfit variable in Stata
		Data.setVarLabel(stdinfit, "Person-level Standardized Infit");

		// Add variable label to the theta variable in Stata
		Data.setVarLabel(theta, "Estimated Ability/Skill of Person (Theta)");

		// Add variable label to the csem variable in Stata
		Data.setVarLabel(csem, "Conditional Standard Error of Measurement");

		// Loop over the variables passed to the Java application
		for(int i = 0; i < this.thedata.length; i++) {

			// Get the Stata variable name for this variable index
			Integer obid = i + 1;

			// Set scalar value for the item-level outfit
			Data.storeNum(outfit, obid, getPersonOutfit(i));

			// Set scalar value for the item-level infit
			Data.storeNum(infit, obid, getPersonInfit(i));

			// Set scalar value for the item-level standardized outfit
			Data.storeNum(stdoutfit, obid, getPersonStdOutfit(i));

			// Set scalar value for the item-level standardized infit
			Data.storeNum(stdinfit, obid, getPersonStdInfit(i));

			// Set scalar value for the item difficulty estimates
			Data.storeNum(theta, obid, getTheta(i));

			// Set scalar value for the standard error of the difficulty
			// estimates
			Data.storeNum(csem, obid, getCSEM(i));

		} // End Loop over variables

	} // End of Method declaration that returns person fit statistics to Stata

	/***
	 * Method used to set all Item Fit Statistics
	 * This method is used to set item level outfit, infit, standardized
	 * outfit, and standardized infit values after JMLE estimation of a
	 * Rasch IRT model.  To access these data from Stata, see the return
	 * methods below.
	 *
	 * The unweighted mean square error (outfit) is defined as:
	 *
	 * \(u_i = \sum_{n = 1}^{N}z_{ni}^2 / N\)
	 *
	 * Where \(z_{ni} = \epsilon_{ni} / \sqrt{W_{ni}} \) which is the
	 * standardized residual on the ith item for the nth person, and \(
	 * W_{ni} = \sum_{k = 0}^{m}(k - E_{ni})^2 \pi_{nik}\) or the variance
	 * of the response.
	 */
	public void setItemFitStatistics() {

		// Initialize a new array of RaschFitStatistics objects
		RaschFitStatistics[] fitstats = new RaschFitStatistics[this.metaob.varindex.size()];

		// Create temporary object to store item outfit statistics
		Double[] iout = new Double[this.metaob.varindex.size()];

		// Create temporary object to store item infit statistics
		Double[] iin = new Double[this.metaob.varindex.size()];

		// Create temporary object to store item standardized outfit
		// statistics
		Double[] isout = new Double[this.metaob.varindex.size()];

		// Create temporary object to store item standardized infit statistics
		Double[] isin = new Double[this.metaob.varindex.size()];

		// Create temp object to store difficulty parameters
		Double[] idif = new Double[this.metaob.varindex.size()];

		// Create temp object to store SE around difficulty estimates
		Double[] idifse = new Double[this.metaob.varindex.size()];

		// Loop over person indices
		for(int i = 0; i < this.metaob.varindex.size(); i++) {

			// Populate the array elements of the RaschFitStatistics object
			// using the getPersonFitStatisticsAt(person index) method from
			// the JointMaximumLikelihoodEstimator class object of the com
			// .itemanalysis.psychometrics package.
			fitstats[i] = this.jmle.getItemFitStatisticsAt(i);

			// Populate the pout temp variable with the unweighted mean
			// squared error (or Outfit) statistic
			iout[i] = fitstats[i].getUnweightedMeanSquare();

			// Populate the pin temp variable with the weighted mean
			// squared error (or Infit) statistic
			iin[i] = fitstats[i].getWeightedMeanSquare();

			// Populate the psout temp variable with the standardized
			// unweighted mean squared error (or Outfit) statistic
			isout[i] = fitstats[i].getStandardizedUnweightedMeanSquare();

			// Populate the psin temp variable with the standardized
			// weighted mean squared error (or Infit) statistic
			isin[i] = fitstats[i].getStandardizedWeightedMeanSquare();

			// Populate the difficulty parameter temp variable
			idif[i] = irm[i].getDifficulty();

			// Populate the standard errors around the difficulty parameter
			// estimates
			idifse[i] = irm[i].getDifficultyStdError();

		} // End of Loop over persons

		// Set item-level outfit variable to the value of the iout temp variable
		this.itemOutfit = iout;

		// Set item-level infit variable to the value of the iin temp variable
		this.itemInfit = iin;

		// Set item-level standardized outfit variable to the value of the isout
		// temp variable
		this.itemStdOutfit = isout;

		// Set item-level standardized infit variable to the value of the isin
		// temp variable
		this.itemStdInfit = isin;

		// Set the difficulty parameter estimates
		this.diff = idif;

		// Set the standard errors around the difficulty estimates
		this.diffSE = idifse;

	} // End of method used to populate people fit statistics variables

	/***
	 * Method to set values of residual object
	 * <p>Populates the values of the residuals member variable with double
	 * precision differences between the expected probability of a correct
	 * response and the observed response.  These residuals are subsequently
	 * used in the estimation of both person and item fit statistics.
	 *
	 * Residuals here are defined as: </p>
	 *
	 * \(\varepsilon_{ij} = Y_{ij} - \sum_{k = 0}^{m}k\pi_{ijk}\)
	 *
	 * <p>Where \(\pi_{ijk}\) represents the modeled probability of the ith
	 * subject selecting the kth category on the jth item and \(Y_{ij}\) is
	 * the observed response on the jth item from the ith subject.</p>
	 */
	public void setResiduals() {

		// Initialize a new 2d array of double valued objects with dimensions
		// m x n, where m is the number of observations (rows) and n is the
		// number of items (variables/columns) in the test data.
		Double[][] res = new Double[this.metaob.obsindex.size()][this.metaob
				.varindex.size()];

		// Loop over observations
		for(int i = 0; i < this.metaob.obsindex.size(); i++) {

			// Loop over items
			for (int j = 0; j < this.metaob.varindex.size(); j++) {

				// Populate the jth cell for the ith observation with the
				// residual for that person/item
				res[i][j] = this.jmle.getResidualAt(i, j);

			} // End Loop over items

		} // End Loop over persons

		// Set the value of the residuals object to the temporary variable
		// initialized by this method.
		this.residuals = res;

	} // End of Method declaration to set the residuals member variable

	/***
	 * Method to access the estimated item difficulty parameters
	 * @return An array of real valued numbers containing the coefficients
	 * for the difficulty parameters
	 */
	public Double[] getDiff() {
		return this.diff;
	}

	/***
	 * Method to access the estimated item difficulty parameter for the jth item
	 * @param idx An index value to identify the jth item
	 * @return A real number with the estimated coefficient on the difficulty
	 * parameter for the jth item.
	 */
	public Double getDiff(int idx) {
		return this.diff[idx];
	}

	/***
	 * Method to access the estimated standard error of the difficulty
	 * parameter estimates
	 * @return An array of real valued numbers containing the standard errors
	 * on the difficulty parameter estimates
	 */
	public Double[] getDiffSE() {
		return this.diffSE;
	}

	/***
	 * Method to access the estimated item difficulty standard error for the
	 * jth item
	 * @param idx An index value to identify the jth item
	 * @return A real number with the estimated standard error on the difficulty
	 * parameter estimate for the jth item.
	 */
	public Double getDiffSE(int idx) {
		return this.diffSE[idx];
	}

	/***
	 * Method to access the estimated item-level infit statistics
	 *
	 * The item infit (or the Information Weighted Mean Square error) is
	 * defined as :
	 *
	 * \(v_i = \sum_{j}^{J}W_{ij}z_{ij}^{2} / \sum_{j}^{J}W_{ij} \)
	 *
	 * Where \(W_{ij}\) is the variance of the ith person and jth item:
	 *
	 * \(W_{ij} = \sum_{k = 0}^{m_{i}}(k - \varepsilon_{ij})^{2}\pi_{ijk}\)
	 *
	 * and \(z_{ij}\) is the residual on the jth item for the ith subject.
	 *
	 * @return An array of real valued numbers containing estimated
	 * item-level infit statistics for all items
	 */
	public Double[] getItemInfit() {
		return this.itemInfit;
	}

	/***
	 * Method to access the estimated item infit statistic for the jth item
	 * @param idx An index value to identify the jth item
	 * @return A real number with the estimated infit statistic for the jth
	 * item.
	 */
	public Double getItemInfit(int idx) {
		return this.itemInfit[idx];
	}

	/***
	 * Method to access the estimated item-level outfit statistics
	 *
	 * The item outfit (or the Unweighted Mean Square error) is defined as:
	 *
	 *
	 * \(u_{i} = \sum_{n = 1}^{N}z_{ij}^{2} / N \)
	 *
	 * Where \(z_{ij}^{2}\) is the squared residual for the ith subject
	 * responding to the jth item and N is the number of subjects.  In other
	 * words, this is the average sum of squared person x item residuals.
	 * @return An array of real valued numbers containing estimated
	 * item-level outfit statistics for all items
	 */
	public Double[] getItemOutfit() {
		return this.itemOutfit;
	}

	/***
	 * Method to access the estimated item outfit statistic for the jth item
	 * @param idx An index value to identify the jth item
	 * @return A real number with the estimated outfit statistic for the jth
	 * item.
	 */
	public Double getItemOutfit(int idx) {
		return this.itemOutfit[idx];
	}

	/***
	 * Method to access the estimated standardized item infit statistics
	 * @return An array of real valued numbers containing estimated
	 * standardized item-level infit statistics for all items
	 */
	public Double[] getItemStdInfit() {
		return this.itemStdInfit;
	}

	/***
	 * Method to access the estimated standardized item infit statistic for the
	 * jth item
	 * @param idx An index value to identify the jth item
	 * @return A real number with the estimated standardized infit statistic
	 * for the jth item.
	 */
	public Double getItemStdInfit(int idx) {
		return this.itemStdInfit[idx];
	}

	/***
	 * Method to access the estimated standardized item outfit statistics
	 * @return An array of real valued numbers containing estimated
	 * standardized item-level outfit statistics for all items
	 */
	public Double[] getItemStdOutfit() {
		return this.itemStdOutfit;
	}

	/***
	 * Method to access the estimated standardized item outfit statistic for
	 * the jth item
	 * @param idx An index value to identify the jth item
	 * @return A real number with the estimated standardized outfit statistic
	 * for the jth item.
	 */
	public Double getItemStdOutfit(int idx) {
		return this.itemStdOutfit[idx];
	}

	/***
	 * Method used to access the entire m x n matrix of residuals
	 * @return An m x n matrix of residuals for M observations over N items
	 */
	public Double[][] getResiduals() {
		return this.residuals;
	}

	/***
	 * Method used to obtain a the residuals across items 1..j for the ith
	 * person
	 * @param obid Index for the ith person
	 * @return A real valued vector of item-level residuals for the ith person
	 */
	public Double[] getResiduals(int obid) {
		return this.residuals[obid];
	}

	/***
	 * Method used to obtain a single residual value for the ith person on
	 * the jth item.
	 * @param obid Index for the ith person
	 * @param varid Index for the jth item
	 * @return A real valued difference between the observed and expected
	 * response of the ith subject to the jth item.
	 */
	public double getResiduals(int obid, int varid) {
		return this.residuals[obid][varid];
	}


	/***
	 * Method returns residuals as a set of scalars which will need to be
	 * further processed by Stata in order to construct the matrix of residuals.
	 */
	public void returnResiduals() {

		// Loop over the observations
		for(int i = 0; i < this.metaob.obsindex.size(); i++) {

			// Loop over the variables
			for (int j = 0; j < this.metaob.varindex.size(); j++) {

				// Adjust the row index to match Stata row indices
				int row = i + 1;

				// Adjust the column index to match Stata column indices
				int col = j + 1;

				// Create a name for the scalar
				String resid = "res_" + row + "_" + col;

				// Set the scalar value
				Scalar.setValue(resid,  getResiduals(i, j));

			} // End Loop over variables

		} // End Loop over observations

	} // End Method declaration to return residuals to Stata

	/***
	 * Method that returns item fit statistics to Stata
	 * This method sets the item fit statistics as Stata scalars which
	 * will be further processed by the .ado file to provide better return
	 * objects.
	 */
	public void returnItemParameters() {

		// Loop over the variables passed to the Java application
		for(int i = 0; i < this.metaob.varindex.size(); i++) {

			// Get the Stata variable name for this variable index
			String varname = this.metaob.statavars.getVariableName(i);

			// Set scalar value for the item-level outfit
			Scalar.setValue(varname + "_outfit", getItemOutfit(i));

			// Set scalar value for the item-level infit
			Scalar.setValue(varname + "_infit", getItemInfit(i));

			// Set scalar value for the item-level standardized outfit
			Scalar.setValue(varname + "_stdoutfit", getItemStdOutfit(i));

			// Set scalar value for the item-level standardized infit
			Scalar.setValue(varname + "_stdinfit", getItemStdInfit(i));

			// Set scalar value for the item difficulty estimates
			Scalar.setValue(varname + "_diff", getDiff(i));

			// Set scalar value for the standard error of the difficulty
			// estimates
			Scalar.setValue(varname + "_diffse", getDiffSE(i));

		} // End Loop over variables

	} // End method declaration to return item parameters to Stata

} // End of RaschJMLE class object declaration
