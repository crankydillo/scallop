This is a fork of [scallop](https://github.com/Rogach/scallop).

Added Features
==============

* Option values can be supplied via a java.util.Properties-formatted configuration file

java.util.Properties Backing
============================

You can choose to supply option values through a configuration file.  The file should use the java.util.Properties syntax.  This is done by setting an environment like so:

```
$ scala -Dscallop.app.config=/path/to/config.properties YourCoolCLI
```

If an option's value is specified from the command line and in the configuration file, the command line value is preferred.
