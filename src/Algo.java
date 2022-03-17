import java.util.ArrayList;
import java.util.Random;

public class Algo {

    private int studentCount = 20;
    private int numberOfProjects = 5;
    private final Random rand = new Random();
    // student count = number of rows, so student 1 = row 0
    private final ArrayList<ArrayList<Integer>> bids = new ArrayList<>(studentCount);

    // each project holds the id of the student in one of three arrays representing
    // whether it was the student's first, second or third choice
    private final ArrayList<ArrayList<ArrayList<Integer>>> project_bids = new ArrayList<>(numberOfProjects);

    // represents the suggested teams (each row is a project and the ints are the student ids)
    private final ArrayList<ArrayList<Integer>> suggestedTeams = new ArrayList<>();

    // represents the students who have not been assigned to a (suggested) team
    private final ArrayList<Integer> availableStudents = new ArrayList<>();

    // represents the students who have already been assigned to a (suggested) team
    private final ArrayList<Integer> assignedStudents = new ArrayList<>();

    // represents the projects that have already been filled
    private final ArrayList<Integer> filledProjects = new ArrayList<>();

    // represents the projects that did not have enough bids to be filled
    private final ArrayList<Integer> unpopularProjects = new ArrayList<>();

    public Algo() {}

    public Algo(int studentCount, int numberOfProjects) {
        this.studentCount = studentCount;
        this.numberOfProjects = numberOfProjects;
    }

    public void run() {

        setRandomBids();

        System.out.println("\nBids");
        for (ArrayList<Integer> bid : bids) {
            System.out.println(bid);
        }

        setProjectBids();

        System.out.println("\nProjects Bids");
        for (ArrayList<ArrayList<Integer>> project_bid : project_bids) {
            System.out.println(project_bid);
        }

        // first init outer array list
        for (int k = 0; k  < numberOfProjects; k++) {
            suggestedTeams.add(new ArrayList<>());
        }

        suggestTeams();

        System.out.println("\nSuggested Teams");
        for (ArrayList<Integer> team : suggestedTeams) {
            System.out.println(team);
        }


    }

    private void suggestTeams() {

        int leastFirstChoiceProject = findLeastNumberOfFirstChoiceBids();

        if (leastFirstChoiceProject == -1) {

            assignRemainingStudents();

            return;
        }

        int choice = 0;
        while (suggestedTeams.get(leastFirstChoiceProject).size() < 4 && choice < 3) {

            for (Integer student : project_bids.get(leastFirstChoiceProject).get(choice)) {

                if (suggestedTeams.get(leastFirstChoiceProject).size() < 4) {

                    if (! assignedStudents.contains(student)) {
                        suggestedTeams.get(leastFirstChoiceProject).add(student);
                        assignedStudents.add(student);
                        availableStudents.remove(student);

                        if (suggestedTeams.get(leastFirstChoiceProject).size() == 4) {
                            filledProjects.add(leastFirstChoiceProject);
                        }
                    }

                }

            }

            choice++;

            if (choice == 3 && suggestedTeams.get(leastFirstChoiceProject).size() < 4) {

                // not enough bids for this project, remove it and remove any students
                // placed in it from the assignedStudents list
                for (Integer student : suggestedTeams.get(leastFirstChoiceProject)) {
                    assignedStudents.remove(student);
                    availableStudents.add(student);
                }
                unpopularProjects.add(leastFirstChoiceProject);
            }
        }

        suggestTeams();

    }

    private void assignRemainingStudents() {

        ArrayList<Integer> studentsToRemove = new ArrayList<>();

        for (ArrayList<Integer> team : suggestedTeams) {

            if (team.size() < 4) {
                for (Integer student : availableStudents) {

                    studentsToRemove = new ArrayList<>();

                    if (team.size() < 4) {
                        team.add(student);
                        assignedStudents.add(student);
                        studentsToRemove.add(student);
                    }
                }
            }
        }

        for (Integer student : studentsToRemove) {
            availableStudents.remove(student);
        }

    }

    private int findLeastNumberOfFirstChoiceBids() {

        int counter = -1;
        int currentLow = 100;
        int currentLowestIndex = -1;

        for (ArrayList<ArrayList<Integer>> project_bid : project_bids) {

            counter++;

            if (project_bid.get(0).size() < currentLow
                    && ! filledProjects.contains(counter)
                    && ! unpopularProjects.contains(counter) ) {

                currentLow = project_bid.get(0).size();
                currentLowestIndex = counter;

            }
        }

        return currentLowestIndex;
    }

    private void setProjectBids() {

        // first init outer array list
        for (int k = 0; k < numberOfProjects; k++) {

            project_bids.add(new ArrayList<>());

            // each project needs three slots to place the student's ids
            // that chose them as their first, second or third choice
            for (int m = 0; m < 3; m++) {

                project_bids.get(k).add(new ArrayList<>());

            }

        }

        // go through all bids which look like a list of these -> [3, 1, 5]
        for (int k = 0; k < bids.size(); k++) {

            for (int i = 0; i < 3; i++) {

                // the .get(bids.get(k).get(i)) get the correct index of the bid based on the project id
                // then .get(i) either gets the 1st, 2nd or 3rd array of the project bid
                // finally .add(k) assigns the student's id (row number) to the above array
                project_bids.get(bids.get(k).get(i)).get(i).add(k);

            }
        }

    }

    private void setRandomBids() {

        // first init outer array list and set available students ArrayList
        for (int k = 0; k < studentCount; k++) {
            bids.add(new ArrayList<>());
            availableStudents.add(k);
        }

        for (int i = 0; i < studentCount; i++) {

            // create new array list of projects
            ArrayList<Integer> list = new ArrayList<>(numberOfProjects);
            for (int j = 0; j < numberOfProjects; j++) {
                list.add(j);
            }

            // randomly select three projects for each student
            while (list.size() > numberOfProjects - 3) { 
                Integer index = rand.nextInt(numberOfProjects);
                if (! bids.get(i).contains(index)) {
                    bids.get(i).add(index);
                    list.remove(index);
                }
            }
        }
    }


}
