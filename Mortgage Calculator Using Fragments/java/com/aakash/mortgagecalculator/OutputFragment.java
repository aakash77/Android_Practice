package com.aakash.mortgagecalculator;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class OutputFragment extends Fragment {

    private TextView totalTaxPaid,totalInterest,monthlyPayment,payOffDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_output, container, false);
        totalTaxPaid = (TextView)view.findViewById(R.id.totalTaxPaid);
        totalInterest = (TextView)view.findViewById(R.id.totalInterestPaid);
        monthlyPayment = (TextView)view.findViewById(R.id.monthlyPayment);
        payOffDate = (TextView)view.findViewById(R.id.payOffDate);
        Bundle args = getArguments();
        if(args!=null){
            setContent(args);
        }

        // Inflate the layout for this fragment
        return view;
    }

    public void setContent(Bundle args){
        totalTaxPaid.setText(args.getString("totalTaxPaid"));
        totalInterest.setText(args.getString("totalInterest"));
        monthlyPayment.setText(args.getString("monthlyPayment"));
        payOffDate.setText(args.getString("payOffDate"));
    }
}
