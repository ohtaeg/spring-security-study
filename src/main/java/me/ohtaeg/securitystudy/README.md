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
- hello api를 호출을 하게 되면 시큐리티 의존성 주입을 인해 기본적으로 요청에 유효한 인증 자격이 없으면 401을 뿌려준다.
- 이를 해결하기 위해서 API별 권한 설정을 위한 기본적인 시큐리티 설정을 해줘야한다.

