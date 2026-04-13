package ru.vych.chat;

import ru.vych.agent.tools.ToolJsonResponseWrapper;

import java.util.HashMap;
import java.util.Map;

public class CardTools {
    private static int BALANCE = 100;

    public static String getBalance() {
        var response = new ToolJsonResponseWrapper("get_balance", new HashMap<>());
        return response.addContent("balance", BALANCE).json();
    }

    public static String deposit(Integer amount) {
        var response = new ToolJsonResponseWrapper("deposit", Map.of(
                "amount", amount
        ));
        BALANCE += amount;
        return response.addContent("newBalance", BALANCE).json();
    }

    public static String withdraw(Integer amount) {
        var response = new ToolJsonResponseWrapper("withdraw", Map.of(
                "amount", amount
        ));
        BALANCE -= amount;
        return response.addContent("newBalance", BALANCE).json();
    }
}
