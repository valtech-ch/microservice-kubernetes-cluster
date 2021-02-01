variable "client_id" {
    description = "Azure Kubernetes Service Cluster service principal"
}
variable "client_secret" {
    description = "Azure Kubernetes Service Cluster password"
}

variable "agent_count" {
    default = 3
}

variable "ssh_public_key" {
    default = "~/.ssh/id_rsa.pub"
}

variable "dns_prefix" {
    default = "k8scluster"
}

variable cluster_name {
    default = "k8scluster"
}

variable resource_group_name {
    default = "azure-k8scluster"
}

variable location {
    default = "Switzerland North"
}
