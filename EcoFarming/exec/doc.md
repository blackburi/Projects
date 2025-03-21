### 1. [사용 도구]

---

- 이슈 관리 : Jira
- 형상 관리 : GitLab
- 커뮤니케이션 : Notion, MatterMost
- CI/CD : Jenkins, Docker

### 2. [개발 도구]

---

- Visual Studio Code : 1.76.0
- Intellij : 2022.3.2 (Ultimate Edition)

### 3. [개발 환경]

---

### Frontend

### Backend

| Java | openjdk version 17.0.2 |
| --- | --- |
| Spring Boot | 3.3.3 |
| Python | 3.9.18 |
| MySQL | 9.0.1 |
| JWT (JSON Web Token)  | 0.12.3 |
| Redis | 7.4.0 |
| Spring Batch | 5.1.2 |

### Server

| AWS EC2 | CPU : Intel(R) Xeon(R) CPU E5-2686 v4 @ 2.30GHz |
| --- | --- |

RAM : 15GB

OS : Ubuntu |

### Service

### 4. [환경변수 형태]

---

- Backend
    - application.yml
    
    ```java
    spring:
      application:
        name: ecofarming
    
      profiles:
        active: prod
    
      datasource:
        url: jdbc:mysql://j11a101.p.ssafy.io:3306/ecofarming
        username: ${DB_USERNAME}
        password: ${DB_PASSWORD}
    
      jpa:
        hibernate:
          ddl-auto: update
        properties:
          hibernate:
            dialect: org.hibernate.dialect.MySQLDialect
    
      servlet:
        multipart:
          max-file-size: 10MB
          max-request-size: 10MB
    
      jwt:
        secret: ${JWT_SECRET}
    
      redis:
        host: ${REDIS_HOST}
        port: 6379
        password: ${REDIS_PASSWORD}
        timeout: 6000ms
    
    firebase:
      config: file:${FIREBASE_CONFIG_PATH}
    
    webhook:
      mattermost:
        report:
          url: ${MM_REPORT_URL}
          priority: standard
        error:
          url: ${MM_ERROR_URL}
          priority: important
    
    server:
      servlet:
        context-path: /api
    
    management:
      endpoints:
        web:
          exposure:
            include: health, info, metrics
        health:
          show-details: always
    
    file:
      upload-dir: /home/ubuntu/uploads
    ```
    
    - src/main/resources/firebase-config.json (FCM key)

- frontend
    - .env
    
    ```jsx
    REACT_APP_API_URL=https://j11a101.p.ssafy.io/api
    
    # FCM KEY
    REACT_APP_FIREBASE_API_KEY=``
    REACT_APP_FIREBASE_AUTH_DOMAIN=``
    REACT_APP_FIREBASE_PROJECT_ID=``
    REACT_APP_FIREBASE_STORAGE_BUCKET=``
    REACT_APP_FIREBASE_MESSAGING_SENDER_ID=``
    REACT_APP_FIREBASE_APP_ID=``
    REACT_APP_MEASUREMENT_ID=``
    REACT_APP_VAPID_KEY=``
    ```
    

### 5. [CI/CD 구축]

---

- EC2
- Docker
    - 포트번호
    
    ![image.png](./image.png)
    
