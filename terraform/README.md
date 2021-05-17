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

Set the credentials in [terraform.tfvars](cluster/terraform.tfvars)

```bash
cd ./terraform/backend
terraform init
terraform apply
```