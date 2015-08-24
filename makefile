bin/StableMatching.java: src/*.java
	javac src/* -d bin
run:
	java -classpath bin StableMatching
