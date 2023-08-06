package kg.kadyrbekov.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kg.kadyrbekov.model.User;
import lombok.*;

import javax.persistence.*;
import java.util.List;

import static javax.persistence.CascadeType.*;

@Getter
@Setter
@Entity
@Table(name = "images")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    ;
    private String name;

    private String originalFileName;

    private String url;

    private Long size;

    private String contentType;

    private boolean isPreviewImage;

    @Lob
    private byte[] bytes;

    @OneToOne(cascade = {PERSIST,REFRESH,DETACH,MERGE})
    private User user;


    @ManyToOne(cascade = REFRESH, fetch = FetchType.EAGER)
    private Cars cars;

}