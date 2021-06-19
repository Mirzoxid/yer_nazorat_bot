package uz.pr.yer_nazorat_bot.enums;

public enum YerTuri {
    QISHLOQ_XUJALIGI_YERLARI,
    NOQISHLOQ_XUJALIGI_YERLARI;

    @Override
    public String toString() {
        if (this == QISHLOQ_XUJALIGI_YERLARI){
            return "Қишлоқ хўжалиги ерлари";
        } else if (this == NOQISHLOQ_XUJALIGI_YERLARI){
            return "Ноқишлоқ хўжалиги ерлари";
        } else {
            return "";
        }
    }
}
