import java.io.*;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.time.LocalDate;


public class Main {
    public static void main(String[] args) {

        /*
        The main method runs the command-line interface and stores the user's data.

         Most of the user input validation in the main method is to ensure users entered the write data type as their input,
         while the Trip class handles majority of the logical validation for expenses and payments such as rejecting a negative payment amount.

         This separation is so that the Trip class works on its own without much of the filtering from the main class,
         and it allows for easier testing when checking to see if Trip class works properly.

         */

        File tripFile = new File("TripData.ser");
        if (!tripFile.exists()) {
            IO.println("Welcome to the Group Expense Tracker\n");

            String tripName = IO.readln("Enter the trip name: ");
            while (tripName.equals("")) {
                IO.println("Nothing entered, try again");
                tripName = IO.readln("Enter the trip name: ");
            }


            int numMembers = 0;
            while (true) {
                try {
                    numMembers = Integer.parseInt(IO.readln("Please enter the number of members in the trip: "));
                    if (numMembers <= 1) {
                        throw new IllegalArgumentException();
                    }
                    break;

                } catch (NumberFormatException e) {
                    IO.println("Wrong input, please enter a positive whole number greater than one");
                } catch (IllegalArgumentException e) {
                    IO.println("Please enter a positive whole number greater than one");
                }
            }
            IO.println();

            Map<Integer, String> memberMap = new HashMap<>();
            Map<String, Integer> personalBudgetMap = new HashMap<>();

            for (int i = 1; i <= numMembers; i++) {
                String name = IO.readln("Please enter member " + i + " name: ");
                while (name.equals("")) {
                    IO.println("Nothing entered, try again");
                    name = IO.readln("Please enter member " + i + " name: ");
                }
                double personalBudget = 0.0;
                int convertedPersonalBudget = 0;
                while (true) {
                    try{
                        personalBudget = Double.parseDouble(IO.readln("Please enter the personal budget for " + name + ": $"));
                        if(personalBudget <= 0) {
                            throw new IllegalArgumentException();
                        }
                        break;
                    }
                    catch (NumberFormatException e) {
                        IO.println("Please enter a valid number. Ensure no commas are present.");
                    }
                    catch (IllegalArgumentException e) {
                        IO.println("Please enter a positive budget");
                    }
                }
                convertedPersonalBudget = (int) (personalBudget *100);
                memberMap.put(i, name);
                personalBudgetMap.put(name,convertedPersonalBudget);

            }
            IO.println();


            /*
                The total group budget should be at max the sum of every single personal budget.
                An array is used to calculate the total and declared final because Java requires such variables to be final or effectively final inside the lambda.
             */

            final int [] totalPersonalBudgets = {0};

            personalBudgetMap.forEach((memberName, budget) -> {
                totalPersonalBudgets[0] += budget;
            });
            double convertedTotalPersonal = totalPersonalBudgets[0]/100.0;


            double groupBudget = 0;

            while (true) {
                try {
                    groupBudget = Double.parseDouble(IO.readln("Please enter the group budget for the trip: $"));
                    if (groupBudget <= 0) {
                        throw new IllegalArgumentException("Budget less than zero");
                    }
                    if(groupBudget > convertedTotalPersonal) {
                        throw new IllegalArgumentException("Budget greater than personal budgets");
                    }
                    break;

                } catch (NumberFormatException e) {
                    IO.println("Please enter a valid number. Ensure no commas are present.");
                } catch (IllegalArgumentException e) {
                    if(e.getMessage().equals("Budget less than zero")) {
                        IO.println("Please enter a positive positive budget");
                    }
                    else if(e.getMessage().equals("Budget greater than personal budgets")) {
                        IO.println("The group budget is larger than the sum of all personal budgets...Please try again");
                    }

                }
            }
            int convertedGroupBudget = (int) (groupBudget*100.0);

            Trip trip = new Trip(tripName, memberMap, convertedGroupBudget, personalBudgetMap);


            while (true) {
                IO.println();
                IO.println("Please select one of the following options:\n 1) Add Expense\n 2) Add Payment\n 3) View Balance Summary\n 4) View Group Budget\n 5) View Personal Budgets\n 6) Save and Exit\n 7) End Trip");

                int menuSelection = 0;
                while (true) {
                    try {
                        menuSelection = Integer.parseInt(IO.readln("Enter the integer next to your selection: "));
                        if (menuSelection < 1 || menuSelection > 7) {
                            throw new IllegalArgumentException();
                        }
                        break;
                    } catch (NumberFormatException e) {
                        IO.println("Wrong input, please try again");
                    } catch (IllegalArgumentException e) {
                        IO.println("Please a number between 1 and 5 for your selected option");
                    }
                }

                if (menuSelection == 1) {

                    double expenseAmount = 0;
                    IO.println();
                    while (true) {
                        try {
                            expenseAmount = Double.parseDouble(IO.readln("Enter the expense amount: $"));
                            break;
                        } catch (NumberFormatException e) {
                            IO.println("Please enter a valid number. Ensure no commas are present.");
                        }

                    }
                    int convertedExpense = (int) (expenseAmount * 100);

                    IO.println();
                    IO.println("Below are the trip members");
                    memberMap.forEach((id, name) -> {
                        IO.println("Name: " + name + " ID: " + id);
                    });
                    IO.println();

                    int numSplit = 0;
                    while (true) {
                        try {
                            numSplit = Integer.parseInt(IO.readln("Among how many members was the expense split (including the payer): "));

                            if (numSplit <= 0 || numSplit > numMembers) {
                                throw new IllegalArgumentException();
                            }
                            break;
                        } catch (NumberFormatException e) {
                            IO.println("Wrong input, please try again");
                        } catch (IllegalArgumentException e) {
                            IO.println("Out of range, please enter an integer in range");
                        }
                    }
                    IO.println();

                    List<Integer> sharedList = new ArrayList<>();
                    IO.println("Enter the ID for each member involved in the split (including the payer): ");
                    for (int i = 1; i <= numSplit; i++) {

                        while (true) {
                            try {
                                int splitMember = Integer.parseInt(IO.readln("Enter ID #" + i + " : "));
                                sharedList.add(splitMember);
                                break;
                            } catch (NumberFormatException e) {
                                IO.println("Wrong input, please try again");
                            }
                        }
                    }
                    IO.println();

                    int payerNumber = 0;

                    while (true) {
                        try {
                            payerNumber = Integer.parseInt(IO.readln("Enter the ID for the payer: "));

                            break;
                        } catch (NumberFormatException e) {
                            IO.println("Wrong input, please try again");
                        }
                    }
                    IO.println();


                    LocalDate date = null;

                    while (true) {
                        IO.println("Please enter the date of the expense in the following format YYYY-MM-DD");
                        try {
                            date = LocalDate.parse(IO.readln("Enter the date: "));
                            break;
                        } catch (DateTimeParseException e) {
                            IO.println("The format is wrong, try again");
                        }
                    }
                    IO.println();
                    Expense expense = new Expense(payerNumber, sharedList, convertedExpense, date);
                    trip.addExpense(expense);

                } else if (menuSelection == 2) {
                    IO.println();
                    IO.println("Below are the trip members");
                    memberMap.forEach((id, name) -> {
                        IO.println("Name: " + name + " ID: " + id);
                    });
                    IO.println();

                    IO.println("Enter below the IDs for the payer and the receiver");
                    int payerID = 0;
                    int receiverID = 0;
                    while (true) {
                        try {
                            payerID = Integer.parseInt(IO.readln("Payer ID: "));
                            receiverID = Integer.parseInt(IO.readln("Receiver ID: "));
                            break;
                        } catch (NumberFormatException e) {
                            IO.println("Invalid input, please try again");
                        }

                    }
                    IO.println();

                    double paymentAmount = 0;

                    while (true) {
                        try {
                            paymentAmount = Double.parseDouble(IO.readln("Enter the payment amount: $"));
                            break;
                        } catch (NumberFormatException e) {
                            IO.println("Please enter a valid number. Ensure no commas are present.");
                        }
                    }
                    IO.println();

                    int convertedPayment = (int) (paymentAmount * 100);


                    LocalDate date = null;

                    while (true) {
                        IO.println("Please enter the date of the payment in the following format YYYY-MM-DD");
                        try {
                            date = LocalDate.parse(IO.readln("Enter the date: "));
                            break;
                        } catch (DateTimeParseException e) {
                            IO.println("The format is wrong, try again");
                        }
                    }
                    IO.println();

                    Payment payment = new Payment(payerID, receiverID, convertedPayment, date);
                    trip.addPayment(payment);

                } else if (menuSelection == 3) {
                    IO.println();
                    IO.println();
                    IO.println("The current balances are:");
                    trip.displayBalance();

                } else if (menuSelection == 4) {
                    IO.println();
                    IO.println();
                    trip.showGroupBudget();

                }

                else if(menuSelection == 5) {
                    IO.println();
                    IO.println();
                    trip.showPersonalBudgets();

                }

                else if(menuSelection == 6) {

                    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(tripFile))) {
                        oos.writeObject(trip);
                        oos.flush();
                    }
                    catch (IOException e) {

                    }

                    break;

                }

                else if (menuSelection == 7) {

                    IO.println();
                    trip.finalSummary();
                    break;

                }

            }
        }
        else {
            Trip trip = null;

            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(tripFile))) {
                trip = (Trip) ois.readObject();


            }
            catch (ClassNotFoundException e) {

            }
            catch (EOFException e) {

            }
            catch (IOException e) {
                e.printStackTrace();

            }

            IO.println("Welcome Back to the Group Expense Tracker");
            IO.println("Here is the current balance summary for the trip: " + trip.getTripName());
            IO.println();
            IO.println();
            IO.println("The current balances are:");
            trip.displayBalance();
            IO.println();
            trip.showGroupBudget();
            IO.println();
            IO.println();
            trip.showPersonalBudgets();

            int numMembers = trip.getMemberIDs().size();

            while (true) {
                IO.println();
                IO.println("Please select one of the following options:\n 1) Add Expense\n 2) Add Payment\n 3) View Balance Summary\n 4) View Group Budget\n 5) View Personal Budgets\n 6) Save and Exit\n 7) End Trip");

                int menuSelection = 0;
                while (true) {
                    try {
                        menuSelection = Integer.parseInt(IO.readln("Enter the integer next to your selection: "));
                        if (menuSelection < 1 || menuSelection > 7) {
                            throw new IllegalArgumentException();
                        }
                        break;
                    } catch (NumberFormatException e) {
                        IO.println("Wrong input, please try again");
                    } catch (IllegalArgumentException e) {
                        IO.println("Please a number between 1 and 5 for your selected option");
                    }
                }

                if (menuSelection == 1) {

                    double expenseAmount = 0;
                    IO.println();
                    while (true) {
                        try {
                            expenseAmount = Double.parseDouble(IO.readln("Enter the expense amount: $"));
                            break;
                        } catch (NumberFormatException e) {
                            IO.println("Please enter a valid number. Ensure no commas are present.");
                        }

                    }
                    int convertedExpense = (int) (expenseAmount * 100);

                    IO.println();
                    IO.println("Below are the trip members");
                    trip.getMemberIDs().forEach((id, name) -> {
                        IO.println("Name: " + name + " ID: " + id);
                    });
                    IO.println();

                    int numSplit = 0;
                    while (true) {
                        try {
                            numSplit = Integer.parseInt(IO.readln("Among how many members was the expense split (including the payer): "));

                            if (numSplit <= 0 || numSplit > numMembers) {
                                throw new IllegalArgumentException();
                            }
                            break;
                        } catch (NumberFormatException e) {
                            IO.println("Wrong input, please try again");
                        } catch (IllegalArgumentException e) {
                            IO.println("Out of range, please enter an integer in range");
                        }
                    }
                    IO.println();

                    List<Integer> sharedList = new ArrayList<>();
                    IO.println("Enter the ID for each member involved in the split (including the payer): ");
                    for (int i = 1; i <= numSplit; i++) {

                        while (true) {
                            try {
                                int splitMember = Integer.parseInt(IO.readln("Enter ID #" + i + " : "));
                                sharedList.add(splitMember);
                                break;
                            } catch (NumberFormatException e) {
                                IO.println("Wrong input, please try again");
                            }
                        }
                    }
                    IO.println();

                    int payerNumber = 0;

                    while (true) {
                        try {
                            payerNumber = Integer.parseInt(IO.readln("Enter the ID for the payer: "));

                            break;
                        } catch (NumberFormatException e) {
                            IO.println("Wrong input, please try again");
                        }
                    }
                    IO.println();


                    LocalDate date = null;

                    while (true) {
                        IO.println("Please enter the date of the expense in the following format YYYY-MM-DD");
                        try {
                            date = LocalDate.parse(IO.readln("Enter the date: "));
                            break;
                        } catch (DateTimeParseException e) {
                            IO.println("The format is wrong, try again");
                        }
                    }
                    IO.println();
                    Expense expense = new Expense(payerNumber, sharedList, convertedExpense, date);
                    trip.addExpense(expense);

                } else if (menuSelection == 2) {
                    IO.println();
                    IO.println("Below are the trip members");
                    trip.getMemberIDs().forEach((id, name) -> {
                        IO.println("Name: " + name + " ID: " + id);
                    });
                    IO.println();

                    IO.println("Enter below the IDs for the payer and the receiver");
                    int payerID = 0;
                    int receiverID = 0;
                    while (true) {
                        try {
                            payerID = Integer.parseInt(IO.readln("Payer ID: "));
                            receiverID = Integer.parseInt(IO.readln("Receiver ID: "));
                            break;
                        } catch (NumberFormatException e) {
                            IO.println("Invalid input, please try again");
                        }

                    }
                    IO.println();

                    double paymentAmount = 0;

                    while (true) {
                        try {
                            paymentAmount = Double.parseDouble(IO.readln("Enter the payment amount: $"));
                            break;
                        } catch (NumberFormatException e) {
                            IO.println("Please enter a valid number. Ensure no commas are present.");
                        }
                    }
                    IO.println();

                    int convertedPayment = (int) (paymentAmount * 100);


                    LocalDate date = null;

                    while (true) {
                        IO.println("Please enter the date of the payment in the following format YYYY-MM-DD");
                        try {
                            date = LocalDate.parse(IO.readln("Enter the date: "));
                            break;
                        } catch (DateTimeParseException e) {
                            IO.println("The format is wrong, try again");
                        }
                    }
                    IO.println();

                    Payment payment = new Payment(payerID, receiverID, convertedPayment, date);
                    trip.addPayment(payment);

                } else if (menuSelection == 3) {
                    IO.println();
                    IO.println();
                    IO.println("The current balances are:");
                    trip.displayBalance();

                } else if (menuSelection == 4) {
                    IO.println();
                    IO.println();
                    trip.showGroupBudget();

                }
                else if(menuSelection == 5) {
                    IO.println();
                    IO.println();
                    trip.showPersonalBudgets();

                }

                else if(menuSelection == 6) {

                    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(tripFile))) {
                        oos.writeObject(trip);
                        oos.flush();
                    }
                    catch (IOException e) {

                    }

                    break;
                }

                else if (menuSelection == 7) {

                    IO.println();
                    trip.finalSummary();
                    tripFile.delete();
                    break;

                }

            }



        }

    }

}



