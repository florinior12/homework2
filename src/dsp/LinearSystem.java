package dsp;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Florin on 1/21/2017.
 */
public class LinearSystem {
    private ArrayList<Element> elements = new ArrayList<>();
    private ArrayList<Element> sortedElements = new ArrayList<>();
    private Signal input = null;
    private Signal output = null;

    public LinearSystem() {

    }


    public LinearSystem(String fileName) throws IOException {
        BufferedReader bf = null;
        bf = new BufferedReader(new FileReader(fileName));
        HashMap hashMap = new HashMap();

        String line = bf.readLine();
        String text = "";

        while ((line != null)) {
            if (!line.equals(""))
                text += line + "\n";
            line = bf.readLine();
        }
        bf.close();

        String[] instructions = text.split("\n");

        for (String instruction : instructions) {
            String[] labels = instruction.split("\\s+");
            switch (labels[0]) {
                case "GAIN":
                    if (!hashMap.containsKey(labels[1])) {
                        hashMap.put(labels[1].hashCode(), new Signal());
                    }
                    if (!hashMap.containsKey(labels[2])) {
                        hashMap.put(labels[2].hashCode(), new Signal());
                    }
                    elements.add(new Gain((Signal) hashMap.get(labels[1].hashCode()), (Signal) hashMap.get(labels[2].hashCode()), Double.parseDouble(labels[3])));
                    break;
                case "DELAY":
                    if (!hashMap.containsKey(labels[1])) {
                        hashMap.put(labels[1].hashCode(), new Signal());
                    }
                    if (!hashMap.containsKey(labels[2])) {
                        hashMap.put(labels[2].hashCode(), new Signal());
                    }
                    elements.add(new Delay((Signal) hashMap.get(labels[1].hashCode()), (Signal) hashMap.get(labels[2].hashCode()), Integer.parseInt(labels[3])));
                case "FILTER":
                    if (!hashMap.containsKey(labels[1])) {
                        hashMap.put(labels[1].hashCode(), new Signal());
                    }
                    if (!hashMap.containsKey(labels[2])) {
                        hashMap.put(labels[2].hashCode(), new Signal());
                    }
                    double[] samples = new double[labels.length];
                    for (int i = 3; i<labels.length; i++) {
                        samples[i-3] = Double.parseDouble(labels[i]);
                    }
                    elements.add(new Filter((Signal) hashMap.get(labels[1].hashCode()), (Signal) hashMap.get(labels[2].hashCode()), samples));
                    break;
                case "ADDER":
                    if (!hashMap.containsKey(labels[1])) {
                        hashMap.put(labels[1].hashCode(), new Signal());
                    }
                    Signal[] toAdd = new Signal[labels.length-2];
                    for (int i = 2; i<labels.length; i++) {
                        if (!hashMap.containsKey(labels[i])) {
                            hashMap.put(labels[i].hashCode(), new Signal());
                        }
                    }
                case "IN":
                    hashMap.put(labels[1].hashCode(), new Signal(input.samples));
                    input = (Signal) hashMap.get(labels[1].hashCode());

            }
        }


    }

    public void add(Element element) {
        elements.add(element);
    }

    public void compute() { //Compute method
        int i = 0;
        //This method calls the compute of all the elements whose inputs are not empty signals, untill the output is not an empty signal anymore
        while (this.output.samples == null) {   //While the output is an empty signal
            System.out.println(++i);
            for (Element element : elements) {  //Foreach loop
                if (!element.getClass().getSimpleName().equals("Adder")) {  //For elements that are not adders and have just one input signal
                    if (element.singleInput.samples != null) {  //Check if the input is empty signal
                        element.compute();
                    }
                } else {
                    if (isMultipleFull(element)) {  //Check if all the input signals are not empty signals
                        element.compute();
                    }
                }
            }
        }
    }

    public void setInput(Signal input) {
        this.input = input;
    }

    public void setOutput(Signal output) {
        this.output = output;
    }

