package com.example.board.boards;

import junitparams.JUnitParamsRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

//lombok test
@RunWith(JUnitParamsRunner.class)
public class BoardTest {
    @Test
    public void builder() {
        Board board = Board.builder()
                .name("이지연")
                .title("test board")
                .content("test")
                .build();
        assertThat(board).isNotNull();
    }

    @Test
    public void javaBean() {
        //Given
        String name = "Board";
        String title = "Spring";

        //When
        Board board = new Board();
        board.setName(name);
        board.setTitle(title);

        //Then
        assertThat(board.getName()).isEqualTo(name);
        assertThat(board.getTitle()).isEqualTo(title);
    }

}
