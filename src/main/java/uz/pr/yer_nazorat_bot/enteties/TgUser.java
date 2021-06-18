package uz.pr.yer_nazorat_bot.enteties;

import lombok.Data;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "ynb_tg_users")
@Data
public class TgUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tgChatId;

    private String userType;

    private Instant createdDate = Instant.now();

    private Instant updatedDate;

    private String phoneNumber;

    @Column(nullable = false)
    private String firstName;

    private String lastName;

    private String userName;

    private Byte isActive = 1;
}
