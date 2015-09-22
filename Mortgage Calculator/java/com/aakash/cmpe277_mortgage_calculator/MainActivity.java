package com.aakash.cmpe277_mortgage_calculator;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText homeValue,downPayment,interestRate,propTaxRate;
    private Spinner terms;
    private TextView paymentHeader,totalTaxPaid,totalInterest,monthlyPayment,payOffDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        homeValue =  (EditText)findViewById(R.id.homeValue);
        downPayment =  (EditText)findViewById(R.id.downPayment);
        interestRate =  (EditText)findViewById(R.id.interestRate);
        propTaxRate =  (EditText)findViewById(R.id.propTaxRate);
        terms = (Spinner)findViewById(R.id.terms);
        paymentHeader = (TextView)findViewById(R.id.paymentHeader);
        totalTaxPaid = (TextView)findViewById(R.id.totalTaxPaid);
        totalInterest = (TextView)findViewById(R.id.totalInterestPaid);
        monthlyPayment = (TextView)findViewById(R.id.monthlyPayment);
        payOffDate = (TextView)findViewById(R.id.payOffDate);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.resetAction) {
            reset();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Calculate Button Event
     * @param view
     */
    public void calculateMortgage(View view){

        boolean allValid = true;
        if(homeValue.length()==0 || isEditTextValid(homeValue)==-1){
            homeValue.setError("Invalid Number");
            allValid=false;
        }
        if(downPayment.length()==0 || isEditTextValid(downPayment)==-1){
            downPayment.setError("Invalid Number");
            allValid=false;
        }
        else if(Float.parseFloat(downPayment.getText().toString())>Float.parseFloat(homeValue.getText().toString())){
            downPayment.setError("Cannot be greater than Home Value");
            allValid=false;
        }
        if(interestRate.length()==0 || isEditTextValid(interestRate)==-1){
            interestRate.setError("Invalid Number");
            allValid=false;
        }
        if(propTaxRate.length()==0 || isEditTextValid(propTaxRate)==-1){
            propTaxRate.setError("Invalid Number");
            allValid=false;
        }
        if(isSpinnerSelected(terms)==-1){
            TextView textView = (TextView)terms.getSelectedView();
            allValid=false;
            textView.setError("Must be a valid year");
        }
        if(allValid){
            /**
             * P = Principal
             * M = Monthly Payment
             * I = Interest Rate
             * N = Number of Payments
             * H = Home Value
             * DP = Down Payment
             */
            float H = Float.parseFloat(homeValue.getText().toString());
            float DP = Float.parseFloat(downPayment.getText().toString());
            float P = H-DP;
            float I = Float.parseFloat(interestRate.getText().toString())/1200;
            float N = Float.parseFloat(terms.getSelectedItem().toString());

            /**
             * Formula
             * M = (P*I*((1+I)^N))/(((1+I)^N)-1)
             */
            double tempNumerator = I*Math.pow(I+1,N);
            double tempDenominator = Math.pow(I+1,N)-1;
            double M = (P*I*tempNumerator/tempDenominator);
            String.format("%.2f",M);
            /**
             * TI = Total Interest Paid
             * Formula
             * TI = (M*N) - P
             */
            double TI = (M*N) - P;

            /**
             * TPT = Total Property Tax Paid
             * Formula = Property Tax*N
             * Property Tax = Home Value * Property Tax Rate / 1200
             */
            double TPT = (H*Float.parseFloat(propTaxRate.getText().toString())/1200)*N;
            String.format("%.2f",TPT);


            /**
             * PD = Pay off Date
             * Formula
             * Current Date + Terms
             */
            Calendar calendar = Calendar.getInstance();
            String month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US);
            int year = calendar.get(Calendar.YEAR) + Integer.parseInt(terms.getSelectedItem().toString());
            String PD = month+" "+year;

            /**
             * Setting Output to corresponding Text Views
             */
            paymentHeader.setText("Payment");
            totalTaxPaid.setText("Total Tax Paid : "+String.valueOf(TPT));
            totalInterest.setText("Total Interest : "+String.valueOf(TI));
            monthlyPayment.setText("Monthly Payment : "+String.valueOf(M));
            payOffDate.setText("Pay Off Date : "+String.valueOf(PD));

            Log.i("Total Tax Paid = ", String.valueOf(TPT));
            Log.i("Total Interest = ",String.valueOf(TI));
            Log.i("Monthly Payment = ",String.valueOf(M));
            Log.i("Pay off date = ",String.valueOf(PD));
        }
    }

    /**
     * Input validation for Editext
     * @param editText
     * @return float value if valid
     */
    private float isEditTextValid(EditText editText){
        try{
            float num = Float.parseFloat(editText.getText().toString());
            if(num<0)
                throw new NumberFormatException("Cannot be negative");
            else
                return num;
        }catch (NumberFormatException e){
            return -1;
        }
    }

    /**
     * Input validation for Spinner
     * @param spinner
     * @return selected float value if valid
     */
    private float isSpinnerSelected(Spinner spinner){
        try{
            float terms = Float.parseFloat(spinner.getSelectedItem().toString());
            return terms;
        }catch (NumberFormatException e){
            return -1;
        }
    }

    public void reset(){

        homeValue.setText(null);
        homeValue.requestFocus();
        downPayment.setText(null);
        interestRate.setText(null);
        propTaxRate.setText(null);
        terms.setSelection(0);
        paymentHeader.setText(null);
        totalTaxPaid.setText(null);
        totalInterest.setText(null);
        monthlyPayment.setText(null);
        payOffDate.setText(null);
    }
}