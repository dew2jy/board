package com.example.board.boards;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class BoardResource extends EntityModel<Board> {
    public BoardResource(Board board, Link... links) {
        super(board, links);
        add(linkTo(BoardApiController.class).slash(board.getId()).withSelfRel());
    }
}
