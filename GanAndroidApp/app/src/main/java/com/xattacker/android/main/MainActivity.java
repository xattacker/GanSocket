package com.xattacker.android.main;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.xattacker.gan.GanClient;
import com.xattacker.gan.GanClientListener;
import com.xattacker.util.DateTimeUtility;
import com.xattacker.util.DateTimeUtility.DateTimeFormatType;

import java.util.Date;

public class MainActivity extends Activity implements OnClickListener, GanClientListener
{
	 private EditText _accountEdit, _pwdEdit;
	 private EditText _receiverEdit, _msgEdit;
	 private EditText _receivedMsgEdit;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        try
        {
      	  // 122.254.39.227 // home
      	  // 192.168.226.50 // office
      	  GanClient.constructInstance("192.168.226.50", 5999, this);
        }
        catch (Exception ex)
        {
      	  android.util.Log.i("aaa", ex.toString());
        }
        
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        setContentView(layout);
        
        
        _accountEdit = new EditText(this);
        _accountEdit.setText("test");
        layout.addView(_accountEdit);
        
        _pwdEdit = new EditText(this);
        _pwdEdit.setText("123");
        layout.addView(_pwdEdit);
        
        LinearLayout layout2 = new LinearLayout(this);
        layout2.setOrientation(LinearLayout.HORIZONTAL);
        layout.addView(layout2);
        
        Button button = new Button(this);
        button.setText("login");
        button.setId(1);
        button.setOnClickListener(this);
        layout2.addView(button);
        
        button = new Button(this);
        button.setText("logout");
        button.setId(2);
        button.setOnClickListener(this);
        layout2.addView(button);
        
        button = new Button(this);
        button.setText("get ip");
        button.setId(4);
        button.setOnClickListener(this);
        layout2.addView(button);

        button = new Button(this);
        button.setText("get server time");
        button.setId(5);
        button.setOnClickListener(this);
        layout2.addView(button);
        
        
        _receiverEdit = new EditText(this);
        _receiverEdit.setText("test");
        layout.addView(_receiverEdit);
        
        _msgEdit = new EditText(this);
        _msgEdit.setText("1231234");
        layout.addView(_msgEdit);
        
