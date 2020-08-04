# 패스워드 암호화 기법들
- 보안은 강한 부분이 얼마나 강한지가 아니라 약한곳이 얼마나 약한지에 따라 좌우가 된다.
- 대부분의 웹 사이트에서 사용하고 있는 검증된 암호화 알고리즘들이 무엇이 있는지 공부해보자

## 해킹 공격 방법
- Brute Force : 무식하게 조합 가능한 모든 패스워드를 대입해보는 공격 방법이다.
    - 시간이 엄청 오래걸리는데 무어의 법칙처럼 시간이 지날수록 장비의 성능이 좋아져 시간이 단축되어 진다는것.
- Rainbow Table : 미리 많이 쓰는 비밀번호와 해시 값들을 테이블로 만들어 매핑하여 비교해나가는 방법.
- 브루트 포스를 이용하여 가능한 조합을 다 계산한 Rainbow Table을 가지고 공격한다면?
- 그래서 이 두가지 방법에 대한 대처 방법은 다음과 같다.
    - 암호화 시간을 많이 소요하게하여 브루트 포스 공격의 효율성을 저하시키는 것
        - 브루트 포스를 막을 수 있는 방법이 없으므로, 느리게 만드는것이 최선
    - Rainbow Table을 무력화 하는 방법
        - 원본 패스워드에 임의의 문자열을 추가하는 방법
- 복잡하게 느린 알고리즘을 사용하면 될까? 답은 ㄴㄴ
    - 복잡한건 공격자에게 아무 필요없고, 시간이 답이다.
    - 알고리즘 수행 시간을 조정 가능한 방법을 사용하는게 좋다.
    - 알고리즘 수행 시간을 조정하는 방법은 해싱을 반복하는 것.
- 어떤 암호화 알고리즘이 있는지 알아보자.
    
<br>

### 단뱡항 해시 함수
- 기본적으로 암호화 알고리즘은 해시를 이용하는데 해시된 데이터는 원본 데이터로 돌릴 수 없어 해싱을 주로 사용한다.
- 원본 데이터를 알면 암호화 데이터를 구하기 쉽지만, 암호화 데이터로 원본 데이터로 구할 수 없기에 단방향이다.
- 댠뱡향 해시함수의 연산을 통해 원본 데이터를 암호화한 데이터를 `다이제스트`라고 한다.
- 단방향 해시 함수의 문제가 존재하는데 링크드인이 SHA-1 기법을 사용했지만 털린적이 있다. 무슨 문제들이 있는지 살펴보자.

<br>

1. 인식 가능성
    - 동일한 값에 대해 언제나 동일한 다이제스트를 가진다.
    - 공격자가 해당 다이제스트에 대한 전처리값을 많이 확보하고 공격한다면 원본 데이터를 찾는것과 동일한 효과를 가진다.
        - 이 방법을 위에서 설명한 Rainbow Table 공격 기법이라 한다.
            ```
            18 % 10 = 8(해시코드)
             8 % 10 = 8(해시코드)
            ```
        - 해시 충돌을 이용한 방법이다.
2. 속도
    - 해시는 패스워드 저장이 아닌 짧은 시간에 데이터를 검색하기 위해 설계된 방법이다.
    - 해시함수의 빠른 처리 속도로 인해 공격자는 빠른 속도로 다이제스트를 비교할 수 있다는 문제가 있다.

<br>

### 단방향 해시 함수의 보완
1. 솔팅(salting)
    - salt란 다이제스트 생성전에 소금(salt)치듯 임의의 문자열을 추가 하는것을 말한다.
    - 원본 데이터에 솔트 문자열을 추가하여 다이제스트를 생성하는것을 `솔팅`이라 한다.
    - 이 방법의 목적은 Rainbow Table을 무력화하기 위해 고안된 것.
        - 미리 계산해놓은 Rainbow Table은 꽤 힘이 빠질 것.
    - 고정된 salt를 가진다면 이에 대한 Rainbow Table가 완성될 수 있으므로 사용자마다 다른 솔트를 적용한다면 인식 가능성의 문제가 개선된다.
    - salt의 길이는 16byte 정도는 되어야 안전하다고 한다.
