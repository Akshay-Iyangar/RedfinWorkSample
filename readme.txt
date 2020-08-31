
Instruction to run the code.

Please make sure you have JDK 8 and gradle installed.
java -version
gradle -version


1. Build the code using

./gradlew clean build shadowJar


2. Run the code using

java -classpath build/libs/RedfinWorkSample-1.0-all.jar com.redfin.exercise.Application

the api may get throttled in that case use the app token

java -classpath build/libs/RedfinWorkSample-1.0-all.jar com.redfin.exercise.Application jKmvMrZecuk6CTvaupbJA111C