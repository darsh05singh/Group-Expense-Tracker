import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

public class Expense implements Serializable {

    private int amount;
    private int payer;
    private LocalDate date;
    private List<Integer> users;



    public Expense (int payer, List<Integer> users, int amount, LocalDate date) {
        this.payer = payer;
        this.amount = amount;
        this.users = users;
        this.date = date;
    }


    public int getAmount() {
        return amount;
    }
    public int getPayer() {
        return payer;
    }
    public List<Integer> getUsers() {
        return users;
    }
    public LocalDate getDate() {
        return date;
    }



}
