package dsp;


/**
 * Name: Iordache Florin
 * Group: 422G
 * Homework: 2
 */
public class Adder extends Element {


    public Adder(Signal out, Signal in1, Signal in2) {  //in case there are two input signals to be added
        this(out, new Signal[]{in1, in2});  //call the constructor used for array of signals, giving the two signals as array

    }

    public Adder(Signal out, Signal[] in) { //constructor for multiple inputs, as array
        output = out;
        multipleInput = in;

    }

    @Override
    public void compute() {


        Signal result = multipleInput[0];   //initialize result with first input

        for (int i = 1; i < multipleInput.length; i++) {

            result = result.add(multipleInput[i]);  //add next input to result, we use Signal's method add


        }
        output.copy(result);    //copy the result into Element's output field


    }
}
