package dsp;

/**
 * Created by Florin on 1/6/2017.
 */
public class Gain extends Element{
    private double gain;
    public Gain(Signal out, Signal in, double gain) {
        output = out;
        singleInput = in;
        this.gain = gain;
    }

    @Override
    public void compute() {
        output.copy(singleInput.scale(gain));
    }
}
