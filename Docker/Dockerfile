FROM openjdk:8-jre-alpine
RUN apk --no-cache upgrade \
  && apk --no-cache add \
    groff \
    python3 \
    bash \
  && apk --no-cache add --virtual build-dep \
      python3-dev \
      py3-pip \
  && pip3 install awscli \
  && pip3 install boto3 \
  && apk del build-dep \
  && rm -rf ~/.cache/pip

ENV JAVA_HOME=/usr/lib/jvm/java-1.8-openjdk \
    USER=appuser
ENV HOME=/home/$USER
RUN adduser -S $USER

## Copy configuration overrides and run script
## Copy application itself
## PREREQUISITE:
##    Docker cannot access contents outside, so this must be run when building:
##    $ cp -p "../target/$(ls -t ../target/*.jar | grep -v /orig | head -1)" app.jar
COPY GetPropertiesFromParameterStore.py runapp.sh app.jar $HOME/

## Create directory for holding application logs and configure permissions
## Configure permissions
RUN chmod 755 $HOME/*.sh && \
    chown -R $USER $HOME

## Expose application port
EXPOSE 8080

USER $USER
WORKDIR /home/$USER
CMD ["./runapp.sh"]
