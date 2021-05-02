## 401 Unauthorized 해결을 위한 Security 설정
- 현재 HelloController는 다음과 같다.
  - 커밋 id : 790435c72b044d220b107f9011a09f751a0e3545
```java
@RestController
@RequestMapping("/apis")
public class HelloController {

    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Hello");
    }
}
```
- hello api를 호출 혹은 테스트 코드를 돌려보면 시큐리티 의존성 주입으로 인해 기본적으로 요청에 유효한 인증 자격이 없으면 401을 뿌려준다.
- 이를 해결하기 위해서 API별 권한 설정을 위한 기본적인 시큐리티 설정을 해줘야한다. (/config/SecurityConfig.java)
  - 커밋 id : 6ac4627eb00febc136865dc4e756460a01d2a965
  
<br>
<br>

## jwt 적용전 사전 작업

- 커밋 id : 42f1715b89ac50de5bd422465c31cd3f63936ee3
- jwt 적용전 사전 작업
  - 데이터 세팅 및 엔티티 설정
  - h2-console 및 favicon.ico에 대한 요청은 시큐리티 인증 수행이 안되도록 Config 추가 설정
  - application.properties에 사전 정보 작성

<br>
<br>

## Jwt 적용을 위한 객체 생성 및 설정

### JWT 관련 객체 생성

- 커밋 id : 14622982dab607e3f4829a176f389a42f4074868
- jwt 관련 로직들을 수행하는 클래스들을 생성한다.
- JwtProvider
  - 토큰의 `생성`과 요청으로 넘어온 토큰의 `유효성 검증` 등을 담당할 Token Provider
  - UsernamePasswordAuthenticationToken 과 SimpleGrantedAuthority 이해 필요
- JwtFilter
  - 현재 실행중인 시큐리티 컨텍스트에 Jwt 인증 정보를 저장을 수행하는 필터
  - JwtProvider 를 주입받고 JwtProvider 를 이용하여 리퀘스트 헤더에 있는 jwt 문자열 값을 파싱해서 Authentication 을 만든 다음 저장
  - GenericFilterBean 이해 필요
- JwtSecurityConfig
  - JwtFilter를 Security 로직에 적용할 수 있도록 필터를 등록하는 역할
  - SecurityConfigurerAdapter 이해 필요
- JwtAuthenticationEntryPoint
  - 유효하지 않는 접근일 경우 401 에러 구현
  - AuthenticationEntryPoint 이해 필요
- JwtAccessDeniedHandler
  - 필요한 권한이 존재하지 않는 경우에 403 에러 구현
  - AccessDeniedHandler 이해 필요

<br>

### JWT 커스텀 객체들 시큐리티 설정에 적용

- 커밋 id : 17105485f5a1a213fd698ac236ad28073d6f810b
