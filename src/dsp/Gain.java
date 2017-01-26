package dsp;


/**
 * Name: Iordache Florin
 * Group: 422G
 * Homework: 2
 */
public class Gain extends Element {
    double gain;

    public Gain(Signal out, Signal in, double gain) {   //Besides input and output, we have a scalar value gain
        output = out;
        singleInput = in;
        this.gain = gain;
    }

    @Override
    public void compute() {
        output.copy(singleInput.scale(gain));   //compute uses Signal's scale method
    }

    public String toString() {
        return "GAIN";
    }
}
