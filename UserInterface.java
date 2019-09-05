import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/*the following class implements an user interface of a calculator.
 * the calculator mimics the standard calculator included in windows 10.
 * calculation algorithm was made as close as possible to the calculator mentionef above*/
public class UserInterface extends JFrame implements ActionListener {
    private JButton[] buttonsArr = new JButton[12]; /*array of the main keypad which includes the building blocks for the operands*/
    private JButton[] bttnOpArr = new JButton[8]; /*array of arithmetic operations*/
    private JTextField displayJTF; /*calculator's output will be assigned here*/
    private StringBuffer displayStringBuf = new StringBuffer("0"); /*stringbuffer represents the calc's output*/
    private KeypadPanel keypadPanel;    /*keypad panel ref*/
    private OperationPanel opPanel;     /*operation panel ref*/
    private ArgType numType = ArgType.INT; /*current output type*/
    /*input list will always contain the cumulative calculation answer in its first element.
     * the second element will be the current operation, the final element is the operantion's
     * second operand.*/
    private List<String> input = new ArrayList<>();
    private Boolean newNumBool;
    private Boolean gotEqual;
    private Boolean gotOp;

    /*main window init*/
    public UserInterface() {
        setLayout(new BorderLayout());
        setTitle("Calculator");
        JPanel keypad = new JPanel();
        newNumBool = gotEqual = gotOp = false;
        /*main output display cosmetic configuration*/
        add(displayJTF = new JTextField(displayStringBuf.toString()), BorderLayout.CENTER); /*add new textfield*/
        displayJTF.setFont(new Font("Arial", Font.PLAIN, 26));
        displayJTF.setEnabled(false);
        displayJTF.setPreferredSize(new Dimension(200, 50));
        displayJTF.setDisabledTextColor(Color.BLACK);
        displayJTF.setHorizontalAlignment(JTextField.RIGHT);
        displayJTF.setBorder(new LineBorder(Color.BLACK, 1));
        keypad.add(keypadPanel = new KeypadPanel());/*add new keypad*/
        keypad.add(opPanel = new OperationPanel());/*add new operation panel*/
        add(keypad, BorderLayout.SOUTH);
        pack();/*pack window to fit to panels*/
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }

    /*the following class creates a keypad panel containing the required buttons*/
    private class KeypadPanel extends JPanel {
        public KeypadPanel() {
            final int rows = 4;
            final int cols = 3;
            final int fontSize = 20;
            setLayout(new GridLayout(rows, cols));
            final int[] arr = {7, 8, 9, 4, 5, 6, 1, 2, 3, 11, 0, 10};
            for (int i : arr) {
                /*create button per requirements*/
                if (i < 10)/*as 10 digits;*/
                    buttonsArr[i] = new JButton(Integer.toString(i));
                else
                    buttonsArr[i] = new JButton(i == 10 ? "." : "+/-");
                /*configure appearance*/
                buttonsArr[i].setFont(new Font("Arial", Font.PLAIN, fontSize));
                buttonsArr[i].addActionListener(UserInterface.this);
                buttonsArr[i].setContentAreaFilled(false);
                add(buttonsArr[i]);
            }
        }
    }

    /*the following class creates the arithmetic operations panel*/
    private class OperationPanel extends JPanel {
        public OperationPanel() {
            final int rows = 4;
            final int cols = 2;
            final int fontSize = 20;
            setLayout(new GridLayout(rows, cols));
            final String[] opArr = {"CE", "C", "X", "/", "+", "-", "=", "del"};
            /*create and configure each button*/
            for (int i = 0; i < opArr.length; i++) {
                bttnOpArr[i] = new JButton(opArr[i]);
                bttnOpArr[i].addActionListener(UserInterface.this);
                bttnOpArr[i].setFont(new Font("Arial", Font.PLAIN, fontSize));
                bttnOpArr[i].setContentAreaFilled(false);
                add(bttnOpArr[i]);
            }
        }
    }

    /*the following method flips the type flag of the current output*/
    private void switchType() {
        if (numType == ArgType.INT)
            numType = ArgType.FLOAT;
        else
            numType = ArgType.INT;
    }

    /*the following function changes the output from integer to a floating number*/
    private void changeToFloat() {
        if (numType == ArgType.INT) {/*if current output is of type int*/
            displayStringBuf.insert(displayStringBuf.toString().length(), ".");/*base case, insert floating point at the end.*/
            displayJTF.setText(displayStringBuf.toString());
            switchType();
        }
    }

    /*the following function returns true if the current output is positive, otherwise false*/
    private Boolean getPositivity() {
        return Double.parseDouble(displayStringBuf.toString()) >= 0.0;
    }

    /*change the sign of the current output*/
    private void changeSign() {
        String tmp;
        if (!displayStringBuf.toString().equals("0")) {/*if output is other than zero*/
            if (getPositivity())/*if positive*/
                displayStringBuf.insert(0, "-");/*change to negative*/
            else
                displayStringBuf.replace(0, 1, "");/*else change to positive*/
            displayJTF.setText(tmp = displayStringBuf.toString());/*update display*/
            if (!newNumBool)
                input.set(0, tmp);
        }
    }

    /*update display according to the input from user, method argument is object which action performed upon*/
    private void updateDisplay(Object obj) {
        /*handle floating point request*/
        if (obj == buttonsArr[10]) {/*change to float*/
            changeToFloat();
            return;
            /*handle sign change*/
        } else if (obj == buttonsArr[11]) {/*change sign*/
            changeSign();
            return;
            /*handle adding zero*/
        } else if (displayStringBuf.toString().equals("0")) {
            if (obj == buttonsArr[0])/*skip leading zeros*/
                return;
            else
                displayStringBuf.replace(0, 1, ((JButton) obj).getText()); /*overwrite 0*/
        } else
            displayStringBuf.insert(displayStringBuf.toString().length(), ((JButton) obj).getText());/*base case, add digit to the end*/
        displayJTF.setText(displayStringBuf.toString());/*update label*/
    }

