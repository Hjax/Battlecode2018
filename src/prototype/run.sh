#!/bin/sh
# build the java files.
# there will eventually be a separate build step, but for now the build counts against your time.

# Compile our code.
echo javac $(find . -name '*.java') -classpath ../battlecode/ -d .
javac $(find . -name '*.java') -classpath ../battlecode/java -d .

# Run our code.
echo java -Xmx60m -classpath .:../battlecode/java prototype.Player
java -Xmx60m -classpath .:../battlecode/java prototype.Player