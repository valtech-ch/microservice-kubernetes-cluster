# Microservice Kubernetes Cluster

![Build](https://github.com/valtech-ch/microservice-kubernetes-cluster/actions/workflows/gradle-build.yml/badge.svg)
![Sonar](https://github.com/valtech-ch/microservice-kubernetes-cluster/actions/workflows/gradle-analyse.yml/badge.svg)
![Deployment](https://github.com/valtech-ch/microservice-kubernetes-cluster/actions/workflows/gradle-deploy.yml/badge.svg)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=valtech-ch_microservice-kubernetes-cluster&metric=alert_status)](https://sonarcloud.io/dashboard?id=valtech-ch_microservice-kubernetes-cluster)

### Requirements

* [kubectl](https://kubernetes.io/docs/tasks/tools/)
* [Azure CLI](https://docs.microsoft.com/en-us/cli/azure/install-azure-cli)
* [Helm](https://helm.sh/docs/intro/install/)
* [Argo CD CLI](https://argoproj.github.io/argo-cd/cli_installation/)
* [kubeseal](https://github.com/bitnami-labs/sealed-secrets#installation)
* [Terraform CLI](https://learn.hashicorp.com/tutorials/terraform/install-cli)
* [Java 15](https://www.azul.com/downloads/?version=java-15-mts&package=jdk)
* [Gradle](https://gradle.org/install/)
* [Docker](https://docs.docker.com/get-docker/)
* [Docker Compose](https://docs.docker.com/compose/install/)
* [NPM](https://www.npmjs.com/get-npm)

## Azure

### Setup Kubectl
```bash
az login # Login to Azure, use your @valtech.com Account
az account list --output table # Show your accounts
az account set --subscription <subscriptionId> # Set account to your Subscription.
az aks get-credentials --name vtch-kubernetes-blueprint --resource-group vtch-kubernetes-blueprint # Adds the cluster config to your kubectl config
kubectl get nodes # Lists the nodes from the cluster
kubectl get namespace # Lists the namespaces from the cluster
```

### Apply manual configs

You can use kubectl to manually apply changes for testing

```bash
kubectl apply -f ./aks/cluster/templates/application-peer-authentication.yaml
```

## Terraform

* [terraform/README.md](terraform/README.md)

## Local Docker Compose Setup
For testing the whole cluster locally we added a [docker-compose](scripts/docker-compose/docker-compose.yml) file which runs the FE, BE, Keycloak, Kafka and MariaDB
```bash
cd ./scripts/docker-compose
docker-compose up -d
docker-compose down
```

## Frontend
* [frontend/README.md](frontend/README.md)

```bash
npm install # Build
npm run serve # Run
```

## Backend Microservices

* [file-storage/README.md](file-storage/README.md)
* [persistence/README.md](persistence/README.md)

```bash
./gradlew build # Full build including cloud functions

./gradlew :file-storage:build # Build file storage microservice only
./gradlew :persistence:build # Build persistence microservice only

# Run locally
./gradlew :file-storage:bootRun
./gradlew :persistence:bootRun
```

## Cloud Functions

* [functions/README.md](functions/README.md)

```bash
./gradlew build # Full build including backend microservices
./gradlew :functions:build # Build functions only

# Run locally
./gradlew azureFunctionsRun
```

## Kubeseal Secret Management
Have a read at https://github.com/bitnami-labs/sealed-secrets

Sample sealing of a secrert.

```bash
# Replace sealed-secrets-1614621994 by the controller name in the cluster
kubeseal \
--controller-name=sealed-secrets-1614621994 \
--controller-namespace=kube-system \
--format yaml <xyz-secret.yaml >xyz-secret-sealed.yaml
```