pipeline {
  agent any

  stages {
  
    stage('Check Workspace') {
      steps {
        sh '''
          echo "WORKSPACE CONTENT:"
          pwd
          ls -la
        '''
      }
    }

    stage('Check Tools') {
      steps {
        sh '''
          java -version
          mvn -version
          node -v
          npm -v
          python3 --version
          google-chrome --version
        '''
      }
    }

    stage('Start Backend') {
      steps {
        sh '''
          nohup mvn spring-boot:run > backend.log 2>&1 &
          echo $! > backend.pid
        '''
      }
    }

    stage('Start Frontend') {
      steps {
        sh '''
          cd frontend
          npm install
          nohup npm run dev > frontend.log 2>&1 &
          echo $! > frontend.pid
        '''
      }
    }

    stage('Smoke Test') {
      steps {
        sh '''
          echo "Waiting for backend..."
          for i in {1..30}; do
            curl -sf http://localhost:8080 && break
            sleep 2
          done

          echo "Waiting for frontend..."
          for i in {1..30}; do
            curl -sf http://localhost:3000 && break
            sleep 2
          done
        '''
      }
    }

    stage('Init submodules') {
      steps {
        sh '''
          git submodule update --init --recursive
        '''
      }
    }

    stage('Install vendored jh_utils') {
      steps {
        sh '''
          echo "Installing vendored jh_utils..."

          cd vendor/jh_utils

          python3 --version
          pip3 --version

          pip3 install -e .

          echo "jh_utils installed (editable)"
        '''
      }
    }

    stage('Run Auto Test') {
      steps {
        sh '''
          python3 -m scripts.auto_test | tee run.log

          echo "=== auto_test output ==="
          cat run.log

          cp output.md /base/output.md

          if grep -q "^success$" run.log; then
            echo "Auto test success"
          else
            echo "Auto test failed"
            exit 1
          fi
        '''
      }
    }
  }

  post {
    always {
      sh '''
        echo "Stopping services..."

        if [ -f backend/backend.pid ]; then
          kill $(cat backend/backend.pid) || true
        fi

        if [ -f frontend/frontend.pid ]; then
          kill $(cat frontend/frontend.pid) || true
        fi

        echo "=== Jenkins finished ==="
        ls -la /base/output.md || true
      '''
    }
  }
}
