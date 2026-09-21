public class Employee {
    private int id;
    private String name;
    private String department;
    private double salary;

    
    public Employee(int id, String name, String department, double salary) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.salary = salary;
    }

    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    
    public double calculateAnnualSalary() {
        return salary * 10;
    }

    
    public double calculateAnnualSalary(double bonus) {
        return (salary * 10) + bonus;
    }

    
    public void displayDetails() {
        System.out.println("Employee ID: " + id);
        System.out.println("Name: " + name);
        System.out.println("Department: " + department);
        System.out.println("Monthly Salary: " + salary);
        System.out.println("Annual Salary: " + calculateAnnualSalary());
    }

   
    public String toString() {
        return "Employee ID: " + id +
               ", Name: " + name +
               ", Department: " + department +
               ", Salary: " + salary;
    }

    
    public static void main(String[] args) {

        Employee emp = new Employee(
            101,
            "sana khan salam",
            "Computer Science",
            40000
        );

        System.out.println("----- Employee Details -----");
        emp.displayDetails();

        System.out.println("\n----- Method Overloading -----");
        System.out.println("Annual Salary: " +
                emp.calculateAnnualSalary());

        System.out.println("Annual Salary with Bonus: " +
                emp.calculateAnnualSalary(50000));

        System.out.println("\n----- toString() -----");
        System.out.println(emp);
    }
}