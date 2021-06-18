package uz.pr.yer_nazorat_bot.enteties;

import lombok.Data;
import uz.pr.yer_nazorat_bot.enums.QonunBuzilishTuri;
import uz.pr.yer_nazorat_bot.enums.YerTuri;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "ynb_nazorat_message")
@Data
public class NazoratMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @Column(nullable = false)
    private TgUser tgUser;

    @Enumerated(EnumType.STRING)
    private YerTuri yerTuri;

    private String address;

    @ManyToOne
    private Region region;

    @ManyToOne
    private District district;

    @Enumerated(EnumType.STRING)
    private QonunBuzilishTuri qonunBuzilishTuri;

    @Column(columnDefinition = "varchar2(4000)")
    private String xabarMazmuni;

    private Byte isView = 0;

    private Instant createdDate = Instant.now();

    private Instant updatedDate;
}
