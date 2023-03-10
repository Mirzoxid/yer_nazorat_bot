package uz.pr.yer_nazorat_bot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.pr.yer_nazorat_bot.enteties.NazoratMessage;
import uz.pr.yer_nazorat_bot.enteties.NazoratMessageFiles;

import java.util.List;

@Repository
public interface NazoratMessageFileRepository extends JpaRepository<NazoratMessageFiles, Long> {
    List<NazoratMessageFiles> findAllByNazoratMessageId(Long id);

    List<NazoratMessageFiles> findAllByFileUrlIsNull();
}
