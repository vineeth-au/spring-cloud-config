## Overview
This repository demonstrates a peculiar configuration merging behavior that occurs when deploying Spring Boot applications to Kubernetes with profile-specific YAML files. While everything works as expected locally, the same application exhibits unexpected configuration merging behavior when running in a Kubernetes environment.

### The Problem
A Spring Boot application with multiple configuration files. A standard `application.yaml` file in your Spring Boot project that looks like this:


```yaml
my-application-name:
action1:
- "Item 1"
- "Item 2"
- "Item 3"
- "Item 4"
- "Item 5"
```
And a profile-specific configuration `application-dev.yaml` with these values:
```yaml
my-application-name:
action1:
- "Item 1"
- "Item 6"
- "Item 7"
```
### Expected Behavior
When you activate the dev profile in Spring Boot, the profile-specific configuration would completely override the base configuration for the `action1` property. So the final result should be:
```yaml
my-application-name:
action1:
- "Item 1"
- "Item 6"
- "Item 7"
```

### What Actually Happens
When running locally: Everything works perfectly! You get exactly what you expect: `["Item 1", "Item 6", "Item 7"]`.
However, when running on Kubernetes instead of the expected override behavior, you get what appears to be a merge of both configurations: `["Item 1", "Item 6", "Item 7", "Item 4", "Item 5"]`.

Notice how `Items 4` and `Item 5` from the base configuration, and they somehow sneak their way back into the final result, even though they should have been completely replaced by the profile-specific configuration.

### Why This Matters
This inconsistency between local and Kubernetes deployments can cause some serious headaches:

* Environment Parity Issues: Your application behaves differently in different environments, which violates the principle of environment parity and mental sanity!!!
* Debugging Nightmares: When your config works locally but fails in production, it can lead to hours of frustrating debugging sessions.
* Configuration Management Confusion: It becomes unclear which configuration values will actually be used in production.

### Steps to Reproduce the issue

1. Let's build our Spring Boot application using Maven.
```shell
  mvn clean install
```
2. Run the application locally to see how it should behave. This project includes `default`, `dev` & `staging` for testing. 
```shell
  mvn spring-boot:run -Dspring-boot.run.profiles="dev" 
```  
3. When the application starts up, you should see something like `AppConfig(items=[Item 1, Item 6, Item 7])`. This shows that the configuration is working exactly as expected - the dev profile configuration has completely overridden the base configuration, giving us exactly 3 items in our list.
4. Here's where things get weird. Let's deploy the exact same application to a Kubernetes cluster and see what happens. For this example, I'm using `minikube`.
```shell
  k apply -f minikube.yaml
```
This will create all the necessary Kubernetes resources (deployment, service, etc.).

PS: My miniKube's overly permissive `cluster-admin` `ClusterRoleBinding` was bound to the `system:serviceaccounts` group. This resulted in all service accounts in my default cluster having `cluster-admin` privileges. Since it's just a miniKube I ended up doing this
> [!WARNING]
> Do not do this in your actual cluster.
```shell
  k create clusterrolebinding serviceaccounts-cluster-admin --clusterrole=cluster-admin --group=system:serviceaccounts
```

### Log Comparison 
Attaching the logs from my `local` and `miniKube`.

Local - 

