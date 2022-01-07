# Simulation of a Stream Processing Use Case
Stream processing simulation of a typical game use case.

Author: [Rafael Moczalla](Rafael.Moczalla@hpi.de)

Create Date: 04 January 2022

Last Update: 05 January 2022

Tested on Ubuntu 21.10.

## Prerequisites
1. Install git.
    ```bash
    sudo apt install git
    ```

2. Download the project and change directory to the project folder.
    ```bash
    git clone git@github.com:rafaelmoczalla/streamProcessingExample.git
    cd <path/to/the/project/folder>
    ```

## Usage
You can run the simulation either on your local machine without or with a simulated
cluster. The cluster is created with docker containers.

### Local Execution
To run the example on your local machine without a cluster
run the gradle run command.
```bash
./gradlew run
```

### Cluster Execution

First you need to create the cluster and build an executable file for the cluster.
1. To create the cluster go into the cluster folder. See the [README](cluster/README.md)
in the cluster folder for more details.

2. Build the executable jar file
    ```bash
    ./gradlew jar
    ```

To run the example on the cluster download a version of Flink and use it to submit a job.

3. Download and unzip Flink if not already done
    ```bash
    curl https://downloads.apache.org/flink/flink-1.14.2/flink-1.14.2-bin-scala_2.12.tgz | tar -xz
    ```

4. Run the example as follows
    ```bash
    ./flink-1.14.2/bin/flink run ./build/libs/streamProcessingExample-1.0-SNAPSHOT.jar
    ```
