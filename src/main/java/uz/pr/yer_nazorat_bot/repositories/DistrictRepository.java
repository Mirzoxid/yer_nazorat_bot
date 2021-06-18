package uz.pr.yer_nazorat_bot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pr.yer_nazorat_bot.enteties.District;

import java.util.List;

public interface DistrictRepository extends JpaRepository<District, Long> {
    List<District> findAllByRegionIdOrderByName(Long id);
}
