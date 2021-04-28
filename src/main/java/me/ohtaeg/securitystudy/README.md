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

