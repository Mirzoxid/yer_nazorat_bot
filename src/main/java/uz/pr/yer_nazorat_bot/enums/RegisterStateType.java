package uz.pr.yer_nazorat_bot.enums;

public enum RegisterStateType {
    REGION, DISTRICT, YER_TYPE, ADDRESS, QONUN_BUZILISH, FINISH;

    public static boolean hasContains(String registerState) {
        for (RegisterStateType type : values()) {
            if (type.name().equals(registerState)) {
                return true;
            }
        }
        return false;
    }
}
