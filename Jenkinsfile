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
        '''
      }
    }

    stage('Start Backend') {
      steps {
        sh '''
          cd backend
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
  }

  post {
    always {
      sh '''
        echo "Stopping services..."
        kill $(cat backend/backend.pid) || true
        kill $(cat frontend/frontend.pid) || true
      '''
    }
  }
}
