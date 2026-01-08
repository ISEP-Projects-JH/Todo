FROM jenkins/jenkins:lts

USER root

RUN apt-get update && \
    apt-get install -y \
      curl \
      maven \
      ca-certificates \
      gnupg

RUN curl -fsSL https://deb.nodesource.com/setup_18.x | bash - && \
    apt-get install -y nodejs

RUN apt-get update && \
    apt-get install -y \
      python3 \
      python3-pip \
      python3-venv \
      wget \
      unzip \
      fonts-liberation \
      libnss3 \
      libxss1 \
      libasound2 \
      libatk-bridge2.0-0 \
      libgtk-3-0 \
      libgbm1 \
      libxshmfence1 \
      libdrm2 \
    && rm -rf /var/lib/apt/lists/*

RUN wget -q -O - https://dl.google.com/linux/linux_signing_key.pub \
      | gpg --dearmor > /usr/share/keyrings/chrome.gpg && \
    echo "deb [arch=amd64 signed-by=/usr/share/keyrings/chrome.gpg] \
      http://dl.google.com/linux/chrome/deb/ stable main" \
      > /etc/apt/sources.list.d/google-chrome.list && \
    apt-get update && \
    apt-get install -y google-chrome-stable && \
    rm -rf /var/lib/apt/lists/*

RUN python3 -m venv /opt/venv

RUN /opt/venv/bin/pip install --no-cache-dir \
    requests \
    selenium \
    webdriver-manager

RUN chown -R jenkins:jenkins /opt/venv

ENV PATH="/opt/venv/bin:$PATH"

RUN jenkins-plugin-cli --plugins \
    workflow-job \
    workflow-cps \
    workflow-scm-step \
    workflow-durable-task-step \
    pipeline-model-definition \
    pipeline-stage-step \
    git

RUN mkdir -p /base && \
    chown -R jenkins:jenkins /base

USER jenkins
