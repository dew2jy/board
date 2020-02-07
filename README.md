# board

* 구현 설명
기술스택 : java, spring boot, jpa, h2 in-memory db, spring security, swagger, thymeleaf, lombok
1. 초기 세팅값은 WebConfig 클래스에서 applicationRunner 메소드를 오버라이드하여 설정하였습니다.
~~~
    @Bean
    public ApplicationRunner applicationRunner() {
        return new ApplicationRunner() {
            @Autowired
            private BoardRepository boardRepository;

            @Autowired
            private AccountService accountService;

            @Autowired
            private AppProperties appProperties;

            @Override
            public void run(ApplicationArguments args) throws Exception {
                Account account = Account.builder()
                        .username(appProperties.getUserUsername())
                        .password(appProperties.getUserPassword())
                        .build();

               this.accountService.saveAccount(account);

                Account account2 = Account.builder()
                        .username(appProperties.getUser2Username())
                        .password(appProperties.getUser2Password())
                        .build();

                Account newAccount = this.accountService.saveAccount(account2);

                Board board = Board.builder()
                        .name("이지연")
                        .title("환영합니다.")
                        .content("자유롭게 글을 남겨주세요.")
                        .username(newAccount.getUsername())
                        .build();

                this.boardRepository.save(board);
            }
        };
    }
~~~

2. 게시판의 쓰기/수정/삭제/조회 기능을 BoardApiController에 REST 방식으로 구현하였습니다.
하지만, thymeleaf를 이용해 API 연동을 하려고 하니 번거로워서 연동 메서드만 만들고 나머지는 model에 데이터를 추가하여 view에 전달하는 방식으로 구현했습니다.
`private static boolean connectRESTAPI(String method,  String body, Model model)`

3. swagger를 이용해 api 문서를 제공합니다. 원래 다음 어노테이션 방식으로 작성하였으나, 오류가 발생하여 github과 stackoverflow를 참조하여 현재 어노테이션으로 변경하였습니다.
`@EnableSwagger2`

4. 로그인은 Spring Security를 사용하였습니다. SecurityConfig에서 static 경로 리소스는 접근 가능하도록 세팅하였습니다.

5. entity 클래스 작성 시 lombok을 사용하여 편리하게 구현하였습니다.


* 구동 방법
    1. git clone https://github.com/dew2jy/board.git
    2. board 디렉토리로 이동
    3. mvn package
    4. target 디렉토리로 이동
    5. java -jar board-0.0.1-SNAPSHOT.jar
    6. 브라우저에서 http://localhost:8080/ 접속
    7. 기본설정 계정인 username : user@email.com / password : user 입력 후 로그인

[테마 출처] (https://www.w3schools.com/)