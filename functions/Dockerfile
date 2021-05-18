ARG JAVA_VERSION=11
FROM mcr.microsoft.com/azure-functions/java:3.0-java$JAVA_VERSION

COPY /build/azure-functions /functions
WORKDIR /functions

RUN apt-get update && \
    apt-get install -y curl && \
    apt-get clean && \
    mkdir -p /home/site/wwwroot && \
    cd $(ls -d */|head -n 1) && \
    cp -a . /home/site/wwwroot

ENV AzureWebJobsScriptRoot=/home/site/wwwroot \
    AzureFunctionsJobHost__Logging__Console__IsEnabled=true