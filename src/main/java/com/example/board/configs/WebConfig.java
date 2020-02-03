package com.example.board.configs;

import com.example.board.boards.Board;
import com.example.board.boards.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/","/boards/list");
    }

    @Bean
    public ApplicationRunner applicationRunner() {
        return new ApplicationRunner() {
            @Autowired
            private BoardRepository boardRepository;

            @Override
            public void run(ApplicationArguments args) throws Exception {
                Board board = Board.builder()
                        .name("관리자")
                        .title("환영합니다.")
                        .content("자유롭게 글을 남겨주세요.")
                        .build();

                this.boardRepository.save(board);
            }
        };
    }
}