2. 키 스트레칭 (key-stretching)
    - 입력한 패스워드의 다이제스트를 생성하고, 생성된 다이제스트를 다시 해시함수의 입력값으로 다이제스트를 생성하는 방법으로 해시함수를 반복해서 다이제스트를 생성하는 방법이다.
    - 입력한 패스워드를 동일한 횟수만큼 해싱해야 일치여부 확인이 가능하다.
    - 이 방법은 공격자가 패스워드를 추측하는데 많은 시간을 소요하도록 하는 방법이다.
    - 좋은 장비로는 1초에 50억개 다이제스트 비교연산이 가능하다고 한다. 키 스트레칭을 적용하면 1초에 수백번정도의 연산할 수 있도록 효율을 떨어뜨릴 수 있다.

<br>

## 솔팅과 키 스트레칭이 적용된 승인된 암호화 기법들

1. PBKDF2 (Password - Based Key Derived Function)
    - 솔트 적용후 해시 함수의 반복 횟수를 정하는 알고리즘
    - Digest = PBKDF2(난수, password, salt, count, length);
2. BCrypt
    - 다이제스트 생성시 반복횟수를 변수로 지정가능하게하여 작업량(해싱시간)을 조절하는 방법
    - 다만 입력값을 72byte로 해야하는 귀찮은 단점이 있다.
    - [온라인으로 bcrypt로 암호화](https://www.devglan.com/online-tools/bcrypt-hash-generator)를 해보면 다음과 같다.
        - 원본 패스워드가 "abcd", 그리고 연산의 cost를 4로 지정하면 다음과 같은 해시코드가 만들어진다.
            ```
            $2a$04$6Jd0kafuk416QeURiwj8YOsQMgflUshhaKLp3LUYUBF7rrpKHZGg2
          
            $2a - bcrypt 버전 정보
            $04 - cost 정보
            $6Jd... - base64된 해시코드
            ```
3. scrypt
    - PBKDF2와 유사하며, 메모리 오버헤드를 갖도록 설계되어 브루트 포스 시도시, 병렬처리를 어렵게하는 방법.
    - 비용이 비싸기 때문에 충분한 비용이 있다면 이 방법을 택하는것을 권장한다.
    
## 3가지 알고리즘 중 왜 BCrypt를 사용하는가?
- GPU 기반 공격과 관련이 있다. 암호화 알고리즘은 공격자가 공격하기 어렵게 하기 위해 고안된 것인데, 공격자는 GPU를 사용하여 더 많은 공격을 할 수 있다.
- 우선 CPU와 GPU 이야기를 해보자.
    - 코어가 작업을 처리하는 애인데, CPU가 8코어면 동시에 8개의 작업을 처리 가능하다. 
    - GPU는 그래픽 처리를 하는데 단순하게 보여지는 역할만 해서 작업 속도는 느리지만 코어가 많다.
    - 비트코인 채굴의 과정을 보면 결국 해싱 돌려서 결과값 맞추는 단순 작업이라 CPU의 성능보단 GPU의 병렬성이 더 적합하여 비트코인을 GPU로 채굴한다.
- 이러한 관점에서 봤을 때 결국 공격자는 PC로 주로 공격을 하겠지만 GPU를 사용하는 산업용 하드웨어로도 공격할 수 있다.
- PBKDF2는 작업이 단순하기 때문에 GPU로 커버가 가능하고 그 중 어떠한 반도체로 만든 GPU는 최적화돼서 공격자에게 유리하다.
    - 정말 작업이 단순하다고 말할 수 있는지는 모른다. 뇌피셜
- BCrypt는 연산이 복잡하기 때문에 메모리 사용을 하도록 구현했다. 그렇기에 GPU 내부에서는 공유 메모리를 사용하고 있어서 모든 코어가 내장 메모리 버스의 제어를 위해 경쟁하다보니 느리다.
- 그래서 BCrypt 를 사용하는갑다.
