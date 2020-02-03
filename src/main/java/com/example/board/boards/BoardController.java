package com.example.board.boards;

import com.example.board.common.AppProperties;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

@Controller
@RequestMapping(value = "/boards")
public class BoardController {

    private final BoardRepository boardRepository;

    private final AppProperties appProperties;

    static final String SPEC = "http://localhost:8080/api/boards";

    public BoardController(BoardRepository boardRepository, AppProperties appProperties){
        this.boardRepository = boardRepository;
        this.appProperties = appProperties;
    }

    @GetMapping(value = "/list")
    public String listBoards(Model model) {
        model.addAttribute("boards", this.boardRepository.findAll());

        return "list";
    }

    @GetMapping(value = "/write")
    public String writeBoardsForm(Integer id, Board board, Model model) {
        Optional<Board> optional = this.boardRepository.findById(id);

        if(optional.isPresent()) {
            model.addAttribute("board", optional.get());
        }

        return "write";
    }

    @PostMapping(value = "/write")
    public String writeBoards(@Valid Board board, BindingResult result){
        /*
        ObjectMapper objectMapper = new ObjectMapper();
        String boardJson = objectMapper.writeValueAsString(board);

        if (connectRESTAPI(HttpMethod.POST.toString(), boardJson, model)) return "board";

        model.addAttribute("errorMessage", "error");
        return "error";
        */

        if(result.hasErrors()) {
            return "write";
        }
        Board newBoard = this.boardRepository.save(board);

        return "redirect:/boards/detail?id="+newBoard.getId();
    }

    //REST API 연동
    private static boolean connectRESTAPI(String method,  String body, Model model) throws IOException {
        URL url = new URL(SPEC);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        conn.setRequestMethod(HttpMethod.POST.toString());

        //header
        conn.setRequestProperty(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        conn.setRequestProperty(HttpHeaders.ACCEPT, MediaTypes.HAL_JSON_VALUE);
        conn.setDoInput(true);

        if(body != null) {
            conn.setDoOutput(true);

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(body);
            wr.flush();
        }

        StringBuilder sb = new StringBuilder();
        if(conn.getResponseCode() == HttpStatus.CREATED.value()) {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "utf-8"));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            br.close();

            String s = sb.toString();

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            Board resBoard = objectMapper.readValue(s, Board.class);
            model.addAttribute("board", resBoard);
            return true;
        } else {
            System.out.println(conn.getResponseMessage());
        }
        return false;
    }

    @GetMapping(value = "/detail")
    public String detailBoard(Integer id, Model model) {
        Optional<Board> optional = this.boardRepository.findById(id);

        if(!optional.isPresent()) {
            model.addAttribute("errorMessage", "존재하지 않는 게시글입니다.");
            return "error";
        } else {
            model.addAttribute("board", optional.get());
        }

        return "detail";
    }

    @GetMapping(value = "/delete")
    public String deleteBoard(Integer id, Model model) {
        try {
            this.boardRepository.deleteById(id);
        } catch(EmptyResultDataAccessException e) {
            model.addAttribute("errorMessage", "존재하지 않는 게시글입니다.");
            return "error";
        }
        return "redirect:/boards/list";
    }
}
