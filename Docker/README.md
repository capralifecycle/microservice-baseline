# Docker as deployment unit

This directory contains a Docker-setup for java applications packaged as
an uber-jar. The `Dockerfile` expects app.jar to be present in the current
working directory at build time.

It is based on Alpine linux with openjdk 8 and uses the python
aws sdk to download properties from the AWS Parameter Store.
You may set java parameters through the environment variable
`JAVA_PARAMS_OVERRIDE`.

The flags `-XX:+UnlockExperimentalVMOptions` and
`-XX:+UseCGroupMemoryLimitForHeap` have been set by default.

## AWS SSM Parameter Store

The setup contains integration with [AWS SSM Parameter Store](
https://docs.aws.amazon.com/systems-manager/latest/userguide/systems-manager-paramstore.html).
To download the properties associated with a ParamStore path,
the following environment variables must be provided to the container:

* `AWS_PARAMETER_STORE_ENABLED` set to `true`
* `AWS_PARAMETER_STORE_OUTPUTPATH` set to where the properties should be put.
  * e.g. `application.properties`
* `AWS_PARAMETER_STORE_PATH`
  * e.g. `/dev/myapp/`

With the examples above every parameter with the path `/dev/myapp/`
would be downloaded and put into `applications.properties`.
The script also trims away the ParamStore path before putting it in the
properties file. `/dev/myapp/db.username` with the value `12345` would
 result in the following line in `applications.properties`

`db.username=1234`

The container must have the appropriate IAM permissions to get the parameters.
Example IAM policy as it would be with CloudFormation for en ECS container:

```yaml
MyAppTaskRole:
  Type: AWS::IAM::Role
  Properties:
    AssumeRolePolicyDocument:
      Version: '2012-10-17'
      Statement: # Allow ecs-tasks to assume this role
        - Effect: 'Allow'
          Principal:
            Service: 'ecs-tasks.amazonaws.com'
          Action: 'sts:AssumeRole'
    Path: '/'
    Policies:
      - PolicyName: 'ParamStore'
        PolicyDocument:
          Version: '2012-10-17'
          Statement:
            - Effect: 'Allow'
              Action: 'ssm:GetParameters*'
              Resource: !Sub 'arn:aws:ssm:${AWS::Region}:${AWS::AccountId}:parameter/dev/myapp/*'
```

The example above may be attached to an ECS Task Definition,
allowing the container to resolve `/dev/myapp/`, without having access to any
other applications parameter.

Alternatives to downloading parameters, and putting them in a file,
include resolving properties from parameter store as part of the application
logic.

The technique described above is a nice way to provision properties
to applications that was written without the intent of using Parameter Store.

## Logging with ECS

The application logs to STDOUT by default. This allows us to the use the
[CloudWatch Logs logging driver](
https://docs.aws.amazon.com/AmazonECS/latest/developerguide/using_awslogs.html).

Example configuration for ECS with CloudFormation:

```yaml
LogConfiguration:
  LogDriver: 'awslogs'
  Options:
    awslogs-group: !Ref LogGroup
    awslogs-region: !Ref 'AWS::Region'
    awslogs-stream-prefix: !Sub '${ServiceName}'
    awslogs-datetime-format: '%Y-%m-%dT%H:%M:%S.%L' # 2018-09-23T14:39:54.042
```
