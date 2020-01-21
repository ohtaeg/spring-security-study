# Spring Security
study

## Environment
- Java 8
- SpringBoot 2.x
- SpringSecurity 5.x  +  JWT


### I learned

#### Session
- `Session`은 서버가 해당 서버로 접근하는 클라이언트를 식별하는 방법중 하나이다.
    1. 클라이언트가 서버에게 접속 요청
    2. 서버는 클라이언트에게 reponse-header field인 cookie에서 `Session`-id를 보냈는지 확인
    3. 만약 `Session`-id가 없으면 서버는 `Session`-id를 생성하여 클라이언트에게 set-cookie를 통해 
    `Session`-id를 발행하고 클라이언트에게 전달 및 서버에 저장.
    4. 서버로부터 발행된 `Session`-id는 서버와 클라이언트에 저장된다.

#### Stateful 
- `Stateful` 서버는 클라이언트의 요청을 받고, 클라이언트의 상태를 계속 유지 하며 이 정보를 서비스 제공에 이용한다.
- 대표적인 예로는 `Session`기반 서버이다. `Session`을 사용하는 서버들은 유저의 정보를 기억하고 있어야 하므로
`Session`을 유지하려면 메모리 / 디스크 / DB를 통해 세션을 관리해야 했었다.

##### Stateful의 단점
- 만약 유저의 수가 늘어난다면 서버 및 DB 성능에 무리를 줄 수도 있다.
- 많은 트래픽을 여러개의 서버로 분산하다보면 서버 컴퓨터를 추가해야되는 하드웨어적인 문제가 존재.
- 만약 특정 서버가 장애가 나서 다른 서버에서 사용자를 인증해야되는 경우, 장애난 서버에서 인증된 사람들은
재 로그인 해야하는 문제가 발생한다. (즉 장애로 인해 서버의 인증 정보가 유실 된다.)
- `Session`을 관리할때 사용하는 쿠키는 `동일 출처 정책(Same-origin policy)`를 지키는
착한 녀석(?)이기 때문에 여러 도메인에서 관리하려면 CORS 처리를 신경써야 한다.
- `Session` 클러스터를 통해 모든 서버들이 `Session`을 공유하는 방법이 있지만, `Session` 클러스터가 문제 생길 경우
대규모 장애가 발생할 가능성이 있다.
- 애초에 HTTP 프로토콜은 정보를 전달할뿐 저장하지 않는 근본적으로 `무상태(Stateless)` 프로토콜이다.

#### Stateless
- `Stateless` 서버는 상태를 유지하지 않고 클라이언트 측에서 들어오는 요청만 수행한다.
- `Stateless` 특징으로 인해 클라이언트와 서버의 연결고리가 없어지므로 사용자 정보가 특정 WAS에 의존적이지 않게되고, 확장성이 높아지는 장점이 있다.
- `Stateless` 서버를 구성한 대부분의 서비스들은 `토큰 기반 인증 시스템`을 이용한다.
- `토큰 기반 인증 시스템`을 이용하면 서버 확장이 유연하며, 웹 표준 기반을 따르게 된다.
- 서버측에서는 `Session`을 유지 할 필요가 없다. 즉 유저가 로그인 되어있는지 안되어있는지 신경 쓸 필요가 없고, 유저가 요청을 했을때 토큰만 확인하면 되니, <br>
`Session` 관리가 필요 없어서 서버 자원을 아낄 수 있다.
- `Stateless`의 대표적인 예는 `JWT`가 있다.
1. 클라이언트가 서버에게 인증 요청
2. 서버측에서 계정정보가 검증이 되면 클라이언트에게 검증된 signed JWT 토큰을 생성
3. 서버에서 클라이언트에게 토큰을 전달하는데 HTTP 헤더에 이 토큰을 담아 전달
4. 클라이언트는 검증된 signed JWT 토큰을 저장.
5. 클라이언트는 이제 요청을 할때마다 JWT 토큰을 같이 보내고 서버는 받은 JWT이 위변조됐는지 확인하고 요청을 처리

