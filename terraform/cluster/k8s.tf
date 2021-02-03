provider "azurerm" {
  features {}
}

terraform {
  backend "azurerm" {
    resource_group_name  = "vtch-kubernetes-blueprint"
    storage_account_name = "vtchk8sblueprinttfst"
    container_name       = "vtchk8sblueprinttfct"
    key                  = "terraform.tfstate"
  }
}

resource "azurerm_role_assignment" "role_acrpull" {
  scope                            = azurerm_container_registry.acr.id
  role_definition_name             = "AcrPull"
  principal_id                     = azurerm_kubernetes_cluster.k8s.kubelet_identity.0.object_id
  skip_service_principal_aad_check = true
}

resource "azurerm_container_registry" "acr" {
  name                = var.container_registry_name
  resource_group_name = var.resource_group_name
  location            = var.location
  sku                 = "Basic"
  admin_enabled       = true
}

resource "azurerm_kubernetes_cluster" "k8s" {
  name                = var.cluster_name
  location            = var.location
  resource_group_name = var.resource_group_name
  dns_prefix          = var.dns_prefix

  linux_profile {
    admin_username = var.admin_username
    ssh_key {
      key_data = "${trimspace(tls_private_key.ssh_key.public_key_openssh)} ${var.admin_username}@azure.com"
    }
  }

  identity {
    type = "SystemAssigned"
  }

  default_node_pool {
    name            = "agentpool"
    node_count      = var.agent_count
    vm_size         = "Standard_D2_v2"
  }

  network_profile {
    load_balancer_sku = "Standard"
    network_plugin = "kubenet"
  }

  role_based_access_control {
    enabled = true
  }

  addon_profile {
    kube_dashboard {
      enabled = true
    }
    http_application_routing {
      enabled = false
    }
  }

  tags = {
    Environment = "Demo"
  }
  depends_on = [tls_private_key.ssh_key]
}

resource "tls_private_key" "ssh_key" {
  algorithm = "RSA"
  rsa_bits  = 2048
}