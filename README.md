
# LeaderBoard Library

LeaderBoard library contains an awesome LeaderBoard class implementation.

The library stores the matches results in an in-memory sorted collection and has the following properties:
* Contains singleton implementation with factory pattern generation.
* Thread-safe access to public functions.
* Implemented tests.

# Features

The scoreboard supports the following operations:
* Start a new match, assuming initial score 0 – 0 and adding it the scoreboard.  
  This should capture following parameters:
    * Home team
    * Away team
* Update score. This should receive a pair of absolute scores: home team score and away  
  team score.
* Finish match currently in progress. This removes a match from the scoreboard.
* Get a summary of matches in progress ordered by their total score. The matches with the  
  same total score will be returned ordered by the most recently started match in the  
  scoreboard.

# Key assumptions

I assume that majority of the access calls will be requesting the leaderboard results with occasional updates 
when a team scores or a match finishes. Add, update, remove match operations have complexity O(log(n)) and 
retrieval is O(n) as we are just listing all entries. 

Solution is storing the results in a sorted data structure which means that 
adding and updating matches keeps them sorted in the requested order.

I am returning leaderboard as a copy to prevent exposing the internal structure from the external world.

# Dependencies

Java 21 LTS is required to be installed on the DEV machine. Installed versions can be managed by SDK manager (https://sdkman.io).

Core of the solution is implemented with Core Java functionalities. Development dependencies are managed by Maven and are specified in the pom.xml. We are just using the JUnit 5 test framework and Jetbrains Annotations library. The Annotations library allows us explicitly declaring the nullability of elements, the code becomes easier to maintain and less prone to nullability-related errors.

During the development I used IntelliJ IDEA IDE which uses Jetbrains annotation to improve the code quality when extending the functionality.

# Building the Library

Apache Maven is used as a build tool.

Building the library is simple, just run:
```
# package JAR
mvn clean package
# execute tests
mvn clean test
```
This will compile and package Java code and create a "target" folder which contains:

* wc-leaderboard-1.0-SNAPSHOT.jar - JAR library that can be distributed via a private Maven repository (Nexus, JFrog, ...) or a public one.
* site/jacoco/index.html - Test coverage report
* Java bytecode and intermediate build and test files

# Using the library

```
import static com.sportradar.wc.CountryEnum.*;
import com.sportradar.wc.LeaderBoard;
import com.sportradar.wc.MatchEntry;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        LeaderBoard leaderBoard = LeaderBoard.getInstance();
        leaderBoard.addMatch(new MatchEntry(SLOVENIA, SERBIA, 0, 0));
        leaderBoard.addMatch(new MatchEntry(CROATIA, SERBIA, 0, 0));

        leaderBoard.updateMatch(new MatchEntry(SLOVENIA, SERBIA, 3, 0));
        leaderBoard.updateMatch(new MatchEntry(CROATIA, SERBIA, 0, 2));
        System.out.println(leaderBoard.getScoreBoard());
    }
}
// outputs: [MatchEntry[homeTeam=SLOVENIA, awayTeam=SERBIA, homeScore=3, awayScore=0], MatchEntry[homeTeam=CROATIA, awayTeam=SERBIA, homeScore=0, awayScore=2]]
```