The first line from the below log shows that the active profile is dev. The `AppConfig(items=[Item 1, Item 6, Item 7])` clearly shows that only the elements present in dev is present.
```log
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

 :: Spring Boot ::               (v3.3.12)

2025-06-17T13:02:32.744-04:00  INFO 19610 --- [my-spring-config-test-app] [           main] com.spring.Application                   : The following 1 profile is active: "dev"
2025-06-17T13:02:32.964-04:00  INFO 19610 --- [my-spring-config-test-app] [           main] o.s.cloud.context.scope.GenericScope     : BeanFactory id=6d19df4a-28eb-321a-8361-5e088fa4a504
2025-06-17T13:02:32.984-04:00  WARN 19610 --- [my-spring-config-test-app] [           main] trationDelegate$BeanPostProcessorChecker : Bean 'org.springframework.cloud.client.loadbalancer.LoadBalancerAutoConfiguration$DeferringLoadBalancerInterceptorConfig' of type [org.springframework.cloud.client.loadbalancer.LoadBalancerAutoConfiguration$DeferringLoadBalancerInterceptorConfig] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying). The currently created BeanPostProcessor [lbRestClientPostProcessor] is declared through a non-static factory method on that class; consider declaring it as static instead.
2025-06-17T13:02:32.985-04:00  WARN 19610 --- [my-spring-config-test-app] [           main] trationDelegate$BeanPostProcessorChecker : Bean 'deferringLoadBalancerInterceptor' of type [org.springframework.cloud.client.loadbalancer.DeferringLoadBalancerInterceptor] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying). Is this bean getting eagerly injected/applied to a currently created BeanPostProcessor [lbRestClientPostProcessor]? Check the corresponding BeanPostProcessor declaration and its dependencies/advisors. If this bean does not have to be post-processed, declare it with ROLE_INFRASTRUCTURE.
2025-06-17T13:02:32.985-04:00  WARN 19610 --- [my-spring-config-test-app] [           main] trationDelegate$BeanPostProcessorChecker : Bean 'org.springframework.cloud.client.loadbalancer.reactive.LoadBalancerBeanPostProcessorAutoConfiguration' of type [org.springframework.cloud.client.loadbalancer.reactive.LoadBalancerBeanPostProcessorAutoConfiguration] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying). The currently created BeanPostProcessor [loadBalancerWebClientBuilderBeanPostProcessor] is declared through a non-static factory method on that class; consider declaring it as static instead.
2025-06-17T13:02:32.986-04:00  WARN 19610 --- [my-spring-config-test-app] [           main] trationDelegate$BeanPostProcessorChecker : Bean 'org.springframework.cloud.client.loadbalancer.reactive.LoadBalancerBeanPostProcessorAutoConfiguration$ReactorDeferringLoadBalancerFilterConfig' of type [org.springframework.cloud.client.loadbalancer.reactive.LoadBalancerBeanPostProcessorAutoConfiguration$ReactorDeferringLoadBalancerFilterConfig] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying). Is this bean getting eagerly injected/applied to a currently created BeanPostProcessor [loadBalancerWebClientBuilderBeanPostProcessor]? Check the corresponding BeanPostProcessor declaration and its dependencies/advisors. If this bean does not have to be post-processed, declare it with ROLE_INFRASTRUCTURE.
2025-06-17T13:02:32.986-04:00  WARN 19610 --- [my-spring-config-test-app] [           main] trationDelegate$BeanPostProcessorChecker : Bean 'reactorDeferringLoadBalancerExchangeFilterFunction' of type [org.springframework.cloud.client.loadbalancer.reactive.DeferringLoadBalancerExchangeFilterFunction] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying). Is this bean getting eagerly injected/applied to a currently created BeanPostProcessor [loadBalancerWebClientBuilderBeanPostProcessor]? Check the corresponding BeanPostProcessor declaration and its dependencies/advisors. If this bean does not have to be post-processed, declare it with ROLE_INFRASTRUCTURE.
App Config {}AppConfig(items=[Item 1, Item 6, Item 7])
2025-06-17T13:02:33.600-04:00  WARN 19610 --- [my-spring-config-test-app] [           main] iguration$LoadBalancerCaffeineWarnLogger : Spring Cloud LoadBalancer is currently working with the default cache. While this cache implementation is useful for development and tests, it's recommended to use Caffeine cache in production.You can switch to using Caffeine cache, by adding it and org.springframework.cache.caffeine.CaffeineCacheManager to the classpath.
2025-06-17T13:02:33.651-04:00  INFO 19610 --- [my-spring-config-test-app] [           main] o.s.b.web.embedded.netty.NettyWebServer  : Netty started on port 8080 (http)
2025-06-17T13:02:33.664-04:00  INFO 19610 --- [my-spring-config-test-app] [           main] com.spring.Application                   : Started Application in 1.253 seconds (process running for 1.395)

```

miniKube - 

