import java.io.Serializable;
import java.util.*;

public class Trip implements Serializable {

    private List<Expense> expenseList;
    private List<Payment> paymentList;
    private Map<Integer, String> memberID;
    private Map<Integer, Map<Integer, Integer>> balance;


    public Trip(Map<Integer, String> memberID) {
        this.memberID = memberID;
        this.balance = new HashMap<>();
        this.paymentList = new ArrayList<>();
        this.expenseList = new ArrayList<>();

    }

    public boolean addExpense(Expense expense) {

        int payerID = expense.getPayer();
        int amount = expense.getAmount();
        List<Integer> userList = expense.getUsers();

        if (amount <= 0 || (!memberID.containsKey(payerID)) || !userList.contains(payerID)) {
            return false;
        }

        for (int oneUser : userList) {
            if (!memberID.containsKey(oneUser)) {
                return false;
            }
        }

        for (int i = 0; i < userList.size(); i++) {
            int userCheck = userList.get(i);
            for (int j = i + 1; j < userList.size(); j++) {
                if (userCheck == userList.get(j)) {
                    return false;
                }
            }
        }

        int participants = userList.size();
        int share = amount/participants;
        int remainder = amount % participants;

        for(int i = 0; i < participants; i++) {

            share = amount/participants;

            if(i < remainder) {
                share +=1;
            }

            if(userList.get(i) != payerID) {

                if(payerID < userList.get(i)) {
                    if(!balance.containsKey(payerID)){
                        balance.put(payerID, new HashMap<>());
                        balance.get(payerID).put(userList.get(i), share);
                    }
                    else {
                        int oldShare = balance.get(payerID).getOrDefault(userList.get(i), 0);
                        share+=oldShare;
                        balance.get(payerID).put(userList.get(i), share);
                    }

                }
                else {
                    if(!balance.containsKey(userList.get(i))){
                        balance.put(userList.get(i), new HashMap<>());
                        balance.get(userList.get(i)).put(payerID, share * -1);
                    }
                    else {
                        int oldShare = balance.get(userList.get(i)).getOrDefault(payerID, 0);
                        share-=oldShare;
                        balance.get(userList.get(i)).put(payerID, share * -1);
                    }

                }

            }

        }

        expenseList.add(expense);
        return true;

    }




}
