package uz.pr.yer_nazorat_bot.enteties;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "BN_TUMAN")
@Data
public class District {
    @Id
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "REGION_ID")
    private Long regionId;

}
