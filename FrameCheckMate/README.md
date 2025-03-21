![image](https://github.com/user-attachments/assets/b3a2040e-86bd-4a6c-abe7-26c35c069961)
<br>
# 엔터테인먼트 산업을 위한 B2B 영상 편집 협업 툴

- 영상 내 특정 객체가 나오는 타임스탬프를 제공하여 영상 검토 시간 최소화
- 효율적인 영상 편집 환경과 체계적인 업무 분배 관리 제공

**쉽고 빠른 협업으로 영상 제작 워크플로우를 최적화하세요. :clapper::sparkles:**

<br>

## 기능 소개

### 🔎 방송 심의 객체 탐지

- 사용자가 업로드 한 영상에서 AI를 활용하여 술, 담배, 칼이 포함된 화면 탐지
- 감지된 화면을 (start sec ~ end sec)로 표시

### 📽️ 영상 분할 및 병합

- 탐지된 객체 정보를 기반으로 프레임 단위로 영상 분할
- 각 카드별로 수정된 영상을 최종적으로 하나의 파일로 병합

### ✏️ 칸반보드 기반 카드 상태 관리

- 각 카드 별 작업자 할당 및 영상 업로드/다운로드
- 커멘트 및 컨펌 저장
- 작업 로그 데이터 조회

### 💌 작업 상태 변경 시 알림 & Slack 전송

- 카드 상태 변경 시 (작업 할당, 컨펌 요청, 컨펌 반려, 최종 승인) 상태에 따른 이메일 전송
- 메일 전송 실패 시 개발자 관리 Slack에서 메일 메시지 분석

<br>

## 시스템 아키텍처
![Architecture](https://github.com/user-attachments/assets/02154e42-cfd8-473b-9a11-07eeff0bf7f0)

<br>

## DB ERD
<img src = "https://github.com/user-attachments/assets/1101a234-bf12-48b2-99aa-67b7bfa2b449" width ="900" height = "400">

<br>

## 서비스 소개 영상
[![FrameCheckMate](https://img.youtube.com/vi/-1keWKxLl7A/0.jpg)](https://www.youtube.com/watch?v=-1keWKxLl7A)

<br>

## 동작 화면
![image](https://github.com/user-attachments/assets/84a91ec2-92ba-4d70-959e-c65740600be0)
![image](https://github.com/user-attachments/assets/053e5375-75bf-4a58-912b-831bbc53017a)
![image](https://github.com/user-attachments/assets/7c1dab78-2f49-418e-885a-67e8f1ff1a63)

<br>

## 사용 기술
|Frontend|Backend|Infra/DevOps|
|:---:|:---:|:---:|
|<img src="https://img.shields.io/badge/react-F05138?style=for-the-badge&logo=React&logoColor=white"><br><img src="https://img.shields.io/badge/typescript-F1007E?style=for-the-badge&logo=typescript"><br><img src="https://img.shields.io/badge/nextjs-F1007E?style=for-the-badge"><br><img src="https://img.shields.io/badge/reactquery-2396F3?style=for-the-badge&logo=reactquery&logoColor=white">|<img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=OpenJDK&logoColor=white"><br><img src="https://img.shields.io/badge/kotlin-007396?style=for-the-badge&logo=Kotlin&logoColor=white"><br><img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"><br><img src="https://img.shields.io/badge/springcloud-6DB33F?style=for-the-badge&logo=springcloud&logoColor=white"><br><img src="https://img.shields.io/badge/hibernate-59666C?style=for-the-badge&logo=hibernate&logoColor=white"><br><img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=MySQL&logoColor=white"><br><img src="https://img.shields.io/badge/MongoDB-4479A1?style=for-the-badge&logo=MongoDB&logoColor=white"><br><img src="https://img.shields.io/badge/Kafka-4479A1?style=for-the-badge&logo=Kafka&logoColor=white">|<img src="https://img.shields.io/badge/amazonrds-569A31?style=for-the-badge&logo=amazonrds&logoColor=white"><br><img src="https://img.shields.io/badge/amazonec2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white"><br><img src="https://img.shields.io/badge/amazons3-FF9900?style=for-the-badge&logo=amazones3&logoColor=white"><br><img src="https://img.shields.io/badge/docker-2496ED?style=for-the-badge&logo=docker&logoColor=white"><br><img src="https://img.shields.io/badge/slack-2496ED?style=for-the-badge&logo=slack&logoColor=white"><br><img src="https://img.shields.io/badge/nginx-2496ED?style=for-the-badge&logo=nginx&logoColor=white"><br><img src="https://img.shields.io/badge/jenkins-2496ED?style=for-the-badge&logo=jenkins&logoColor=white">|

<br>

## 특징

### Backend
1. FFmpeg를 활용해 프레임 단위 분할과 병합, H.264 재인코딩으로 영상 처리 시스템 구축
2. 로직 서버와 알림 서버간 통신을 Kafka를 통해 구현 (서버간 결합도를 낮춤)
3. 이메일 전송 실패 시 DLQ 전략 수립 (개발자 확인을 위한 SLACK 전송 구현)
4. Amazon S3를 활용해 영상 데이터의 업로드와 다운로드를 효율적으로 관리

### Frontend
1. 리액트 플레이어를 이용한 원활한 영상 재생 환경을 제공 및 콘텐츠의 효율적인 구성
2. 컴포넌트 분리 및 재사용을 통한 코드의 일관성을 유지 및 프로젝트 확장성과 관리성 향상
3. 스타일드 컴포넌트를 사용하여 각 컴포넌트 스타일의 모듈화, 디자인 효율성 증가

### AI
1. YOLO모델을 관련 Object(담배)에 대해 직접 학습
2. OpenCV로 video를 읽어오고, 학습시킨 YOLO model(best.pt)로 분석
3. video마다 FPS값이 다르기 때문에 초당 1 frame을 분석하도록 설정
4. 탐지된 시간을 구간 형태로 제공하여, 사용자의 업무 분담 효율성 증가

<br>

## 멤버 소개
|진주원(팀장)|김수빈|주연수|김영표|김태경|이재희|
|:----:|:----:|:----:|:----:|:----:|:----:|
|Backend/Infra|Backend/Infra|Backend|Backend/Infra|FrontEnd/AI|FrontEnd|
|[@jinjoo-lab](https://github.com/jinjoo-lab)|[@ksb3458](https://github.com/ksb3458)|[@jooys130](https://github.com/jooys130)|[@menstoo121](https://github.com/menstoo121)|[@blackburi](https://github.com/blackburi)|[@hee0109](https://github.com/hee0109)|
 | <img src = "https://avatars.githubusercontent.com/u/84346055?v=4" width ="120" height = "150"> | <img src = "https://github.com/shin5774/SSAFY_CS_Study/blob/main/image/SIUU.jpeg?raw=true" width ="120" height = "150">| <img src = "https://github.com/user-attachments/assets/cd5aa20d-66c3-4fdf-9b88-7127864c4f4d" width ="120" height = "150">| <img src = "https://github.com/user-attachments/assets/1b495940-e134-4079-8777-d5a3c2df9ae1" width ="120" height = "150">| <img src = "https://avatars.githubusercontent.com/u/156290298?v=4" width ="120" height = "150">| <img src = "https://github.com/user-attachments/assets/e27ce71f-e517-446f-aa5d-31542268754c" width ="120" height = "150">|
