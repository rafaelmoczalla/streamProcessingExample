# Flink Cluster for Local Use
Author: Rafael Moczalla

Create Date: 1 March 2021

Last Update: 5 January 2022

Tested with Docker in version 20.10.4 and Docker Compose in version 1.28.5 on Ubuntu
21.10.

## Prerequisites
To install Docker
```bash
sudo apt-get remove docker docker-engine docker.io containerd runc
sudo apt-get update
sudo apt-get install \
apt-transport-https \
ca-certificates \
curl \
gnupg-agent \
software-properties-common
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
sudo add-apt-repository \
"deb [arch=amd64] https://download.docker.com/linux/ubuntu \
$(lsb_release -cs) \
stable"
sudo apt-get update
sudo apt-get install docker-ce docker-ce-cli containerd.io
sudo usermod -aG docker $USER
```

Test if docker is working with the following command
`sudo docker run hello-world`

To install Docker Compose
```bash
sudo curl -L "https://github.com/docker/compose/releases/download/1.28.5/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
sudo ln -s /usr/local/bin/docker-compose /usr/bin/docker-compose
```

## Start/Stop/Remove the Cluster
To start the cluster just run the following command in the same folder as the
docker-compose file
`docker-compose up -d --scale taskmanager=3`

To change the number of taskmanager nodes change the 3 to your desired number of
taskmanager nodes.

To access the dashboard type `127.0.0.1:8081` or `localhost:8081` into your Browser.

To stop the cluster run the following command in the same folder as the docker-compose
file.
`docker-compose stop`

To remove the cluster run the following command. All containers will be removed.
`docker-compose rm`

## Work on a Cluster Node via Command Line
To login to a cluster node via command line one has to execute a bash on the container
via `docker exec -it ContainerName bash` where `ContainerName` has to be replaced by the
name of the desired container.

To show a list of all running containers run `docker container list --format {{.Names}}`
in the console.

Be careful! Do not forget that after removing the containers all changes in a container
are removed as well.

## Submit a Job to the Cluster
To submit a job just download a version of Flink and use it to submit a job as follows.

1. Download and unzip Flink if not already done
```bash
curl https://downloads.apache.org/flink/flink-1.14.2/flink-1.14.2-bin-scala_2.12.tgz | tar -xz
```

2. Change into the directory and submit an example job as follows
```bash
cd flink-1.14.2
./bin/flink run ./examples/streaming/TopSpeedWindowing.jar
```
