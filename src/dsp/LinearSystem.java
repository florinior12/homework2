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
    private Signal input = new Signal();
    private Signal output;
    //PLEASE CHANGE THE ACCESS OF samples[] field of the Signal class, delay field of Delay, samples[] field of Filter and gain field of Gain to implicit access modifier
    //The LinearSystem(String fileName) constructor works by calling the setInput method and then the getOutput method, so there needs to be just one Signal declaration in main, the input signal
    public LinearSystem() { //Empty system
    }


    public LinearSystem(String fileName) throws IOException {
        BufferedReader bf = null;
        HashMap hashMap = new HashMap();
        bf = new BufferedReader(new FileReader(fileName));
        String line = bf.readLine();
        String text = "";   //Whole text file is kept in this String

        while ((line != null)) {    //While loop used to read all the lines of the text file
            if (!line.equals(""))
                text += line + "\n";
            line = bf.readLine();
        }
        bf.close();

        String[] instructions = text.split("\n");   //The text is split in lines, each line is an instruction
        int k = 0;  //For debug purposes

        for (String instruction : instructions) {
            String[] labels = instruction.split("\\s+");    //Each instruction line is split in it's components (labels)
            //The first component is the type of the element, so we will make a switch statement according to it
            switch (labels[0]) {
                case "GAIN":    //When there is a gain type
                    //Output is the second component
                    if (!hashMap.containsKey(labels[1].hashCode())) {   //If its hashcode isn't added to the hashmap, we add it
                        hashMap.put(labels[1].hashCode(), new Signal());    //Add the hashcode of the second component to the hashmap and associate it a new Signal
                        k++;
                    }
                    //Input is the third component
                    if (!hashMap.containsKey(labels[2].hashCode())) {
                        hashMap.put(labels[2].hashCode(), new Signal());
                        k++;
                    }
                    //We add to the elements arraylist a new Gain element which has as output and input the corresponding Signals
                    //of the hashcodes from the hashmap.
                    // The number by which the Gain is done is obtained by downcasting from Element to Gain
                    elements.add(new Gain((Signal) hashMap.get(labels[1].hashCode()), (Signal) hashMap.get(labels[2].hashCode()), Double.parseDouble(labels[3])));
                    break;
                case "DELAY":
                    if (!hashMap.containsKey(labels[1].hashCode())) {
                        hashMap.put(labels[1].hashCode(), new Signal());
                        k++;
                    }
                    if (!hashMap.containsKey(labels[2].hashCode())) {
                        hashMap.put(labels[2].hashCode(), new Signal());
                        k++;
                    }
                    elements.add(new Delay((Signal) hashMap.get(labels[1].hashCode()), (Signal) hashMap.get(labels[2].hashCode()), Integer.parseInt(labels[3])));
                case "FILTER":
                    if (!hashMap.containsKey(labels[1].hashCode())) {
                        hashMap.put(labels[1].hashCode(), new Signal());
                        k++;
                    }
                    if (!hashMap.containsKey(labels[2].hashCode())) {
                        hashMap.put(labels[2].hashCode(), new Signal());
                        k++;
                    }
                    //We will create an array of samples starting from the third component
                    double[] samples = new double[labels.length];
                    for (int i = 3; i < labels.length; i++) {
                        samples[i - 3] = Double.parseDouble(labels[i]);
                    }
                    elements.add(new Filter((Signal) hashMap.get(labels[1].hashCode()), (Signal) hashMap.get(labels[2].hashCode()), samples));
                    break;
                case "ADDER":
                    if (!hashMap.containsKey(labels[1].hashCode())) {
                        hashMap.put(labels[1].hashCode(), new Signal());
                        k++;
                    }
                    //We will create a vector of Signals of length components-2 because the first 2 components are element type and output
                    Signal[] toAdd = new Signal[labels.length - 2];
                    for (int i = 2; i < labels.length; i++) {
                        if (!hashMap.containsKey(labels[i].hashCode())) {
                            hashMap.put(labels[i].hashCode(), new Signal());
                            k++;
                        }
                        toAdd[i - 2] = (Signal) hashMap.get(labels[i].hashCode());
                    }
                    //Add a new element having the second component as output and the created vector as input
                    elements.add(new Adder((Signal) hashMap.get(labels[1].hashCode()), toAdd));
                    break;
                case "IN":
                    if (!hashMap.containsKey(labels[1].hashCode())) {
                        hashMap.put(labels[1].hashCode(), input);
                        k++;
                    }
                    //The input adress of our system is the adress of the second component
                    this.input = (Signal) hashMap.get(labels[1].hashCode());
                    break;
                case "OUT":
                    if (!hashMap.containsKey(labels[1].hashCode())) {
                        hashMap.put(labels[1].hashCode(), new Signal());
                        k++;
                    }
                    //The output adress is the adress of the second component
                    this.output = (Signal) hashMap.get(labels[1].hashCode());
                    break;

            }
        }


    }

    public void add(Element element) {
        elements.add(element);
    }

    public void compute() { //Compute method

        //System.out.println(this.input);
        //This method calls the compute of all the elements whose inputs are not empty signals, untill the output is not an empty signal anymore
        while (this.output.samples == null) {   //While the output is an empty signal

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

    public void setInput(Signal a) {
        input.copy(a);  //We want to keep the adress of the input instantiated as the field of linearsystem class, for further use
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
        HashMap hashMap = new HashMap();    //We will use a hashmap to store the hashcodes of the used variables and give these hashcodes certain characters
        hashMap.put(input.hashCode(), "in");    //Associate input's hashcode with the string "in"
        hashMap.put(output.hashCode(), "out");  //Associate output's hashcode with the string "out"
        int i = 0;  //Counter which increments for each added character and is used to generate characters in order

        for (Element element : elements) {
            if (!hashMap.containsKey(element.output.hashCode())) {
                //Add to 'a' char the value of i to get another char, depending on number of characters and associate to the obtained char the hashcode of the output
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
                    for (double sample : ((Filter) element).samples) {
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

    public static void main(String[] args) throws IOException {
        LinearSystem ls = new LinearSystem("instructions.txt");


        double[] samples = {1, 2, 3};
        Signal a = new Signal(samples);
        /*Signal b = new Signal();


        Signal e = new Signal();
        Signal f = new Signal();
        Signal k = new Signal();
        Signal o = new Signal();
        Signal m = new Signal();
        Signal n = new Signal();
        Signal p = new Signal();
        double[] fir = {3, -2, 1, 0};

        ls.add(new Gain(b, a, 2));
        ls.add(new Gain(f, a, 2));
        ls.add(new Adder(o, m, n));
        ls.add(new Adder(m,k,p));
        ls.add(new Gain(k, b, 2));
        ls.add(new Gain(e, a, 2));
        ls.add(new Adder(p, k, e));
        ls.add(new Gain(n, f, 2));*/


        ls.setInput(a);
        System.out.println("in\t" + a);

        //ls.setOutput(o);

        ls.compute();
        try {
            ls.save();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        System.out.println("out\t" + ls.getOutput());

    }
}
