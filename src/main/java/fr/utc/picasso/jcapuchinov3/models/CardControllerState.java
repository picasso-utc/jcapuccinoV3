package fr.utc.picasso.jcapuchinov3.models;

import java.util.HashMap;
import java.util.Map;

public class CardControllerState {
    ControllerStates state;

    public static CardControllerState notConnected() {
        return new CardControllerState(ControllerStates.NOT_CONNECTED);
    }

    public static CardControllerState connected() {
        return new CardControllerState(ControllerStates.CONNECTED);
    }

    public static CardControllerState unknown() {
        return new CardControllerState(ControllerStates.UNKNOWN);
    }

    private CardControllerState(ControllerStates state) {
        this.state = state;
    }

    public Map<String, String> toMap() {
        Map<String, String> json = new HashMap<>();
        json.put("state", state.toString());
        return json;
    }
}
