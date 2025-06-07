package neuralnetwork;// This program is based upon an excellent blog post from Matt Mazur.
// See: https://mattmazur.com/2015/03/17/a-step-by-step-backpropagation-example/
// Matt's solution is in Python and this program is a translation of the same logic into Java.
// The mathematics underlying this neural network are explained well in the blog post.
// To fully understand this program, you would need to study the math in the blog post.

/*The idea is to train a neural network to learn a truth table.
Here is a truth table for the AND operation:
0   0   0
0   1   0
1   0   0
1   1   1

And here is a truth table for the XOR operation:
0  0  0
0  1  1
1  0  1
1  1  0

The user is asked to provide the rightmost column of the table. For example, if the user wants to
train the table for the XOR operation, the user will provide the following inputs:  0  1  1  0.

10,000 steps are typically used to train the network. If the error is close to 0, for example, 0.053298, the network
will perform well.

If the output of a test is close to 1, for example, .9759876, we will call that a 1.
If the output of a test is close to 0, for example, .0348712, we will call that a 0.

The program is not written to handle input errors. We assume that the user behaves well.
 */

import java.util.*;

// Each Neuron has a bias, a list of weights, and a list of inputs.
// Each neuron will produce a single real number as an output.
class Neuron {
    private double bias;
    public List<Double> weights;
    public List<Double> inputs;
    double output;

    // Construct a neuron with a bias and reserve memory for its weights.
    public Neuron(double bias) {
        this.bias = bias;
        weights = new ArrayList<Double>();
    }

    //Calculate the output by using the inputs and weights already provided.
    //Squash the result so the output is between 0 and 1.
    public double calculateOutput(List<Double> inputs) {

        this.inputs = inputs;

        output = squash(calculateTotalNetInput());
        return output;
    }

    // Compute the total net input from the input, weights, and bias.
    public double calculateTotalNetInput() {

        double total = 0.0;
        for (int i = 0; i < inputs.size(); i++) {
            total += inputs.get(i) * weights.get(i);
        }
        return total + bias;
    }

    // This is the activation function, returning a value between 0 and 1.
    public double squash(double totalNetInput) {
        double v = 1.0 / (1.0 + Math.exp(-1.0 * totalNetInput));
        return v;
    }

    // Compute the partial derivative of the error with respect to the total net input.
    public Double calculatePDErrorWRTTotalNetInput(double targetOutput) {
        return calculatePDErrorWRTOutput(targetOutput) * calculatePDTotalNetInputWRTInput();
    }

    // Calculate error. How different are we from the target?
    public Double calculate_error(Double targetOutput) {
        double theError = 0.5 * Math.pow(targetOutput - output, 2.0);
        return theError;
    }

    // Compute the partial derivative of the error with respect to the output.
    public Double calculatePDErrorWRTOutput(double targetOutput) {
        return (-1) * (targetOutput - output);
    }

    // Compute the partial derivative of the total net input with respect to the input.
    public Double calculatePDTotalNetInputWRTInput() {
        return output * (1.0 - output);

    }

    // Calculate the partial derivative of the total net input with respect to the weight.
    public Double calculatePDTotalNetInputWRTWeight(int index) {
        return inputs.get(index);
    }
}

// The Neuron layer represents a collection of neurons.
// All neurons in the same layer have the same bias.
// We include in each layer the number of neurons and the list of neurons.
class NeuronLayer {
    private double bias;
    private int numNeurons;

    public List<Neuron> neurons;

    // Construct by specifying the number of neurons and the bias that applies to all the neurons in this layer.
    // If the bias is not provided, choose a random bias.
    // Create neurons for this layer and set the bias in each neuron.
    public NeuronLayer(int numNeurons, Double bias) {
        if (bias == null) {

            this.bias = new Random().nextDouble();
        } else {
            this.bias = bias;
        }
        this.numNeurons = numNeurons;
        this.neurons = new ArrayList<Neuron>();
        for (int i = 0; i < numNeurons; i++) {
            this.neurons.add(new Neuron(this.bias));
        }
    }

    // Display the neuron layer by displaying each neuron.
    public String toString() {
        String s = "";
        s = s + "Neurons: " + neurons.size() + "\n";
        for (int n = 0; n < neurons.size(); n++) {
            s = s + "Neuron " + n + "\n";
            for (int w = 0; w < neurons.get(n).weights.size(); w++) {
                s = s + "\tWeight: " + neurons.get(n).weights.get(w) + "\n";
            }
            s = s + "\tBias " + bias + "\n";
        }

        return s;
    }

