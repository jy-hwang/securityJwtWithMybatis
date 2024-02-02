# securityJwtWithMybatis

## 사용기술
 - JAVA 17
 - Spring boot 2.7.18
 - mysql
 - mybatis
 - JWT 0.12.3
 - Eclipse STS(4.20.1)

## 간단히 만든 회원가입과 로그인
backend 만 구현하였으므로 restapi tester 를 통해 확인해볼 수 있다.

 - /api/v1/member        POST 회원가입
```json
{
    "email" : "",
    "name" : "",
    "mobile" : "",
    "password" :"",
    "confirmPassword" : "",
    "agree": true
}
```

 - /api/v1/member/token  POST 로그인(토큰발급확인)
```json
{
    "email" : "",
    "password" :"",
}
```

### 참고 : https://github.com/yonggyo1125
이 샘플 프로젝트는 securityJWTWithJPA 프로젝트를 mybatis - mysql 로 변경한 것으로 자세한 설명과 주석은 위 사이트를 참고하기 바람.
간단히 만들어본 프로젝트 이므로 추가할 부분이 많다. 차후에 하나씩 해봤으면 좋겠다.

### 추가해보면 좋을만한 사항들
 - rememberme 기능 구현 및 refresh Token 추가
 - login 기능만 있는데, logout 기능 추가
