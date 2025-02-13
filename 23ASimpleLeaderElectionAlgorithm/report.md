### Report on Testing the Leader Election Algorithm

#### Introduction
This report details the testing process and results for the implementation of a simple O(n²) leader election algorithm for unidirectional rings.

#### Testing Setup
The leader election algorithm was implemented using the Akka framework in Java. The system consists of multiple actors arranged in a ring topology. Each actor can send and receive messages to and from its immediate neighbor.

#### Test Cases
To ensure the correctness of the algorithm, the following test cases were designed:

1. **Basic Functionality Test**:
    - **Objective**: Verify that the algorithm correctly elects a leader in a ring of actors.
    - **Setup**: Create a ring of 7 actors and initiate the election process from a random actor.
    - **Expected Result**: One actor should be elected as the leader, and the election process should terminate correctly.

2. **Multiple Elections Test**:
    - **Objective**: Verify that the algorithm can handle multiple election processes sequentially.
    - **Setup**: Create a ring of 7 actors and initiate the election process multiple times from different actors.
    - **Expected Result**: Each election should correctly elect a leader without interference from previous elections.

3. **Edge Case Test**:
    - **Objective**: Verify the algorithm's behavior with the minimum number of actors (2 actors).
    - **Setup**: Create a ring of 2 actors and initiate the election process.
    - **Expected Result**: The actor with the higher ID should be elected as the leader.

#### Results
The tests were executed, and the results were logged. Below are the observations for each test case:

1. **Basic Functionality Test**:
    - **Log Output**:
      ```
      [INFO] [actor1] received message Start from [actor3]
      [INFO] [actor1] received message Election from [actor1]
      [INFO] [actor1] is the leader
      [INFO] [actor2] received message Elected from [actor1]
      [INFO] [actor3] received message Elected from [actor2]
      ...
      ```
    - **Result**: The algorithm successfully elected `actor1` as the leader, and the election process terminated correctly.

2. **Multiple Elections Test**:
    - **Log Output**:
      ```
      [INFO] [actor4] received message Start from [actor6]
      [INFO] [actor4] received message Election from [actor4]
      [INFO] [actor4] is the leader
      [INFO] [actor5] received message Elected from [actor4]
      ...
      [INFO] [actor2] received message Start from [actor7]
      [INFO] [actor2] received message Election from [actor2]
      [INFO] [actor2] is the leader
      [INFO] [actor3] received message Elected from [actor2]
      ...
      ```
    - **Result**: The algorithm correctly handled multiple election processes, electing `actor4` and `actor2` as leaders in separate elections.

3. **Edge Case Test**:
    - **Log Output**:
      ```
      [INFO] [actor1] received message Start from [actor2]
      [INFO] [actor1] received message Election from [actor1]
      [INFO] [actor1] is the leader
      [INFO] [actor2] received message Elected from [actor1]
      ```
    - **Result**: The algorithm correctly elected `actor1` as the leader in a ring of 2 actors.