    // Feed the input data into the neural network and produce some output in the output layer.
    // Return a list of outputs. There may be a single output in the output list.
    List<Double> feedForward(List<Double> inputs) {

        List<Double> outputs = new ArrayList<Double>();

        for (Neuron neuron : neurons) {

            outputs.add(neuron.calculateOutput(inputs));
        }

        return outputs;
    }

    // Return a list of outputs from this layer.
    // We do this by gathering the output of each neuron in the layer.
    // This is returned as a list of Doubles.
    // This is not used in this program.
    List<Double> getOutputs() {
        List<Double> outputs = new ArrayList<Double>();
        for (Neuron neuron : neurons) {
            outputs.add(neuron.output);
        }
        return outputs;
    }
}

// The NeuralNetwork class represents two layers of neurons - a hidden layer and an output layer.
// We also include the number of inputs and the learning rate.
// The learning rate determines the step size by which the network’s weights are
// updated during each iteration of training. This is typically chosen experimentally.

public class NeuralNetwork {

    // The learning rate is chosen experimentally. Typically, it is set between 0 and 1.
    private double LEARNING_RATE = 0.5;
    // This truth table example will have two inputs.
    private int numInputs;

    // This neural network will be built from two layers of neurons.
    private NeuronLayer hiddenLayer;
    private NeuronLayer outputLayer;

    // The neural network is constructed by specifying the number of inputs, the number of neurons in the hidden layer,
    // the number of neurons in the output layer, the hidden layer weights, the hidden layer bias,
    // the output layer weights and output layer bias.
    public NeuralNetwork(int numInputs, int numHidden, int numOutputs, List<Double> hiddenLayerWeights, Double hiddenLayerBias,
                         List<Double> outputLayerWeights, Double outputLayerBias) {
        // How many inputs to this neural network
        this.numInputs = numInputs;

        // Create two layers, one hidden layer and one output layer.
        hiddenLayer = new NeuronLayer(numHidden, hiddenLayerBias);
        outputLayer = new NeuronLayer(numOutputs, outputLayerBias);

        initWeightsFromInputsToHiddenLayerNeurons(hiddenLayerWeights);

        initWeightsFromHiddenLayerNeuronsToOutputLayerNeurons(outputLayerWeights);
    }

    // The hidden layer neurons have weights that are assigned here. If the actual weights are not
    // provided, random weights are generated.
    public void initWeightsFromInputsToHiddenLayerNeurons(List<Double> hiddenLayerWeights) {

        int weightNum = 0;
        for (int h = 0; h < hiddenLayer.neurons.size(); h++) {
            for (int i = 0; i < numInputs; i++) {
                if (hiddenLayerWeights == null) {
                    hiddenLayer.neurons.get(h).weights.add((new Random()).nextDouble());
                } else {
                    hiddenLayer.neurons.get(h).weights.add(hiddenLayerWeights.get(weightNum));
                }
                weightNum = weightNum + 1;
            }
        }
    }

    // The output layer neurons have weights that are assigned here. If the actual weights are not
    // provided, random weights are generated.
    public void initWeightsFromHiddenLayerNeuronsToOutputLayerNeurons(List<Double> outputLayerWeights) {
        int weightNum = 0;
        for (int o = 0; o < outputLayer.neurons.size(); o++) {
            for (int h = 0; h < hiddenLayer.neurons.size(); h++) {
                if (outputLayerWeights == null) {
                    outputLayer.neurons.get(o).weights.add((new Random()).nextDouble());
                } else {
                    outputLayer.neurons.get(o).weights.add(outputLayerWeights.get(weightNum));
                }
                weightNum = weightNum + 1;
            }
        }
    }

    // Display a NeuralNetwork object by calling the toString on each layer.
    public String toString() {
        String s = "";
        s = s + "-----\n";
        s = s + "* Inputs: " + numInputs + "\n";
        s = s + "-----\n";

        s = s + "Hidden Layer\n";
        s = s + hiddenLayer.toString();
        s = s + "----";
        s = s + "* Output layer\n";
        s = s + outputLayer.toString();
        s = s + "-----";
        return s;
    }

    // Feed the inputs provided into the network and get outputs.
    // The inputs are provided to the hidden layer. The hidden layer's outputs
    // are provided as inputs the output layer. The outputs of the output layer
    // are returned to the caller as a list of outputs. That number of outputs may be one.
    // The feedForward method is called on each layer.
    public List<Double> feedForward(List<Double> inputs) {

        List<Double> hiddenLayerOutputs = hiddenLayer.feedForward(inputs);
        return outputLayer.feedForward(hiddenLayerOutputs);
    }

