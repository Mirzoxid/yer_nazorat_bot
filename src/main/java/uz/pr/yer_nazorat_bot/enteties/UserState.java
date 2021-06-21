package uz.pr.yer_nazorat_bot.enteties;

import lombok.Data;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "ynb_user_state")
@Data
public class UserState {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    @Column(columnDefinition = "clob")
    private String stateData;

    private Instant createdDate = Instant.now();

    private Instant updatedDate;
}