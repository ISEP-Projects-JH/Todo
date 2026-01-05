FROM jenkins/jenkins:lts

USER root

RUN apt-get update && \
    apt-get install -y \
      curl \
      maven \
      ca-certificates \
      gnupg && \
    curl -fsSL https://deb.nodesource.com/setup_18.x | bash - && \
    apt-get install -y nodejs

USER jenkins
