package com.example.board.boards;

import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.xml.ws.Response;
import java.net.URI;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

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

    @GetMapping("/{id}")
    public ResponseEntity getBoard(@PathVariable Integer id) {
        Optional<Board> optionalBoard = this.boardRepository.findById(id);
        if(!optionalBoard.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Board board = optionalBoard.get();
        BoardResource boardResource = new BoardResource(board);
        return ResponseEntity.ok(boardResource);
    }
}