- Jenkins
    
    ```java
    pipeline {
        agent any
        environment {
            DOCKER_HUB_CREDENTIALS_ID = 'dockerhub-access-chanmin'
            GITLAB_CREDENTIALS_ID = 'gitlab-access-chanmin2'
            BACKEND_DOCKER_REPO = 'chanmin314/ecofarmingback'
            DOCKERHUB_FRONTEND_REPO = 'chanmin314/ecofarmingfront'
            USER_SERVER_IP = 'j11a101.p.ssafy.io'
            SPRING_PROFILE = 'prod'
            BLUE_PORT = '8085'
            GREEN_PORT = '8086'
            PORT_FILE = "/home/ubuntu/current_port.txt"  // EC2 서버에 저장할 포트 상태 파일
        }
    
        stages {
    
            // SECRET 로드
            stage('Load SECRET') {
                steps {
                    withCredentials([
                        string(credentialsId: 'jwt-secret-id', variable: 'JWT_SECRET'),
                        string(credentialsId: 'REDIS_PASSWORD', variable: 'REDIS_PASSWORD'),
                        string(credentialsId: 'DB_PASSWORD', variable: 'DB_PASSWORD')                  
                    ]) {
                        script {
                            // JWT_SECRET을 환경 변수로 설정
                            env.JWT_SECRET = "${JWT_SECRET}"
                            echo "JWT_SECRET Loaded Successfully"
    
                            env.REDIS_PASSWORD = "${REDIS_PASSWORD}"
                            echo "REDIS_PASSWORD Loaded Successfully"
    
                            env.DB_PASSWORD = "${DB_PASSWORD}"
                            echo "DB_PASSWORD Loaded Successfully"
                        }
                    }
                }
            }
    
            // WEB HOOK URL 로드
            stage('Load WEB HOOK') {
                steps {
                    withCredentials([
                        string(credentialsId: 'MM_REPORT_URL', variable: 'MM_REPORT_URL'),
                        string(credentialsId: 'MM_ERROR_URL', variable: 'MM_ERROR_URL'),
                        string(credentialsId: 'FIREBASE_CONFIG_PATH', variable: 'FIREBASE_CONFIG_PATH')
                    ]) {
                        script {
                            env.MM_REPORT_URL = "${MM_REPORT_URL}"
                            echo "MM_REPORT_URL Loaded Successfully"
                            env.MM_ERROR_URL =  "${MM_ERROR_URL}"
                            echo "MM_ERROR_URL Loaded Successfully"
                            env.FIREBASE_CONFIG_PATH="${FIREBASE_CONFIG_PATH}"
                            echo "FIREBASE_CONFIG_PATH Loaded Successfully";
                        }
    
                    }
                }
            }
    
            // 현재 활성화된 포트 읽기
            stage('Read Current Active Port') {
                steps {
                    sshagent(['ssafy-ec2-ssh']) {
                        script {
                            def currentPort = sh(script: "ssh -o StrictHostKeyChecking=no ubuntu@${USER_SERVER_IP} 'cat ${PORT_FILE} || echo ${BLUE_PORT}'", returnStdout: true).trim()
                            env.CURRENT_ACTIVE_PORT = currentPort
                            echo "Current Active Port is: ${currentPort}"
                        }
                    }
                }
            }
    
            // GitLab에서 소스 코드 클론
            stage('Clone Repository') {
                steps {
                    script {
                        def newPort = (env.CURRENT_ACTIVE_PORT == BLUE_PORT) ? GREEN_PORT : BLUE_PORT
                        def environmentName = (newPort == BLUE_PORT) ? "Blue" : "Green"
                        echo "Start Cloning Repository for ${environmentName} Environment..."
                        git credentialsId: "${GITLAB_CREDENTIALS_ID}", branch: 'master', url: 'https://lab.ssafy.com/s11-ai-image-sub1/S11P21A101.git'
                        echo "Repository Cloning Complete for ${environmentName} Environment!"
                    }
                }
            }
    
            // 백엔드 빌드 및 Docker 이미지 생성
            stage('Build Backend and Docker Image') {
                steps {
                    script {
                        def newPort = (env.CURRENT_ACTIVE_PORT == BLUE_PORT) ? GREEN_PORT : BLUE_PORT
                        def environmentName = (newPort == BLUE_PORT) ? "Blue" : "Green"
                        dir('backend') {
                            echo "Start Building Backend for ${environmentName} Environment..."
                            sh 'chmod +x ./gradlew'
                            sh './gradlew clean build -x test -Pprofile=prod'
                            echo "Backend Build Complete for ${environmentName} Environment!"
    
                            echo "Start Building Docker Image for ${environmentName} Environment..."
                            def app = docker.build("${BACKEND_DOCKER_REPO}:latest")
                            echo "Docker Image Build Complete for ${environmentName} Environment!"
                        }
                    }
                }
            }
    
              stage('Push Backend to Docker Hub') {
                steps {
                    script {
                        docker.withRegistry('', DOCKER_HUB_CREDENTIALS_ID) {
                            docker.image("${BACKEND_DOCKER_REPO}:latest").push()
                        }
                    }
                }
            }
    
            // 새로운 버전 배포
    stage('Deploy to New Environment') {
        steps {
            sshagent(['ssafy-ec2-ssh']) {
                withCredentials([usernamePassword(credentialsId: "${DOCKER_HUB_CREDENTIALS_ID}", usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                    script {
                        def newPort = (env.CURRENT_ACTIVE_PORT == BLUE_PORT) ? GREEN_PORT : BLUE_PORT
                        def environmentName = (newPort == BLUE_PORT) ? "Blue" : "Green"
                        echo "Deploying to ${environmentName} Environment (Port: ${newPort})..."
    
                        sh """
                        ssh -o StrictHostKeyChecking=no ubuntu@${USER_SERVER_IP} \\
                            'docker image prune -f && \\
                            echo ${DOCKER_PASSWORD} | docker login -u ${DOCKER_USERNAME} --password-stdin && \\
                            docker pull ${BACKEND_DOCKER_REPO}:latest && \\
                            docker stop backend_${newPort} || true && \\
                            docker rm backend_${newPort} || true && \\
                            docker run -d --name backend_${newPort} -p ${newPort}:8080 \\
                            -e JWT_SECRET=${JWT_SECRET} \\
                            -e MM_REPORT_URL=${MM_REPORT_URL} \\
                            -e MM_ERROR_URL=${MM_ERROR_URL} \\
                            -e FIREBASE_CONFIG_PATH=${FIREBASE_CONFIG_PATH} \\
                            -e REDIS_PASSWORD=${REDIS_PASSWORD} \\
                            -e DB_PASSWORD=${DB_PASSWORD} \\
                            -v /home/ubuntu/uploads:/home/ubuntu/uploads ${BACKEND_DOCKER_REPO}:latest \\
                            --spring.profiles.active=${SPRING_PROFILE} --file.upload-dir=/home/ubuntu/uploads && \\
                            docker logout'
                        """
                        echo "Deployment to ${environmentName} Environment Complete!"
                    }
                }
            }
        }
    }
    
            // Nginx 설정 변경 및 트래픽 전환
            stage('Switch Traffic to New Environment') {
                steps {
                    script {
                        def newPort = (env.CURRENT_ACTIVE_PORT == BLUE_PORT) ? GREEN_PORT : BLUE_PORT
                        def environmentName = (newPort == BLUE_PORT) ? "Blue" : "Green"
                        echo "Switching Traffic to ${environmentName} Environment (Port: ${newPort})..."
                        sshagent(['ssafy-ec2-ssh']) {
                            sh """
                                ssh -o StrictHostKeyChecking=no ubuntu@${USER_SERVER_IP} \\
                                "sudo sed -i 's/localhost:808[5|6]/localhost:${newPort}/g' /etc/nginx/sites-enabled/j11a101.p.ssafy.io && sudo nginx -s reload && echo ${newPort} > ${PORT_FILE}"
                            """
                            env.CURRENT_ACTIVE_PORT = newPort
                        }
                        echo "Traffic Successfully Switched to ${environmentName} Environment!"
                    }
                }
            }
            // Frontend 빌드, 이미지 생성 및 배포
            stage('Build Frontend') {
                steps {
                    script {
                        dir('frontend') {
                            echo "Start Build Frontend,,,"
                            sh 'npm install'
                            sh 'CI=false npm run build'
                            echo "Build Frontend Complete!!!"
                        }
                    }
                }
            }
            stage('Build Frontend Docker Image') {
                steps {
                    script {
                        dir('frontend') {
                            def app = docker.build("${DOCKERHUB_FRONTEND_REPO}:latest")
                        }
                    }
                }
            }
            stage('Push Frontend to Docker Hub') {
                steps {
                    script {
                        docker.withRegistry('', DOCKER_HUB_CREDENTIALS_ID) {
                            docker.image("${DOCKERHUB_FRONTEND_REPO}:latest").push()
                        }
                    }
                }
            }
            stage('Deploy Frontend') {
                steps {
                    sshagent(['ssafy-ec2-ssh']) {
                        withCredentials([usernamePassword(credentialsId: "${DOCKER_HUB_CREDENTIALS_ID}", usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                            withCredentials([file(credentialsId: 'front-env', variable: 'FRONT_ENV_FILE')]) {
                                sh """
                                ssh -o StrictHostKeyChecking=no ubuntu@${USER_SERVER_IP} << EOF
                                echo ${DOCKER_PASSWORD} | docker login -u ${DOCKER_USERNAME} --password-stdin
                                docker pull ${DOCKERHUB_FRONTEND_REPO}:latest
                                docker stop frontend || true
                                docker rm frontend || true
                                docker run -d --name frontend -p 3000:80 \\
                                --env-file ${FRONT_ENV_FILE} \\
                                ${DOCKERHUB_FRONTEND_REPO}:latest
                                docker logout
    EOF
                                """
                            }
                        }
                    }
                }
            }
        }
    }
    
    ```
    

