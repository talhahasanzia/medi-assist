package com.bsn.mediassist.ecg;


import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bsn.mediassist.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class BluetoothEnableFragment extends Fragment
{
	
	private final BroadcastReceiver mReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive( Context context, Intent intent )
		{
			final String action = intent.getAction();
			
			if ( action.equals( BluetoothAdapter.ACTION_STATE_CHANGED ) )
			{
				final int state = intent.getIntExtra( BluetoothAdapter.EXTRA_STATE,
						BluetoothAdapter.ERROR );
				switch ( state )
				{
					case BluetoothAdapter.STATE_OFF:
						Toast.makeText( getActivity(), "Please enable bluetooth.", Toast.LENGTH_SHORT ).show();
						break;
					case BluetoothAdapter.STATE_TURNING_OFF:
						
						break;
					case BluetoothAdapter.STATE_ON:
						
						
						Slide slideTransition = new Slide( Gravity.RIGHT );
						slideTransition.setDuration( 1000 );
						ConnectDeviceFragment connectDeviceFragment = new ConnectDeviceFragment();
						connectDeviceFragment.setEnterTransition( slideTransition );
						( (EcgActivity) getActivity() ).setCurrentScreen( connectDeviceFragment );
						
						( (EcgActivity) getActivity() ).currentScreen = 1;
						
						break;
					case BluetoothAdapter.STATE_TURNING_ON:
						
						break;
				}
			}
		}
	};
	BluetoothAdapter bluetoothAdapter;
	int REQUEST_ENABLE_BT = 234;
	
	
	public BluetoothEnableFragment()
	{
		// Required empty public constructor
	}
	
	@OnClick(R.id.bluetooth_button)
	public void onClick( View v )
	{
		
		if ( bluetoothAdapter != null && !bluetoothAdapter.isEnabled() )
		{
			/*Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);*/
			
			Intent discoverableIntent =
					new Intent( BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE );
			discoverableIntent.putExtra( BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0 );
			startActivity( discoverableIntent );
          /*  Intent intentOpenBluetoothSettings = new Intent();
            intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
            startActivity(intentOpenBluetoothSettings);*/
		}
		
	}
	
	@Override
	public void onActivityResult( int requestCode, int resultCode, Intent data )
	{
		super.onActivityResult( requestCode, resultCode, data );
		
		if ( requestCode == REQUEST_ENABLE_BT )
		{
			
			if ( resultCode == RESULT_OK )
			{
				
				Slide slideTransition = new Slide( Gravity.RIGHT );
				slideTransition.setDuration( 1000 );
				ConnectDeviceFragment connectDeviceFragment = new ConnectDeviceFragment();
				connectDeviceFragment.setEnterTransition( slideTransition );
				( (EcgActivity) getActivity() ).setCurrentScreen( connectDeviceFragment );
				
				( (EcgActivity) getActivity() ).currentScreen = 1;
				
				
			}
			else
			{
				
				Toast.makeText( getActivity(), "Please enable bluetooth.", Toast.LENGTH_SHORT ).show();
				
			}
			
		}
	}
	
	@Override
	public void onAttach( Context context )
	{
		super.onAttach( context );
		bluetoothAdapter = ( (EcgActivity) getActivity() ).mBluetoothAdapter;
		
	}
	
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState )
	{
		// Inflate the layout for this fragment
		View v = inflater.inflate( R.layout.fragment_bluetiith_enable, container, false );
		
		ButterKnife.bind( this, v );
		
		
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		return v;
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		if ( bluetoothAdapter.isEnabled() )
		{
			
			Slide slideTransition = new Slide( Gravity.RIGHT );
			slideTransition.setDuration( 1000 );
			ConnectDeviceFragment connectDeviceFragment = new ConnectDeviceFragment();
			connectDeviceFragment.setEnterTransition( slideTransition );
			( (EcgActivity) getActivity() ).setCurrentScreen( connectDeviceFragment );
			
			( (EcgActivity) getActivity() ).currentScreen = 1;
			
		}
		else
		{
			
			
			IntentFilter filter = new IntentFilter( BluetoothAdapter.ACTION_STATE_CHANGED );
			getActivity().registerReceiver( mReceiver, filter );
		}
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		try
		{
			getActivity().unregisterReceiver( mReceiver );
		}
		catch ( IllegalArgumentException npe )
		{
			
		}
	}
}
