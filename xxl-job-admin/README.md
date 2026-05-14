## XXL-Job Admin

This module is kept as a **deployment sub-module** to manage all scheduled jobs in one place.

### Run with Docker (recommended)

```bash
cd D:\Project\iot-platform\xxl-job-admin
docker compose up -d
```

- **Admin UI**: `http://localhost:8090/xxl-job-admin`
- **MySQL**: exposed on `localhost:3307`

### Executor services

Each microservice can enable executor registration via:

- `iot.xxl.job.enabled=true`
- `iot.xxl.job.admin-addresses=http://localhost:8090/xxl-job-admin`
- `iot.xxl.job.appname=<service-name>`

