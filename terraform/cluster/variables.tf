variable "admin_username" {
  description = "Linux Administrator Password"
}

variable "agent_min_count" {
  default = 2
}

variable "agent_max_count" {
  default = 3
}

variable "app_pool_size" {
  default = 2
}

variable "dns_prefix" {
  default = "vtchk8sblueprintdns"
}

variable cluster_name {
  default = "vtchk8sblueprintcluster"
}

variable resource_group_name {
  description = "Name of the Azure Resource Group"
  default = "vtch-kubernetes-blueprint"
}

variable location {
  description = "Azure Location"
  default = "Switzerland North"
}

variable "container_registry_name" {
  description = "Container Registry Name"
  default     = "vtchk8sblueprintrg"
}

variable "mariadb_username" {
  description = "Maria DB username login credential"
}

variable "mariadb_password" {
  description = "Maria DB password login credential"
}

variable "storage_account_name" {
  description = "Terraform State Storage Account Name"
  default     = "vtchk8sblueprinttfst"
}

variable "storage_account_access_key" {
  description = "Terraform State Storage Account Key"
}