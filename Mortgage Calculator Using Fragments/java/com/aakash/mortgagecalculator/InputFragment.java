package com.aakash.mortgagecalculator;

import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;


public class InputFragment extends Fragment {

    private EditText homeValue,downPayment,interestRate,propTaxRate;
    private Spinner terms;
    private String totalTaxPaid,totalInterest,monthlyPayment,payOffDate;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final View view = inflater.inflate(R.layout.fragment_input, container, false);
        homeValue =  (EditText)view.findViewById(R.id.homeValue);
        downPayment =  (EditText)view.findViewById(R.id.downPayment);
        interestRate =  (EditText)view.findViewById(R.id.interestRate);
        propTaxRate =  (EditText)view.findViewById(R.id.propTaxRate);
        terms = (Spinner)view.findViewById(R.id.terms);
        Button btnCalculate = (Button)view.findViewById(R.id.btnCalculate);
        Button btnReset = (Button)view.findViewById(R.id.btnReset);

        /**
         * Calculate Button Listener
         */
        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(calculate()){
                    OutputFragment fragment = new OutputFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("totalTaxPaid",totalTaxPaid+" $");
                    bundle.putString("totalInterest",totalInterest+" $");
                    bundle.putString("monthlyPayment", monthlyPayment+" $");
                    bundle.putString("payOffDate", payOffDate);
                    fragment.setArguments(bundle);

                    if(container!=null && container.findViewById(R.id.fragment_container) != null){
                        if(view.findViewById(R.id.fragment_container)==null){
                            CalculateButtonListener listener = (CalculateButtonListener) getActivity();
                            listener.setContent(bundle);
                        }else{
                            getFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).addToBackStack(null).commit();
                        }
                    }
                    else{
                        CalculateButtonListener listener = (CalculateButtonListener) getActivity();
                        listener.setContent(bundle);
                    }
                }
            }
        });

        /**
         * Reset Button Listener
         */
        btnReset.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                reset();
                OutputFragment fragment = new OutputFragment();
                Bundle bundle = new Bundle();
                bundle.putString("totalTaxPaid",null);
                bundle.putString("totalInterest",null);
                bundle.putString("monthlyPayment", null);
                bundle.putString("payOffDate", null);
                fragment.setArguments(bundle);
                if(view.findViewById(R.id.fragment_container) == null){
                    CalculateButtonListener listener = (CalculateButtonListener) getActivity();
                    listener.setContent(bundle);
                }
            }
        });
        return view;
    }

    /**
     * Calculating the mortgage
     */
    private boolean calculate(){

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
            float N = Float.parseFloat(terms.getSelectedItem().toString())*12;

            /**
             * Formula
             * M = (P*I*((1+I)^N))/(((1+I)^N)-1)
             */
            double tempNumerator = Math.pow(I+1,N);
            double tempDenominator = Math.pow(I+1,N)-1;
            double M = (P*I*tempNumerator/tempDenominator);

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
            totalTaxPaid = String.format("%.2f", TPT);
            totalInterest = String.format("%.2f", TI);
            monthlyPayment = String.format("%.2f", M);
            payOffDate = String.valueOf(PD);
            /*paymentHeader.setText("Payment");
            totalTaxPaid.setText("Total Tax Paid : "+String.format("%.2f", TPT));
            totalInterest.setText("Total Interest : "+String.format("%.2f", TI));
            monthlyPayment.setText("Monthly Payment : "+String.format("%.2f", M));
            payOffDate.setText("Pay Off Date : "+String.valueOf(PD));*/

            Log.i("Total Tax Paid = ", String.format("%.2f", TPT));
            Log.i("Total Interest = ",String.format("%.2f", TI));
            Log.i("Monthly Payment = ",String.format("%.2f", M));
            Log.i("Pay off date = ",String.valueOf(PD));
        }

        return allValid;
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

    /**
     * Reset button callback
     */
    public void reset(){

        homeValue.setText(null);
        homeValue.requestFocus();
        downPayment.setText(null);
        interestRate.setText(null);
        propTaxRate.setText(null);
        terms.setSelection(0);
        /*paymentHeader.setText(null);
        totalTaxPaid.setText(null);
        totalInterest.setText(null);
        monthlyPayment.setText(null);
        payOffDate.setText(null);*/
    }
}
