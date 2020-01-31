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

import javax.print.attribute.standard.Media;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
                .andExpect(status().isBadRequest());
    }

}