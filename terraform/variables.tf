variable "client_id" {
  description = "Azure Kubernetes Service Cluster service principal"
}
variable "client_secret" {
  description = "Azure Kubernetes Service Cluster password"
}

variable "admin_username" {
  description = "Linux Administrator Password"
}

variable "agent_count" {
  default = 3
}

variable "dns_prefix" {
  default = "k8scluster-dns"
}

variable cluster_name {
  default = "k8scluster"
}

variable resource_group_name {
  description = "Name of the Azure Resource Group"
  default = "vtch-kubernetes-blueprint"
}

variable location {
  description = "Azure Location"
  default = "Switzerland North"
}

variable "storage_account_name" {
  description = "Terraform State Storage Account Name"
  default     = "terraformstate"
}

variable "storage_container_name" {
  description = "Terraform State Storage Container Name"
  default     = "terraformstate"
}

variable "container_registry_name" {
  description = "Container Registry Name"
  default     = "k8sclusterregistry"
}