import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

public class Trip implements Serializable {

    private List<Expense> expenseList;
    private List<Payment> paymentList;
    private Map<Integer, String> memberID;
    private Map<Integer, Map<Integer, Integer>> balance;
    private String tripName;


    public Trip(String tripName,Map<Integer, String> memberID) {
        this.tripName = tripName;
        this.memberID = memberID;
        this.balance = new HashMap<>();
        this.paymentList = new ArrayList<>();
        this.expenseList = new ArrayList<>();

    }

    public String getTripName() {
        return tripName;
    }
    public Map<Integer,String> getMemberIDs () {
        return memberID;
    }

    public boolean addExpense(Expense expense) {

        int payerID = expense.getPayer();
        int amount = expense.getAmount();
        List<Integer> userList = expense.getUsers();

        if (amount <= 0) {
            IO.println();
            IO.println("EXPENSE UNSUCCESSFUL...Expense amount has to be positive");
            IO.println();

            return false;
        }

        for (int oneUser : userList) {
            if (!memberID.containsKey(oneUser)) {
                IO.println();
                IO.println("EXPENSE UNSUCCESSFUL...The ID(s) provided for the split do not belong to trip members");
                IO.println();

                return false;
            }
        }

        if ((!memberID.containsKey(payerID))) {
            IO.println();
            IO.println("EXPENSE UNSUCCESSFUL...The ID for the payer doesn't belong to any trip member");
            IO.println();

            return false;
        }

        if(!userList.contains(payerID)) {
            IO.println();
            IO.println("EXPENSE UNSUCCESSFUL...The split list doesn't include the selected payer");
            IO.println();

            return false;
        }


        for (int i = 0; i < userList.size(); i++) {
            int userCheck = userList.get(i);
            for (int j = i + 1; j < userList.size(); j++) {
                if (userCheck == userList.get(j)) {
                    IO.println();
                    IO.println("EXPENSE UNSUCCESSFUL...Duplicate members selected for the split");
                    IO.println();

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
        IO.println();
        IO.println("EXPENSE SUCCESSFULLY ADDED");
        IO.println("--------------------------");
        IO.println("The current balances are:");
        displayBalance();

        return true;

    }

    public boolean addPayment(Payment payment) {

        int amount = payment.getAmount();
        int payerID = payment.getPayer();
        int receiverID = payment.getReceiver();

        if (amount <= 0) {
            IO.println();
            IO.println("PAYMENT UNSUCCESSFUL... Payment amount was not positive");
            IO.println("Enter a positive amount");
            IO.println();

            return false;
        }
        if (payerID == receiverID) {
            IO.println();
            IO.println("PAYMENT UNSUCCESSFUL...Payer can't be Receiver");
            IO.println();

            return false;
        }

        if ((!memberID.containsKey(payerID)) || !memberID.containsKey(receiverID)) {
            IO.println();
            IO.println("PAYMENT UNSUCCESSFUL...The ID(s) provided do not belong to trip members");
            IO.println();

            return false;
        }

        if (receiverID < payerID) {
            if (balance.containsKey(receiverID)) {
                if (balance.get(receiverID).containsKey(payerID)) {
                    int oldShare = balance.get(receiverID).get(payerID);
                    if (oldShare <= 0) {
                        IO.println();
                        IO.println("PAYMENT UNSUCCESSFUL...No balance was owed");
                        IO.println();

                        return false;
                    }
                    if (amount > oldShare) {
                        IO.println();
                        IO.println("PAYMENT UNSUCCESSFUL...Payment amount is more than whats owed");
                        IO.println();

                        return false;
                    }
                    int newShare = oldShare - amount;
                    balance.get(receiverID).put(payerID, newShare);
                    paymentList.add(payment);
                    IO.println();
                    IO.println("PAYMENT SUCCESSFULLY ADDED");
                    IO.println("--------------------------");
                    IO.println("The current balances are:");
                    displayBalance();
                    return true;

                } else {
                    IO.println();
                    IO.println("PAYMENT UNSUCCESSFUL...No balance was owed");
                    IO.println();
                    return false;
                }
            } else {
                IO.println();
                IO.println("PAYMENT UNSUCCESSFUL...No balance was owed");
                IO.println();
                return false;
            }
        } else {
            if (balance.containsKey(payerID)) {
                if (balance.get(payerID).containsKey(receiverID)) {
                    int oldShare = balance.get(payerID).get(receiverID);
                    if (oldShare >= 0) {
                        IO.println();
                        IO.println("PAYMENT UNSUCCESSFUL...No balance was owed");
                        IO.println();
                        return false;
                    }
                    if (amount > (oldShare * -1)) {
                        IO.println();
                        IO.println("PAYMENT UNSUCCESSFUL...Payment amount is more than whats owed");
                        IO.println();
                        return false;
                    }
                    int newShare = oldShare + amount;
                    balance.get(payerID).put(receiverID, newShare);
                    paymentList.add(payment);
                    IO.println();
                    IO.println("PAYMENT SUCCESSFULLY ADDED");
                    IO.println("--------------------------");
                    IO.println("The current balances are:");
                    displayBalance();
                    return true;

                } else {
                    IO.println();
                    IO.println("PAYMENT UNSUCCESSFUL...No balance was owed");
                    IO.println();
                    return false;
                }
            } else {
                IO.println();
                IO.println("PAYMENT UNSUCCESSFUL...No balance was owed");
                IO.println();
                return false;
            }
        }
    }


    public void displayBalance() {

        final int[] counter = {0};
        final int[] counter1 = {0};

        if (balance.isEmpty()) {
            IO.println("There are no current dues");
            return;
        }

        balance.forEach((key, value) -> {
            value.forEach((key1, value1) -> {
                counter1[0] += 1;
                double newValue = value1 / 100.0;


                if (value1 > 0) {
                    String outputValue = String.format("%.2f", newValue);
                    IO.println(memberID.get(key1) + " owes " + memberID.get(key) + " $" + outputValue);
                } else if (value1 < 0) {
                    newValue = newValue * -1;
                    String outputValue = String.format("%.2f", newValue);
                    IO.println(memberID.get(key) + " owes " + memberID.get(key1) + " $" + outputValue);
                } else {
                    counter[0]++;
                }
            });
        });

        if (counter[0] == counter1[0]) {
            IO.println("There are no leftover dues");
        }
        IO.println();
    }

    public void finalSummary() {

        IO.println("\t\t\t\t\tThe final summary for: " + tripName);
        IO.println();
        IO.println("Number of members in the trip: " + memberID.size());
        IO.println("Member names: ");

        memberID.forEach((id, name) -> {
            IO.print(name + " ");
        });
        IO.println();
        IO.println();

        IO.println("Total number of expenses made: " + expenseList.size());
        IO.println("Total number of payments made: " + paymentList.size());
        IO.println();

        int expenseMoney = 0;
        for(int i = 0; i<expenseList.size(); i++) {
            expenseMoney+=expenseList.get(i).getAmount();

        }
        double newExpenseMoney = expenseMoney/100.0;
        IO.println(String.format("Total money spent on expenses: %.2f", newExpenseMoney));

        int paymentMoney = 0;
        for(int i = 0; i<paymentList.size(); i++) {
            paymentMoney+=paymentList.get(i).getAmount();
        }
        double newPaymentMoney = paymentMoney/100.0;
        IO.println(String.format("Total money spent on payments: %.2f", newPaymentMoney));
        IO.println();

        LocalDate date = null;
        LocalDate maxDate = null;
        int maxCount = 0;

        int dateCount = 0;

        for(int i = 0; i<expenseList.size(); i++) {
            date = expenseList.get(i).getDate();

            for (int j = 0; j<expenseList.size(); j++) {
                if(expenseList.get(j).getDate().isEqual(date)) {
                    dateCount++;
                }
            }
            if(dateCount>maxCount) {
                maxCount = dateCount;
                maxDate = expenseList.get(i).getDate();
            }
            dateCount = 0;
        }


        IO.println("The date with the most expenses was " + maxDate + " with a total of " + maxCount + " expenses");
        IO.println();
        IO.println("The remaining leftover balances are: ");

        displayBalance();
        IO.println();
        IO.println("Hope you had a fun trip!");


    }

    }
