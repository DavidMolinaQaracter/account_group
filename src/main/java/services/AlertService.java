package services;

public class AlertService {
    public static void sendLongBalanceAlert(long accountId) {
        System.out.println("Long balance alert correctly sent for account " + accountId + ".");
    }
}
