package uz.pr.yer_nazorat_bot.enums;

public enum QonunBuzilishTuri {
    UZBOSHIMCHALIK_BILAN_EGALLASH,
    NOQONUNIY_QURILISH,
    MAQSADSIZ_VA_SAMARASIZ_QURILISH;

    @Override
    public String toString() {
        if (this == MAQSADSIZ_VA_SAMARASIZ_QURILISH) {
            return "Мақсадсиз ва самарасиз фойдаланиш";
        } else if (this == NOQONUNIY_QURILISH) {
            return "Ноқонуний қурилиш";
        } else if (this == UZBOSHIMCHALIK_BILAN_EGALLASH) {
            return "Ўзбошимчалик билан эгаллаш";
        } else {
            return "";
        }
    }
}
