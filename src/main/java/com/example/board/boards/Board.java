package com.example.board.boards;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import java.util.Date;

@Builder @AllArgsConstructor @NoArgsConstructor
@Getter @Setter
@Entity
public class Board {
    @Id
    @GeneratedValue
    private Integer id;
    @NotEmpty
    private String name;
    @NotEmpty
    private String title;
    @NotEmpty
    private String content;
    @CreationTimestamp
    @Column(updatable = false)
    private Date createdDateTime;
    @UpdateTimestamp
    private Date updatedDateTime;
    @Column(nullable=false, updatable = false)
    private String username;
}
