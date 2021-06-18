package uz.pr.yer_nazorat_bot.enteties;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "BN_VILOYAT")
@Data
public class Region {
    @Id
    private Long id;

    @Column(name = "NAME")
    private String name;
}
