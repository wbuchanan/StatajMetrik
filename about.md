---
layout: page
title: about
permalink: /about/
---

Project page for [Stata](http://www.stata.com) and [jMetrik](http://www.itemanalysis.com) integration.  The goal of the project is to make the capabilities of [jMetrik](http://www.itemanalysis.com) and the [Java Psychometrics](https://github.com/meyerjp3/psychometrics) available to users of [Stata](http://www.stata.com) with a familiar API.  Currently, the package only includes methods for the Joint Maximum Likelihood Estimator of Binary Items fitted with a Rasch model.  As time permits additional methods will be made available.  Over time this will likely require the generation of a new Stata API that would likely mirror the syntax structure of the `irt` commands currently available (e.g., jmetrik raschjmle, jmetrik 4pl, etc...).

The program now is available for Stata 13 and 14 without any need to install any additional packages.  The only requirement is that you __must__ have __Java 8 or later__ installed on your system and must tell Stata to use the __Java 8 JVM__.