        button = new Button(this);
        button.setText("send sms");
        button.setId(3);
        button.setOnClickListener(this);
        layout.addView(button);
        
        
        _receivedMsgEdit = new EditText(this);
        _receivedMsgEdit.setGravity(Gravity.TOP);
        _receivedMsgEdit.setTextColor(Color.BLUE);
        _receivedMsgEdit.setEnabled(false);
        _receivedMsgEdit.setLines(3);
        layout.addView(_receivedMsgEdit);
    }
    
    public void onDestroy()
    {
		 super.onDestroy();

		 GanClient.release();
    }

	@Override
	public void onClick(View aView)
	{
		switch (aView.getId())
		{
			case 1:
			{
				final String account = _accountEdit.getText().toString();
				final String pwd = _pwdEdit.getText().toString();

				asyncRun(
				new AsyncRunnable()
				{
					@Override
					public void run() throws Exception
					{
						boolean result = GanClient.instance().accountService().login(account, pwd);
						android.util.Log.i("aaa", "login " + result);
						showToast("login result: " + result);
					}

					@Override
					public void onExceptionHappened(Throwable th)
					{
					}
				}
				);
			}
				break;
				
			case 2:
			{
				asyncRun(
				new AsyncRunnable()
				{
					@Override
					public void run() throws Exception
					{
						boolean result = GanClient.instance().accountService().logout();
						android.util.Log.i("aaa", "logout " + result);
						showToast("logout result: " + result);
					}

					@Override
					public void onExceptionHappened(Throwable th)
					{
					}
				}
				);

				_receivedMsgEdit.setText("");
			}
				break;
				
			case 3:
			{
				final String receiver = _receiverEdit.getText().toString();
				final String msg = _msgEdit.getText().toString();

				asyncRun(
				new AsyncRunnable()
				{
					@Override
					public void run() throws Exception
					{
						boolean result = GanClient.instance().smsService().sendMsg(receiver, msg);
						android.util.Log.i("aaa", "send msg " + result);
						showToast("send msg result: " + result);
					}

					@Override
					public void onExceptionHappened(Throwable th)
					{
					}
				}
				);
			}
				break;
				
			case 4:
			{
				asyncRun(
				new AsyncRunnable()
				{
					@Override
					public void run() throws Exception
					{
						String ip = GanClient.instance().systemService().getIPAddress();
						if (ip != null)
						{
							android.util.Log.i("aaa", "get IP: " + ip);
							showToast("get IP:  " + ip);
						}
						else
						{
							showToast("get IP failed ");
						}
					}

					@Override
					public void onExceptionHappened(Throwable th)
					{
					}
				}
				);
			}
				break;
				
			case 5:
			{
				Date time = GanClient.instance().systemService().getSystemTime();
				if (time != null)
				{
					String time_str = DateTimeUtility.getDateTimeString
					                  (
					                  time, 
					                  DateTimeFormatType.DATETIME_COMPLETE
					                  );
					Toast.makeText(this, time_str, Toast.LENGTH_LONG).show();
				}

				asyncRun(
				new AsyncRunnable()
				{
					@Override
					public void run() throws Exception
					{
						Date time = GanClient.instance().systemService().getSystemTime();
						if (time != null)
						{
							String time_str = DateTimeUtility.getDateTimeString
							(
							time,
							DateTimeFormatType.DATETIME_COMPLETE
							);
							android.util.Log.i("aaa", "get time: " + time_str);
							showToast("get time:  " + time_str);
						}
						else
						{
							showToast("get time failed");
						}
					}

					@Override
					public void onExceptionHappened(Throwable th)
					{
					}
				}
				);
			}
				break;
		}
	}
	
	@Override
	public void onAccountLogined(String aAccount)
	{
		android.util.Log.i("aaa", "onAccountLogined " + aAccount);
	}
	
	@Override
	public void onAccountClosed(String aAccount)
	{
		android.util.Log.i("aaa", "onAccountClosed " + aAccount);
	}
	
	@Override
	public void onSMSReceived(final String aSender, final long aTime, final String aMsg)
	{
		android.util.Log.i("aaa", "onSMSReceived " + aSender + ", " + aTime);
		
		runOnUiThread
		(
		new Runnable()
		{
			public void run()
			{
				if (_receivedMsgEdit != null)
				{
					_receivedMsgEdit.setText("");

					Date date = new Date(aTime);
					_receivedMsgEdit.append
					(
				   DateTimeUtility.getDateTimeString
				   (
				   date, 
				   DateTimeFormatType.DATETIME_COMPLETE
				   )
				   );
					_receivedMsgEdit.append("\n" + aMsg);
				}
			}
		}
		);
	}
	public interface AsyncRunnable
	{
		void run() throws Exception;
		void onExceptionHappened(Throwable th);
	}

	protected void asyncRun(final AsyncRunnable run, final int delay)
	{
		try
		{
			new Thread()
			{
				public void run()
				{
					try
					{
						Looper.prepare();

						if (delay > 0)
						{
							Thread.sleep(delay);
						}

						run.run();
					}
					catch (Throwable th)
					{
						showToast(th.getLocalizedMessage() != null ? th.getLocalizedMessage()  : th.toString());
						run.onExceptionHappened(th);
					}
				}
			}.start();
		}
		catch (Throwable th)
		{
			showToast(th.getLocalizedMessage());
			run.onExceptionHappened(th);
		}
	}

	protected void asyncRun(final AsyncRunnable run)
	{
		asyncRun(run, 0);
	}

	public String getAppVersion()
	{
		String version = null;

		try
		{
			version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		}
		catch (Exception ex)
		{
		}

		return version;
	}

	protected void showToast(final String text)
	{
		if (isMainThread())
		{
			Toast.makeText(this, text, Toast.LENGTH_LONG).show();
		}
		else
		{
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(MainActivity.this, text, Toast.LENGTH_LONG).show();
				}
			});
		}
	}

	protected boolean isMainThread()
	{
		return Looper.getMainLooper().equals(Looper.myLooper());
	}

	protected boolean isOnEmulator()
	{
		String android_id = Settings.Secure.ANDROID_ID;
		String product = Build.PRODUCT;

		return "sdk".equalsIgnoreCase(product) ||
					"google_sdk".equalsIgnoreCase(product) ||
					"sdk_x86".equalsIgnoreCase(product) ||
					"sdk_google_phone_x86".equalsIgnoreCase(product) ||
					"vbox86p".equalsIgnoreCase(product) ||
					android_id == null ||
					android_id.equalsIgnoreCase("9774D56D682E549C");
	}
}
