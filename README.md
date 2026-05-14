## iot-platform

Monorepo (Maven multi-module) for a basic microservice skeleton:

- **API Gateway**: `iot-gateway`
- **Core services**: `iot-service-core`, `iot-service-user`, `iot-service-device`, `iot-service-alarm`
- **Shared middleware starters**: `iot-platform-starter-redis`, `iot-platform-starter-kafka`, `iot-platform-starter-xxljob`
- **Deployment modules**: `xxl-job-admin`（Nacos 使用外部环境；配置模板见 `nacos-config/`）

### Docs

- **Deployment**: `docs/DEPLOYMENT.md`
- **Module responsibilities**: `docs/MODULES.md`

