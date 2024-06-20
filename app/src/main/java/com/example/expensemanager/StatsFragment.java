package com.example.expensemanager;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.expensemanager.Model.Data;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import java.util.ArrayList;

import java.util.Map;
import java.util.TreeMap;

import com.github.mikephil.charting.charts.PieChart;


public class StatsFragment extends Fragment {

    //chart
    private PieChart chart;

    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;
    private DatabaseReference mExpenseDatabase;
    private final String[] type={"Income", "Expense"};
    private final int[] values={0,0};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.fragment_stats, container, false);
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);
        mIncomeDatabase.keepSynced(true);
        mExpenseDatabase.keepSynced(true);

        //Calculate total income
        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                values[0] = 0;
                for (DataSnapshot mysnap : snapshot.getChildren()) {
                    Data data = mysnap.getValue(Data.class);
                    values[0] += data.getAmount();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        //Calculate total expense
        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                values[1] = 0;
                for (DataSnapshot mysnap : snapshot.getChildren()) {
                    Data data = mysnap.getValue(Data.class);
                    values[1] += data.getAmount();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        // My pie chart

        chart = myView.findViewById(R.id.piechart);
        chart.getDescription().setEnabled(false);

        chart.setCenterText("Income vs Expense");
        chart.setCenterTextSize(13);
        chart.animateXY(1700, 1700);

        //for legend
        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setTextSize(18);
        l.setTextColor(Color.CYAN);

        chart.setData(generatePieData());
        chart.invalidate();

        return myView;
    }


    protected PieData generatePieData() {

        ArrayList<PieEntry> entries1 = new ArrayList<PieEntry>();

        for(int i = 0; i < values.length; i++){
            entries1.add(new PieEntry(values[i], type[i]));
        }

        int[] colorClassArray=new int[]{0xFF669900, 0xFFCC0000};
        PieDataSet ds1 = new PieDataSet(entries1, "");
        ds1.setColors(colorClassArray);
        ds1.setSliceSpace(2f);
        ds1.setValueTextColor(Color.BLACK);
        ds1.setValueTextSize(14f);

        PieData d = new PieData(ds1);
        return d;
    }
}
