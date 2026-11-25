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

        if ((!memberID.containsKey(payerID)) || !userList.contains(payerID)) {
            IO.println("Wrong member selected");
            return false;
        }

        if(amount <= 0) {
            IO.println("Amount has to be positive");
            return false;
        }

        for (int oneUser : userList) {
            if (!memberID.containsKey(oneUser)) {
                IO.println("Wrong member/s selected");
                return false;
            }
        }

        for (int i = 0; i < userList.size(); i++) {
            int userCheck = userList.get(i);
            for (int j = i + 1; j < userList.size(); j++) {
                if (userCheck == userList.get(j)) {
                    IO.println("Duplicate members selected");
                    return false;
                }
            }
        }

        int participants = userList.size();
        int share = amount / participants;
        int remainder = amount % participants;

        for (int i = 0; i < participants; i++) {

            share = amount / participants;

            if (i < remainder) {
                share += 1;
            }

            if (userList.get(i) != payerID) {

                if (payerID < userList.get(i)) {
                    if (!balance.containsKey(payerID)) {
                        balance.put(payerID, new HashMap<>());
                        balance.get(payerID).put(userList.get(i), share);
                    } else {
                        int oldShare = balance.get(payerID).getOrDefault(userList.get(i), 0);
                        share += oldShare;
                        balance.get(payerID).put(userList.get(i), share);
                    }

                } else {
                    if (!balance.containsKey(userList.get(i))) {
                        balance.put(userList.get(i), new HashMap<>());
                        balance.get(userList.get(i)).put(payerID, share * -1);
                    } else {
                        int oldShare = balance.get(userList.get(i)).getOrDefault(payerID, 0);
                        share -= oldShare;
                        balance.get(userList.get(i)).put(payerID, share * -1);
                    }

                }

            }

        }

        expenseList.add(expense);
        IO.println("Expense successfully added");
        return true;

    }

    public boolean addPayment(Payment payment) {

        int amount = payment.getAmount();
        int payerID = payment.getPayer();
        int receiverID = payment.getReceiver();

        if (amount <= 0) {
            IO.println("Enter a positive amount");
            return false;
        }
        if (payerID == receiverID) {
            IO.println("Can't pay yourself");
            return false;
        }

        if ((!memberID.containsKey(payerID)) || !memberID.containsKey(receiverID)) {
            IO.println("Wrong member selected");
            return false;
        }

        if (receiverID < payerID) {
            if (balance.containsKey(receiverID)) {
                if (balance.get(receiverID).containsKey(payerID)) {
                    int oldShare = balance.get(receiverID).get(payerID);
                    if (oldShare <= 0) {
                        IO.println("Nothing owed");
                        return false;
                    }
                    if (amount > oldShare) {
                        IO.println("Payment is more than whats owed");
                        return false;
                    }
                    int newShare = oldShare - amount;
                    balance.get(receiverID).put(payerID, newShare);
                    paymentList.add(payment);
                    IO.println("Payment successfully added");
                    return true;

                } else {
                    IO.println("Nothing owed");
                    return false;
                }
            } else {
                IO.println("Nothing owed");
                return false;
            }
        } else {
            if (balance.containsKey(payerID)) {
                if (balance.get(payerID).containsKey(receiverID)) {
                    int oldShare = balance.get(payerID).get(receiverID);
                    if (oldShare >= 0) {
                        IO.println("Nothing owed");
                        return false;
                    }
                    if (amount > (oldShare * -1)) {
                        IO.println("Payment is more than whats owed");
                        return false;
                    }
                    int newShare = oldShare + amount;
                    balance.get(payerID).put(receiverID, newShare);
                    paymentList.add(payment);
                    IO.println("Payment successfully added");
                    return true;

                } else {
                    IO.println("Nothing owed");
                    return false;
                }
            } else {
                IO.println("Nothing owed");
                return false;
            }
        }

    }

}
