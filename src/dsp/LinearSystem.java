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
    private Signal input;
    private Signal output;

    public LinearSystem() {

    }

    public LinearSystem(String fileName) throws IOException{
        BufferedReader bf = null;
        bf = new BufferedReader(new FileReader(fileName));

        String line = bf.readLine();
        String text = "";

        while((line != null)) {
            if (!line.equals(""))
                text += line + "\n";
            line = bf.readLine();
        }
        bf.close();

        String[] instructions = text.split("\n");
        /*
        for (String instruction : instructions) {
            String[] labels = instruction.split("\\s+");
            switch (labels[0]) {
                case "ADDER":
                    Signal[] inputs = new Signal[labels.length-2];
                    for (int i = 0; i<inputs.length; i++) {
                        inputs[i] = labels[i+2];
                    }
            }
        }*/


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

    public void save () throws IOException{

        BufferedWriter bf = null;
        HashMap hashMap = new HashMap();
        hashMap.put(input.hashCode(),"in");
        hashMap.put(output.hashCode(), "out");
        int i = 0;

        for (Element element : elements) {
            if (!hashMap.containsKey(element.output.hashCode())) {

                hashMap.put(element.output.hashCode(), (char) (97 + i));
                i++;
            }
            if (!element.getClass().getSimpleName().equals("Adder")) {
                if (!hashMap.containsKey(element.singleInput.hashCode())) {
                    hashMap.put(element.singleInput.hashCode(), (char) (97 + i));
                    i++;
                }
            } else {
                for (int j = 0; j<element.multipleInput.length;j++) {
                    if (!hashMap.containsKey(element.multipleInput[j].hashCode())) {
                        hashMap.put(element.multipleInput[j].hashCode(), ((char) (97 + i) + "" + j));

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
                    for (int j = 0; j<element.multipleInput.length;j++) {
                        line += "\t" + hashMap.get(element.multipleInput[j].hashCode());
                    }
                    break;
                default:
                    line += element.getClass().getSimpleName().toUpperCase() + "\t" + hashMap.get(element.output.hashCode()) + "\t" +  hashMap.get(element.singleInput.hashCode()) +"\t";
                    break;
            }
            try {
                bf.write(line + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            bf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    /*
    void printHash() {
        for (Element element : elements) {
            System.out.println(elements.indexOf(element) + ":");

            if (element.getClass().getSimpleName().equals("Adder")) {
                System.out.print((char)(element.output.hashCode()%25+97) + "\t");
                for (int i = 0; i< element.multipleInput.length;i++) {
                    System.out.print((char)(element.multipleInput[i].hashCode()%25+97)+"\t");
                }
                System.out.print("\n");
            }
            else {
                System.out.println(((char)(element.output.hashCode()%25+97)) + "\t" + ((char)(element.singleInput.hashCode()%25+97)));
            }
        }
    }*/

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
        double[] samples = {1,2,3};
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

        ls.add(new Gain(b,a,2));
        ls.add(new Gain(f,a,2));
        ls.add(new Adder(o,m,n));
        ls.add(new Adder(m,k,p));
        ls.add(new Gain(k,b,2));
        ls.add(new Gain(e,a,2));
        ls.add(new Adder(p,k,e));
        ls.add(new Gain(n,f,2));



        ls.setInput(a);
        System.out.println("in\t" + a);

        ls.setOutput(o);
        try {
            ls.save();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        //ls.compute();

    }
}
