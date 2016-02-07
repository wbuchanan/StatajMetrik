{smcl}
{* *! version 0.0.0 09OCT2015}{...}
{cmd:help raschjmle}
{hline}

{marker raschtitle}{p 2 2 2}{title:RASCHJMLE}{p_end}

{p 4 4 4}{hi:raschjmle {hline 2}} A Joint Maximum Likelihood Estimator for Rasch IRT models of dichotomous items. {p_end}

{marker raschsyn}{p 2 2 2}{title:Syntax}{p_end}

{p 4 4 4}{cmd:raschjmle} {it:varlist} [{ifin}] [, {cmdab:gi:ter(}{it:integer}{opt )} 
{cmdab:gc:onv(}{it:real}{opt )} {cmdab:pi:ter(}{it:integer}{opt )} 
{cmdab:pc:onv(}{it:real}{opt )} {cmdab:adj:ustment(}{it:real}{opt )} {cmdab:i:ntercept(}{it:real}{opt )} 
{cmdab:s:cale(}{it:real}{opt )} {cmdab:pri:nt(}{it:string}{opt )}]

{marker raschdesc}{p 2 2 2}{title:Description}{p_end}

{p 4 4 4}{cmd:raschjmle} is an interface to the psychometrics Java library 
developed by {browse "http://curry.virginia.edu/about/directory/joseph-p.-meyer-iii":J. Patrick Meyer}.  
This JMLE estimator does not implement the Newton-Raphson algorithm proposed by 
Birnbaum (1968) (and described in detail in Baker and Kim (2004)), but instead 
implements a new algorithm developed by Meyer and Hailey (2012) they 
call proportional curve fitting.  The underlying source code is available on 
{browse "http://www.github.com/meyerjp3/psychometrics":GitHub}.  This program will 
add variables to your data set with the names: theta, csem, infit, outfit, stdinfit, 
and stdoutfit.  These are the person parameter and fit statistic values 
corresponding to the persons used to fit the model to the data.{p_end}

{marker raschopts}{p 2 2 2}{title:Options}{p_end}{break}

{p 4 4 4}{cmdab:gi:ter} is an optional parameter used to specify the global maximum 
number of iterations to try when fitting the model.  This defaults to 5000 if no 
argument is passed to the parameter.{p_end}

{p 4 4 4}{cmdab:gc:onv} is an optional parameter used to specify the global MLE 
convergence criterion.  The default value is 0.001.{p_end}

{p 4 4 4}{cmdab:pi:ter} is an optional parameter used to specify the maximum 
number of iterations to use when estimating the person parameter theta.  This 
defaults to 500 if no argument is passed to the parameter.{p_end}

{p 4 4 4}{cmdab:pc:onv} is an optional parameter used to specify the MLE 
convergence criterion for the estimation of the person parameter theta.  This 
also defaults to a value of 0.001.{p_end}

{p 4 4 4}{cmdab:adj:ustment} is an optional parameter used to specify an extreme 
score (e.g., perfect failure/success on items) adjustment.  The default value is 
0.3.{p_end}

{p 4 4 4}{cmdab:i:ntercept} is an optional parameter used to perform a linear 
transformation on the estimated parameters (e.g., theta and beta) and fit 
statistics.  The value is transformed using a simple linear equation Xn = a + b*Xo 
where Xo is the old score, a is the intercept, and b is the scale parameter.  
This defaults to a value of 0.0.{p_end}

{p 4 4 4}{cmdab:s:cale} is the other optional parameter related to linear scaling 
of the estimated parameter values.  The scale parameter defaults to a value of 
1.0; effectively, the default values prevent any transformation.{p_end}

{p 4 4 4}{cmdab:pri:nt} is an optional parameter used to specify what information 
will be printed to the Stata console (e.g., Results pane).  Passing a value of 
all will print the MLE iteration history log; the estimated beta parameter values, 
their standard errors, item level infit, outfit, standardized infit, and 
standardized infit; theta and the standard error of the estimates of theta; 
item and person parameter scale qualit statistics (e.g., variance, MSE, separation 
index, number of strata, reliability estimates, etc...); and the raw score to 
theta conversion with the conditional standard error of measurement.  Other 
possible values include: person (prints person information and iteration log), 
qaqc (scale quality and iteration log), or lookup (conversion table and iteration 
log).  Any other arguments passed to this parameter will use the default, which 
prints everything but person information to the console. {p_end}


{marker raschempls}{p 2 2 2}{title:Examples}{p_end}{break}

{p 4 4 4}Load example data set for IRT models in Stata.{p_end}
{p 4 4 4}{stata webuse masc1, clear}{p_end}

{p 4 4 4}Fit the model with all the default parameter values.{p_end}
{p 4 4 4}{stata raschjmle q1-q9}{p_end}

{p 4 4 4}This illustrates what can happen if the convergence criteria is set too low{p_end}
{p 4 4 4}{stata raschjmle q1-q9, gc(0.000000000000001) gi(75000) pc(0.000000000001) pi(10000)}{p_end}

{p 4 4 4}Removing a single decimal place from the global criterion then gets everything back to normal{p_end}
{p 4 4 4}{stata raschjmle q1-q9, gc(0.00000000000001) gi(75000) pc(0.000000000001) pi(10000)}{p_end}

{marker raschrefs}{p 2 2 2}{title:Returned values}{p_end}{break}

{p 8 8 8}r(itemstats) = a matrix of item parameter and fit statistic estimates{p_end}
{p 8 8 8}r(residuals) = the m x n matrix of person x item residuals{p_end}
{p 8 8 8}r(personparams) = the person parameters (these are also added to the data set in memory){p_end}

{marker raschrefs}{p 2 2 2}{title:References}{p_end}{break}

{p 4 8 8}Baker, F. B., & Kim, S. (2004).  {it:Item response theory: parameter estimation techniques}. 
2nd Ed.  New York City, NY: Marcel Dekker, Inc.{p_end}

{p 4 8 8}Birnbaum, A. (1968).  Some latent trait models and their use in inferring an examinee's ability.  
In F. M. Lord & M. R. Novick (Eds.), {it:Statistical theories of mental test scores} 
(pp. 111-132).  Chapel Hill, NC: The University of North Carolina Press.{p_end}

{p 4 8 8}Meyer, J. P., & Hailey, E. (2012). A study of Rasch partial credit, and rating scale model parameter 
recovery in WINSTEPS and jMetrik. {it:Journal of Applied Measurement, 13(3)}, 248-258.{p_end}

{marker contact}{p 4 4 4}{title:{ul:Author}}{p_end}{break}
{p 4 4 4}William R. Buchanan, Ph.D. {p_end}
{p 4 4 4}Data Scientist {p_end}
{p 4 4 4}{browse "http://mpls.k12.mn.us":Minneapolis Public Schools}{p_end}
{p 4 4 4}William.Buchanan at mpls [dot] k12 [dot] mn [dot] us{p_end}{break}

