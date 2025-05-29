job "mahu-dev" {
    datacenters = ["dc1"]
    type = "service"

    group "backend" {
        network {
            port "postgres" {
                static = 15432
                to     = 5432  # 容器内端口(可选)
            }

            port "rabbitmq" {
                static = 15672
                to     = 5672  # 容器内端口(可选)
            }

            port "minio" {
                static = 19000
                to     = 9000  # 容器内端口(可选)
            }
        }

        task "postgres" {
            driver = "podman"
            config {
                image = "m.daocloud.io/docker.io/postgres:17.5-alpine"
                ports = ["postgres"]
            }
            env {
                POSTGRES_PASSWORD = "test"
                POSTGRES_USER     = "test"
                POSTGRES_DB       = "mahu-dev"
            }
        }

        task "rabbitmq" {
            driver = "podman"
            config {
                image = "m.daocloud.io/docker.io/rabbitmq:4.1.0-management-alpine"
                ports = ["rabbitmq"]
            }
            env {
                RABBITMQ_DEFAULT_USER = "guest"
                RABBITMQ_DEFAULT_PASS = "guest"
            }
        }

        task "minio" {
            driver = "podman"
            config {
                image = "m.daocloud.io/docker.io/minio/minio:RELEASE.2025-05-24T17-08-30Z"
                ports = ["minio"]
            }
            env {
                MINIO_ROOT_USER     = "minioadmin"
                MINIO_ROOT_PASSWORD = "minioadmin"
            }
        }
    }
}
