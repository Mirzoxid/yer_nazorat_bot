package uz.pr.yer_nazorat_bot.components;

import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.s;
import org.springframework.stereotype.Component;
import uz.pr.yer_nazorat_bot.enums.RegisterStateType;
import uz.pr.yer_nazorat_bot.enums.UserStateType;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class UserStateMapComponent {
    private Map<String, Map<String, Object>> stateMap = new HashMap<>();

    public Map<String, Map<String, Object>> getStateMap() {
        return stateMap;
    }

    public void setStateMap(Map<String, Map<String, Object>> stateMap) {
        this.stateMap = stateMap;
    }

    public Map<String, Object> getUserStateMap(String userId) {
        return this.stateMap.getOrDefault(userId, new HashMap<>());
    }

    public Map<String, Object> putUserStateMap(String userId, Map<String, Object> userState) {
        return this.stateMap.put(userId, userState);
    }

    public void putUserStateMap(String userId, String stateType, UserStateType userStateType) {
        Map<String, Object> objectMap = getUserStateMap(userId);
        objectMap.put(stateType, userStateType.name());
        this.stateMap.put(userId, objectMap);
    }

    public void putUserStateMap(String userId, String stateType, RegisterStateType registerStateType) {
        Map<String, Object> objectMap = getUserStateMap(userId);
        objectMap.put(stateType, registerStateType.name());
        this.stateMap.put(userId, objectMap);
    }

    public void putUserStateMap(String userId, String stateType, Object o) {
        Map<String, Object> objectMap = getUserStateMap(userId);
        objectMap.put(stateType, o);
        this.stateMap.put(userId, objectMap);
    }

    public void endRegisterState(String userId) {
        Map<String, Object> stateMap = new HashMap<>();
        stateMap.put("stateType", UserStateType.WAIT);
        this.stateMap.put(userId, stateMap);
    }
}
