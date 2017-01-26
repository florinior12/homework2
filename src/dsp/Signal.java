package dsp;

import java.io.*;


/**
 * Name: Iordache Florin
 * Group: 422G
 * Homework: 2
 */
public class Signal {
    double[] samples;   //Array of type double which contains samples

    public Signal() {      //Default constructor creates an empty signal, with no samples
        this.samples = null;
    }

    public Signal(double[] samples) {  //Constructor that creates a signal with samples given as array argument
        this.samples = samples;
    }

    public Signal(String fileName) throws IOException {    //Constructor that reads samples from file
        BufferedReader bf =
                new BufferedReader(new FileReader(fileName));   //Instantiates a buffered reader for the file

        StringBuilder sb = new StringBuilder(); //We will use a StringBuilder to read all the lines
        String text = bf.readLine();    //Reads the first line
        try {
            while (text != null) {  //While loop that ends whene there are no more lines in the file
                sb.append(text);    //Append the line to the StringBuilder
                sb.append("\n");    //Append a new line
                text = bf.readLine();   //Read a new line

            }
        } finally {
            bf.close(); //Close the buffered reader
        }
        //System.out.print(sb.toString());

        //Creates an array of Strings from the StringBuilder string, splitted at the encounter of whitespace of new line
        String[] splitSamples = sb.toString().split("\\s+|\n");

        samples = new double[splitSamples.length];  //We create a double array that has the length of String array
        for (int i = 0; i < splitSamples.length; i++) {
            //System.out.print(samples[i] );
            samples[i] = Double.parseDouble(splitSamples[i]);   //Converts the strings from array to doubles

        }
    }

    public void copy(Signal signal) {  //Copy the sammples of argument signal into our signal samples
        samples = getSamples(signal);   //getSamples is a private method used to get the samples of a signal
    }

    public Signal add(Signal signalToAdd) {    //Superposition of two signals
        double[] samplesToAdd = getSamples(signalToAdd);


        //The result signal has the length of the signal with more samples, so we check which is largest
        if (samplesToAdd.length > samples.length) {
            double[] resultSamples = samplesToAdd.clone();  //Clone the larger sample array into result samples
            //The loop goes from 0 to the smaller array of samples. It's like adding 0 after the small signal ends
            for (int i = 0; i < samples.length; i++) {
                resultSamples[i] = resultSamples[i] + samples[i];
            }

            return new Signal(resultSamples);   //Returns a new signal having the samples the results
        } else {
            double[] resultSamples = samples.clone();
            for (int i = 0; i < samplesToAdd.length; i++) {
                resultSamples[i] = samplesToAdd[i] + samples[i];
            }
            return new Signal(resultSamples);
        }

    }

    public Signal scale(double gain) {     //Scale the signal
        double[] resultSamples = new double[samples.length];
        for (int i = 0; i < samples.length; i++) {
            resultSamples[i] = gain * samples[i];   //Multiply each sample with a value
        }
        return new Signal(resultSamples);   //Return new scaled signal
    }

    public Signal delay(int delay) {   //Delay the signal
        double[] delayedSamples = new double[samples.length];
        if (delay > 0) { //Delay positive, signal[t-delay]
            for (int i = delay; i < samples.length; i++) {  //Starts from the delayed value, such that to the left of delay there is 0
                delayedSamples[i] = samples[i - delay]; //Delayed samples take the value of delayed samples array

            }
        } else {
            for (int i = 0; i < samples.length + delay; i++) {  //keep in mind that delay is negative in this case
                delayedSamples[i] = samples[i - delay];
            }
        }
        return new Signal(delayedSamples);
    }

    public Signal convolve(Signal signal) {    //Convolution
        double[] resultSamples;
        double[] secondSignalSamples = getSamples(signal);


        //genEmptySignal is a private method for generating an empty signal (samples are zeros) of a certain length
        //We will fill the signal with less non zero samples with zeros, such that the samples arrays will have equal length
        if (secondSignalSamples.length > samples.length) {
            samples = getSamples(this.add(genEmptySignal(secondSignalSamples.length)));   //Fill signal that has less samples with zeros

            resultSamples = new double[secondSignalSamples.length];
        } else {
            secondSignalSamples = getSamples(signal.add(genEmptySignal(samples.length)));   //Fill signal thas has less samples with zeros

            resultSamples = new double[samples.length];
        }


        for (int t = 0; t < resultSamples.length; t++) {
            resultSamples[t] = 0;
            for (int k = 0; k < samples.length; k++) {
                if (t >= k)    //Condition to avoid negative indices
                    resultSamples[t] = resultSamples[t] + samples[k] * secondSignalSamples[t - k];  //Discrete signal time convolution
            }
        }
        return new Signal(resultSamples);
    }


    public String toString() {
        if (samples == null) {
            return "NULL MADAFAKA";
        }
        StringBuffer sbf = new StringBuffer();
        for (int i = 0; i < samples.length; i++) {
            sbf.append(samples[i]).append("\t ");   //added an empty space after tab because tab alone would print just a space
        }
        return sbf.toString();
    }

    void save(String fileName) throws IOException { //Write our signal's samples to a file
        BufferedWriter bfw = new BufferedWriter(new FileWriter(fileName));

        try {
            for (int i = 0; i < samples.length; i++) {
                bfw.write(samples[i] + " ");
            }
        } finally {
            bfw.close();
        }
    }

    private double[] getSamples(Signal signal) {    //Method that gets the samples of a given signal
        String[] stringSamples = signal.toString().split("\t ");    //added an empty space after tab because only \t would sometimes print a space character
        double[] samples = new double[stringSamples.length];

        for (int i = 0; i < stringSamples.length; i++) {
            samples[i] = Double.parseDouble(stringSamples[i]);

        }
        return samples;
    }

    private Signal genEmptySignal(int length) { //Method that instantiates an empty signal of a certain length
        double[] samples = new double[length];
        for (int i = 0; i < length; i++) {
            samples[i] = 0;
        }
        return new Signal(samples);
    }

}
