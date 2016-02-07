---
layout: page
title: raschjmle
permalink: /raschjmle/
---

# Rasch Model for Binary Items using the Joint Maximum Likelihood Estimator 

raschjmle _varlist if in_ [, 
    <u>gi</u>ter(integer) 
    <u>pi</u>ter(integer) 
    <u>gc</u>onv(real) 
    <u>pc</u>onv(real) 
    <u>adj</u>ustment(real) 
    <u>i</u>ntercept(real) 
    <u>s</u>cale(real) 
    <u>pri</u>nt(string) ]


## Options

__giter__ is an optional parameter used to specify the global maximum number of iterations to try when fitting the model.  This defaults to 5000 if no argument is passed to the parameter.

__piter__ is an optional parameter used to specify the global MLE convergence criterion.  The default value is 0.001.

__gconv__ is an optional parameter used to specify the maximum number of iterations to use when estimating the person parameter theta.  This defaults to 500 if no argument is passed to the parameter.

__pconv__ is an optional parameter used to specify the MLE convergence criterion for the estimation of the person parameter theta.  This also defaults to a value of 0.001.
       
__adjustment__ is an optional parameter used to specify an extreme score (e.g., perfect failure/success on items) adjustment.  The default value is 0.3.
            
__intercept__ is an optional parameter used to perform a linear transformation on the estimated parameters (e.g., theta and beta) and fit statistics.  The value is transformed using a simple linear equation Xn = a + b*Xo where Xo is the old score, a is the intercept, and b is the scale parameter.  This defaults to a value of 0.0.

__scale__ is the other optional parameter related to linear scaling of the estimated parameter values.  The scale parameter defaults to a value of 1.0; effectively, the default values prevent any transformation.

__print__ is an optional parameter used to specify what information will be printed to the Stata console (e.g., Results pane).  Passing a value of all will print the MLE iteration history log; the estimated beta parameter values, their standard errors, item level infit, outfit, standardized infit, and standardized infit; theta and the standard error of the estimates of theta; item and person parameter scale qualit statistics (e.g., variance, MSE, separation index, number of strata, reliability estimates, etc...); and the raw score to theta conversion with the conditional standard error of measurement.  Other possible values include: person (prints person information and iteration log), qaqc (scale quality and iteration log), or lookup (conversion table and iteration log).  Any other arguments passed to this parameter will use the default, which prints everything but person information to the console.
                                                
                                               
## Examples

```
// Load example data set used in IRT manuals
webuse masc1.dta, clear
 
// Fit the model, print the results to the screen and add person parameters to 
// the current data set in memory
. raschjmle q1-q9
  800
 Iteration                 Delta           Log-likelihood
--------------------------------------------------------------
         1     0.502591842208104       -3402.304331969046
         2     0.142412255554409       -3397.822027114892
         3     0.020979991419945       -3397.719031584525
         4     0.003561687956111       -3397.716620516149
         5     0.000591506681447       -3397.716599152711
--------------------------------------------------------------
                                                                                                              
=================================================================================================        
Item           Difficulty     Std. Error        WMS       Std. WMS        UMS       Std. UMS             
-------------------------------------------------------------------------------------------------        
q1                  -0.40           0.08         0.85        -4.32         0.84        -2.86                  
q2                   0.11           0.08         1.03         1.04         1.05         1.04                  
q3                  -1.36           0.10         0.93        -1.39         0.86        -1.39                  
q4                   0.49           0.08         0.99        -0.25         1.02         0.38                  
q5                   1.66           0.09         0.93        -1.54         1.02         0.28                  
q6                   0.82           0.08         0.93        -2.05         0.95        -0.82                  
q7                   1.37           0.09         1.10         2.42         1.17         1.99                  
q8                  -1.87           0.11         0.77        -3.81         0.85        -1.14                  
q9                  -0.81           0.09         1.04         1.04         1.13         1.66                  
=================================================================================================        



SCALE QUALITY STATISTICS                          
==================================================
Statistic                  Items     Persons  
--------------------------------------------------
Observed Variance         1.3031      1.4411
Observed Std. Dev.        1.1415      1.2005
Mean Square Error         0.0080      0.7097
Root MSE                  0.0894      0.8425
Adjusted Variance         1.2951      0.7314
Adjusted Std. Dev.        1.1380      0.8552
Separation Index         12.7235      1.0151
Number of Strata         17.2980      1.6868
Reliability               0.9939      0.5075
==================================================

            SCORE TABLE                
==================================
  Score        Theta      Std. Err
----------------------------------
    0.00        -3.94         1.89     
    1.00        -2.55         1.12     
    2.00        -1.59         0.89     
    3.00        -0.89         0.80     
    4.00        -0.28         0.77     
    5.00         0.31         0.76     
    6.00         0.91         0.79     
    7.00         1.59         0.87     
    8.00         2.53         1.11     
    9.00         3.89         1.89     
==================================
``` 


