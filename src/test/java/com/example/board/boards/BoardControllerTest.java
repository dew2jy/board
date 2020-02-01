package com.example.board.boards;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class BoardControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    BoardRepository boardRepository;

    //정상적으로 글을 쓰는지 테스트
    @Test
    public void createBoard() throws Exception {
        Board board = Board.builder()
                .name("Spring")
                .title("Spring is Funny")
                .content("It's lie")
                .build();

        mockMvc.perform(post("/api/boards")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(board))
        )
                .andDo(print())
                .andExpect(status().isCreated())    //201 response code
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.query-boards").exists())
                .andExpect(jsonPath("_links.update-board").exists())
                .andExpect(jsonPath("_links.delete-board").exists())
        ;
    }

    //입력 값이 비어있는 경우에 에러가 발생하는 테스트
    @Test
    public void createBoard_Bad_Request_Empty_Input() throws Exception {
        Board board = Board.builder().build();

        this.mockMvc.perform(post("/api/boards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(board))
        )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    //특정 게시글 정상적으로 조회하는지 테스트
    @Test
    public void getBoard() throws Exception {
        Board board = generateBoard(100);

        this.mockMvc.perform(get("/api/boards/{id}", board.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("_links.self").exists())
                ;
    }

    //없는 게시글을 조회했을 때 404에러 응답
    @Test
    public void getBoard404() throws Exception {
        this.mockMvc.perform(get("/api/boards/13123"))
                .andDo(print())
                .andExpect(status().isNotFound())
                ;
    }

    //테스트값 생성
    public Board generateBoard(int index) {
        Board board = Board.builder()
                .name("board" + index)
                .title("board title")
                .content("board content")
                .build();
        return this.boardRepository.save(board);
    }
}