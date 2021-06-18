package uz.pr.yer_nazorat_bot.enteties;

import lombok.Data;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "YNB_NAZORAT_MESSAGE_FILES")
@Data
public class NazoratMessageFiles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private NazoratMessage nazoratMessage;

    @Column(name = "FILE_URL")
    private String fileUrl;

    @Column(name = "TG_FILE_ID")
    private String tgFileId;

    @Column(name = "IS_DELETED")
    private Integer isDeleted;

    @Column(name = "CREATED_DATE")
    private Date createdDate;

    @Column(name = "LAST_MODIFIED_DATE")
    private Date lastModifiedDate;

    @Column(name = "DOWNLOAD_ACTION_CNT")
    private Long downloadActionCnt;
}
