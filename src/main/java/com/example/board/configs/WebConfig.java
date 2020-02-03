package com.example.board.configs;

import com.example.board.accounts.Account;
import com.example.board.accounts.AccountService;
import com.example.board.boards.Board;
import com.example.board.boards.BoardRepository;
import com.example.board.common.AppProperties;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/","/boards/list");
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

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
}
