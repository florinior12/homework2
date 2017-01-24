package dsp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

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

    public void compute() { //I need to sort the array list somehow...
        /*for (Element element : elements) {
            elements.remove();

            if (element.output == output && elements.indexOf(element)!=elements.size()-1) {
                Element toSwap = element;
                elements.set(elements.indexOf(element),elements.get(elements.size()-1));
                elements.set(elements.size()-1,toSwap);
            }
        }*/


        for (Element element : elements) {
            if(element.singleInput.samples!=null || element.isMultipleFull()) {

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
    public static void main(String[] args) {
        LinearSystem ls = new LinearSystem();
        double[] samples = {2, 3, 4, 3, 2, 0, -2, -3, -4, -3, -2, 0, 0, 0, 0};
        Signal a = new Signal(samples);
        Signal b = new Signal();
        Signal c = new Signal();
        Signal d = new Signal();
        double[] fir = {3, -2, 1, 0};

        ls.add(new Gain(c, a, 0.5));

        ls.add(new Filter(b, a, fir));
        ls.add(new Adder(d, b, c));

        ls.setInput(a);
        System.out.println("in\t" + a);
        ls.setOutput(d);
        ls.compute();
       // System.out.println("out\t" + d);
    }
}
