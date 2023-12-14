# 2023_senier_project
2023 유한대 3-1반 졸업 프로젝트 2조_ '양양포장마차'

## 개발 기간
- 전체 개발 기간: 9월 ~ 11월(약 3개월)
- 주어진 시간이 많아도, 불 떨어져야 개발하게 되어있음. 달려라!!

## 멤버 구성
### 팀장: 양나은[naeunYang](https://github.com/naeunYang)
### 팀원
- 홍서빈[girin17](https://github.com/girin17)
- 조성은[Hwa-A](https://github.com/Hwa-A)
- 이승현[Klaxon9718](https://github.com/Klaxon9718)
- 신영완[shinya](https://github.com/shinyagitst)
- 유기상[kisangs](https://github.com/kisangs) => 팀장에서 팀원으로 직책 변경.<br>
    - 변경 사유: 개발 중 업무 포기로 인한 개발 일정에 차질<br>
　　　　　/ 개인에게 직접적으로 부여된 업무만을 중시 / 공동체 의식 부족
    
## 업무 분할
  - 메인 페이지: 양나은
  - 제보 페이지: 홍서빈
  - 상세 페이지<br>
       - 가게 상세 정보 및 수정: 홍서빈<br>
       -  가게 리뷰: 조성은<br>
       - 가게 번개: 이승현<br>
　　- 가게 번개 등록: 조성은
  - 마이페이지: 이승현, 양나은<br>
     - 리뷰 수정: 조성은
  - 로그인: 유기상
  - 전체 UI: 홍서빈
  - 전체 파일 병합: 이승현
  - 테스터: 신영완

## 작품 구조도
![noname01](https://github.com/Hwa-A/YangYangPojangMacha_2023/assets/100755682/a479ef21-f8f0-405a-b3bf-55c3051ef51a)

## DB구조
### 관계형 데이터베이스(RDB)
![DB](https://github.com/Hwa-A/YangYangPojangMacha_2023/assets/100755682/3f7a8c21-883a-4705-b6d2-a2474c30267b)
### 파이어베이스(Firebase)
**1. 가게**<br>
![가게_파이어베이스](https://github.com/Hwa-A/YangYangPojangMacha_2023/assets/100755682/4e0a363d-a81e-46a8-a836-3f58c7b46e28)

**2. 리뷰**<br>
![리뷰_파이어베이스](https://github.com/Hwa-A/YangYangPojangMacha_2023/assets/100755682/c7f5a654-4d1a-4047-bd80-d772313ecb0f)

**3. 번개**<br>
![번개_파이어베이스](https://github.com/Hwa-A/YangYangPojangMacha_2023/assets/100755682/f1687fee-98c2-40e5-b443-620708a09b95)

## 기능 및 상세화면
### 로그인 및 회원가입
**진행: 스플래시 로딩화면 > 구글 로그인 > 권한 요청 > 상세정보 입력**<br>
① 스플래시 로딩화면<br>
![스플래시](https://github.com/Hwa-A/YangYangPojangMacha_2023/assets/100755682/e18b0e06-87b6-4568-9fe3-1b920619a42a)<br>
② 구글 로그인<br>
![구글로그인](https://github.com/Hwa-A/YangYangPojangMacha_2023/assets/100755682/4dfc7dcf-0e8a-4fdd-b4d2-7124af0e4ff3)<br>
③ 권한 요청<br>
![권한요청](https://github.com/Hwa-A/YangYangPojangMacha_2023/assets/100755682/73c846a3-64e6-4aee-9685-594969e1d359)<br>
④ 상세정보 입력<br>
![사용자 정보 입력](https://github.com/Hwa-A/YangYangPojangMacha_2023/assets/100755682/26ef2349-670d-493c-8e15-3ae5a9063246)
### 메인화면
**어플 소개 화면**<br>
![메인_어플설명](https://github.com/Hwa-A/YangYangPojangMacha_2023/assets/100755682/5753a18c-e8ea-4f61-b555-ad5b981d7d67)<br>
**메인화면**<br>
![메인_지도화면](https://github.com/Hwa-A/YangYangPojangMacha_2023/assets/100755682/ccb4d9bc-1069-447d-a308-5beabd83745e)<br>
　　　1. 검색기능<br>
　　　2. 카테고리별 필터링<br>
　　　3. 인증, 번개 필터링<br>
　　　　　　① 인증<br>
　　　　　　　　　- O: 초록색<br>
　　　　　　　　　- X: 빨간색<br>
　　　　　　② 번개<br>
　　　　　　　　　- O: 번개마크<br>
　　　　　　　　　- X: 번개마크 X
### 주소 검색
　　　1. 검색 입력 전, 이전 검색 목록 출력<br>
　　　![주소검색_기본주소검색](https://github.com/Hwa-A/YangYangPojangMacha_2023/assets/100755682/16398faa-300d-4e83-b6d6-8e7b3c506ce9)<br>
　　　2. 키워드에 따른 검색 목록 출력<br>
　　　![주소검색_키워드 검색](https://github.com/Hwa-A/YangYangPojangMacha_2023/assets/100755682/b90f6157-d23c-49ec-9c74-cf5b7fe6c14c)<br>
　　　　　　- 용자가 한 글자씩 키워드를 입력할 때마다 웹 서버로 http요청을 보낸 후, 해당 키워드와 일치하는 데이터를 30개 정도를 받음<br>
　　　　　　- 현재 사용자의 위치를 기준으로 동일한 지역에 속하는 주소를 우선적으로 표시하고, 추가적인 필터링 과정을 통해 관련 데이터를 상위에 정렬하여 리스트 뷰에 나타냄<br>
　　　　　　- 지명, 도로명 주소, 지번 주소, 좌표값 등을 데이터로 받고, 해당 좌표값을 기준으로 마커를 표시
### 가게 제보
　　　1. 가게 제보<br>
　　　![가게제보_입력](https://github.com/Hwa-A/YangYangPojangMacha_2023/assets/100755682/000fceda-ecc4-4f95-bd00-38f65a83f265)<br>
　　　　　　① 주소 검색 시, 주변의 이미 등록된 가게 위치 표시<br>
　　　　　　![가게제보_가게주소설정](https://github.com/Hwa-A/YangYangPojangMacha_2023/assets/100755682/8719e1ca-b189-419a-8a07-d2f7bdaac9ab)
### 가게 상세
　　　1. 가게 상세 정보<br>
　　　　　　① 좋아요 기능<br>
　　　　　　② 신고, 좋아요 기능<br>
      ![가게상세_처음사진](https://github.com/Hwa-A/YangYangPojangMacha_2023/assets/100755682/4996cb18-f56a-42d4-8b6d-8fafc1fa3f4e)<br>
　　　2. 가게 정보 수정<br>
　　　![가게상세_정보수정](https://github.com/Hwa-A/YangYangPojangMacha_2023/assets/100755682/dc7b3534-620c-4d5e-94b9-2f941296f400)
### 가게상세 > 리뷰
　　　1. 리뷰<br>
　　　　　　① 전체 리뷰<br>
　　　　　　![가게상세_전체리뷰목록](https://github.com/Hwa-A/YangYangPojangMacha_2023/assets/100755682/0ec4d48e-fb3f-45ee-a5f6-241635339a30)<br>
　　　　　　② 리뷰 작성<br>
　　　　　　![가게상세_리뷰작성](https://github.com/Hwa-A/YangYangPojangMacha_2023/assets/100755682/32cd9cc6-e253-4c68-b565-fc1f60099813)
### 가게상세 > 번개
　　　1. 번개<br>
　　　　　　① 전체 번개<br>
　　　　　　　　　- 지난 번개 비활성화<br>
　　　　　　![가게상세_번개목록](https://github.com/Hwa-A/YangYangPojangMacha_2023/assets/100755682/63af23a8-73d4-4ab3-be3d-122f0f705d37)<br>
　　　　　　② 아래 조건에 의해 번개 참석 가능/불가능 필터링<br>
　　　　　　　　　- 연령대<br>
　　　　　　　　　- 모집 인원<br>
　　　　　　![가게상세_번개작성](https://github.com/Hwa-A/YangYangPojangMacha_2023/assets/100755682/f32985fc-0bc7-4edc-9025-00037796eb9c)
### 마이페이지
　　　1. 정보 조회<br>
　　　　　　① 내가 좋아요한 가게<br>
　　　　　　② 내가 제보한 가게<br>
　　　　　　③ 내가 작성/참여한 번개<br>
　　　　　　④ 내가 작성한 리뷰<br>
　　　![마이페이지_전체](https://github.com/Hwa-A/YangYangPojangMacha_2023/assets/100755682/3178e697-d0d6-4bdb-ae5f-bc251ebdaf58)<br>
　　　　　　⑤ 리뷰 수정<br>
　　　　　　![마이페이지_리뷰 수정](https://github.com/Hwa-A/YangYangPojangMacha_2023/assets/100755682/3142b524-9a6f-43ae-97f5-14ac9881b4b9)
### 계정관리
　　　1. 로그아웃<br>
　　　2. 탈퇴<br>
　　　3. 메인 진입 시, 읽을 수 있는 어플 설명<br>
　　　![계정관리_처음](https://github.com/Hwa-A/YangYangPojangMacha_2023/assets/100755682/aefdd54e-9706-4e50-92b4-0f049706125d)<br>
　　　　　　① 어플<br>
　　　　　　![계정관리_어플설명](https://github.com/Hwa-A/YangYangPojangMacha_2023/assets/100755682/8b662301-d10b-4205-bb49-075a284290c5)<br>
　　　　　　② 가게 인증<br>
　　　　　　![계정관리_인증설명](https://github.com/Hwa-A/YangYangPojangMacha_2023/assets/100755682/cb0ad931-916a-4720-a8c9-4700e3d289c7)<br>
　　　　　　③ 가게 번개<br>
　　　　　　![계정관리_번개설명](https://github.com/Hwa-A/YangYangPojangMacha_2023/assets/100755682/c3a12724-c681-4fb5-a318-36ad1812fcc4)
