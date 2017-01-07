package dsp;

/**
 * Created by Florin on 1/6/2017.
 */
public class Filter extends Element {
    private double[] samples;
    public Filter(Signal out, Signal in, double[] samples) {
        output = out;
        singleInput = in;
        this.samples = samples;
    }

    public void compute() {
        output.copy(singleInput.convolve(new Signal(samples)));
    }
}
