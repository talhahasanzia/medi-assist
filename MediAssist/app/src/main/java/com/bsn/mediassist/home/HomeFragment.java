package com.bsn.mediassist.home;


import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bsn.mediassist.R;
import com.bsn.mediassist.data.User;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements UpdateOnUser
{
	
	
	boolean isUserSignedIn = false;
	
	ArrayList<BarEntry> BARENTRY;
	ArrayList<String> BarEntryLabels;
	BarDataSet Bardataset;
	BarData BARDATA;
	
	User user;
	
	
	@BindView(R.id.chart1)
	BarChart mChart;
	
	@BindView(R.id.last_bpm)
	TextView lastBPMText;
	
	@BindView(R.id.last_date)
	TextView lastDateText;
	
	
	Dialog dialog;
	BroadcastReceiver bpmReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive( Context context, Intent intent )
		{
			String bpm = intent.getIntExtra( "bpm", 0 ) + "";
			String dateintent = intent.getStringExtra( "bpm" );
			
			lastDateText.setText( dateintent );
			lastBPMText.setText( bpm );
			
			
		}
	};
	private String[] lastSevenDays;
	
	public HomeFragment()
	{
		// Required empty public constructor
	}
	
	@Override
	public void onAttach( Context context )
	{
		super.onAttach( context );
		
		isUserSignedIn = ( (MainActivity) getActivity() ).isUserSignedIn;
		
	}
	
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState )
	{
		// Inflate the layout for this fragment
		View v = inflater.inflate( R.layout.fragment_home, container, false );
		
		ButterKnife.bind( this, v );
		
		return v;
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		IntentFilter intentFilter = new IntentFilter( "ACTION_BPM" );
		getActivity().registerReceiver( bpmReceiver, intentFilter );
		
		
		if ( user != null )
		{
			BARENTRY = new ArrayList<>();
			
			BarEntryLabels = new ArrayList<String>();
			
			AddValuesToBARENTRY();
			
			AddValuesToBarEntryLabels();
			
			Bardataset = new BarDataSet( BARENTRY, "History" );
			
			BARDATA = new BarData( BarEntryLabels, Bardataset );
			
			Bardataset.setColors( ColorTemplate.PASTEL_COLORS );
			
			mChart.setData( BARDATA );
			
			mChart.animateY( 3000 );
		}
		else
		{
			
			user = ( (MainActivity) getActivity() ).user;
			
			if ( user != null )
			{
				update( user );
			}
			
		}
		
		
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		
		getActivity().unregisterReceiver( bpmReceiver );
		
		if ( dialog != null && dialog.isShowing() )
		{
			dialog.dismiss();
		}
	}
	
	public void AddValuesToBARENTRY()
	{
		
		
		String monitoringData = user.monitoringData;
		
		
		String[] lastSevenDays = getLastSevenDays();
		
		int i = 0;
		
		for ( String day : lastSevenDays )
		{
			
			BARENTRY.add( new BarEntry( getAverages( day, monitoringData ), i ) );
			BarEntryLabels.add( day );
			i++;
			
		}
/*
        BARENTRY.add(new BarEntry(2f, 0));
        BARENTRY.add(new BarEntry(4f, 1));
        BARENTRY.add(new BarEntry(6f, 2));
        BARENTRY.add(new BarEntry(8f, 3));
        BARENTRY.add(new BarEntry(7f, 4));
        BARENTRY.add(new BarEntry(3f, 5));
        BARENTRY.add(new BarEntry(5f, 6));*/
		
	}
	
	public void AddValuesToBarEntryLabels()
	{

     /*   BarEntryLabels.add("Monday");
        BarEntryLabels.add("Tuesday");
        BarEntryLabels.add("Wednesday");
        BarEntryLabels.add("Thursday");
        BarEntryLabels.add("Friday");
        BarEntryLabels.add("Saturday");
        BarEntryLabels.add("Sunday");*/
		
	}
	
	public String[] getLastSevenDays()
	{
		
		String[] days = new String[ 7 ];
		Calendar gregorianCalendar = GregorianCalendar.getInstance();
		
		days[ 6 ] = new SimpleDateFormat( "dd-MM-yyyy" ).format( gregorianCalendar.getTimeInMillis() );
		
		gregorianCalendar.add( Calendar.DAY_OF_MONTH, -1 );
		
		days[ 5 ] = new SimpleDateFormat( "dd-MM-yyyy" ).format( gregorianCalendar.getTimeInMillis() );
		
		gregorianCalendar.add( Calendar.DAY_OF_MONTH, -1 );
		
		days[ 4 ] = new SimpleDateFormat( "dd-MM-yyyy" ).format( gregorianCalendar.getTimeInMillis() );
		
		gregorianCalendar.add( Calendar.DAY_OF_MONTH, -1 );
		
		days[ 3 ] = new SimpleDateFormat( "dd-MM-yyyy" ).format( gregorianCalendar.getTimeInMillis() );
		
		gregorianCalendar.add( Calendar.DAY_OF_MONTH, -1 );
		
		days[ 2 ] = new SimpleDateFormat( "dd-MM-yyyy" ).format( gregorianCalendar.getTimeInMillis() );
		
		gregorianCalendar.add( Calendar.DAY_OF_MONTH, -1 );
		
		days[ 1 ] = new SimpleDateFormat( "dd-MM-yyyy" ).format( gregorianCalendar.getTimeInMillis() );
		
		gregorianCalendar.add( Calendar.DAY_OF_MONTH, -1 );
		
		days[ 0 ] = new SimpleDateFormat( "dd-MM-yyyy" ).format( gregorianCalendar.getTimeInMillis() );
		
		
		return days;
	}
	
	float getAverages( String day, String data )
	{
		
		float average = 0;
		
		ArrayList<String> bpm = new ArrayList<>();
		
		String[] dataArray = data.split( "#" );
		
		for ( String dataEntry : dataArray )
		{
			
			if ( dataEntry.contains( day ) )
			{
				
				bpm.add( dataEntry.split( ":" )[ 1 ] );
			}
			
		}
		float sum = 0f;
		
		for ( String bpmEntry : bpm )
		{
			
			sum += Float.parseFloat( bpmEntry );
			
		}
		
		if ( bpm.size() > 0 )
		{
			average = sum / (float) bpm.size();
		}
		
		
		return average;
		
	}
	
	@Override
	public void update( User user )
	{
		if ( user != null )
		{
			
			this.user = user;
			String finalDate = "";
			String finalBPM = "";
			
			String temp = user.monitoringData;
			
			String[] tempArray = temp.split( "#" );
			
			
			try
			{
				finalDate = tempArray[ tempArray.length - 1 ].split( ":" )[ 0 ];
				finalBPM = tempArray[ tempArray.length - 1 ].split( ":" )[ 1 ];
			}
			catch ( IndexOutOfBoundsException IOB )
			{
				
				
			}
			
			lastBPMText.setText( finalBPM + "bpm" );
			lastDateText.setText( finalDate );
			
			BARENTRY = new ArrayList<>();
			
			BarEntryLabels = new ArrayList<String>();
			
			AddValuesToBARENTRY();
			
			AddValuesToBarEntryLabels();
			
			Bardataset = new BarDataSet( BARENTRY, "Bpm History" );
			
			BARDATA = new BarData( BarEntryLabels, Bardataset );
			
			Bardataset.setColors( ColorTemplate.PASTEL_COLORS );
			
			mChart.setData( BARDATA );
			
			mChart.animateY( 3000 );
		}
	}
}