    // Training means to feed the data forward - forward propagation. Compare the result with the target(s), and
    // use backpropagation to update the weights. See the blog post to review the math.
    public void train(List<Double> trainingInputs, List<Double> trainingOutputs) {

        // Update state of neural network and ignore the return value
        feedForward(trainingInputs);
        // Perform backpropagation
        List<Double> pdErrorsWRTOutputNeuronTotalNetInput =
                new ArrayList<Double>(Collections.nCopies(outputLayer.neurons.size(), 0.0));
        for (int o = 0; o < outputLayer.neurons.size(); o++) {
            pdErrorsWRTOutputNeuronTotalNetInput.set(o, outputLayer.neurons.get(o).calculatePDErrorWRTTotalNetInput(trainingOutputs.get(o)));
        }
        List<Double> pdErrorsWRTHiddenNeuronTotalNetInput =
                new ArrayList<Double>(Collections.nCopies(hiddenLayer.neurons.size(), 0.0));
        for (int h = 0; h < hiddenLayer.neurons.size(); h++) {
            double dErrorWRTHiddenNeuronOutput = 0;
            for (int o = 0; o < outputLayer.neurons.size(); o++) {
                dErrorWRTHiddenNeuronOutput +=
                        pdErrorsWRTOutputNeuronTotalNetInput.get(o) * outputLayer.neurons.get(o).weights.get(h);
                pdErrorsWRTHiddenNeuronTotalNetInput.set(h, dErrorWRTHiddenNeuronOutput *
                        hiddenLayer.neurons.get(h).calculatePDTotalNetInputWRTInput());
            }
        }
        for (int o = 0; o < outputLayer.neurons.size(); o++) {
            for (int wHo = 0; wHo < outputLayer.neurons.get(o).weights.size(); wHo++) {
                double pdErrorWRTWeight =
                        pdErrorsWRTOutputNeuronTotalNetInput.get(o) *
                                outputLayer.neurons.get(o).calculatePDTotalNetInputWRTWeight(wHo);
                outputLayer.neurons.get(o).weights.set(wHo, outputLayer.neurons.get(o).weights.get(wHo) - LEARNING_RATE * pdErrorWRTWeight);
            }
        }
        for (int h = 0; h < hiddenLayer.neurons.size(); h++) {
            for (int wIh = 0; wIh < hiddenLayer.neurons.get(h).weights.size(); wIh++) {
                double pdErrorWRTWeight =
                        pdErrorsWRTHiddenNeuronTotalNetInput.get(h) *
                                hiddenLayer.neurons.get(h).calculatePDTotalNetInputWRTWeight(wIh);
                hiddenLayer.neurons.get(h).weights.set(wIh, hiddenLayer.neurons.get(h).weights.get(wIh) - LEARNING_RATE * pdErrorWRTWeight);
            }
        }
    }

    // Perform a feed forward for each training row and total the error.
    public double calculateTotalError(ArrayList<Double[][]> trainingSets) {

        double totalError = 0.0;

        for (int t = 0; t < trainingSets.size(); t++) {
            List<Double> trainingInputs = Arrays.asList(trainingSets.get(t)[0]);
            List<Double> trainingOutputs = Arrays.asList(trainingSets.get(t)[1]);
            feedForward(trainingInputs);
            for (int o = 0; o < trainingOutputs.size(); o++) {
                totalError += outputLayer.neurons.get(o).calculate_error(trainingOutputs.get(o));
            }
        }
        return totalError;
    }

    // Read the user input with a Scanner.
    static Scanner scanner = new Scanner(System.in);

