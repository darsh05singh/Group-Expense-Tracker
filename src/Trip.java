import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

/*
The Trip class store the data for one trip.
This classes process and validates all the expenses, payments, and contains method to show balance and the final summary.
*/

public class Trip implements Serializable {
    /*
     The expenseList and paymentList store all successful expenses and payments.
     The memberID map stores the ID associated with each member of the Trip.
     The balance nested map keeps track of one members net balance with another member
     */
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

    public void addExpense(Expense expense) {

        int payerID = expense.getPayer();
        int amount = expense.getAmount();
        List<Integer> userList = expense.getUsers();

        if (amount <= 0) {
            IO.println();
            IO.println("EXPENSE UNSUCCESSFUL...Expense amount has to be positive");
            IO.println();

            return;
        }

        for (int oneUser : userList) {
            if (!memberID.containsKey(oneUser)) {
                IO.println();
                IO.println("EXPENSE UNSUCCESSFUL...The ID(s) provided for the split do not belong to trip members");
                IO.println();

                return;
            }
        }

        if ((!memberID.containsKey(payerID))) {
            IO.println();
            IO.println("EXPENSE UNSUCCESSFUL...The ID for the payer doesn't belong to any trip member");
            IO.println();

            return;
        }

        if(!userList.contains(payerID)) {
            IO.println();
            IO.println("EXPENSE UNSUCCESSFUL...The split list doesn't include the selected payer");
            IO.println();

            return;
        }


        for (int i = 0; i < userList.size(); i++) {
            int userCheck = userList.get(i);
            for (int j = i + 1; j < userList.size(); j++) {
                if (userCheck == userList.get(j)) {
                    IO.println();
                    IO.println("EXPENSE UNSUCCESSFUL...Duplicate members selected for the split");
                    IO.println();

                    return;
                }
            }
        }


        int participants = userList.size();
        int share = amount / participants;
        int remainder = amount % participants;

        for (int i = 0; i < participants; i++) {

            /*
              To account for uneven splits (Ex. $10  among 3 members), every participant starts with an equal amount
              and then for every participant in the order they are in the list, their share increases by 1 cent until the leftovers are covered.

              For Example, in a $10 (1000 cents) three-way split, the first user will start with 333 cents whose share will add up to 334($3.34),
              and now since the remainder has been covered, the rest of the users will retain the original 333($3.33) share.
             */

            share = amount / participants;

            if (i < remainder) {
                share += 1;
            }

            //The payer doesn't owe anybody anything, which is why they are excluded when determining dues

            if (userList.get(i) != payerID) {

                /*

                To avoid counting a balance between two members twice, the balance map is split
                such that the outer key is the member with the smaller ID, the inner nested key is the member with larger ID,
                and the value of the inner nested map is the two members' net balance.

                This is so that if between two member, A(ID 1) and B (ID 2), share a net balance, they aren't
                stored twice depending on who was the payer, for example, A-> B-> Net Balance and B->A-> Net Balance.

                Because the IDs are stored in the order of small to large, a positive net balance would represent
                a receivable for the smaller ID and a payable for the larger ID.
                Hence, if the payerID is smaller, the new share from the split is added to the old share.

                Similarly, a negative net balance represents a receivable for the larger ID and a payable for the smaller ID.
                Hence, if the payerID is larger, new share is subtracted from the old share and the final amount is multiplied by * -1.

                EX. Between two members A(ID 1) and B(ID 2), if B was the payer and A owes them $20 while their current net balance is 0.
                     20(New Share) - 0(Old Share) = 20 -> 20*-1 = -20(The new net balance between A and B)


                 */

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

        return;

    }

    public void addPayment(Payment payment) {

        int amount = payment.getAmount();
        int payerID = payment.getPayer();
        int receiverID = payment.getReceiver();

        if (amount <= 0) {
            IO.println();
            IO.println("PAYMENT UNSUCCESSFUL... Payment amount was not positive");
            IO.println("Enter a positive amount");
            IO.println();

            return;
        }
        if (payerID == receiverID) {
            IO.println();
            IO.println("PAYMENT UNSUCCESSFUL...Payer can't be Receiver");
            IO.println();

            return;
        }

        if ((!memberID.containsKey(payerID)) || !memberID.containsKey(receiverID)) {
            IO.println();
            IO.println("PAYMENT UNSUCCESSFUL...The ID(s) provided do not belong to trip members");
            IO.println();

            return;
        }




        if (receiverID < payerID) {

            /*
                It is checked that the smaller ID has a record in the balance map,
                then it is checked if the smaller ID has a record with the larger ID.

                If either of the two ends up being false, there is no balance owed.
             */

            if (balance.containsKey(receiverID)) {
                if (balance.get(receiverID).containsKey(payerID)) {

                    /*
                        The program makes sure one doesn't end up paying somebody if they don't owe them,
                        and that if they do owe, they don't pay more than what's owed.

                     */

                    int oldShare = balance.get(receiverID).get(payerID);
                    if (oldShare <= 0) {
                        IO.println();
                        IO.println("PAYMENT UNSUCCESSFUL...No balance was owed");
                        IO.println();

                        return;
                    }
                    if (amount > oldShare) {
                        IO.println();
                        IO.println("PAYMENT UNSUCCESSFUL...Payment amount is more than what's owed");
                        IO.println();

                        return;
                    }
                    int newShare = oldShare - amount;
                    balance.get(receiverID).put(payerID, newShare);
                    paymentList.add(payment);
                    IO.println();
                    IO.println("PAYMENT SUCCESSFULLY ADDED");
                    IO.println("--------------------------");
                    IO.println("The current balances are:");
                    displayBalance();
                    return;

                } else {
                    IO.println();
                    IO.println("PAYMENT UNSUCCESSFUL...No balance was owed");
                    IO.println();
                    return;
                }
            } else {
                IO.println();
                IO.println("PAYMENT UNSUCCESSFUL...No balance was owed");
                IO.println();
                return;
            }
        } else {
            if (balance.containsKey(payerID)) {
                if (balance.get(payerID).containsKey(receiverID)) {
                    /*

                    Since the payer is the smaller ID here, the two members must have a negative net balance(an amount payable to the receiver),
                    otherwise the receiver would be the one that owes the payer.

                     */
                    int oldShare = balance.get(payerID).get(receiverID);
                    if (oldShare >= 0) {
                        IO.println();
                        IO.println("PAYMENT UNSUCCESSFUL...No balance was owed");
                        IO.println();
                        return;
                    }
                    if (amount > (oldShare * -1)) {
                        IO.println();
                        IO.println("PAYMENT UNSUCCESSFUL...Payment amount is more than whats owed");
                        IO.println();
                        return;
                    }
                    int newShare = oldShare + amount;
                    balance.get(payerID).put(receiverID, newShare);
                    paymentList.add(payment);
                    IO.println();
                    IO.println("PAYMENT SUCCESSFULLY ADDED");
                    IO.println("--------------------------");
                    IO.println("The current balances are:");
                    displayBalance();
                    return;

                } else {
                    IO.println();
                    IO.println("PAYMENT UNSUCCESSFUL...No balance was owed");
                    IO.println();
                    return;
                }
            } else {
                IO.println();
                IO.println("PAYMENT UNSUCCESSFUL...No balance was owed");
                IO.println();
                return;
            }
        }
    }


    public void displayBalance() {


        if (balance.isEmpty()) {
            IO.println("There are no current dues");
            return;
        }

         /*
            These variables are created to see if all the existing balances between members are settled.
            An array is used and declared final because Java requires such variables to be final or effectively final inside the lambda.
         */

        final int[] counter = {0};
        final int[] counter1 = {0};



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
            IO.println("All the current balances are settled");
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
        IO.println(String.format("Total money spent on expenses: $%.2f", newExpenseMoney));

        int paymentMoney = 0;
        for(int i = 0; i<paymentList.size(); i++) {
            paymentMoney+=paymentList.get(i).getAmount();
        }
        double newPaymentMoney = paymentMoney/100.0;
        IO.println(String.format("Total money spent on payments: $%.2f", newPaymentMoney));
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
