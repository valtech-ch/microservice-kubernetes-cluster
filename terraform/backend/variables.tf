variable "storage_account_name" {
  description = "Terraform State Storage Account Name"
  default     = "vtchk8sblueprinttfst"
}

variable "storage_container_name" {
  description = "Terraform State Storage Container Name"
  default     = "vtchk8sblueprinttfct"
}

variable location {
  description = "Azure Location"
  default = "Switzerland North"
}

variable resource_group_name {
  description = "Name of the Azure Resource Group"
  default = "vtch-kubernetes-blueprint"
}