import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

public class Expense implements Serializable {

    private int amount;
    private int payer;
    private LocalDate date;
    private List<Integer> members;



    public Expense (int payer, List<Integer> members, int amount, LocalDate date) {
        this.payer = payer;
        this.amount = amount;
        this.members = members;
        this.date = date;
    }


    public int getAmount() {
        return amount;
    }
    public int getPayer() {
        return payer;
    }
    public List<Integer> getMembers() {
        return members;
    }
    public LocalDate getDate() {
        return date;
    }



}
