package com.example.javacalculator;

import android.os.Bundle;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class MainActivity extends android.app.Activity {

    private TextView tvExpression;
    private TextView tvDisplay;

    private BigDecimal firstOperand = null;
    private String pendingOperator = null;
    private boolean startNewNumber = true;
    private boolean errorState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvExpression = findViewById(R.id.tvExpression);
        tvDisplay = findViewById(R.id.tvDisplay);

        setDigitListener(R.id.btn0, "0");
        setDigitListener(R.id.btn1, "1");
        setDigitListener(R.id.btn2, "2");
        setDigitListener(R.id.btn3, "3");
        setDigitListener(R.id.btn4, "4");
        setDigitListener(R.id.btn5, "5");
        setDigitListener(R.id.btn6, "6");
        setDigitListener(R.id.btn7, "7");
        setDigitListener(R.id.btn8, "8");
        setDigitListener(R.id.btn9, "9");

        bindClick(R.id.btnDot, v -> inputDecimal());

        setOperatorListener(R.id.btnAdd, "+");
        setOperatorListener(R.id.btnSubtract, "−");
        setOperatorListener(R.id.btnMultiply, "×");
        setOperatorListener(R.id.btnDivide, "÷");

        bindClick(R.id.btnEquals, v -> calculateResult());
        bindClick(R.id.btnClear, v -> clearAll());
        bindClick(R.id.btnBackspace, v -> backspace());
        bindClick(R.id.btnSign, v -> toggleSign());
        bindClick(R.id.btnPercent, v -> percent());
    }

    private void bindClick(int viewId, View.OnClickListener listener) {
        View view = findViewById(viewId);
        view.setOnClickListener(v -> {
            animateButtonPress(v);
            listener.onClick(v);
        });
    }

    private void setDigitListener(int buttonId, String digit) {
        bindClick(buttonId, v -> inputDigit(digit));
    }

    private void setOperatorListener(int buttonId, String operator) {
        bindClick(buttonId, v -> chooseOperator(operator));
    }

    private void animateButtonPress(View view) {
        view.animate()
                .scaleX(0.92f)
                .scaleY(0.92f)
                .setDuration(60)
                .withEndAction(() -> view.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(110)
                        .start())
                .start();
    }

    private void inputDigit(String digit) {
        if (errorState) {
            clearAll();
        }

        String current = tvDisplay.getText().toString();

        if (startNewNumber || current.equals("0")) {
            tvDisplay.setText(digit);
            startNewNumber = false;
            return;
        }

        if (countNumericCharacters(current) < 16) {
            tvDisplay.setText(current + digit);
        }
    }

    private void inputDecimal() {
        if (errorState) {
            clearAll();
        }

        String current = tvDisplay.getText().toString();

        if (startNewNumber) {
            tvDisplay.setText("0.");
            startNewNumber = false;
        } else if (!current.contains(".")) {
            tvDisplay.setText(current + ".");
        }
    }

    private void chooseOperator(String operator) {
        if (errorState) return;

        BigDecimal current = readDisplay();

        if (firstOperand == null) {
            firstOperand = current;
        } else if (!startNewNumber && pendingOperator != null) {
            BigDecimal result = performOperation(firstOperand, current, pendingOperator);
            if (result == null) return;
            firstOperand = result;
            tvDisplay.setText(format(result));
        }

        pendingOperator = operator;
        tvExpression.setText(format(firstOperand) + " " + operator);
        animateExpressionHint();
        startNewNumber = true;
    }

    private void animateExpressionHint() {
        tvExpression.setAlpha(0f);
        tvExpression.setTranslationY(16f);
        tvExpression.setScaleX(1.08f);
        tvExpression.setScaleY(1.08f);

        tvExpression.animate()
                .alpha(1f)
                .translationY(0f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(180)
                .start();
    }

    private void calculateResult() {
        if (errorState || firstOperand == null || pendingOperator == null || startNewNumber) {
            return;
        }

        BigDecimal secondOperand = readDisplay();
        String expression = format(firstOperand) + " " + pendingOperator + " " + format(secondOperand);
        BigDecimal result = performOperation(firstOperand, secondOperand, pendingOperator);
        if (result == null) return;

        playEqualsAnimation(expression, format(result));

        firstOperand = null;
        pendingOperator = null;
        startNewNumber = true;
    }

    private void playEqualsAnimation(String expression, String resultText) {
        tvExpression.setText(expression);

        tvExpression.setPivotX(tvExpression.getWidth());
        tvExpression.setPivotY(tvExpression.getHeight());

        tvExpression.setAlpha(0f);
        tvExpression.setTranslationY(48f);
        tvExpression.setScaleX(1.22f);
        tvExpression.setScaleY(1.22f);

        tvDisplay.animate().cancel();
        tvExpression.animate().cancel();

        tvDisplay.setText(resultText);
        tvDisplay.setAlpha(0f);
        tvDisplay.setTranslationY(72f);

        tvExpression.animate()
                .alpha(1f)
                .translationY(0f)
                .scaleX(1f)
                .scaleY(1f)
                .setInterpolator(new OvershootInterpolator(0.7f))
                .setDuration(260)
                .start();

        tvDisplay.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(300)
                .setStartDelay(90)
                .setInterpolator(new OvershootInterpolator(0.55f))
                .start();
    }

    private BigDecimal performOperation(BigDecimal left, BigDecimal right, String operator) {
        try {
            switch (operator) {
                case "+":
                    return left.add(right);
                case "−":
                    return left.subtract(right);
                case "×":
                    return left.multiply(right);
                case "÷":
                    if (right.compareTo(BigDecimal.ZERO) == 0) {
                        showError("Không thể chia cho 0");
                        return null;
                    }
                    return left.divide(right, 12, RoundingMode.HALF_UP);
                default:
                    return right;
            }
        } catch (ArithmeticException ex) {
            showError("Lỗi tính toán");
            return null;
        }
    }

    private void toggleSign() {
        if (errorState) {
            clearAll();
            return;
        }

        BigDecimal current = readDisplay();
        if (current.compareTo(BigDecimal.ZERO) != 0) {
            tvDisplay.setText(format(current.negate()));
            startNewNumber = false;
        }
    }

    private void percent() {
        if (errorState) {
            clearAll();
            return;
        }

        BigDecimal current = readDisplay();
        BigDecimal result = current.divide(new BigDecimal("100"), 12, RoundingMode.HALF_UP);
        tvDisplay.setText(format(result));
        startNewNumber = false;
    }

    private void backspace() {
        if (errorState) {
            clearAll();
            return;
        }

        if (startNewNumber) return;

        String current = tvDisplay.getText().toString();

        if (current.length() <= 1 || (current.startsWith("-") && current.length() == 2)) {
            tvDisplay.setText("0");
            startNewNumber = true;
        } else {
            tvDisplay.setText(current.substring(0, current.length() - 1));
        }
    }

    private void clearAll() {
        tvDisplay.setText("0");
        tvExpression.setText("");
        firstOperand = null;
        pendingOperator = null;
        startNewNumber = true;
        errorState = false;
        tvDisplay.setAlpha(1f);
        tvDisplay.setTranslationY(0f);
        tvExpression.setAlpha(1f);
        tvExpression.setTranslationY(0f);
        tvExpression.setScaleX(1f);
        tvExpression.setScaleY(1f);
    }

    private void showError(String message) {
        tvExpression.setText(message);
        tvDisplay.setText("Error");
        firstOperand = null;
        pendingOperator = null;
        startNewNumber = true;
        errorState = true;

        tvDisplay.setScaleX(0.9f);
        tvDisplay.setScaleY(0.9f);
        tvDisplay.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(180)
                .start();
    }

    private BigDecimal readDisplay() {
        String value = tvDisplay.getText().toString();

        if (value.equals("Error") || value.isEmpty() || value.equals("-")) {
            return BigDecimal.ZERO;
        }

        return new BigDecimal(value);
    }

    private String format(BigDecimal value) {
        BigDecimal normalized = value.stripTrailingZeros();

        if (normalized.scale() < 0) {
            normalized = normalized.setScale(0);
        }

        String text = normalized.toPlainString();
        if (text.length() > 18) {
            return value.round(new MathContext(12)).stripTrailingZeros().toEngineeringString();
        }
        return text;
    }

    private int countNumericCharacters(String value) {
        int count = 0;
        for (char c : value.toCharArray()) {
            if (Character.isDigit(c)) count++;
        }
        return count;
    }
}
