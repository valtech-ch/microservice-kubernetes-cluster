variable "admin_username" {
  description = "Linux Administrator Password"
}

variable "agent_count" {
  default = 3
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