workspace "Microservice Kubernetes Cluster" "The software architecture" {

    !identifiers "hierarchical"

    model {
        !impliedRelationships "false"

        User = person "User" "The web user." "User"

        enterprise "Azure Cloud" {
            KubernetesCluster = softwareSystem "Microservice Kubernetes Cluster" "" "" {
                SinglePageApplication = container "Single-Page Application" "Provides frontend to user via their web browser." "Vue TypeScript" "Web Browser"
                WebServer = container "Web Server" "Delivers the static content and the single page application." "Nginx"
                FileStorageAPI = container "FileStorage API" "Provides file storage API." "Spring Boot" "" {
                    FileStorageController = component "File Storage Controller" "REST Controller" "Controller"
                    KafkaProducerService = component "Kafka Producer Service" "Sends messages on auditing topic" "Service"
                    KafkaReverseStream = component "Kafka ReverseStream Consumer" "Kafka Stream reading from auditing topic, invoking an Azure Function and sending the resulting messages to the reverse-auditing topic" "Component"
                    ReactivePersistenceClient = component "Reactive Persistence Client" "WebClient consuming reactive messages from persistence API" "Component"
                    AuditingServiceRest = component "Auditing Service REST" "REST Client communicating with the Persistence Auditing API" "Service"
                    AuditingServiceGrpc = component "Auditing Service GRPC" "GRPC Client communicating with the Persistence Auditing API" "Service"
                    AzureFunctionsService = component "Azure Functions Service" "REST Client invoking Azure Functions" "Service"
                    FileStorageCloudService = component "FileStorage Cloud Service" "Service interacting with the Azure Storage" "Service"
                    Security = component "SecurityComponent" "Spring Security" "Bean"
                }
                PersistenceAPI = container "Persistence API" "Provides Auditing API." "Spring Boot" "" {
                    PersistenceControllerRest = component "Persistence Controller REST" "REST Controller" "Controller"
                    PersistenceControllerGrpc = component "Persistence Controller GRPC" "GRPC Controller" "Controller"
                    PersistenceControllerGrpcReactive = component "Persistence Controller GRPC Reactive" "Reactive GRPC Controller" "Controller"
                    KafkaConsumerService = component "Kafka Consumer Service" "Consumes messages from the auditing and reverse-auditing topic" "Service"
                    Security = component "Security Component" "Spring Security" "Bean"
                    AuditingRepository = component "Auditing Repository" "R2DBC MariaDB Repository" "Repository"
                    PersistenceService = component "Persistence Service" "Service handling auditing messages" "Service"
                }
                Keycloak = container "Keycloak" "Keycloak Identity Service" "Helm Chart" ""
                Kafka = container "Event Streaming" "Kafka Cluster" "Helm Chart (Kubernetes Operator)" "Events"
                Database = container "Database" "Stores auditing messages." "Azure MariaDB" "Database"
                FunctionApp = container "Functions" "Azure serverless FAAS" "Azure Function App" "Function"
                Storage = container "Storage" "Azure Object store" "Azure Storage account" "Storage"
            }
        }

        User -> KubernetesCluster.WebServer "Visits website using" "HTTP/2" ""
        User -> KubernetesCluster.SinglePageApplication "Views and uploads files, read audit logs using" "" ""
        KubernetesCluster.WebServer -> KubernetesCluster.SinglePageApplication "Delivers to the customer's web browser" "" ""
        KubernetesCluster.FileStorageAPI -> KubernetesCluster.FunctionApp "Invokes function" "HTTPS" ""
        KubernetesCluster.FileStorageAPI -> KubernetesCluster.Kafka "Sends messages to topics" "TCP" ""
        KubernetesCluster.FileStorageAPI -> KubernetesCluster.Keycloak "Checks identity and permissions" "HTTP/2" ""
        KubernetesCluster.FileStorageAPI -> KubernetesCluster.Storage "Stores files" "HTTPS" ""
        KubernetesCluster.FileStorageAPI -> KubernetesCluster.PersistenceAPI "Sends and reads audit messages" "HTTP/2 and GRPC" ""
        KubernetesCluster.PersistenceAPI -> KubernetesCluster.Keycloak "Checks identity and permissions" "HTTP/2" ""
        KubernetesCluster.PersistenceAPI -> KubernetesCluster.Database "Reads from and writes to" "R2DBC" ""
        KubernetesCluster.SinglePageApplication -> KubernetesCluster.Keycloak "Makes API calls to" "HTTP/2" ""
        KubernetesCluster.SinglePageApplication -> KubernetesCluster.FileStorageAPI "Makes API calls to" "HTTP/2" ""
        KubernetesCluster.Kafka -> KubernetesCluster.FileStorageAPI "Consume messages from topics" "TCP" ""
        KubernetesCluster.Kafka -> KubernetesCluster.PersistenceAPI "Consume messages from topics" "TCP" ""

        KubernetesCluster.SinglePageApplication -> KubernetesCluster.FileStorageAPI.FileStorageController "Makes API calls to" "JSON/HTTP2" ""
        KubernetesCluster.FileStorageAPI.FileStorageController -> KubernetesCluster.FileStorageAPI.FileStorageCloudService "Invokes methods" "DTO" ""
        KubernetesCluster.FileStorageAPI.FileStorageController -> KubernetesCluster.FileStorageAPI.KafkaProducerService "Invokes methods" "DTO" ""
        KubernetesCluster.FileStorageAPI.FileStorageController -> KubernetesCluster.FileStorageAPI.AuditingServiceRest "Invokes methods" "DTO" ""
        KubernetesCluster.FileStorageAPI.FileStorageController -> KubernetesCluster.FileStorageAPI.AuditingServiceGrpc "Invokes methods" "DTO" ""
        KubernetesCluster.FileStorageAPI.ReactivePersistenceClient -> KubernetesCluster.PersistenceAPI "Sends and reads audit messages" "HTTP/2" ""
        KubernetesCluster.FileStorageAPI.KafkaProducerService -> KubernetesCluster.Kafka "Sends messages" "TCP" ""
        KubernetesCluster.Kafka -> KubernetesCluster.FileStorageAPI.KafkaReverseStream "Consumes messages" "TCP" ""
        KubernetesCluster.FileStorageAPI.KafkaReverseStream -> KubernetesCluster.FileStorageAPI.AzureFunctionsService "Invokes methods" "DTO" ""
        KubernetesCluster.FileStorageAPI.AuditingServiceRest -> KubernetesCluster.PersistenceAPI "Sends and reads audit messages" "HTTP/2" ""
        KubernetesCluster.FileStorageAPI.AuditingServiceRest -> KubernetesCluster.FileStorageAPI.ReactivePersistenceClient "Sends and reads audit messages" "HTTP/2" ""
        KubernetesCluster.FileStorageAPI.AuditingServiceGrpc -> KubernetesCluster.PersistenceAPI "Sends and reads audit messages" "HTTP/2" ""
        KubernetesCluster.FileStorageAPI.AzureFunctionsService -> KubernetesCluster.FunctionApp "Invokes Azure Functions" "HTTPS" ""
        KubernetesCluster.FileStorageAPI.FileStorageCloudService -> KubernetesCluster.Storage "Sends and reads files" "HTTPS" ""
        KubernetesCluster.FileStorageAPI.Security -> KubernetesCluster.Keycloak "Checks identity and permissions" "HTTP/2" ""
        KubernetesCluster.FileStorageAPI.FileStorageController -> KubernetesCluster.FileStorageAPI.Security "Uses" "" ""
        KubernetesCluster.FileStorageAPI.KafkaProducerService -> KubernetesCluster.FileStorageAPI.Security "Uses" "" ""
        KubernetesCluster.FileStorageAPI.ReactivePersistenceClient -> KubernetesCluster.FileStorageAPI.Security "Uses" "" ""
        KubernetesCluster.FileStorageAPI.AuditingServiceRest -> KubernetesCluster.FileStorageAPI.Security "Uses" "" ""
        KubernetesCluster.FileStorageAPI.AuditingServiceGrpc -> KubernetesCluster.FileStorageAPI.Security "Uses" "" ""

        KubernetesCluster.Kafka -> KubernetesCluster.PersistenceAPI.KafkaConsumerService "Consumes messages" "TCP" ""
        KubernetesCluster.FileStorageAPI -> KubernetesCluster.PersistenceAPI.PersistenceControllerGrpc "Sends and reads audit messages" "HTTP/2" ""
        KubernetesCluster.FileStorageAPI -> KubernetesCluster.PersistenceAPI.PersistenceControllerGrpcReactive "Sends and reads audit messages" "HTTP/2" ""
        KubernetesCluster.FileStorageAPI -> KubernetesCluster.PersistenceAPI.PersistenceControllerRest "Sends and reads audit messages" "HTTP/2" ""
        KubernetesCluster.PersistenceAPI.AuditingRepository -> KubernetesCluster.Database "Reads from and writes to" "R2DBC" ""
        KubernetesCluster.PersistenceAPI.PersistenceService -> KubernetesCluster.PersistenceAPI.AuditingRepository "Invokes methods" "Entities" ""
        KubernetesCluster.PersistenceAPI.KafkaConsumerService -> KubernetesCluster.PersistenceAPI.PersistenceService "Invokes methods" "DTO" ""
        KubernetesCluster.PersistenceAPI.Security -> KubernetesCluster.Keycloak "Checks identity and permissions" "HTTP/2" ""
                KubernetesCluster.PersistenceAPI.PersistenceControllerGrpc -> KubernetesCluster.PersistenceAPI.PersistenceService "Invokes methods" "DTO" ""
        KubernetesCluster.PersistenceAPI.PersistenceControllerGrpcReactive -> KubernetesCluster.PersistenceAPI.PersistenceService "Invokes methods" "DTO" ""
        KubernetesCluster.PersistenceAPI.PersistenceControllerRest -> KubernetesCluster.PersistenceAPI.PersistenceService "DTO" "DTO" ""
        KubernetesCluster.PersistenceAPI.KafkaConsumerService -> KubernetesCluster.PersistenceAPI.Security "Uses" "" ""
        KubernetesCluster.PersistenceAPI.PersistenceControllerGrpc -> KubernetesCluster.PersistenceAPI.Security "Uses" "" ""
        KubernetesCluster.PersistenceAPI.PersistenceControllerGrpcReactive -> KubernetesCluster.PersistenceAPI.Security "Uses" "" ""
        KubernetesCluster.PersistenceAPI.PersistenceControllerRest -> KubernetesCluster.PersistenceAPI.Security "Uses" "" ""
    }

    views {
        container KubernetesCluster "Containers" "The container diagram for the Kubernetes Cluster." {
            include User
            include KubernetesCluster.SinglePageApplication
            include KubernetesCluster.WebServer
            include KubernetesCluster.FileStorageAPI
            include KubernetesCluster.PersistenceAPI
            include KubernetesCluster.Keycloak
            include KubernetesCluster.Kafka
            include KubernetesCluster.Database
            include KubernetesCluster.FunctionApp
            include KubernetesCluster.Storage
            autolayout
        }

        component KubernetesCluster.FileStorageAPI "Filestorage" "Filestorage API Application." {
            include KubernetesCluster.SinglePageApplication
            include KubernetesCluster.PersistenceAPI
            include KubernetesCluster.Keycloak
            include KubernetesCluster.Kafka
            include KubernetesCluster.FunctionApp
            include KubernetesCluster.Storage
            include KubernetesCluster.FileStorageAPI.FileStorageController
            include KubernetesCluster.FileStorageAPI.ReactivePersistenceClient
            include KubernetesCluster.FileStorageAPI.KafkaProducerService
            include KubernetesCluster.FileStorageAPI.KafkaReverseStream
            include KubernetesCluster.FileStorageAPI.AuditingServiceRest
            include KubernetesCluster.FileStorageAPI.AuditingServiceGrpc
            include KubernetesCluster.FileStorageAPI.AzureFunctionsService
            include KubernetesCluster.FileStorageAPI.FileStorageCloudService
            include KubernetesCluster.FileStorageAPI.Security
            autolayout
        }

        component KubernetesCluster.PersistenceAPI "Persistence" "Persistence API Application." {
            include KubernetesCluster.SinglePageApplication
            include KubernetesCluster.FileStorageAPI
            include KubernetesCluster.Keycloak
            include KubernetesCluster.Kafka
            include KubernetesCluster.Database
            include KubernetesCluster.PersistenceAPI.PersistenceControllerRest
            include KubernetesCluster.PersistenceAPI.PersistenceControllerGrpc
            include KubernetesCluster.PersistenceAPI.PersistenceControllerGrpcReactive
            include KubernetesCluster.PersistenceAPI.KafkaConsumerService
            include KubernetesCluster.PersistenceAPI.AuditingRepository
            include KubernetesCluster.PersistenceAPI.PersistenceService
            include KubernetesCluster.PersistenceAPI.Security
            autolayout
        }

        styles {
            element "Component" {
                background "#85bbf0"
                color "#000000"
            }
            element "Container" {
                background "#438dd5"
                color "#ffffff"
            }
            element "Customer" {
                background "#08427b"
            }
            element "Database" {
                shape "Cylinder"
            }
            element "Existing System" {
                background "#999999"
                color "#ffffff"
            }
            element "Failover" {
                opacity "25"
            }
            element "Person" {
                shape "Person"
                color "#ffffff"
                fontSize "22"
            }
            element "Software System" {
                background "#1168bd"
                color "#ffffff"
            }
            element "Web Browser" {
                shape "WebBrowser"
            }
            element "Function" {
                shape "Hexagon"
            }
            element "Events" {
                shape "Pipe"
            }
            element "Storage" {
                shape "Folder"
            }
            relationship "Failover" {
                position "70"
                opacity "25"
            }
        }

    }

}