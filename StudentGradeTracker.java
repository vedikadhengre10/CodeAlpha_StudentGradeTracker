import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

// ─────────────────────────────────────────────
//  Student model
// ─────────────────────────────────────────────
class Student {
    private String name;
    private String rollNo;
    private ArrayList<Double> grades; // stores scores for each subject
    private String[] subjects = {"Math", "Science", "English", "History", "Computer"};

    public Student(String name, String rollNo, ArrayList<Double> grades) {
        this.name   = name;
        this.rollNo = rollNo;
        this.grades = grades;
    }

    // ── Getters ──────────────────────────────
    public String getName()             { return name; }
    public String getRollNo()           { return rollNo; }
    public ArrayList<Double> getGrades(){ return grades; }
    public String[] getSubjects()       { return subjects; }

    // ── Computed stats ───────────────────────
    public double getAverage() {
        double sum = 0;
        for (double g : grades) sum += g;
        return sum / grades.size();
    }

    public double getHighest() {
        double max = grades.get(0);
        for (double g : grades) if (g > max) max = g;
        return max;
    }

    public double getLowest() {
        double min = grades.get(0);
        for (double g : grades) if (g < min) min = g;
        return min;
    }

    public String getLetterGrade() {
        double avg = getAverage();
        if (avg >= 90) return "A";
        if (avg >= 80) return "B";
        if (avg >= 70) return "C";
        if (avg >= 60) return "D";
        return  "F";
    }

    public String getStatus() {
        return getAverage() >= 60 ? "PASS" : "FAIL";
    }
}

// ─────────────────────────────────────────────
//  Main application
// ─────────────────────────────────────────────
public class StudentGradeTracker {

    static ArrayList<Student> students = new ArrayList<>();
    static Scanner scanner = new Scanner(System.in);
    static final String[] SUBJECTS = {"Math", "Science", "English", "History", "Computer"};

