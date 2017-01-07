package dsp;

import java.io.*;



/**
 * Created by Florin on 12/30/2016.
 */
public class Signal {
    private double[] samples;

    public Signal() {      //EMPTY SIGNAL
        this.samples = null;
    }

    public Signal(double[] samples) {  //SAMPLES READ FROM KEYBOARD
        this.samples = samples;
    }

    public Signal(String fileName) throws IOException {    //SAMPLES READ FROM FILE
        BufferedReader bf =
                new BufferedReader(new FileReader(fileName));

        StringBuilder sb = new StringBuilder();
        String text = bf.readLine();
        try {
            while (text != null) {
                sb.append(text);
                sb.append("\n");
                text = bf.readLine();

            }
        } finally {
            bf.close();
        }
        //System.out.print(sb.toString());

        String[] splitSamples = sb.toString().split("\\s+|\n|,\\s+");
        samples = new double[splitSamples.length];
        for (int i = 0; i < splitSamples.length; i++) {
            //System.out.print(samples[i] );
            samples[i] = Double.parseDouble(splitSamples[i]);

        }
    }

    public void copy(Signal signal) {  //COPY SAMPLES OF SIGNAL INTO OUR SAMPLES
        samples = getSamples(signal);
    }

    public Signal add(Signal signalToAdd) {    //ADD A SIGNAL TO OUR SIGNAL
        double[] samplesToAdd = getSamples(signalToAdd);



        if (samplesToAdd.length > samples.length) {
            double[] resultSamples = (double[]) samplesToAdd.clone();

            for (int i = 0; i < samples.length; i++) {
                resultSamples[i] = resultSamples[i] + samples[i];
            }

            return new Signal(resultSamples);
        } else {
            double[] resultSamples = (double[]) samples.clone();
            for (int i = 0; i < samplesToAdd.length; i++) {
                resultSamples[i] = samplesToAdd[i] + samples[i];
            }
            return new Signal(resultSamples);
        }

    }

    public Signal scale(double gain) {     //SCALE (MULTIPLY)
        double[] resultSamples = new double[samples.length];
        for (int i = 0; i < samples.length; i++) {
            resultSamples[i] = gain * samples[i];
        }
        return new Signal(resultSamples);
    }

    public Signal delay(int delay) {   //DELAY THE SIGNAL
        double[] delayedSamples = new double[samples.length];
        if(delay > 0) {
            for (int i = delay; i < samples.length; i++) {
                delayedSamples[i] = samples[i - delay];

            }
        }
        else {
            for (int i = 0;i<samples.length + delay;i++) {
                delayedSamples[i] = samples[i-delay];
            }
        }
        return new Signal(delayedSamples);
    }

    public Signal convolve(Signal signal) {    //CONVOLUTION
        double[] resultSamples;
        double[] secondSignalSamples = getSamples(signal);

        if(secondSignalSamples.length>samples.length) {
            samples = getSamples(this.add(genEmptySignal(secondSignalSamples.length)));   //fill signal that has less samples with zeros
            //System.out.println((new Signal(samples)).toString());
            resultSamples = new double[secondSignalSamples.length];
        }
        else {
            secondSignalSamples = getSamples(signal.add(genEmptySignal(samples.length)));   //fill signal thas has less samples with zeros
            //System.out.println((new Signal(secondSignalSamples)).toString());
            resultSamples = new double[samples.length];
        }


        for (int t = 0;t<resultSamples.length;t++) {
            resultSamples[t] = 0;
            for (int k = 0; k<samples.length; k++) {
                if(t>=k)
                resultSamples[t] = resultSamples[t] + samples[k]*secondSignalSamples[t-k];
            }
        }
        return new Signal(resultSamples);
    }




    public String toString() {  //TOSTRING
        StringBuffer sbf = new StringBuffer();
        for (int i = 0; i < samples.length; i++) {
            sbf.append(samples[i]).append("\t ");   //added an empty space after tab because tab alone would print just a space
        }
        return sbf.toString();
    }

    void save(String fileName) throws IOException { //WRITE TO FILE OUR SAMPLES
        BufferedWriter bfw = new BufferedWriter(new FileWriter(fileName));

        try {
            for (int i = 0; i < samples.length; i++) {
                bfw.write(samples[i] + " ");
            }
        } finally {
            bfw.close();
        }
    }

    private double[] getSamples(Signal signal) {
        String[] stringSamples = signal.toString().split("\t ");    //added an empty space after tab because only \t would print a space character
        double[] samples = new double[stringSamples.length];

        for (int i = 0; i < stringSamples.length; i++) {
            samples[i] = Double.parseDouble(stringSamples[i]);

        }
        return samples;
    }

    private Signal genEmptySignal(int length) {
        double[] samples = new double[length];
        for (int i = 0; i < length; i++) {
            samples[i] = 0;
        }
        return new Signal(samples);
    }


    public static void main(String[] args) throws IOException {
       double[] samples1 = {3, -3, 0};
        double[] samples2 = {1, 2, 1, -1, -2, -1, 0, 0, 0, 0};
        Signal signal = new Signal("samples.txt");

        Signal signal2 = new Signal("samples");
        System.out.println("signal QQ: " + signal);
        System.out.println("signal 2: " + signal2);



        try {
            signal.convolve(signal2).save("cf");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Signal out = new Signal();
        Element adder = new Filter(out,new Signal(samples2), samples1 );
        adder.compute();
        System.out.println(out);

    }
}
