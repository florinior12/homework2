package dsp;


/**
 * Name: Iordache Florin
 * Group: 422G
 * Homework: 2
 */
public abstract class Element {
    protected Signal output;    //output signal
    protected Signal singleInput;   //when there is only one input signal
    protected Signal[] multipleInput;   //when there is an array of input signals

    void compute() {    //method to be implemented by subclasses

    }
}
