package dsp;

/**
 * Name: Iordache Florin
 * Group: 422G
 * Homework: 2
 */
public class Delay extends Element {
    int delay;

    public Delay(Signal out, Signal in, int delay) {    //besides input and output, we use an integer which represents the delay
        output = out;
        singleInput = in;
        this.delay = delay;
    }

    @Override
    public void compute() {
        output.copy(singleInput.delay(delay));  //compute uses Signal's delay method
    }

    public String toString() {
        return "DELAY";
    }
}
