# AGENTS.md

Spring Cloud microservices demo (Java 17, Spring Boot 3.3.x, Maven).

## Build & Run

- Build: `mvn clean compile`
- Test all: `mvn clean test`
- Package (skip tests): `mvn clean package -DskipTests`
- No Maven wrapper — `mvn` must be on PATH.

## Running Individual Services

```
mvn spring-boot:run -pl services/service-product    # port 9000
mvn spring-boot:run -pl services/service-order      # port 8000
```

## Runtime Dependencies

- **Nacos must be running at `127.0.0.1:8848`** — both services register with and pull config from Nacos.
- No database; ProductServiceImpl returns hardcoded mock data.

## Module Structure

```
cloud-demo/              # root POM (dependency management)
├── model/               # shared JAR — Order, Product beans (Lombok)
└── services/            # aggregator POM (shared deps: Nacos, OpenFeign, Lombok)
    ├── service-order/   # depends on Sentinel, LoadBalancer
    └── service-product/ # minimal; just web + Nacos discovery
```

## Config Quirks

- **service-order** uses multi-document YAML (`application.yml` with `---`). Active profile defaults to `prod`. Each profile imports Nacos config via `spring.cloud.nacos.config.import-check.enabled=false` pattern.
- **Feign config** is in a separate file `application-feign.yml`, activated via `spring.profiles.include: feign`. This is where Sentinel (`feign.sentinel.enabled=true`) and timeouts are configured.
- **service-product** uses flat `.properties` instead of YAML — don't apply YAML conventions there.

## Code Oddities

- **Package `frign` is a typo** — it's `com.atguigu.order.frign`, not `feign`. Don't "fix" it; the `@FeignClient` annotation references work fine.
- `XTokenRequestInterceptor` is `@Component` but its wiring in `application-feign.yml` is commented out. It's inactive unless explicitly wired.
- `OrderServiceImpl` contains dead code (unused private methods for manual DiscoveryClient/RestTemplate calls). The actual invocation path uses `ProductFeignClient`.
- `@LoadBalanced RestTemplate` bean exists in `OrderServiceConfig` but is unused by current code.

## Testing

- Three JUnit Jupiter tests: `weatherTest` (calls external API), `LoadBalancerTest`, `DiscoveryTest`.
- Run a single test: `mvn test -pl services/service-order -Dtest=weatherTest`

## 会话规则

- 所有思考和回复内容均使用中文。
