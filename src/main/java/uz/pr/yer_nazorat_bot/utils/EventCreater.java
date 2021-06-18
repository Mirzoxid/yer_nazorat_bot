package uz.pr.yer_nazorat_bot.utils;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EventCreater<T> {
    private final T obj;

    public T get() {
        return obj;
    }
}
