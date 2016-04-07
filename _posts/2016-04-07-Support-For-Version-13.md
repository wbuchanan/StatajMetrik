---
layout: post
title:  "Stata 13 Now Supported"
date:   2015-04-07 05:38:00
categories: post
---

# Stata 13 Supported 
The `raschjmle` command now works on Stata 13 and later.  No addition methods have been added yet, but with the refactoring of the [Stata Java Utilities](https://github.com/wbuchanan/StataJavaUtilities) to provide support for Stata version 13 in [Stata Java Utilities 13](https://github.com/wbuchanan/StataJavaUtilities13).  However, the program does still require Java 8 or later.  You can find out which JVM Stata is using with the `query java` command.  If you are working on OSX it would look something like:

```Stata
. query java
----------------------------------------------------------------------------------------------------------------------------------------
    Java system information
        Java initialized    no
    Advanced Java settings
        set java_heapinit   16m
        set java_heapmax    384m
        set java_vmpath     /Library/Java/JavaVirtualMachines/jdk1.8.0_74.jdk/Contents/Home/jre/lib/server/libjvm.dylib

```

Note that in the example above the path `/Library/Java/JavaVirtualMachines/jdk1.8.0_74.jdk/Contents/Home/jre/lib/server/libjvm.dylib` is __not the default JVM path set by Stata__.  After installing the latest version of Java, you can change teh JVM used by Stata with the `set java_vmpath` command.  To set the JVM path above, I used the command:
 
```Stata
set java_vmpath "/Library/Java/JavaVirtualMachines/jdk1.8.0_74.jdk/Contents/Home/jre/lib/server/libjvm.dylib"
```
 
 