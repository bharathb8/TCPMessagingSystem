~~~~~~~~~~~~~~~README~~~~~~~~~~~
Compiled using Java 1.8

To Compile:
javac src/com/*.java

To run from src folder:

java com.Server

java com.Client "127.0.0.1"

Test cases:
Compile from com folder
javac -cp .:./src:./jars/junit-4.10.jar:./jars/mockito-all-1.9.0.jar tests/com/*.java 

To run test cases from tests folder
java -cp .:../src:../jars/junit-4.10.jar:../jars/mockito-all-1.9.0.jar com.RelayServiceTest
java -cp .:../src:../jars/junit-4.10.jar:../jars/mockito-all-1.9.0.jar com.ServerTest
java -cp .:../src:../jars/junit-4.10.jar:../jars/mockito-all-1.9.0.jar com.CommandReceiverTest