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


}
