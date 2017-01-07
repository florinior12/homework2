package dsp;

/**
 * Created by Florin on 12/31/2016.
 */
public class Adder extends Element {


    public Adder(Signal out, Signal in1, Signal in2) {
        this(out, new Signal[]{in1, in2});

    }

    public Adder(Signal out, Signal[] in) {
        output = out;
        multipleInput = in;

    }

    @Override
    public void compute() {


        Signal result = multipleInput[0];

        for (int i = 1; i < multipleInput.length; i++) {

            result = result.add(multipleInput[i]);


        }
        output.copy(result);


    }
}