The second line from the below log shows that the active profile is `dev`, `kubernetes`. However, the `AppConfig(items=[Item 1, Item 6, Item 7, Item 4, Item 5])` shows that even though dev profile is selected, the values from `application.yaml` is present as well. 
```log

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::               (v3.3.12)
2025-06-17T16:14:27.038Z  INFO 1 --- [my-spring-config-test-app] [           main] b.c.PropertySourceBootstrapConfiguration : Located property source: [BootstrapPropertySource {name='bootstrapProperties-configmap.spring-config-map.default'}]
2025-06-17T16:14:27.044Z  INFO 1 --- [my-spring-config-test-app] [           main] com.spring.Application                   : The following 2 profiles are active: "dev", "kubernetes"
2025-06-17T16:14:27.809Z  INFO 1 --- [my-spring-config-test-app] [           main] o.s.cloud.context.scope.GenericScope     : BeanFactory id=c2696e75-ac9f-3490-bf1e-214fc8dc92ae
2025-06-17T16:14:27.835Z  WARN 1 --- [my-spring-config-test-app] [           main] trationDelegate$BeanPostProcessorChecker : Bean 'org.springframework.cloud.client.loadbalancer.LoadBalancerAutoConfiguration$DeferringLoadBalancerInterceptorConfig' of type [org.springframework.cloud.client.loadbalancer.LoadBalancerAutoConfiguration$DeferringLoadBalancerInterceptorConfig] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying). The currently created BeanPostProcessor [lbRestClientPostProcessor] is declared through a non-static factory method on that class; consider declaring it as static instead.
2025-06-17T16:14:27.836Z  WARN 1 --- [my-spring-config-test-app] [           main] trationDelegate$BeanPostProcessorChecker : Bean 'deferringLoadBalancerInterceptor' of type [org.springframework.cloud.client.loadbalancer.DeferringLoadBalancerInterceptor] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying). Is this bean getting eagerly injected/applied to a currently created BeanPostProcessor [lbRestClientPostProcessor]? Check the corresponding BeanPostProcessor declaration and its dependencies/advisors. If this bean does not have to be post-processed, declare it with ROLE_INFRASTRUCTURE.
2025-06-17T16:14:27.837Z  WARN 1 --- [my-spring-config-test-app] [           main] trationDelegate$BeanPostProcessorChecker : Bean 'org.springframework.cloud.client.loadbalancer.reactive.LoadBalancerBeanPostProcessorAutoConfiguration' of type [org.springframework.cloud.client.loadbalancer.reactive.LoadBalancerBeanPostProcessorAutoConfiguration] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying). The currently created BeanPostProcessor [loadBalancerWebClientBuilderBeanPostProcessor] is declared through a non-static factory method on that class; consider declaring it as static instead.
2025-06-17T16:14:27.837Z  WARN 1 --- [my-spring-config-test-app] [           main] trationDelegate$BeanPostProcessorChecker : Bean 'org.springframework.cloud.client.loadbalancer.reactive.LoadBalancerBeanPostProcessorAutoConfiguration$ReactorDeferringLoadBalancerFilterConfig' of type [org.springframework.cloud.client.loadbalancer.reactive.LoadBalancerBeanPostProcessorAutoConfiguration$ReactorDeferringLoadBalancerFilterConfig] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying). Is this bean getting eagerly injected/applied to a currently created BeanPostProcessor [loadBalancerWebClientBuilderBeanPostProcessor]? Check the corresponding BeanPostProcessor declaration and its dependencies/advisors. If this bean does not have to be post-processed, declare it with ROLE_INFRASTRUCTURE.
2025-06-17T16:14:27.838Z  WARN 1 --- [my-spring-config-test-app] [           main] trationDelegate$BeanPostProcessorChecker : Bean 'reactorDeferringLoadBalancerExchangeFilterFunction' of type [org.springframework.cloud.client.loadbalancer.reactive.DeferringLoadBalancerExchangeFilterFunction] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying). Is this bean getting eagerly injected/applied to a currently created BeanPostProcessor [loadBalancerWebClientBuilderBeanPostProcessor]? Check the corresponding BeanPostProcessor declaration and its dependencies/advisors. If this bean does not have to be post-processed, declare it with ROLE_INFRASTRUCTURE.
App Config {}AppConfig(items=[Item 1, Item 6, Item 7, Item 4, Item 5])
2025-06-17T16:14:28.528Z  WARN 1 --- [my-spring-config-test-app] [           main] iguration$LoadBalancerCaffeineWarnLogger : Spring Cloud LoadBalancer is currently working with the default cache. While this cache implementation is useful for development and tests, it's recommended to use Caffeine cache in production.You can switch to using Caffeine cache, by adding it and org.springframework.cache.caffeine.CaffeineCacheManager to the classpath.
2025-06-17T16:14:28.646Z  INFO 1 --- [my-spring-config-test-app] [           main] o.s.b.web.embedded.netty.NettyWebServer  : Netty started on port 8080 (http)
2025-06-17T16:14:28.707Z  INFO 1 --- [my-spring-config-test-app] [           main] com.spring.Application                   : Started Application in 3.428 seconds (process running for 3.971)
```

### What This Repo Demonstrates
This is a minimal, reproducible example that showcases this exact problem. You can clone this repo, run it locally to see the "correct" behavior, then deploy it to a Kubernetes cluster to witness the configuration merging weirdness firsthand. The goal here is to provide a clear, isolated test case that can help identify the root cause of this issue and potentially find a workaround or solution.