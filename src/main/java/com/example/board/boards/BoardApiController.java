package com.example.board.boards;

import com.example.board.common.Util;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Api(value = "Board API Documentation")
@Controller
@RequestMapping(value="/api/boards", produces = MediaTypes.HAL_JSON_VALUE)
public class BoardApiController {

    private final BoardRepository boardRepository;

    private final ModelMapper modelMapper;

    public BoardApiController(BoardRepository boardRepository, ModelMapper modelMapper) {
        this.boardRepository = boardRepository;
        this.modelMapper = modelMapper;
    }

    @ApiOperation(value = "게시물 작성 API")
    @PostMapping
    public ResponseEntity createBoard(@RequestBody @Valid Board board, @ApiIgnore Errors errors) {
        if(errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors);
        }

        UserDetails principal = Util.getPrincipal();
        board.setUsername(principal.getUsername());

        Board newBoard = this.boardRepository.save(board);
        WebMvcLinkBuilder selfLinkBuilder = linkTo(BoardApiController.class).slash(newBoard.getId());
        URI createdUri = selfLinkBuilder.toUri();

        BoardResource boardResource = new BoardResource(board);
        boardResource.add(linkTo(BoardApiController.class).withRel("query-boards"));
        boardResource.add(selfLinkBuilder.withRel("update-board"));
        boardResource.add(selfLinkBuilder.withRel("delete-board"));

        return ResponseEntity.created(createdUri).body(boardResource);
    }

    @ApiOperation(value = "특정 게시물 조회 API")
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

    @ApiOperation(value = "게시물 수정 API")
    @PutMapping("/{id}")
    public ResponseEntity updateBoard(@PathVariable Integer id,
                                      @RequestBody @Valid Board board,
                                      @ApiIgnore Errors errors) {
        Optional<Board> optionalBoard = this.boardRepository.findById(id);
        if(!optionalBoard.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        if(errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors);
        }

        Board originBoard = optionalBoard.get();

        UserDetails principal = Util.getPrincipal();
        if(!principal.getUsername().equals(originBoard.getUsername())) {
            return ResponseEntity.status(401).build();
        }

        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        this.modelMapper.map(board, originBoard);
        this.boardRepository.save(originBoard);

        BoardResource boardResource = new BoardResource(originBoard);
        return ResponseEntity.ok(boardResource);
    }

    @ApiOperation(value = "게시물 삭제 API")
    @DeleteMapping("/{id}")
    public ResponseEntity deleteBoard(@PathVariable Integer id) {
        Optional<Board> optionalBoard = this.boardRepository.findById(id);
        if(!optionalBoard.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Board board = optionalBoard.get();

        UserDetails principal = Util.getPrincipal();
        if(!principal.getUsername().equals(board.getUsername())) {
            return ResponseEntity.status(401).build();
        }

        this.boardRepository.delete(board);

        return ResponseEntity.noContent().build();
    }
}