    public Signal getInput() {
        return input;
    }

    public Signal getOutput() {
        return output;
    }

    public void save() throws IOException {

        BufferedWriter bf = null;
        HashMap hashMap = new HashMap();
        hashMap.put(input.hashCode(), "in");
        hashMap.put(output.hashCode(), "out");
        int i = 0;

        for (Element element : elements) {
            if (!hashMap.containsKey(element.output.hashCode())) {

                hashMap.put(element.output.hashCode(), (char) ('a' + i));
                i++;
            }
            if (!element.getClass().getSimpleName().equals("Adder")) {
                if (!hashMap.containsKey(element.singleInput.hashCode())) {
                    hashMap.put(element.singleInput.hashCode(), (char) ('a' + i));
                    i++;
                }
            } else {
                for (int j = 0; j < element.multipleInput.length; j++) {
                    if (!hashMap.containsKey(element.multipleInput[j].hashCode())) {
                        hashMap.put(element.multipleInput[j].hashCode(), (char) ('a' + i));
                        i++;
                    }
                }
                i++;
            }
        }


        try {
            bf = new BufferedWriter(new FileWriter("saved.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }


        for (Element element : elements) {
            String line = "";
            switch (element.getClass().getSimpleName()) {
                case "Adder":
                    line += "ADDER\t" + hashMap.get(element.output.hashCode());
                    for (int j = 0; j < element.multipleInput.length; j++) {
                        line += "\t" + hashMap.get(element.multipleInput[j].hashCode());
                    }
                    break;
                case "Gain":
                    line += "GAIN\t" + hashMap.get(element.output.hashCode()) + "\t" + hashMap.get(element.singleInput.hashCode()) + "\t" + ((Gain) element).gain;
                    break;
                case "Filter":
                    line += "FILTER\t" + hashMap.get(element.output.hashCode()) + "\t" + hashMap.get(element.singleInput.hashCode()) + "\t";
                    for (double sample:((Filter)element).samples) {
                        line += sample + "\t";
                    }
                    break;
                case "Delay":
                    line += "DELAY\t" + hashMap.get(element.output.hashCode()) + "\t" + hashMap.get(element.singleInput.hashCode()) + "\t" + ((Delay) element).delay;
                    break;

            }
            try {
                bf.write(line + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        bf.write("IN\t" + hashMap.get(input.hashCode()) + "\n");
        bf.write("OUT\t" + hashMap.get(output.hashCode()));
        try {
            bf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    boolean isMultipleFull(Element element) {      //Checks if all the inputs from the multipleInput() are signals with samples not null
        boolean isFull = true;
        if (element.multipleInput != null) {
            for (int i = 0; i < element.multipleInput.length; i++) {
                if (element.multipleInput[i].samples == null) {
                    isFull = false;
                    i = element.multipleInput.length;
                }
            }

            return isFull;
        } else {
            return false;
        }
    }

    public static void main(String[] args) {
        LinearSystem ls = new LinearSystem();

        double[] samples = {1, 2, 3};
        Signal a = new Signal(samples);
        Signal b = new Signal();
        Signal e = new Signal();
        Signal f = new Signal();
        Signal k = new Signal();
        Signal o = new Signal();
        Signal m = new Signal();
        Signal n = new Signal();
        Signal p = new Signal();
        double[] fir = {3, -2, 1, 0};

        ls.add(new Gain(b, a, 2));
        ls.add(new Filter(f, a, fir));
        ls.add(new Adder(o, m, n));
        ls.add(new Delay(m, k, 1));
        ls.add(new Gain(k, b, 2));
        ls.add(new Gain(e, a, 2));
        ls.add(new Adder(p, k, e));
        ls.add(new Gain(n, f, 2));


        ls.setInput(a);
        System.out.println("in\t" + a);

        ls.setOutput(o);
        try {
            ls.save();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        ls.compute();

        System.out.println("out\t" + o);

    }
}