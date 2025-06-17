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

### What This Repo Demonstrates
This is a minimal, reproducible example that showcases this exact problem. You can clone this repo, run it locally to see the "correct" behavior, then deploy it to a Kubernetes cluster to witness the configuration merging weirdness firsthand.

### Steps to Reproduce the issue

1. Build the App 
```shell
  mvn clean install
```
2. Run the app locally to verify (I have Dev and Staging profiles, substitute accordingly, or you can remove profiles to see the default behaviour)
```shell
  mvn spring-boot:run -Dspring-boot.run.profiles="dev" 
```  
3. When the application starts up, you can see in the logs `AppConfig(items=[Item 1, Item 6, Item 7])` (Incase you have loaded Dev) the right number of elements are present inside the array.

4. Deploy the application in `minikube`.
```shell
  k apply -f minikube.yaml
```
PS: My miniKube's overly permissive `cluster-admin` `ClusterRoleBinding` was bound to the `system:serviceaccounts` group. This resulted in all service accounts in my default cluster having `cluster-admin` privileges. Since it's just a miniKube I ended up doing this 
```shell
  k create clusterrolebinding serviceaccounts-cluster-admin --clusterrole=cluster-admin --group=system:serviceaccounts
```
> [!WARNING]
> Do not do this in your actual cluster.

The goal here is to provide a clear, isolated test case that can help identify the root cause of this issue and potentially find a workaround or solution. Whether this is a Spring Boot bug, a Kubernetes-specific behavior, or something related to how configuration files are loaded in containerized environments, this repository should help get to the bottom of it.