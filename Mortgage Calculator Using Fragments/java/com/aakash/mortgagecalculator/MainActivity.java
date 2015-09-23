package com.aakash.mortgagecalculator;

import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements CalculateButtonListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            InputFragment firstFragment = new InputFragment();
            firstFragment.setArguments(getIntent().getExtras());

            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_container,firstFragment).commit();
        }
    }

    @Override
    public void setContent(Bundle args){
        OutputFragment fragment = (OutputFragment) getFragmentManager().findFragmentById(R.id.output_fragment);

        if(fragment != null){
            fragment.setContent(args);
        }else{
            OutputFragment newFragment1 = new OutputFragment();
            Bundle bundle = new Bundle();
            bundle.putString("totalTaxPaid",args.getString("totalTaxPaid"));
            bundle.putString("totalInterest",args.getString("totalInterest"));
            bundle.putString("monthlyPayment", args.getString("monthlyPayment"));
            bundle.putString("payOffDate", args.getString("payOffDate"));
            newFragment1.setContent(bundle);

            getFragmentManager().beginTransaction().replace(R.id.fragment_container,newFragment1).addToBackStack(null).commit();


        }
    }
}