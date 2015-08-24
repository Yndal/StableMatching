bin/StableMatching.java: src/*.java
		javac src/StableMatching.java -d bin
run:
	java -classpath bin StableMatching