    /*preform calculation on the given operands with the given operation*/
    private String calculate(String op) {
        String result = null;
        String operandOne, operandTwo;
        /*leaving edga cases as is (div by zero and zero/zero), as the conversion to double takes care of it.
         * - becomes infinity and NaN respectively*/
        operandOne = input.get(0);
        /*if there is no second operand, duplicate the first one.*/
        if (input.size() < 3) {
            input.add(input.get(0));
        }
        operandTwo = input.get(2);
        /* preform calculation*/
        switch (op) {
            case "X":
                result = Double.toString(Double.parseDouble(operandOne) * Double.parseDouble(operandTwo));
                break;
            case "/":
                result = Double.toString(Double.parseDouble(operandOne) / Double.parseDouble(operandTwo));
                break;
            case "+":
                result = Double.toString(Double.parseDouble(operandOne) + Double.parseDouble(operandTwo));
                break;
            case "-":
                result = Double.toString(Double.parseDouble(operandOne) - Double.parseDouble(operandTwo));
                break;
        }
        /*remove unnecessary floating point*/
        if (result != null && Double.parseDouble(result) - (int) Double.parseDouble(result) == 0.0)
            result = Integer.toString((int) Double.parseDouble(result));
        else {
            numType = ArgType.FLOAT;
        }
        return result;
    }

    /*reset display to 0, set type as int*/
    private void resetDisplay() {
        displayJTF.setText((displayStringBuf = new StringBuffer("0")).toString());
        numType = ArgType.INT;
    }

    /*set display string and textfield to the given string*/
    private void setDisplay(String str) {
        if (str == null)
            return;
        displayStringBuf = new StringBuffer(str);
        displayJTF.setText(displayStringBuf.toString());
    }

    /*preform the calculation according to the input. logic is set to be as close as possible to
     * the calculator exists in win10 on standard mode. the method's input is the given operation entered by the user.
     * the prev operation is then preformed, input list is updated accordingly.*/
    private void preformArithmetics(Object obj) {
        String op = ((JButton) obj).getText();
        String curOp = null;
        int size = input.size();
        /* got new operand, place it in the correct place in the input list.*/
        if (newNumBool) {
            String newNumStr = displayJTF.getText();
            if (size == 3)
                input.set(2, newNumStr);
            else {
                input.add(newNumStr);
                size++;
            }
            newNumBool = gotOp = false;
        }
        /*got new operation request.*/
        if (!op.equals("=")) {
            gotOp = true;
            /*input list is empty, save first operand.*/
            if (size == 0)
                input.add(displayJTF.getText());
                /*at least one operation was already saved, get it. update list*/
            else if (size > 1) {
                curOp = input.get(1);
                input.set(1, op);
            } else /**/
                input.add(op);
            if (gotEqual) { /*skip calculation if previously got equals request*/
                gotEqual = false;
                return;
            }
        } else { /*equals requested*/
            gotEqual = true;
            if (gotOp && size == 3) input.remove(2); /*num+= -> num+num=*/
            if (size >= 2) curOp = input.get(1);
            gotOp = false;
        }
        if (curOp != null)
            input.set(0, calculate(curOp));
        setDisplay(input.get(0));
    }

    /*reset instance variables to the initial state, preform C*/
    private void hardReset() {
        numType = ArgType.INT;
        newNumBool = gotEqual = false;
        input.clear();
        setDisplay("0");
    }

    /*reset display and sec operand, preform CE*/
    private void softReset() {
        setDisplay("0");
        if (input.size() > 0)
            input.set(0, "0");
    }

    /*delete last char enter by user in the number building phase*/
    private void del() {
        if (newNumBool) {
            int compensateNeg = 0;
            if (!getPositivity())/*compensate for extra char for the sign*/
                compensateNeg = 1;
            if (displayStringBuf.length() - compensateNeg - 1 > 0) {
                if (displayStringBuf.charAt(displayStringBuf.length() - 1) == '.')
                    numType = ArgType.INT;
                setDisplay(displayStringBuf.replace(displayStringBuf.length() - 1, displayStringBuf.length(), "").toString());
            } else
                setDisplay("0");
        }
    }

    /*remove floating point if it's the last character*/
    private void removeFloatingPoint() {
        /*if . is last, remove it.*/
        if (displayStringBuf.charAt(displayStringBuf.length() - 1) == '.') {
            displayStringBuf.replace(displayStringBuf.length() - 1, displayStringBuf.length(), "");
            setDisplay(displayStringBuf.toString());/*update display*/
        }
    }

    @Override
    /*action listener*/
    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();/*get pressed obj*/
        if (obj instanceof JButton) {/*check if button*/
            if (((JButton) obj).getParent() == keypadPanel) {/*if button from keypad*/
                if (!newNumBool && obj != buttonsArr[11]) {/*if not +/- and didnt got new number, ie entering new number*/
                    resetDisplay(); /*reset display*/
                    newNumBool = true;
                }
                updateDisplay(obj); /*get new digits/or other and update display*/
            }
            if (((JButton) obj).getParent() == opPanel) {/*if button from operation panel, act accordingly*/
                removeFloatingPoint();/*remove floating point in case where got no input after it*/
                if (((JButton) obj).getText().equals("C"))
                    hardReset();
                else if (((JButton) obj).getText().equals("CE"))
                    softReset();
                else if (((JButton) obj).getText().equals("del"))
                    del();
                else
                    preformArithmetics(obj);
            }
        }
    }
}