### 6. [빌드 및 실행]

---

### Local

- 소스 클론 및 frontend 실행
    
    ```jsx
    git clone ``
    cd S11P21A101
    cd frontend
    npm i && npm start
    ```
    
- Backend
- 환경변수 입력
    
    ```bash
    JWT_SECRET='';
    DB_PASSWORD=''
    REDIS_HOST=[J11A101.p.ssafy.io](http://j11a101.p.ssafy.io/);
    REDIS_PASSWORD='';
    
    MM_REPORT_URL=https://meeting.ssafy.com/hooks/'[mattermost webhook url]';
    MM_ERROR_URL=https://meeting.ssafy.com/hooks/'[mattermost webhook url]';
    
    FIREBASE_CONFIG_PATH='[실행환경의 firebase-config.json의 파일 경로]'
    ```
    
- EcofarmingApplication 실행
    
    ```bash
    ls # S11P2A101/backend
    # IDE로 실행
    ```
    

### Deploy

```bash
docker pull hanmin314/ecofarmingback:latest
docker run -d --name backend_${newPort} -p ${newPort}:8080 \\
                        -e JWT_SECRET=${JWT_SECRET} \\
                        -e MM_REPORT_URL=${MM_REPORT_URL} \\
                        -e MM_ERROR_URL=${MM_ERROR_URL} \\
                        -e FIREBASE_CONFIG_PATH=${FIREBASE_CONFIG_PATH} \\
                        -e REDIS_PASSWORD=${REDIS_PASSWORD} \\
                        -e DB_PASSWORD=${DB_PASSWORD} \\
                        -v /home/ubuntu/uploads:/home/ubuntu/uploads ${BACKEND_DOCKER_REPO}:latest \\
                        --spring.profiles.active=${SPRING_PROFILE}

docker pull chanmin314/ecofarmingfront:latest
docker stop frontend || true
docker rm frontend || true
docker run -d --name frontend -p 3000:80 \\
--env-file ${FRONT_ENV_FILE} \\ #프론트엔드의 환경 변수 파일 (.env)
chanmin314/ecofarmingfront:latest
docker logout
```

- **배포환경 주소**
    - https://j11a101.p.ssafy.io/
