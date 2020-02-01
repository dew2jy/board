package com.example.board.boards;

import org.modelmapper.ModelMapper;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Controller
@RequestMapping(value="/api/boards", produces = MediaTypes.HAL_JSON_VALUE)
public class BoardController {

    private final BoardRepository boardRepository;

    private final ModelMapper modelMapper;

    public BoardController(BoardRepository boardRepository, ModelMapper modelMapper) {
        this.boardRepository = boardRepository;
        this.modelMapper = modelMapper;
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

    @PutMapping("/{id}")
    public ResponseEntity updateBoard(@PathVariable Integer id,
                                      @RequestBody @Valid Board board,
                                      Errors errors) {
        Optional<Board> optionalBoard = this.boardRepository.findById(id);
        if(!optionalBoard.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        if(errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors);
        }

        Board originBoard = optionalBoard.get();
        this.modelMapper.map(board, originBoard);
        this.boardRepository.save(originBoard);

        BoardResource boardResource = new BoardResource(originBoard);
        return ResponseEntity.ok(boardResource);
    }
}
