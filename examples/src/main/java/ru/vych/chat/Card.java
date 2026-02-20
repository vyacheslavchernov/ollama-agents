package ru.vych.chat;

/**
 * "Счёт", которым может управлять агент {@link BankAgent}
 */
public class Card {
    private static int balance = 100;

    public static String getBalance() {
        return String.format("{\"balance\":\"%d\"}", balance);
    }

    public static String deposit(int amount) {
        balance += amount;
        return getBalance();
    }

    public static String withdraw(int amount) {
        balance -= amount;
        return getBalance();
    }
}
