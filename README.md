# Microservice Kubernetes Cluster

![Build](https://github.com/valtech-ch/microservice-kubernetes-cluster/actions/workflows/gradle-build.yml/badge.svg)
![Sonar](https://github.com/valtech-ch/microservice-kubernetes-cluster/actions/workflows/gradle-analyse.yml/badge.svg)
![Deployment](https://github.com/valtech-ch/microservice-kubernetes-cluster/actions/workflows/gradle-deploy.yml/badge.svg)

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

### Terraform Backend
Setup the Azure Backend one time
```bash
cd ./terraform/backend
terraform init
terraform apply
```

### Terraform Cluster
Provision the cluster by using the azure backend

Set the credentials in [terraform.tfvars](../terraform/cluster/terraform.tfvars)

```bash
cd ./terraform/backend
terraform init
terraform apply
```

## Frontend

[Frontend](../frontend/README.md)

## Backend Microservices
```bash
./gradlew build # Full build including cloud functions
```

## Cloud Functions
```bash
./gradlew build # Full build including backend microservices
```