#### JWT
- Json Web Token, 웹표준 ([RFC 7519](https://tools.ietf.org/html/rfc7519)) 
- 가볍고 필요한 정보를 자체적으로 가지고 있는 자가 수용적인 방식이다.
- HTTP 헤더 or URL 파라미터로 전달할 수 있다.
- `JWT`의 주요 목적은 안전하게 `클레임 (Claim)`을 전송하는 것이다.
- 주로 회원 인증에서 많이 사용한다.
- `.` 구분자로 3가지의 문자열로 구성되어 있다.
    - xxxx.yyyy.zzzz
    - xxxx - `헤더 (Header)`
    - yyyy - `정보 (Payload)`
    - zzzz - `서명 (Signature)`
    
<br>

- `헤더 (Header)` : typ, alg 라는 두가지 정보를 가진다.
    - typ : 토큰 타입, JWT
    - alg : 해싱 알고리즘, 주로 sha256이나 RSA 방식을 채택한다. 이 알고리즘은 `서명 (Signature)`에서도 사용한다.
    - JWT 토큰을 만들때는 담당 라이브러리가 알아서 Base64 인/디코딩 + 해싱작업을 해준다.
    - Base64로 인코딩하면서 공백 및 엔터들이 제거가 된다. 인코딩후 맨뒤에 == 같은 문자가 붙는데 이런 문자를 `padding 문자` 라고 한다.
    - `padding 문자`는 url-safe 하지 않기 때문에 지우고 사용해야 한다.
    <pre>
        <code>{</code>
        <code>  "typ" : "JWT"</code>
        <code>  , "alg" : "HS256"</code>
        <code>}</code>
    </pre>
       
<br>
 
- `정보 (Payload)` : 토큰의 바디로써, 자체적인 정보를 가지는 부분이다. 정보들을 `클레임 (Claim)`으로 관리 한다.
    - `클레임 (Claim)` : 객체나 데이터에 대한 정의, 표현을 나타내며, key-value 한쌍으로 3가지 유형으로 set을 이루고 있다.
        - `등록된 클레임 (Registered Claim)`
        - `공개 클레임 (Public Claim)`
        - `비공개 클레임 (Private Claim)`
    <pre>
        <code>
            {
                "iss": "test",
                "nbf": "1455270000000",
                "exp": "1485270000000",
                "http://localhost:8080/api/user": true,
                "userId": "1",
                "username": "ohtaeg"
            }
            /*
            3개의 등록된 클레임
            1개의 공개 클레임
            2개의 비공개 클레임
            */
        </code>
    </pre>
    - `등록된 클레임 (Registered Claim)`
        - 토큰에 대한 정보를 담기위해 이미 이름이 정해잔 클레임
        - iss (issuer) : 토큰 발급자
        - sub (subject) : 토큰 주제
        - aud (audience) : 토큰을 사용할 수신자
        - exp (expire) : 토큰 만료 시간
        - nbf (not before) : 해당 날짜전까지 해당 토큰은 처리 불가
        - iat (issued at) : 토큰이 발급된 시간
        - jti : `JWT`의 ID, 중복처리 방지를 위해 일회용 토큰에 주로 사용
        - `등록된 클레임`의 사용은 선택적이다.
    
    - `공개 클레임 (Public Claim)`
        - 충돌이 방지를 위해 이름을 가지는데 이름은 URI 형식으로 되어있다.
        <pre><code>{ "http://localhost:8080/api/user/1" : true }</code></pre> 
    
    - `비공개 클레임 (Private Claim)`
        - 클라이언트 - 서버간에 사용되는 클레임.
        <pre><code>{ "userId" : "1", "userName : "ohtaeg" }</code></pre>
          
<br>
  
- `서명 (Signature)`
    - JWT의 마지막 부분으로써, `헤더 (Header)`의 인코딩 값 + `정보 (Payload)`의 인코딩 값을 합친 후
    헤더에서 명시한 알고리즘을 이용하여 비밀키로 생성한다.
    - 비밀키를 base64 형태로 변환 (문자열 인코딩이 아닌 포맷 변환)만 해주면 `JWT`가 만들어진다.
