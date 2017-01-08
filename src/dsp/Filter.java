package dsp;


/**
 * Name: Iordache Florin
 * Group: 422G
 * Homework: 2
 */
public class Filter extends Element {
    private double[] samples;

    //besides input and output, we have an array of samples, the system's finite impulse response
    public Filter(Signal out, Signal in, double[] samples) {
        output = out;
        singleInput = in;
        this.samples = samples;
    }

    @Override
    public void compute() {
        output.copy(singleInput.convolve(new Signal(samples))); //compute uses Signal's convolve method
    }
}
