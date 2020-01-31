package com.example.board.boards;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.http.ResponseEntity.badRequest;

@Controller
@RequestMapping(value="/api/boards")
public class BoardController {

    private final BoardRepository boardRepository;

    public BoardController(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    @PostMapping
    public ResponseEntity createBoard(@RequestBody @Valid Board board, Errors errors) {
        if(errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors);
        }

        Board newBoard = this.boardRepository.save(board);
        WebMvcLinkBuilder selfLinkBuilder = linkTo(BoardController.class).slash(newBoard.getId());
        URI createdUri = selfLinkBuilder.toUri();

        BoardResource boardResource = new BoardResource(board);
        boardResource.add(linkTo(BoardController.class).withRel("query-boards"));
        boardResource.add(selfLinkBuilder.withRel("update-board"));
        boardResource.add(selfLinkBuilder.withRel("delete-board"));

        return ResponseEntity.created(createdUri).body(boardResource);
    }
}
