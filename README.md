# GitHub Microservice

This is a sample Java + Spring Boot microservice that interacts with the GitHub API in order to retrieve Repositories + Branches information.

## Configuration

- ```github.baseUrl``` -> the GitHub API base url (default https://api.github.com)
- ```github.personalAccessToken``` -> your GitHub Personal Access Token; if empty, the GitHub's API rate limit will be lower
- ```openapi.generated``` -> if true, then the RepositoryBranchesDelegatedApi will make use of the generated OpenApi entities; if false, rename BranchEntity.java.bak & RepositoryBranchesEntity.java.bak, and comment out the openapi-generator-maven-plugin section in pom.xml (default true)  

## Build Java sources

Run ```mvn clean install``` to build the project, and generate the OpenApi sources based on swagger-openapi3.yml specification template.

## Build Docker image

```docker build . -t github-microservice:latest```

## Run Docker image

```docker run -d --name github-microservice -e github.personalAccessToken=<PersonalAccessToken> -p 8080:8080 --rm github-microservice:latest```

## AWS ECS Fargate CloudFormation template

see ```aws/aws-ecs-fargate-cloudformation.yml```

Prerequisites:
- AWS account
- ECR repository: ```aws ecr create-repository --repository-name github-microservice```
- Docker image pushed to the ECR repository:
  - ```aws ecr get-login-password --region eu-west-1```
  - ```aws ecr --region eu-west-1 | docker login -u AWS -p <AuthToken> <ECRRepositoryUrl>/github-microservice```
  - ```docker tag github-microservice:latest <ECRRepositoryUrl>/github-microservice:latest```
  - ```docker push <ECRRepositoryUrl>/github-microservice:latest```

## Swagger UI

```http://localhost:8080/swagger-ui/index.html```

## Actuator health endpoint

```http://localhost:8080/actuator/health```

## [Extra] CLI commands for accessing microservices' API

```curl -v 'http://localhost:8080/api/v1/stats/repository-branches?username=cosminnicula' --header 'Accept: application/json'```
```curl -v 'http://localhost:8080/api/v1/stats/repository-branches?username=a7a7a7a7a7a7a' --header 'Accept: application/json'```
```curl -v 'http://localhost:8080/api/v1/stats/repository-branches?username=cosminnicula' --header 'Accept: application/xml'```

## [Extra] CLI commands for accessing GitHub's API

```curl -v https://api.github.com/users/cosminnicula/repos```
```curl -v https://api.github.com/repos/cosminnicula/algofun/branches```
```curl -v -H "Authorization: token <PersonalAccessToken>" -s "https://api.github.com/users/cosminnicula/repos"```
