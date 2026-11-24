import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

public class Payment implements Serializable {

    private int amount;
    private int payer;
    private int receiver;
    private LocalDate date;


    public Payment (int payer, int receiver, int amount, LocalDate date) {
        this.amount = amount;
        this.payer = payer;
        this.receiver = receiver;
        this.date = date;
    }

    public int getAmount() {
        return amount;
    }
    public int getPayer() {
        return payer;
    }
    public int getReceiver() {
        return receiver;
    }
    public LocalDate getDate() {
        return date;
    }





}
