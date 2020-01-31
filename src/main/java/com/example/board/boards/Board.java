package com.example.board.boards;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Builder @AllArgsConstructor @NoArgsConstructor
@Getter @Setter
@Entity
public class Board {
    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    private String title;
    private String content;
    @CreationTimestamp
    private Date createdDateTime;
    @UpdateTimestamp
    private Date updatedDateTime;

}
