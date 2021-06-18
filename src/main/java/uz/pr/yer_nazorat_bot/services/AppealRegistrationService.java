package uz.pr.yer_nazorat_bot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.pr.yer_nazorat_bot.repositories.DistrictRepository;
import uz.pr.yer_nazorat_bot.repositories.NazoratMessageFileRepository;
import uz.pr.yer_nazorat_bot.repositories.NazoratMessageRepository;
import uz.pr.yer_nazorat_bot.repositories.RegionRepository;

@Service
@RequiredArgsConstructor
public class AppealRegistrationService {
    private final RegionRepository regionRepository;
    private final DistrictRepository districtRepository;
    private final NazoratMessageRepository nazoratMessageRepository;
    private final NazoratMessageFileRepository nazoratMessageFileRepository;


}
