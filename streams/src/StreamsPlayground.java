import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamsPlayground {

    public static void main(String[] args) {
        System.out.println("****** Multiply Without Operator ******" );
        StreamsPlayground.testMultiplyWithoutMultiply();

        System.out.println("****** Multiply with Streams *******");
        StreamsPlayground.testMultiplyWithStreams();

        System.out.println("******* Testing Order Of Operations ********");
        StreamsPlayground.testOrderOfOperations();
    }

    private static class MultiplyTestCase {
        Integer operandA;
        Integer operandB;
        Integer result;

        public  MultiplyTestCase () {

        }

        public MultiplyTestCase(Integer operandA, Integer operandB, Integer result) {
            this.operandA = operandA;
            this.operandB = operandB;
            this.result = result;
        }

        @Override
        public String toString() {
            return "Test Case Values (" + operandA + ", " + operandB + ") expected result: " + result;
        }
    }

    public static void testMultiplyWithoutMultiply() {
        for(MultiplyTestCase test: getMultiplyTestCases()) {
            try {
                Integer result = multiplyWithoutMultiply(test.operandA, test.operandB);

                Boolean passed = false;

                if(result == null) {
                    if(test.result == null)
                        passed = true;
                } else
                    passed = result.equals(test.result);

                System.out.println(test.toString() + " : Actual Result = " + result + " -- Passed: " + passed);

            } catch (Exception re) {
                System.out.println("Error occurred in test: " + test.toString() + " : " + re);
            }
        }
    }

    public static List<MultiplyTestCase> getMultiplyTestCases() {
        List<MultiplyTestCase> testCases = new ArrayList<MultiplyTestCase>();

        //All Null
        testCases.add(new MultiplyTestCase(null,null,null));
        //A
        testCases.add(new MultiplyTestCase(null,0,null));
        //B Null
        testCases.add(new MultiplyTestCase(0,null,null));
        //All 0
        testCases.add(new MultiplyTestCase(0,0,0));
        //A 0
        testCases.add(new MultiplyTestCase(0,1,0));
        //B 0
        testCases.add(new MultiplyTestCase(1,0,0));
        //Very Large A: TODO: Update to make sure this fails if a is done as a loop
        testCases.add(new MultiplyTestCase(100000000,1,100000000));
        //B 0
        testCases.add(new MultiplyTestCase(1,100000000,100000000));
        //BOth negative
        testCases.add(new MultiplyTestCase(-1,-2,2));
        //A negative
        testCases.add(new MultiplyTestCase(-1,2,-2));
        //B negative
        testCases.add(new MultiplyTestCase(1,-2,-2));
        //A negative and A > B
        testCases.add(new MultiplyTestCase(-2,1,-2));
        //B negative and A > B
        testCases.add(new MultiplyTestCase(2,-1,-2));

        return testCases;
    }


    //Method that multiplies two numbers without using the multiply operator
    public static Integer multiplyWithoutMultiply(Integer a, Integer b) {
        //Initialize sum
        Integer sum = 0;

        //Null Check
        if(a == null || b == null)
            return null;

        //Check for 0
        if(a == 0 || b == 0)
            return sum;

        Integer operandA = a;
        Integer operandB = b;

        //Limit the loops based on the smallest absolute value
        if(Math.abs(a) > Math.abs(b)) {
            operandA = b;
            operandB = a;
        }

        //If operandA is negative then flip the signs
        if(operandA < 0) {
            operandA = 0 - operandA;
            operandB = 0 - operandB;
        }

        for(int index = 0; index < operandA; index++) {
            sum += operandB;
        }

        return sum;
    }

    //Method that multiplies two numbers without using the multiply operator
    public static Integer multiplyWithStreams(List<Integer> operands) {

        //Null Check
        Predicate<Integer> nullVal = val -> val == null;
        if(operands.stream().anyMatch(nullVal))
            return null;

        //Check for 0
        Predicate<Integer> zero = val -> val == 0;
        if(operands.stream().anyMatch(zero))
            return 0;

        //Sort by absolute value
        List<Integer> sortedOperands = operands.stream().sorted(Comparator.comparingInt(Math::abs)).collect(Collectors.toList());

        //IF first operand is negative then flip sign on both operands.
        List<Integer> normalizedOperands = sortedOperands.stream().map(val -> { if(sortedOperands.get(0) < 0) return 0 - val; else return val;} ).collect(Collectors.toList());

        //Generate stream to add elements together.
        return Stream.iterate(normalizedOperands.get(1), i -> i).limit(normalizedOperands.get(0)).reduce(0,Integer::sum);
    }

    public static void testMultiplyWithStreams() {
        for(MultiplyTestCase test: getMultiplyTestCases()) {
            try {

                Integer result = multiplyWithStreams(Arrays.asList(test.operandA, test.operandB));

                Boolean passed = false;

                if(result == null) {
                    if(test.result == null)
                        passed = true;
                } else
                    passed = result.equals(test.result);

                System.out.println(test.toString() + " : Actual Result = " + result + " -- Passed: " + passed);

            } catch (Exception re) {
                System.out.println("Error occurred in test: " + test.toString() + " : " + re);
            }
        }
    }

    /*
        Show order of operations
     */
    public static void testOrderOfOperations() {
        /* Sort occurs after limit and search */
        Predicate<Employee> salaryGt1000 = emp -> emp.getSalary() > 1000;
       List<Employee> empList =  new ArrayList<Employee>(Arrays.asList(getEmployeeList()));

       System.out.println(" :: Limit Before Filter :: ");
       //Returns only one result because the employee list is limited before running the filter so only one item is passed to filter.
        empList.stream().limit(5).filter(salaryGt1000).forEach(System.out::println);

        System.out.println(" :: Limit After Filter :: ");
        //Returns results;
        empList.stream().filter(salaryGt1000).limit(5).forEach(System.out::println);

        Comparator<Employee> salaryCompare = Comparator.comparing(employee -> employee.getSalary());
        /* Return top 10 salarys */
        System.out.println(" :: Sorting top 10 Salaries :: ");
        empList.stream().limit(10).sorted(salaryCompare.reversed()).forEach(System.out::println);

        System.out.println(" :: Sorting top 10 Salaries :: ");
        empList.stream().sorted(salaryCompare.reversed()).limit(10).forEach(System.out::println);

        System.out.println(" :: Find bottom 5 of the Top 10 Salaries :: " );
        empList.stream().sorted(salaryCompare.reversed()).limit(10).sorted(salaryCompare).limit(5).forEach(System.out::println);

    }

    public static class Employee {
        private String firstName;
        private String lastName;
        private Integer salary;
        private String department;

        public Employee () {

        }

        public Employee(String firstName, String lastName, Integer salary, String department) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.salary = salary;
            this.department = department;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public Integer getSalary() {
            return salary;
        }

        public void setSalary(Integer salary) {
            this.salary = salary;
        }

        public String getDepartment() {
            return department;
        }

        public void setDepartment(String department) {
            this.department = department;
        }

        @Override
        public String toString() {
            return "Employee: " + lastName + ", " + firstName + " | Salary: " + salary + " | Department: " + department;
        }
    }

    /*
        Generates List of employees to work on.
     */
    public static Employee[] getEmployeeList() {
        Employee[] employeeList= {
                new Employee("Anne","Frank",500,"Human Resources"),
                new Employee("Joe","Jackson",1000,"Human Resources"),
                new Employee("John","Fire",1500,"Human Resources"),
                new Employee("Steve","Frank",200,"Human Resources"),
                new Employee("Sarah","Frank",400,"Human Resources"),
                new Employee("Jean","Frank",600,"Human Resources"),
                new Employee("Anna","Frank",1200,"Human Resources"),
                new Employee("Annabelle","Frank",1400,"Human Resources"),
                new Employee("Keri","Frank",900,"Human Resources"),
                new Employee("Kayla","Frank",250,"Human Resources"),
                new Employee("James","Frank",100,"Human Resources"),
                new Employee("Quentin","Frank",120,"Human Resources"),
                new Employee("Adrian","Frank",180,"Human Resources"),
                new Employee("Claire","Frank",900,"Human Resources"),
                new Employee("Melanie","Frank",350,"Human Resources"),
                new Employee("MIke","Frank",375,"Human Resources"),
                new Employee("Liam","Frank",425,"Human Resources"),
                new Employee("Luke","Frank",555,"Human Resources"),
                new Employee("Lisa","Frank",655,"Human Resources"),
                new Employee("Joseph","Frank",725,"Human Resources"),
                new Employee("Patty","Frank",325,"Human Resources"),
                new Employee("Patrick","Frank",225,"Human Resources"),
                new Employee("Bill","Frank",175,"Human Resources"),
                new Employee("Britta","Frank",100,"Human Resources"),
        };

        return employeeList;
    }

}