    public static void main(String args[]) {

        // Create an initial truth table with all 0's in the range.
        ArrayList<Double[][]> userTrainingSets = new ArrayList<Double[][]>(Arrays.asList(
                new Double[][]{{0.0, 0.0}, {0.0}},
                new Double[][]{{0.0, 1.0}, {0.0}},
                new Double[][]{{1.0, 0.0}, {0.0}},
                new Double[][]{{1.0, 1.0}, {0.0}}
        ));
        //     Create a neural network suitable for working with truth tables.
        //     There will be two inputs, 5 hidden neurons, and 1 output. All weights and biases will be random.
        //     This is the initial neural network on start up.
        NeuralNetwork neuralNetwork = new NeuralNetwork(2, 5, 1, null, null, null, null);

        Random rand = new Random();
        int random_choice;

        // Hold a list of doubles for input for the neural network to train on.
        // In this example, if we want to train the neural network to learn the XOR,
        // the list would have two doubles, say 0 1 or 1 0 or 1 1.
        List<Double> userTrainingInputs;

        // Hold a list of double for the output of training. For example, XOR would produce 1 double as output.
        List<Double> userTrainingOutputs;

        int userSelection = menu();

        while (userSelection != 5) {
            switch (userSelection) {
                case 0: // display the truth table
                    System.out.println("Working with the following truth table");
                    for (int r = 0; r < 4; r++) {
                        System.out.print(userTrainingSets.get(r)[0][0] + "  " + userTrainingSets.get(r)[0][1] + "  " + userTrainingSets.get(r)[1][0] + "  ");
                        System.out.println();
                    }
                    break;
                case 1:  // get the range values of the truth table. These values are from the rightmost column of a
                    // standard truth table.
                    System.out.println("Enter the four results of a 4 by 2 truth table. Each value should be 0 or 1.");
                    Double a = scanner.nextDouble();
                    Double b = scanner.nextDouble();
                    Double c = scanner.nextDouble();
                    Double d = scanner.nextDouble();
                    // Create the new table with the user input as the rightmost column.
                    userTrainingSets = new ArrayList<Double[][]>(Arrays.asList(
                            new Double[][]{{0.0, 0.0}, {a}},
                            new Double[][]{{0.0, 1.0}, {b}},
                            new Double[][]{{1.0, 0.0}, {c}},
                            new Double[][]{{1.0, 1.0}, {d}}
                    ));
                    // Build a new neural network with new random weights.
                    neuralNetwork = new NeuralNetwork(2, 5, 1, null, null, null, null);
                    break;

                case 2: // perform a single trainng step and display total error.
                    random_choice = rand.nextInt(4);
                    // Get the two inputs
                    userTrainingInputs = Arrays.asList(userTrainingSets.get(random_choice)[0]);
                    // Get the one output (in the case of truth tables).
                    userTrainingOutputs = Arrays.asList(userTrainingSets.get(random_choice)[1]);
                    // Show that row to the neural network
                    neuralNetwork.train(userTrainingInputs, userTrainingOutputs);
                    // Show error as we train
                    System.out.println("After this step the error is : " + neuralNetwork.calculateTotalError(userTrainingSets));
                    break;
                case 3: // perform n training steps
                    System.out.println("Enter the number of training sets.");
                    int n = scanner.nextInt();
                    for (int i = 0; i < n; i++) {
                        random_choice = rand.nextInt(4);
                        // Get the two inputs
                        userTrainingInputs = Arrays.asList(userTrainingSets.get(random_choice)[0]);
                        // Get the one output
                        userTrainingOutputs = Arrays.asList(userTrainingSets.get(random_choice)[1]);
                        // Show that row to the neural network
                        neuralNetwork.train(userTrainingInputs, userTrainingOutputs);
                    }
                    // Show error as we train
                    System.out.println("After " + n + " training steps, our error " + neuralNetwork.calculateTotalError(userTrainingSets));
                    break;
                case 4: // test with a pair of inputs.
                    System.out.println("Enter a pair of doubles from a row of the truth table. These are domain values.");
                    double input0 = scanner.nextDouble();
                    double input1 = scanner.nextDouble();

                    List<Double> testUserInputs = new ArrayList<>(Arrays.asList(input0, input1));
                    List<Double> userOutput = neuralNetwork.feedForward(testUserInputs);
                    System.out.println("The range value is approximately " + userOutput.get(0));
                    break;
                case 5:
                    System.exit(0);
                    break;
                default:
                    System.out.println("Error in input. Please choose an integer from the main menu.");
                    break;
            }
            userSelection = menu();
        }
    }

    public static int menu() {
        System.out.println("Using a neural network to learn a truth table.\nMain Menu");
        System.out.println("0. Display the current truth table.");
        System.out.println("1. Provide four inputs for the range of the two input truth table and build a new neural network. To test XOR, enter 0  1  1  0.");
        System.out.println("2. Perform a single training step.");
        System.out.println("3. Perform n training steps. 10000 is a typical value for n.");
        System.out.println("4. Test with a pair of inputs.");
        System.out.println("5. Exit program.");
        int selection = scanner.nextInt();
        return selection;
    }
}
