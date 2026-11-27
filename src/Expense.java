import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

/*
The Expense class stores the information for one individual Expense
 */

public class Expense implements Serializable {

    //The amount field is taken as an int to avoid double rounding issues
    // An Integer list is included in the fields because an Expense can be shared among multiple members and this stores their IDs

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