    // ── Entry point ──────────────────────────
    public static void main(String[] args) {
        loadSampleData(); // pre-load a few sample students
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║      STUDENT GRADE TRACKER v1.0      ║");
        System.out.println("╚══════════════════════════════════════╝");

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Enter choice: ");
            System.out.println();
            switch (choice) {
                case 1 -> addStudent();
                case 2 -> viewAllStudents();
                case 3 -> searchStudent();
                case 4 -> updateGrades();
                case 5 -> deleteStudent();
                case 6 -> displaySummaryReport();
                case 7 -> displaySubjectAverages();
                case 8 -> displayRankings();
                case 0 -> { running = false; System.out.println("Goodbye!"); }
                default -> System.out.println("  ✗ Invalid option. Please try again.");
            }
        }
        scanner.close();
    }

    // ─────────────────────────────────────────
    //  Menu
    // ─────────────────────────────────────────
    static void printMenu() {
        System.out.println("\n┌──────────────────────────────┐");
        System.out.println("│           MAIN MENU          │");
        System.out.println("├──────────────────────────────┤");
        System.out.println("│  1. Add student              │");
        System.out.println("│  2. View all students        │");
        System.out.println("│  3. Search student           │");
        System.out.println("│  4. Update grades            │");
        System.out.println("│  5. Delete student           │");
        System.out.println("│  6. Summary report           │");
        System.out.println("│  7. Subject averages         │");
        System.out.println("│  8. Class rankings           │");
        System.out.println("│  0. Exit                     │");
        System.out.println("└──────────────────────────────┘");
    }

    // ─────────────────────────────────────────
    //  1. Add student
    // ─────────────────────────────────────────
    static void addStudent() {
        System.out.println("── Add New Student ─────────────────────");
        System.out.print("  Full name  : ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) { System.out.println("  ✗ Name cannot be empty."); return; }

        System.out.print("  Roll no.   : ");
        String roll = scanner.nextLine().trim();
        if (rollExists(roll)) { System.out.println("  ✗ Roll no. " + roll + " already exists."); return; }

        ArrayList<Double> grades = new ArrayList<>();
        System.out.println("  Enter scores for each subject (0 – 100):");
        for (String subject : SUBJECTS) {
            double score = readScore("    " + subject + ": ");
            grades.add(score);
        }

        students.add(new Student(name, roll, grades));
        System.out.println("\n  ✓ Student '" + name + "' added successfully!");
    }

    // ─────────────────────────────────────────
    //  2. View all students
    // ─────────────────────────────────────────
    static void viewAllStudents() {
        System.out.println("── All Students ────────────────────────");
        if (students.isEmpty()) { System.out.println("  No students found."); return; }
        printTableHeader();
        for (Student s : students) printStudentRow(s);
        printTableFooter();
    }

    // ─────────────────────────────────────────
    //  3. Search student
    // ─────────────────────────────────────────
    static void searchStudent() {
        System.out.println("── Search Student ──────────────────────");
        System.out.print("  Enter name or roll no.: ");
        String query = scanner.nextLine().trim().toLowerCase();

        boolean found = false;
        for (Student s : students) {
            if (s.getName().toLowerCase().contains(query) || s.getRollNo().equalsIgnoreCase(query)) {
                if (!found) { printTableHeader(); found = true; }
                printStudentRow(s);
                printDetailBlock(s);
            }
        }
        if (!found) System.out.println("  ✗ No student found matching '" + query + "'.");
        else printTableFooter();
    }

    // ─────────────────────────────────────────
    //  4. Update grades
    // ─────────────────────────────────────────
    static void updateGrades() {
        System.out.println("── Update Grades ───────────────────────");
        System.out.print("  Enter roll no.: ");
        String roll = scanner.nextLine().trim();
        Student s = findByRoll(roll);
        if (s == null) { System.out.println("  ✗ Student not found."); return; }

        System.out.println("  Updating grades for: " + s.getName());
        ArrayList<Double> updated = new ArrayList<>();
        for (int i = 0; i < SUBJECTS.length; i++) {
            System.out.print("    " + SUBJECTS[i] + " [current: " + s.getGrades().get(i) + "]: ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                updated.add(s.getGrades().get(i)); // keep old
            } else {
                try {
                    double val = Double.parseDouble(input);
                    if (val < 0 || val > 100) throw new NumberFormatException();
                    updated.add(val);
                } catch (NumberFormatException e) {
                    System.out.println("    ✗ Invalid score — keeping old value.");
                    updated.add(s.getGrades().get(i));
                }
            }
        }
        s.getGrades().clear();
        s.getGrades().addAll(updated);
        System.out.println("  ✓ Grades updated for " + s.getName() + ".");
    }

    // ─────────────────────────────────────────
    //  5. Delete student
    // ─────────────────────────────────────────
    static void deleteStudent() {
        System.out.println("── Delete Student ──────────────────────");
        System.out.print("  Enter roll no.: ");
        String roll = scanner.nextLine().trim();
        Student s = findByRoll(roll);
        if (s == null) { System.out.println("  ✗ Student not found."); return; }

        System.out.print("  Confirm delete '" + s.getName() + "'? (yes/no): ");
        String confirm = scanner.nextLine().trim();
        if (confirm.equalsIgnoreCase("yes")) {
            students.remove(s);
            System.out.println("  ✓ Student deleted.");
        } else {
            System.out.println("  Cancelled.");
        }
    }

    // ─────────────────────────────────────────
    //  6. Summary report
    // ─────────────────────────────────────────
    static void displaySummaryReport() {
        System.out.println("══════════════════════════════════════════");
        System.out.println("           CLASS SUMMARY REPORT           ");
        System.out.println("══════════════════════════════════════════");
        if (students.isEmpty()) { System.out.println("  No data available."); return; }

        double classAvg = 0, highest = Double.MIN_VALUE, lowest = Double.MAX_VALUE;
        Student topStudent = null, bottomStudent = null;
        int passing = 0;

        for (Student s : students) {
            double avg = s.getAverage();
            classAvg += avg;
            if (avg > highest) { highest = avg; topStudent = s; }
            if (avg < lowest)  { lowest  = avg; bottomStudent = s; }
            if (avg >= 60) passing++;
        }
        classAvg /= students.size();

        System.out.printf("  Total students : %d%n", students.size());
        System.out.printf("  Class average  : %.2f%n", classAvg);
        System.out.printf("  Highest avg    : %.2f  (%s)%n", highest, topStudent.getName());
        System.out.printf("  Lowest avg     : %.2f  (%s)%n", lowest,  bottomStudent.getName());
        System.out.printf("  Passing (≥60)  : %d / %d%n", passing, students.size());
        System.out.printf("  Failing (<60)  : %d / %d%n", students.size() - passing, students.size());

        System.out.println("\n  Grade distribution:");
        int[] dist = new int[5]; // A B C D F
        for (Student s : students) {
            String g = s.getLetterGrade();
            switch (g) {
                case "A" -> dist[0]++;
                case "B" -> dist[1]++;
                case "C" -> dist[2]++;
                case "D" -> dist[3]++;
                default  -> dist[4]++;
            }
        }
        String[] labels = {"A (90–100)", "B (80–89)", "C (70–79)", "D (60–69)", "F (0–59)"};
        for (int i = 0; i < 5; i++) {
            String bar = "█".repeat(dist[i] * 3);
            System.out.printf("    %-12s : %s %d%n", labels[i], bar.isEmpty() ? "—" : bar, dist[i]);
        }
        System.out.println("══════════════════════════════════════════");
    }

    // ─────────────────────────────────────────
    //  7. Subject averages
    // ─────────────────────────────────────────
    static void displaySubjectAverages() {
        System.out.println("── Subject Averages ────────────────────");
        if (students.isEmpty()) { System.out.println("  No data available."); return; }

        for (int i = 0; i < SUBJECTS.length; i++) {
            double sum = 0, hi = Double.MIN_VALUE, lo = Double.MAX_VALUE;
            String hiName = "", loName = "";
            for (Student s : students) {
                double g = s.getGrades().get(i);
                sum += g;
                if (g > hi) { hi = g; hiName = s.getName(); }
                if (g < lo) { lo = g; loName = s.getName(); }
            }
            double avg = sum / students.size();
            int bars = (int)(avg / 5);
            System.out.printf("  %-10s | %s %.1f%n", SUBJECTS[i], "▓".repeat(bars), avg);
            System.out.printf("             | Top: %-20s %.1f%n", hiName, hi);
            System.out.printf("             | Low: %-20s %.1f%n%n", loName, lo);
        }
    }

    // ─────────────────────────────────────────
    //  8. Class rankings
    // ─────────────────────────────────────────
    static void displayRankings() {
        System.out.println("── Class Rankings ──────────────────────");
        if (students.isEmpty()) { System.out.println("  No data available."); return; }

        ArrayList<Student> ranked = new ArrayList<>(students);
        ranked.sort(Comparator.comparingDouble(Student::getAverage).reversed());

        System.out.printf("  %-4s %-22s %-8s %-6s %-6s%n", "Rank", "Name", "Roll", "Avg", "Grade");
        System.out.println("  " + "─".repeat(50));
        for (int i = 0; i < ranked.size(); i++) {
            Student s = ranked.get(i);
            String medal = i == 0 ? "🥇" : i == 1 ? "🥈" : i == 2 ? "🥉" : "   ";
            System.out.printf("  #%-3d %-22s %-8s %-6.1f %s  %s%n",
                i + 1, s.getName(), s.getRollNo(), s.getAverage(), s.getLetterGrade(), medal);
        }
    }

    // ─────────────────────────────────────────
    //  Display helpers
    // ─────────────────────────────────────────
    static void printTableHeader() {
        System.out.printf("  %-6s %-22s %-6s %-6s %-6s %-6s %-6s %-8s %-7s%n",
            "Roll", "Name", "Math", "Sci", "Eng", "His", "Comp", "Avg", "Grade");
        System.out.println("  " + "─".repeat(75));
    }

    static void printStudentRow(Student s) {
        ArrayList<Double> g = s.getGrades();
        System.out.printf("  %-6s %-22s %-6.1f %-6.1f %-6.1f %-6.1f %-6.1f %-8.2f %-3s [%s]%n",
            s.getRollNo(), s.getName(),
            g.get(0), g.get(1), g.get(2), g.get(3), g.get(4),
            s.getAverage(), s.getLetterGrade(), s.getStatus());
    }

    static void printTableFooter() {
        System.out.println("  " + "─".repeat(75));
        System.out.println("  Total: " + students.size() + " student(s)");
    }

    static void printDetailBlock(Student s) {
        System.out.println("\n  ┌─ Detail ─────────────────────────────");
        System.out.println("  │  Name    : " + s.getName());
        System.out.println("  │  Roll    : " + s.getRollNo());
        for (int i = 0; i < SUBJECTS.length; i++)
            System.out.printf("  │  %-9s: %.1f%n", SUBJECTS[i], s.getGrades().get(i));
        System.out.printf("  │  Average : %.2f%n", s.getAverage());
        System.out.printf("  │  Highest : %.1f%n", s.getHighest());
        System.out.printf("  │  Lowest  : %.1f%n", s.getLowest());
        System.out.println("  │  Grade   : " + s.getLetterGrade());
        System.out.println("  │  Status  : " + s.getStatus());
        System.out.println("  └──────────────────────────────────────");
    }

    // ─────────────────────────────────────────
    //  Utility methods
    // ─────────────────────────────────────────
    static boolean rollExists(String roll) {
        for (Student s : students)
            if (s.getRollNo().equalsIgnoreCase(roll)) return true;
        return false;
    }

    static Student findByRoll(String roll) {
        for (Student s : students)
            if (s.getRollNo().equalsIgnoreCase(roll)) return s;
        return null;
    }

    static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("  ✗ Please enter a valid number.");
            }
        }
    }

    static double readScore(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                double v = Double.parseDouble(scanner.nextLine().trim());
                if (v < 0 || v > 100) throw new NumberFormatException();
                return v;
            } catch (NumberFormatException e) {
                System.out.println("    ✗ Enter a number between 0 and 100.");
            }
        }
    }

    // ─────────────────────────────────────────
    //  Sample data (for demonstration)
    // ─────────────────────────────────────────
    static void loadSampleData() {
        addSample("Aarav Mehta",  "101", 88, 92, 75, 80, 95);
        addSample("Sneha Patil",  "102", 76, 68, 89, 72, 84);
        addSample("Rohan Desai",  "103", 92, 95, 88, 91, 97);
        addSample("Priya Sharma", "104", 61, 73, 80, 65, 70);
        addSample("Karan Joshi",  "105", 84, 79, 71, 77, 88);
    }

    static void addSample(String name, String roll, double... scores) {
        ArrayList<Double> g = new ArrayList<>();
        for (double s : scores) g.add(s);
        students.add(new Student(name, roll, g));
    }
}