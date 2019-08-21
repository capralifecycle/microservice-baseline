FROM adoptopenjdk/openjdk12:jre-12.0.2_10-alpine
RUN apk --no-cache upgrade \
  && apk --no-cache add \
    groff \
    python2 \
    bash \
    su-exec \
  && apk --no-cache add --virtual build-dep \
      python2-dev \
      py2-pip \
  && pip install awscli \
  && pip install boto3 \
  && apk del build-dep \
  && rm -rf ~/.cache/pip

ENV JAVA_HOME=/opt/java/openjdk \
    USER=appuser
ENV HOME=/home/$USER
RUN adduser -S $USER

## Copy configuration overrides and run script
## Run the docker build commad from root directory of the project like this
## `docker build -t microservice -f Docker/Dockerfile .`
COPY Docker/GetPropertiesFromParameterStore.py Docker/runapp.sh $HOME/
COPY target/lib $HOME/lib

## Create directory for holding application logs and configure permissions
## Configure permissions
RUN chmod 755 $HOME/*.sh && \
    chown -R $USER $HOME

## Expose application port
EXPOSE 8080

WORKDIR /home/$USER
CMD ["./runapp.sh"]
