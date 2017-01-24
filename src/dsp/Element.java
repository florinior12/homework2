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

    boolean isMultipleFull() {
        boolean isFull = true;
        if (multipleInput != null) {
            for (int i = 0; i < multipleInput.length; i++) {
                if (multipleInput[i].samples == null) {
                    isFull = false;
                    i = multipleInput.length;
                }
            }

            return isFull;
        } else {
            return false;
        }
    }
}
