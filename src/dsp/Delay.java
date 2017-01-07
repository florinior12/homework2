package dsp;

/**
 * Created by Florin on 1/6/2017.
 */
public class Delay extends Element {
    private int delay;
    public Delay(Signal out, Signal in, int delay) {
        output = out;
        singleInput = in;
        this.delay = delay;
    }

    public void compute() {
        output.copy(singleInput.delay(delay));
    }
}
